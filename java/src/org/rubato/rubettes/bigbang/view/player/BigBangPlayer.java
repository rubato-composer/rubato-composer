package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice.Info;

import org.rubato.rubettes.bigbang.view.io.BigBangMidiReceiver;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;

public class BigBangPlayer {
	
	public static final int MIN_BPM = 1;
	public static final int MAX_BPM = 5000;
	public static final int INITIAL_BPM = 100;
	
	public static final int BASE_A4 = 440; // A4 tuning in Hz
	public static final int SAMPLE_RATE = 48000;
	public static final double DEFAULT_ADVANCE = 0.05; //seconds
	public final int MAX_NUMBER_OF_THREADS = 200;
	
	public static final String[] WAVEFORMS = {"Sine", "Square", "Sawtooth", "Triangle"};
	private static Map<String,MidiDevice> midiOutDevices;
	
	private Synthesizer synth;
	private JSynScore score;
	private Map<Integer,JSynPerformance> currentPerformances, currentMonitorPitches;
	private String waveform;
	private int tempo; //in bpm
	private boolean synthActive;
	private String selectedMidiOutDeviceName;
	private boolean isLooping;
	private double loopOnset;
	private double loopDuration;
	private List<Integer> keysOfCurrentPerformancesInOrder;
	
	private boolean inLiveMidiMode;
	
	//needs separate variable since player only starts after a brief delay
	private boolean isPlaying;
	
	public BigBangPlayer() {
		this.synth = JSyn.createSynthesizer();
		this.setWaveform(BigBangPlayer.WAVEFORMS[0]);
		this.isLooping = false;
		this.inLiveMidiMode = false;
		this.currentPerformances = new TreeMap<Integer,JSynPerformance>();
		this.currentMonitorPitches = new TreeMap<Integer,JSynPerformance>();
		this.keysOfCurrentPerformancesInOrder = new ArrayList<Integer>();
		this.setTempo(this.tempo);
		this.setWaveform(BigBangPlayer.WAVEFORMS[0]);
		this.isPlaying = false;
		if (midiOutDevices.size() > 0) {
			this.setMidiOutDevice(midiOutDevices.keySet().iterator().next());
		}
	}
	
	public void setMidiOutDevice(String outDeviceName) {
		if (this.selectedMidiOutDeviceName != null) {
			MidiDevice outDevice = midiOutDevices.get(this.selectedMidiOutDeviceName);
			if (outDevice != null) {
				midiOutDevices.get(this.selectedMidiOutDeviceName).close();
			}
		}
		this.selectedMidiOutDeviceName = outDeviceName;
		try {
			MidiDevice outDevice = midiOutDevices.get(this.selectedMidiOutDeviceName);
			if (outDevice != null) {
				outDevice.open();
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void startSynth() {
		if (!this.synth.isRunning()) {
			//this.synth.getAudioDeviceManager().setSuggestedOutputLatency(.09);
			this.synth.start(BigBangPlayer.SAMPLE_RATE);
			//this.synth.start(JSynPlayer.SAMPLE_RATE, AudioDeviceManager.USE_DEFAULT_DEVICE, 2, AudioDeviceManager.USE_DEFAULT_DEVICE, 2);
		}
	}
	
	public void addToSynth(UnitGenerator generator) {
		this.synth.add(generator);
	}
	
	public void removeFromSynthAndStop(UnitGenerator generator) {
		if (generator != null) {
			try {
				generator.stop();
				this.synth.remove(generator);
			} catch (NullPointerException e) {
				//System.out.println(generator);
			}
		}
	}
	
	public void setScore(JSynScore score) {
		this.score = score;
		System.out.println(score);
		//standard loop is entire score
		if (score.getObjects().size() > 0) {
			this.loopOnset = score.getObjects().iterator().next().getOnset();
			this.loopDuration = this.getLastOffset()-this.loopOnset;
		}
		this.updatePerformances();
	}
	
	/**
	 * reschedules the score this is playing right now (good in case of object state changes)
	 */
	public void updatePerformances() {
		if (this.isPlaying) {
			for (JSynPerformance performance : new HashSet<JSynPerformance>(this.currentPerformances.values())) {
				performance.updateStartOrChangeTimes();
				performance.replaceScore(this.score);
			}
		}
	}
	
	public void setSynthActive(boolean synthActive) {
		this.synthActive = synthActive;
	}
	
	public void setIsLooping(boolean isLooping) {
		this.isLooping = isLooping;
	}
	
	public boolean isLooping() {
		return this.isLooping;
	}
	
	public void setTempo(int bpm) {
		this.tempo = bpm;
		for (JSynPerformance performance : this.currentPerformances.values()) {
			performance.updateStartOrChangeTimes();
			performance.interrupt();
		}
		//this.bbPlayer.interrupt();
	}
	
	public void setPlaybackPosition(double playbackPosition) {
		//if (!this.isLooping) {
			for (JSynPerformance performance : this.currentPerformances.values()) {
				performance.setPlaybackPosition(playbackPosition);
			}
		//
	}
	
	public double getCurrentSynthTime() {
		//System.out.println("STH "+this.synth.getCurrentTime());
		return this.synth.getCurrentTime();
	}
	
	/*
	 * returns the current symbolic time of the latest performance
	 */
	public double getCurrentSymbolicTime() {
		if (this.currentPerformances.size() > 0) {
			int latestPerformance = this.keysOfCurrentPerformancesInOrder.get(this.keysOfCurrentPerformancesInOrder.size()-1);
			return this.currentPerformances.get(latestPerformance).getCurrentSymbolicTime();
		}
		return 0;
	}
	
	public double getLoopOnset() {
		return this.loopOnset;
	}
	
	public double getLoopDuration() {
		return this.loopDuration;
	}
	
	public Synthesizer getSynth() {
		return this.synth;
	}
	
	public double convertToSynthDuration(double symbolicDuration) {
		return symbolicDuration*60/this.tempo;
	}
	
	public double convertToSymbolicDuration(double synthDuration) {
		return synthDuration/60*this.tempo;
	}
	
	public synchronized void togglePlayMode() {
		System.out.println("\n\nTOG "+this.isPlaying);
		if (!this.isPlaying) {
			this.startPlaying();
		} else {
			this.stopPlaying();
		}
	}
	
	private void startPlaying() {
		if (this.score != null && !this.isPlaying) {
			if (!this.inLiveMidiMode) {
				this.playScoreVersion(60, 127);
			}
			this.isPlaying = true;
		}
		System.out.println("PLAY " + this.currentPerformances + " " + this.inLiveMidiMode + " " + this.isLooping + " " + this.score);
	}
	
	public void pressMidiKey(int channel, int pitch, int velocity, boolean recording) {
		if (this.isPlaying) {
			if (recording) {
				this.playMonitorPitch(channel, pitch, velocity);
			} else {
				if (!this.inLiveMidiMode) {
					this.stopAllScoreVersions();
				}
				this.inLiveMidiMode = true;
				this.playScoreVersion(pitch, velocity);
			}
		}
	}
	
	private void playScoreVersion(int pitch, int velocity) {
		MidiDevice outputDevice = midiOutDevices.get(this.selectedMidiOutDeviceName);
		JSynPerformance performance = new JSynPerformance(this, outputDevice, this.score, pitch, velocity);
		//System.out.println("PSV " + this.isLooping);
		if (this.isLooping) {
			performance.setSymbolicStartOrChangeTime(this.loopOnset);
		}
		this.currentPerformances.put(pitch, performance);
		this.keysOfCurrentPerformancesInOrder.add(pitch);
		performance.startPlaying();
	}
	
	private void playMonitorPitch(int channel, int pitch, int velocity) {
		JSynScore recordingMonitorScore = new JSynScore(channel, pitch, velocity);
		JSynPerformance performance = new JSynPerformance(this, recordingMonitorScore);
		this.currentMonitorPitches.put(this.getChannelPitchKey(channel, pitch), performance);
		performance.startPlaying();
	}
	
	public void releaseMidiKey(int channel, int pitch, boolean recording) {
		//System.out.println("PERF " +pitch+ " " + this.currentPerformances);
		//System.out.println(pitch);
		if (this.isPlaying) {
			if (recording) {
				JSynPerformance performance = this.currentMonitorPitches.remove(this.getChannelPitchKey(channel, pitch));
				performance.stopPlaying(false);
			} else {
				this.stopScoreVersion(pitch);
			}
		}
	}
	
	private void stopScoreVersion(int pitch) {
		this.keysOfCurrentPerformancesInOrder.remove((Integer)pitch);
		JSynPerformance performance = this.currentPerformances.remove(pitch);
		if (performance != null) {
			performance.stopPlaying(false);
		}
	}
	
	private void stopAllScoreVersions() {
		for (JSynPerformance performance : this.currentPerformances.values()) {
			performance.stopPlaying(false);
		}
		this.currentPerformances = new TreeMap<Integer,JSynPerformance>();
		this.keysOfCurrentPerformancesInOrder = new ArrayList<Integer>();
	}
	
	public void transposeAllScoreVersionsByOctave(boolean up) {
		if (this.isPlaying) {
			List<Integer> pitches = new ArrayList<Integer>(this.currentPerformances.keySet());
			for (Integer currentPitch : pitches) {
				int oldPitch = currentPitch;
				int newPitch;
				if (up) {
					newPitch = oldPitch+12;
				} else {
					newPitch = oldPitch-12;
				}
				JSynPerformance currentPerformance = this.currentPerformances.remove(oldPitch);
				currentPerformance.setPitch(newPitch);
				this.currentPerformances.put(newPitch, currentPerformance);
				this.keysOfCurrentPerformancesInOrder.set(this.keysOfCurrentPerformancesInOrder.indexOf(oldPitch), newPitch);
			}
		}
	}
	
	public void changeVelocity(int velocity) {
		if (this.isPlaying) {
			for (JSynPerformance performance : this.currentPerformances.values()) {
				performance.setVelocity(velocity);
			}
		}
	}
	
	public void changeRate(double rateModifier) {
		if (this.isPlaying) {
			for (JSynPerformance performance : this.currentPerformances.values()) {
				performance.setRateModifier(rateModifier);
			}
		}
	}
	
	public boolean isPlaying() {
		return this.currentPerformances.size() > 0 || this.inLiveMidiMode;
	}

	/*
	 * Clean up synthesis by overriding stop() method.
	 */
	public void stopPlaying() {
		if (this.isLooping) {
			//this.bbPlayer.interrupt();
		}
		this.stopAllScoreVersions();
		this.inLiveMidiMode = false;
		this.synth.stop();
		this.isPlaying = false;
	}
	
	/*
	 * Setup synthesis by overriding start() method.
	 */
	/*public void play(JSynObject note) {
		try {
			if (!this.isPlaying()) {
				this.synth.start(JSynPlayer.SAMPLE_RATE, AudioDeviceManager.USE_DEFAULT_DEVICE, 2, AudioDeviceManager.USE_DEFAULT_DEVICE, 2);
			}
			
			JSynModule liveModule = new JSynModule(this);
			
			//note.setOnsetInTicks(JSynPlayer.TICKS_PER_SECOND + JSynPlayer.PLAYBACK_DELAY);
			JSynThread liveThread = new JSynThread(this, note);
			liveThread.setModule(liveModule);
			liveThread.start();
			
	   } catch(SynthException e) {
		  e.printStackTrace();
	   }
	}*/
	
	public double getLastOffset() {
		double lastOffset = 0;
		if (this.score != null) {
			for (JSynObject currentObject : this.score.getObjects()) {
				lastOffset = Math.max(currentObject.getOffset(), lastOffset);
			}
		}
		return lastOffset;
	}
	
	
	
	public void setWaveform(String waveform) {
		this.waveform = waveform;
	}
	
	public UnitOscillator getSpecificOscillator() {
		if (this.waveform.equals(BigBangPlayer.WAVEFORMS[0])) {
			return new SineOscillator();
		} else if (this.waveform.equals(BigBangPlayer.WAVEFORMS[1])) {
			return new SquareOscillator();
		} else if (this.waveform.equals(BigBangPlayer.WAVEFORMS[2])) {
			return new SawtoothOscillator();
		} else {
			return new TriangleOscillator();
		}
	}
	
	public double getRecommendedAmplitude() {
		//TODO pretty bad to control this here...
		if (this.synthActive) {
			if (this.waveform.equals(BigBangPlayer.WAVEFORMS[0]) || this.waveform.equals(BigBangPlayer.WAVEFORMS[3])) {
				return 0.3;
			}
			return 0.15;
		}
		return 0;
	}
	
	private int getChannelPitchKey(int channel, int pitch) {
		return (channel+1)*pitch;
	}
	
	public String getSelectedMidiOutDeviceName() {
		return this.selectedMidiOutDeviceName;
	}
	
	
	//static device management...
	
	static {
		midiOutDevices = new TreeMap<String,MidiDevice>();
		//add empty selection
		midiOutDevices.put(" ", null);
		Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (Info currentInfo : infos) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice(currentInfo);
				if (device.getMaxReceivers() != 0) {
					midiOutDevices.put(BigBangMidiReceiver.getDeviceString(currentInfo), device);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String[] getMidiOutDeviceNames() {
		return midiOutDevices.keySet().toArray(new String[midiOutDevices.size()]);
	}

}

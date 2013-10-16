package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.rubettes.util.CoolFormRegistrant;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;

/** Demonstrate how event buffering and absolute sleep solve the timing problems.
 */
public class JSynPlayer {
	
	public static final int BASE_A4 = 440; // A4 tuning in Hz
	public static final int SAMPLE_RATE = 48000;
	public static final double DEFAULT_ADVANCE = 0.05; //seconds
	public final int MAX_NUMBER_OF_THREADS = 200;
	
	public static final String[] WAVEFORMS = {"Sine", "Square", "Sawtooth", "Triangle"};
	
	//TODO: unite at some point
	private BigBangPlayer bbPlayer;
	private Synthesizer synth;
	private JSynScore score;
	private Map<Integer,JSynPerformance> currentPerformances, currentMonitorPitches;
	private String waveform;
	private double tempo; //in bpm
	private boolean isLooping;
	private double loopOnset;
	private double loopDuration;
	private List<Integer> keysOfCurrentPerformancesInOrder;
	
	private boolean inLiveMidiMode;
	
	public JSynPlayer(BigBangPlayer bbPlayer) {
		this.bbPlayer = bbPlayer;
		this.synth = JSyn.createSynthesizer();
		this.setWaveform(JSynPlayer.WAVEFORMS[0]);
		this.isLooping = false;
		this.inLiveMidiMode = false;
		this.currentPerformances = new TreeMap<Integer,JSynPerformance>();
		this.currentMonitorPitches = new TreeMap<Integer,JSynPerformance>();
		this.keysOfCurrentPerformancesInOrder = new ArrayList<Integer>();
	}
	
	public void startSynth() {
		if (!this.synth.isRunning()) {
			//this.synth.getAudioDeviceManager().setSuggestedOutputLatency(.2);
			this.synth.start(JSynPlayer.SAMPLE_RATE);
			//this.synth.start(JSynPlayer.SAMPLE_RATE, AudioDeviceManager.USE_DEFAULT_DEVICE, 2, AudioDeviceManager.USE_DEFAULT_DEVICE, 2);
		}
	}
	
	public void addToSynth(UnitGenerator generator) {
		this.synth.add(generator);
	}
	
	public void removeFromSynthAndStop(UnitGenerator generator) {
		if (generator != null) {
			generator.stop();
			this.synth.remove(generator);
		}
	}
	
	private void setScore(JSynScore score) {
		this.score = score;
		//standard loop is entire score
		if (score.getObjects().size() > 0) {
			this.loopOnset = score.getObjects().get(0).getOnset();
			this.loopDuration = this.getLastOffset()-this.loopOnset;
		}
		//System.out.println("LOOP "+ this.loopOnset + " " + this.getLastOffset() + " " + this.loopDuration);
		this.bbPlayer.interrupt();
		//System.out.println(score.getObjects());
	}
	
	public void replaceScore(JSynScore score) {
		this.setScore(score);
		for (JSynPerformance performance : this.currentPerformances.values()) {
			performance.updateStartOrChangeTimes();
			performance.replaceScore(score);
		}
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
		this.bbPlayer.interrupt();
	}
	
	public void setPlaybackPosition(double playbackPosition) {
		//if (!this.isLooping) {
			for (JSynPerformance performance : this.currentPerformances.values()) {
				performance.setPlaybackPosition(playbackPosition);
			}
		//
	}
	
	public double getCurrentSynthTime() {
		return this.synth.getCurrentTime();
	}
	
	public double getCurrentSymbolicTimeOfLatestPerformance() {
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
	
	public void play(JSynScore score) {
		this.setScore(score);
		if (!this.inLiveMidiMode) {
			this.playScoreVersion(60, 127);
		}
	}
	
	public void pressMidiKey(int pitch, int velocity, boolean recording) {
		if (recording) {
			this.playMonitorPitch(pitch, velocity);
		} else {
			if (!this.inLiveMidiMode) {
				this.stopAllScoreVersions();
			}
			this.inLiveMidiMode = true;
			this.playScoreVersion(pitch, velocity);
		}
	}
	
	private void playScoreVersion(int pitch, int velocity) {
		JSynPerformance performance = new JSynPerformance(this, this.score, pitch, velocity);
		if (this.isLooping) {
			performance.setSymbolicStartOrChangeTime(this.loopOnset);
		}
		this.currentPerformances.put(pitch, performance);
		this.keysOfCurrentPerformancesInOrder.add(pitch);
		performance.playScore();
	}
	
	private void playMonitorPitch(int pitch, int velocity) {
		JSynScore recordingMonitorScore = new JSynScore();
		JSynObject recordingMonitor = recordingMonitorScore.addNewObject(null, null);
		recordingMonitor.addValues(CoolFormRegistrant.PITCH_FORM, Arrays.asList(new Double(pitch)));
		recordingMonitor.addValues(CoolFormRegistrant.LOUDNESS_FORM, Arrays.asList(new Double(velocity)));
		JSynPerformance performance = new JSynPerformance(this, recordingMonitorScore);
		this.currentMonitorPitches.put(pitch, performance);
		performance.playScore();
	}
	
	public void releaseMidiKey(int pitch, boolean recording) {
		if (recording) {
			JSynPerformance performance = this.currentMonitorPitches.remove(pitch);
			performance.stopPlaying(false);
		} else {
			this.stopScoreVersion(pitch);
		}
	}
	
	private void stopScoreVersion(int pitch) {
		this.keysOfCurrentPerformancesInOrder.remove((Integer)pitch);
		JSynPerformance performance = this.currentPerformances.remove(pitch);
		performance.stopPlaying(false);
	}
	
	private void stopAllScoreVersions() {
		for (JSynPerformance performance : this.currentPerformances.values()) {
			performance.stopPlaying(this.isLooping);
		}
		this.currentPerformances = new TreeMap<Integer,JSynPerformance>();
		this.keysOfCurrentPerformancesInOrder = new ArrayList<Integer>();
	}
	
	public void transposeAllScoreVersionsByOctave(boolean up) {
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
	
	public void changeVelocity(int velocity) {
		for (JSynPerformance performance : this.currentPerformances.values()) {
			performance.setVelocity(velocity);
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
			this.bbPlayer.interrupt();
		}
		this.stopAllScoreVersions();
		this.inLiveMidiMode = false;
		this.synth.stop();
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
		if (this.waveform.equals(JSynPlayer.WAVEFORMS[0])) {
			return new SineOscillator();
		} else if (this.waveform.equals(JSynPlayer.WAVEFORMS[1])) {
			return new SquareOscillator();
		} else if (this.waveform.equals(JSynPlayer.WAVEFORMS[2])) {
			return new SawtoothOscillator();
		} else {
			return new TriangleOscillator();
		}
	}
	
	public double getRecommendedAmplitude() {
		if (this.waveform.equals(JSynPlayer.WAVEFORMS[0]) || this.waveform.equals(JSynPlayer.WAVEFORMS[3])) {
			return 0.3;
		}
		return 0.15;
	}

}
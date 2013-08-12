package org.rubato.rubettes.bigbang.view.player;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice.Info;

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
public class JSynPlayer implements Receiver {
	
	public static final int BASE_A4 = 440; // A4 tuning in Hz
	public static final int SAMPLE_RATE = 44100;
	public static final double DEFAULT_ADVANCE = 0.05; //seconds
	public final int MAX_NUMBER_OF_THREADS = 200;
	
	public static final String[] WAVEFORMS = {"Sine", "Square", "Sawtooth", "Triangle"};
	
	//TODO: unite at some point
	private BigBangPlayer bbPlayer;
	private Synthesizer synth;
	private JSynScore score;
	private List<JSynPerformance> currentPerformances;
	private String waveform;
	private double tempo; //in bpm
	private boolean isLooping;
	private double loopOnset;
	private double loopDuration;
	private double synthTimeAtStartOrChange;
	
	private boolean inLiveMidiMode;
	
	public JSynPlayer(BigBangPlayer bbPlayer) {
		this.bbPlayer = bbPlayer;
		this.synth = JSyn.createSynthesizer();
		//this.synth.add( lag = new LinearRamp() );
		this.setWaveform(JSynPlayer.WAVEFORMS[0]);
		this.isLooping = false;
		this.inLiveMidiMode = true;
		this.currentPerformances = new ArrayList<JSynPerformance>();
		Info[] infos = MidiSystem.getMidiDeviceInfo();
		try {
			MidiDevice inputDevice = MidiSystem.getMidiDevice(infos[0]);
			inputDevice.open();
			inputDevice.getTransmitter().setReceiver(this);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void addToSynth(UnitGenerator generator) {
		this.synth.add(generator);
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
		this.updateStartOrChangeTimes();
		for (JSynPerformance performance : this.currentPerformances) {
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
		this.updateStartOrChangeTimes();
		for (JSynPerformance performance : this.currentPerformances) {
			performance.interrupt();
		}
		this.bbPlayer.interrupt();
	}
	
	public boolean isPlaying() {
		return this.currentPerformances.size() > 0;
	}

	/*
	 * Clean up synthesis by overriding stop() method.
	 */
	public void stopPlaying() {
		if (this.isLooping) {
			this.bbPlayer.interrupt();
		}
		for (JSynPerformance performance : this.currentPerformances) {
			performance.stopPlaying(this.isLooping);
		}
		//this.synth.stop();
		System.out.println("stop " + this + " " + this.score);
	}
	
	public void setPlaybackPosition(double playbackPosition) {
		//if (!this.isLooping) {
			for (JSynPerformance performance : this.currentPerformances) {
				performance.setPlaybackPosition(playbackPosition);
			}
			this.synthTimeAtStartOrChange = this.getCurrentSynthTime();
		//
	}
	
	public double getCurrentSynthTime() {
		return this.synth.getCurrentTime();
	}
	
	public double getCurrentSymbolicTimeOfFirstPerformance() {
		if (this.currentPerformances.size() > 0) {
			return this.currentPerformances.get(0).getCurrentSymbolicTime();
		}
		return 0;
	}
	
	private void updateStartOrChangeTimes() {
		//symbolic time has to update first since it uses previous synthTimeAtStartOrTempoChange!!!!!
		for (JSynPerformance performance : this.currentPerformances) {
			performance.updateSymbolicStartOrChangeTime();
		}
		this.synthTimeAtStartOrChange = this.getCurrentSynthTime();
	}
	
	public double getSynthTimeAtStartOrChange() {
		return this.synthTimeAtStartOrChange;
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
		this.synthTimeAtStartOrChange = this.getCurrentSynthTime();
		JSynPerformance performance = new JSynPerformance(this, this.score);
		if (this.isLooping) {
			performance.setSymbolicStartOrChangeTime(this.loopOnset);
		}
		performance.playScore();
	}
	
	private void playScoreVersion(int pitch, int velocity) {
		JSynPerformance performance = new JSynPerformance(this, this.score, pitch, velocity);
		performance.playScore();
		this.currentPerformances.add(performance);
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

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public void send(MidiMessage message, long timeStamp) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage)message;
			if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
				//velocity 0
				if (shortMessage.getData2() == 0) {
					this.stopPlaying();
				} else {
					//this.playScore();
					this.playScoreVersion(shortMessage.getData1(), shortMessage.getData2());
				}
			} else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
				//TODO has to be pitch-specific!
				this.stopPlaying();
			}
		}
	}

}
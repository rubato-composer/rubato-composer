package org.rubato.rubettes.bigbang.view.player;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;
import com.softsynth.jsyn.SynthException;

/** Demonstrate how event buffering and absolute sleep solve the timing problems.
 */
public class JSynPlayer {
	
	public static final int BASE_A4 = 440; // A4 tuning in Hz
	public static final int SAMPLE_RATE = 44100;
	public static final double DEFAULT_ADVANCE = 0.05; //seconds
	private final int MAX_NUMBER_OF_THREADS = 200;
	
	public static final String[] WAVEFORMS = {"Sine", "Square", "Sawtooth", "Triangle"};
	
	private Synthesizer synth;
	private JSynThreadGroup threads;
	private List<JSynModule> modules;
	private String waveform;
	private double synthTimeAtLastTempoChange;
	private double symbolicTimeAtLastTempoChange;
	private double tempo; //in bpm
	
	public JSynPlayer() {
		this.synth = JSyn.createSynthesizer();
		//this.synth.add( lag = new LinearRamp() );
		this.threads = new JSynThreadGroup();
		this.modules = new ArrayList<JSynModule>();
		this.setWaveform(JSynPlayer.WAVEFORMS[0]);
		this.synthTimeAtLastTempoChange = 0;
		this.symbolicTimeAtLastTempoChange = 0;
	}
	
	public void addToSynth(UnitGenerator generator) {
		this.synth.add(generator);
	}
	
	public double getCurrentSynthTime() {
		return this.synth.getCurrentTime();
	}
	
	public Synthesizer getSynth() {
		return this.synth;
	}
	
	public double getCurrentSymbolicTime() {
		double timeSinceLastTempoChange = this.getCurrentSynthTime()-this.synthTimeAtLastTempoChange;
		return this.symbolicTimeAtLastTempoChange+(this.convertToSymbolicDuration(timeSinceLastTempoChange));
	}
	
	public double getSynthTime(double symbolicTime) {
		double synthDuration = this.convertToSynthDuration(symbolicTime - this.getCurrentSymbolicTime());
		return this.getCurrentSynthTime()+synthDuration;
	}
	
	public double convertToSynthOnset(double symbolicOnset) {
		double synthOnsetFromNow = this.convertToSynthDuration(symbolicOnset - this.getCurrentSymbolicTime());
		return this.getCurrentSynthTime()+synthOnsetFromNow;
	}
	
	public double convertToSynthDuration(double symbolicDuration) {
		return symbolicDuration*60/this.tempo;
	}
	
	private double convertToSymbolicDuration(double synthDuration) {
		return synthDuration/60*this.tempo;
	}

	/*
	 * Setup synthesis by overriding start() method.
	 */
	public void play(JSynScore score) {
		if (!this.isPlaying()) {
			this.synth.start(JSynPlayer.SAMPLE_RATE, AudioDeviceManager.USE_DEFAULT_DEVICE, 2, AudioDeviceManager.USE_DEFAULT_DEVICE, 2);
		} else {
			this.threads.stop();
		}
		
		this.threads = this.generateThreads(score);
		System.out.println("play!");
		this.allocateModules(this.threads);
			
		this.threads.start();
	}
	
	/*
	 * Setup synthesis by overriding start() method.
	 */
	public void play(JSynObject note) {
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
	}
	
	public void setTempo(int bpm) {
		this.tempo = bpm;
		this.symbolicTimeAtLastTempoChange = this.getCurrentSymbolicTime();
		this.synthTimeAtLastTempoChange = this.synth.getCurrentTime();
		this.threads.interrupt();
	}
	
	public void replaceScore(JSynScore score) {
		if (this.isPlaying()) {
			this.threads.stop();
			JSynThreadGroup newThreads = this.generateThreads(score);
			this.allocateModules(newThreads);
			this.threads = newThreads;
		}
		this.threads.start();
	}
	
	private JSynThreadGroup generateThreads(JSynScore score) {
		List<JSynObject> notes = score.getObjects();
		JSynThreadGroup threads = new JSynThreadGroup();
		if (notes.size() > 0) {
			for (JSynObject currentNote : notes) {
				currentNote.setOnset(currentNote.getOnset());
				this.addNoteToConvenientThread(currentNote, threads);
			}
		}
		this.removeExcessiveThreads(threads);
		return threads;
	}
	
	private void addNoteToConvenientThread(JSynObject note, JSynThreadGroup threads) {
		double onset = note.getOnset();
		double offset = note.getOffset();
		double voice = note.getVoice();
		boolean convenientThreadFound = false;
		for (JSynThread currentThread : threads) {
			if (currentThread.getVoice() == voice) {
				if (!currentThread.playsAt(onset, offset)) {
					currentThread.addObject(note);
					convenientThreadFound = true;
					return;
				}
			}
		}
		if (!convenientThreadFound) {
			threads.add(new JSynThread(this, note));
		}
	}
	
	private void removeExcessiveThreads(JSynThreadGroup threads) {
		while (threads.size() > this.MAX_NUMBER_OF_THREADS) {
			threads.remove((int)Math.round(Math.random()*(threads.size()-1)));
		}
	}
	
	//reallocate sound modules in a way to get as few glitches as possible
	private void allocateModules(JSynThreadGroup threads) {
		
		//order all voices by their distance from one of the module's playing pitch
		TreeMap<Double,List<JSynModule>> remainingModules = new TreeMap<Double,List<JSynModule>>();
		for (JSynModule currentModule : this.modules) {
			double currentFrequency = currentModule.getCarrierFrequency();
			if (remainingModules.keySet().contains(currentFrequency)) {
				remainingModules.get(currentFrequency).add(currentModule);
			} else {
				remainingModules.put(currentFrequency, new ArrayList<JSynModule>(Arrays.asList(currentModule)));
			}
		}
		
		//associate closest modules or create new ones
		double currentTime = this.synth.getCurrentTime();
		List<JSynThread> notPlayingThreads = new ArrayList<JSynThread>();
		for (JSynThread currentThread : threads) {
			JSynObject objectAtCurrentTime = currentThread.getObjectAt(currentTime);
			JSynModule closestModule = null;
			//System.out.println(remainingModules);
			if (objectAtCurrentTime != null) {
				double currentFrequency = objectAtCurrentTime.getFrequency();
				closestModule = this.getModuleWithClosestFrequency(currentFrequency, remainingModules);
				if (closestModule != null) {
					currentThread.setModule(closestModule);
					//System.out.println(currentFrequency + " " + closestModule.getCarrierFrequency());
				} else {
					JSynModule newModule = new JSynModule(this);
					this.modules.add(newModule);
					currentThread.setModule(newModule);
					//System.out.println(currentFrequency + " new module");
				}
			} else {
				notPlayingThreads.add(currentThread);
			}
		}
		
		//remove unused modules from this.modules
		for (List<JSynModule> currentRemainingModules : remainingModules.values()) {
			for (JSynModule currentRemainingModule : currentRemainingModules) {
				if (notPlayingThreads.size() > 0) {
					notPlayingThreads.remove(0).setModule(currentRemainingModule);
				} else {
					this.modules.remove(currentRemainingModule);
					currentRemainingModule.finalize();
				}
			}
		}
		
		//make more modules for threads that are not playing 
		for (JSynThread currentThread : notPlayingThreads) {
			JSynModule newModule = new JSynModule(this);
			this.modules.add(newModule);
			currentThread.setModule(newModule);
		}
	}
	
	//returns the module whose frequency is closest to the one of thread
	private JSynModule getModuleWithClosestFrequency(double frequency, TreeMap<Double,List<JSynModule>> modules) {
		TreeSet<Double> frequencies = new TreeSet<Double>(modules.keySet());
		if (frequencies.contains(frequency)) {
			return this.removeFirstInSet(frequency, modules);
		}
		double headFrequency = -1, tailFrequency = -1;
		if (frequencies.headSet(frequency).size() > 0) {
			headFrequency = frequencies.headSet(frequency).last();
		}
		if (frequencies.tailSet(frequency).size() > 0) {
			tailFrequency = frequencies.tailSet(frequency).first();
		}
		if ((headFrequency != -1 && tailFrequency != -1 && frequency-headFrequency < tailFrequency-frequency)
				|| (headFrequency != -1 && tailFrequency == -1)) {
			return this.removeFirstInSet(headFrequency, modules); 
		} else if (tailFrequency != -1) {
			return this.removeFirstInSet(tailFrequency, modules);
		}
		return null;
	}
	
	private JSynModule removeFirstInSet(double frequency, TreeMap<Double,List<JSynModule>> modules) {
		List<JSynModule> moduleWithFrequency = modules.get(frequency);
		JSynModule closestModule = moduleWithFrequency.remove(0); 
		if (moduleWithFrequency.size() == 0) {
			modules.remove(frequency);
		}
		return closestModule;
	}
	
	public boolean isPlaying() {
		return this.threads.isRunning();
	}

  /*
   * Clean up synthesis by overriding stop() method.
   */
	public void stopPlaying() {
		if (this.isPlaying()) {
			// Set flag so that run() loop will exit the next time around.
			this.threads.stop(); // tell run() to exit peacefully
			//System.out.println("Stopping engine " + this)
			this.synth.stop();
			
			//System.out.println("Stopped " + this);
		}
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
			return 0.1;
		}
		return 0.05;
	}

}
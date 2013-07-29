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
	
	//TODO: unite at some point
	private BigBangPlayer bbPlayer;
	private Synthesizer synth;
	private JSynScore score;
	private JSynThreadGroup threads;
	private List<JSynModule> modules;
	private String waveform;
	private double synthTimeAtStartOrChange;
	private double symbolicTimeAtStartOrChange;
	private double tempo; //in bpm
	private boolean isLooping;
	private double loopOnset;
	private double loopDuration;
	private boolean isPlaying;
	
	public JSynPlayer(BigBangPlayer bbPlayer) {
		this.bbPlayer = bbPlayer;
		this.synth = JSyn.createSynthesizer();
		//this.synth.add( lag = new LinearRamp() );
		this.threads = new JSynThreadGroup(false);
		this.modules = new ArrayList<JSynModule>();
		this.setWaveform(JSynPlayer.WAVEFORMS[0]);
		this.synthTimeAtStartOrChange = 0;
		this.symbolicTimeAtStartOrChange = 0;
		this.isLooping = false;
		this.isPlaying = false;
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
		if (this.isPlaying) {
			this.updateStartOrChangeTimes();
			this.setScore(score);
			JSynThreadGroup newThreads = this.generateThreads(false);
			this.allocateModules(newThreads);
			this.threads.stop();
			this.threads = newThreads;
			//System.out.println("replace " + this.score + " " + this.getCurrentSynthTime());
		}
		this.threads.start();
	}
	
	public void setIsLooping(boolean isLooping) {
		this.isLooping = isLooping;
	}
	
	public boolean isLooping() {
		return this.isLooping;
	}
	
	public void setTempo(int bpm) {
		this.tempo = bpm;
		if (this.isPlaying) {
			this.updateStartOrChangeTimes();
		} else {
			this.setPlaybackPosition(0);
		}
		this.threads.interrupt();
		this.bbPlayer.interrupt();
	}
	
	public void setPlaybackPosition(double playbackPosition) {
		//if (!this.isLooping) {
			boolean restart = playbackPosition < this.symbolicTimeAtStartOrChange;
			this.symbolicTimeAtStartOrChange = playbackPosition;
			this.synthTimeAtStartOrChange = this.getCurrentSynthTime();
			if (restart) {
				this.restartPlaying();
			}
		//}
	}
	
	private void updateStartOrChangeTimes() {
		//symbolic time has to update first since it uses previous synthTimeAtStartOrTempoChange!!!!!
		this.symbolicTimeAtStartOrChange = this.getCurrentSymbolicTime();
		this.synthTimeAtStartOrChange = this.getCurrentSynthTime();
		//System.out.println("T "+this.symbolicTimeAtStartOrChange + " " + this.synthTimeAtStartOrChange);
		//TODO: restartPlaying()????!!!!???
	}
	
	private void restartPlaying() {
		this.stopPlaying();
		this.playScore();
	}
	
	public boolean isPlaying() {
		return this.isPlaying;
	}

  /*
   * Clean up synthesis by overriding stop() method.
   */
	public void stopPlaying() {
		if (this.isPlaying) {
			this.isPlaying = false;
			if (this.isLooping) {
				this.bbPlayer.interrupt();
			} else {
				this.threads.stop(); // tell run() to exit peacefully
			}
			for (JSynModule currentModule : this.modules) {
				currentModule.mute();
			}
			//this.synth.stop();
		}
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
		double timeSinceLastTempoChange = this.getCurrentSynthTime()-this.synthTimeAtStartOrChange;
		double currentSymbolicTime = this.symbolicTimeAtStartOrChange+this.convertToSymbolicDuration(timeSinceLastTempoChange);
		if (this.isLooping && this.getLastOffset() > 0) {
			currentSymbolicTime = ((currentSymbolicTime-this.loopOnset) % this.loopDuration) + this.loopOnset; 
		}
		return currentSymbolicTime;
	}
	
	public double getSynthOnset(double symbolicOnset, boolean inNextLoop) {
		double synthOnsetFromNow;
		if (inNextLoop && symbolicOnset < this.getCurrentSymbolicTime()) {
			synthOnsetFromNow = this.convertToSynthDuration(this.loopDuration - (this.getCurrentSymbolicTime()-this.loopOnset) + symbolicOnset-this.loopOnset);
			//System.out.println(this.loopDuration + " " + this.getCurrentSymbolicTime() + " " + symbolicOnset + " " + this.loopOnset + " " + synthOnsetFromNow);
		} else {
			synthOnsetFromNow = this.convertToSynthDuration(symbolicOnset - this.getCurrentSymbolicTime());
		}
		return this.getCurrentSynthTime()+synthOnsetFromNow;
	}
	
	public double convertToSynthDuration(double symbolicDuration) {
		return symbolicDuration*60/this.tempo;
	}
	
	private double convertToSymbolicDuration(double synthDuration) {
		return synthDuration/60*this.tempo;
	}
	
	public void play(JSynScore score) {
		this.setScore(score);
		this.synthTimeAtStartOrChange = this.getCurrentSynthTime();
		if (this.isLooping) {
			this.symbolicTimeAtStartOrChange = this.loopOnset;
		}
		this.playScore();
	}

	private void playScore() {
		if (!this.isPlaying) {
			if (!this.synth.isRunning()) {
				this.synth.start(JSynPlayer.SAMPLE_RATE, AudioDeviceManager.USE_DEFAULT_DEVICE, 2, AudioDeviceManager.USE_DEFAULT_DEVICE, 2);
			}
		} else {
			this.threads.stop();
		}
		
		this.isPlaying = true;
		
		this.threads = this.generateThreads(false);
		this.allocateModules(this.threads);
		this.threads.start();
		//System.out.println("play "+ this.score + " "  + this.getCurrentSynthTime() + " " + this.getCurrentSymbolicTime());
		
		if (this.isLooping) {
			double timeOfNextLoop = this.getCurrentSynthTime();
			
			while (this.isLooping && this.isPlaying) {
				double currentLoopDuration = this.convertToSynthDuration(this.loopDuration);
				timeOfNextLoop += currentLoopDuration;
				
				while (timeOfNextLoop - JSynPlayer.DEFAULT_ADVANCE > this.getCurrentSynthTime()) {
					try {
						this.synth.sleepUntil(timeOfNextLoop - JSynPlayer.DEFAULT_ADVANCE);
					} catch (InterruptedException e) {
						if (this.isLooping && this.isPlaying) {
							timeOfNextLoop -= currentLoopDuration;
							currentLoopDuration = this.convertToSynthDuration(this.loopDuration);
							timeOfNextLoop += currentLoopDuration;
							//go back to sleep
						} else {
							this.threads.stop();
							return;
						}
					}
				}
				
				//System.out.println("loop "+ this.score + " "  + this.getCurrentSynthTime());
				//this.threads.stop();
				this.threads = this.generateThreads(true);
				this.allocateModules(this.threads);
				this.threads.start();
			}
		}
		//System.out.println("done "+ this.score + " "  + this.getCurrentSynthTime());
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
	
	private double getLastOffset() {
		double lastOffset = 0;
		if (this.score != null) {
			for (JSynObject currentObject : this.score.getObjects()) {
				lastOffset = Math.max(currentObject.getOffset(), lastOffset);
			}
		}
		return lastOffset;
	}
	
	/*
	 * generates threads based on copies of this score, adjusted to the given starting time
	 */
	private JSynThreadGroup generateThreads(boolean playInNextLoop) {
		List<JSynObject> objects = this.score.getObjects();
		JSynThreadGroup threads = new JSynThreadGroup(playInNextLoop);
		if (objects.size() > 0) {
			for (JSynObject currentObject : objects) {
				JSynObject clone = currentObject.clone();
				this.addNoteToConvenientThread(clone, threads);
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
	private synchronized void allocateModules(JSynThreadGroup threads) {
		
		//order all voices by their distance from one of the module's playing pitch
		TreeMap<Double,List<JSynModule>> remainingModules = new TreeMap<Double,List<JSynModule>>();
		for (JSynModule currentModule : this.modules) {
			double currentFrequency = currentModule.getMainCarrierFrequency();
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
				double currentFrequency = objectAtCurrentTime.getMainFrequency();
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
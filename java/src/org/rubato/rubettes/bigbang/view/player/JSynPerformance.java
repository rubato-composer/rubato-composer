package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangObject;

public class JSynPerformance extends Thread {
	
	private BigBangPlayer player;
	private JSynScore score;
	private JSynThreadGroup threads;
	private List<JSynModule> modules;
	private double synthTimeAtStartOrChange;
	private double symbolicTimeAtStartOrChange;
	private boolean isPlaying;
	private Integer pitch, velocity;
	
	public JSynPerformance(BigBangPlayer player, JSynScore score) {
		this(player, score, null, null);
	}
	
	public JSynPerformance(BigBangPlayer player, JSynScore score, Integer pitch, Integer velocity) {
		this.player = player;
		this.score = score;
		this.threads = new JSynThreadGroup(false);
		this.modules = new ArrayList<JSynModule>();
		this.symbolicTimeAtStartOrChange = 0;
		this.isPlaying = false;
		this.pitch = pitch;
		this.velocity = velocity;
	}
	
	public BigBangPlayer getPlayer() {
		return this.player;
	}
	
	public void setPitch(int pitch) {
		this.pitch = pitch;
		this.replaceThreads();
	}
	
	public int getPitch() {
		return this.pitch;
	}
	
	public void setVelocity(int velocity) {
		this.velocity = velocity;
		this.replaceThreads();
	}
	
	public void replaceScore(JSynScore score) {
		this.score = score;
		this.replaceThreads();
	}
	
	private void replaceThreads() {
		JSynThreadGroup newThreads = this.generateThreads(false);
		this.allocateModules(newThreads);
		this.threads.stop();
		this.threads = newThreads;
		this.threads.start();
	}
	
	public void interrupt() {
		this.threads.interrupt();
	}
	
	public void setPlaybackPosition(double playbackPosition) {
		boolean restart = playbackPosition < this.symbolicTimeAtStartOrChange;
		this.symbolicTimeAtStartOrChange = playbackPosition;
		this.synthTimeAtStartOrChange = this.player.getCurrentSynthTime();
		//TODO: reinstate!!!!
		/*if (restart) {
			this.restartPlaying();
		}*/
	}
	
	public void setSymbolicStartOrChangeTime(double symbolicTime) {
		this.symbolicTimeAtStartOrChange = symbolicTime;
	}
	
	public void updateStartOrChangeTimes() {
		//symbolic time has to update first since it uses previous synthTimeAtStartOrTempoChange!!!!!
		this.symbolicTimeAtStartOrChange = this.getCurrentSymbolicTime();
		this.synthTimeAtStartOrChange = this.player.getCurrentSynthTime();
		//TODO: restartPlaying()????!!!!???
	}
	
	public double getCurrentSymbolicTime() {
		double timeSinceLastTempoChange = this.player.getCurrentSynthTime()-this.synthTimeAtStartOrChange;
		double currentSymbolicTime = this.symbolicTimeAtStartOrChange+this.player.convertToSymbolicDuration(timeSinceLastTempoChange);
		if (this.player.isLooping() && this.player.getLastOffset() > 0) {
			double loopOnset = this.player.getLoopOnset();
			currentSymbolicTime = ((currentSymbolicTime-loopOnset) % this.player.getLoopDuration()) + loopOnset; 
		}
		return currentSymbolicTime;
	}
	
	public double getSynthOnset(double symbolicOnset, boolean inNextLoop) {
		double synthOnsetFromNow;
		if (inNextLoop && symbolicOnset < this.getCurrentSymbolicTime()) {
			double loopOnset = this.player.getLoopOnset();
			synthOnsetFromNow = this.player.convertToSynthDuration(this.player.getLoopDuration() - (this.getCurrentSymbolicTime()-loopOnset) + symbolicOnset-loopOnset);
			//System.out.println(this.loopDuration + " " + this.getCurrentSymbolicTime() + " " + symbolicOnset + " " + this.loopOnset + " " + synthOnsetFromNow);
		} else {
			synthOnsetFromNow = this.player.convertToSynthDuration(symbolicOnset - this.getCurrentSymbolicTime());
		}
		return this.player.getCurrentSynthTime()+synthOnsetFromNow;
	}
	
	/*
	 * Clean up synthesis by overriding stop() method.
	 */
	public void stopPlaying(boolean justMute) {
		this.interrupt();
		this.isPlaying = false;
		for (JSynModule currentModule : this.modules) {
			currentModule.mute();
		}
		if (!justMute) {
			this.threads.stop(); // tell run() to exit peacefully
			for (JSynModule currentModule : this.modules) {
				currentModule.finalize();
			}
		}
	}
	
	public void startPlaying() {
		this.start();
	}
	
	public void run() {
		if (this.score != null) {
			if (!this.isPlaying) {
				this.player.startSynth();
			} else {
				this.threads.stop();
			}
			
			this.isPlaying = true;
			this.synthTimeAtStartOrChange = this.player.getCurrentSynthTime();
			
			this.threads = this.generateThreads(false);
			this.allocateModules(this.threads);
			this.threads.start();
			
			if (this.player.isLooping()) {
				double timeOfNextLoop = this.player.getCurrentSynthTime();
				
				while (this.player.isLooping() && this.isPlaying) {
					double currentLoopDuration = this.player.convertToSynthDuration(this.player.getLoopDuration());
					timeOfNextLoop += currentLoopDuration;
					
					while (timeOfNextLoop - BigBangPlayer.DEFAULT_ADVANCE > this.player.getCurrentSynthTime()) {
						try {
							long sleepTime = (long)Math.ceil((timeOfNextLoop - BigBangPlayer.DEFAULT_ADVANCE - this.player.getCurrentSynthTime())*1000);
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							/*if (this.player.isLooping() && this.isPlaying) {
								timeOfNextLoop -= currentLoopDuration;
								currentLoopDuration = this.player.convertToSynthDuration(this.player.getLoopDuration());
								timeOfNextLoop += currentLoopDuration;
								//go back to sleep
							} else {*/
								this.threads.stop();
								return;
							//}
						}
					}
					
					//System.out.println("loop "+ this.score + " "  + this.player.getCurrentSynthTime());
					//this.threads.stop();
					this.threads = this.generateThreads(true);
					this.allocateModules(this.threads);
					this.threads.start();
				}
			}
			//System.out.println("done "+ this.score + " "  + this.getCurrentSynthTime());
		}
	}
	
	/*
	 * generates threads based on copies of this score, adjusted to the given starting time and pitch
	 */
	private JSynThreadGroup generateThreads(boolean playInNextLoop) {
		Set<JSynObject> objects = this.score.getObjects();
		JSynThreadGroup threads = new JSynThreadGroup(playInNextLoop);
		if (objects.size() > 0) {
			for (JSynObject currentObject : objects) {
				JSynObject clone = currentObject.clone();
				if (this.pitch != null) {
					clone.setTranspositionInterval(this.pitch-60);
				}
				if (this.velocity != null) {
					clone.setLoudnessRatio(((double)this.velocity)/127);
				}
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
		while (threads.size() > this.player.MAX_NUMBER_OF_THREADS) {
			threads.remove((int)Math.round(Math.random()*(threads.size()-1)));
		}
	}
	
	//reallocate sound modules in order to get as few glitches as possible
	private synchronized void allocateModules(JSynThreadGroup threads) {
		
		//associate currently playing threads with modules playing same BigBangObjects, or create new ones
		Set<JSynModule> remainingModules = new HashSet<JSynModule>(this.modules);
		Map<BigBangObject,JSynModule> objectsAndModulesMap = this.getCurrentObjectsAndModules();
		double currentTime = this.player.getSynth().getCurrentTime();
		List<JSynThread> notPlayingThreads = new ArrayList<JSynThread>();
		
		for (JSynThread currentThread : threads) {
			JSynObject objectAtCurrentTime = currentThread.getObjectAt(currentTime);
			
			//System.out.println(remainingModules);
			if (objectAtCurrentTime != null) {
				JSynModule objectModule = objectsAndModulesMap.get(objectAtCurrentTime.getBigBangObject());
				
				if (objectModule != null) {
					currentThread.setModule(objectModule);
					remainingModules.remove(objectModule);
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
		for (JSynModule currentRemainingModule : remainingModules) {
			if (notPlayingThreads.size() > 0) {
				notPlayingThreads.remove(0).setModule(currentRemainingModule);
			} else {
				this.modules.remove(currentRemainingModule);
				currentRemainingModule.finalize();
			}
		}
		
		//make more modules for threads that are not playing 
		for (JSynThread currentThread : notPlayingThreads) {
			JSynModule newModule = new JSynModule(this);
			this.modules.add(newModule);
			currentThread.setModule(newModule);
		}
	}
	
	private Map<BigBangObject,JSynModule> getCurrentObjectsAndModules() {
		Map<BigBangObject,JSynModule> objectsAndModulesMap = new HashMap<BigBangObject,JSynModule>();
		for (JSynModule currentModule : this.modules) {
			JSynObject currentObject = currentModule.getCurrentObject();
			if (currentObject != null) {
				objectsAndModulesMap.put(currentObject.getBigBangObject(), currentModule);
			}
		}
		return objectsAndModulesMap;
	}

}

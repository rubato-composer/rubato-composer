package org.rubato.rubettes.bigbang.view.player;

import java.util.Map;
import java.util.TreeMap;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.util.CoolFormRegistrant;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangRecorder extends Thread {
	
	public static final int MIN_BPM = 1;
	public static final int MAX_BPM = 5000;
	public static final int INITIAL_BPM = 100;
	
	private BigBangPlayer player;
	private BigBangController controller;
	private boolean threadRunning, isRecording;
	private long startingTime;
	private Map<Integer,Double> currentKeyOnsets;
	private Map<Integer,Integer> currentKeyVelocities;
	
	public BigBangRecorder(BigBangPlayer player, BigBangController controller) {
		this.player = player;
		this.controller = controller;
		this.currentKeyOnsets = new TreeMap<Integer,Double>();
		this.currentKeyVelocities = new TreeMap<Integer,Integer>();
	}
	
	public boolean isRecording() {
		return this.isRecording;
	}
	
	public synchronized void toggleRecordMode() {
		if (!this.isRecording()) {
			this.startRecording();
		} else {
			this.stopRecording();
		}
	}
	
	public synchronized void startRecording() {
		this.startRecording(System.currentTimeMillis());
	}
	
	private synchronized void startRecording(long startingTime) {
		if (!this.threadRunning) {
			this.threadRunning = true;
			this.start();
		}
		this.isRecording = true;
		this.startingTime = startingTime;
	}
	
	public synchronized void pressMidiKey(int pitch, int velocity) {
		if (this.isRecording) {
			this.currentKeyOnsets.put(pitch, this.player.getCurrentSymbolicTime());
			this.currentKeyVelocities.put(pitch, velocity);
		}
	}
	
	public synchronized void releaseMidiKey(int pitch) {
		if (this.isRecording) {
			//TODO make flexible for other forms!!!!
			Map<DenotatorPath,Double> denotatorValues = new TreeMap<DenotatorPath,Double>();
			denotatorValues.put(new DenotatorPath(CoolFormRegistrant.NOTE_FORM, new int[]{0}), this.currentKeyOnsets.get(pitch));
			denotatorValues.put(new DenotatorPath(CoolFormRegistrant.NOTE_FORM, new int[]{1}), new Double(pitch));
			denotatorValues.put(new DenotatorPath(CoolFormRegistrant.NOTE_FORM, new int[]{2}), this.currentKeyVelocities.get(pitch).doubleValue());
			double duration = this.player.getCurrentSymbolicTime()-this.currentKeyOnsets.get(pitch);
			denotatorValues.put(new DenotatorPath(CoolFormRegistrant.NOTE_FORM, new int[]{3}), duration);
			this.controller.addObject(denotatorValues, new DenotatorPath(CoolFormRegistrant.SCORE_FORM, new int[]{}));
			this.currentKeyOnsets.remove(pitch);
			this.currentKeyVelocities.remove(pitch);
		}
	}
	
	public void run() {
		while (this.threadRunning) {
			try { Thread.sleep(100); } catch (InterruptedException e) { } //e.printStackTrace(); }
			if (this.startingTime != 0 && System.currentTimeMillis() >= this.startingTime) {
				//TODO always update score with a preview!!!!!
				this.startingTime = 0;
			}
		}
	}
	
	public void stopRecording() {
		this.interrupt(); //interrupt to stop thread and stop looping immediately in jsynplayer
		this.startingTime = 0;
		this.isRecording = false;
	}

}

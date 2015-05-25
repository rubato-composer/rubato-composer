package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.model.BigBangSwingView;
import org.rubato.rubettes.bigbang.view.model.BigBangView;
import org.rubato.rubettes.util.CoolFormRegistrant;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangRecorder extends Thread {
	
	private BigBangView view;
	private BigBangPlayer player;
	private BigBangController controller;
	private boolean threadRunning, isRecording;
	private long startingTime;
	private Map<Integer,Double> currentKeyOnsets;
	private Map<Integer,Integer> currentKeyVelocities;
	
	public BigBangRecorder(BigBangView view, BigBangPlayer player, BigBangController controller) {
		this.view = view;
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
	
	/**
	 * 
	 * @param channel
	 * @param pitch
	 * @param velocity can be null! is then replaced with standard value
	 */
	public synchronized void pressMidiKey(int channel, int pitch, Integer velocity) {
		if (this.isRecording) {
			int key = this.getChannelPitchKey(channel, pitch);
			this.currentKeyOnsets.put(key, this.player.getCurrentSymbolicTime());
			this.currentKeyVelocities.put(key, velocity);
		}
	}
	
	public synchronized void releaseMidiKey(int channel, int pitch) {
		if (this.isRecording) {
			//TODO make flexible for other forms!!!!
			int key = this.getChannelPitchKey(channel, pitch);
			Map<DenotatorPath,Double> denotatorValues = new TreeMap<DenotatorPath,Double>();
			this.putDenotatorValueIfFormPresent(CoolFormRegistrant.ONSET_FORM, this.currentKeyOnsets.get(key), denotatorValues);
			this.putDenotatorValueIfFormPresent(CoolFormRegistrant.PITCH_FORM, new Double(pitch), denotatorValues);
			Double loudness = new Double(this.currentKeyVelocities.get(key));
			this.putDenotatorValueIfFormPresent(CoolFormRegistrant.LOUDNESS_FORM, loudness, denotatorValues);
			double duration = this.player.getCurrentSymbolicTime()-this.currentKeyOnsets.get(key);
			this.putDenotatorValueIfFormPresent(CoolFormRegistrant.DURATION_FORM, duration, denotatorValues);
			this.putDenotatorValueIfFormPresent(CoolFormRegistrant.VOICE_FORM, new Double(channel), denotatorValues);
			List<Map<DenotatorPath,Double>> denotatorValuesList = new ArrayList<Map<DenotatorPath,Double>>();
			denotatorValuesList.add(denotatorValues);
			List<DenotatorPath> powersetPaths = new ArrayList<DenotatorPath>();
			powersetPaths.add(this.view.getDisplayObjects().getActiveObjectType().getPath().getParentPath());
			
			this.controller.addObjects(denotatorValuesList, powersetPaths, false);
			this.currentKeyOnsets.remove(key);
			this.currentKeyVelocities.remove(key);
		}
	}
	
	private void putDenotatorValueIfFormPresent(SimpleForm form, Double value, Map<DenotatorPath,Double> denotatorValues) {
		Integer valueIndex = this.view.getDisplayObjects().getActiveObjectFirstValueIndex(form);
		if (valueIndex >= 0 && value != null) {
			DenotatorPath valuePath = this.view.getDisplayObjects().getActiveObjectValuePathAt(valueIndex);
			denotatorValues.put(valuePath, value);
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
	
	private int getChannelPitchKey(int channel, int pitch) {
		return (channel+1)*pitch;
	}

}

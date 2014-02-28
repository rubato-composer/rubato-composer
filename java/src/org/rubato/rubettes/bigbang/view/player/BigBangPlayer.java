package org.rubato.rubettes.bigbang.view.player;

public class BigBangPlayer extends Thread {
	
	public static final int MIN_BPM = 1;
	public static final int MAX_BPM = 5000;
	public static final int INITIAL_BPM = 100;
	
	//needs separate variable since player only starts after a brief delay
	private boolean isPlaying;
	private boolean threadRunning;
	private long startingTime;
	private JSynPlayer player;
	
	private JSynScore score;
	private int bpm;
	
	public BigBangPlayer() {
		this.player = new JSynPlayer(this);
		this.setTempo(this.bpm);
		this.setWaveform(JSynPlayer.WAVEFORMS[0]);
		this.startingTime = 0;
		this.isPlaying = false;
	}
	
	public synchronized void setScore(JSynScore score) {
		this.score = score;
		this.update();
	}
	
	/**
	 * reschedules the score this is playing right now (good in case of object state changes)
	 */
	public void update() {
		if (this.isPlaying) {
			this.player.replaceScore(this.score);
		}
	}
	
	public boolean isPlaying() {
		return this.isPlaying;
	}
	
	public double getCurrentSymbolicTime() {
		return this.player.getCurrentSymbolicTimeOfLatestPerformance();
	}
	
	public synchronized void togglePlayMode() {
		if (!this.isPlaying()) {
			this.startPlaying();
		} else {
			this.stopPlaying();
		}
	}
	
	public synchronized void startPlaying() {
		this.startPlaying(System.currentTimeMillis());
	}
	
	private synchronized void startPlaying(long startingTime) {
		if (!this.threadRunning) {
			this.threadRunning = true;
			this.start();
		}
		this.startingTime = startingTime;
		this.isPlaying = true;
	}
	
	public synchronized void pressMidiKey(int pitch, int velocity, boolean recording) {
		if (this.isPlaying()) {
			this.player.pressMidiKey(pitch, velocity, recording);
		}
	}
	
	public synchronized void releaseMidiKey(int pitch, boolean recording) {
		if (this.isPlaying()) {
			this.player.releaseMidiKey(pitch, recording);
		}
	}
	
	public synchronized void transposeAllScoreVersionsByOctave(boolean up) {
		if (this.isPlaying()) {
			this.player.transposeAllScoreVersionsByOctave(up);
		}
	}
	
	public synchronized void changeVelocity(int velocity) {
		if (this.isPlaying()) {
			this.player.changeVelocity(velocity);
		}
	}
	
	public void run() {
		while(this.threadRunning) {
			try { Thread.sleep(100); } catch (InterruptedException e) { } //e.printStackTrace(); }
			if (this.startingTime != 0 && System.currentTimeMillis() >= this.startingTime
					&& this.score != null && !this.player.isPlaying()) {
				this.player.play(this.score);
				this.startingTime = 0;
			}
		}
	}
	
	public void stopPlaying() {
		this.player.stopPlaying();
		this.isPlaying = false;
		this.interrupt(); //interrupt to stop thread and stop looping immediately in jsynplayer
		this.startingTime = 0;
	}
	
	public void setIsLooping(boolean isLooping) {
		this.player.setIsLooping(isLooping);
	}
	
	public void setTempo(int bpm) {
		this.bpm = bpm;
		this.player.setTempo(bpm);
	}
	
	public void setPlaybackPosition(double playbackPosition) {
		this.player.setPlaybackPosition(playbackPosition);
	}
	
	public void setWaveform(String waveform) {
		this.player.setWaveform(waveform);
	}

}

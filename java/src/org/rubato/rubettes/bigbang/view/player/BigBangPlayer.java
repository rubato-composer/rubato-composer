package org.rubato.rubettes.bigbang.view.player;

public class BigBangPlayer extends Thread {
	
	public static final int MIN_BPM = 1;
	public static final int MAX_BPM = 1000;
	public static final int INITIAL_BPM = 100;
	
	
	private boolean isRunning;
	private long startingTime;
	private JSynPlayer player;
	
	private JSynScore score;
	private int bpm;
	
	public BigBangPlayer() {
		this.player = new JSynPlayer();
		this.setTempo(this.bpm);
		this.setWaveform(JSynPlayer.WAVEFORMS[0]);
		this.startingTime = 0;
	}
	
	public synchronized void setScore(JSynScore score) {
		this.score = score;
		if (this.player.isPlaying()) {
			this.player.replaceScore(score);
		}
	}
	
	public boolean isPlaying() {
		return this.player.isPlaying();
	}
	
	public double getCurrentSymbolicTime() {
		return this.player.getCurrentSymbolicTime();
	}
	
	public synchronized void startPlaying() {
		this.startPlaying(System.currentTimeMillis());
	}
	
	private synchronized void startPlaying(long startingTime) {
		if (!this.isRunning) {
			this.isRunning = true;
			this.start();
		}
		this.startingTime = startingTime;
	}
	
	public synchronized void playObject(JSynObject object) {
		this.player.play(object);
	}
	
	public void run() {
		while(this.isRunning) {
			try { Thread.sleep(100); } catch (InterruptedException e) { } //e.printStackTrace(); }
			if (this.startingTime != 0 && System.currentTimeMillis() >= this.startingTime
					&& this.score != null && !this.player.isPlaying()) {
				this.player.play(this.score);
			}
		}
	}
	
	public void stopPlaying() {
		this.player.stopPlaying();
		this.interrupt(); //interrupt to stop thread and stop looping immediately in jsynplayer
		this.startingTime = 0;
	}
	
	public void setTempo(int bpm) {
		this.bpm = bpm;
		this.player.setTempo(bpm);
	}
	
	public void setWaveform(String waveform) {
		this.player.setWaveform(waveform);
	}

}

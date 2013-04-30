package org.rubato.rubettes.bigbang.view.player;

public class BigBangPlayer extends Thread {
	
	private final int DELAY = 300;
	public static final int MIN_BPM = 20;
	public static final int MAX_BPM = 500;
	public static final int INITIAL_BPM = 100;
	
	
	private boolean isRunning;
	private long startingTime;
	private JSynPlayer player;
	
	private JSynScore score;
	private int bpm;
	private String waveform;
	
	public BigBangPlayer() {
		this.init();
	}
	
	private void init() {
		this.player = new JSynPlayer();
		this.setTempo(BigBangPlayer.INITIAL_BPM);
		this.setWaveform(JSynPlayer.WAVEFORMS[0]);
		this.startingTime = 0;
		this.isRunning = true;
		this.start();
	}
	
	public synchronized void setScore(JSynScore score) {
		this.score = score;
		if (this.player.isPlaying()) {
			this.player.replaceScore(score);
		}
	}
	
	public boolean isPlaying() {
		return this.isRunning;
	}
	
	public synchronized void startPlaying() {
		this.startPlaying(this.score, System.currentTimeMillis() + this.DELAY);
	}
	
	private synchronized void startPlaying(JSynScore score, long startingTime) {
		this.startingTime = startingTime;
		this.score = score;
	}
	
	public synchronized void playObject(JSynObject object) {
		this.player.play(object);
	}
	
	//TODO: make real-time tempo change and continuing play
	public void run() {
		while(this.isRunning) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			if (this.startingTime != 0 && System.currentTimeMillis() >= this.startingTime) {
				if (this.score != null) {
					this.resetPlayer();
					this.player.play(this.score);
				}
			}
		}
	}
	
	public void resetPlayer() {
		this.stopPlaying();
		this.player = new JSynPlayer();
		this.player.setWaveform(this.waveform);
	}
	
	public void stopPlaying() {
		this.player.stopPlaying();
		this.startingTime = 0;
	}
	
	public void setTempo(int bpm) {
		this.stopPlaying();
		this.bpm = bpm;
	}
	
	public void setWaveform(String waveform) {
		this.stopPlaying();
		this.waveform = waveform;
		this.player.setWaveform(waveform);
	}

}

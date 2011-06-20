package org.rubato.rubettes.bigbang.model.player;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;

public class BigBangPlayer extends Thread {
	
	private final int DELAY = 300;
	public static final int MIN_BPM = 20;
	public static final int MAX_BPM = 500;
	public static final int INITIAL_BPM = 100;
	
	
	private boolean isRunning;
	private long startingTime;
	private JSynPlayer player;
	
	private PowerDenotator composition;
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
	
	public synchronized void playCompositionImmediately(PowerDenotator composition) {
		this.playComposition(composition, System.currentTimeMillis());
	}
	
	public synchronized void playComposition(PowerDenotator composition) {
		this.playComposition(composition, System.currentTimeMillis() + this.DELAY);
	}
	
	private synchronized void playComposition(PowerDenotator composition, long startingTime) {
		this.startingTime = startingTime;
		this.composition = composition;
	}
	
	public synchronized void playSingleNote(Denotator node) {
		DenotatorValueExtractor extractor = new DenotatorValueExtractor();
		this.player.play(extractor.extractValues(node, bpm));
	}
	
	public void run() {
		while(this.isRunning) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			if (this.startingTime != 0 && System.currentTimeMillis() >= this.startingTime) {
				if (this.composition != null) {
					this.resetPlayer();
					this.player.play(new JSynScore(this.composition, this.bpm));
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
	}

}

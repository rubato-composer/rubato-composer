package org.rubato.rubettes.bigbang.model.player;


import java.util.List;

import com.softsynth.jsyn.*;
import com.softsynth.jsyn.view102.Tweakable;

/** Demonstrate how event buffering and absolute sleep solve the timing problems.
 */
public class JSynPlayer implements Tweakable {
	
	public static final int BASE_A4 = 440; // A4 tuning in Hz
	public static final int SAMPLE_RATE = 44100;
	public static final int TICKS_PER_SECOND = 689;
	public static final int PLAYBACK_DELAY = 100; //in miliseconds
	
	public static final String[] WAVEFORMS = {"Sine", "Square", "Sawtooth", "Triangle"};
	
	private JSynThreadGroup threads;
	private String waveform;
	
	int                 advanceTime; // ticks
	final static double DEFAULT_ADVANCE = 0.02; // seconds\
	
	public JSynPlayer() {
		this.threads = new JSynThreadGroup();
	}

	/*
	 * Setup synthesis by overriding start() method.
	 */
	public void play(JSynScore score) {
		try {
			if (!this.isPlaying()) {
				Synth.startEngine(0, JSynPlayer.SAMPLE_RATE);
			} else {
				this.threads.stop();
			}
			
			this.initThreads(score);
			//System.out.println("threads " + this.threads.size());
			
			this.threads.start();
			
	   } catch(SynthException e) {
		  e.printStackTrace();
	   }
	}
	
	/*
	 * Setup synthesis by overriding start() method.
	 */
	public void play(JSynNote note) {
		try {
			if (!this.isPlaying()) {
				Synth.startEngine(0, JSynPlayer.SAMPLE_RATE);
			}
			
			note.setOnset(Synth.getTickCount() + JSynPlayer.PLAYBACK_DELAY);
			JSynThread liveThread = new JSynThread(this, note);
			
			this.threads.add(liveThread);
			liveThread.start();
			
	   } catch(SynthException e) {
		  e.printStackTrace();
	   }
	}
	
	public boolean isPlaying() {
		return this.threads.isRunning();
	}
	
	private void initThreads(JSynScore score) {
		List<JSynNote> notes = score.getNotes();
		if (notes.size() > 0) {
			this.threads = new JSynThreadGroup();
			int startTickCount = Synth.getTickCount() + JSynPlayer.PLAYBACK_DELAY;
			int firstOnset = notes.get(0).getOnset();
			for (JSynNote currentNote : notes) {
				//correct onset and add note to a thread
				currentNote.setOnset(currentNote.getOnset() - firstOnset + startTickCount);
				this.addNoteToConvenientThread(currentNote);
			}
		}
	}
	
	private void addNoteToConvenientThread(JSynNote note) {
		double symbolicStart = note.getSymbolicStart();
		double symbolicEnd = note.getSymbolicEnd();
		double voice = note.getVoice();
		boolean convenientThreadFound = false;
		for (JSynThread currentThread : this.threads) {
			if (currentThread.getVoice() == voice) {
				if (!currentThread.playsAt(symbolicStart, symbolicEnd)) {
					currentThread.addNote(note);
					convenientThreadFound = true;
					return;
				}
			}
		}
		if (!convenientThreadFound) {
			this.threads.add(new JSynThread(this, note));
		}
	}
	
/* called by LabelledFaders */
	public void tweak( int targetIndex, double val )
	{		
		switch( targetIndex )
		{
		case 1:
			advanceTime = (int) (Synth.getTickRate() * val);
			break;
		}
	}

  /*
   * Clean up synthesis by overriding stop() method.
   */
	public void stopPlaying() {
		if (this.isPlaying()) {
			// Set flag so that run() loop will exit the next time around.
			this.threads.stop(); // tell run() to exit peacefully
			//System.out.println("Stopping engine " + this)
			Synth.stopEngine();
			//System.out.println("Stopped " + this);
		}
	}
	
	public void setWaveform(String waveform) {
		this.waveform = waveform;
	}
	
	public SynthOscillator getSpecificOscillator() {
		if (this.waveform.equals(JSynPlayer.WAVEFORMS[0])) {
			return new SineOscillator();
		} else if (this.waveform.equals(JSynPlayer.WAVEFORMS[1])) {
			return new SquareOscillator();
		}  else if (this.waveform.equals(JSynPlayer.WAVEFORMS[2])) {
			return new SawtoothOscillator();
		}  else {
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
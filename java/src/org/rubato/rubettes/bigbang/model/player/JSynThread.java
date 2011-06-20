package org.rubato.rubettes.bigbang.model.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.softsynth.jsyn.AddUnit;
import com.softsynth.jsyn.EnvelopePlayer;
import com.softsynth.jsyn.LineOut;
import com.softsynth.jsyn.Synth;
import com.softsynth.jsyn.SynthEnvelope;
import com.softsynth.jsyn.SynthException;
import com.softsynth.jsyn.SynthOscillator;
import com.softsynth.jsyn.SynthUnit;

class JSynThread extends Thread {
	
	private JSynPlayer player;
	private JSynThreadGroup group;
	private int voice;
	
	private List<JSynNote> notes = new ArrayList<JSynNote>();
	
	private EnvelopePlayer     envPlayer;
	private SynthOscillator 	carrier;
	private List<SynthUnit> modulatorUnits;
	private LineOut            lineOut;
	
	public JSynThread(JSynPlayer player, JSynNote note) {
		this(player, note.getVoice());
		this.addNote(note);
		this.modulatorUnits = new ArrayList<SynthUnit>();
	}
	
	public JSynThread(JSynPlayer player, int voice) {
		
		this.player = player;
		this.voice = voice;
		
		// Create unit generators.
	 	this.carrier = this.player.getSpecificOscillator();
	 	this.envPlayer = new EnvelopePlayer();  // create an envelope player
	 	this.lineOut = new LineOut();

	 	// control oscillator amplitude with envelope
	 	this.envPlayer.output.connect( this.carrier.amplitude );
		
	 	

	 	this.carrier.output.connect( 0, lineOut.input, 0 );
	 	this.carrier.output.connect( 0, lineOut.input, 1 );
	}
	
	public void setGroup(JSynThreadGroup group) {
		this.group = group;
	}
	
	public void addNote(JSynNote note) {
		this.notes.add(note);
	}
	
	public int getVoice() {
		return this.voice;
	}
	
	public boolean playsAt(double symbolicStart, double symbolicEnd) {
		for (JSynNote currentNote : this.notes) {
			//due to ordering every following note is later
			if (currentNote.playsAt(symbolicStart, symbolicEnd)) {
				return true;
			}
		}
		return false;
	}
	
	private void bang(JSynNote note) {
		this.carrier.frequency.set(note.getFrequency());
		this.clearModulators();
		this.generateModulators(note, this.carrier);
		// set envelopes amplitude which in turn controls oscillator amplitude
		envPlayer.amplitude.set(note.getAmplitude()*this.player.getRecommendedAmplitude());
		envPlayer.envelopePort.clear(note.getOnset()); // clear the queue
		
		// define shape of envelope as an array of doubles
	 	double[] data =
	 	{
	 		0.005, 1.0, // Attack
	 		0.030, 0.8,  // Decay
	 		note.getDuration()-0.035, 0.7, // Sustain
	 		0.1, 0.0 // Release
	 	};
	 	SynthEnvelope envelope = new SynthEnvelope(data);
		
		envPlayer.envelopePort.queue(note.getOnset(), envelope);  // queue the envelope
		//to prevent table 'too many tokens' error
		envelope.delete();
	}
	
	private void generateModulators(JSynNote note, SynthOscillator carrier) {
		AddUnit modulatorSum = new AddUnit();
		modulatorSum.inputA.set(carrier.frequency.get());
		modulatorSum.output.connect(carrier.frequency);
		modulatorSum.start();
		this.modulatorUnits.add(modulatorSum);
		for (JSynModulator currentModulator: note.getModulators()) {
			SynthOscillator modulator = this.player.getSpecificOscillator();
			modulator.frequency.set(currentModulator.getFrequency());
			modulator.amplitude.set(currentModulator.getAmplitude()*20);
			//TODO:runs all the time. later respect onset!!!
			modulator.start();
			this.modulatorUnits.add(modulator);
			this.generateModulators(currentModulator, modulator);
			
			AddUnit newSum = new AddUnit();
			modulator.output.connect(newSum.inputA);
			newSum.output.connect(modulatorSum.inputB);
			newSum.start();
			this.modulatorUnits.add(newSum);
			modulatorSum = newSum;
		}
	}
	
	private void clearModulators() {
		for (SynthUnit currentModulator: this.modulatorUnits) {
			currentModulator.stop();
			currentModulator.delete();
		}
		this.modulatorUnits = new ArrayList<SynthUnit>();
	}

	public void playNotes() {
		player.advanceTime = (int) (Synth.getTickRate() * JSynPlayer.DEFAULT_ADVANCE);
		
		Iterator<JSynNote> noteIterator = this.notes.iterator();
		JSynNote currentNote = noteIterator.next();
		int nextTime = currentNote.getOnset();
		
		// try to start in sync
		Synth.sleepUntilTick(nextTime  - player.advanceTime);
		
		while(this.group.isRunning()) {
			
			/* Play a note at the specified time. */
			this.bang(currentNote);
			
			if (noteIterator.hasNext()) {
				currentNote = noteIterator.next();
				/* Advance nextTime by fixed amount. */
				nextTime = currentNote.getOnset();
				/* sleep until advanceTime BEFORE we have to play the next note */
				Synth.sleepUntilTick( nextTime  - player.advanceTime);
			} else {
				Synth.sleepUntilTick( nextTime + (int)Math.ceil(currentNote.getDuration()*JSynPlayer.TICKS_PER_SECOND) + 500);
				break;
			}
		}
	}
	
	/* real-time task for thread */
	public void run() {
		try {
			this.carrier.start();
		 	this.envPlayer.start();
		 	this.lineOut.start();
		 	
			playNotes();
			
			this.clearModulators();
			this.carrier.stop();
			envPlayer.stop();
			lineOut.stop();
		 	this.carrier.delete();
		 	envPlayer.delete();
		 	lineOut.delete();
		} catch( SynthException e ) {
			// Just print message. Do not use Alert because we can easily get an exception
			// when we stop the thread.
			//System.err.println("run() caught " + e );
		}
	}
	
	public String toString() {
		String representation = "[";
		for (JSynNote currentNote: this.notes) {
			representation += currentNote.toString() + ", ";
		}
		representation = representation.substring(0, representation.length()-2);
		representation += "]";
		return representation;
	}
	
}
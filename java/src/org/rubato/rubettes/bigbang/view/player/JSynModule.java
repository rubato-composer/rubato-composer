package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.softsynth.shared.time.TimeStamp;

public class JSynModule {
	
	private final double RAMP_DURATION = .02;
	
	private JSynPlayer player;
	private UnitOscillator carrier;
	private LinearRamp oscSweeper;
	private VariableRateMonoReader envPlayer;
	private List<UnitGenerator> modulatorUnits;
	private LineOut lineOut;
	private boolean isRunning;
	
	public JSynModule(JSynPlayer player) {
		this.player = player;
		
		// Create unit generators.
	 	this.player.getSynthesizer().add(this.carrier = player.getSpecificOscillator());
	 	this.modulatorUnits = new ArrayList<UnitGenerator>();
	 	this.player.getSynthesizer().add(this.envPlayer = new VariableRateMonoReader());
	 	this.player.getSynthesizer().add(this.oscSweeper = new LinearRamp());
	 	this.player.getSynthesizer().add(this.lineOut = new LineOut());
	 	
	 	// control carrier frequency with a ramp 
	 	this.oscSweeper.output.connect(this.carrier.frequency);
	 	this.oscSweeper.time.set(this.RAMP_DURATION);
	 	
	 	// control carrier amplitude with an envelope
	 	this.envPlayer.output.connect( this.carrier.amplitude );

	 	this.carrier.output.connect( 0, this.lineOut.input, 0 );
	 	this.carrier.output.connect( 0, this.lineOut.input, 1 );
	 	
	 	this.start();
	}
	
	public void playNote(JSynObject note) {
		this.updateCarrierAndModulators(note);
		this.queueEnvelope(note, 0.005, 0.030, note.getDuration());
	}
	
	public void modifyNote(JSynObject note, double remainingDuration) {
		this.updateCarrierAndModulators(note);
		//this.queueEnvelopeWithoutAttackAndDecay(note, remainingDuration);
	}
	
	private void updateCarrierAndModulators(JSynObject note) {
		//double currentFrequency = this.carrier.frequency.get();
		//System.out.println(this.carrier +" " +currentFrequency + " " + frequency);
		//this.oscSweeper.current.set(currentFrequency);
		//this.oscSweeper.input.set(currentFrequency);
		this.oscSweeper.input.set(note.getFrequency());
		//this.carrier.frequency.set(frequency);
		this.clearModulators();
		this.generateModulators(note);
	}
	
	private void queueEnvelopeWithoutAttackAndDecay(JSynObject note, double duration) {
		double[] envelopeData = {
			duration-0.035, 0.7, // Sustain
			0.1, 0.0 // Release
		};
		this.queueEnvelope(note, envelopeData);
	}
	
	private void queueEnvelope(JSynObject note, double attack, double decay, double duration) {
		double[] envelopeData = {
		 	attack, 1.0, // Attack
		 	decay, 0.8,  // Decay
		 	duration-0.035, 0.7, // Sustain
		 	0.1, 0.0 // Release
		};
		this.queueEnvelope(note, envelopeData);
	}
	
	private void queueEnvelope(JSynObject note, double[] envelopeData) {
		// set envelopes amplitude which in turn controls oscillator amplitude
		this.envPlayer.amplitude.set(note.getAmplitude()*this.player.getRecommendedAmplitude());
		this.envPlayer.dataQueue.clear(); // clear the queue
		//System.out.println(duration);
		
	 	SegmentedEnvelope envelope = new SegmentedEnvelope(envelopeData);
		
	 	this.envPlayer.dataQueue.queue(envelope, 0, envelope.getNumFrames(), new TimeStamp(note.getOnset()));  // queue the envelope
	 	this.envPlayer.start();
		//to prevent table 'too many tokens' error
		//envelope.delete(); TODO: not happen anymore in new version??
	}
	
	public void generateModulators(JSynObject note) {
		this.generateModulators(note, this.carrier);
	}
	
	//recursive method!
	private void generateModulators(JSynObject note, UnitOscillator carrier) {
		/*Add modulatorSum = new Add();
		this.player.getSynthesizer().add(modulatorSum);
		modulatorSum.inputA.set(carrier.frequency.get());
		modulatorSum.output.connect(carrier.frequency);
		modulatorSum.start();
		this.modulatorUnits.add(modulatorSum);
		for (JSynModulator currentModulator: note.getModulators()) {
			UnitOscillator modulator = this.player.getSpecificOscillator();
			modulator.frequency.set(currentModulator.getFrequency());
			modulator.amplitude.set(currentModulator.getAmplitude()*20);
			//TODO:runs all the time. later respect onset!!!
			modulator.start();
			this.modulatorUnits.add(modulator);
			this.generateModulators(currentModulator, modulator);
			
			Add newSum = new Add();
			this.player.getSynthesizer().add(newSum);
			modulator.output.connect(newSum.inputA);
			newSum.output.connect(modulatorSum.inputB);
			newSum.start();
			this.modulatorUnits.add(newSum);
			modulatorSum = newSum;
		}*/
	}
	
	public void clearModulators() {
		for (UnitGenerator currentModulator: this.modulatorUnits) {
			currentModulator.stop();
			//currentModulator.delete();
		}
		this.modulatorUnits = new ArrayList<UnitGenerator>();
	}
	
	private void start() {
		this.carrier.start();
	 	this.envPlayer.start();
	 	this.lineOut.start();
	 	this.isRunning = true;
	}
	
	@Override
	protected void finalize() {
		System.out.println("finalize!!");
		this.clearModulators();
		
		this.carrier.stop();
		this.envPlayer.stop();
		this.lineOut.stop();
		
	 	//this.carrier..delete();
	 	//this.envPlayer.delete();
	 	//this.lineOut.delete();
	 	this.isRunning = false;
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}

}

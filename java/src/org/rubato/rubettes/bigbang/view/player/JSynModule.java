package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.softsynth.shared.time.TimeStamp;

public class JSynModule {
	
	private final double RAMP_DURATION = .02;
	private final double ATTACK = 0.002;
	private final double DECAY = 0.03;
	private final double RELEASE = 0.1;
	
	private JSynPlayer player;
	private UnitOscillator carrier;
	private LinearRamp carFreqSweeper;
	private LinearRamp envAmpSweeper;
	private VariableRateMonoReader envPlayer;
	private List<UnitGenerator> modulatorUnits;
	private LineOut lineOut;
	
	
	public JSynModule(JSynPlayer player) {
		this.player = player;
		
		// Create unit generators.
	 	this.player.addToSynth(this.carrier = player.getSpecificOscillator());
	 	this.modulatorUnits = new ArrayList<UnitGenerator>();
	 	this.player.addToSynth(this.envPlayer = new VariableRateMonoReader());
	 	this.player.addToSynth(this.carFreqSweeper = new LinearRamp());
	 	this.player.addToSynth(this.envAmpSweeper = new LinearRamp());
	 	this.player.addToSynth(this.lineOut = new LineOut());
	 	
	 	// control carrier frequency with a ramp 
	 	this.carFreqSweeper.output.connect(this.carrier.frequency);
	 	this.carFreqSweeper.time.set(this.RAMP_DURATION);
	 	
	 	// control carrier amplitude with an envelope
	 	this.envPlayer.output.connect(this.carrier.amplitude);
	 	
	 	// control envelope amplitude with a ramp 
	 	this.envAmpSweeper.output.connect(this.envPlayer.amplitude);
	 	this.envAmpSweeper.time.set(this.RAMP_DURATION);

	 	this.carrier.output.connect( 0, this.lineOut.input, 0 );
	 	this.carrier.output.connect( 0, this.lineOut.input, 1 );
	 	
	 	this.start();
	}
	
	public double getCarrierFrequency() {
		return this.carFreqSweeper.input.get();
	}
	
	public void playOrAdjustObject(JSynObject object) {
		this.updateCarrierAndModulators(object);
		double currentSymbolicTime = this.player.getCurrentSymbolicTime();
		if (object.getOnset() > currentSymbolicTime) {
			double duration = this.player.convertToSynthDuration(object.getDuration());
			this.queueEnvelope(duration, this.player.convertToSynthOnset(object.getOnset()));
		} else {
			double remainingDuration = this.player.convertToSynthDuration(object.getDuration()-(currentSymbolicTime-object.getOnset()));
			if (remainingDuration > 0) {
				this.queueEnvelopeWithoutAttackAndDecay(remainingDuration, this.player.getCurrentSynthTime());
			} else {
				this.mute();
			}
		}
	}
	
	public void mute() {
		this.envAmpSweeper.input.set(0);
	}
	
	private void updateCarrierAndModulators(JSynObject note) {
		this.clearModulators();
		this.generateModulators(note);
		this.carFreqSweeper.input.set(note.getFrequency());
		this.envAmpSweeper.input.set(note.getAmplitude()*this.player.getRecommendedAmplitude());
	}
	
	private void queueEnvelopeWithoutAttackAndDecay(double duration, double onset) {
		double[] envelopeData = {
			this.ATTACK, 0.7,
			duration-this.ATTACK, 0.7, // Sustain
			this.RELEASE, 0.0 // Release
		};
		this.queueEnvelope(envelopeData, onset);
	}
	
	private void queueEnvelope(double duration, double onset) {
		double[] envelopeData = {
		 	this.ATTACK, 1.0, // Attack
		 	this.DECAY, 0.7,  // Decay
		 	duration-(this.ATTACK+this.DECAY), 0.7, // Sustain
		 	this.RELEASE, 0.0 // Release
		};
		this.queueEnvelope(envelopeData, onset);
	}
	
	private void queueEnvelope(double[] envelopeData, double onset) {
		// set envelopes amplitude which in turn controls oscillator amplitude
		this.envPlayer.dataQueue.clear(); // clear the queue
		
	 	SegmentedEnvelope envelope = new SegmentedEnvelope(envelopeData);
		
	 	this.envPlayer.dataQueue.queue(envelope, 0, envelope.getNumFrames(), new TimeStamp(onset));  // queue the envelope
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
	}
	
	@Override
	protected void finalize() {
		this.envAmpSweeper.input.set(0);
		/*try {
			Thread.sleep((long)this.RAMP_DURATION);
		} catch (InterruptedException e) { e.printStackTrace(); }*/
		this.clearModulators();
		this.carrier.stop();
		this.envPlayer.stop();
		this.lineOut.stop();
	}

}

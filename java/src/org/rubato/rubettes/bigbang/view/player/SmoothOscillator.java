package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.softsynth.shared.time.TimeStamp;

public class SmoothOscillator {
	
	public static final double RAMP_DURATION = .007;
	private final double ATTACK = 0.002;
	private final double DECAY = 0.03;
	private final double RELEASE = 0.1;
	
	private JSynPlayer player;
	private UnitOscillator oscillator;
	private LinearRamp frequencySweeper;
	private LinearRamp amplitudeSweeper;
	private VariableRateMonoReader envelopePlayer;
	private List<Add> addUnits;
	List<SmoothOscillator> modulators;
	
	public SmoothOscillator(JSynPlayer player) {
		this.player = player;
		this.player.addToSynth(this.oscillator = player.getSpecificOscillator());
		this.player.addToSynth(this.frequencySweeper = new LinearRamp());
		this.player.addToSynth(this.amplitudeSweeper = new LinearRamp());
		this.player.addToSynth(this.envelopePlayer = new VariableRateMonoReader());
		
		this.addUnits = new ArrayList<Add>();
		this.addUnits.add(new Add());
		this.player.addToSynth(this.getCurrentAddUnit());
		this.getCurrentAddUnit().output.connect(this.oscillator.frequency);
		
		//control frequency with a ramp 
	 	this.frequencySweeper.output.connect(this.getCurrentAddUnit().inputA);
	 	this.frequencySweeper.time.set(RAMP_DURATION);
	 	
	 	//control carrier amplitude with an envelope
	 	this.envelopePlayer.output.connect(this.oscillator.amplitude);
	 	
	 	//control envelope amplitude with a ramp 
	 	this.amplitudeSweeper.output.connect(this.envelopePlayer.amplitude);
	 	this.amplitudeSweeper.time.set(RAMP_DURATION);
	 	
	 	this.modulators = new ArrayList<SmoothOscillator>();
	}
	
	private Add getCurrentAddUnit() {
		if (this.addUnits.size() > 0) {
			return this.addUnits.get(this.addUnits.size()-1);
		}
		return null;
	}
	
	public void addModulator() {
		SmoothOscillator modulator = new SmoothOscillator(this.player);
		Add newAddUnit = new Add();
		this.player.addToSynth(newAddUnit);
		newAddUnit.output.connect(this.getCurrentAddUnit().inputB);
		modulator.getOutput().connect(newAddUnit.inputA);
		this.modulators.add(modulator);
		this.addUnits.add(newAddUnit);
	}
	
	public List<SmoothOscillator> getModulators() {
		return this.modulators;
	}
	
	public void removeLastModulator() {
		SmoothOscillator lastModulator = this.modulators.remove(this.modulators.size()-1);
		lastModulator.removeFromSynthAndStop();
		Add lastAddUnit = this.getCurrentAddUnit();
		//necessary to disconnect. removing from synth apparently does not work
		lastAddUnit.output.disconnectAll();
		this.addUnits.remove(lastAddUnit);
		this.player.removeFromSynthAndStop(lastAddUnit);
		//System.out.println(lastAddUnit.inputB.disconnectAll());
	}
	
	public void setFrequency(double frequency) {
		this.frequencySweeper.input.set(frequency);
	}
	
	public double getFrequency() {
		return this.frequencySweeper.input.get();
	}
	
	public void setAmplitude(double amplitude) {
		this.amplitudeSweeper.input.set(amplitude);
	}
	
	public double getAmplitude() {
		return this.amplitudeSweeper.input.get();
	}
	
	public UnitOutputPort getOutput() {
		return this.oscillator.output;
	}
	
	public void queueEnvelopeWithoutAttackAndDecay(double duration, double onset) {
		//System.out.println("WO "+ onset + " " + duration);
		double[] envelopeData = {
			this.ATTACK, 0.7,
			duration-this.ATTACK, 0.7, // Sustain
			this.RELEASE, 0.0 // Release
		};
		this.queueEnvelope(envelopeData, onset, true);
	}
	
	public void queueEnvelope(double duration, double onset, boolean override) {
		//System.out.println("AD "+ onset + " " + duration);
		double[] envelopeData = {
		 	this.ATTACK, 1.0, // Attack
		 	this.DECAY, 0.7,  // Decay
		 	duration-(this.ATTACK+this.DECAY), 0.7, // Sustain
		 	this.RELEASE, 0.0 // Release
		};
		this.queueEnvelope(envelopeData, onset, override);
	}
	
	private void queueEnvelope(double[] envelopeData, double onset, boolean override) {
		if (override) {
			this.envelopePlayer.dataQueue.clear();
		}
	 	SegmentedEnvelope envelope = new SegmentedEnvelope(envelopeData);
	 	this.envelopePlayer.dataQueue.queue(envelope, 0, envelope.getNumFrames(), new TimeStamp(onset));
	 	this.envelopePlayer.start();
	}
	
	public void mute() {
		this.amplitudeSweeper.input.set(0);
		this.envelopePlayer.dataQueue.clear();
	}
	
	public void removeFromSynthAndStop() {
		this.player.removeFromSynthAndStop(this.oscillator);
		this.player.removeFromSynthAndStop(this.frequencySweeper);
		this.player.removeFromSynthAndStop(this.amplitudeSweeper);
		this.player.removeFromSynthAndStop(this.envelopePlayer);
		for (SmoothOscillator currentOscillator : this.modulators) {
			currentOscillator.removeFromSynthAndStop();
		}
		for (Add currentAdd : this.addUnits) {
			this.player.removeFromSynthAndStop(currentAdd);
		}
	}

}

package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.Multiply;
import com.jsyn.unitgen.UnitBinaryOperator;
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
	private List<SmoothOscillatorModule> modules;
	private UnitOutputPort lastOutput;
	private UnitInputPort lastInput;
	List<SmoothOscillator> modulators;
	
	public SmoothOscillator(JSynPlayer player) {
		this.player = player;
		this.player.addToSynth(this.oscillator = player.getSpecificOscillator());
		this.player.addToSynth(this.frequencySweeper = new LinearRamp());
		this.player.addToSynth(this.amplitudeSweeper = new LinearRamp());
		this.player.addToSynth(this.envelopePlayer = new VariableRateMonoReader());
		
		//control frequency with a ramp
	 	this.connect(this.frequencySweeper.output, this.oscillator.frequency);
	 	this.frequencySweeper.time.set(RAMP_DURATION);
	 	
	 	//control carrier amplitude with an envelope
	 	this.envelopePlayer.output.connect(this.oscillator.amplitude);
	 	
	 	//control envelope amplitude with a ramp 
	 	this.amplitudeSweeper.output.connect(this.envelopePlayer.amplitude);
	 	this.amplitudeSweeper.time.set(RAMP_DURATION);
	 	
	 	this.modulators = new ArrayList<SmoothOscillator>();
	 	this.modules = new ArrayList<SmoothOscillatorModule>();
	}
	
	private void connect(UnitOutputPort output, UnitInputPort input) {
		//init place where modulators are inserted
		this.lastOutput = output;
		this.lastInput = input;
		output.connect(input);
	}
	
	private void insertModule(UnitBinaryOperator newOperatorUnit, UnitOutputPort newOutput) {
		SmoothOscillatorModule newModule = new SmoothOscillatorModule(newOperatorUnit, newOutput);
		if (this.modules.size() > 0) {
			this.player.addToSynth(newOperatorUnit);
			newModule.insertBetween(this.getLastModule());
		} else {
			//no modules yet, channel frequency sweeper through input A
			newModule.insertBetween(this.frequencySweeper.output, this.oscillator.frequency);
		}
		this.modules.add(newModule);
	}
	
	private void removeLast() {
		
	}
	
	private SmoothOscillatorModule getLastModule() {
		if (this.modules.size() > 0) {
			return this.modules.get(this.modules.size()-1);
		}
		return null;
	}
	
	public void addModulator(int modulatorType) {
		SmoothOscillator modulator = new SmoothOscillator(this.player);
		UnitBinaryOperator newOperatorUnit = null; 
		if (modulatorType == JSynObject.FREQUENCY_MODULATION) {
			newOperatorUnit = new Add();
		} else {//if (modulatorType == JSynObject.RING_MODULATION) {
			newOperatorUnit = new Multiply();
		}
		if (newOperatorUnit != null) {
			this.insertModule(newOperatorUnit, modulator.getOutput());
		} else {
			//add dummy operator so that adding/removing does not get messed up. used in additive synthesis
			//TODO make parallel group for additive synthesis (units added to signal not frequency!!!)
		}
		this.modulators.add(modulator);
	}
	
	public void setModulatorType(int index, int modulatorType) {
		System.out.println(this.modules + " " + this.modulators);
		this.modules.get(index).setType(modulatorType);
	}
	
	public List<SmoothOscillator> getModulators() {
		return this.modulators;
	}
	
	public void removeLastModulator() {
		if (this.modulators.size() > 0) {
			SmoothOscillator lastModulator = this.modulators.remove(this.modulators.size()-1);
			lastModulator.removeFromSynthAndStop();
			SmoothOscillatorModule lastModule = this.getLastModule();
			//necessary to disconnect. removing from synth apparently does not work
			lastModule.remove();
			this.modules.remove(lastModule);
			//System.out.println(lastModule + " " + lastModule.getOperatorUnit());
			//this.player.removeFromSynthAndStop(lastModule.getOperatorUnit());
			//System.out.println(lastAddUnit.inputB.disconnectAll());
		}
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
		for (SmoothOscillatorModule currentModule : this.modules) {
			this.player.removeFromSynthAndStop(currentModule.getOperatorUnit());
		}
	}

}

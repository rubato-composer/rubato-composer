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
	private SmoothOscillatorModule lastSerialFrequencyModule;
	private SmoothOscillatorModule firstParallelModule;
	private SmoothOscillatorModule lastParallelModule;
	private List<SmoothOscillator> modulators;
	private UnitInputPort outputTo;
	
	public SmoothOscillator(JSynPlayer player, UnitInputPort outputTo) {
		this.player = player;
		this.player.addToSynth(this.oscillator = player.getSpecificOscillator());
		
		//control frequency with a ramp
		this.player.addToSynth(this.frequencySweeper = new LinearRamp());
	 	this.frequencySweeper.output.connect(this.oscillator.frequency);
	 	this.frequencySweeper.time.set(RAMP_DURATION);
	 	
	 	//control carrier amplitude with an envelope
	 	this.player.addToSynth(this.envelopePlayer = new VariableRateMonoReader());
	 	this.envelopePlayer.output.connect(this.oscillator.amplitude);
	 	
	 	//control envelope amplitude with a ramp
	 	this.player.addToSynth(this.amplitudeSweeper = new LinearRamp());
	 	this.amplitudeSweeper.output.connect(this.envelopePlayer.amplitude);
	 	this.amplitudeSweeper.time.set(RAMP_DURATION);
	 	
	 	this.setOutputTo(outputTo);
	 	
	 	this.modulators = new ArrayList<SmoothOscillator>();
	 	this.modules = new ArrayList<SmoothOscillatorModule>();
	}
	
	public void setOutputTo(UnitInputPort outputTo) {
		if (outputTo != null) {
			this.oscillator.output.connect(outputTo);
		 	this.outputTo = outputTo;
		}
	}
	
	private void insertSerialFrequencyModule(UnitBinaryOperator newOperatorUnit, UnitOutputPort newOutput) {
		SmoothOscillatorModule newModule = new SmoothOscillatorModule(newOperatorUnit, newOutput);
		if (this.lastSerialFrequencyModule != null) {
			this.player.addToSynth(newOperatorUnit);
			newModule.insertBetween(this.lastSerialFrequencyModule);
		} else {
			//no modules yet, channel frequency sweeper through input A
			newModule.insertBetween(this.frequencySweeper.output, this.oscillator.frequency);
		}
		this.modules.add(newModule);
		this.lastSerialFrequencyModule = newModule;
	}
	
	private void insertParallelModule(UnitBinaryOperator newOperatorUnit, UnitOutputPort newOutput) {
		SmoothOscillatorModule newModule = new SmoothOscillatorModule(newOperatorUnit, newOutput);
		if (this.lastParallelModule != null) {
			this.player.addToSynth(newOperatorUnit);
			newModule.insertBetween(this.lastParallelModule);
		} else {
			//no modules yet, add operator after oscillator output
			newModule.insertBetween(this.oscillator.output, this.outputTo);
			this.firstParallelModule = newModule;
		}
		this.modules.add(newModule);
		this.lastParallelModule = newModule;
	}
	
	public void addModulator(int modulatorType) {
		SmoothOscillator modulator = new SmoothOscillator(this.player, null);
		if (modulatorType == JSynObject.FREQUENCY_MODULATION) {
			this.insertSerialFrequencyModule(new Add(), modulator.getOutput());
		} else {
			UnitBinaryOperator newOperatorUnit;
			if (modulatorType == JSynObject.RING_MODULATION) {
				newOperatorUnit = new Multiply();
			} else {
				newOperatorUnit = new Add();
			}
			this.insertSerialFrequencyModule(newOperatorUnit, modulator.getOutput());
		}
		this.modulators.add(modulator);
	}
	
	public void setModulatorType(int index, int modulatorType) {
		int moduleType = this.modules.get(index).getType();
		//System.out.println(this.modules + " " + this.modulators);
		if (modulatorType == JSynObject.FREQUENCY_MODULATION &&
				(moduleType == JSynObject.RING_MODULATION || moduleType == JSynObject.ADDITIVE)) {
			
		} else {
			
		}
		this.modules.get(index).setType(modulatorType);
	}
	
	public List<SmoothOscillator> getModulators() {
		return this.modulators;
	}
	
	public void removeLastModulator() {
		if (this.modulators.size() > 0) {
			SmoothOscillator lastModulator = this.modulators.remove(this.modulators.size()-1);
			lastModulator.removeFromSynthAndStop();
			SmoothOscillatorModule lastModule = this.modules.get(this.modules.size()-1);
			//necessary to disconnect. removing from synth apparently does not work
			lastModule.remove();
			this.modules.remove(lastModule);
			if (this.modules.size() > 0) {
				this.lastSerialFrequencyModule = this.modules.get(this.modules.size()-1);
			} else {
				this.lastSerialFrequencyModule = null;
			}
			//System.out.println(lastModule + " " + lastModule.getOperatorUnit());
			//this.player.removeFromSynthAndStop(lastModule.getOperatorUnit());
			//System.out.println(lastAddUnit.inputB.disconnectAll());
		}
	}
	
	public void setFrequency(double frequency) {
		this.frequencySweeper.input.set(frequency);
	}
	
	public double getFrequency() {
		return this.oscillator.frequency.getValue();//this.frequencySweeper.input.get();
	}
	
	public void setAmplitude(double amplitude) {
		this.amplitudeSweeper.input.set(amplitude);
	}
	
	public double getAmplitude() {
		return this.amplitudeSweeper.input.get();
	}
	
	public UnitOutputPort getOutput() {
		if (this.firstParallelModule != null) {
			return this.firstParallelModule.getOperatorUnit().output;
		}
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
	
	public String toString() {
		return this.getFrequency() + " " + this.getAmplitude() + " ";
	}

}

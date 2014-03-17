package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.softsynth.shared.time.TimeStamp;

public class SmoothOscillator {
	
	public static final double RAMP_DURATION = .007;
	private final double ATTACK = 0.002;
	private final double DECAY = 0.03;
	private final double RELEASE = 0.1;
	
	private BigBangPlayer player;
	private UnitOscillator oscillator;
	private LinearRamp frequencySweeper;
	private LinearRamp amplitudeSweeper;
	private VariableRateMonoReader envelopePlayer;
	//satelliteModules contains all modules in the other two lists in order of addition
	private List<SmoothOscillatorModule> satelliteModules;
	private List<SmoothOscillatorModule> serialFrequencyModules; //frequency modulators
	private List<SmoothOscillatorModule> parallelModules; //additive oscillators and ring modulators
	private UnitInputPort outputTo;
	
	public SmoothOscillator(BigBangPlayer player, UnitInputPort outputTo) {
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
	 	
	 	this.satelliteModules = new ArrayList<SmoothOscillatorModule>();
	 	this.serialFrequencyModules = new ArrayList<SmoothOscillatorModule>();
	 	this.parallelModules = new ArrayList<SmoothOscillatorModule>();
	}
	
	public void setOutputTo(UnitInputPort outputTo) {
		if (outputTo != null) {
			this.oscillator.output.connect(outputTo);
		 	this.outputTo = outputTo;
		}
	}
	
	public void addSatellite(int satelliteType) {
		SmoothOscillatorModule newModule = new SmoothOscillatorModule(this.player, satelliteType);
		if (satelliteType == JSynObject.FREQUENCY_MODULATION) {
			this.insertSerialFrequencyModule(newModule);
		} else {
			this.insertParallelModule(newModule);
		}
		this.satelliteModules.add(newModule);
		//System.out.println(newModule);
	}
	
	public void setSatelliteType(int index, int satelliteType) {
		SmoothOscillatorModule moduleAtIndex = this.satelliteModules.get(index);
		int moduleType = moduleAtIndex.getType();
		if (satelliteType == JSynObject.FREQUENCY_MODULATION
				&& (moduleType == JSynObject.RING_MODULATION || moduleType == JSynObject.ADDITIVE)) {
			moduleAtIndex.disconnect();
			this.parallelModules.remove(moduleAtIndex);
			moduleAtIndex.setType(satelliteType);
			this.insertSerialFrequencyModule(moduleAtIndex);
		} else if ((satelliteType == JSynObject.RING_MODULATION || satelliteType == JSynObject.ADDITIVE)
				&& moduleType == JSynObject.FREQUENCY_MODULATION) {
			moduleAtIndex.disconnect();
			this.serialFrequencyModules.remove(moduleAtIndex);
			moduleAtIndex.setType(satelliteType);
			this.insertParallelModule(moduleAtIndex);
		}
	}
	
	private void insertSerialFrequencyModule(SmoothOscillatorModule newModule) {
		if (this.serialFrequencyModules.size() > 0) {
			newModule.insertAfter(this.serialFrequencyModules.get(this.serialFrequencyModules.size()-1));
		} else {
			//no modules yet, channel frequency sweeper through input A
			newModule.insertBetween(this.frequencySweeper.output, this.oscillator.frequency);
		}
		this.serialFrequencyModules.add(newModule);
	}
	
	private void insertParallelModule(SmoothOscillatorModule newModule) {
		if (this.parallelModules.size() > 0) {
			newModule.insertAfter(this.parallelModules.get(this.parallelModules.size()-1));
		} else {
			//no modules yet, add operator after oscillator output
			newModule.insertBetween(this.oscillator.output, this.outputTo);
		}
		this.parallelModules.add(newModule);
	}
	
	public List<SmoothOscillatorModule> getSatellites() {
		return this.satelliteModules;
	}
	
	public void removeLastSatellite() {
		if (this.satelliteModules.size() > 0) {
			SmoothOscillatorModule lastSatellite = this.satelliteModules.remove(this.satelliteModules.size()-1);
			this.parallelModules.remove(lastSatellite);
			this.serialFrequencyModules.remove(lastSatellite);
			lastSatellite.removeFromSynthAndStop();
		}
	}
	
	public void setFrequency(double frequency) {
		this.frequencySweeper.input.set(frequency);
	}
	
	public double getFrequency() {
		return this.frequencySweeper.input.get();//this.oscillator.frequency.getValue();
	}
	
	public void setAmplitude(double amplitude) {
		this.amplitudeSweeper.input.set(amplitude);
	}
	
	public double getAmplitude() {
		return this.amplitudeSweeper.input.get();
	}
	
	public UnitOutputPort getOutput() {
		if (this.parallelModules.size() > 0) {
			return this.parallelModules.get(0).getOutput();
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
		for (SmoothOscillatorModule currentModule : this.satelliteModules) {
			currentModule.removeFromSynthAndStop();
		}
	}
	
	public String toString() {
		return this.getFrequency() + " " + this.getAmplitude() + " " + this.serialFrequencyModules + " " + this.parallelModules + " " + this.satelliteModules;
	}

}

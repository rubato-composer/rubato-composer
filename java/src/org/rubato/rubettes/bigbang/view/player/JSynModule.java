package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.unitgen.LineOut;

public class JSynModule {
	
	private JSynPlayer player;
	private List<SmoothOscillator> carriers;
	private LineOut lineOut;
	
	
	public JSynModule(JSynPlayer player) {
		this.player = player;
	 	//TODO: why one line out PER module????
	 	this.player.addToSynth(this.lineOut = new LineOut());
	 	this.carriers = new ArrayList<SmoothOscillator>();
	 	this.addCarrier();
	 	this.start();
	}
	
	private void addCarrier() {
		SmoothOscillator newCarrier = new SmoothOscillator(this.player);
		newCarrier.getOutput().connect(0, this.lineOut.input, 0);
		newCarrier.getOutput().connect(0, this.lineOut.input, 1);
		this.carriers.add(newCarrier);
	}
	
	public double getMainCarrierFrequency() {
		return this.carriers.get(0).getFrequency();
	}
	
	public void playOrAdjustObject(JSynObject object) {
		this.playOrAdjustObject(object, 1);
	}
	
	private void playOrAdjustObject(JSynObject object, int modulatorAmplitudeFactor) {
		for (int i = 0; i < object.getFrequencies().size(); i++) {
			double currentFrequency = object.getFrequencies().get(i);
			if (this.carriers.size() <= i) {
				this.addCarrier();
			}
			this.playOrAdjustObject(this.carriers.get(i), object, currentFrequency, modulatorAmplitudeFactor);
		}
	}
	
	//recursive method
	private void playOrAdjustObject(SmoothOscillator oscillator, JSynObject object, double frequency, int modulatorAmplitudeFactor) {
		//adjust frequency and amplitude
		oscillator.setFrequency(frequency);
		oscillator.setAmplitude(object.getAmplitude()*this.player.getRecommendedAmplitude()*modulatorAmplitudeFactor);
		//adjust or schedule time
		double currentSymbolicTime = this.player.getCurrentSymbolicTime();
		if (object.getOnset() > currentSymbolicTime) {
			double duration = this.player.convertToSynthDuration(object.getDuration());
			oscillator.queueEnvelope(duration, this.player.convertToSynthOnset(object.getOnset()));
		} else {
			double remainingDuration = this.player.convertToSynthDuration(object.getDuration()-(currentSymbolicTime-object.getOnset()));
			if (remainingDuration > 0) {
				oscillator.queueEnvelopeWithoutAttackAndDecay(remainingDuration, this.player.getCurrentSynthTime());
			} else {
				this.mute();
			}
		}
		//recursively create or adjust modulators 
		List<JSynObject> modulatorObjects = object.getModulators();
		List<SmoothOscillator> modulators = oscillator.getModulators();
		for (int i = 0; i < modulatorObjects.size(); i++) {
			JSynObject currentModulator = modulatorObjects.get(i);
			if (modulators.size() <= i) {
				oscillator.addModulator();
			}
			//TODO: one modulator may have several frequencies! go through all
			this.playOrAdjustObject(modulators.get(i), currentModulator, currentModulator.getMainFrequency(), 2000);
		}
		//TODO: remove exceeding ones!!!!
	}
	
	public void mute() {
		for (SmoothOscillator currentCarrier : this.carriers) {
			currentCarrier.mute();
		}
	}
	
	private void start() {
		this.lineOut.start();
	}
	
	@Override
	protected void finalize() {
		this.mute();
		this.lineOut.stop();
	}

}

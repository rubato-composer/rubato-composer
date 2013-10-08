package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Pan;

public class JSynModule {
	
	private JSynPerformance performance;
	private JSynPlayer player;
	private List<SmoothOscillator> carriers;
	private LineOut lineOut;
	private Pan pan;
	
	
	public JSynModule(JSynPerformance performance) {
		this.performance = performance;
		this.player = performance.getPlayer();
		this.player.addToSynth(this.pan = new Pan());
	 	//TODO: why one line out PER module????
	 	this.player.addToSynth(this.lineOut = new LineOut());
	 	this.pan.output.connect(0, this.lineOut.input, 0);
		this.pan.output.connect(1, this.lineOut.input, 1);
	 	this.carriers = new ArrayList<SmoothOscillator>();
	 	this.addCarrier();
	 	this.start();
	}
	
	private void addCarrier() {
		SmoothOscillator newCarrier = new SmoothOscillator(this.player);
		newCarrier.getOutput().connect(0, this.pan.input, 0);
		this.carriers.add(newCarrier);
	}
	
	public double getMainCarrierFrequency() {
		return this.carriers.get(0).getFrequency();
	}
	
	public double getMainCarrierAmplitude() {
		return this.carriers.get(0).getAmplitude();
	}
	
	public double getPan() {
		return this.pan.pan.get();
	}
	
	public void playOrAdjustObject(JSynObject object, boolean playInNextLoop) {
		this.playOrAdjustObject(object, 1, playInNextLoop);
	}
	
	private void playOrAdjustObject(JSynObject object, int modulatorAmplitudeFactor, boolean playInNextLoop) {
		for (int i = 0; i < object.getFrequencies().size(); i++) {
			double currentFrequency = object.getFrequencies().get(i);
			if (this.carriers.size() <= i) {
				this.addCarrier();
			}
			this.playOrAdjustObject(this.carriers.get(i), object, currentFrequency, modulatorAmplitudeFactor, playInNextLoop);
		}
	}
	
	//recursive method
	private void playOrAdjustObject(SmoothOscillator oscillator, JSynObject object, double frequency, int modulatorAmplitudeFactor, boolean playInNextLoop) {
		//System.out.println(object + " " + this.player.getCurrentSymbolicTime() + " " + this.player.getCurrentSynthTime());
		//adjust frequency and amplitude
		oscillator.setFrequency(frequency);
		oscillator.setAmplitude(object.getAmplitude()*this.player.getRecommendedAmplitude()*modulatorAmplitudeFactor);
		this.pan.pan.set(object.getPan());
		//adjust or schedule time
		double currentSymbolicTime = this.performance.getCurrentSymbolicTime();
		if (object.isPlayable()) {
			if (object.getOnset() > currentSymbolicTime || (object.getOnset() < currentSymbolicTime && playInNextLoop)) {
				double onset = this.performance.getSynthOnset(object.getOnset(), playInNextLoop);
				double duration = this.player.convertToSynthDuration(object.getDuration());
				oscillator.queueEnvelope(duration, onset, true);
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
				this.playOrAdjustObject(modulators.get(i), currentModulator, currentModulator.getMainFrequency(), 2000, playInNextLoop);
			}
			//remove exceeding ones
			while (modulatorObjects.size() < modulators.size()) {
				oscillator.removeLastModulator();
			}
		}
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
		//lame but works!
		new Thread() {
			public void run() {
				//first mute and wait for ramps to go down to not get glitches
				mute();
				try {
					player.getSynth().sleepFor(SmoothOscillator.RAMP_DURATION*2);
				} catch (InterruptedException e) { e.printStackTrace();	}
				//then remove
				for (SmoothOscillator oscillator : carriers) {
					oscillator.removeFromSynthAndStop();
				}
				player.removeFromSynthAndStop(lineOut);
			}
		}.start();
	}

}

package org.rubato.rubettes.bigbang.view.player;

import java.util.List;

import com.jsyn.unitgen.LineOut;

public class JSynModule {
	
	private JSynPlayer player;
	private SmoothOscillator carrier;
	private LineOut lineOut;
	
	
	public JSynModule(JSynPlayer player) {
		this.player = player;
		
		// Create unit generators.
	 	this.carrier = new SmoothOscillator(player);
	 	//TODO: why one line out PER module????
	 	this.player.addToSynth(this.lineOut = new LineOut());

	 	this.carrier.getOutput().connect( 0, this.lineOut.input, 0 );
	 	this.carrier.getOutput().connect( 0, this.lineOut.input, 1 );
	 	
	 	this.start();
	}
	
	public double getCarrierFrequency() {
		return this.carrier.getFrequency();
	}
	
	public void playOrAdjustObject(JSynObject object) {
		this.playOrAdjustObject(this.carrier, object, 1);
	}
	
	//recursive method
	private void playOrAdjustObject(SmoothOscillator oscillator, JSynObject object, int modulatorAmplitudeFactor) {
		//adjust frequency and amplitude
		oscillator.setFrequency(object.getFrequency());
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
			if (modulators.size() <= i) {
				SmoothOscillator modulator = new SmoothOscillator(this.player);
				oscillator.addModulator(modulator);
			}
			this.playOrAdjustObject(modulators.get(i), modulatorObjects.get(i), 2000);
		}
		//TODO: remove exceeding ones!!!!
	}
	
	public void mute() {
		this.carrier.mute();
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

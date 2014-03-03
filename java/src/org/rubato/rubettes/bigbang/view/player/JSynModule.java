package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Pan;

public class JSynModule {
	
	private JSynPerformance performance;
	private JSynPlayer player;
	private JSynObject currentObject;
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
	
	public JSynObject getCurrentObject() {
		return this.currentObject;
	}
	
	private void addCarrier() {
		SmoothOscillator newCarrier = new SmoothOscillator(this.player, this.pan.input);
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
		for (int i = 0; i < object.getFrequencies().size(); i++) {
			double currentFrequency = object.getFrequencies().get(i);
			if (this.carriers.size() <= i) {
				this.addCarrier();
			}
			this.playOrAdjustObject(this.carriers.get(i), object, currentFrequency, playInNextLoop);
		}
		this.currentObject = object;
	}
	
	//recursive method
	private void playOrAdjustObject(SmoothOscillator oscillator, JSynObject object, double frequency, boolean playInNextLoop) {
		//System.out.println(object + " " + this.player.getCurrentSynthTime() + " " +oscillator);
		//adjust frequency and amplitude
		oscillator.setFrequency(frequency);
		oscillator.setAmplitude(object.getAmplitude()*this.player.getRecommendedAmplitude());
		this.pan.pan.set(object.getPan());
		//adjust or schedule time
		double currentSymbolicTime = this.performance.getCurrentSymbolicTime();
		if (object.isAudible()) {
			if (object.getOnset() > currentSymbolicTime || (object.getOnset() < currentSymbolicTime && playInNextLoop)) {
				double onset = this.performance.getSynthOnset(object.getOnset(), playInNextLoop);
				double duration = this.player.convertToSynthDuration(object.getDuration());
				oscillator.queueEnvelope(duration, onset, true);
				int onsetMillis = (int)Math.round((onset-this.player.getCurrentSynthTime())*1000);
				int durationMillis = (int)Math.round(duration*1000);
				this.player.sendMidiMessages(object.getVoice(), object.frequencyToMidi(frequency), object.getLoudness(), onsetMillis, durationMillis);
			} else {
				double remainingDuration = this.player.convertToSynthDuration(object.getDuration()-(currentSymbolicTime-object.getOnset()));
				if (remainingDuration > 0) {
					oscillator.queueEnvelopeWithoutAttackAndDecay(remainingDuration, this.player.getCurrentSynthTime());
				} else {
					this.mute();
				}
			}
			//recursively create or adjust modulators 
			List<JSynObject> satelliteObjects = object.getSatellites();
			int modulatorType = object.getSatelliteType();
			List<SmoothOscillatorModule> satellites = oscillator.getSatellites();
			//System.out.println(modulatorObjects + " " + modulatorType);
			for (int i = 0; i < satelliteObjects.size(); i++) {
				JSynObject currentModulator = satelliteObjects.get(i);
				if (satellites.size() <= i) {
					oscillator.addSatellite(modulatorType);
				}
				oscillator.setSatelliteType(i, modulatorType);
				//TODO: one modulator may have several frequencies! go through all
				this.playOrAdjustObject(satellites.get(i).getOscillator(), currentModulator, currentModulator.getMainFrequency(), playInNextLoop);
			}
			//remove exceeding ones
			while (satelliteObjects.size() < satellites.size()) {
				oscillator.removeLastSatellite();
			}
			//System.out.println(oscillator);
		} else {
			this.mute();
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

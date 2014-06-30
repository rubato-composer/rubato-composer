package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.Pan;

public class JSynModule {
	
	private JSynPerformance performance;
	private BigBangPlayer player;
	private JSynObject currentObject;
	private List<SmoothOscillator> carriers;
	private LineOut lineOut;
	private LinearRamp panSweeper;
	private Pan pan;
	
	
	public JSynModule(JSynPerformance performance) {
		this.performance = performance;
		this.player = performance.getPlayer();
		this.player.addToSynth(this.pan = new Pan());
		this.player.addToSynth(this.panSweeper = new LinearRamp());
	 	this.panSweeper.output.connect(this.pan.pan);
	 	this.panSweeper.time.set(SmoothOscillator.RAMP_DURATION);
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
	
	public void setPan(double pan) {
		this.panSweeper.input.set(pan);
	}
	
	public double getPan() {
		return this.panSweeper.input.get();//this.oscillator.frequency.getValue();
	}
	
	public void playOrAdjustObject(JSynObject object, boolean playInNextLoop) {
		this.playOrAdjustObject(null, object, playInNextLoop);
		this.currentObject = object;
	}
	
	//recursive method
	private void playOrAdjustObject(SmoothOscillator parentOscillator, JSynObject object, boolean playInNextLoop) {
		if (object.isAudible()) {
			this.setPan(object.getPan());
			//adjust or schedule time
			double currentSymbolicTime = this.performance.getCurrentSymbolicTime();
			if (object.getOnset() > currentSymbolicTime || (object.getOnset() < currentSymbolicTime && playInNextLoop)) {
				//sound has to be started
				double onset = this.performance.getSynthOnset(object.getOnset(), playInNextLoop);
				double duration = this.player.convertToSynthDuration(object.getDuration());
				this.adjustOscillators(object, parentOscillator, onset, duration);
				//start midi
				int onsetMillis = (int)Math.round((onset-this.player.getCurrentSynthTime())*1000);
				int durationMillis = (int)Math.round(duration*1000);
				this.performance.scheduleMidiNote(object, onsetMillis, durationMillis);
			} else {
				//sound has to be changed
				double remainingDuration = this.player.convertToSynthDuration(object.getDuration()-(currentSymbolicTime-object.getOnset()));
				if (remainingDuration > 0) {
					this.adjustOscillators(object, parentOscillator, null, remainingDuration);
					int durationMillis = (int)Math.round(remainingDuration*1000);
					this.performance.scheduleMidiNote(object, 0, durationMillis);
				} else if (object.getRate() > 0) {
					//this.adjustOscillators(object, parentOscillator, null, remainingDuration);
					int durationMillis = (int)Math.round(object.getDuration()*1000);
					this.performance.scheduleMidiNote(object, 0, durationMillis);
				} else {
					this.mute();
					this.performance.muteMidi(object);
				}
			}
			//recursively create or adjust modulators 
			List<JSynObject> satelliteObjects = object.getSatellites();
			for (SmoothOscillator currentCarrier : this.getSatellitesAt(parentOscillator)) {
				for (int i = 0; i < satelliteObjects.size(); i++) {
					JSynObject currentSatelliteObject = satelliteObjects.get(i);
					this.playOrAdjustObject(currentCarrier, currentSatelliteObject, playInNextLoop);
				}
				//remove exceeding ones
				while (satelliteObjects.size() < currentCarrier.getSatellites().size()) {
					currentCarrier.removeLastSatellite();
				}
				//System.out.println(oscillator);
			}
		} else {
			this.mute();
		}
	}
	
	/*
	 * adjusts frequencies and amplitudes of oscillators. if onset != null, schedules new envelope. else
	 * adjusts envelope taking duration as remaining duration.
	 */
	private void adjustOscillators(JSynObject object, SmoothOscillator parentOscillator, Double onset, double duration) {
		//play each frequency with an oscillator. add new ones if necessary
		for (int i = 0; i < object.getFrequencies().size(); i++) {
			SmoothOscillator currentOscillator;
			if (object.getParent() != null) {
				currentOscillator = this.getSatelliteAt(parentOscillator, i, object.getParent().getSatelliteType());
			} else {
				currentOscillator = this.getCarrierAt(i);
			}
			//adjust frequency and amplitude
			currentOscillator.setFrequency(object.getFrequencies().get(i));
			currentOscillator.setAmplitude(object.getAmplitude()*this.player.getRecommendedAmplitude());
			if (onset != null) {
				currentOscillator.queueEnvelope(duration, onset, true);
			} else {
				currentOscillator.queueEnvelopeWithoutAttackAndDecay(duration, this.player.getCurrentSynthTime());
			}
		}
	}
	
	private SmoothOscillator getCarrierAt(int index) {
		while (this.carriers.size() <= index) {
			this.addCarrier();
		}
		return this.carriers.get(index);
	}
	
	private SmoothOscillator getSatelliteAt(SmoothOscillator parentOscillator, int index, int modulatorType) {
		while (parentOscillator.getSatellites().size() <= index) {
			parentOscillator.addSatellite(modulatorType);
		}
		return parentOscillator.getSatellites().get(index).getOscillator();
	}
	
	private List<SmoothOscillator> getSatellitesAt(SmoothOscillator parentOscillator) {
		if (parentOscillator == null) {
			return this.carriers;
		}
		List<SmoothOscillator> satellites = new ArrayList<SmoothOscillator>();
		for (SmoothOscillatorModule currentSatellite : parentOscillator.getSatellites()) {
			satellites.add(currentSatellite.getOscillator());
		}
		return satellites;
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

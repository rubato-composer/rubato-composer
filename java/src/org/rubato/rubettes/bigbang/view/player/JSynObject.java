package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.math.yoneda.Form;

public class JSynObject {
	
	private Double frequency;
	private double amplitude;
	private Double duration;
	private int voice;
	private Double onset, offset;
	private List<JSynModulator> modulators;
	
	public JSynObject() {
		this.modulators = new ArrayList<JSynModulator>();
		//assign standard values
		this.setOnset(0);
		this.setFrequency(60);
		this.setAmplitude(100);
		this.setDuration(Double.MAX_VALUE);
		this.setVoice(0);
	}
	
	public void addValues(Form form, List<Double> values) {
		Repository repository = Repository.systemRepository();
		if (form == repository.getForm("Onset")) {
			this.setOnset(values.get(0));
		} else if (form == repository.getForm("Pitch")) {
			this.setFrequency(values.get(0));
		} else if (form == repository.getForm("PitchClass")) {
			this.setFrequency(60+values.get(0));
		} else if (form == repository.getForm("Loudness")) {
			this.setAmplitude(values.get(0));
		} else if (form == repository.getForm("Duration")) {
			this.setDuration(values.get(0));
		} else if (form == repository.getForm("Voice")) {
			this.setVoice((int)values.get(0).doubleValue());
		}
	}
	
	public boolean playsAt(double onset, double offset) {
		boolean noIntersection = this.offset < onset || this.onset > offset; 
		return !noIntersection;
	}
	
	public void setOnset(double onset) {
		this.onset = onset;
		//this.onsetInTicks = (int) (onset * JSynPlayer.TICKS_PER_SECOND); //(60.0 / bpm * onset * JSynPlayer.TICKS_PER_SECOND);
	}
	
	public double getOnset() {
		return this.onset;
	}

	public double getFrequency() {
		return this.frequency;
	}

	private void setFrequency(double pitch) {
		this.frequency = this.midiToFrequency(pitch);
	}

	public double getAmplitude() {
		return this.amplitude;
	}

	private void setAmplitude(double loudness) {
		loudness = Math.min(loudness, 127);
		loudness = Math.max(loudness, 0);
		this.amplitude = loudness/127;
	}
	
	public double getDuration() {
		return this.duration;
	}

	private void setDuration(double duration) {
		this.offset = this.onset + duration;
		this.duration = duration;//60.0 / bpm * duration;
		//this.durationInTicks = (int) (duration * JSynPlayer.TICKS_PER_SECOND);
	}
	
	public double getOffset() {
		return this.offset;
	}
	
	public int getVoice() {
		return this.voice;
	}
	
	private void setVoice(int voice) {
		this.voice = voice;
	}
	
	private double midiToFrequency(double midiPitch) {
		return JSynPlayer.BASE_A4*Math.pow(2, (midiPitch-57)/12);
	}
	
	//TODO: won't work!!
	public JSynModulator addModulator(Form form, List<Double> values) {
		JSynModulator modulator = new JSynModulator(); 
		modulator.addValues(form, values);
		this.modulators.add(modulator);
		return modulator;
	}
	
	public List<JSynModulator> getModulators() {
		return this.modulators;
	}
	
	public String toString() {
		return "(" + this.onset + " " +this.frequency + " " + this.offset + ")";
	}

}

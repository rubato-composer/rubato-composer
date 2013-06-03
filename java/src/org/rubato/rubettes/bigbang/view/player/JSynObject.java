package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.util.CoolFormRegistrant;

public class JSynObject {
	
	private JSynObject parent;
	private List<Double> intervalStructure;
	private List<Double> frequencies;
	private double amplitude;
	private Double duration;
	private int voice;
	private Double onset, offset;
	private List<JSynObject> modulators;
	
	public JSynObject(JSynObject parent) {
		this.parent = parent;
		//TODO: modulators will just be children!!!!
		this.modulators = new ArrayList<JSynObject>();
		//assign standard values
		this.setOnset(0);
		this.setPitch(60);
		this.setLoudness(100);
		this.setDuration(Double.MAX_VALUE);
		this.setVoice(0);
	}
	
	public void addValues(Form form, Map<String,Double> values) {
		if (form.equals(CoolFormRegistrant.ONSET_FORM) || form.equals(CoolFormRegistrant.BEAT_CLASS_FORM)) {
			this.setOnset(this.getSingleValue(values));
			if (form.equals(CoolFormRegistrant.BEAT_CLASS_FORM)) {
				//TODO: loop
			}
		} else if (form.equals(CoolFormRegistrant.PITCH_FORM) || form.equals(CoolFormRegistrant.CHROMATIC_PITCH_FORM)) {
			this.setPitch(this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.PITCH_CLASS_FORM)) {
			this.setPitch(60+this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.OVERTONE_INDEX_FORM)) {
			if (this.parent != null) {
				this.setOvertoneFrequency(this.parent.getFrequencies().get(0), (int)this.getSingleValue(values));
			}
		} else if (form.equals(CoolFormRegistrant.LOUDNESS_FORM)) {
			this.setLoudness(this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.DURATION_FORM)) {
			this.setDuration(this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.VOICE_FORM)) {
			this.setVoice((int)this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.QUALITY_FORM)) {
			this.setTriadQuality((int)this.getSingleValue(values));
		}
	}
	
	private double getSingleValue(Map<String,Double> values) {
		return values.values().iterator().next();
	}
	
	public boolean playsAt(double onset, double offset) {
		boolean noIntersection = this.offset < onset || this.onset > offset; 
		return !noIntersection;
	}
	
	/**
	 * sets onset and adjusts offset to match the object's duration
	 */
	public void setOnset(double onset) {
		this.onset = onset;
		if (this.duration != null) {
			this.offset = onset + this.duration;
		}
	}
	
	public double getOnset() {
		return this.onset;
	}
	
	/*
	 * sets duration and adjusts offset
	 */
	private void setDuration(double duration) {
		this.duration = duration;
		this.offset = this.onset + duration;
	}
	
	public double getDuration() {
		return this.duration;
	}
	
	public double getOffset() {
		return this.offset;
	}
	
	public double getMainFrequency() {
		return this.frequencies.get(0);
	}

	public List<Double> getFrequencies() {
		return this.frequencies;
	}

	private void setPitch(double pitch) {
		this.setFrequency(this.midiToFrequency(pitch));
	}
	
	private void setTriadQuality(int quality) {
		if (quality < 2) {
			this.addInterval(3);
		} else {
			this.addInterval(4);
		}
		if (quality % 2 == 0) {
			this.addInterval(3);
		} else {
			this.addInterval(4);
		}
		this.updateFrequencies();
	}
	
	private void addInterval(double interval) {
		if (this.intervalStructure == null) {
			this.intervalStructure = new ArrayList<Double>();
		}
		this.intervalStructure.add(interval);
	}
	
	private void setFrequency(double frequency) {
		this.frequencies = new ArrayList<Double>(); 
		this.frequencies.add(frequency);
		this.updateFrequencies();
	}
	
	private void addFrequency(double frequency) {
		this.frequencies.add(frequency);
	}
	
	private void setFrequencies(List<Double> frequencies) {
		this.frequencies = frequencies;
	}
	
	private void updateFrequencies() {
		if (this.intervalStructure != null) {
			double currentFrequency = this.frequencies.get(0);
			for (double currentInterval : this.intervalStructure) {
				currentFrequency = currentFrequency*this.intervalToRatio(currentInterval);
				this.addFrequency(currentFrequency);
			}
		}
	}
	
	private void setOvertoneFrequency(double baseFrequency, int overtoneIndex) {
		double overtoneFrequency = baseFrequency;
		while (overtoneIndex > 0) {
			overtoneFrequency = overtoneFrequency*(overtoneIndex+1)/overtoneIndex;
			overtoneIndex--;
		}
		this.setFrequency(overtoneFrequency);
	}

	public double getAmplitude() {
		return this.amplitude;
	}
	
	private void setLoudness(double loudness) {
		loudness = Math.min(loudness, 127);
		loudness = Math.max(loudness, 0);
		this.setAmplitude(loudness/127);
	}

	private void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
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
	
	private double intervalToRatio(double interval) {
		return Math.pow(Math.sqrt(Math.sqrt(Math.cbrt(2))), interval); //12th root of two
	}
	
	public JSynObject addModulator() {
		JSynObject modulator = new JSynObject(this); 
		this.modulators.add(modulator);
		return modulator;
	}
	
	public List<JSynObject> getModulators() {
		return this.modulators;
	}
	
	//TODO: IMPROVE!!!
	public JSynObject clone() {
		JSynObject clone = new JSynObject(this.parent);
		clone.setOnset(this.onset);
		clone.setFrequencies(this.frequencies);
		clone.setAmplitude(this.amplitude);
		clone.setDuration(this.duration);
		clone.setVoice(voice);
		return clone;
	}
	
	public String toString() {
		return "(" + this.onset + " " + this.frequencies + " " + this.amplitude + " " + this.offset + ")";
	}

}

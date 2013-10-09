package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.util.CoolFormRegistrant;

public class JSynObject {
	
	public static final int ADDITIVE = 0;
	public static final int RING_MODULATION = 1;
	public static final int FREQUENCY_MODULATION = 2;
	
	private JSynObject parent;
	private List<Double> intervalStructure;
	private List<Double> frequencies;
	private Double amplitude;
	private Double duration;
	private Integer voice;
	private Double onset;
	private Double pan;
	private Integer operation;
	private List<JSynObject> modulators;
	
	public JSynObject(JSynObject parent) {
		this.parent = parent;
		//TODO: modulators will just be children!!!!
		this.modulators = new ArrayList<JSynObject>();
		this.frequencies = new ArrayList<Double>();
	}
	
	public void addValues(Form form, List<Double> values) {
		if (form.equals(CoolFormRegistrant.ONSET_FORM) || form.equals(CoolFormRegistrant.BEAT_CLASS_FORM)) {
			this.setOnset(this.getSingleValue(values));
			if (form.equals(CoolFormRegistrant.BEAT_CLASS_FORM)) {
				//TODO: loop
			}
		} else if (form.equals(CoolFormRegistrant.PITCH_FORM) || form.equals(CoolFormRegistrant.CHROMATIC_PITCH_FORM)) {
			this.addPitch(this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.PITCH_CLASS_FORM)) {
			this.addPitch(60+this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.OVERTONE_INDEX_FORM)) {
			if (this.frequencies.size() > 0) {
				this.setOvertoneFrequency(this.getMainFrequency(), (int)this.getSingleValue(values));
			} else if (this.parent != null) {
				this.setOvertoneFrequency(this.parent.getMainFrequency(), (int)this.getSingleValue(values));
			}
		} else if (form.equals(CoolFormRegistrant.LOUDNESS_FORM)) {
			this.setLoudness((int)this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.DURATION_FORM)) {
			this.setDuration(this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.VOICE_FORM)) {
			this.setVoice((int)this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.QUALITY_FORM)) {
			this.setTriadQuality((int)this.getSingleValue(values));
		} else if (form.equals(CoolFormRegistrant.PAN_FORM)) {
			this.pan = this.getSingleValue(values);
		} else if (form.equals(CoolFormRegistrant.OPERATION_FORM)) {
			this.operation = (int)this.getSingleValue(values);
		}
	}
	
	private double getSingleValue(List<Double> values) {
		return values.get(0);
	}
	
	/**
	 * @return true if the object is worth being played. for now this is only the case if it contains at least
	 * one frequency. 
	 */
	public boolean isPlayable() {
		return this.frequencies.size() > 0;
	}
	
	
	//TIME
	
	/**
	 * sets onset and adjusts offset to match the object's duration
	 */
	private void setOnset(Double onset) {
		this.onset = onset;
	}
	
	public double getOnset() {
		if (this.onset != null) {
			return this.onset;
		}
		return 0;
	}
	
	/*
	 * sets duration and adjusts offset
	 */
	private void setDuration(Double duration) {
		this.duration = duration;
	}
	
	public double getDuration() {
		if (this.duration != null) {
			return this.duration;
		}
		return Double.MAX_VALUE;
	}
	
	public Double getOffset() {
		return this.getOnset() + this.getDuration();
	}
	
	public boolean playsAt(double onset, double offset) {
		boolean noIntersection = this.getOffset() < onset || this.getOnset() > offset; 
		return !noIntersection;
	}
	
	
	//FREQUENCY
	
	public double getMainFrequency() {
		return this.getFrequencies().get(0);
	}

	public List<Double> getFrequencies() {
		if (this.frequencies.size() > 0) {
			return this.frequencies;
		}
		return Arrays.asList(this.midiToFrequency(60));
	}

	private void addPitch(Double pitch) {
		this.addFrequency(this.midiToFrequency(pitch));
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
	
	private void setFrequency(Double frequency) {
		this.frequencies = new ArrayList<Double>();
		this.addFrequency(frequency);
		this.updateFrequencies();
	}
	
	private void addFrequency(Double frequency) {
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
	
	public void adjustFrequencies(double interval) {
		for (int i = 0; i < this.frequencies.size(); i++) {
			this.frequencies.set(i, this.frequencies.get(i)*this.intervalToRatio(interval));
		}
		for (JSynObject currentModulator : this.modulators) {
			currentModulator.adjustFrequencies(interval);
		}
	}
	
	
	//AMPLITUDE

	public double getAmplitude() {
		if (this.amplitude != null) {
			return this.amplitude;
		}
		return this.midiToAmplitude(100);
	}
	
	private void setLoudness(int loudness) {
		this.setAmplitude(this.midiToAmplitude(loudness));
	}

	private void setAmplitude(Double amplitude) {
		this.amplitude = amplitude;
	}
	
	public void adjustAmplitude(double ratio) {
		this.amplitude *= ratio;
		for (JSynObject currentModulator : this.modulators) {
			currentModulator.adjustAmplitude(ratio);
		}
	}
	
	
	//VOICE
	
	public int getVoice() {
		if (this.voice != null) {
			return this.voice;
		}
		return 0;
	}
	
	private void setVoice(Integer voice) {
		this.voice = voice;
	}
	
	
	//PAN
	public double getPan() {
		if (this.pan != null) {
			return this.midiToPan(this.pan);
		}
		return 0;
	}
	
	private void setPan(Double pan) {
		this.pan = pan;
	}
	
	
	//MODULATORS
	
	public JSynObject addModulator() {
		JSynObject modulator = new JSynObject(this);
		this.modulators.add(modulator);
		return modulator;
	}
	
	public List<JSynObject> getModulators() {
		return this.modulators;
	}
	
	public int getModulatorType() {
		if (this.operation != null) {
			if (this.operation == 0) {
				return JSynObject.ADDITIVE;
			} else if (this.operation == 1) {
				return JSynObject.RING_MODULATION;
			} return JSynObject.FREQUENCY_MODULATION;
		}
		return JSynObject.FREQUENCY_MODULATION;
	}
	
	private void setModulators(List<JSynObject> modulators) {
		this.modulators = modulators;
	}
	
	private void setOperation(int operation) {
		this.operation = operation;
	}
	
	
	//UTIL
	
	private double midiToFrequency(double midiPitch) {
		return JSynPlayer.BASE_A4*Math.pow(2, (midiPitch-57)/12);
	}
	
	private double midiToAmplitude(double loudness) {
		loudness = Math.min(loudness, 127);
		loudness = Math.max(loudness, 0);
		return loudness/127;
	}
	
	private double midiToPan(double pan) {
		pan = Math.min(pan, 127);
		pan = Math.max(pan, 0);
		return (pan/64)-1;
	}
	
	private double intervalToRatio(double interval) {
		return Math.pow(Math.sqrt(Math.sqrt(Math.cbrt(2))), interval); //12th root of two
	}
	
	//TODO: IMPROVE!!! 
	public JSynObject clone() {
		JSynObject clone = new JSynObject(this.parent);
		clone.setOnset(this.onset);
		clone.setFrequencies(new ArrayList<Double>(this.frequencies));
		clone.setAmplitude(this.amplitude);
		clone.setDuration(this.duration);
		clone.setVoice(this.voice);
		clone.setPan(this.pan);
		if (this.operation != null) {
			clone.setOperation(this.operation);
		}
		if (this.modulators.size() > 0) {
			List<JSynObject> clonedModulators = new ArrayList<JSynObject>();
			for (JSynObject currentModulator : this.modulators) {
				clonedModulators.add(currentModulator.clone());
			}
			clone.setModulators(clonedModulators);
		}
		return clone;
	}
	
	public String toString() {
		return "(" + this.onset + " " + this.frequencies + " " + this.amplitude + " " + this.duration + ")";
	}

}

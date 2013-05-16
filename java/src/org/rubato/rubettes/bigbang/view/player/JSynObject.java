package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.base.Repository;
import org.rubato.math.yoneda.Form;

public class JSynObject {
	
	private JSynObject parent;
	private Double frequency;
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
		this.setFrequency(60);
		this.setAmplitude(100);
		this.setDuration(Double.MAX_VALUE);
		this.setVoice(0);
	}
	
	public void addValues(Form form, Map<String,Double> values) {
		Repository repository = Repository.systemRepository();
		if (form == repository.getForm("Onset")) {
			this.setOnset(this.getSingleValue(values));
		} else if (form == repository.getForm("Pitch")) {
			this.setFrequency(this.getSingleValue(values));
		} else if (form == repository.getForm("PitchClass")) {
			this.setFrequency(60+this.getSingleValue(values));
		} else if (form == repository.getForm("OvertoneIndex")) {
			if (this.parent != null) {
				this.setOvertoneFrequency(this.parent.getFrequency(), (int)this.getSingleValue(values));
			}
		} else if (form == repository.getForm("Loudness")) {
			this.setAmplitude(this.getSingleValue(values));
		} else if (form == repository.getForm("Duration")) {
			this.setDuration(this.getSingleValue(values));
		} else if (form == repository.getForm("Voice")) {
			this.setVoice((int)this.getSingleValue(values));
		}
	}
	
	private double getSingleValue(Map<String,Double> values) {
		return values.values().iterator().next();
	}
	
	public boolean playsAt(double onset, double offset) {
		boolean noIntersection = this.offset < onset || this.onset > offset; 
		return !noIntersection;
	}
	
	public void setOnset(double onset) {
		this.onset = onset;
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
	
	private void setOvertoneFrequency(double baseFrequency, int overtoneIndex) {
		this.frequency = baseFrequency;
		while (overtoneIndex > 0) {
			this.frequency = this.frequency*(overtoneIndex+1)/overtoneIndex;
			overtoneIndex--;
		}
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
		this.duration = duration;
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
	public JSynObject addModulator(Form form, Map<String,Double> values) {
		JSynObject modulator = new JSynObject(this); 
		modulator.addValues(form, values);
		this.modulators.add(modulator);
		return modulator;
	}
	
	public List<JSynObject> getModulators() {
		return this.modulators;
	}
	
	public String toString() {
		return "(" + this.onset + " " +this.frequency + " " + this.offset + ")";
	}

}

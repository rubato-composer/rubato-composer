package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.math.yoneda.Form;

public class JSynObject {
	
	private int onsetInTicks;
	private double frequency;
	private double amplitude;
	private double duration;
	private int durationInTicks;
	private int voice;
	private double symbolicStart, symbolicEnd;
	private List<JSynModulator> modulators;
	
	public JSynObject() {
		this.modulators = new ArrayList<JSynModulator>();
	}
	
	public void addValues(Form form, List<Double> values) {
		Repository repository = Repository.systemRepository();
		if (form == repository.getForm("Onset")) {
			this.setOnsetFromDenotator(values.get(0));
		} else if (form == repository.getForm("Pitch")) {
			this.setFrequency(values.get(0));
		} else if (form == repository.getForm("Loudness")) {
			this.setAmplitude(values.get(0));
		} else if (form == repository.getForm("Duration")) {
			this.setDuration(values.get(0));
		} else if (form == repository.getForm("Voice")) {
			this.setVoice((int)values.get(0).doubleValue());
		}
	}
	
	public boolean playsAt(double symbolicStart, double symbolicEnd) {
		boolean noIntersection = this.symbolicEnd < symbolicStart || this.symbolicStart > symbolicEnd; 
		return !noIntersection;
	}
	
	public void setOnset(double onset) {
		this.symbolicStart = onset;
	}
	
	public double getOnset() {
		return this.symbolicStart;
	}

	/*public int getOnsetInTicks() {
		return this.onsetInTicks;
	}*/

	private void setOnsetFromDenotator(double onset) {
		this.symbolicStart = onset;
		this.onsetInTicks = (int) (onset * JSynPlayer.TICKS_PER_SECOND); //(60.0 / bpm * onset * JSynPlayer.TICKS_PER_SECOND);
	}
	
	public void setOnsetInTicks(int onset) {
		this.onsetInTicks = onset;
	}
	
	public double getSymbolicStart() {
		return this.symbolicStart;
	}

	public double getFrequency() {
		return frequency;
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
	
	/*public int getDurationInTicks() {
		return this.durationInTicks;
	}*/
	
	public double getDuration() {
		return this.duration;
	}

	private void setDuration(double duration) {
		this.symbolicEnd = this.symbolicStart + duration;
		//TODO: why was duration not corrected with TICKS_PER_SECOND???
		this.duration = duration;//60.0 / bpm * duration;
		this.durationInTicks = (int) (duration * JSynPlayer.TICKS_PER_SECOND);
	}
	
	public double getSymbolicEnd() {
		return this.symbolicEnd;
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
		return "(" + this.symbolicStart + " " +this.frequency + " " + this.symbolicEnd + ")";
	}

}

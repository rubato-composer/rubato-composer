package org.rubato.rubettes.bigbang.model.player;

import java.util.ArrayList;
import java.util.List;

public class JSynNote {
	
	private int onset;
	private double frequency;
	private double amplitude;
	private double duration;
	private int voice;
	private double symbolicStart, symbolicEnd;
	private List<JSynModulator> modulators;
	private int bpm;
	
	public JSynNote(List<Double> rubatoValues, int bpm) {
		this.bpm = bpm;
		this.setOnsetFromDenotator(rubatoValues.get(0), bpm);
		this.setFrequency(rubatoValues.get(1));
		this.setAmplitude(rubatoValues.get(2));
		this.setDuration(rubatoValues.get(3), bpm);
		this.setVoice((int) rubatoValues.get(4).doubleValue());
		this.modulators = new ArrayList<JSynModulator>();
	}
	
	public boolean playsAt(double symbolicStart, double symbolicEnd) {
		boolean noIntersection = this.symbolicEnd < symbolicStart || this.symbolicStart > symbolicEnd; 
		return !noIntersection;
	}

	public int getOnset() {
		return onset;
	}

	private void setOnsetFromDenotator(double onset, int bpm) {
		this.symbolicStart = onset;
		this.onset = (int) (60.0 / bpm * onset * JSynPlayer.TICKS_PER_SECOND);
	}
	
	public void setOnset(int onset) {
		this.onset = onset;
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

	public double getDuration() {
		return duration;
	}

	private void setDuration(double duration, int bpm) {
		this.symbolicEnd = this.symbolicStart + duration;
		this.duration = 60.0 / bpm * duration;
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
	
	public JSynModulator addModulator(List<Double> rubatoValues) {
		JSynModulator modulator = new JSynModulator(rubatoValues, this.bpm); 
		this.modulators.add(modulator);
		return modulator;
	}
	
	public List<JSynModulator> getModulators() {
		return this.modulators;
	}
	
	public String toString() {
		return "(" + this.symbolicStart + " " + this.symbolicEnd + ")";
	}

}

package org.rubato.rubettes.bigbang.view.player;

import java.util.Arrays;
import java.util.List;

public class JSynMonitorObject extends JSynObject {
	
	private int voice;
	private double pitch;
	private double loudness;
	
	public JSynMonitorObject(int voice, double pitch, double loudness) {
		super(null, JSynObject.ADDITIVE);
		this.voice = voice;
		this.pitch = pitch;
		this.loudness = loudness;
	}
	
	@Override
	public boolean isAudible() {
		return true;
	}
	
	@Override
	public List<Double> getFrequencies() {
		return Arrays.asList(this.midiToFrequency(this.pitch));
	}
	
	public int getLoudness() {
		return (int)this.loudness;
	}
	
	public int getVoice() {
		return this.voice;
	}
	
	@Override
	public JSynObject clone() {
		return new JSynMonitorObject(this.voice, this.pitch, this.loudness);
	}

}

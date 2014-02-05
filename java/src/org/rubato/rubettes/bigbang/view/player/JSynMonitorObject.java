package org.rubato.rubettes.bigbang.view.player;

import java.util.Arrays;
import java.util.List;

public class JSynMonitorObject extends JSynObject {
	
	private double pitch;
	private double loudness;
	
	public JSynMonitorObject(double pitch, double loudness) {
		super(null, JSynObject.ADDITIVE);
		this.pitch = pitch;
		this.loudness = loudness;
	}
	
	@Override
	public List<Double> getFrequencies() {
		return Arrays.asList(this.midiToFrequency(this.pitch));
	}
	
	public Double getLoudness() {
		return this.midiToAmplitude(this.loudness);
	}
	
	@Override
	public JSynObject clone() {
		return new JSynMonitorObject(this.pitch, this.loudness);
	}

}

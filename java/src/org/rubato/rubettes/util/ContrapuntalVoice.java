package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.yoneda.LimitDenotator;

public class ContrapuntalVoice {
	
	private List<LimitDenotator> notes;
	private double firstOnset;
	private double lastOnset;
	private double lastPitch;
	private double lastDuration;
	
	public ContrapuntalVoice(LimitDenotator note, double onset, double pitch, double duration) {
		this.firstOnset = onset;
		this.notes = new ArrayList<LimitDenotator>();
		this.addNote(note, onset, pitch, duration);
	}
	
	public void addNote(LimitDenotator note, double onset, double pitch, double duration) {
		this.notes.add(note);
		this.lastOnset = onset;
		this.lastPitch = pitch;
		this.lastDuration = duration;
	}
	
	public boolean hasRoomFor(double onset, double duration) {
		return onset >= this.lastOnset+this.lastDuration || (onset == this.lastOnset && duration == this.lastDuration);
	}
	
	public double getFirstOnset() {
		return this.firstOnset;
	}
	
	public double getLastPitch() {
		return this.lastPitch;
	}
	
	public List<LimitDenotator> getNotes() {
		return this.notes;
	}

}

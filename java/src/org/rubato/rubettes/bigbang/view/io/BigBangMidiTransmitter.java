package org.rubato.rubettes.bigbang.view.io;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.ShortMessage;

import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.view.player.JSynObject;
import org.rubato.rubettes.bigbang.view.player.JSynScore;

public class BigBangMidiTransmitter {
	
	private MidiDevice outputDevice;
	private Map<BigBangObject,MidiNoteRepeater> repeaters;
	private Timer timer;
	
	public BigBangMidiTransmitter(MidiDevice outputDevice) {
		this.outputDevice = outputDevice;
		this.repeaters = new HashMap<BigBangObject,MidiNoteRepeater>();
		this.timer = new Timer();
	}
	
	public void clear() {
		for (MidiNoteRepeater currentRepeater : this.repeaters.values()) {
			currentRepeater.end();
		}
		this.repeaters = new TreeMap<BigBangObject,MidiNoteRepeater>();
	}
	
	public synchronized void scheduleNote(JSynObject object, int onset, int duration) {
		if (this.repeaters.containsKey(object.getBigBangObject()) && this.repeaters.get(object.getBigBangObject()).isAlive()) {
			this.repeaters.get(object.getBigBangObject()).update(object);
		} else {
			this.repeaters.put(object.getBigBangObject(), new MidiNoteRepeater(this, object, onset, duration));
		}
	}
	
	public void removeOldRepeaters(JSynScore score) {
		for (BigBangObject currentKey : new HashSet<BigBangObject>(this.repeaters.keySet())) {
			if (!score.contains(currentKey)) {
				//remove and end
				this.repeaters.remove(currentKey).end();
			}
		}
	}
	
	public void mute(JSynObject object) {
		if (this.repeaters.containsKey(object.getBigBangObject())) {
			this.repeaters.get(object.getBigBangObject()).mute();
		}
	}
	
	public void scheduleNoteOn(int channel, int pitch, int velocity, long delay) {
		this.scheduleNoteTask(true, channel, pitch, velocity, delay);
	}
	
	public void scheduleNoteOff(int channel, int pitch, long delay) {
		this.scheduleNoteTask(false, channel, pitch, 0, delay);
	}
	
	private void scheduleNoteTask(boolean noteOn, int channel, int pitch, int velocity, long delay) {
		int command = noteOn ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF;
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(command, channel, pitch, velocity);
			this.timer.schedule(new MidiTimerTask(message, Arrays.asList(this.outputDevice)), delay);
		}  catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

}

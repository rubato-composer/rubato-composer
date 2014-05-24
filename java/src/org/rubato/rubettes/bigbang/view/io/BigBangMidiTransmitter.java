package org.rubato.rubettes.bigbang.view.io;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.ShortMessage;

import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.view.player.JSynObject;
import org.rubato.rubettes.bigbang.view.player.JSynScore;

public class BigBangMidiTransmitter {
	
	//remembers each currently pressed pitch for each device and channel 
	static Map<MidiDevice,Map<Integer,Set<Integer>>> currentNotes = 
			new HashMap<MidiDevice,Map<Integer,Set<Integer>>>();
	//current scheduled noteoff for every device, channel, and pitch 
	static Map<MidiDevice,Map<Integer,Map<Integer,MidiTimerTask>>> currentNoteOffs = 
			new HashMap<MidiDevice,Map<Integer,Map<Integer,MidiTimerTask>>>();
	
	private MidiDevice outputDevice;
	private Map<BigBangObject,MidiNoteRepeater> repeaters;
	private Timer timer;
	
	public BigBangMidiTransmitter(MidiDevice outputDevice) {
		this.outputDevice = outputDevice;
		this.repeaters = new HashMap<BigBangObject,MidiNoteRepeater>();
		if (!currentNotes.containsKey(outputDevice)) {
			currentNotes.put(outputDevice, new TreeMap<Integer,Set<Integer>>());
			currentNoteOffs.put(outputDevice, new TreeMap<Integer,Map<Integer,MidiTimerTask>>());
		}
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
		//System.out.println("MUTOB");
		if (this.repeaters.containsKey(object.getBigBangObject())) {
			this.repeaters.get(object.getBigBangObject()).mute();
		}
	}
	
	public synchronized void scheduleNoteOn(int channel, int pitch, int velocity, long delay) {
		//TODO not good with delay
		Set<Integer> currentPitches = getCurrentPitches(this.outputDevice, channel);
		if (!currentPitches.contains(pitch)) {
			//System.out.println("ON  "+ pitch+" "+delay);
			this.scheduleNoteTask(true, channel, pitch, velocity, delay);
			currentPitches.add(pitch);
		}
	}
	
	public synchronized void scheduleNoteOff(int channel, int pitch, long delay) {
		Set<Integer> currentPitches = getCurrentPitches(this.outputDevice, channel);
		if (currentPitches.contains(pitch)) {
			//System.out.println("OFF "+ pitch+" "+delay);
			this.scheduleNoteTask(false, channel, pitch, 0, delay);
		}
	}
	
	static Set<Integer> getCurrentPitches(MidiDevice outputDevice, int channel) {
		if (!currentNotes.get(outputDevice).containsKey(channel)) {
			currentNotes.get(outputDevice).put(channel, new TreeSet<Integer>());
		}
		return currentNotes.get(outputDevice).get(channel);
	}
	
	private void scheduleNoteTask(boolean noteOn, int channel, int pitch, int velocity, long delay) {
		int command = noteOn ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF;
		ShortMessage message = new ShortMessage();
		try {
			if (0 <= pitch && pitch <= 127 && 0 <= velocity && velocity <= 127) {
				message.setMessage(command, channel, pitch, velocity);
				MidiTimerTask newNoteTask = new MidiTimerTask(message, Arrays.asList(this.outputDevice));
				if (!noteOn) {
					this.replaceNoteOff(channel, pitch, newNoteTask);
				}
				this.timer.schedule(newNoteTask, delay);
			}
		}  catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
	private void replaceNoteOff(int channel, int pitch, MidiTimerTask noteOffTask) {
		if (!currentNoteOffs.get(this.outputDevice).containsKey(channel)) {
			currentNoteOffs.get(this.outputDevice).put(channel, new TreeMap<Integer,MidiTimerTask>());
		}
		MidiTimerTask previousNoteOff = currentNoteOffs.get(this.outputDevice).get(channel).get(pitch);
		if (previousNoteOff != null) {
			previousNoteOff.cancel();
		}
		currentNoteOffs.get(this.outputDevice).get(channel).put(pitch, noteOffTask);
	}

}

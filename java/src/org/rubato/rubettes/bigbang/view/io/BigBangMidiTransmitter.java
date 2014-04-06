package org.rubato.rubettes.bigbang.view.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice.Info;

import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.view.player.JSynObject;
import org.rubato.rubettes.bigbang.view.player.JSynScore;

public class BigBangMidiTransmitter {
	
	private List<MidiDevice> outputDevices;
	private Map<BigBangObject,MidiNoteRepeater> repeaters;
	private Timer timer;
	
	public BigBangMidiTransmitter() {
		this.outputDevices = new ArrayList<MidiDevice>();
		this.repeaters = new HashMap<BigBangObject,MidiNoteRepeater>();
		this.timer = new Timer();
		Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (Info currentInfo : infos) {
			//System.out.println(currentInfo.getName() + " | " + currentInfo.getDescription() + " | " + currentInfo.getVendor() + " | " + currentInfo.getVersion());
			if (currentInfo.getDescription().equals("IAC Driver Bus 1")) {
				this.addOutputDevice(currentInfo);
			}
		}
	}
	
	private void addOutputDevice(Info deviceInfo) {
		try {
			MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);
			//System.out.println(device + " " + device.getMaxReceivers() + " " + device.getMaxTransmitters() + " " + device.getReceivers());
			if (device.getMaxReceivers() != 0) {
				this.outputDevices.add(device);
				device.open();
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
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
		MidiTimerTask noteOnTask = this.createNoteTask(true, channel, pitch, velocity);
		this.timer.schedule(noteOnTask, delay);
	}
	
	public void scheduleNoteOff(int channel, int pitch, long delay) {
		MidiTimerTask noteOffTask = this.createNoteTask(false, channel, pitch, 0);
		this.timer.schedule(noteOffTask, delay);
	}
	
	public MidiTimerTask createNoteTask(boolean noteOn, int channel, int pitch, int velocity) {
		int command = noteOn ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF;
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(command, channel, pitch, velocity);
			return new MidiTimerTask(message, this.outputDevices);
		} catch (InvalidMidiDataException e) {
			return null;
		}
	}
	
	public void close() {
		for (MidiDevice currentDevice : this.outputDevices) {
			currentDevice.close();
		}
	}

}

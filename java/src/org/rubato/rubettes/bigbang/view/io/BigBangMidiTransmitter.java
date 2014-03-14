package org.rubato.rubettes.bigbang.view.io;

import java.util.ArrayList;
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

public class BigBangMidiTransmitter {
	
	private final boolean REITERATE = false;
	
	private List<MidiDevice> outputDevices;
	private Map<Integer,MidiTimerTask> noteOnTasks, noteOffTasks;
	private Timer timer;
	
	public BigBangMidiTransmitter() {
		this.outputDevices = new ArrayList<MidiDevice>();
		this.noteOnTasks = new TreeMap<Integer,MidiTimerTask>();
		this.noteOffTasks = new TreeMap<Integer,MidiTimerTask>();
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
	
	public void scheduleNote(int channel, int pitch, int velocity, int onset, int duration, int rate) {
		//System.out.println( pitch + " " +  velocity + " " +  onset+ " " +  duration);
		this.sendNoteOn(channel, pitch, velocity, onset);
		if (onset+duration > 0) {
			this.sendNoteOff(channel, pitch, onset+duration);
		}
	}
	
	private void sendNoteOn(int channel, int pitch, int velocity, long timeStamp) {
		MidiTimerTask noteOnTask = this.createNoteTask(true, channel, pitch, velocity, timeStamp);
		if (this.noteOnTasks.containsKey(pitch)) {
			//if (this.noteOnTasks.get(pitch).getStartingTime() <
		}
		//TODO REMOVE CONFLICTING TASKS!!
		this.noteOnTasks.put(pitch, noteOnTask);
		this.timer.schedule(noteOnTask, timeStamp);
	}
	
	public void sendNoteOff(int channel, int pitch, long timeStamp) {
		MidiTimerTask noteOffTask = this.createNoteTask(false, channel, pitch, 0, timeStamp);
		//TODO REMOVE CONFLICTING TASKS!!
		this.noteOffTasks.put(pitch, noteOffTask);
		this.timer.schedule(noteOffTask, timeStamp);
	}
	
	private MidiTimerTask createNoteTask(boolean noteOn, int channel, int pitch, int velocity, long timeStamp) {
		int command = noteOn ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF;
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(command, channel, pitch, velocity);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		return new MidiTimerTask(message, this.outputDevices, System.currentTimeMillis()+timeStamp);
	}
	
	public void close() {
		for (MidiDevice currentDevice : this.outputDevices) {
			currentDevice.close();
		}
	}

}

package org.rubato.rubettes.bigbang.view.io;

import java.util.List;
import java.util.TimerTask;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;

public class MidiTimerTask extends TimerTask {
	
	private MidiMessage message;
	private List<MidiDevice> devices;
	private long startingTime;
	
	public MidiTimerTask(MidiMessage message, List<MidiDevice> devices, long startingTime) {
		this.message = message;
		this.devices = devices;
		this.startingTime = startingTime;
	}
	
	public long getStartingTime() {
		return this.startingTime;
	}

	@Override
	public void run() {
		for (MidiDevice currentDevice : this.devices) {
			try {
				//System.out.println(currentDevice.getMicrosecondPosition());
				//timeStamp += currentDevice.getMicrosecondPosition();
				//TIMESTAMPS DON'T WORK, WITH MAC IAS BUS AT LEAST :(
				currentDevice.getReceiver().send(message, -1);
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

}

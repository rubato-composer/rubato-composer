package org.rubato.rubettes.bigbang.view.io;

import java.util.List;
import java.util.TimerTask;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

public class MidiTimerTask extends TimerTask {
	
	private ShortMessage message;
	private List<MidiDevice> devices;
	
	public MidiTimerTask(ShortMessage message, List<MidiDevice> devices) {
		this.message = message;
		this.devices = devices;
	}
	
	public ShortMessage getMessage() {
		return this.message;
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

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
				//System.out.println(" RU "+this.message.getData1() + " " + this.message.getData2());
				currentDevice.getReceiver().send(this.message, -1);
				if (this.message.getCommand() == ShortMessage.NOTE_ON) {
					BigBangMidiTransmitter.getCurrentPitches(currentDevice, this.message.getChannel())
						.add(message.getData1());
				} else if (this.message.getCommand() == ShortMessage.NOTE_OFF) {
					BigBangMidiTransmitter.getCurrentPitches(currentDevice, this.message.getChannel())
						.remove(message.getData1());
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

}

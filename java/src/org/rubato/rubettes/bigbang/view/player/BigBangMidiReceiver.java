package org.rubato.rubettes.bigbang.view.player;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice.Info;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class BigBangMidiReceiver implements Receiver {
	
	private ViewController controller;
	
	public BigBangMidiReceiver(ViewController controller) {
		this.controller = controller;
		Info[] infos = MidiSystem.getMidiDeviceInfo();
		try {
			MidiDevice inputDevice = MidiSystem.getMidiDevice(infos[0]);
			inputDevice.open();
			inputDevice.getTransmitter().setReceiver(this);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void send(MidiMessage message, long timeStamp) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage)message;
			if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
				int pitch = shortMessage.getData1();
				int velocity = shortMessage.getData2();
				if (velocity == 0) {
					this.controller.releaseMidiKey(pitch);
				} else {
					this.controller.pressMidiKey(pitch, velocity);
				}
			} else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
				int pitch = shortMessage.getData1();
				this.controller.releaseMidiKey(pitch);
			}
		}
	}
	
	public void close() {
		// TODO Auto-generated method stub
	}

}

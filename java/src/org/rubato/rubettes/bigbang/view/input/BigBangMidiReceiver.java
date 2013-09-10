package org.rubato.rubettes.bigbang.view.input;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice.Info;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class BigBangMidiReceiver implements Receiver {
	
	private final int EWI_BREATH_SENSOR_INDEX = 2;
	private ViewController controller;
	private List<MidiDevice> inputDevices;
	
	public BigBangMidiReceiver(ViewController controller) {
		this.controller = controller;
		this.inputDevices = new ArrayList<MidiDevice>();
		Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (Info currentInfo : infos) {
			//System.out.println(currentInfo.getName() + " " + currentInfo.getDescription() + " " + currentInfo.getVendor() + " " + currentInfo.getVersion());
			if (currentInfo.getVendor().equals("E-MU Systems, Inc.") || currentInfo.getVendor().equals("M-Audio")) {
				this.addInputDevice(currentInfo);
			}
		}
	}
	
	private void addInputDevice(Info deviceInfo) {
		try {
			MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);
			if (device.getMaxTransmitters() != 0) {
				this.inputDevices.add(device);
				device.open();
				device.getTransmitter().setReceiver(this);
			}
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
			} else if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
				int controlChangeNumber = shortMessage.getData1();
				int controlChangeValue = shortMessage.getData2();
				int emuKnobIndex = this.getEmuKnobIndex(controlChangeNumber);
				if (emuKnobIndex >= 0) {
					this.controller.modifyOperation(emuKnobIndex, controlChangeValue);
				} else if (controlChangeNumber == this.EWI_BREATH_SENSOR_INDEX) {
					this.controller.changeVelocity(controlChangeValue);
				}
				//System.out.println(shortMessage.getCommand() + " " + shortMessage.getData1() + " " + shortMessage.getData2());
			}
		}
	}
	
	private int getEmuKnobIndex(int controlChangeNumber) {
		switch (controlChangeNumber) {
			case 21: return 0;
			case 22: return 1;
			case 23: return 2;
			case 24: return 3;
			case 25: return 4;
			case 26: return 5;
			case 27: return 6;
			case 28: return 7;
			case 70: return 8;
			case 71: return 9;
			case 72: return 10;
			case 73: return 11;
			case 91: return 12;
			case 93: return 13;
			case 82: return 14;
			case 83: return 15;
			default: return -1;
		}
	}
	
	public void close() {
		for (MidiDevice currentDevice : this.inputDevices) {
			currentDevice.close();
		}
	}

}

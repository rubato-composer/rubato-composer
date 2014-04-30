package org.rubato.rubettes.bigbang.view.io;

import java.util.Map;
import java.util.TreeMap;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice.Info;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class BigBangMidiReceiver implements Receiver {
	
	private static Map<String,MidiDevice> midiInDevices;
	
	private final int EWI_BREATH_SENSOR_INDEX = 2;
	private ViewController controller;
	private String selectedInDeviceName;
	
	public BigBangMidiReceiver(ViewController controller) {
		this.controller = controller;
		if (midiInDevices.size() > 0) {
			this.setSelectedInDevice(midiInDevices.keySet().iterator().next());
		}
	}
	
	public void setSelectedInDevice(String inDeviceName) {
		if (this.selectedInDeviceName != null) {
			MidiDevice inDevice = midiInDevices.get(this.selectedInDeviceName);
			if (inDevice != null) {
				inDevice.close();
			}
		}
		this.selectedInDeviceName = inDeviceName;
		try {
			MidiDevice inDevice = midiInDevices.get(this.selectedInDeviceName);
			if (inDevice != null) {
				inDevice.open();
				inDevice.getTransmitter().setReceiver(this);
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void send(MidiMessage message, long timeStamp) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage)message;
			if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
				int channel = shortMessage.getChannel();
				int pitch = shortMessage.getData1();
				int velocity = shortMessage.getData2();
				if (velocity == 0) {
					this.controller.releaseMidiKey(channel, pitch);
				} else {
					this.controller.pressMidiKey(channel, pitch, velocity);
				}
			} else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
				int channel = shortMessage.getChannel();
				int pitch = shortMessage.getData1();
				this.controller.releaseMidiKey(channel, pitch);
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
	
	public String getSelectedInDeviceName() {
		return this.selectedInDeviceName;
	}

	@Override
	public void close() {
		midiInDevices.get(this.selectedInDeviceName).close();
	}
	
	
	//static device management...
	
	static {
		midiInDevices = new TreeMap<String,MidiDevice>();
		//add empty selection
		midiInDevices.put(" ", null);
		
		for (Info currentInfo : MidiSystem.getMidiDeviceInfo()) {
			//System.out.println(currentInfo.getName() + " " + currentInfo.getDescription() + " " + currentInfo.getVendor() + " " + currentInfo.getVersion());
			//if (currentInfo.getVendor().equals("E-MU Systems, Inc.") || currentInfo.getVendor().equals("M-Audio")) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice(currentInfo);
				if (device.getMaxTransmitters() != 0) {
					midiInDevices.put(getDeviceString(currentInfo), device);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getDeviceString(Info deviceInfo) {
		return deviceInfo.getName() + " (" + deviceInfo.getDescription() + ")";
	}
	
	public static String[] getMidiInDeviceNames() {
		return midiInDevices.keySet().toArray(new String[midiInDevices.size()]);
	}

}

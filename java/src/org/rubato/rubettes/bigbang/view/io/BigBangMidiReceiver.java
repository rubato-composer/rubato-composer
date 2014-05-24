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

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class BigBangMidiReceiver implements Receiver {
	
	private static final String EMU_NAME = "";
	private static final String LAUNCHKEYMINI_NAME = "LK Mini MIDI (Launchkey Mini LK Mini MIDI)";
	private static final String NANOKONTROL2_NAME = "SLIDER/KNOB (nanoKONTROL2 SLIDER/KNOB)";
	private static final String EWI_NAME = "";
	private static final String PUSH_NAME = "Live Port (Ableton Push Live Port)";
	private static final boolean AFTERTOUCH_ON = false;
	private static final boolean VELOCITY_ON = true;
	private static final int EWI_BREATH_SENSOR_INDEX = 2;
	
	private static Map<String,MidiDevice> midiInDevices;
	
	private BigBangController bbController;
	private ViewController controller;
	private String selectedInDeviceName;
	
	public BigBangMidiReceiver(ViewController controller, BigBangController bbController) {
		this.controller = controller;
		this.bbController = bbController;
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
			System.out.println(shortMessage.getChannel() + " " + shortMessage.getCommand() + " " +shortMessage.getData1() + " " + shortMessage.getData2());
			if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
				int channel = shortMessage.getChannel();
				int pitch = shortMessage.getData1();
				int velocity = shortMessage.getData2();
				if (velocity == 0) {
					this.controller.releaseMidiKey(channel, pitch);
				} else {
					//push knobs and strip send note on!?
					if (!this.selectedInDeviceName.equals(PUSH_NAME) || pitch > 12) {
						if (!VELOCITY_ON) {
							this.controller.pressMidiKey(channel, pitch, 1);
						} else {
							this.controller.pressMidiKey(channel, pitch, velocity);
						}
					}
				}
			} else if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
				int channel = shortMessage.getChannel();
				int pitch = shortMessage.getData1();
				this.controller.releaseMidiKey(channel, pitch);
			} else if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
				int controlChangeNumber = shortMessage.getData1();
				int controlChangeValue = shortMessage.getData2();
				if (this.selectedInDeviceName.equals(EMU_NAME)) {
					this.controller.modifyOperation(this.getEmuKnobIndex(controlChangeNumber), controlChangeValue);
				} else if (this.selectedInDeviceName.equals(LAUNCHKEYMINI_NAME)) {
					if (21 <= controlChangeNumber && controlChangeNumber <= 28) {
						this.controller.modifyOperation(controlChangeNumber-21, controlChangeValue);
					} else if (controlChangeNumber == 106 && controlChangeValue == 127) {
						this.controller.selectPreviousCompositionState();
					} else if (controlChangeNumber == 107 && controlChangeValue == 127) {
						this.controller.selectNextCompositionState();
					}
				} else if (this.selectedInDeviceName.equals(NANOKONTROL2_NAME)) {
					this.controller.modifyOperation(this.getNanoKontrol2Index(controlChangeNumber), controlChangeValue);
				} else if (this.selectedInDeviceName.equals(EWI_NAME)) {
					if (controlChangeNumber == EWI_BREATH_SENSOR_INDEX) {
						this.controller.changeVelocity(controlChangeValue);
					}
				} else {
					this.controller.modifyOperation(controlChangeNumber, controlChangeValue);
				}
			} else if (shortMessage.getCommand() == ShortMessage.CHANNEL_PRESSURE && AFTERTOUCH_ON) {
				this.controller.changeVelocity(shortMessage.getData1());
			} else if (shortMessage.getCommand() == ShortMessage.PITCH_BEND) {
				double bendValue = ((double)shortMessage.getData2())/128;
				bendValue += ((double)shortMessage.getData1())/(128*128);
				//ignore jumping back to neutral
				if (bendValue != 0.5) {
					this.bbController.setAnimationPosition(bendValue);
				}
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
	
	private int getLaunchkeyMiniIndex(int controlChangeNumber) {
		switch (controlChangeNumber) {
			case 21: return 0;
			case 22: return 1;
			case 23: return 2;
			case 24: return 3;
			case 25: return 4;
			case 26: return 5;
			case 27: return 6;
			case 28: return 7;
			default: return controlChangeNumber;
		}
	}
	
	private int getNanoKontrol2Index(int controlChangeNumber) {
		switch (controlChangeNumber) {
			case 0: return 0;
			case 1: return 1;
			case 2: return 2;
			case 3: return 3;
			case 4: return 4;
			case 5: return 5;
			case 6: return 6;
			case 7: return 7;
			case 16: return 8;
			case 17: return 9;
			case 18: return 10;
			case 19: return 11;
			case 20: return 12;
			case 21: return 13;
			case 22: return 14;
			case 23: return 15;
			default: return controlChangeNumber;
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

package org.rubato.rubettes.bigbang.view.io;

import org.rubato.rubettes.bigbang.view.player.JSynObject;

public class MidiNoteRepeater extends Thread {
	
	public static final long STANDARD_RATE = 130;
	
	private BigBangMidiTransmitter transmitter;
	private JSynObject object;
	private long onset;
	private long duration;
	
	public MidiNoteRepeater(BigBangMidiTransmitter transmitter, JSynObject object, int onset, int duration) {
		this.transmitter = transmitter;
		this.object = object;
		this.onset = onset;
		this.duration = duration;
		this.start();
	}
	
	public void update(JSynObject object) {
		this.object = object;
	}
	
	public void run() {
		//oneshot:
		if (this.object.getRate() <= 0) {
			this.scheduleNotes();
		//repeated:
		} else {
			while (true) {
				long rate = this.object.getRate();
				if (rate > 0) {
					this.scheduleNotes();
					try { Thread.sleep(rate); } catch (InterruptedException e) { break; }
				} else {
					//if no rate then just wait until there is one again...
					try { Thread.sleep(100); } catch (InterruptedException e) { break;}
				}
			}
		}
	}
	
	private void scheduleNotes() {
		long onset = Math.max(this.onset, 0);
		for (double currentFrequency : this.object.getFrequencies()) {
			int currentPitch = this.object.frequencyToMidi(currentFrequency);
			this.transmitter.scheduleNoteOn(this.object.getVoice(), currentPitch, this.object.getLoudness(), onset);
			if (this.duration > 0 && (this.duration < this.object.getRate() || this.object.getRate() <= 0)) {
				this.transmitter.scheduleNoteOff(this.object.getVoice(), currentPitch, onset+this.duration);
			} else if (this.object.getRate() > 0) {
				long duration = Math.round(this.object.getRate()*0.9);
				this.transmitter.scheduleNoteOff(this.object.getVoice(), currentPitch, duration);
			}
		}
	}
	
	public void mute() {
		//System.out.println("MUTE "+ this.object.frequencyToMidi(this.object.getFrequencies().get(0)));
		for (double currentFrequency : this.object.getFrequencies()) {
			int currentPitch = this.object.frequencyToMidi(currentFrequency);
			this.transmitter.scheduleNoteOff(this.object.getVoice(), currentPitch, 0);
		}
	}
	
	public void end() {
		this.mute();
		this.interrupt();
	}

}

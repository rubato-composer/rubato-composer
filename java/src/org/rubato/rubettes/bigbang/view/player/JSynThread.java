package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class JSynThread extends Thread {
	
	private JSynPlayer player;
	private JSynThreadGroup group;
	private JSynModule module;
	
	private List<JSynObject> objects;
	private Iterator<JSynObject> objectIterator;
	private int voice;
	
	public JSynThread(JSynPlayer player, JSynObject object) {
		this.player = player;
		this.objects = new ArrayList<JSynObject>();
		this.voice = object.getVoice();
		this.addObject(object);
	}
	
	public void setGroup(JSynThreadGroup group) {
		this.group = group;
	}
	
	public void addObject(JSynObject object) {
		this.objects.add(object);
	}
	
	public JSynObject getObjectAt(double time) {
		for (JSynObject currentObject : this.objects) {
			if (currentObject.getOnset() <= time && currentObject.getOnset()+currentObject.getDuration() >= time) {
				return currentObject;
			}
		}
		return null;
	}
	
	public void setModule(JSynModule module) {
		this.module = module;
	}
	
	public JSynModule getModule() {
		return this.module;
	}
	
	public int getVoice() {
		return this.voice;
	}
	
	public boolean playsAt(double symbolicStart, double symbolicEnd) {
		for (JSynObject currentNote : this.objects) {
			//due to ordering every following note is later
			if (currentNote.playsAt(symbolicStart, symbolicEnd)) {
				return true;
			}
		}
		return false;
	}

	public void playNotes() throws InterruptedException {
		
		this.objectIterator = this.objects.iterator();
		JSynObject nextNote = this.iterateThroughPastNotesAndAdjustCurrent();
		if (nextNote != null) {
			double nextOnset = nextNote.getOnset();
			
			// try to start in sync TODO: MAYBE PUT BACK TO JSYNPLAYER??
			this.player.getSynth().sleepUntil(this.player.getSynthTime(nextNote.getOnset()) - JSynPlayer.DEFAULT_ADVANCE);
			
			while(this.group.isRunning()) {
				
				/* Play a note at the specified time. */
				this.module.playOrAdjustObject(nextNote);
				
				if (objectIterator.hasNext()) {
					nextNote = objectIterator.next();
					
					/* Advance nextTime by fixed amount. */
					nextOnset = nextNote.getOnset();
					/* sleep until advanceTime BEFORE we have to play the next note */
					this.player.getSynth().sleepUntil(this.player.getSynthTime(nextOnset) - JSynPlayer.DEFAULT_ADVANCE);
				} else {
					break;
				}
			}
		}
	}
	
	/*
	 * iterates through all past notes, adjusts the currently playing one, and returns the next note to be played,
	 * null if there is none.
	 */
	private JSynObject iterateThroughPastNotesAndAdjustCurrent() {
		boolean foundOneToAdjust = false;
		while (this.objectIterator.hasNext()) {
			JSynObject currentNote = this.objectIterator.next();
			if (currentNote.getOnset() < this.player.getCurrentSymbolicTime()) {
				this.module.playOrAdjustObject(currentNote);
				foundOneToAdjust = true;
			} else {
				if (!foundOneToAdjust) {
					this.module.mute();
				}
				return currentNote;
			}
		}
		return null;
	}
	
	/* real-time task for thread */
	public void run() {
		try {
			this.playNotes();
		} catch (InterruptedException e) {
			if (this.group.isRunning()) {
				this.run();
			}
		}
	}
	
	public String toString() {
		String representation = "[";
		for (JSynObject currentNote: this.objects) {
			representation += currentNote.toString() + ", ";
		}
		representation = representation.substring(0, representation.length()-2);
		representation += "]";
		return representation;
	}
	
}
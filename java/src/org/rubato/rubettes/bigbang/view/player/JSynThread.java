package org.rubato.rubettes.bigbang.view.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class JSynThread extends Thread {
	
	private JSynPlayer player;
	private JSynThreadGroup group;
	private JSynModule module;
	
	private List<JSynObject> notes;
	private Iterator<JSynObject> noteIterator;
	private int voice;
	
	public JSynThread(JSynPlayer player, JSynObject note) {
		this.player = player;
		this.notes = new ArrayList<JSynObject>();
		this.voice = note.getVoice();
		this.addNote(note);
	}
	
	public void setGroup(JSynThreadGroup group) {
		this.group = group;
	}
	
	public void addNote(JSynObject note) {
		this.notes.add(note);
	}
	
	public JSynObject getNoteAt(double time) {
		for (JSynObject currentObject : this.notes) {
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
		for (JSynObject currentNote : this.notes) {
			//due to ordering every following note is later
			if (currentNote.playsAt(symbolicStart, symbolicEnd)) {
				return true;
			}
		}
		return false;
	}
	
	private void playNote(JSynObject note) {
		 this.module.playNote(note);
	}
	
	private void adjustCurrentlyPlayingNote(JSynObject note) {
		double remainingDuration = note.getDuration() - (this.player.getSynthesizer().getCurrentTime()-note.getOnset());
		this.module.modifyNote(note, remainingDuration);
	}

	public void playNotes() throws InterruptedException {
		
		this.noteIterator = this.notes.iterator();
		JSynObject nextNote = this.iterateThroughPastNotesAndAdjustCurrent();
		if (nextNote != null) {
			double nextOnset = nextNote.getOnset();
			
			// try to start in sync
			this.player.getSynthesizer().sleepUntil(nextNote.getOnset() - JSynPlayer.DEFAULT_ADVANCE);
			
			while(this.group.isRunning()) {
				
				/* Play a note at the specified time. */
				this.playNote(nextNote);
				
				if (noteIterator.hasNext()) {
					nextNote = noteIterator.next();
					
					/* Advance nextTime by fixed amount. */
					nextOnset = nextNote.getOnset();
					/* sleep until advanceTime BEFORE we have to play the next note */
					this.player.getSynthesizer().sleepUntil(nextOnset  - JSynPlayer.DEFAULT_ADVANCE);
				} else {
					this.player.getSynthesizer().sleepUntil(nextOnset + nextNote.getDuration() + .5);
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
		while (this.noteIterator.hasNext()) {
			JSynObject currentNote = this.noteIterator.next();
			if (currentNote.getOnset() < this.player.getSynthesizer().getCurrentTime()) {
				//if a note is playing now and should be replaced by another
				if (currentNote.getOnset()+currentNote.getDuration() > this.player.getSynthesizer().getCurrentTime()) {
					this.adjustCurrentlyPlayingNote(currentNote);
				}
			} else return currentNote;
		}
		return null;
	}
	
	/* real-time task for thread */
	public void run() {
		try {
			this.playNotes();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String representation = "[";
		for (JSynObject currentNote: this.notes) {
			representation += currentNote.toString() + ", ";
		}
		representation = representation.substring(0, representation.length()-2);
		representation += "]";
		return representation;
	}
	
}
package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;

public class VoiceSplitter {
	
	List<ContrapuntalVoice> contrapuntalVoices;
	private double firstOnset;
	private SoundNoteGenerator noteGenerator;
	
	public VoiceSplitter(PowerDenotator score) {
		this.noteGenerator = new SoundNoteGenerator();
		this.splitContrapuntalVoices(score);
	}
	
	private void splitContrapuntalVoices(PowerDenotator score) {
    	Iterator<Denotator> notes = score.iterator();
    	this.contrapuntalVoices = new ArrayList<ContrapuntalVoice>();
    	
    	while (notes.hasNext()) {
    		LimitDenotator currentNote = (LimitDenotator) notes.next();
			double currentOnset = this.noteGenerator.getDoubleValue(currentNote, new int[]{0,0});
			double currentPitch = this.noteGenerator.getDoubleValue(currentNote, new int[]{1,0});
			double currentDuration = this.noteGenerator.getDoubleValue(currentNote, new int[]{3,0});
			this.firstOnset = Math.min(currentOnset, this.firstOnset);
			TreeMap<Double,ContrapuntalVoice> candidates = new TreeMap<Double,ContrapuntalVoice>();
			for (ContrapuntalVoice currentVoice : contrapuntalVoices) {
				if (currentVoice.hasRoomFor(currentOnset, currentDuration)) {
					double pitchDistance = Math.abs(currentVoice.getLastPitch()-currentPitch);
					if (!candidates.keySet().contains(pitchDistance)) {
						candidates.put(pitchDistance, currentVoice);
					}
				}
			}
			if (candidates.size() > 0) {
				double smallestPitchDistance = new ArrayList<Double>(candidates.keySet()).get(0);
				candidates.get(smallestPitchDistance).addNote(currentNote, currentOnset, currentPitch, currentDuration);
			} else {
				contrapuntalVoices.add(new ContrapuntalVoice(currentNote, currentOnset, currentPitch, currentDuration));
			}
		}
    	Collections.reverse(this.contrapuntalVoices);
    }
	
	public List<ContrapuntalVoice> getVoices() {
		return this.contrapuntalVoices;
	}
	
	public double getFirstOnset() {
		return this.firstOnset;
	}

}

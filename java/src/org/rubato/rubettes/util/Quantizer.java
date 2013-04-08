package org.rubato.rubettes.util;

import java.util.Iterator;

import org.rubato.composer.RunInfo;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;

public class Quantizer {
	
	private double timeUnit, pitchUnit;
	private SoundNoteGenerator noteGenerator;
	
	public Quantizer(double timeUnit, double pitchUnit) {
		this.timeUnit = timeUnit;
		this.pitchUnit = pitchUnit;
		this.noteGenerator = new SoundNoteGenerator();
	}
	
	public PowerDenotator getQuantizedScore(PowerDenotator score, RunInfo runInfo) {
		Iterator<Denotator> notes = score.iterator();
		while (notes.hasNext()) {
			LimitDenotator currentNote = (LimitDenotator) notes.next();
			this.replaceElementRoundedToUnit(currentNote, new int[]{0,0}, this.timeUnit);
			this.replaceElementRoundedToUnit(currentNote, new int[]{3,0}, this.timeUnit);
			this.replaceElementRoundedToUnit(currentNote, new int[]{1,0}, this.pitchUnit);
			if (runInfo.stopped()) {
				break;
			}
		}
		return score;
	}
	
	private void replaceElementRoundedToUnit(LimitDenotator noteDenotator, int[] elementPath, double unit) {
    	double elementValue = this.noteGenerator.getDoubleValue(noteDenotator, elementPath);
    	elementValue = Math.round(elementValue/unit)*unit;
    	this.noteGenerator.modifyNoteDenotator(noteDenotator, elementPath, elementValue);
    }

}

/*
 * Copyright (C) 2006 Florian Thalmann
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package org.rubato.rubettes.score;

import java.util.Iterator;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.*;
import org.rubato.rubettes.util.NoteGenerator;
import org.rubato.rubettes.util.MacroNoteGenerator;

/**
 * A rubette for providing each note of an input Score denotators with new rhythmic quality,
 * i.e. new onset, duration and loudness. The output is a monophonic Score denotator.
 * 
 * @author Florian Thalmann
 */
public class RhythmizeRubette extends SimpleAbstractRubette {
	
	private int numberOfNoteValues;
	private double maxNoteValue;
	private double timeBase;
	private double emphasizingPeriod;
	private int emphasizedLoudness;
	private int unemphasizedLoudness;
	
	private final String numberOfNoteValuesKey = "numberOfNoteValues";
	private final String maxNoteValueKey = "maxNoteValue";
	private final String timeBaseKey = "timeBase";
	private final String emphasizingPeriodKey = "emphasizingPeriod";
	private final String emphasizedLoudnessKey = "emphasizedLoudness";
	private final String unemphasizedLoudnessKey = "unemphasizedLoudness";
	
	private NoteGenerator noteGenerator;
	
	private static final ImageIcon icon = Icons.loadIcon(RhythmizeRubette.class, "rhythmizeicon.png");
	
	/**
	 * Creates a basic RhythmizeRubette.
	 */
    public RhythmizeRubette() {
        this.setInCount(1);
        this.setOutCount(1);
        this.putProperty(new IntegerProperty(this.numberOfNoteValuesKey, "Number of note values", 3, 1, 1000000));
        this.putProperty(new DoubleProperty(this.timeBaseKey, "Value base", 2, 0, 1000000));
        this.putProperty(new DoubleProperty(this.maxNoteValueKey, "Maximal value exponent", 1, 0, 1000000));
        this.putProperty(new DoubleProperty(this.emphasizingPeriodKey, "Meter period", 2, 0, 1000000));
        this.putProperty(new IntegerProperty(this.emphasizedLoudnessKey, "Loudness of accented notes", 120));
        this.putProperty(new IntegerProperty(this.unemphasizedLoudnessKey, "Loudness of unaccented notes", 80));
        this.noteGenerator = new MacroNoteGenerator();
    }

    public void run(RunInfo runInfo) {
		PowerDenotator score = (PowerDenotator) this.getInput(0).copy();
		this.readProperties();
		
		try {
			Iterator<Denotator> notes = score.iterator();
			
			double onset = 0.0;
			double duration = 1.0;
			int loudness;
			double durationCount = 0.0;
			while (notes.hasNext()) {
				double randomValue = Math.random() * this.numberOfNoteValues;
				randomValue = randomValue - this.numberOfNoteValues + this.maxNoteValue;
				duration = Math.pow(this.timeBase,(int)Math.floor(randomValue));
				LimitDenotator currentNote = (LimitDenotator) notes.next();
				if (this.emphasizingPeriod > 0) {
					if (durationCount == 0) {
						loudness = this.emphasizedLoudness;
					} else {
						loudness = this.unemphasizedLoudness;
					}
					durationCount += duration;
					if (durationCount >= this.emphasizingPeriod) {
						durationCount -= duration;
						duration = this.emphasizingPeriod - durationCount;
						durationCount = 0.0;
					}
					this.noteGenerator.modifyNoteDenotator(currentNote, onset, loudness, duration);
				} else {
					this.noteGenerator.modifyNoteDenotator(currentNote, onset, duration);
				}
				onset += duration;
				if (runInfo.stopped()) {
					break;
				}
			}
		} catch(RubatoException e) {}
		
		this.setOutput(0, score);
	}
	
	private void readProperties() {
    	this.numberOfNoteValues = ((IntegerProperty)this.getProperty(this.numberOfNoteValuesKey)).getInt();
    	this.maxNoteValue = ((DoubleProperty)this.getProperty(this.maxNoteValueKey)).getDouble();
    	this.timeBase = ((DoubleProperty)this.getProperty(this.timeBaseKey)).getDouble();
    	this.emphasizingPeriod = ((DoubleProperty)this.getProperty(this.emphasizingPeriodKey)).getDouble();
    	this.emphasizedLoudness = ((IntegerProperty)this.getProperty(this.emphasizedLoudnessKey)).getInt();
    	this.unemphasizedLoudness = ((IntegerProperty)this.getProperty(this.unemphasizedLoudnessKey)).getInt();
    }
    
    public String getGroup() {
        return "Score";
    }
    
    public String getName() {
        return "Rhythmize";
    }
	
    public String getShortDescription() {
        return "Rhytmizes a score denotator";
    }
    
    public ImageIcon getIcon() {
        return icon;
    }

    public String getLongDescription() {
        return "The Rhythmize rubette rhythmizes a score denotator"+
               " randomly and emphasizes its notes";
    }

    public String getInTip(int i) {
        return "Input score denotator";
    }

    public String getOutTip(int i) {
        return "Output score denotator";
    }
}

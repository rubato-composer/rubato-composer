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

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.icons.Icons;
import org.rubato.rubettes.util.NoteGenerator;
import org.rubato.rubettes.util.MacroNoteGenerator;

/**
 * A rubette for generating random melodies according to specified criteria. The output denotators
 * are of the form Score.
 * 
 * @author Florian Thalmann
 */
public class MelodyRubette extends SimpleAbstractRubette {
	
	private NoteGenerator noteGenerator;
	
	private int numberOfNotes, startingPitch, minPitch, maxPitch,
		minIntervalDown, maxIntervalDown, minIntervalUp, maxIntervalUp;
	private double step;
	
	private final String numberOfNotesKey = "numberOfNotes";
	private final String startingPitchKey = "startingPitch";
	private final String minPitchKey = "minPitch";
	private final String maxPitchKey = "maxPitch";
	private final String stepKey = "step";
	private final String minIntDownKey = "minDown";
	private final String maxIntDownKey = "maxDown";
	private final String minIntUpKey = "minUp";
	private final String maxIntUpKey = "maxUp";
	
	private static final ImageIcon icon = Icons.loadIcon(MelodyRubette.class, "melodyicon.png");
	
	/**
	 * Creates a basic MelodyRubette.
	 */
	public MelodyRubette() {
        this.setInCount(0);
        this.setOutCount(1);
        this.noteGenerator = new MacroNoteGenerator();
        this.putProperty(new IntegerProperty(this.numberOfNotesKey, "Number of notes", 10, 1, 1000000));
        this.putProperty(new IntegerProperty(this.startingPitchKey, "Starting pitch", 60));
        this.putProperty(new IntegerProperty(this.minPitchKey, "Lowest pitch", 0));
        //this.addRelation(this.minPitchKey, this.startingPitchKey, SimpleAbstractRubette.LEQ);
        this.putProperty(new IntegerProperty(this.maxPitchKey, "Highest pitch", 127));
        //this.addRelation(this.startingPitchKey, this.maxPitchKey, SimpleAbstractRubette.LEQ);
        //this.addRelation(this.minPitchKey, this.maxPitchKey, SimpleAbstractRubette.LEQ);
        this.putProperty(new DoubleProperty(this.stepKey, "Step size (halftone = 1)", 1, 0, 12));
        this.putProperty(new IntegerProperty(this.minIntDownKey, "Minimum interval down", 1, 0, 1000000));
        this.putProperty(new IntegerProperty(this.maxIntDownKey, "Maximum interval down", 6, 0, 1000000));
        //this.addRelation(this.minIntDownKey, this.maxIntDownKey, SimpleAbstractRubette.LEQ);
        this.putProperty(new IntegerProperty(this.minIntUpKey, "Minimum interval up", 1, 0, 1000000));
        this.putProperty(new IntegerProperty(this.maxIntUpKey, "Maximum interval up", 6, 0, 1000000));
        //this.addRelation(this.minIntUpKey, this.maxIntUpKey, SimpleAbstractRubette.LEQ);
    }
	
	public void init() { }

    public void run(RunInfo runInfo) {
    	this.readProperties();
    	this.noteGenerator.startNewMelody(1);
    	double currentPitch = this.startingPitch;
		for (int i = 0; i < this.numberOfNotes; i++) {
			this.noteGenerator.addNoteToMelody(currentPitch);
			currentPitch = this.getNextPitch(currentPitch);
			if (runInfo.stopped()) {
				break;
			}
		}        
		this.setOutput(0, this.noteGenerator.createScoreWithMelody());
    }
    
    private void readProperties() {
    	this.numberOfNotes = ((IntegerProperty)this.getProperty(this.numberOfNotesKey)).getInt();
    	this.startingPitch = ((IntegerProperty)this.getProperty(this.startingPitchKey)).getInt();
    	this.maxPitch = ((IntegerProperty)this.getProperty(this.maxPitchKey)).getInt();
    	this.minPitch = ((IntegerProperty)this.getProperty(this.minPitchKey)).getInt();
    	this.step = ((DoubleProperty)this.getProperty(this.stepKey)).getDouble();
    	this.minIntervalDown = ((IntegerProperty)this.getProperty(this.minIntDownKey)).getInt();
    	this.maxIntervalDown = ((IntegerProperty)this.getProperty(this.maxIntDownKey)).getInt();
    	this.minIntervalUp = ((IntegerProperty)this.getProperty(this.minIntUpKey)).getInt();
    	this.maxIntervalUp = ((IntegerProperty)this.getProperty(this.maxIntUpKey)).getInt();
    }
	
	private double getNextPitch(double currentPitch) {
		int currentMaxUp = (int)Math.min((this.maxPitch-currentPitch)/this.step, this.maxIntervalUp);
		int currentMaxDown = (int)Math.min((currentPitch-this.minPitch)/this.step, this.maxIntervalDown);
		int totalIntervalsUp = Math.max(currentMaxUp-this.minIntervalUp+1, 0);
		int totalIntervalsDown = Math.max(currentMaxDown-this.minIntervalDown+1, 0);
		int selectedInterval = (int) Math.floor(Math.random()*(totalIntervalsUp+totalIntervalsDown));
		
		int mappedInterval;
		if (totalIntervalsDown > 0) {
			mappedInterval = -1*currentMaxDown + selectedInterval;
			if (selectedInterval > totalIntervalsDown-1) {
				mappedInterval += this.minIntervalDown + this.minIntervalUp - 1;
			}
		} else {
			mappedInterval = selectedInterval + this.minIntervalUp;
		}
		return currentPitch + this.step*mappedInterval;
	}
	
	public JComponent getProperties() {
		JComponent properties = super.getProperties();
		return properties;
	}
    
    public String getGroup() {
        return "Score";
    }

    public String getName() {
        return "Melody";
    }
    
    public String getShortDescription() {
        return "Outputs a score denotator";
    }
    
    public ImageIcon getIcon() {
        return icon;
    }

    public String getLongDescription() {
        return "The Melody Rubette generates a simple random melody"+
               " and and outputs it as a score denotator";
    }

    public String getOutTip(int i) {
        return "Output score denotator";
    }
    
}
/*
 * Copyright (C) 2013 Florian Thalmann
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
import org.rubato.math.yoneda.*;
import org.rubato.rubettes.util.Quantizer;
import org.rubato.rubettes.util.SoundNoteGenerator;

/**
 * A rubette that quantizes a Score denotator according to the given time and pitch units. Rounds onset, pitch,
 * and duration to the closest values fitting the respective units
 * 
 * @author Florian Thalmann
 */
public class QuantizeRubette extends SimpleAbstractRubette {
	
	private final String timeUnitKey = "timeUnit";
	private final String pitchUnitKey = "pitchUnit";
	
	private static final ImageIcon icon = Icons.loadIcon(QuantizeRubette.class, "quantizeicon.png");
	
	/**
	 * Creates a basic RhythmizeRubette.
	 */
    public QuantizeRubette() {
        this.setInCount(1);
        this.setOutCount(1);
        this.putProperty(new DoubleProperty(this.timeUnitKey, "Time unit", .25, -1000000, 1000000));
        this.putProperty(new DoubleProperty(this.pitchUnitKey, "Pitch unit", 1, -1000000, 1000000));
    }

    public void run(RunInfo runInfo) {
    	Denotator input = this.getInput(0).copy();
    	if (input == null) {
            this.addError("Input denotator is null.");
    	} else if (input.hasForm(new SoundNoteGenerator().getScoreForm())) {
    		double timeUnit = ((DoubleProperty)this.getProperty(this.timeUnitKey)).getDouble();
        	double pitchUnit = ((DoubleProperty)this.getProperty(this.pitchUnitKey)).getDouble();
        	Quantizer quantizer = new Quantizer(timeUnit, pitchUnit);
    		this.setOutput(0, quantizer.getQuantizedScore((PowerDenotator)input, runInfo));
    	} else {
    		this.addError("Input denotator is not of form \"Score\".");
    	}
    }
    
    public String getGroup() {
        return "Score";
    }
    
    public String getName() {
        return "Quantize";
    }
	
    public String getShortDescription() {
        return "Quantizes a score denotator";
    }
    
    public ImageIcon getIcon() {
        return QuantizeRubette.icon;
    }

    public String getLongDescription() {
        return "The Quantize rubette quantizes a score denotator"+
               " according to the given time and pitch units";
    }

    public String getInTip(int i) {
        return "Input score denotator";
    }

    public String getOutTip(int i) {
        return "Output score denotator";
    }
}

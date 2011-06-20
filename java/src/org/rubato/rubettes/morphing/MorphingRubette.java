/*
 * Copyright (C) 2007 Florian Thalmann
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

package org.rubato.rubettes.morphing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.rubato.base.RubatoException;
import org.rubato.base.SimpleAbstractRubette;
import org.rubato.composer.RunInfo;
import org.rubato.math.module.RElement;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.rubettes.alteration.Alterator;
import org.rubato.rubettes.alteration.NearestNeighborFinder;
import org.rubato.rubettes.util.NoteGenerator;
import org.rubato.rubettes.util.MacroNoteGenerator;
import org.rubato.rubettes.util.SimpleFormFinder;

/**
 * A rubette for generating morphs between two denotators.
 * 
 * @author Florian Thalmann
 */
public class MorphingRubette extends SimpleAbstractRubette {
	
	private PowerDenotator d1, d2;
	private PowerForm inputForm;
	private int[][] paths;
	private int[][] elementPaths;
	protected double newDuration;
	private NoteGenerator noteGenerator;
	private Alterator alterator;
	
	/**
	 * Creates a basic MorphingRubette.
	 */
	public MorphingRubette() {
        this.setInCount(2);
        this.setOutCount(1);
        this.noteGenerator = new MacroNoteGenerator();
        this.alterator = new Alterator();
	}

	@Override
	public String getName() {
		return "Morphing";
	}
	
	@Override
	public String getGroup() {
		return "Core";
	}
	
	protected void setInput(PowerDenotator d1, PowerDenotator d2) {
		this.d1 = d1;
		this.d2 = d2;
		this.updatePaths();
	}

	@Override
	public void run(RunInfo runInfo) {
		if (this.readAndVerifyInput()) {
			try {
				if (this.inputForm.equals(this.noteGenerator.getScoreForm())) {
					this.makeTimeCorrection(this.d1, this.d2);
				}
				this.setOutput(0, this.getMorph());
			} catch (RubatoException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean readAndVerifyInput() {
		Denotator id1 = this.getInput(0);
		Denotator id2 = this.getInput(1);
		if (id1 instanceof PowerDenotator && id2 instanceof PowerDenotator) {
            this.d1 = (PowerDenotator) id1.copy();
            this.d2 = (PowerDenotator) id2.copy();
			if (this.d1.getForm().equals(this.d1.getForm())) {
				this.updatePaths();
				return true;
			} else {
				this.addError("Input denotators not of same form.");
				return false;
			}
		} else {
			this.addError("Input denotators not of type Power.");
			return false;
		}
	}
	
	private void updatePaths() {
		this.inputForm = this.d1.getPowerForm();
		SimpleFormFinder finder = new SimpleFormFinder(this.inputForm, 1);
		this.paths = finder.getSimpleFormArrayPaths();
		this.elementPaths = finder.getElementPaths();
	}
	
	protected void makeTimeCorrection(PowerDenotator score1, PowerDenotator score2) throws RubatoException {
		double[] timeInfo1 = this.getTimeInfo(score1);
		double[] timeInfo2 = this.getTimeInfo(score2);
		this.newDuration = (timeInfo1[1]+timeInfo2[1])/2;
		//System.out.println("r"+this.newDuration/timeInfo1[1]);
		this.makeTimeCorrection(score1, timeInfo1, false);
		this.makeTimeCorrection(score2, timeInfo2, true);
	}
	
	/*
	 * Returns an array with the following time information about a Score denotator:
	 * {smallest onset, total duration (difference between greatest and smallest onset)}.
	 */
	protected double[] getTimeInfo(PowerDenotator score) throws RubatoException {
		double minOnset, maxOnset;
		int[] onsetPath = {0,0};
		Iterator<Denotator> notes = score.iterator();
		double currentOnset = ((RElement)notes.next().getElement(onsetPath)).getValue();
		minOnset = currentOnset;
		maxOnset = currentOnset;
		while (notes.hasNext()) {
			currentOnset = ((RElement)notes.next().getElement(onsetPath)).getValue();
			minOnset = Math.min(currentOnset, minOnset);
			maxOnset = Math.max(currentOnset, maxOnset);
		}
		return new double[] {minOnset, maxOnset-minOnset};
	}
	
	/*
	 * Modifies the specified score, so that it starts at onset 0 and stretches it by the two
	 * specified factors. By the first in the beginning and the second in the end. At the
	 * positions inbetween, the two factors are interpolated. 
	 */
	private void makeTimeCorrection(PowerDenotator score, double[] timeInfo, boolean back) throws RubatoException {
		Iterator<Denotator> notes = score.iterator();
		int[] onsetPath = {0,0};
		int[] durationPath = {3,0};
		while (notes.hasNext()) {
			LimitDenotator currentNote = (LimitDenotator) notes.next();
			double currentOnset = ((RElement)currentNote.getElement(onsetPath)).getValue();
			double currentDuration = ((RElement)currentNote.getElement(durationPath)).getValue();
			
			//System.out.print(currentOnset+" "+currentDuration+", ");
			//correct onset offset (move score to position 0)
			currentOnset = currentOnset - timeInfo[0];
			double currentNoteEnd = currentOnset+currentDuration;
			//correct onset and duration (extend or compress gradually)
			currentOnset = this.calculateNewTime(currentOnset, timeInfo[1], back);
			currentNoteEnd = this.calculateNewTime(currentNoteEnd, timeInfo[1], back);
			currentDuration = currentNoteEnd-currentOnset;
			//System.out.println(currentOnset+" "+currentDuration);
			//update current Note
			this.noteGenerator.modifyNoteDenotator(currentNote, currentOnset, currentDuration);
		}
	}
	
	private double calculateNewTime(double oldTime, double oldDuration, boolean back) {
		double position = oldTime/oldDuration;
		double relationship = this.newDuration/oldDuration;
		if (back) {
			double factor = (1-position)*(relationship-1);
			return relationship*oldTime + factor*oldTime;
		} else {
			double factor = (1-position) + position*relationship;
			return factor*oldTime;
		}
	}
	
	/*private double calculateNewTime(double oldTime, double oldDuration) {
		double position = oldTime/oldDuration;
		double currentFactor = position/2;
		currentFactor = currentFactor * (2.0 - 2*this.newDuration/oldDuration);
		currentFactor = 1-currentFactor;
		return oldTime*currentFactor*this.newDuration*2/oldDuration;
	}
	
	/*
	 * Modifies the specified score, so that it starts at onset 0 and stretches it by the two
	 * specified factors. By the first in the beginning and the second in the end. At the
	 * positions inbetween, the two factors are interpolated. 
	 *
	private void makeTimeCorrection(PowerDenotator score, double[] timeInfo, double[] factors) throws RubatoException {
		Iterator<Denotator> notes = score.iterator();
		int[] onsetPath = {0,0};
		int[] durationPath = {3,0};
		while (notes.hasNext()) {
			LimitDenotator currentNote = (LimitDenotator) notes.next();
			double currentOnset = ((RElement)currentNote.getElement(onsetPath)).getValue();
			double currentDuration = ((RElement)currentNote.getElement(durationPath)).getValue();
			System.out.print(currentOnset+" "+currentDuration+",");
			//correct onset offset (move score to position 0)
			currentOnset = currentOnset - timeInfo[0];
			//correct onset and duration (extend or compress gradually)
			double position = currentOnset/timeInfo[1];
			double currentFactor = (1-position)*factors[0] + position*factors[1];
			//System.out.println(position+ " " +currentFactor);
			currentOnset = currentFactor*currentOnset;
			currentDuration = currentFactor*currentDuration;
			System.out.println(currentOnset+" "+currentDuration);
			//update current Note
			this.noteGenerator.modifyNoteDenotator(currentNote, currentOnset, currentDuration);
		}
	}
	
	private void makeTimeCorrectionRe(PowerDenotator score, double[] timeInfo, double[] factors) throws RubatoException {
		Iterator<Denotator> notes = score.iterator();
		int[] onsetPath = {0,0};
		int[] durationPath = {3,0};
		while (notes.hasNext()) {
			LimitDenotator currentNote = (LimitDenotator) notes.next();
			double currentOnset = ((RElement)currentNote.getElement(onsetPath)).getValue();
			double currentDuration = ((RElement)currentNote.getElement(durationPath)).getValue();
			//correct onset offset (move score to position 0)
			currentOnset = currentOnset - timeInfo[0];
			//correct onset and duration (extend or compress gradually)
			double position = currentOnset/timeInfo[1];
			double currentFactor = (1-position)*factors[0] + position*factors[1];
			//System.out.println(position+ " " +currentFactor);
			currentOnset = currentOnset + position*(this.newDuration-timeInfo[1]);
			currentDuration = currentFactor*currentDuration;
			//update current Note
			this.noteGenerator.modifyNoteDenotator(currentNote, currentOnset, currentDuration);
		}
	}*/
	
	protected PowerDenotator getMorph() throws RubatoException {
		Map<Denotator, Denotator> neighborMap1 = this.getNeighborMap(this.d1, this.d2);
		Map<Denotator, Denotator> neighborMap2 = this.getNeighborMap(this.d2, this.d1);
		Set<Denotator> neighbors1 = new HashSet<Denotator>(neighborMap1.values());
		Set<Denotator> neighbors2 = new HashSet<Denotator>(neighborMap2.values());
		PowerDenotator morph = this.getBasicMorph(neighborMap1, neighbors2);
		morph = morph.union(this.getAdditionalMorph(neighborMap2, neighbors1));
		return morph;
	}
	
	private PowerDenotator getBasicMorph(Map<Denotator, Denotator> neighborMap, Set<Denotator> complementSet) throws RubatoException {
		int[] positionPath = {0,0}; //onset for now...
		PowerDenotator partialMorph = new PowerDenotator(NameDenotator.make(""), this.inputForm, new ArrayList<Denotator>());
		Iterator<Denotator> keys = neighborMap.keySet().iterator();
		while (keys.hasNext()) {
			Denotator currentDenotator = keys.next();
			Denotator currentNeighbor = neighborMap.get(currentDenotator);
			double currentOnset = ((RElement)currentDenotator.getElement(positionPath)).getValue();
			double position = currentOnset/this.newDuration;
			double factor = position; //1-probablility that denotator is present
			if (complementSet.contains(currentDenotator)) {
				factor = 0; //probability 1 that denotator is present
			}
			//System.out.println(position + " " + factor);
			if (Math.random() > factor) {
				partialMorph.appendFactor(this.alterator.alter(currentDenotator, currentNeighbor, position, this.paths));
			}
		}
		return partialMorph;
	}
	
	private PowerDenotator getAdditionalMorph(Map<Denotator, Denotator> neighborMap, Set<Denotator> complementSet) throws RubatoException {
		int[] positionPath = {0,0}; //onset for now...
		Iterator<Denotator> keys = neighborMap.keySet().iterator();
		PowerDenotator additionalMorph = new PowerDenotator(NameDenotator.make(""), this.inputForm, new ArrayList<Denotator>());
		while (keys.hasNext()) {
			Denotator currentDenotator = keys.next();
			if (!complementSet.contains(currentDenotator)) {
				Denotator currentNeighbor = neighborMap.get(currentDenotator);
				double currentOnset = ((RElement)currentDenotator.getElement(positionPath)).getValue();
				double position = currentOnset/this.newDuration;
				double factor = 1-position; //1-probablility that denotator is present
				//System.out.println(position + " " + factor);
				if (Math.random() > factor) {
					additionalMorph.appendFactor(this.alterator.alter(currentDenotator, currentNeighbor, factor, this.paths));
				}
			}
		}
		return additionalMorph;
		
	}
	
	private Map<Denotator, Denotator> getNeighborMap(PowerDenotator denotator, PowerDenotator neighbors) {
		Map<Denotator, Denotator> neighborMap = new HashMap<Denotator, Denotator>();
		NearestNeighborFinder neighborFinder = new NearestNeighborFinder(neighbors, this.elementPaths);
		for (int i = 0; i < denotator.getFactorCount(); i++) {
			Denotator currentFactor = denotator.getFactor(i);
			neighborMap.put(currentFactor, neighborFinder.findNearestNeighbor(currentFactor));
		}
		return neighborMap;
	}

}

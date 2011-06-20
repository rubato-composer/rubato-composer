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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rubato.base.RubatoException;
import org.rubato.math.module.QElement;
import org.rubato.math.module.RElement;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.util.MacroNoteGenerator;

import junit.framework.TestCase;

/**
 * Defines tests for the MorphingRubette class.
 * 
 * @author Florian Thalmann
 */
public class MorphingRubetteTest extends TestCase {
	
	private MacroNoteGenerator noteGenerator;
	private MorphingRubette rubette;
	private PowerDenotator score1, score2, score3;
	
	protected void setUp() {
		this.noteGenerator = new MacroNoteGenerator();
		this.rubette = new MorphingRubette();
		this.score1 = this.noteGenerator.createSimpleMelody(1, 69, 74, 73, 82, 67, 71);
		this.score2 = this.noteGenerator.createSimpleMelody(1, 67, 67, 69, 73);
		this.score3 = this.noteGenerator.createSimpleMelody(1, 69, 74, 73, 82);
	}
	
	public void testGetTimeInfo() {
		try {
			double[] timeInfo1 = this.rubette.getTimeInfo(score1);
			double[] timeInfo2 = this.rubette.getTimeInfo(score2);
			TestCase.assertTrue(timeInfo1[0] == 0 && timeInfo1[1] == 5);
			TestCase.assertTrue(timeInfo2[0] == 0 && timeInfo2[1] == 3);
		} catch (RubatoException e) { }
	}
	
	public void testMakeTimeCorrection() throws RubatoException {
		this.rubette.makeTimeCorrection(score1, score2);
		double[] timeInfo1 = this.rubette.getTimeInfo(score1);
		double[] timeInfo2 = this.rubette.getTimeInfo(score2);
		//check onsets
		TestCase.assertTrue(timeInfo1[0] == 0 && timeInfo1[1] == 4);
		TestCase.assertTrue(timeInfo2[0] == 0 && timeInfo2[1] == 4);
		//check durations
		System.out.println("l"+this.getDuration(score2.getFactor(0)));
		TestCase.assertTrue(this.getDuration(score1.getFactor(0)) < 1);
		TestCase.assertTrue(this.getDuration(score1.getFactor(4)) < 84.0/125);
		TestCase.assertTrue(this.getDuration(score1.getFactor(5)) < Math.pow(4.0/5,2));
		TestCase.assertTrue(this.getDuration(score2.getFactor(0)) > 1);
		TestCase.assertTrue(this.getDuration(score2.getFactor(3)) < 1);
	}
	
	public void testMakeTimeCorrection2() throws RubatoException {
		this.rubette.makeTimeCorrection(score2, score1);
		double[] timeInfo1 = this.rubette.getTimeInfo(score1);
		double[] timeInfo2 = this.rubette.getTimeInfo(score2);
		//check onsets
		TestCase.assertTrue(timeInfo1[0] == 0 && timeInfo1[1] == 4);
		TestCase.assertTrue(timeInfo2[0] == 0 && timeInfo2[1] == 4);
		//check durations
		System.out.println("l"+this.getDuration(score1.getFactor(0)));
		TestCase.assertTrue(this.getDuration(score1.getFactor(0)) == 0.6400000000000001); //12/25
		TestCase.assertTrue(this.getDuration(score1.getFactor(4)) < 1);
		TestCase.assertTrue(this.getDuration(score1.getFactor(5)) > 1);
		TestCase.assertTrue(this.getDuration(score2.getFactor(0)) > 1);
		TestCase.assertTrue(this.getDuration(score2.getFactor(3)) > 4.0/3);
	}
	
	public void testGetMorph() throws RubatoException {
		this.rubette.setInput(score2,score3);
		this.rubette.makeTimeCorrection(score2, score3);
		PowerDenotator morph = this.rubette.getMorph();
		List<Denotator> factors = morph.getFactors();
		int size = factors.size();
		TestCase.assertTrue(size <= 8);
		
		TestCase.assertTrue(this.getPitch(factors.get(0)) == 67);
		TestCase.assertTrue(this.getPitch(factors.get(size-1)) == 82);
		
		Set<Double> possiblePitches = new HashSet<Double>();
		possiblePitches.add(new Double(67));
		
		possiblePitches.add(new Double(82));
		
		for (int i = 0; i < factors.size(); i++) {
			//System.out.println(new Double(this.getPitch(factors.get(i))));
			//TestCase.assertTrue(possiblePitches.contains(new Double(this.getPitch(factors.get(i)))));
		}
	}
	
	private double getPitch(Denotator denotator) throws RubatoException {
		return ((QElement)denotator.getElement(new int[]{1,0})).getValue().doubleValue();
	}
	
	private double getDuration(Denotator denotator) throws RubatoException {
		return ((RElement)denotator.getElement(new int[]{3,0})).getValue();
	}

}

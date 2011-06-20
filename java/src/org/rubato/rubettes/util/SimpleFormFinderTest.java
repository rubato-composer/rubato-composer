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

package org.rubato.rubettes.util;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Defines tests for the SimpleFormFinder class.
 * 
 * @author Florian Thalmann
 */
public class SimpleFormFinderTest extends TestCase {
	
	//private PowerForm macroScoreForm = (PowerForm)Repository.systemRepository().getForm("MacroScore");
	
	private NoteGenerator generator;
	private SimpleFormFinder finder;
	
	public void setUp() {
		this.generator = new MacroNoteGenerator();
	}
	
	public void testWithScoreForm() {
		this.finder = new SimpleFormFinder(generator.getScoreForm());
		
		//test with different recursion levels
		this.finder.setMaxRecursion(0);
		TestCase.assertTrue(this.finder.getSimpleForms().size() == 0);
		TestCase.assertTrue(this.finder.getSimpleFormPaths().size() == 0);
		this.finder.setMaxRecursion(1);
		TestCase.assertTrue(this.finder.getSimpleForms().size() == 5);
		TestCase.assertTrue(this.finder.getSimpleFormPaths().size() == 5);
		this.finder.setMaxRecursion(10);
		TestCase.assertTrue(this.finder.getSimpleForms().size() == 5);
		TestCase.assertTrue(this.finder.getSimpleFormPaths().size() == 5);
		
		//test getPath()
		int[] path = this.finder.getPath(generator.getLoudnessForm());
		TestCase.assertTrue(Arrays.equals(path, new int[]{2}));
		path = this.finder.getPathForElement(generator.getDurationForm());
		TestCase.assertTrue(Arrays.equals(path, new int[]{3,0}));
	}
	
	public void testWithMacroScoreForm() {
		/* First we need the MacroScore form to be built-in...
		 
		this.finder = new SimpleFormFinder(this.macroScoreForm);
		
		//test with different recursion levels
		this.finder.setMaxRecursion(0);
		System.out.println(this.finder.getSimpleForms());
		TestCase.assertTrue(this.finder.getSimpleForms().size() == 0);
		TestCase.assertTrue(this.finder.getSimpleFormPaths().size() == 0);
		this.finder.setMaxRecursion(1);
		TestCase.assertTrue(this.finder.getSimpleForms().size() == 0);
		TestCase.assertTrue(this.finder.getSimpleFormPaths().size() == 0);
		this.finder.setMaxRecursion(2);
		TestCase.assertTrue(this.finder.getSimpleForms().size() == 5);
		TestCase.assertTrue(this.finder.getSimpleFormPaths().size() == 5);
		this.finder.setMaxRecursion(10);
		TestCase.assertTrue(this.finder.getSimpleForms().size() == 5);
		TestCase.assertTrue(this.finder.getSimpleFormPaths().size() == 5);
		
		//test getPath()
		int[] path = this.finder.getPath(generator.getLoudnessForm());
		TestCase.assertTrue(Arrays.equals(path, new int[]{0,2}));
		path = this.finder.getPathForElement(generator.getDurationForm());
		TestCase.assertTrue(Arrays.equals(path, new int[]{0,3,0}));*/
	}

}

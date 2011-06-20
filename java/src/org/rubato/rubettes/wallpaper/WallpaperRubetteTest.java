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

package org.rubato.rubettes.wallpaper;
//
//  WallpaperRubetteTest.java
//  WallpaperRubette
//
//  Created by Florian Thalmann on 5/29/06.
//  Copyright 2006 __MyCompanyName__. All rights reserved.
//

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;

import java.util.List;
import java.util.ArrayList;

import org.rubato.base.Repository;
import org.rubato.base.RubatoException;
import org.rubato.math.arith.Rational;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.yoneda.*;
import org.rubato.math.module.Module;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.QElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RAffineMorphism;
import org.rubato.math.module.morphism.ZAffineMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.xml.XMLWriter;
import org.rubato.xml.XMLReader;

import org.rubato.rubettes.alteration.AlterationRubetteTest;
import org.rubato.rubettes.util.SimpleFormFinderTest;
import org.rubato.rubettes.score.ScaleRubetteTest;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Defines tests for the WallpaperRubette class.
 * 
 * @author Florian Thalmann
 */
public class WallpaperRubetteTest extends TestCase {

	private PowerDenotator denotator;
	private List<ModuleMorphism> morphisms;
	private WallpaperRubette rubette;
	
	private NameDenotator emptyName = NameDenotator.make("");
	private Repository systemRepository = Repository.systemRepository();
	private LimitForm noteForm = (LimitForm)systemRepository.getForm("Note");
	private PowerForm scoreForm = (PowerForm)systemRepository.getForm("Score");
	private SimpleForm loudnessForm = (SimpleForm)systemRepository.getForm("Loudness");
	private PowerForm realSetForm = (PowerForm)systemRepository.getForm("RealSet");
	private SimpleForm realForm = (SimpleForm)systemRepository.getForm("Real");

	protected void setUp() {
		Denotator note = this.noteForm.createDefaultDenotator();
		List<Denotator> noteList = new ArrayList<Denotator>();
		noteList.add(note);
		try {
			this.denotator = new PowerDenotator(this.emptyName, this.scoreForm, noteList);
		} catch (RubatoException e) { e.printStackTrace(); }
		this.rubette = (WallpaperRubette)new WallpaperRubette().newInstance();
		this.rubette.setInputForm(this.scoreForm);
		this.morphisms = new ArrayList<ModuleMorphism>();
		this.morphisms.add(new RAffineMorphism(2, 1));
		this.morphisms.add(RFreeAffineMorphism.make(new RMatrix(new double[][]{{1,1},{1,1}}), new double[]{1,2}));
	}
	
	public void testMapDenotatorReal() throws RubatoException {
		this.assertThisDenotatorAsDefault();
			
		this.rubette.addMorphism(this.morphisms.get(0), 0, 1, this.createSimplePaths(0, 0));
		this.denotator = this.rubette.mapDenotator(this.denotator, this.morphisms.get(0));
		TestCase.assertTrue(((RElement)this.denotator.getElement(new int[]{0,0,0})).getValue() == 1);
		
		this.rubette.addMorphism(this.morphisms.get(1), 0, 1, this.createSimplePaths(0, 1, 0, 1));
		this.denotator = this.rubette.mapDenotator(this.denotator, this.morphisms.get(1));
		TestCase.assertTrue(((RElement)this.denotator.getElement(new int[]{0,0,0})).getValue() == 2);
		TestCase.assertTrue(((QElement)this.denotator.getElement(new int[]{0,1,0})).getValue().equals(new Rational(3)));
	}
	
	public void testMapDenotatorInteger() throws RubatoException {
		this.morphisms.add(new ZAffineMorphism(2, 1));
		SimpleDenotator newLoudness = new SimpleDenotator(emptyName, loudnessForm, new ZElement(3));
		((LimitDenotator)this.denotator.getFactor(0)).setFactor(2, newLoudness);
		
		TestCase.assertTrue(((ZElement)this.denotator.getElement(new int[]{0,2,0})).getValue() == 3);
		
		this.rubette.addMorphism(this.morphisms.get(2), 0, 1, this.createSimplePaths(2, 2));
		this.denotator = this.rubette.mapDenotator(this.denotator, this.morphisms.get(2));
		TestCase.assertTrue(((ZElement)this.denotator.getElement(new int[]{0,2,0})).getValue() == 7);
	}
	
	public void testGetUnitedMappedDenotatorsWithOneMorphism() throws RubatoException {
		this.assertThisDenotatorAsDefault();
			
		this.rubette.addMorphism(this.morphisms.get(1), 1, 3, this.createSimplePaths(0, 1, 0, 1));
		this.denotator = this.rubette.getUnitedMappedDenotators(this.denotator);
		TestCase.assertTrue(this.denotator.getFactorCount() == 3);
		this.assertDenotatorFactor(0, 1, 2);
		this.assertDenotatorFactor(1, 4, 5);
		this.assertDenotatorFactor(2, 10, 11);
	}
	
	public void testGetUnitedMappedDenotatorsWithTwoMorphisms() throws RubatoException {
		this.assertThisDenotatorAsDefault();
			
		this.rubette.addMorphism(this.morphisms.get(0), 0, 1, this.createSimplePaths(1, 1));
		this.rubette.addMorphism(this.morphisms.get(1), 0, 1, this.createSimplePaths(0, 1, 0, 1));
		this.denotator = this.rubette.getUnitedMappedDenotators(this.denotator);
		TestCase.assertTrue(this.denotator.getFactorCount() == 4);
		this.assertDenotatorFactor(0, 0, 0);
		this.assertDenotatorFactor(1, 0, 1);
		this.assertDenotatorFactor(2, 1, 2);
		this.assertDenotatorFactor(3, 1, 5);
	}
	
	public void testGetUnitedMappedDenotatorsWithToAndFromXML() throws Exception {
		this.assertThisDenotatorAsDefault();
			
		this.rubette.setInputForm(this.scoreForm);
		this.rubette.addMorphism(this.morphisms.get(1), 1, 3, this.createSimplePaths(0, 1, 0, 1));
		this.rubette.addMorphism(this.morphisms.get(0), 0, 1, this.createSimplePaths(1, 1));
		
		File testFile = new File("./wprTest");
		if (!testFile.exists()) {
			testFile.createNewFile();
		}
		Reader bufferedReader = new BufferedReader(new FileReader(testFile));
		//PrintStream stream = new PrintStream(new ByteArrayOutputStream());
		XMLWriter writer = new XMLWriter(testFile);
		XMLReader reader = new XMLReader(bufferedReader);
		writer.open();
		this.rubette.toXML(writer);
		writer.close();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Element element = builder.parse(new InputSource(bufferedReader)).getDocumentElement();
		reader.parse();
		this.rubette = (WallpaperRubette)this.rubette.fromXML(reader, element);
		this.denotator = this.rubette.getUnitedMappedDenotators(this.denotator);
		testFile.delete();
		
		TestCase.assertTrue(this.rubette.getTempInputForm().equals(this.scoreForm));
		TestCase.assertTrue(this.denotator.getFactorCount() == 6);
		this.assertDenotatorFactor(0, 1, 2);
		this.assertDenotatorFactor(1, 2, 3);
		this.assertDenotatorFactor(2, 4, 5);
		this.assertDenotatorFactor(3, 6, 7);
		this.assertDenotatorFactor(4, 10, 11);
		this.assertDenotatorFactor(5, 14, 15);
	}
	
	public void testGetUnitedMappedDenotatorsWithRange() throws RubatoException {
		this.assertThisDenotatorAsDefault();
			
		this.rubette.addMorphism(this.morphisms.get(0), 2, 3, this.createSimplePaths(1, 1));
		this.rubette.addMorphism(this.morphisms.get(1), 1, 4, this.createSimplePaths(0, 1, 0, 1));
		this.denotator = this.rubette.getUnitedMappedDenotators(this.denotator);
		TestCase.assertTrue(this.denotator.getFactorCount() == 8);
		this.assertDenotatorFactor(0, 1, 11);
		this.assertDenotatorFactor(1, 1, 23);
		this.assertDenotatorFactor(2, 4, 23);
		this.assertDenotatorFactor(3, 4, 47);
		this.assertDenotatorFactor(4, 10, 47);
		this.assertDenotatorFactor(5, 10, 95);
		this.assertDenotatorFactor(6, 22, 95);
		this.assertDenotatorFactor(7, 22, 191);
	}
	
	/**
	 * Tests the Wallpaper rubette with a RealSet denotator.
	 * @throws RubatoException
	 */
	public void testGetUnitedMappedDenotatorsWithRealSet() throws RubatoException {
		this.assertThisDenotatorAsDefault();
		
		//the paths in the list must be empty, because the realSet's elements are Simples themselves
		List<List<Integer>> paths = this.createSimplePaths();
		paths.add(new ArrayList<Integer>());
		paths.add(new ArrayList<Integer>());
		this.rubette.addMorphism(this.morphisms.get(0), 1, 3, paths);
		List<Denotator> realList = new ArrayList<Denotator>();
		realList.add(this.realForm.createDefaultDenotator());
		this.denotator = new PowerDenotator(this.emptyName, this.realSetForm, realList);
		this.denotator = this.rubette.getUnitedMappedDenotators(this.denotator);
		//TestCase.assertTrue(this.denotator.getFactorCount() == 3);
		
		SimpleDenotator d = (SimpleDenotator)this.realForm.createDefaultDenotator();
		ModuleMorphism m = d.getModuleMorphism();
		m = this.morphisms.get(0).compose(m);
		d = new SimpleDenotator(this.emptyName, this.realForm, m);
	}
	
	private void assertDenotatorFactor(int factorIndex, int onsetValue, int pitchValue) throws RubatoException {
		Denotator factor = this.denotator.getFactor(factorIndex);
		TestCase.assertTrue(((RElement)factor.getElement(new int[]{0,0})).getValue() == onsetValue);
		TestCase.assertTrue(((QElement)factor.getElement(new int[]{1,0})).getValue().equals(new Rational(pitchValue)));
	}
	
	private void assertThisDenotatorAsDefault() throws RubatoException {
		TestCase.assertTrue(((RElement)this.denotator.getElement(new int[]{0,0,0})).getValue() == 0);
		TestCase.assertTrue(((QElement)this.denotator.getElement(new int[]{0,1,0})).getValue().equals(new Rational(0)));
		TestCase.assertTrue(((ZElement)this.denotator.getElement(new int[]{0,2,0})).getValue() == 0);
		TestCase.assertTrue(((RElement)this.denotator.getElement(new int[]{0,3,0})).getValue() == 0);
	}
	
	public void testJSelectMorphismElements() {
		JSelectSimpleForms selectElements = new JSelectSimpleForms(this.rubette, null);
		TestCase.assertTrue(selectElements.getElements() == null);
		TestCase.assertTrue(selectElements.getNumberOfFormBoxes() == 0);
		//test automatic elements
		selectElements.setMorphism(this.morphisms.get(1));
		List<List<Integer>> coordinatePaths = new ArrayList<List<Integer>>();
		coordinatePaths.add(this.createPath(0));
		coordinatePaths.add(this.createPath(1));
		coordinatePaths.add(this.createPath(0));
		coordinatePaths.add(this.createPath(1));
		TestCase.assertTrue(selectElements.getNumberOfFormBoxes() == 4);
		TestCase.assertTrue(selectElements.getElements().equals(coordinatePaths));
		//test set and get
		coordinatePaths = new ArrayList<List<Integer>>();
		coordinatePaths.add(this.createPath(2));
		coordinatePaths.add(this.createPath(1));
		coordinatePaths.add(this.createPath(2));
		coordinatePaths.add(this.createPath(3));
		selectElements.setElements(coordinatePaths);
		TestCase.assertTrue(selectElements.getElements().equals(coordinatePaths));
		selectElements.dispose();
	}
	
	public void testJWallpaperView() {
		ModuleMorphism morphism = this.morphisms.get(1);
		Module domain = morphism.getDomain();
		int domainDim = domain.getDimension();
		List<List<Integer>> coordinates = this.createSimplePaths(0, 1, 0, 1);
		List<List<Integer>> coordinates2 = this.createSimplePaths(0, 0);
		this.rubette.addMorphism(this.morphisms.get(0), 1, 3, coordinates2);
		this.rubette.addMorphism(morphism, 0, 2, coordinates);
		JWallpaperView view = new JWallpaperView(this.rubette.getMorphismsTable());
		List<List<Integer>> indices = view.getIndices(false, domainDim, coordinates);
		TestCase.assertTrue(indices.equals(this.createSimplePaths(0, 1)));
		this.rubette.getView();
	}
	
	private List<List<Integer>> createSimplePaths(int... paths) {
		List<List<Integer>> pathList = new ArrayList<List<Integer>>();
		for (int i = 0; i < paths.length; i++) {
			pathList.add(this.createPath(paths[i]));
		}
		return pathList;
	}
	
	private List<Integer> createPath(int path) {
		List<Integer> newPath = new ArrayList<Integer>();
		newPath.add(new Integer(path));
		return newPath;
	}
	
	public static void main(String args[]) {
		TestSuite suite = new TestSuite(WallpaperRubetteTest.class);
		suite.addTestSuite(AlterationRubetteTest.class);
		suite.addTestSuite(SimpleFormFinderTest.class);
		suite.addTestSuite(ScaleRubetteTest.class);
        junit.textui.TestRunner.run(suite);
    }
}

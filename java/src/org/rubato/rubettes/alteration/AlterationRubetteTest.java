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

package org.rubato.rubettes.alteration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.rubato.math.arith.Rational;
import org.rubato.math.matrix.QMatrix;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.QFreeAffineMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.util.MacroNoteGenerator;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Defines tests for the AlterationRubette class.
 * 
 * @author Florian Thalmann
 */
public class AlterationRubetteTest extends TestCase {
	
	private MacroNoteGenerator noteGenerator;
	private AlterationRubette rubette;
	private Alterator alterator;
	private List<ModuleMorphism> morphisms;
	private SimpleForm onsetForm, pitchForm, loudnessForm, durationForm;
	
	protected void setUp() {
		this.noteGenerator = new MacroNoteGenerator();
		this.onsetForm = this.noteGenerator.getOnsetForm();
		this.pitchForm = this.noteGenerator.getPitchForm();
		this.loudnessForm = this.noteGenerator.getLoudnessForm();
		this.durationForm = this.noteGenerator.getDurationForm();
		this.rubette = new AlterationRubette();
		this.rubette.setInputForm(this.noteGenerator.getScoreForm());
		JAlterationDimensionsTable dimensions = this.rubette.getDimensionsTable();
		dimensions.addDimension(this.onsetForm, 0.5);
		dimensions.addDimension(this.pitchForm, 0.7);
		dimensions.addDimension(this.durationForm, 0.2);
		dimensions.applyChanges();
		
		this.morphisms = new ArrayList<ModuleMorphism>();
		this.morphisms.add(RFreeAffineMorphism.make(new RMatrix(new double[][]{{3,5,7}}), new double[]{0}));
		this.morphisms.add(RFreeAffineMorphism.make(new RMatrix(new double[][]{{1,2,3}}), new double[]{0}));
		QMatrix matrix1 = new QMatrix(new Rational[][]{{new Rational(3),new Rational(5),new Rational(7)}});
		QMatrix matrix2 = new QMatrix(new Rational[][]{{new Rational(1),new Rational(2),new Rational(3)}});
		this.morphisms.add(QFreeAffineMorphism.make(matrix1, new Rational[]{new Rational(0)}));
		this.morphisms.add(QFreeAffineMorphism.make(matrix2, new Rational[]{new Rational(0)}));
	}
	
	public void testGetProperties() {
		this.rubette = new AlterationRubette();
		this.rubette.getProperties();
	}
	
	public void testJAlterationDimensionsTable() {
		JAlterationDimensionsTable dimensions = this.rubette.getDimensionsTable();
		TestCase.assertTrue(dimensions.dimensionCount() == 3);
		TestCase.assertTrue(dimensions.getRowCount() == 3);
		//add a form 
		dimensions.addDimension(this.loudnessForm, 0.5);
		TestCase.assertTrue(dimensions.dimensionCount() == 3);
		TestCase.assertTrue(dimensions.getRowCount() == 4);
		//revert changes
		dimensions.revertChanges();
		TestCase.assertTrue(dimensions.dimensionCount() == 3);
		TestCase.assertTrue(dimensions.getRowCount() == 3);
		//apply changes
		dimensions.addDimension(this.loudnessForm, 0.5);
		TestCase.assertTrue(dimensions.applyChanges());
		TestCase.assertTrue(dimensions.dimensionCount() == 4);
		TestCase.assertTrue(dimensions.getRowCount() == 4);
		//add the same form a second time
		dimensions.addDimension(this.loudnessForm, 0.3);
		TestCase.assertTrue(!dimensions.applyChanges());
		TestCase.assertTrue(dimensions.dimensionCount() == 4);
		TestCase.assertTrue(dimensions.getForm(1).equals(this.noteGenerator.getPitchForm()));
		TestCase.assertTrue(dimensions.getStartPercentage(2) == 0.2);
		TestCase.assertTrue(dimensions.getEndPercentage(2) == 0.2);
	}
	
	public void testInputFormValid() {
		PowerDenotator input0 = this.noteGenerator.createSimpleMelody(1, 69, 74, 73);
		PowerDenotator input1 = this.noteGenerator.createSimpleMelody(1, 69, 67, 69);
		TestCase.assertTrue(input0.getForm().equals(this.rubette.getTempInputForm()));
		TestCase.assertTrue(this.rubette.inputFormAndDenotatorsValid(input0, input1));
	}
	
	/*public void testGetIndicesOf() {
		int[][] allPaths = new int[][]{{0,0},{1,0},{0,0},{2,0},{3,0},{2,0},{1,0}};
		int[][] differentPaths = new int[][]{{0,0},{1,0},{2,0},{3,0}};
		int[] predictedIndices = new int[]{0,1,0,2,3,2,1};
		int[] indices = this.rubette.getIndicesOf(allPaths, differentPaths);
		TestCase.assertTrue(Arrays.equals(indices, predictedIndices));
	}
	
	public void testGetMinAndMax() {
		PowerDenotator score = this.noteGenerator.createSimpleMelody(1, 69, 70, 79, 54, 66, 72);
		int[][] paths = new int[][]{{0,0},{1,0}};
		try {
			double[][] minAndMax = this.rubette.getMinAndMaxDouble(score, paths);
			TestCase.assertTrue(Arrays.deepEquals(minAndMax, new double[][]{{0,54},{5,79}}));
		} catch (RubatoException e) { e.printStackTrace(); }
	}
	
	/*
	 * slight deviations in onset (0.19999999999999996 and 0.3999999999999999), therefore rounding
	 *
	public void testGetMorphing() throws RubatoException {
		JAlterationDimensionsTable dimensions = this.rubette.getDimensionsTable();
		dimensions.clear();
		dimensions.addDimension(this.onsetForm, 0.8);
		dimensions.addDimension(this.pitchForm, 0.8);
		dimensions.applyChanges();
		
		PowerDenotator melody1 = this.noteGenerator.createSimpleMelody(1, 69, 74, 73);
		PowerDenotator melody2 = this.noteGenerator.createSimpleMelody(1, 69, 67, 65);
		PowerDenotator morphing = this.rubette.getMorphing(melody1, melody2);
		double onset0 = ((RElement)morphing.getFactor(0).getElement(new int[]{0,0})).getValue();
		double onset1 = ((RElement)morphing.getFactor(1).getElement(new int[]{0,0})).getValue();
		double onset2 = ((RElement)morphing.getFactor(2).getElement(new int[]{0,0})).getValue();
		double factor = 1000000000000000.0;
		TestCase.assertTrue(onset0 == 0);
		System.out.println(onset2);
		TestCase.assertTrue(Math.round(onset1*factor)/factor == 0.2);
		TestCase.assertTrue(Math.round(onset2*factor)/factor == 0.4);
		
		double pitch0 = (((QElement)morphing.getFactor(0).getElement(new int[]{1,0})).getValue()).doubleValue();
		double pitch1 = (((QElement)morphing.getFactor(1).getElement(new int[]{1,0})).getValue()).doubleValue();
		double pitch2 = (((QElement)morphing.getFactor(2).getElement(new int[]{1,0})).getValue()).doubleValue();
		TestCase.assertTrue(pitch0 == 69 && pitch1 == 70 && pitch2 == 69.8);
	}
	
	public void testGetMorphingSpecialCases() throws RubatoException {
		JAlterationDimensionsTable dimensions = this.rubette.getDimensionsTable();
		dimensions.clear();
		dimensions.addDimension(this.pitchForm, 1);
		dimensions.applyChanges();
		
		PowerDenotator melody1 = this.noteGenerator.createSimpleMelody(1, 66, 74, 68, 63, 70);
		PowerDenotator melody2 = this.noteGenerator.createSimpleMelody(1, 64, 70, 65, 69, 61);
		
		PowerDenotator predicted = this.noteGenerator.createSimpleMelody(1, 65, 70, 69, 64, 70);
		PowerDenotator morphing = this.rubette.getMorphing(melody1, melody2);
		TestCase.assertTrue(morphing.equals(predicted));
		
		//for onset, no morphing takes place, but it is respected during nearest neighbour search
		dimensions.addDimension(this.onsetForm, 0);
		dimensions.applyChanges();
		predicted = this.noteGenerator.createSimpleMelody(1, 64, 70, 69, 61, 69);
		morphing = this.rubette.getMorphing(melody1, melody2);
		/*System.out.println(((QElement)morphing.getFactor(0).getElement(new int[]{1,0})).getValue());
		System.out.println(((QElement)morphing.getFactor(1).getElement(new int[]{1,0})).getValue());
		System.out.println(((QElement)morphing.getFactor(2).getElement(new int[]{1,0})).getValue());
		System.out.println(((QElement)morphing.getFactor(3).getElement(new int[]{1,0})).getValue());
		System.out.println(((QElement)morphing.getFactor(4).getElement(new int[]{1,0})).getValue());/
		TestCase.assertTrue(morphing.equals(predicted));
	}
	
	public void testGetMorphingRelative() throws RubatoException {
		JAlterationDimensionsTable dimensions = this.rubette.getDimensionsTable();
		dimensions.clear();
		dimensions.addDimension(this.pitchForm, 0, 1, this.onsetForm);
		dimensions.applyChanges();
		
		PowerDenotator melody1 = this.noteGenerator.createSimpleMelody(1, 66, 73, 68, 63, 72);
		PowerDenotator melody2 = this.noteGenerator.createSimpleMelody(1, 64, 71, 65, 69, 61);
		
		PowerDenotator predicted = this.noteGenerator.createSimpleMelody(1, 66, 72.5, 68.5, 63.75, 71);
		PowerDenotator morphing = this.rubette.getMorphing(melody1, melody2);
		TestCase.assertTrue(morphing.equals(predicted));
		
		dimensions.clear();
		dimensions.addDimension(this.pitchForm, 0, 1, this.pitchForm);
		dimensions.applyChanges();
		predicted = this.noteGenerator.createSimpleMelody(1, 65.7, 71, 68.5, 63, 71.1);
		morphing = this.rubette.getMorphing(melody1, melody2);
		TestCase.assertTrue(morphing.equals(predicted));
	}
	
	public void testMorphDenotatorRelative() {
		Denotator note0 = this.noteGenerator.createNoteDenotator(5,1,6,2,0);
		Denotator note1 = this.noteGenerator.createNoteDenotator(0,4,1,3,0);
		Denotator note2 = this.noteGenerator.createNoteDenotator(3,3,1,1,0);
		Denotator note3 = this.noteGenerator.createNoteDenotator(6,2,5,5,0);
		int[] indices = new int[]{0,2,1};
		int[][] paths = new int[][]{{0,0},{1,0},{3,0}};
		double[][] minAndMax = new double[][]{{0,2,1},{6,4,5}};
		//percentages: 0, 0.35, 0.8
		Denotator predictedNote1 = this.noteGenerator.createNoteDenotator(0,2.95,1,2.2,0);
		//percentages: 0.5, 0.1, 0.5
		Denotator predictedNote2 = this.noteGenerator.createNoteDenotator(4,2.8,1,1.5,0);
		//percentages: 1, 0.6, 0.2
		Denotator predictedNote3 = this.noteGenerator.createNoteDenotator(5,1.4,5,4.4,0);
		Denotator morphedNote1 = null, morphedNote2 = null, morphedNote3 = null;
		try {
			double[] starts = new double[]{0, 0.1, 0.2};
			double[] ends = new double[]{1, 0.6, 0.8};
			morphedNote1 = this.rubette.morphDenotator(note1, note0, indices, paths, minAndMax, starts, ends);
			morphedNote2 = this.rubette.morphDenotator(note2, note0, indices, paths, minAndMax, starts, ends);
			morphedNote3 = this.rubette.morphDenotator(note3, note0, indices, paths, minAndMax, starts, ends);
			/*System.out.println(((RElement)morphedNote2.getElement(new int[]{0,0})).getValue());
			System.out.println(((QElement)morphedNote2.getElement(new int[]{1,0})).getValue());
			System.out.println(((ZElement)morphedNote2.getElement(new int[]{2,0})).getValue());
			System.out.println(((RElement)morphedNote2.getElement(new int[]{3,0})).getValue());/
            TestCase.assertTrue(morphedNote1.equals(predictedNote1));
            TestCase.assertTrue(morphedNote2.equals(predictedNote2));
            TestCase.assertTrue(morphedNote3.equals(predictedNote3));
		} catch (RubatoException e) {
			e.printStackTrace();
		}
	}*/
	
	public void testToAndFromXML() throws Exception {
		File testFile = new File("./arTest");
		if (!testFile.exists()) {
			testFile.createNewFile();
		}
		Reader bufferedReader = new BufferedReader(new FileReader(testFile));
		//PrintStream stream = new PrintStream(new ByteArrayOutputStream());
		XMLWriter writer = new XMLWriter(testFile);
		XMLReader reader = new XMLReader(bufferedReader);
		writer.open();
		this.rubette.setGlobal(true);
		this.rubette.toXML(writer);
		writer.close();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Element element = builder.parse(new InputSource(bufferedReader)).getDocumentElement();
		reader.parse();
		this.rubette = (AlterationRubette)this.rubette.fromXML(reader, element);
		testFile.delete();
		
		TestCase.assertTrue(this.rubette.getDimensionsTable().dimensionCount() == 3);
	}

}

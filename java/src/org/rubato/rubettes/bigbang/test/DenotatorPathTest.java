package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.logeo.DenoFactory;
import org.rubato.logeo.FormFactory;
import org.rubato.math.arith.Rational;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.QElement;
import org.rubato.math.module.RRing;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.ZProperFreeElement;
import org.rubato.math.module.ZProperFreeModule;
import org.rubato.math.module.ZStringProperFreeModule;
import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitForm;
import org.rubato.math.yoneda.PowerForm;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.util.DenotatorPath;

public class DenotatorPathTest extends TestCase {
	
	private TestObjects objects;
	private DenotatorPath satellitePath, modulatorPath, rationalTriplePath, realTriplesPath;
		
	protected void setUp() {
		this.objects = new TestObjects();
		this.satellitePath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0});
		this.modulatorPath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,5,0,5,3,5,2});
		this.rationalTriplePath = new DenotatorPath(this.objects.RATIONAL_TRIPLE_FORM, new int[]{2});
		this.realTriplesPath = new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{5,1});
	}
	
	public void testGeneralMethods() {
		TestCase.assertFalse(this.satellitePath.equals(this.modulatorPath));
		TestCase.assertEquals(-2, this.satellitePath.compareTo(this.modulatorPath));
		TestCase.assertEquals(0, this.satellitePath.compareTo(this.satellitePath));
		TestCase.assertFalse(this.satellitePath.isElementPath());
		TestCase.assertEquals(this.satellitePath, this.satellitePath.getDenotatorSubpath());
		TestCase.assertNull(this.satellitePath.getElementSubpath());
		TestCase.assertEquals(this.objects.SOUND_NOTE_FORM, this.satellitePath.getEndForm());
		TestCase.assertNull(this.satellitePath.getModule());
		
		TestCase.assertTrue(this.rationalTriplePath.isElementPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.RATIONAL_TRIPLE_FORM), this.rationalTriplePath.getDenotatorSubpath());
		TestCase.assertEquals(this.rationalTriplePath, this.rationalTriplePath.getElementSubpath());
		TestCase.assertEquals(this.objects.RATIONAL_TRIPLE_FORM, this.rationalTriplePath.getEndForm());
		TestCase.assertEquals(this.objects.RATIONAL_TRIPLE_MODULE.getComponentModule(2), this.rationalTriplePath.getModule());
		
		TestCase.assertTrue(this.realTriplesPath.isElementPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{5}), this.realTriplesPath.getDenotatorSubpath());
		TestCase.assertEquals(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{1}), this.realTriplesPath.getElementSubpath());
		TestCase.assertEquals(this.objects.REAL_TRIPLE_FORM, this.realTriplesPath.getEndForm());
		TestCase.assertEquals(RRing.ring, this.realTriplesPath.getModule());
		
		DenotatorPath modulatorPath2 = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{4,1,3,0,5,1,5,5});
		TestCase.assertTrue(modulatorPath2.equalsExceptForPowersetIndices(this.modulatorPath));
		TestCase.assertTrue(this.modulatorPath.equalsExceptForPowersetIndices(modulatorPath2));
		DenotatorPath modulatorPath3 = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,0,1,5,1,5,0});
		TestCase.assertFalse(modulatorPath3.equalsExceptForPowersetIndices(this.modulatorPath));
		TestCase.assertFalse(this.modulatorPath.equalsExceptForPowersetIndices(modulatorPath3));
	}
	
	public void testWithIntegerPath() {
		DenotatorPath integerPath = new DenotatorPath(this.objects.INTEGER_FORM, new int[]{});
		TestCase.assertNull(integerPath.getParentPath());
		TestCase.assertNull(integerPath.getPowersetPath(0));
		TestCase.assertNull(integerPath.getPowersetPath(0, this.objects.INTEGER_FORM));
	}
	
	public void testWithSatellitePath() {
		//TestCase.assertFalse(this.satellitePath.isModulatorPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5}), this.satellitePath.getParentPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1}), this.satellitePath.getAnchorPowersetPath());
		//TODO: test ALL possible methods!!!!!
		TestCase.assertEquals(5, this.satellitePath.getObjectIndex());
	}
	
	public void testSatelliteFamilyPaths() {
		ArrayList<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3}));
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}));
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM));
		//System.out.println(this.satellitePath.getAnchorPaths());
		TestCase.assertEquals(parentPaths, this.satellitePath.getAnchorPaths());
		TestCase.assertEquals(parentPaths.get(0), this.satellitePath.getAnchorPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1}), this.satellitePath.getFirstPowersetPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,5}), this.satellitePath.getPowersetPath(1));
		TestCase.assertTrue(this.satellitePath.isDirectSatelliteOf(parentPaths.get(0)));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1}), this.satellitePath.getPowersetPath(0, this.objects.SOUND_NODE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1}), this.satellitePath.getPowersetPath(1, this.objects.SOUND_NODE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1,0,1}), this.satellitePath.getPowersetPath(2, this.objects.SOUND_NODE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,0,5}), this.satellitePath.getPowersetPath(0, this.objects.SOUND_NOTE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,5}), this.satellitePath.getPowersetPath(1, this.objects.SOUND_NOTE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,5,0,5}), this.satellitePath.getPowersetPath(2, this.objects.SOUND_NOTE_FORM));
		//TODO TEST WITH GENERIC SOUND FORM!!!!
		DenotatorPath genericSoundPath = new DenotatorPath(this.objects.GENERIC_SOUND_FORM, new int[]{0});
		TestCase.assertEquals(null, genericSoundPath.getPowersetPath(0, this.objects.GENERIC_SOUND_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.GENERIC_SOUND_FORM, new int[]{1}), genericSoundPath.getPowersetPath(1, this.objects.GENERIC_SOUND_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.GENERIC_SOUND_FORM, new int[]{1,0,1}), genericSoundPath.getPowersetPath(2, this.objects.GENERIC_SOUND_FORM));
	}
	
	public void testModulatorPath() {
		//TestCase.assertTrue(this.modulatorPath.isModulatorPath());
		//TestCase.assertEquals(this.modulatorPath, this.modulatorPath.getParentPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,5,0,5,3,5}), this.modulatorPath.getAnchorPowersetPath());
		TestCase.assertEquals(2, this.modulatorPath.getObjectIndex());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,5,0,5,3}), this.modulatorPath.getAnchorPath());
	}
	
	public void testModulatorFamilyPaths() {
		ArrayList<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,5,0,5,3}));
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,5}));
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}));
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM));
		TestCase.assertEquals(parentPaths, this.modulatorPath.getAnchorPaths());
		TestCase.assertEquals(parentPaths.get(0), this.modulatorPath.getAnchorPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,5,0,5,3,5,2,5}), this.modulatorPath.getFirstPowersetPath());
		TestCase.assertTrue(this.modulatorPath.isDirectSatelliteOf(parentPaths.get(0)));
	}
	
	public void testGetChild() {
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1,4}), this.satellitePath.getSatellitePath(4,0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,5,4}), this.satellitePath.getSatellitePath(4,1));
		//of course, within a modulator, the only powerset is the one of its modulators!!
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,5,0,5,3,5,2,5,4}), this.modulatorPath.getSatellitePath(4,0));
	}
	
	public void testGetPowersetPath() {
		DenotatorPath node = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2});
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1}), node.getPowersetPath(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,0,5}), node.getPowersetPath(1));
	}
	
	public void testInConflictingColimitPositions() {
		DenotatorPath intOrRealPath0 = new DenotatorPath(this.objects.INTEGER_OR_REALS_FORM, new int[]{0,0});
		DenotatorPath intOrRealPath1 = new DenotatorPath(this.objects.INTEGER_OR_REALS_FORM, new int[]{0,1});
		TestCase.assertTrue(intOrRealPath0.inConflictingColimitPositions(intOrRealPath1));
		TestCase.assertTrue(intOrRealPath1.inConflictingColimitPositions(intOrRealPath0));
		TestCase.assertFalse(intOrRealPath0.inConflictingColimitPositions(intOrRealPath0));
		
		DenotatorPath intOrRealPath2 = new DenotatorPath(this.objects.INTEGER_OR_REALS_FORM, new int[]{0});
		TestCase.assertFalse(intOrRealPath0.inConflictingColimitPositions(intOrRealPath2));
		TestCase.assertFalse(intOrRealPath1.inConflictingColimitPositions(intOrRealPath2));
		TestCase.assertFalse(intOrRealPath2.inConflictingColimitPositions(intOrRealPath0));
		TestCase.assertFalse(intOrRealPath2.inConflictingColimitPositions(intOrRealPath1));
		
		DenotatorPath intOrRealPath3 = new DenotatorPath(this.objects.INTEGER_OR_REALS_FORM, new int[]{});
		TestCase.assertFalse(intOrRealPath0.inConflictingColimitPositions(intOrRealPath3));
		TestCase.assertFalse(intOrRealPath1.inConflictingColimitPositions(intOrRealPath3));
		TestCase.assertFalse(intOrRealPath3.inConflictingColimitPositions(intOrRealPath0));
		TestCase.assertFalse(intOrRealPath3.inConflictingColimitPositions(intOrRealPath1));
	}
	
	public void testOtherStuffForThesisTest() throws RubatoException {
		SimpleForm onset = FormFactory.makeQModuleForm("Onset");
		Module eulerPitchSpace = ZProperFreeModule.make(3);
		SimpleForm eulerPitch = FormFactory.makeModuleForm("EulerPitch", eulerPitchSpace);
		Module loudnessSpace = ZStringProperFreeModule.make(1);
		SimpleForm loudness = FormFactory.makeModuleForm("Loudness", loudnessSpace);
		SimpleForm duration = FormFactory.makeQModuleForm("Duration");
		LimitForm eulerNote = FormFactory.makeLimitForm("EulerNote", onset, eulerPitch, loudness, duration);
		LimitForm rest = FormFactory.makeLimitForm("Rest", onset, duration);
		ColimitForm eulerNoteOrRest = FormFactory.makeColimitForm("EulerNoteOrRest", eulerNote, rest);
		PowerForm eulerScore = FormFactory.makePowerForm("EulerScore", eulerNoteOrRest);
		
		SimpleDenotator onset1 = DenoFactory.makeDenotator(onset, new Rational(0));
		ModuleElement pitch1Element = ZProperFreeElement.make(new int[]{1, 0, -1});
		SimpleDenotator pitch1 = DenoFactory.makeDenotator(eulerPitch, pitch1Element);
		SimpleDenotator loudness1 = DenoFactory.makeDenotator(loudness, "sfz");
		SimpleDenotator duration1 = DenoFactory.makeDenotator(duration, new Rational(1, 4));
		Denotator note1 = DenoFactory.makeDenotator(eulerNote, onset1, pitch1, loudness1, duration1);
		Denotator noteOne = DenoFactory.makeDenotator(eulerNoteOrRest, 0, note1);
		
		SimpleDenotator onsetAtBeat2 = DenoFactory.makeDenotator(onset, new Rational(1, 4));
		Denotator rest1 = DenoFactory.makeDenotator(rest, onsetAtBeat2, duration1);
		Denotator shortRest = DenoFactory.makeDenotator(eulerNoteOrRest, 1, rest1);
		
		SimpleDenotator onset2 = DenoFactory.makeDenotator(onset, new Rational(1, 2));
		ModuleElement pitch2Element = ZProperFreeElement.make(new int[]{-1, 1, 1});
		SimpleDenotator pitch2 = DenoFactory.makeDenotator(eulerPitch, pitch2Element);
		SimpleDenotator loudness2 = DenoFactory.makeDenotator(loudness, "ppp");
		SimpleDenotator duration2 = DenoFactory.makeDenotator(duration, new Rational(3, 2));
		Denotator note2 = DenoFactory.makeDenotator(eulerNote, onset2, pitch2, loudness2, duration2);
		Denotator noteTwo = DenoFactory.makeDenotator(eulerNoteOrRest, 0, note2);
		
		Denotator twoNoteScore = DenoFactory.makeDenotator(eulerScore, noteOne, shortRest, noteTwo);
		
		Denotator pitchOfNoteTwo = twoNoteScore.get(new int[]{1,0,1});
		//TODO DID THIS WORK AT SOME POINT???
		//pitchOfNoteTwo.display();
		//System.out.println(pitchOfNoteTwo.getElement(new int[]{0}));
		//int thirdValue = ((ZElement)twoNoteScore.getElement(new int[]{1,0,1,0})).getValue();
		//System.out.println(thirdValue);
		
		//twoNoteScore.display();
	}

}

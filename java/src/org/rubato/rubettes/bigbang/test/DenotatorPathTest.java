package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.rubato.math.module.RRing;
import org.rubato.rubettes.util.DenotatorPath;

public class DenotatorPathTest extends TestCase {
	
	private TestObjects objects;
	private DenotatorPath satellitePath, modulatorPath, rationalTriplePath, realTriplesPath;
		
	protected void setUp() {
		this.objects = new TestObjects();
		this.satellitePath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0});
		this.modulatorPath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3,6,2});
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
		
		DenotatorPath modulatorPath2 = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{4,1,3,0,6,1,6,5});
		TestCase.assertTrue(modulatorPath2.equalsExceptForPowersetIndices(this.modulatorPath));
		TestCase.assertTrue(this.modulatorPath.equalsExceptForPowersetIndices(modulatorPath2));
		DenotatorPath modulatorPath3 = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,0,1,6,1,6,0});
		TestCase.assertFalse(modulatorPath3.equalsExceptForPowersetIndices(this.modulatorPath));
		TestCase.assertFalse(this.modulatorPath.equalsExceptForPowersetIndices(modulatorPath3));
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
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,6}), this.satellitePath.getPowersetPath(1));
		TestCase.assertTrue(this.satellitePath.isSatelliteOf(parentPaths.get(0)));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1}), this.satellitePath.getPowersetPath(0, this.objects.SOUND_NODE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1}), this.satellitePath.getPowersetPath(1, this.objects.SOUND_NODE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1,0,1}), this.satellitePath.getPowersetPath(2, this.objects.SOUND_NODE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,0,6}), this.satellitePath.getPowersetPath(0, this.objects.SOUND_NOTE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,6}), this.satellitePath.getPowersetPath(1, this.objects.SOUND_NOTE_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,6,0,6}), this.satellitePath.getPowersetPath(2, this.objects.SOUND_NOTE_FORM));
		//TODO TEST WITH GENERIC SOUND FORM!!!!
		DenotatorPath genericSoundPath = new DenotatorPath(this.objects.GENERIC_SOUND_FORM, new int[]{0});
		TestCase.assertEquals(null, genericSoundPath.getPowersetPath(0, this.objects.GENERIC_SOUND_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.GENERIC_SOUND_FORM, new int[]{1}), genericSoundPath.getPowersetPath(1, this.objects.GENERIC_SOUND_FORM));
		TestCase.assertEquals(new DenotatorPath(this.objects.GENERIC_SOUND_FORM, new int[]{1,0,1}), genericSoundPath.getPowersetPath(2, this.objects.GENERIC_SOUND_FORM));
	}
	
	public void testModulatorPath() {
		//TestCase.assertTrue(this.modulatorPath.isModulatorPath());
		//TestCase.assertEquals(this.modulatorPath, this.modulatorPath.getParentPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3,6}), this.modulatorPath.getAnchorPowersetPath());
		TestCase.assertEquals(2, this.modulatorPath.getObjectIndex());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3}), this.modulatorPath.getAnchorPath());
	}
	
	public void testModulatorFamilyPaths() {
		ArrayList<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3}));
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,6}));
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}));
		parentPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM));
		TestCase.assertEquals(parentPaths, this.modulatorPath.getAnchorPaths());
		TestCase.assertEquals(parentPaths.get(0), this.modulatorPath.getAnchorPath());
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3,6,2,6}), this.modulatorPath.getFirstPowersetPath());
		TestCase.assertTrue(this.modulatorPath.isSatelliteOf(parentPaths.get(0)));
	}
	
	public void testGetChild() {
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1,4}), this.satellitePath.getSatellitePath(4,0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,6,4}), this.satellitePath.getSatellitePath(4,1));
		//of course, within a modulator, the only powerset is the one of its modulators!!
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3,6,2,6,4}), this.modulatorPath.getSatellitePath(4,0));
	}
	
	public void testGetPowersetPath() {
		DenotatorPath node = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2});
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,1}), node.getPowersetPath(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2,0,6}), node.getPowersetPath(1));
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

}

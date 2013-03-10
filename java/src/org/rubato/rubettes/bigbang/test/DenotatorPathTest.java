package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.rubato.base.Repository;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.util.DenotatorPath;

public class DenotatorPathTest extends TestCase {
	
	private final Form SOUND_SCORE_FORM = Repository.systemRepository().getForm("SoundScore");
	private final DenotatorPath SATELLITE_PATH = new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0});
	private final DenotatorPath MODULATOR_PATH = new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3,6,2});
	
	protected void setUp() {
	}
	
	public void testGeneralMethods() {
		TestCase.assertFalse(this.SATELLITE_PATH.equals(this.MODULATOR_PATH));
		TestCase.assertTrue(this.SATELLITE_PATH.compareTo(this.MODULATOR_PATH) < 0);
	}
	
	public void testWithSatellitePath() {
		//TestCase.assertFalse(this.SATELLITE_PATH.isModulatorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,3,1,5}), this.SATELLITE_PATH.getParentPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,3,1}), this.SATELLITE_PATH.getAnchorPowersetPath());
		//TODO: test ALL possible methods!!!!!
		TestCase.assertEquals(5, this.SATELLITE_PATH.getObjectIndex());
	}
	
	public void testSatelliteFamilyPaths() {
		ArrayList<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,3}));
		parentPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2}));
		parentPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM));
		//System.out.println(this.SATELLITE_PATH.getAnchorPaths());
		TestCase.assertEquals(parentPaths, this.SATELLITE_PATH.getAnchorPaths());
		TestCase.assertEquals(parentPaths.get(0), this.SATELLITE_PATH.getAnchorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1}), this.SATELLITE_PATH.getFirstPowersetPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,6}), this.SATELLITE_PATH.getPowersetPath(1));
		TestCase.assertTrue(this.SATELLITE_PATH.isChildOf(parentPaths.get(0)));
	}
	
	public void testModulatorPath() {
		//TestCase.assertTrue(this.MODULATOR_PATH.isModulatorPath());
		//TestCase.assertEquals(this.MODULATOR_PATH, this.MODULATOR_PATH.getParentPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3,6}), this.MODULATOR_PATH.getAnchorPowersetPath());
		TestCase.assertEquals(2, this.MODULATOR_PATH.getObjectIndex());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3}), this.MODULATOR_PATH.getAnchorPath());
	}
	
	public void testModulatorFamilyPaths() {
		ArrayList<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3}));
		parentPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,6}));
		parentPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2}));
		parentPaths.add(new DenotatorPath(this.SOUND_SCORE_FORM));
		TestCase.assertEquals(parentPaths, this.MODULATOR_PATH.getAnchorPaths());
		TestCase.assertEquals(parentPaths.get(0), this.MODULATOR_PATH.getAnchorPath());
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3,6,2,6}), this.MODULATOR_PATH.getFirstPowersetPath());
		TestCase.assertTrue(this.MODULATOR_PATH.isChildOf(parentPaths.get(0)));
	}
	
	public void testGetChild() {
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,1,4}), this.SATELLITE_PATH.getSatellitePath(4,0));
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,3,1,5,0,6,4}), this.SATELLITE_PATH.getSatellitePath(4,1));
		//of course, within a modulator, the only powerset is the one of its modulators!!
		TestCase.assertEquals(new DenotatorPath(this.SOUND_SCORE_FORM, new int[]{2,1,6,0,6,3,6,2,6,4}), this.MODULATOR_PATH.getSatellitePath(4,0));
	}

}

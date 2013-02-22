package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.rubato.rubettes.util.DenotatorPath;

public class NotePathTest extends TestCase {
	
	private final DenotatorPath SATELLITE_PATH = new DenotatorPath(new int[]{2,1,3,1,5,0});
	private final DenotatorPath MODULATOR_PATH = new DenotatorPath(new int[]{2,1,6,0,6,3,6,2});
	
	protected void setUp() {
	}
	
	public void testGeneralMethods() {
		TestCase.assertFalse(this.SATELLITE_PATH.equals(this.MODULATOR_PATH));
		TestCase.assertTrue(this.SATELLITE_PATH.compareTo(this.MODULATOR_PATH) < 0);
	}
	
	public void testSatellitePath() {
		TestCase.assertFalse(this.SATELLITE_PATH.isModulatorPath());
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,3,1,5}), this.SATELLITE_PATH.getElementPath());
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,3,1}), this.SATELLITE_PATH.getPowersetPath());
		TestCase.assertEquals(5, this.SATELLITE_PATH.getNoteIndex());
	}
	
	public void testSatelliteFamilyPaths() {
		ArrayList<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(new int[]{2,1,3,0}));
		parentPaths.add(new DenotatorPath(new int[]{2,0}));
		TestCase.assertEquals(parentPaths, this.SATELLITE_PATH.getParentPaths());
		TestCase.assertEquals(parentPaths.get(0), this.SATELLITE_PATH.getParentPath());
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,3,1,5,1}), this.SATELLITE_PATH.getChildrenPath());
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,3,1,5,0,6}), this.SATELLITE_PATH.getModulatorsPath());
		TestCase.assertTrue(this.SATELLITE_PATH.isChildOf(parentPaths.get(0)));
	}
	
	public void testModulatorPath() {
		TestCase.assertTrue(this.MODULATOR_PATH.isModulatorPath());
		TestCase.assertEquals(this.MODULATOR_PATH, this.MODULATOR_PATH.getElementPath());
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,6,0,6,3,6}), this.MODULATOR_PATH.getPowersetPath());
		TestCase.assertEquals(2, this.MODULATOR_PATH.getNoteIndex());
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,6,0,6,3}), this.MODULATOR_PATH.getParentPath());
	}
	
	public void testModulatorFamilyPaths() {
		ArrayList<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		parentPaths.add(new DenotatorPath(new int[]{2,1,6,0,6,3}));
		parentPaths.add(new DenotatorPath(new int[]{2,1,6,0}));
		parentPaths.add(new DenotatorPath(new int[]{2,0}));
		TestCase.assertEquals(this.MODULATOR_PATH.getParentPaths(), parentPaths);
		TestCase.assertEquals(this.MODULATOR_PATH.getParentPath(), parentPaths.get(0));
		TestCase.assertEquals(this.MODULATOR_PATH.getChildrenPath(), new DenotatorPath(new int[]{2,1,6,0,6,3,6,2,6}));
		TestCase.assertTrue(this.MODULATOR_PATH.isChildOf(parentPaths.get(0)));
	}
	
	public void testGetChild() {
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,3,1,5,1,4,0}), this.SATELLITE_PATH.getChildPath(4,false));
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,3,1,5,0,6,4}), this.SATELLITE_PATH.getChildPath(4,true));
		TestCase.assertEquals(new DenotatorPath(new int[]{2,1,6,0,6,3,6,2,6,4}), this.MODULATOR_PATH.getChildPath(4,true));
	}

}

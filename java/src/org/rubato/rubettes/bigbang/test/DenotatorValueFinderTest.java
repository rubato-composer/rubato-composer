package org.rubato.rubettes.bigbang.test;

import junit.framework.TestCase;

import org.rubato.rubettes.util.DenotatorValueFinder;

public class DenotatorValueFinderTest extends TestCase {
	
	private TestObjects testObjects;
	
	protected void setUp() {
		this.testObjects = new TestObjects();
	}
	
	public void testWithMacroScore() {
		DenotatorValueFinder finder = new DenotatorValueFinder(this.testObjects.MACRO_SCORE_FORM, true);
		TestCase.assertEquals(5, finder.getValueNamesInFoundOrder().size());
		TestCase.assertEquals(5, finder.getValuePathsInFoundOrder().size());
		TestCase.assertEquals(5, finder.getValueNamesAndPaths().size());
		TestCase.assertEquals(1, finder.getObjectsInFoundOrder().size());
		TestCase.assertEquals(1, finder.getObjectsAndPaths().size());
		TestCase.assertEquals(0, finder.getColimitsInFoundOrder().size());
		TestCase.assertEquals(0, finder.getColimitsAndPaths().size());
		TestCase.assertEquals(true, finder.formAllowsForSatellites());
		TestCase.assertEquals(false, finder.formContainsColimit());
	}
	
	public void testWithSoundScore() {
		DenotatorValueFinder finder = new DenotatorValueFinder(this.testObjects.SOUND_SCORE_FORM, true);
		TestCase.assertEquals(6, finder.getValueNamesInFoundOrder().size());
		TestCase.assertEquals(6, finder.getValuePathsInFoundOrder().size());
		TestCase.assertEquals(6, finder.getValueNamesAndPaths().size());
		TestCase.assertEquals(2, finder.getObjectsInFoundOrder().size());
		TestCase.assertEquals(2, finder.getObjectsAndPaths().size());
		TestCase.assertEquals(0, finder.getColimitsInFoundOrder().size());
		TestCase.assertEquals(0, finder.getColimitsAndPaths().size());
		TestCase.assertEquals(true, finder.formAllowsForSatellites());
		TestCase.assertEquals(false, finder.formContainsColimit());
	}
	
	public void testWithHarmonicSpectrum() {
		DenotatorValueFinder finder = new DenotatorValueFinder(this.testObjects.HARMONIC_SPECTRUM_FORM, true);
		TestCase.assertEquals(3, finder.getValueNamesInFoundOrder().size());
		TestCase.assertEquals(3, finder.getValuePathsInFoundOrder().size());
		TestCase.assertEquals(3, finder.getValueNamesAndPaths().size());
		TestCase.assertEquals(2, finder.getObjectsInFoundOrder().size());
		TestCase.assertEquals(2, finder.getObjectsAndPaths().size());
		TestCase.assertEquals(0, finder.getColimitsInFoundOrder().size());
		TestCase.assertEquals(0, finder.getColimitsAndPaths().size());
		TestCase.assertEquals(true, finder.formAllowsForSatellites());
		TestCase.assertEquals(false, finder.formContainsColimit());
	}
	
	public void testWithIntegerOrReals() {
		DenotatorValueFinder finder = new DenotatorValueFinder(this.testObjects.INTEGER_OR_REALS_FORM, true);
		TestCase.assertEquals(2, finder.getValueNamesInFoundOrder().size());
		TestCase.assertEquals(2, finder.getValuePathsInFoundOrder().size());
		TestCase.assertEquals(2, finder.getValueNamesAndPaths().size());
		TestCase.assertEquals(1, finder.getObjectsInFoundOrder().size());
		TestCase.assertEquals(1, finder.getObjectsAndPaths().size());
		TestCase.assertEquals(1, finder.getColimitsInFoundOrder().size());
		TestCase.assertEquals(1, finder.getColimitsAndPaths().size());
		TestCase.assertEquals(false, finder.formAllowsForSatellites());
		TestCase.assertEquals(true, finder.formContainsColimit());
	}

}

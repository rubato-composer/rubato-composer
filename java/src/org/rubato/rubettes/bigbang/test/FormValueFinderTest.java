package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.rubato.rubettes.util.CoolFormRegistrant;
import org.rubato.rubettes.util.FormValueFinder;

public class FormValueFinderTest extends TestCase {
	
	private TestObjects testObjects;
	
	protected void setUp() {
		this.testObjects = new TestObjects();
	}
	
	public void testWithMacroScore() {
		FormValueFinder finder = new FormValueFinder(this.testObjects.MACRO_SCORE_FORM, true);
		TestCase.assertEquals(5, finder.getCoordinateSystemValueNames().size());
		TestCase.assertEquals(1, finder.getObjectCount());
		TestCase.assertEquals(5, finder.getObjectAt(0).getColimitConfigurationValueNames(this.createColimitConfiguration()).size());
		TestCase.assertEquals(5, finder.getObjectAt(0).getColimitConfigurationValuePaths(this.createColimitConfiguration()).size());
		TestCase.assertEquals(0, finder.getObjectAt(0).getColimits().size());
		TestCase.assertEquals(0, finder.getObjectAt(0).getColimitPaths().size());
		TestCase.assertEquals(true, finder.formAllowsForSatellites());
		TestCase.assertEquals(false, finder.formContainsColimits());
	}
	
	public void testWithSoundScore() {
		FormValueFinder finder = new FormValueFinder(this.testObjects.SOUND_SCORE_FORM, true);
		TestCase.assertEquals(5, finder.getCoordinateSystemValueNames().size());
		TestCase.assertEquals(2, finder.getObjectCount());
		TestCase.assertEquals(5, finder.getObjectAt(0).getColimitConfigurationValueNames(this.createColimitConfiguration()).size());
		TestCase.assertEquals(5, finder.getObjectAt(0).getColimitConfigurationValuePaths(this.createColimitConfiguration()).size());
		TestCase.assertEquals(0, finder.getObjectAt(0).getColimits().size());
		TestCase.assertEquals(0, finder.getObjectAt(0).getColimitPaths().size());
		TestCase.assertEquals(true, finder.formAllowsForSatellites());
		TestCase.assertEquals(false, finder.formContainsColimits());
	}
	
	public void testWithHarmonicSpectrum() {
		FormValueFinder finder = new FormValueFinder(this.testObjects.HARMONIC_SPECTRUM_FORM, true);
		TestCase.assertEquals(3, finder.getCoordinateSystemValueNames().size());
		TestCase.assertEquals(2, finder.getObjectCount());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimitConfigurationValueNames(this.createColimitConfiguration()).size());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimitConfigurationValuePaths(this.createColimitConfiguration()).size());
		TestCase.assertEquals(0, finder.getObjectAt(0).getColimits().size());
		TestCase.assertEquals(0, finder.getObjectAt(0).getColimitPaths().size());
		TestCase.assertEquals(2, finder.getObjectAt(1).getColimitConfigurationValueNames(this.createColimitConfiguration()).size());
		TestCase.assertEquals(2, finder.getObjectAt(1).getColimitConfigurationValuePaths(this.createColimitConfiguration()).size());
		TestCase.assertEquals(0, finder.getObjectAt(1).getColimits().size());
		TestCase.assertEquals(0, finder.getObjectAt(1).getColimitPaths().size());
		TestCase.assertEquals(true, finder.formAllowsForSatellites());
		TestCase.assertEquals(false, finder.formContainsColimits());
	}
	
	public void testWithIntegerOrReals() {
		FormValueFinder finder = new FormValueFinder(this.testObjects.INTEGER_OR_REALS_FORM, true);
		TestCase.assertEquals(2, finder.getCoordinateSystemValueNames().size());
		TestCase.assertEquals(1, finder.getObjectCount());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimits().size());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimitPaths().size());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimitConfigurationValueNames(this.createColimitConfiguration(0)).size());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimitConfigurationValuePaths(this.createColimitConfiguration(0)).size());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimitConfigurationValueNames(this.createColimitConfiguration(1)).size());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimitConfigurationValuePaths(this.createColimitConfiguration(1)).size());
		TestCase.assertEquals(false, finder.formAllowsForSatellites());
		TestCase.assertEquals(true, finder.formContainsColimits());
	}
	
	public void testWithGeneralScore() {
		FormValueFinder finder = new FormValueFinder(this.testObjects.GENERAL_SCORE_FORM, true);
		TestCase.assertEquals(5, finder.getCoordinateSystemValueNames().size());
		TestCase.assertEquals(1, finder.getObjectCount());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimits().size());
		TestCase.assertEquals(1, finder.getObjectAt(0).getColimitPaths().size());
		TestCase.assertEquals(5, finder.getObjectAt(0).getColimitConfigurationValueNames(this.createColimitConfiguration(0)).size());
		TestCase.assertEquals(5, finder.getObjectAt(0).getColimitConfigurationValuePaths(this.createColimitConfiguration(0)).size());
		TestCase.assertEquals(3, finder.getObjectAt(0).getColimitConfigurationValueNames(this.createColimitConfiguration(1)).size());
		TestCase.assertEquals(3, finder.getObjectAt(0).getColimitConfigurationValuePaths(this.createColimitConfiguration(1)).size());
		TestCase.assertEquals(false, finder.formAllowsForSatellites());
		TestCase.assertEquals(true, finder.formContainsColimits());
	}
	
	public void testWithDyad() {
		FormValueFinder finder = new FormValueFinder(CoolFormRegistrant.DYADS_FORM, true);
		TestCase.assertEquals(2, finder.getCoordinateSystemValueNames().size());
		TestCase.assertEquals(1, finder.getObjectCount());
		TestCase.assertEquals(2, finder.getObjectAt(0).getColimitConfigurationValueNames(this.createColimitConfiguration()).size());
		TestCase.assertEquals(2, finder.getObjectAt(0).getColimitConfigurationValuePaths(this.createColimitConfiguration()).size());
		TestCase.assertEquals(0, finder.getObjectAt(0).getColimits().size());
		TestCase.assertEquals(0, finder.getObjectAt(0).getColimitPaths().size());
		TestCase.assertEquals(false, finder.formAllowsForSatellites());
		TestCase.assertEquals(false, finder.formContainsColimits());
	}
	
	private List<Integer> createColimitConfiguration(Integer... integers) {
		return new ArrayList<Integer>(Arrays.asList(integers));
	}

}

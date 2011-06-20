package org.rubato.rubettes.bigbang.test;

import org.rubato.rubettes.bigbang.view.model.ViewParameter;

import junit.framework.TestCase;


public class ViewParameterTest extends TestCase {
	
	protected void setUp() {
	}
	
	public void testGetLimitedValue() {
		ViewParameter relative = new ViewParameter("Test", false, 0, 0, 1, true);
		TestCase.assertTrue(relative.getLimitedValue(2,0,5) == 2);
		TestCase.assertTrue(relative.getLimitedValue(6,0,5) == 1);
		TestCase.assertTrue(relative.getLimitedValue(-7,0,5) == 3);
		relative = new ViewParameter("Test", false, 0, 0, 1, false);
		TestCase.assertTrue(relative.getLimitedValue(2,0,5) == 2);
		TestCase.assertTrue(relative.getLimitedValue(6,0,5) == 5);
		TestCase.assertTrue(relative.getLimitedValue(-2,0,5) == 0);
	}
}

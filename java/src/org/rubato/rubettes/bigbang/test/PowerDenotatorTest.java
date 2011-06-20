package org.rubato.rubettes.bigbang.test;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.util.MacroNoteGenerator;

import junit.framework.TestCase;

public class PowerDenotatorTest extends TestCase {
	
	private final double[][] RELATIVE = new double[][]{
			{0,60,120,1,0,0},{1,3,-4,0,0,0},{1,-3,5,0,1,0}};
	
	private MacroNoteGenerator generator;
	private PowerDenotator macroScore;
	
	protected void setUp() {
		this.generator = new MacroNoteGenerator();
		this.macroScore = this.generator.createMultiLevelMacroScore(this.RELATIVE);
	}
	
	public void testCopy() throws RubatoException {
		PowerDenotator copy = this.macroScore.copy();
		TestCase.assertEquals(copy, this.macroScore);
		TestCase.assertTrue(copy != this.macroScore);
		this.checkIfDeepCopied(copy, this.macroScore, new int[]{0,1,0});
		this.checkIfDeepCopied(copy, this.macroScore, new int[]{0,1,0,1,0});
	}
	
	private void checkIfDeepCopied(Denotator d1, Denotator d2, int[] path) throws RubatoException {
		Denotator insideD1 = d1.get(path);
		Denotator insideD2 = d2.get(path);
		TestCase.assertEquals(insideD1, insideD2);
		TestCase.assertTrue(insideD1 != insideD2);
	}

}

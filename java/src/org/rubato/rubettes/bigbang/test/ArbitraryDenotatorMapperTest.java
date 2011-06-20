package org.rubato.rubettes.bigbang.test;

import org.rubato.base.RubatoException;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.util.ArbitraryDenotatorMapper;
import org.rubato.rubettes.util.SoundNoteGenerator;

import junit.framework.TestCase;

public class ArbitraryDenotatorMapperTest  extends TestCase {
	
	private final double[][] RELATIVE = new double[][]{
			{0,60,120,1,0,1},{1,3,-4,0,0,1},{1,-3,5,0,1,1}};
	
	private LimitDenotator node0, node1Absolute, node1Relative, node2Absolute, node2Relative; 
	private PowerDenotator multiLevelMacroScore;
	
	public void setUp() {
		SoundNoteGenerator generator = new SoundNoteGenerator();
		this.node0 = generator.createNodeDenotator(new double[]{0,60,120,1,0,1});
		this.node1Absolute = generator.createNodeDenotator(new double[]{1,63,116,1,0,1});
		this.node1Relative = generator.createNodeDenotator(new double[]{1,3,-4,0,0,1});
		this.node2Absolute = generator.createNodeDenotator(new double[]{2,60,121,1,1,1});
		this.node2Relative = generator.createNodeDenotator(new double[]{1,-3,5,0,1,1});
		this.multiLevelMacroScore = generator.createMultiLevelSoundScore(this.RELATIVE);
	}
	
	public void testGetMappedPowerDenotator() throws RubatoException {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		ModuleMorphism translation = RFreeAffineMorphism.make(identity, new double[]{-1,-2});
		int[][] elementPaths = new int[][]{{0,0},{0,1},{0,0},{0,1}};
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(translation, elementPaths);
		LimitDenotator node = (LimitDenotator)this.multiLevelMacroScore.get(new int[]{0});
		LimitDenotator mappedNode = (LimitDenotator)mapper.getMappedDenotator(node);
		//check if transformed properly
		LimitDenotator expectedNode = new SoundNoteGenerator().createNodeDenotator(new double[]{-1,58,120,1,0,1});
		this.checkNoteValuesEqual(mappedNode, expectedNode);
		//check, if satellites still there and unchanged
		this.checkNoteValuesEqual(mappedNode.get(new int[]{1,0}), this.node1Relative);
		this.checkNoteValuesEqual(mappedNode.get(new int[]{1,0,1,0}), this.node2Relative);
		//check if layer unchanged
		TestCase.assertEquals(mappedNode.getElement(new int[]{0,5,0}), new ZElement(1));
	}
	
	private void checkNoteValuesEqual(Denotator node1, Denotator node2) throws RubatoException {
		for (int i = 0; i < 5; i++) {
			TestCase.assertEquals(node1.get(new int[]{0,i}).getCoordinate(), node2.get(new int[]{0,i}).getCoordinate());
		}
	}

}

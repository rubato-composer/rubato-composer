package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.List;

import org.rubato.base.RubatoException;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.util.ArbitraryDenotatorMapper;
import org.rubato.rubettes.util.DenotatorPath;

import junit.framework.TestCase;

public class ArbitraryDenotatorMapperTest  extends TestCase {
	
	private TestObjects objects;
	
	public void setUp() {
		this.objects = new TestObjects();
	}
	
	public void testGetMappedPowerDenotator() throws RubatoException {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		ModuleMorphism translation = RFreeAffineMorphism.make(identity, new double[]{-1,-2});
		List<DenotatorPath> elementPaths = new ArrayList<DenotatorPath>();
		elementPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0}));
		elementPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1}));
		elementPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0}));
		elementPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1}));
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(translation, elementPaths);
		LimitDenotator node = (LimitDenotator)this.objects.multiLevelMacroScore.get(new int[]{0});
		LimitDenotator mappedNode = (LimitDenotator)mapper.getMappedDenotator(node);
		//check if transformed properly
		LimitDenotator expectedNode = this.objects.generator.createNodeDenotator(new double[]{-1,58,120,1,0,0});
		this.objects.assertEqualDenotators(mappedNode, expectedNode);
		//check, if satellites still there and unchanged
		this.objects.assertEqualDenotators(mappedNode.get(new int[]{1,0}), this.objects.node1Relative);
		this.objects.assertEqualDenotators(mappedNode.get(new int[]{1,0,1,0}), this.objects.node2Relative);
		//check if layer unchanged
		TestCase.assertEquals(mappedNode.getElement(new int[]{0,5,0}), new ZElement(0));
	}

}

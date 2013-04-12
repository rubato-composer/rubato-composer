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
	private ModuleMorphism translation;
	
	public void setUp() {
		this.objects = new TestObjects();
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		this.translation = RFreeAffineMorphism.make(identity, new double[]{-1,-2});
	}
	
	public void testMappingOfNodeDenotators() throws RubatoException {
		List<DenotatorPath> elementPaths = new ArrayList<DenotatorPath>();
		elementPaths.add(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,0}));
		elementPaths.add(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,1}));
		elementPaths.add(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,0}));
		elementPaths.add(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,1}));
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(this.translation, elementPaths);
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
	
	public void testMappingOfColimitDenotators() throws RubatoException {
		List<DenotatorPath> valuePaths = new ArrayList<DenotatorPath>();
		//add paths of both types of coordinates: integer and real
		valuePaths.add(new DenotatorPath(this.objects.INTEGER_OR_REAL_FORM, new int[]{0}));
		valuePaths.add(new DenotatorPath(this.objects.INTEGER_OR_REAL_FORM, new int[]{1}));
		valuePaths.add(new DenotatorPath(this.objects.INTEGER_OR_REAL_FORM, new int[]{0}));
		valuePaths.add(new DenotatorPath(this.objects.INTEGER_OR_REAL_FORM, new int[]{1}));
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(this.translation, valuePaths);
		
		//test transformation of coordinates directly
		Denotator currentColimit = this.objects.integerOrReals.get(new int[]{0});
		Denotator mappedColimit = mapper.getMappedDenotator(currentColimit);
		Denotator expectedColimit = this.objects.createIntegerOrReal(true, 3);
		this.objects.assertEqualDenotators(expectedColimit, mappedColimit);
		currentColimit = this.objects.integerOrReals.get(new int[]{3});
		mappedColimit = mapper.getMappedDenotator(currentColimit);
		expectedColimit = this.objects.createIntegerOrReal(false, 1.5);
		this.objects.assertEqualDenotators(expectedColimit, mappedColimit);
		
		//test transformation of powerset
		mapper.getMappedPowerDenotator(this.objects.integerOrReals).display();
		Denotator mappedPowerset = mapper.getMappedPowerDenotator(this.objects.integerOrReals);
		expectedColimit = this.objects.createIntegerOrReal(true, 3);
		this.objects.assertEqualDenotators(expectedColimit, mappedPowerset.get(new int[]{0}));
		expectedColimit = this.objects.createIntegerOrReal(true, 4);
		this.objects.assertEqualDenotators(expectedColimit, mappedPowerset.get(new int[]{1}));
		expectedColimit = this.objects.createIntegerOrReal(false, 0.5);
		this.objects.assertEqualDenotators(expectedColimit, mappedPowerset.get(new int[]{2}));
		expectedColimit = this.objects.createIntegerOrReal(false, 1.5);
		this.objects.assertEqualDenotators(expectedColimit, mappedPowerset.get(new int[]{3}));
		
		
	}

}

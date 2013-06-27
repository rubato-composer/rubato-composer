package org.rubato.rubettes.bigbang.test;

import java.util.Arrays;

import org.rubato.base.RubatoException;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
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
		//create paths to map onset x pitch -> onset x pitch
		TransformationPaths paths = this.objects.createStandardTransformationPaths(
				this.objects.SOUND_NODE_FORM, new int[][]{{0,0},{0,1}});
		//init mapper
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(this.translation, paths);
		
		LimitDenotator node = (LimitDenotator)this.objects.multiLevelMacroScore.get(new int[]{0});
		LimitDenotator mappedNode = (LimitDenotator)mapper.getMappedDenotator(node);
		//check if transformed properly and satellites still there and unchanged
		LimitDenotator expectedNode = this.objects.createMultilevelNode(new double[][]{{-1,58,120,1,0,0},{1,3,-4,0,0,0},{1,-3,5,0,1,0}});
		this.objects.assertEqualDenotators(mappedNode, expectedNode);
		//check if layer unchanged
		TestCase.assertEquals(mappedNode.getElement(new int[]{0,5,0}), new ZElement(0));
	}
	
	public void testMappingOfColimitDenotators() throws RubatoException {
		//create paths to map int x real -> int x real
		TransformationPaths paths = this.objects.createStandardTransformationPaths(
				this.objects.INTEGER_OR_REAL_FORM, new int[][]{{0},{1}});
		//init mapper
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(this.translation, paths);
		
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
	
	public void testMappingOfColimitDenotators2() throws RubatoException {
		TransformationPaths paths = new TransformationPaths();
		//add Duration paths for both types of configurations: note and rest
		DenotatorPath noteDurationPath = new DenotatorPath(this.objects.GENERAL_SCORE_FORM, new int[]{0,0,3});
		DenotatorPath restDurationPath = new DenotatorPath(this.objects.GENERAL_SCORE_FORM, new int[]{0,1,1});
		paths.setDomainPaths(0, Arrays.asList(noteDurationPath, restDurationPath));
		paths.setCodomainPaths(0, Arrays.asList(noteDurationPath, restDurationPath));
		//add Pitch paths only for notes
		DenotatorPath notePitchPath = new DenotatorPath(this.objects.GENERAL_SCORE_FORM, new int[]{0,0,1});
		paths.setDomainPaths(1, Arrays.asList(notePitchPath));
		paths.setCodomainPaths(1, Arrays.asList(notePitchPath));
		//init mapper
		ArbitraryDenotatorMapper mapper = new ArbitraryDenotatorMapper(this.translation, paths);
		
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

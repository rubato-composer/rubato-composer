package org.rubato.rubettes.bigbang.test;

import java.util.Map;
import java.util.TreeMap;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.ObjectGenerator;

import junit.framework.TestCase;

public class ObjectGeneratorTest extends TestCase {
	
	TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
	}
	
	public void testCreateObject() {
		double[] noteValues = new double[]{0.0, 60.0, 120.0, 1.0, 0.0, 0.0};
		Map<DenotatorPath,Double> pathsWithValues = new TreeMap<DenotatorPath,Double>();
		//only set middle three values, others will be left zero
		for (int i = 1; i < noteValues.length-1; i++) {
			pathsWithValues.put(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,i}), noteValues[i]);
		}
		LimitDenotator expectedNode = this.objects.generator.createNodeDenotator(noteValues);
		Denotator generatedObject = new ObjectGenerator().createObject(this.objects.SOUND_NODE_FORM, pathsWithValues);
		this.objects.assertEqualNonPowerDenotators(expectedNode, generatedObject);
	}

}

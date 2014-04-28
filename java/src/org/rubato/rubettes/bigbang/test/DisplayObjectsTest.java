package org.rubato.rubettes.bigbang.test;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;

public class DisplayObjectsTest extends TestCase {
	
	private TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
	}
	
	public void testCrucialMethods() throws RubatoException {
		double[][] rests = new double[][]{{3,1},{4,3}};
		DisplayObjects objects = this.createDisplayObjects(this.objects.createGeneralScore(this.objects.ABSOLUTE, rests));
		objects.setActiveColimitCoordinate(0, 0);
		TestCase.assertEquals(0, objects.getActiveObjectValueIndex(0));
		TestCase.assertEquals(1, objects.getActiveObjectValueIndex(1));
		TestCase.assertEquals(2, objects.getActiveObjectValueIndex(2));
		TestCase.assertEquals(3, objects.getActiveObjectValueIndex(3));
		TestCase.assertEquals(4, objects.getActiveObjectValueIndex(4));
		
		objects.setActiveColimitCoordinate(0, 1);
		TestCase.assertEquals(0, objects.getActiveObjectValueIndex(0));
		TestCase.assertEquals(-1, objects.getActiveObjectValueIndex(1));
		TestCase.assertEquals(-1, objects.getActiveObjectValueIndex(2));
		TestCase.assertEquals(1, objects.getActiveObjectValueIndex(3));
		TestCase.assertEquals(2, objects.getActiveObjectValueIndex(4));
	}
	
	public void testWithMultipleOccurrencesOfName() {
		DisplayObjects objects = this.createDisplayObjects(this.objects.createDyad(new double[]{60,63}));
		TestCase.assertEquals(0, objects.getActiveObjectValueIndex(0));
		TestCase.assertEquals(1, objects.getActiveObjectValueIndex(1));
	}
	
	private DisplayObjects createDisplayObjects(Denotator denotator) {
		BigBangModel model = new BigBangModel();
		model.setOrAddComposition(denotator);
		new DenotatorValueExtractor(model.getObjects(), model.getComposition());
		DisplayObjects objects = new DisplayObjects(model.getObjects());
		objects.addObjects(model.getObjects().getObjectsAt(null));
		return objects;
	}

}

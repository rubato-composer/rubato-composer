package org.rubato.rubettes.bigbang.test;

import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;
import org.rubato.rubettes.util.DenotatorPath;

public class DisplayObjectsTest extends TestCase {
	
	private ViewController viewController;
	private TestObjects objects;
	private DenotatorValueExtractor extractor;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.viewController = new ViewController();
	}
	
	public void testCrucialMethods() throws RubatoException {
		double[][] rests = new double[][]{{3,1},{4,3}};
		DisplayObjects objects = this.extractDisplayObjects(this.objects.createGeneralScore(this.objects.ABSOLUTE, rests));
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
		DisplayObjects objects = this.extractDisplayObjects(this.objects.createDyad(new double[]{60,63}));
		TestCase.assertEquals(0, objects.getActiveObjectValueIndex(0));
		TestCase.assertEquals(1, objects.getActiveObjectValueIndex(1));
	}
	
	private DisplayObjects extractDisplayObjects(Denotator denotator) {
		SelectedObjectsPaths noPaths = new SelectedObjectsPaths(new TreeSet<DenotatorPath>(), null);
		ScoreChangedNotification notification = new ScoreChangedNotification(denotator, noPaths, false, false);
		this.extractor = new DenotatorValueExtractor(new LayerStates(this.viewController));
		return this.extractor.extractValues(this.viewController, notification, false);
	}

}

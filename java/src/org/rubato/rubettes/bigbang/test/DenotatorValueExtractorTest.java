package org.rubato.rubettes.bigbang.test;

import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;
import org.rubato.rubettes.util.DenotatorPath;

public class DenotatorValueExtractorTest extends TestCase {
	
	private ViewController viewController;
	private TestObjects objects;
	private DenotatorValueExtractor extractor;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.viewController = new ViewController();
	}
	
	public void testExtractDisplayObjectsWithNotes() {
		DisplayObjects notes = this.extractDisplayObjects(this.objects.score.getComposition());
		TestCase.assertEquals(9, notes.size());
		TestCase.assertEquals(6, this.extractor.getMinValues().size());
		TestCase.assertEquals(4.0, notes.last().getNthValue(DenotatorValueExtractor.SATELLITE_LEVEL, 0));
		TestCase.assertEquals(0.0, notes.last().getNthValue(DenotatorValueExtractor.SIBLING_NUMBER, 0));
	}
	
	public void testExtractDisplayObjectsWithMultilevelSoundScore() {
		DisplayObjects notes = this.extractDisplayObjects(this.objects.multiLevelMacroScore);
		TestCase.assertEquals(3, notes.size());
		TestCase.assertEquals(2.0, notes.last().getNthValue(notes.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertEquals(60.0, notes.last().getNthValue(notes.getCoordinateSystemValueNames().get(1), 0));
		TestCase.assertEquals(121.0, notes.last().getNthValue(notes.getCoordinateSystemValueNames().get(2), 0));
		TestCase.assertEquals(1.0, notes.last().getNthValue(notes.getCoordinateSystemValueNames().get(3), 0));
		TestCase.assertEquals(1.0, notes.last().getNthValue(notes.getCoordinateSystemValueNames().get(4), 0));
		TestCase.assertEquals(2.0, notes.last().getNthValue(DenotatorValueExtractor.SATELLITE_LEVEL, 0));
		TestCase.assertEquals(0.0, notes.last().getNthValue(DenotatorValueExtractor.SIBLING_NUMBER, 0));
	}
	
	public void testExtractDisplayObjectsWithQ3() {
		DisplayObjects triples = this.extractDisplayObjects(this.objects.rationalTriples);
		TestCase.assertEquals(4, triples.size());
		TestCase.assertEquals(4.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertEquals(3.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(1), 0));
		TestCase.assertEquals(1.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(2), 0));
		//TestCase.assertEquals(1.0, triples.last().getNthValue(3));
		//TestCase.assertEquals(3.0, triples.last().getNthValue(4));
	}
	
	public void testExtractDisplayObjectsWithProductRing() {
		DisplayObjects triples = this.extractDisplayObjects(this.objects.realTriples);
		TestCase.assertEquals(3, triples.size());
		TestCase.assertEquals(4.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertEquals(3.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(1), 0));
		TestCase.assertEquals(1.0, triples.last().getNthValue(triples.getCoordinateSystemValueNames().get(2), 0));
		//TestCase.assertEquals(1.0, triples.last().getNthValue(3));
		//TestCase.assertEquals(2.0, triples.last().getNthValue(4));
	}
	
	public void testExtractDisplayObjectsWithColimit() {
		DisplayObjects integerOrReals = this.extractDisplayObjects(this.objects.integerOrReals);
		TestCase.assertEquals(4, integerOrReals.size());
		TestCase.assertEquals("Integer Z", integerOrReals.getCoordinateSystemValueNames().get(0));
		TestCase.assertEquals("Real R", integerOrReals.getCoordinateSystemValueNames().get(1));
		
		//check first element
		TestCase.assertEquals(4.0, integerOrReals.first().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertNull(integerOrReals.first().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(1), 0));
		//sat level, sib number, col index
		//TestCase.assertEquals(1.0, integerOrReals.first().getNthValue(2));
		//TestCase.assertEquals(0.0, integerOrReals.first().getNthValue(3));
		TestCase.assertEquals(0.0, integerOrReals.first().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(2), 0));
		
		//check last element
		TestCase.assertNull(integerOrReals.last().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(0), 0));
		TestCase.assertEquals(3.5, integerOrReals.last().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(1), 0));
		//sat level, sib number, col index
		//TestCase.assertEquals(1.0, integerOrReals.last().getNthValue(2));
		//TestCase.assertEquals(3.0, integerOrReals.last().getNthValue(3));
		TestCase.assertEquals(1.0, integerOrReals.last().getNthValue(integerOrReals.getCoordinateSystemValueNames().get(2), 0));
	}
	
	public void testExtractDisplayObjectsWithColimit2() {
		//TODO: test with general score!!!!
	}
	
	private DisplayObjects extractDisplayObjects(Denotator denotator) {
		SelectedObjectsPaths noPaths = new SelectedObjectsPaths(new TreeSet<DenotatorPath>(), null);
		ScoreChangedNotification notification = new ScoreChangedNotification(denotator, noPaths, false, false);
		this.extractor = new DenotatorValueExtractor(new LayerStates(this.viewController));
		return this.extractor.extractValues(this.viewController, notification, false);
	}

}

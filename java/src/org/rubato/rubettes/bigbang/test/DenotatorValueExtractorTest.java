package org.rubato.rubettes.bigbang.test;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjectList;
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
		DisplayObjectList notes = this.extractDisplayObjects(this.objects.score.getComposition());
		TestCase.assertEquals(9, notes.size());
		TestCase.assertEquals(6, this.extractor.getMinValues().size());
		TestCase.assertEquals(5.0, notes.last().getValue(6));
		TestCase.assertEquals(0.0, notes.last().getValue(7));
		
		notes = this.extractDisplayObjects(this.objects.multiLevelMacroScore);
		TestCase.assertEquals(3, notes.size());
		TestCase.assertEquals(3.0, notes.last().getValue(6));
		TestCase.assertEquals(0.0, notes.last().getValue(7));
	}
	
	public void testExtractDisplayObjectsWithQ3() {
		DisplayObjectList triples = this.extractDisplayObjects(this.objects.rationalTriples);
		TestCase.assertEquals(4, triples.size());
		TestCase.assertEquals(4.0, triples.last().getValue(0));
		TestCase.assertEquals(3.0, triples.last().getValue(1));
		TestCase.assertEquals(1.0, triples.last().getValue(2));
		//TestCase.assertEquals(1.0, triples.last().getValue(3));
		//TestCase.assertEquals(3.0, triples.last().getValue(4));
	}
	
	public void testExtractDisplayObjectsWithProductRing() {
		DisplayObjectList triples = this.extractDisplayObjects(this.objects.realTriples);
		TestCase.assertEquals(3, triples.size());
		TestCase.assertEquals(4.0, triples.last().getValue(0));
		TestCase.assertEquals(3.0, triples.last().getValue(1));
		TestCase.assertEquals(1.0, triples.last().getValue(2));
		//TestCase.assertEquals(1.0, triples.last().getValue(3));
		//TestCase.assertEquals(2.0, triples.last().getValue(4));
	}
	
	public void testExtractDisplayObjectsWithColimit() {
		DisplayObjectList integerOrReals = this.extractDisplayObjects(this.objects.integerOrReals);
		TestCase.assertEquals(4, integerOrReals.size());
		TestCase.assertEquals("Integer Z", integerOrReals.getValueNames().get(0));
		TestCase.assertEquals("Real R", integerOrReals.getValueNames().get(1));
		
		//check first element
		TestCase.assertEquals(4.0, integerOrReals.first().getValue(0));
		TestCase.assertNull(integerOrReals.first().getValue(1));
		//sat level, sib number, col index
		//TestCase.assertEquals(1.0, integerOrReals.first().getValue(2));
		//TestCase.assertEquals(0.0, integerOrReals.first().getValue(3));
		TestCase.assertEquals(0.0, integerOrReals.first().getValue(2));
		
		//check last element
		TestCase.assertNull(integerOrReals.last().getValue(0));
		TestCase.assertEquals(3.5, integerOrReals.last().getValue(1));
		//sat level, sib number, col index
		//TestCase.assertEquals(1.0, integerOrReals.last().getValue(2));
		//TestCase.assertEquals(3.0, integerOrReals.last().getValue(3));
		TestCase.assertEquals(1.0, integerOrReals.last().getValue(2));
	}
	
	private DisplayObjectList extractDisplayObjects(Denotator denotator) {
		Set<DenotatorPath> noPaths = new TreeSet<DenotatorPath>();
		ScoreChangedNotification notification = new ScoreChangedNotification(denotator, noPaths, new DenotatorPath(denotator.getForm()), false);
		this.extractor = new DenotatorValueExtractor(this.viewController, notification, false, new LayerStates(this.viewController));
		return this.extractor.getDisplayObjects();
	}

}

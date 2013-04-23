package org.rubato.rubettes.bigbang.test;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

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
		this.extractor = new DenotatorValueExtractor();
		this.viewController = new ViewController();
	}
	
	public void testExtractDisplayObjectsWithNotes() {
		Set<DenotatorPath> noPaths = new TreeSet<DenotatorPath>();
		ScoreChangedNotification notification = new ScoreChangedNotification(this.objects.score.getComposition(), noPaths, new DenotatorPath(this.objects.SOUND_SCORE_FORM));
		DisplayObjectList notes = this.extractor.extractDisplayObjects(this.viewController, notification, false, new LayerStates(this.viewController));
		TestCase.assertEquals(9, notes.size());
		TestCase.assertEquals(6, this.extractor.getMinValues().size());
		TestCase.assertEquals(5.0, notes.last().getValue(6));
		TestCase.assertEquals(0.0, notes.last().getValue(7));
		
		notification = new ScoreChangedNotification(this.objects.multiLevelMacroScore, noPaths, new DenotatorPath(this.objects.SOUND_SCORE_FORM));
		notes = this.extractor.extractDisplayObjects(this.viewController, notification, false, new LayerStates(this.viewController));
		TestCase.assertEquals(3, notes.size());
		TestCase.assertEquals(3.0, notes.last().getValue(6));
		TestCase.assertEquals(0.0, notes.last().getValue(7));
	}
	
	public void testExtractDisplayObjectsWithQ3() {
		ScoreChangedNotification notification = new ScoreChangedNotification(this.objects.rationalTriples, new TreeSet<DenotatorPath>(), new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM));
		DisplayObjectList triples = this.extractor.extractDisplayObjects(this.viewController, notification, false, new LayerStates(this.viewController));
		TestCase.assertEquals(4, triples.size());
		TestCase.assertEquals(4.0, triples.last().getValue(0));
		TestCase.assertEquals(3.0, triples.last().getValue(1));
		TestCase.assertEquals(1.0, triples.last().getValue(2));
		//TestCase.assertEquals(1.0, triples.last().getValue(3));
		//TestCase.assertEquals(3.0, triples.last().getValue(4));
	}
	
	public void testExtractDisplayObjectsWithProductRing() {
		ScoreChangedNotification notification = new ScoreChangedNotification(this.objects.realTriples, new TreeSet<DenotatorPath>(), new DenotatorPath(this.objects.REAL_TRIPLES_FORM));
		DisplayObjectList triples = this.extractor.extractDisplayObjects(this.viewController, notification, false, new LayerStates(this.viewController));
		TestCase.assertEquals(3, triples.size());
		TestCase.assertEquals(4.0, triples.last().getValue(0));
		TestCase.assertEquals(3.0, triples.last().getValue(1));
		TestCase.assertEquals(1.0, triples.last().getValue(2));
		//TestCase.assertEquals(1.0, triples.last().getValue(3));
		//TestCase.assertEquals(2.0, triples.last().getValue(4));
	}
	
	public void testExtractDisplayObjectsWithColimit() {
		ScoreChangedNotification notification = new ScoreChangedNotification(this.objects.integerOrReals, new TreeSet<DenotatorPath>(), new DenotatorPath(this.objects.INTEGER_OR_REALS_FORM));
		DisplayObjectList integerOrReals = this.extractor.extractDisplayObjects(this.viewController, notification, false, new LayerStates(this.viewController));
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

}

package org.rubato.rubettes.bigbang.test;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
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
		TestCase.assertEquals(5.0, notes.last().getValue(DenotatorValueExtractor.SATELLITE_LEVEL));
		TestCase.assertEquals(0.0, notes.last().getValue(DenotatorValueExtractor.SIBLING_NUMBER));
	}
	
	public void testExtractDisplayObjectsWithMultilevelSoundScore() {
		DisplayObjects notes = this.extractDisplayObjects(this.objects.multiLevelMacroScore);
		TestCase.assertEquals(3, notes.size());
		TestCase.assertEquals(2.0, notes.last().getValue(notes.getValueNames().get(0)));
		TestCase.assertEquals(60.0, notes.last().getValue(notes.getValueNames().get(1)));
		TestCase.assertEquals(121.0, notes.last().getValue(notes.getValueNames().get(2)));
		TestCase.assertEquals(1.0, notes.last().getValue(notes.getValueNames().get(3)));
		TestCase.assertEquals(1.0, notes.last().getValue(notes.getValueNames().get(4)));
		TestCase.assertEquals(3.0, notes.last().getValue(DenotatorValueExtractor.SATELLITE_LEVEL));
		TestCase.assertEquals(0.0, notes.last().getValue(DenotatorValueExtractor.SIBLING_NUMBER));
	}
	
	public void testExtractDisplayObjectsWithQ3() {
		DisplayObjects triples = this.extractDisplayObjects(this.objects.rationalTriples);
		TestCase.assertEquals(4, triples.size());
		TestCase.assertEquals(4.0, triples.last().getValue(triples.getValueNames().get(0)));
		TestCase.assertEquals(3.0, triples.last().getValue(triples.getValueNames().get(1)));
		TestCase.assertEquals(1.0, triples.last().getValue(triples.getValueNames().get(2)));
		//TestCase.assertEquals(1.0, triples.last().getValue(3));
		//TestCase.assertEquals(3.0, triples.last().getValue(4));
	}
	
	public void testExtractDisplayObjectsWithProductRing() {
		DisplayObjects triples = this.extractDisplayObjects(this.objects.realTriples);
		TestCase.assertEquals(3, triples.size());
		TestCase.assertEquals(4.0, triples.last().getValue(triples.getValueNames().get(0)));
		TestCase.assertEquals(3.0, triples.last().getValue(triples.getValueNames().get(1)));
		TestCase.assertEquals(1.0, triples.last().getValue(triples.getValueNames().get(2)));
		//TestCase.assertEquals(1.0, triples.last().getValue(3));
		//TestCase.assertEquals(2.0, triples.last().getValue(4));
	}
	
	public void testExtractDisplayObjectsWithColimit() {
		DisplayObjects integerOrReals = this.extractDisplayObjects(this.objects.integerOrReals);
		TestCase.assertEquals(4, integerOrReals.size());
		TestCase.assertEquals("Integer Z", integerOrReals.getValueNames().get(0));
		TestCase.assertEquals("Real R", integerOrReals.getValueNames().get(1));
		
		//check first element
		TestCase.assertEquals(4.0, integerOrReals.first().getValue(integerOrReals.getValueNames().get(0)));
		TestCase.assertNull(integerOrReals.first().getValue(integerOrReals.getValueNames().get(1)));
		//sat level, sib number, col index
		//TestCase.assertEquals(1.0, integerOrReals.first().getValue(2));
		//TestCase.assertEquals(0.0, integerOrReals.first().getValue(3));
		TestCase.assertEquals(0.0, integerOrReals.first().getValue(integerOrReals.getValueNames().get(2)));
		
		//check last element
		TestCase.assertNull(integerOrReals.last().getValue(integerOrReals.getValueNames().get(0)));
		TestCase.assertEquals(3.5, integerOrReals.last().getValue(integerOrReals.getValueNames().get(1)));
		//sat level, sib number, col index
		//TestCase.assertEquals(1.0, integerOrReals.last().getValue(2));
		//TestCase.assertEquals(3.0, integerOrReals.last().getValue(3));
		TestCase.assertEquals(1.0, integerOrReals.last().getValue(integerOrReals.getValueNames().get(2)));
	}
	
	private DisplayObjects extractDisplayObjects(Denotator denotator) {
		Set<DenotatorPath> noPaths = new TreeSet<DenotatorPath>();
		ScoreChangedNotification notification = new ScoreChangedNotification(denotator, noPaths, new DenotatorPath(denotator.getForm()), false, false);
		this.extractor = new DenotatorValueExtractor(new LayerStates(this.viewController));
		return this.extractor.extractValues(this.viewController, notification, false);
	}

}

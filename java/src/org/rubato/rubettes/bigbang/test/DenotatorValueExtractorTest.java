package org.rubato.rubettes.bigbang.test;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.math.module.DomainException;
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
		ScoreChangedNotification notification = new ScoreChangedNotification(this.objects.score.getComposition(), noPaths, new DenotatorPath());
		DisplayObjectList notes = this.extractor.extractDisplayObjects(this.viewController, notification, false, new LayerStates(this.viewController));
		TestCase.assertTrue(notes.size() == 9);
		TestCase.assertEquals(6, this.extractor.getMinValues().size());
		
		notification = new ScoreChangedNotification(this.objects.multiLevelMacroScore, noPaths, new DenotatorPath());
		notes = this.extractor.extractDisplayObjects(this.viewController, notification, false, new LayerStates(this.viewController));
		TestCase.assertTrue(notes.size() == 3);
	}
	
	public void testExtractDisplayObjectsWithRealTriples() {
		ScoreChangedNotification notification = new ScoreChangedNotification(this.objects.realTriples, new TreeSet<DenotatorPath>(), new DenotatorPath());
		DisplayObjectList triples = this.extractor.extractDisplayObjects(this.viewController, notification, false, new LayerStates(this.viewController));
		TestCase.assertTrue(triples.size() == 3);
	}

}

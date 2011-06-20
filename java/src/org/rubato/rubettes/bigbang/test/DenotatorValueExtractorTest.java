package org.rubato.rubettes.bigbang.test;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
import org.rubato.rubettes.bigbang.view.subview.DisplayNoteList;
import org.rubato.rubettes.util.NotePath;

public class DenotatorValueExtractorTest extends TestCase {
	
	private TestObjects objects;
	private DenotatorValueExtractor extractor;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.extractor = new DenotatorValueExtractor();
	}
	
	public void testExtractDisplayNotes() {
		ViewController controller = new ViewController(); 
		Set<NotePath> noPaths = new TreeSet<NotePath>();
		ScoreChangedNotification notification = new ScoreChangedNotification(this.objects.score.getComposition(), noPaths, new NotePath());
		DisplayNoteList notes = this.extractor.extractDisplayNotes(controller, notification, false, new LayerStates(controller));
		TestCase.assertTrue(notes.size() == 9);
		
		notification = new ScoreChangedNotification(this.objects.multiLevelMacroScore, noPaths, new NotePath());
		notes = this.extractor.extractDisplayNotes(controller, notification, false, new LayerStates(controller));
		TestCase.assertTrue(notes.size() == 3);
		
	}

}

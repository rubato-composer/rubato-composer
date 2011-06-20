package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.model.BigBangScore;
import org.rubato.rubettes.util.NotePath;

public class BigBangScoreTest extends TestCase {
	
	private BigBangScore score;
	private TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.score = new BigBangScore(this.objects.generator);;
	}
	
	public void testAddNode() {
		NotePath nodePath = this.score.addNote(this.objects.NOTE0_VALUES);
		TestCase.assertEquals(nodePath, new NotePath(new int[]{0,0}));
		nodePath = this.score.addNote(this.objects.NOTE2_ABSOLUTE_VALUES);
		TestCase.assertEquals(nodePath, new NotePath(new int[]{1,0}));
		nodePath = this.score.addNote(this.objects.NOTE1_ABSOLUTE_VALUES);
		TestCase.assertEquals(nodePath, new NotePath(new int[]{1,0}));
		PowerDenotator composition = this.score.getComposition();
		TestCase.assertTrue(composition.getFactorCount() == 3);
	}
	
	public void testAddNodesAndFindPaths() {
		//addNodes
		this.score.setComposition(this.objects.multiLevelMacroScore);
		List<NotePath> anchorPaths = new ArrayList<NotePath>();
		anchorPaths.add(new NotePath(new int[]{}));
		anchorPaths.add(new NotePath(new int[]{0,0}));
		anchorPaths.add(new NotePath(new int[]{0,1,0,0}));
		List<LimitDenotator> notes = new ArrayList<LimitDenotator>();
		notes.add(this.objects.note1Absolute);
		notes.add(this.objects.note2Absolute);
		notes.add(this.objects.note0);
		List<NotePath> satellitePaths = this.score.addNotes(notes, anchorPaths);
		TestCase.assertEquals(satellitePaths.get(0), new NotePath(new int[]{1,0}));
		TestCase.assertEquals(satellitePaths.get(1), new NotePath(new int[]{0,1,1,0}));
		TestCase.assertEquals(satellitePaths.get(2), new NotePath(new int[]{0,1,0,1,0,0}));
		//findPaths
		
	}
	
	public void testAddNotes() {
		this.score.setComposition(this.objects.multiLevelMacroScore);
		List<List<LimitDenotator>> noteLists = new ArrayList<List<LimitDenotator>>();
		List<LimitDenotator> notes = new ArrayList<LimitDenotator>();
		notes.add(this.objects.note2Absolute.copy());
		noteLists.add(notes);
		notes = new ArrayList<LimitDenotator>();
		notes.add(this.objects.note1Absolute.copy());
		noteLists.add(notes);
		List<NotePath> newPaths = this.score.addNotes(noteLists);
		List<NotePath> expectedPaths = this.makeNodePaths(new int[]{2,0}, new int[]{1,0});
		TestCase.assertEquals(expectedPaths, newPaths);
	}
	
	public void testGetAbsoluteNote() throws RubatoException {
		this.score.setComposition(this.objects.multiLevelMacroScore);
		NotePath notePath = new NotePath(new int[]{0,1,0,1,0,0});
		LimitDenotator note = (LimitDenotator)this.score.getNote(notePath).getFactor(0);
		this.assertEqualNotes(note, this.objects.note2Relative);
		LimitDenotator absoluteNote = (LimitDenotator)this.score.getAbsoluteNote(notePath).getFactor(0);
		this.assertEqualNotes(absoluteNote, this.objects.note2Absolute);
	}
	
	public void testRemoveNote() throws RubatoException {
		this.score.setComposition(this.objects.multiLevelMacroScore);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 1);
		LimitDenotator removedNote = (LimitDenotator)this.score.removeNote(new NotePath(new int[]{0,1,0,1,0,0})).getFactor(0);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 0);
		this.assertEqualNotes(removedNote, this.objects.note2Absolute);
	}
	
	public void testRemoveNotes() throws RubatoException {
		this.score.setComposition(this.objects.flatMacroScore);
		TestCase.assertTrue(this.score.getComposition().getFactorCount() == 3);
		List<NotePath> paths = new ArrayList<NotePath>();
		paths.add(new NotePath(new int[]{1,0}));
		LimitDenotator removedNote = (LimitDenotator)this.score.removeNotes(paths).get(0).getFactor(0);
		TestCase.assertTrue(this.score.getComposition().getFactorCount() == 2);
		this.assertEqualNotes(removedNote, this.objects.note1Absolute);
		
		this.score.setComposition(this.objects.multiLevelMacroScore);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 1);
		paths = new ArrayList<NotePath>();
		paths.add(new NotePath(new int[]{0,1,0,1,0,0}));
		removedNote = (LimitDenotator)this.score.removeNotes(paths).get(0).getFactor(0);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 0);
		this.assertEqualNotes(removedNote, this.objects.note2Absolute);
	}
	
	private List<NotePath> makeNodePaths(int[]... intNodePaths) {
		List<NotePath> nodePaths = new ArrayList<NotePath>();
		for (int[] currentPath: intNodePaths) {
			nodePaths.add(new NotePath(currentPath));
		}
		return nodePaths;
	}
	
	private void assertEqualNotes(Denotator note1, Denotator note2) throws RubatoException {
		for (int i = 0; i < 6; i++) {
			TestCase.assertEquals(note1.get(new int[]{i}).getCoordinate(), note2.get(new int[]{i}).getCoordinate());
		}
	}
	
	@SuppressWarnings("unused")
    private void printNote(Denotator node) throws RubatoException {
		for (int i = 0; i < 6; i++) {
			System.out.println(node.getElement(new int[]{i,0}));
		}
	}

}

package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.BigBangTransformation;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.util.NotePath;

public class BigBangScoreManagerTest extends TestCase {
	
	private final int[][] COORDINATE_PATHS = new int[][]{{0},{1},{0},{1}};
	
	private BigBangScoreManager scoreManager;
	private TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.scoreManager = this.objects.scoreManager;
	}
	
	public void testAddNote() {
		NotePath nodePath = this.scoreManager.addNote(this.objects.NOTE0_VALUES);
		TestCase.assertEquals(nodePath, new NotePath(new int[]{0,0}));
		nodePath = this.scoreManager.addNote(this.objects.NOTE2_ABSOLUTE_VALUES);
		TestCase.assertEquals(nodePath, new NotePath(new int[]{1,0}));
		nodePath = this.scoreManager.addNote(this.objects.NOTE1_ABSOLUTE_VALUES);
		TestCase.assertEquals(nodePath, new NotePath(new int[]{1,0}));
		PowerDenotator composition = this.scoreManager.getComposition();
		TestCase.assertTrue(composition.getFactorCount() == 3);
	}
	
	public void testAddNotes() {
		//addNodes
		this.scoreManager.setComposition(this.objects.multiLevelMacroScore);
		List<NotePath> anchorPaths = new ArrayList<NotePath>();
		anchorPaths.add(new NotePath(new int[]{}));
		anchorPaths.add(new NotePath(new int[]{0,0}));
		anchorPaths.add(new NotePath(new int[]{0,1,0,0}));
		List<LimitDenotator> notes = new ArrayList<LimitDenotator>();
		notes.add(this.objects.note1Absolute);
		notes.add(this.objects.note2Absolute);
		notes.add(this.objects.note0);
		List<NotePath> satellitePaths = this.scoreManager.addNotes(notes, anchorPaths);
		TestCase.assertEquals(new NotePath(new int[]{1,0}), satellitePaths.get(0));
		TestCase.assertEquals(new NotePath(new int[]{0,1,1,0}), satellitePaths.get(1));
		TestCase.assertEquals(new NotePath(new int[]{0,1,0,1,0,0}), satellitePaths.get(2));
	}
	
	public void testMapNodesFlat() {
		this.scoreManager.setComposition(this.objects.flatMacroScore);
		BigBangTransformation translation = this.makeTranslation(-1,-2);
		List<NotePath> notePaths = this.makeNotePaths(new int[]{0,0}, new int[]{2,0});
		List<NotePath> newPaths = new ArrayList<NotePath>(this.scoreManager.mapNodes(notePaths, translation, false));
		TestCase.assertEquals(newPaths.get(0), new NotePath(new int[]{0,0}));
		TestCase.assertEquals(newPaths.get(1), new NotePath(new int[]{1,0}));
	}
	
	public void testMapNodesFlatSequential() {
		this.scoreManager.setComposition(this.objects.flatMacroScore);
		BigBangTransformation translation = this.makeTranslation(3,5);
		List<NotePath> notePaths = this.makeNotePaths(new int[]{0,0}, new int[]{2,0});
		/*List<List<LimitDenotator>> notes = this.scoreManager.getNotes(notePaths);
		TestCase.assertTrue(notes.size() == 2);
		List<NotePath> retrievedPaths = this.scoreManager.getNotePaths(notes);
		System.out.println(retrievedPaths);*/
		List<NotePath> newPaths = new ArrayList<NotePath>(this.scoreManager.mapNodes(notePaths, translation, false));
		TestCase.assertEquals(newPaths.get(0), new NotePath(new int[]{1,0}));
		TestCase.assertEquals(newPaths.get(1), new NotePath(new int[]{2,0}));
		/*retrievedPaths = this.scoreManager.getNotePaths(notes);
		System.out.println(retrievedPaths);
		TestCase.assertTrue(retrievedPaths.equals(newPaths));*/
	}
	
	public void testMapNodesMultiLevel() throws RubatoException {
		this.objects.multiLevelMacroScore.appendFactor(this.objects.generator.createNodeDenotator(this.objects.note2Absolute));
		this.scoreManager.setComposition(this.objects.multiLevelMacroScore);
		BigBangTransformation translation = this.makeTranslation(-2, -1);
		List<NotePath> nodePaths = this.makeNotePaths(new int[]{1,0}, new int[]{0,1,0,0});
		List<NotePath> newPaths = new ArrayList<NotePath>(this.scoreManager.mapNodes(nodePaths, translation, false));
		TestCase.assertEquals(newPaths.get(0), new NotePath(new int[]{0,0}));
		TestCase.assertEquals(newPaths.get(1), new NotePath(new int[]{1,1,0,0}));
		LimitDenotator expectedNote = this.objects.generator.createNoteDenotator(new double[]{-1,2,-4,0,0,0});
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{1,1,0,0}), expectedNote);
		
		nodePaths = this.makeNotePaths(new int[]{0,0}, new int[]{1,1,0,0});
		newPaths = new ArrayList<NotePath>(this.scoreManager.mapNodes(nodePaths, translation, false));
		TestCase.assertEquals(newPaths.get(0), new NotePath(new int[]{0,0}));
		TestCase.assertEquals(newPaths.get(1), new NotePath(new int[]{1,1,0,0}));
		expectedNote = this.objects.generator.createNoteDenotator(new double[]{-3,1,-4,0,0,0});
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{1,1,0,0}), expectedNote);
	}
	
	public void testMapModulators() throws RubatoException {
		this.scoreManager.setComposition(this.objects.flatMacroScore);
		List<NotePath> paths = new ArrayList<NotePath>();
		paths.add(new NotePath(new int[]{1,0}));
		//build modulator structure
		this.scoreManager.moveNotesToParent(paths, new NotePath(new int[]{0,0}), true);
		this.scoreManager.moveNotesToParent(paths, new NotePath(new int[]{0,0,6,0}), true);
		
		BigBangTransformation translation = this.makeTranslation(-2, -1);
		List<NotePath> nodePaths = this.makeNotePaths(new int[]{0,0,6,0,6,0});
		List<NotePath> newPaths = new ArrayList<NotePath>(this.scoreManager.mapNodes(nodePaths, translation, false));
		TestCase.assertEquals(newPaths.get(0), new NotePath(new int[]{0,0,6,0,6,0}));
		LimitDenotator expectedNote = this.objects.generator.createNoteDenotator(new double[]{-1,-4,5,0,1,0});
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0,6,0,6,0}), expectedNote);
	}
	
	public void testCopyAndMapDenotator() {
		this.scoreManager.setComposition(this.objects.flatMacroScore);
		TreeSet<NotePath> nodePaths = new TreeSet<NotePath>();
		nodePaths.add(new NotePath(new int[]{0}));
		nodePaths.add(new NotePath(new int[]{1}));
		int[][] coordinatePaths = new int[][]{{0},{1},{0},{1}};
		/*Set<DenotatorPath> copyPaths = this.score.copyAndMapNotes(nodePaths, translation, coordinatePaths);
		TestCase.assertTrue(copyPaths.size() == 2);
		//TestCase.assertTrue(copyPaths.first().equals(new DenotatorPath(new int[]{3})));
		//TestCase.assertTrue(copyPaths.last().equals(new DenotatorPath(new int[]{4})));
		TestCase.assertTrue(this.score.getComposition().getFactorCount() == 5);
		//TestCase.assertEquals(this.score.getComposition().get(new int[]{0}), this.node0);
		//this.printNode(this.score.getComposition().get(new int[]{4}));
		//TestCase.assertEquals(this.score.getComposition().get(new int[]{1}), this.node1Absolute);
		//this.checkEquals(this.score.getComposition().get(new int[]{2}), this.node2Absolute);
		//this.checkEquals(this.score.getComposition().get(new int[]{3}), this.node0);
		//this.checkEquals(this.score.getComposition().get(new int[]{4}), this.node2Absolute);
		*/
		/*?????LimitDenotator node = this.node2Absolute = this.generator.createNodeDenotator(new double[]{2,60,121,1,1,0});
		LimitDenotator node2 = this.generator.createNodeDenotator(new double[]{2,60,121,1,1,0});
		TestCase.assertTrue(!node.equals(node2));
		PowerDenotator ms = this.generator.createEmptyScore();
		ms.appendFactor(node);
		ms.appendFactor(node2);
		TestCase.assertTrue(ms.getFactorCount() == 2);
		ms = ms.copy();
		TestCase.assertTrue(ms.getFactorCount() == 2);*/
	}
	
	public void testGetAbsoluteNode() {
		this.scoreManager.setComposition(this.objects.multiLevelMacroScore);
		NotePath nodePath = new NotePath(new int[]{0,1,0,1,0});
		//LimitDenotator absoluteNode = this.scoreManager.getComposition().getAbsoluteNode(nodePath);
		//TestCase.assertEquals(absoluteNode, this.node2Absolute);
	}
	
	public void testBuildSatellites() throws RubatoException {
		this.scoreManager.setComposition(this.objects.flatMacroScore);
		List<NotePath> paths = new ArrayList<NotePath>();
		paths.add(new NotePath(new int[]{1,0}));
		//build first satellite and check if it's there
		this.scoreManager.moveNotesToParent(paths, new NotePath(new int[]{0,0}), false);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,1,0,0}), this.objects.note1Relative);
		//build second satellite and check if it's there
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 0);
		List<NotePath> satellitePaths = this.scoreManager.moveNotesToParent(paths, new NotePath(new int[]{0,1,0,0}), false);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 1);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,1,0,1,0,0}), this.objects.note2Relative);
		//undo and check if original is there again
		this.scoreManager.undoMoveToParent(new ArrayList<NotePath>(satellitePaths), paths);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 0);
		TestCase.assertTrue(this.scoreManager.getComposition().getFactorCount() == 2);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0}), this.objects.note0);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{1,0}), this.objects.note2Absolute);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,1,0,0}), this.objects.note1Relative);
	}
	
	public void testBuildModulators() throws RubatoException {
		this.scoreManager.setComposition(this.objects.flatMacroScore);
		List<NotePath> paths = new ArrayList<NotePath>();
		paths.add(new NotePath(new int[]{1,0}));
		//build first modulator and check if it's there
		this.scoreManager.moveNotesToParent(paths, new NotePath(new int[]{0,0}), true);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0,6,0}), this.objects.note1Relative);
		//build second modulator and check if it's there
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,0,6,0,6})).getFactorCount() == 0);
		List<NotePath> modulatorPaths = this.scoreManager.moveNotesToParent(paths, new NotePath(new int[]{0,0,6,0}), true);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,0,6,0,6})).getFactorCount() == 1);
		Denotator addedModulator = this.scoreManager.getComposition().get(new int[]{0,0,6,0,6,0});
		TestCase.assertEquals(this.objects.generator.SOUND_NOTE_FORM, addedModulator.getForm());
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0,6,0,6,0}), this.objects.note2Relative);
		//undo and check if original is there again
		this.scoreManager.undoMoveToParent(new ArrayList<NotePath>(modulatorPaths), paths);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,0,6,0,6})).getFactorCount() == 0);
		TestCase.assertTrue(this.scoreManager.getComposition().getFactorCount() == 2);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0}), this.objects.note0);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{1,0}), this.objects.note2Absolute);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0,6,0}), this.objects.note1Relative);
		//try to add a note with a modulator as a satellite
		paths = new ArrayList<NotePath>();
		paths.add(new NotePath(new int[]{0,0}));
		this.scoreManager.moveNotesToParent(paths, new NotePath(new int[]{1,0}), false);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0}), this.objects.note2Absolute);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,1,0,0}), this.objects.generator.createNoteDenotator(new double[]{-2,0,-1,0,-1,0}));
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,1,0,0,6,0}), this.objects.note1Relative);
	}
	
	public void testFlatten() throws RubatoException {
		this.objects = new TestObjects();
		this.scoreManager = new BigBangScoreManager(new BigBangController());
		this.scoreManager.setComposition(this.objects.multiLevelMacroScore);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1})).getFactorCount() == 1);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 1);
		TreeSet<NotePath> paths = new TreeSet<NotePath>();
		paths.add(new NotePath(new int[]{0,1,0,1,0,0}));
		this.scoreManager.flattenNotes(paths);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1})).getFactorCount() == 2);
	}
	
	public void testFlatten2() throws RubatoException {
		this.scoreManager.setComposition(this.objects.multiLevelMacroScore);
		TreeSet<NotePath> paths = new TreeSet<NotePath>();
		paths.add(new NotePath(new int[]{0,1,0,0}));
		//flatten the first path
		Map<NotePath,NotePath> newAndOldPaths = this.scoreManager.flattenNotes(paths);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0}), this.objects.note0);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{1,0}), this.objects.note1Absolute);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{1,1,0,0}), this.objects.note2Relative);
		this.scoreManager.unflattenNotes(newAndOldPaths);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0}), this.objects.note0);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,1,0,0}), this.objects.note1Relative);
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,1,0,1,0,0}), this.objects.note2Relative);
	}
	
	public void testShapeNotes() throws RubatoException {
		this.scoreManager.setComposition(this.objects.flatMacroScore);
		Set<NotePath> paths = new TreeSet<NotePath>();
		paths.add(new NotePath(new int[]{0,0}));
		paths.add(new NotePath(new int[]{1,0}));
		paths.add(new NotePath(new int[]{2,0}));
		int[][] elementPaths = new int[][]{{0},{1}};
		TransformationProperties properties = new TransformationProperties(paths, elementPaths, false, false, false);
		TreeMap<Double,Double> shapingLocations = new TreeMap<Double,Double>();
		shapingLocations.put(-0.3, 69.0);
		shapingLocations.put(0.5, 71.0);
		shapingLocations.put(4.0, 99.0);
		this.scoreManager.shapeNotes(properties, shapingLocations);
		
		LimitDenotator expectedNote = this.objects.generator.createNoteDenotator(new double[]{0,69,120,1,0,0});
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{0,0}), expectedNote);
		expectedNote = this.objects.generator.createNoteDenotator(new double[]{1,71,116,1,0,0});
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{1,0}), expectedNote);
		expectedNote = this.objects.generator.createNoteDenotator(new double[]{2,60,121,1,1,0});
		this.assertEqualNotes(this.scoreManager.getComposition().get(new int[]{2,0}), expectedNote);
	}
	
	private BigBangTransformation makeTranslation(int x, int y) {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		ModuleMorphism translation = RFreeAffineMorphism.make(identity, new double[]{x, y});
		return new BigBangTransformation(translation, this.COORDINATE_PATHS, false, null);
	}
	
	private List<NotePath> makeNotePaths(int[]... intNotePaths) {
		List<NotePath> notePaths = new ArrayList<NotePath>();
		for (int[] currentPath: intNotePaths) {
			notePaths.add(new NotePath(currentPath));
		}
		return notePaths;
	}
	
	private void assertEqualNotes(Denotator note1, Denotator note2) throws RubatoException {
		for (int i = 0; i < 6; i++) {
			TestCase.assertEquals(note1.get(new int[]{i}).getCoordinate().getMap(), note2.get(new int[]{i}).getCoordinate().getMap());
		}
	}
	
	@SuppressWarnings("unused")
    private void printNote(Denotator note) throws RubatoException {
		for (int i = 0; i < 7; i++) {
			System.out.println(note.getElement(new int[]{i,0}));
		}
	}

}

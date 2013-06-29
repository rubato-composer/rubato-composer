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
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangScoreManagerTest extends TestCase {
	
	private BigBangScoreManager scoreManager;
	private TestObjects objects;
	private TransformationPaths nodePaths, notePaths, rationalTriplesPaths, realTriplesPaths;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.scoreManager = this.objects.scoreManager;
		this.nodePaths = this.objects.createStandardTransformationPaths(
				this.objects.SOUND_NODE_FORM, new int[][]{{0,0},{0,1}});
		this.notePaths = this.objects.createStandardTransformationPaths(
				this.objects.SOUND_NOTE_FORM, new int[][]{{0},{1}});
		this.rationalTriplesPaths = this.objects.createStandardTransformationPaths(
				this.objects.RATIONAL_TRIPLE_FORM, new int[][]{{1},{2}});
		this.realTriplesPaths = this.objects.createStandardTransformationPaths(
				this.objects.REAL_TRIPLE_FORM, new int[][]{{0},{2}});
	}
	
	/*TODO: adapt!!! public void testAddNote() {
		DenotatorPath nodePath = this.scoreManager.addObject(this.objects.NOTE0_VALUES);
		TestCase.assertEquals(nodePath, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0}));
		nodePath = this.scoreManager.addObject(this.objects.NOTE2_ABSOLUTE_VALUES);
		TestCase.assertEquals(nodePath, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1,0}));
		nodePath = this.scoreManager.addObject(this.objects.NOTE1_ABSOLUTE_VALUES);
		TestCase.assertEquals(nodePath, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1,0}));
		PowerDenotator composition = (PowerDenotator)this.scoreManager.getComposition();
		TestCase.assertTrue(composition.getFactorCount() == 3);
	}*/
	
	public void testAddNotes() {
		//addNodes
		this.scoreManager.setInitialComposition(this.objects.multiLevelMacroScore);
		List<DenotatorPath> anchorPaths = new ArrayList<DenotatorPath>();
		anchorPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}));
		anchorPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0}));
		anchorPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,0}));
		List<Denotator> notes = new ArrayList<Denotator>();
		notes.add(this.objects.node1Absolute);
		notes.add(this.objects.node2Absolute);
		notes.add(this.objects.node0);
		List<DenotatorPath> satellitePaths = this.scoreManager.addObjects(notes, anchorPaths, false);
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}), satellitePaths.get(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,1}), satellitePaths.get(1));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,1,0}), satellitePaths.get(2));
	}
	
	public void testMapNodesFlat() {
		this.scoreManager.setInitialComposition(this.objects.flatMacroScore);
		BigBangTransformation translation = this.makeTranslation(-1,-2, this.nodePaths);
		List<DenotatorPath> notePaths = this.makeNotePaths(new int[]{0}, new int[]{2});
		List<DenotatorPath> newPaths = this.scoreManager.mapObjects(notePaths, translation, false, false);
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}), newPaths.get(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}), newPaths.get(1));
	}
	
	public void testMapRationalTriples() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.rationalTriples);
		BigBangTransformation translation = this.makeTranslation(-1,-2, this.rationalTriplesPaths);
		List<DenotatorPath> triplesPath = new ArrayList<DenotatorPath>();
		triplesPath.add(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{0}));
		triplesPath.add(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{1}));
		triplesPath.add(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{3}));
		List<DenotatorPath> newPaths = this.scoreManager.mapObjects(triplesPath, translation, false, false);
		TestCase.assertEquals(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{0}), newPaths.get(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{1}), newPaths.get(1));
		TestCase.assertEquals(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{3}), newPaths.get(2));
		Denotator expectedTriple = this.objects.createRationalTriple(new double[]{1,1,1});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(newPaths.get(0).toIntArray()));
		expectedTriple = this.objects.createRationalTriple(new double[]{2,0,3});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(newPaths.get(1).toIntArray()));
		expectedTriple = this.objects.createRationalTriple(new double[]{4,2,-1});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(newPaths.get(2).toIntArray()));
	}
	
	public void testMapRealTriples() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.realTriples);
		BigBangTransformation translation = this.makeTranslation(-2,-3, this.realTriplesPaths);
		List<DenotatorPath> triplesPath = new ArrayList<DenotatorPath>();
		triplesPath.add(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{1}));
		triplesPath.add(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{2}));
		List<DenotatorPath> newPaths = this.scoreManager.mapObjects(triplesPath, translation, false, false);
		TestCase.assertEquals(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{0}), newPaths.get(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{2}), newPaths.get(1));
		Denotator expectedTriple = this.objects.createRealTriple(new double[]{0,1,2});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(newPaths.get(0).toIntArray()));
		expectedTriple = this.objects.createRealTriple(new double[]{2,3,-2});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(newPaths.get(1).toIntArray()));
	}
	
	//ProductElement element1 = ProductElement.make(new RElement(1), new RElement(2), new RElement(3));
	//ProductElement element2 = ProductElement.make(new RElement(4), new RElement(3), new RElement(1));
	//ProductElement element3 = ProductElement.make(new RElement(2), new RElement(1), new RElement(5));
	public void testMapRealTriple() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.realTriples.getFactor(1));
		BigBangTransformation translation = this.makeTranslation(-2,-3, this.realTriplesPaths);
		List<DenotatorPath> emptyPath = new ArrayList<DenotatorPath>();
		emptyPath.add(new DenotatorPath(this.objects.REAL_TRIPLE_FORM, new int[]{}));
		List<DenotatorPath> newPaths = this.scoreManager.mapObjects(emptyPath, translation, false, false);
		TestCase.assertEquals(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{}), newPaths.get(0));
		Denotator expectedTriple = this.objects.createRealTriple(new double[]{0,1,2});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition());
	}
	
	public void testMapNodesFlatSequential() {
		this.scoreManager.setInitialComposition(this.objects.flatMacroScore);
		BigBangTransformation translation = this.makeTranslation(3,5, this.nodePaths);
		List<DenotatorPath> notePaths = this.makeNotePaths(new int[]{0}, new int[]{2});
		/*List<List<LimitDenotator>> notes = this.scoreManager.getNotes(notePaths);
		TestCase.assertTrue(notes.size() == 2);
		List<NotePath> retrievedPaths = this.scoreManager.getNotePaths(notes);
		System.out.println(retrievedPaths);*/
		List<DenotatorPath> newPaths = new ArrayList<DenotatorPath>(this.scoreManager.mapObjects(notePaths, translation, false, false));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}), newPaths.get(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}), newPaths.get(1));
		/*retrievedPaths = this.scoreManager.getNotePaths(notes);
		System.out.println(retrievedPaths);
		TestCase.assertTrue(retrievedPaths.equals(newPaths));*/
	}
	
	public void testMapNodesMultiLevel() throws RubatoException {
		this.objects.multiLevelMacroScore.appendFactor(this.objects.generator.createNodeDenotator(this.objects.note2Absolute));
		this.scoreManager.setInitialComposition(this.objects.multiLevelMacroScore);
		BigBangTransformation translation = this.makeTranslation(-2, -1, this.nodePaths);
		List<DenotatorPath> nodePaths = this.makeNotePaths(new int[]{1}, new int[]{0,1,0});
		List<DenotatorPath> newPaths = new ArrayList<DenotatorPath>(this.scoreManager.mapObjects(nodePaths, translation, false, false));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}), newPaths.get(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1,1,0}), newPaths.get(1));
		LimitDenotator expectedNode = this.objects.createMultilevelNode(new double[][]{{-1,2,-4,0,0,0},{1,-3,5,0,1,0}});
		this.objects.assertEqualNonPowerDenotators(expectedNode, this.scoreManager.getComposition().get(new int[]{1,1,0}));
		
		nodePaths = this.makeNotePaths(new int[]{0}, new int[]{1,1,0});
		newPaths = new ArrayList<DenotatorPath>(this.scoreManager.mapObjects(nodePaths, translation, false, false));
		TestCase.assertEquals(newPaths.get(0), new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}));
		TestCase.assertEquals(newPaths.get(1), new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1,1,0}));
		expectedNode = this.objects.createMultilevelNode(new double[][]{{-3,1,-4,0,0,0},{1,-3,5,0,1,0}});
		this.objects.assertEqualNonPowerDenotators(expectedNode, this.scoreManager.getComposition().get(new int[]{1,1,0}));
	}
	
	public void testMapModulators() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.flatMacroScore);
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
		//build modulator structure
		this.scoreManager.moveObjectsToParent(paths, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}), 1, false);
		this.scoreManager.moveObjectsToParent(paths, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0,6,0}), 0, false);
		
		BigBangTransformation translation = this.makeTranslation(-2, -1, this.notePaths);
		List<DenotatorPath> nodePaths = this.makeNotePaths(new int[]{0,0,6,0,6,0});
		List<DenotatorPath> newPaths = this.scoreManager.mapObjects(nodePaths, translation, false, false);
		TestCase.assertEquals(newPaths.get(0), new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0,6,0,6,0}));
		LimitDenotator expectedNote = this.objects.generator.createNoteDenotator(new double[]{-1,-4,5,0,1,0});
		this.objects.assertEqualNonPowerDenotators(expectedNote, this.scoreManager.getComposition().get(new int[]{0,0,6,0,6,0}));
	}
	
	public void testCopyAndMapDenotator() {
		this.scoreManager.setInitialComposition(this.objects.flatMacroScore);
		TreeSet<DenotatorPath> nodePaths = new TreeSet<DenotatorPath>();
		nodePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}));
		nodePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
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
		this.scoreManager.setInitialComposition(this.objects.multiLevelMacroScore);
		DenotatorPath nodePath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,1,0});
		//LimitDenotator absoluteNode = this.scoreManager.getComposition().getAbsoluteNode(nodePath);
		//TestCase.assertEquals(absoluteNode, this.node2Absolute);
	}
	
	public void testBuildSatellites() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.flatMacroScore);
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1,0}));
		//build first satellite and check if it's there
		this.scoreManager.moveObjectsToParent(paths, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}), 0, false);
		this.objects.assertEqualNonPowerDenotators(this.objects.node1Relative, this.scoreManager.getComposition().get(new int[]{0,1,0}));
		//build second satellite and check if it's there
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 0);
		List<DenotatorPath> satellitePaths = this.scoreManager.moveObjectsToParent(paths, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0}), 0, false);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 1);
		this.objects.assertEqualNonPowerDenotators(this.objects.node2Relative, this.scoreManager.getComposition().get(new int[]{0,1,0,1,0}));
		//undo and check if original is there again
		this.scoreManager.undoMoveToParent(new ArrayList<DenotatorPath>(satellitePaths), paths);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 0);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition()).getFactorCount() == 2);
		LimitDenotator expectedNode = this.objects.createMultilevelNode(new double[][]{{0,60,120,1,0,0},{1,3,-4,0,0,0}});
		this.objects.assertEqualNonPowerDenotators(expectedNode, this.scoreManager.getComposition().get(new int[]{0}));
		this.objects.assertEqualNonPowerDenotators(this.objects.node2Absolute, this.scoreManager.getComposition().get(new int[]{1}));
	}
	
	public void testBuildModulators() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.flatMacroScore);
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
		//build first modulator and check if it's there
		this.scoreManager.moveObjectsToParent(paths, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}), 1, false);
		this.objects.assertEqualNonPowerDenotators(this.objects.note1Relative, this.scoreManager.getComposition().get(new int[]{0,0,6,0}));
		//build second modulator and check if it's there
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,0,6,0,6})).getFactorCount() == 0);
		List<DenotatorPath> modulatorPaths = this.scoreManager.moveObjectsToParent(paths, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0,6,0}), 0, false);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,0,6,0,6})).getFactorCount() == 1);
		Denotator addedModulator = this.scoreManager.getComposition().get(new int[]{0,0,6,0,6,0});
		TestCase.assertEquals(this.objects.generator.getSoundNoteForm(), addedModulator.getForm());
		this.objects.assertEqualNonPowerDenotators(this.objects.note2Relative, this.scoreManager.getComposition().get(new int[]{0,0,6,0,6,0}));
		//undo and check if original is there again
		this.scoreManager.undoMoveToParent(new ArrayList<DenotatorPath>(modulatorPaths), paths);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,0,6,0,6})).getFactorCount() == 0);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition()).getFactorCount() == 2);
		//this.objects.assertEqualDenotators(this.objects.node0, this.scoreManager.getComposition().get(new int[]{0}));
		this.objects.assertEqualNonPowerDenotators(this.objects.node2Absolute, this.scoreManager.getComposition().get(new int[]{1}));
		this.objects.assertEqualNonPowerDenotators(this.objects.note1Relative, this.scoreManager.getComposition().get(new int[]{0,0,6,0}));
		//try to add a note with a modulator as a satellite
		paths = new ArrayList<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0}));
		this.scoreManager.moveObjectsToParent(paths, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}), 0, false);
		//this.objects.assertEqualDenotators(this.objects.node2Absolute, this.scoreManager.getComposition().get(new int[]{0}));
		//this.objects.assertEqualDenotators(this.objects.generator.createNodeDenotator(new double[]{-2,0,-1,0,-1,0}), this.scoreManager.getComposition().get(new int[]{0,1,0}));
		this.objects.assertEqualNonPowerDenotators(this.objects.note1Relative, this.scoreManager.getComposition().get(new int[]{0,1,0,0,6,0}));
	}
	
	public void testFlatten() throws RubatoException {
		this.objects = new TestObjects();
		this.scoreManager = new BigBangScoreManager(new BigBangController());
		this.scoreManager.setInitialComposition(this.objects.multiLevelMacroScore);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1})).getFactorCount() == 1);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 1);
		TreeSet<DenotatorPath> paths = new TreeSet<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,1,0,0}));
		this.scoreManager.flattenNotes(paths);
		TestCase.assertTrue(((PowerDenotator)this.scoreManager.getComposition().get(new int[]{0,1})).getFactorCount() == 2);
	}
	
	public void testFlatten2() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.multiLevelMacroScore);
		TreeSet<DenotatorPath> paths = new TreeSet<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0}));
		//flatten the first path
		Map<DenotatorPath,DenotatorPath> newAndOldPaths = this.scoreManager.flattenNotes(paths);
		this.objects.assertEqualNonPowerDenotators(this.objects.node0, this.scoreManager.getComposition().get(new int[]{0}));
		LimitDenotator expectedNode = this.objects.createMultilevelNode(new double[][]{{1,63,116,1,0,0},{1,-3,5,0,1,0}});
		this.objects.assertEqualNonPowerDenotators(expectedNode, this.scoreManager.getComposition().get(new int[]{1}));
		this.scoreManager.unflattenNotes(newAndOldPaths);
		expectedNode = this.objects.createMultilevelNode(new double[][]{{0,60,120,1,0,0},{1,3,-4,0,0,0},{1,-3,5,0,1,0}});
		this.objects.assertEqualNonPowerDenotators(expectedNode, this.scoreManager.getComposition().get(new int[]{0}));
	}
	
	public void testShapeNotes() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.flatMacroScore);
		Set<DenotatorPath> paths = new TreeSet<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}));
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}));
		
		TransformationProperties properties = new TransformationProperties(paths, this.nodePaths, false, false, false);
		TreeMap<Double,Double> shapingLocations = new TreeMap<Double,Double>();
		shapingLocations.put(-0.3, 69.0);
		shapingLocations.put(0.5, 71.0);
		shapingLocations.put(4.0, 99.0);
		this.scoreManager.shapeNotes(properties, shapingLocations);
		
		LimitDenotator expectedNote = this.objects.generator.createNodeDenotator(new double[]{0,69,120,1,0,0});
		this.objects.assertEqualNonPowerDenotators(expectedNote, this.scoreManager.getComposition().get(new int[]{0}));
		expectedNote = this.objects.generator.createNodeDenotator(new double[]{1,71,116,1,0,0});
		this.objects.assertEqualNonPowerDenotators(expectedNote, this.scoreManager.getComposition().get(new int[]{1}));
		expectedNote = this.objects.generator.createNodeDenotator(new double[]{2,60,121,1,1,0});
		this.objects.assertEqualNonPowerDenotators(expectedNote, this.scoreManager.getComposition().get(new int[]{2}));
	}
	
	public void testShapeRationalTriples() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.rationalTriples);
		Set<DenotatorPath> paths = new TreeSet<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{0}));
		paths.add(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{1}));
		paths.add(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{2}));
		paths.add(new DenotatorPath(this.objects.RATIONAL_TRIPLES_FORM, new int[]{3}));
		
		TransformationProperties properties = new TransformationProperties(paths, this.rationalTriplesPaths, false, false, false);
		TreeMap<Double,Double> shapingLocations = new TreeMap<Double,Double>();
		shapingLocations.put(0.7, 1.0);
		shapingLocations.put(2.3, 11.0);
		shapingLocations.put(4.1, 99.0);
		shapingLocations.put(5.0, 0.0);
		this.scoreManager.shapeNotes(properties, shapingLocations);
		
		Denotator expectedTriple = this.objects.createRationalTriple(new double[]{1,2,11});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(new int[]{0}));
		expectedTriple = this.objects.createRationalTriple(new double[]{2,1,1});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(new int[]{1}));
		expectedTriple = this.objects.createRationalTriple(new double[]{3,4,99});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(new int[]{2}));
		expectedTriple = this.objects.createRationalTriple(new double[]{4,3,1});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(new int[]{3}));
	}
	
	//ProductElement element1 = ProductElement.make(new RElement(1), new RElement(2), new RElement(3));
	//ProductElement element2 = ProductElement.make(new RElement(4), new RElement(3), new RElement(1));
	//ProductElement element3 = ProductElement.make(new RElement(2), new RElement(1), new RElement(5));
	public void testShapeRealTriples() throws RubatoException {
		this.scoreManager.setInitialComposition(this.objects.realTriples);
		Set<DenotatorPath> paths = new TreeSet<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{0}));
		paths.add(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{1}));
		paths.add(new DenotatorPath(this.objects.REAL_TRIPLES_FORM, new int[]{2}));
		
		TransformationProperties properties = new TransformationProperties(paths, this.realTriplesPaths, false, false, false);
		TreeMap<Double,Double> shapingLocations = new TreeMap<Double,Double>();
		shapingLocations.put(1.2, 1.0);
		shapingLocations.put(3.0, 11.0);
		shapingLocations.put(2.1, 99.0);
		this.scoreManager.shapeNotes(properties, shapingLocations);
		
		Denotator expectedTriple = this.objects.createRealTriple(new double[]{1,2,1});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(new int[]{0}));
		expectedTriple = this.objects.createRealTriple(new double[]{2,1,99});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(new int[]{1}));
		expectedTriple = this.objects.createRealTriple(new double[]{4,3,1});
		this.objects.assertEqualNonPowerDenotators(expectedTriple, this.scoreManager.getComposition().get(new int[]{2}));
	}
	
	private BigBangTransformation makeTranslation(int x, int y, TransformationPaths paths) {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		ModuleMorphism translation = RFreeAffineMorphism.make(identity, new double[]{x, y});
		return new BigBangTransformation(translation, paths, false, null);
	}
	
	private List<DenotatorPath> makeNotePaths(int[]... intNotePaths) {
		List<DenotatorPath> notePaths = new ArrayList<DenotatorPath>();
		for (int[] currentPath: intNotePaths) {
			notePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, currentPath));
		}
		return notePaths;
	}
	
	@SuppressWarnings("unused")
    private void printNote(Denotator note) throws RubatoException {
		for (int i = 0; i < 7; i++) {
			System.out.println(note.getElement(new int[]{i,0}));
		}
	}

}

package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.rubettes.bigbang.model.BigBangScore;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangScoreTest extends TestCase {
	
	private BigBangScore score;
	private TestObjects objects;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.score = new BigBangScore(this.objects.SOUND_SCORE_FORM);;
	}
	
	public void testAddNote() throws RubatoException {
		DenotatorPath topLevelPath = new DenotatorPath(this.objects.SOUND_SCORE_FORM);
		DenotatorPath nodePath = this.score.addObject(topLevelPath, this.generateNodeValueMap(this.objects.NOTE0_VALUES));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}), nodePath);
		this.assertEqualNodes(this.objects.node0, this.score.getObject(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0})));
		nodePath = this.score.addObject(topLevelPath, this.generateNodeValueMap(this.objects.NOTE2_ABSOLUTE_VALUES));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}), nodePath);
		this.assertEqualNodes(this.objects.node2Absolute, this.score.getObject(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1})));
		nodePath = this.score.addObject(topLevelPath, this.generateNodeValueMap(this.objects.NOTE1_ABSOLUTE_VALUES));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}), nodePath);
		this.assertEqualNodes(this.objects.node1Absolute, this.score.getObject(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1})));
		PowerDenotator composition = (PowerDenotator)this.score.getComposition();
		TestCase.assertTrue(composition.getFactorCount() == 3);
	}
	
	public void testAddNodesAndFindPaths() {
		//addNodes
		this.score.setInitialComposition(this.objects.multiLevelMacroScore);
		List<DenotatorPath> anchorPaths = new ArrayList<DenotatorPath>();
		anchorPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}));
		anchorPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,0}));
		anchorPaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,0}));
		List<Denotator> notes = new ArrayList<Denotator>();
		notes.add(this.objects.node1Absolute);
		notes.add(this.objects.node2Absolute);
		notes.add(this.objects.node0);
		List<DenotatorPath> satellitePaths = this.score.addObjects(notes, anchorPaths);
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}), satellitePaths.get(0));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,1}), satellitePaths.get(1));
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,1,0}), satellitePaths.get(2));
		//findPaths
		
	}
	
	public void testAddNotes() {
		this.score.setInitialComposition(this.objects.multiLevelMacroScore);
		List<List<Denotator>> noteLists = new ArrayList<List<Denotator>>();
		List<Denotator> notes = new ArrayList<Denotator>();
		notes.add(this.objects.node2Absolute.copy());
		noteLists.add(notes);
		notes = new ArrayList<Denotator>();
		notes.add(this.objects.node1Absolute.copy());
		noteLists.add(notes);
		List<DenotatorPath> newPaths = this.score.addObjects(noteLists);
		List<DenotatorPath> expectedPaths = this.makeNodePaths(new int[]{2}, new int[]{1});
		TestCase.assertEquals(expectedPaths, newPaths);
	}
	
	public void testGetAbsoluteNote() throws RubatoException {
		this.score.setInitialComposition(this.objects.multiLevelMacroScore);
		DenotatorPath nodePath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,1,0});
		this.assertEqualNodes(this.objects.node2Relative, this.score.getObject(nodePath));
		this.assertEqualNodes(this.objects.node2Absolute, this.score.getAbsoluteObject(nodePath));
	}
	
	public void testRemoveNote() throws RubatoException {
		this.score.setInitialComposition(this.objects.multiLevelMacroScore);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 1);
		Denotator removedNode = this.score.removeObject(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,1,0}));
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 0);
		this.assertEqualNodes(this.objects.node2Absolute, removedNode);
	}
	
	public void testRemoveNotes() throws RubatoException {
		this.score.setInitialComposition(this.objects.flatMacroScore);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition()).getFactorCount() == 3);
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
		Denotator removedNote = this.score.removeObjects(paths).get(0);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition()).getFactorCount() == 2);
		this.assertEqualNodes(removedNote, this.objects.node1Absolute);
		
		this.score.setInitialComposition(this.objects.multiLevelMacroScore);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 1);
		paths = new ArrayList<DenotatorPath>();
		paths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0,1,0}));
		removedNote = this.score.removeObjects(paths).get(0);
		TestCase.assertTrue(((PowerDenotator)this.score.getComposition().get(new int[]{0,1,0,1})).getFactorCount() == 0);
		this.assertEqualNodes(removedNote, this.objects.node2Absolute);
	}
	
	private List<DenotatorPath> makeNodePaths(int[]... intNodePaths) {
		List<DenotatorPath> nodePaths = new ArrayList<DenotatorPath>();
		for (int[] currentPath: intNodePaths) {
			nodePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, currentPath));
		}
		return nodePaths;
	}
	
	private Map<DenotatorPath,Double> generateNodeValueMap(double[] noteValues) {
		Map<DenotatorPath,Double> valueMap = new TreeMap<DenotatorPath,Double>();
		for (int i = 0; i < noteValues.length; i++) {
			valueMap.put(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,i}), noteValues[i]);
		}
		return valueMap;
	}
	
	private void assertEqualNodes(Denotator note1, Denotator note2) throws RubatoException {
		for (int i = 0; i < 6; i++) {
			TestCase.assertEquals(note1.get(new int[]{0,i}).getCoordinate(), note2.get(new int[]{0,i}).getCoordinate());
		}
	}
	
	@SuppressWarnings("unused")
    private void printNote(Denotator node) throws RubatoException {
		for (int i = 0; i < 6; i++) {
			System.out.println(node.getElement(new int[]{i,0}));
		}
	}

}

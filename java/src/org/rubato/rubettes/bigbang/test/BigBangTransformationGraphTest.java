package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.CompositionState;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AddObjectsEdit;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

import edu.uci.ics.jung.graph.util.Pair;

public class BigBangTransformationGraphTest extends TestCase {
	
	private BigBangModel model;
	private TestObjects objects;
	private TransformationPaths nodePaths;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.model = new BigBangModel(new BigBangController());
		this.nodePaths = this.objects.createStandardTransformationPaths(
				this.objects.SOUND_NODE_FORM, new int[][]{{0,0},{0,1}});
	}
	
	public void testRepeatedAddWithLimit() {
		this.model.setInitialComposition(this.objects.createInteger(3));
		TestCase.assertEquals(3, ((SimpleDenotator)this.model.getComposition()).getInteger());
		int[][] paths = new int[][]{{}};
		double[][] values = new double[][]{{12}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.INTEGER_FORM, paths, values),
				this.createPathsList(null, 1), false);
		TestCase.assertEquals(12, ((SimpleDenotator)this.model.getComposition()).getInteger());
	}
	
	public void testModifyLeadingToTooManyPaths() {
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66},{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getUndoRedoModel().getLastEdit() instanceof AddObjectsEdit);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		SelectedObjectsPaths selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0}, new int[]{1}, new int[]{2}, new int[]{3});
		TransformationProperties properties = new TransformationProperties(selectedPaths, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.rotateObjects(properties, new double[]{1,0}, Math.PI/2);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		this.model.getUndoRedoModel().modifyOperation(0, 0.5);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
	}
	
	public void testModifyWithSatellites() {
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66},{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getUndoRedoModel().getLastEdit() instanceof AddObjectsEdit);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		ArrayList<DenotatorPath> nodePaths = new ArrayList<DenotatorPath>();
		nodePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0}));
		nodePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
		nodePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}));
		DenotatorPath parentNotePath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{3});
		this.model.buildSatellites(nodePaths, parentNotePath, 0);
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		this.model.getUndoRedoModel().modifyOperation(1, 0.5);
		//less satellites but still one anchor
		//TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		this.model.getUndoRedoModel().modifyOperation(0, 0.5);
		//anchor tone not there anymore so satellites should not be built. all we get are two of the four initial notes
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//transform satellites
		TransformationProperties properties = new TransformationProperties(this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0}, new int[]{0,1,1}, new int[]{0,1,2}), Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.scaleObjects(properties , new double[]{2,2});
		this.model.getUndoRedoModel().modifyOperation(2, 0.5);
		
		//test animation
		this.model.getUndoRedoModel().setOperationDurations(.1);
		this.model.getUndoRedoModel().toggleGraphAnimation();
	}
	
	public void testPathDifferences() {
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{1,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 2), false);
		TestCase.assertTrue(this.model.getUndoRedoModel().getLastEdit() instanceof AddObjectsEdit);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//translation so note0 ends up earlier than note1
		SelectedObjectsPaths selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0});
		TransformationProperties properties = new TransformationProperties(selectedPaths, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{2,0});
		this.model.translateObjects(properties);
		Denotator expectedResult = this.objects.generator.createFlatSoundScore(new double[][]{{1,60,0,0,0,0},{2,60,0,0,0,0}});
		TestCase.assertEquals(expectedResult, this.model.getComposition());
		
		//translate note0 at its later position
		selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{1});
		properties = new TransformationProperties(selectedPaths, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{2,0});
		this.model.translateObjects(properties);
		expectedResult = this.objects.generator.createFlatSoundScore(new double[][]{{1,60,0,0,0,0},{4,60,0,0,0,0}});
		TestCase.assertEquals(expectedResult, this.model.getComposition());
		
		//modify first translation so note0 is back at earlier position
		this.model.getUndoRedoModel().modifyOperation(1, 0.0);
		//note0 should still be affected by second translation since pathDifferences should have recorded its identity
		//so (1,60),(2,60) and not (0,60),(3,60)
		expectedResult = this.objects.generator.createFlatSoundScore(new double[][]{{1,60,0,0,0,0},{2,60,0,0,0,0}});
		TestCase.assertEquals(expectedResult, this.model.getComposition());
	}
	
	public void testPathDifferencesWithSatellites() throws RubatoException {
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{1,60},{2,65}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 3), false);
		TestCase.assertTrue(this.model.getUndoRedoModel().getLastEdit() instanceof AddObjectsEdit);
		TestCase.assertEquals(3, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		ArrayList<DenotatorPath> nodePaths = new ArrayList<DenotatorPath>();
		nodePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{1}));
		nodePaths.add(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}));
		DenotatorPath parentNotePath = new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0});
		this.model.buildSatellites(nodePaths, parentNotePath, 0);
		
		//translate both satellites by 1 pitch
		SelectedObjectsPaths selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0,1,0}, new int[]{0,1,1});
		TransformationProperties properties = new TransformationProperties(selectedPaths, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		Denotator expectedNode = this.objects.createMultilevelNode(new double[][]{{1,1,0,0,0,0}});
		TestCase.assertEquals(expectedNode, this.model.getComposition().get(new int[]{0,1,0}));
		expectedNode = this.objects.createMultilevelNode(new double[][]{{2,6,0,0,0,0}});
		TestCase.assertEquals(expectedNode, this.model.getComposition().get(new int[]{0,1,1}));
		
		//modify so that second satellite not built
		this.model.getUndoRedoModel().modifyOperation(1, 0.5);
		//still both nodes should be transformed
		expectedNode = this.objects.createMultilevelNode(new double[][]{{1,1,0,0,0,0}});
		TestCase.assertEquals(expectedNode, this.model.getComposition().get(new int[]{0,1,0}));
		expectedNode = this.objects.createMultilevelNode(new double[][]{{2,66,0,0,0,0}});
		TestCase.assertEquals(expectedNode, this.model.getComposition().get(new int[]{1}));
	}
	
	public void testAddAlternativeEdge() {
		//add one note and perform two translations
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		SelectedObjectsPaths selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0});
		TransformationProperties properties = new TransformationProperties(selectedPaths, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(4, this.model.getUndoRedoModel().getTransformationGraph().getVertexCount());
		TestCase.assertEquals(3, this.model.getUndoRedoModel().getTransformationGraph().getEdgeCount());
		
		//insert a translation starting at state 2 and check graph structure
		this.model.getUndoRedoModel().selectCompositionState(2);
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getUndoRedoModel().getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getUndoRedoModel().getTransformationGraph().getEdgeCount());
		AbstractOperationEdit lastEdit = this.model.getUndoRedoModel().getTransformationGraph().getLastEdit(); 
		TestCase.assertEquals(new Pair<Integer>(2,4), this.model.getUndoRedoModel().getTransformationGraph().getEndpoints(lastEdit));
	}
	
	public void testInsertEdge() {
		//add one note and perform two translations
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		SelectedObjectsPaths selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0});
		TransformationProperties properties = new TransformationProperties(selectedPaths, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(4, this.model.getUndoRedoModel().getTransformationGraph().getVertexCount());
		TestCase.assertEquals(3, this.model.getUndoRedoModel().getTransformationGraph().getEdgeCount());
		
		//insert a translation before the second one and check graph structure
		this.model.getUndoRedoModel().insertOperation(1);
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getUndoRedoModel().getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getUndoRedoModel().getTransformationGraph().getEdgeCount());
	}
	
	public void testRemoveEdge() {
		//add one note and perform three translations
		this.model.setInitialComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		SelectedObjectsPaths selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0});
		TransformationProperties properties = new TransformationProperties(selectedPaths, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getUndoRedoModel().getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getUndoRedoModel().getTransformationGraph().getEdgeCount());
		
		//remove second translation and check graph structure
		this.model.getUndoRedoModel().removeOperation(this.model.getUndoRedoModel().getTransformationGraph().findEdge(2, 3));
		TestCase.assertEquals(4, this.model.getUndoRedoModel().getTransformationGraph().getVertexCount());
		TestCase.assertEquals(3, this.model.getUndoRedoModel().getTransformationGraph().getEdgeCount());
	}
	
	public void testKristinsBookLayout() {
		double totalHorizontalBlank = 19-(4.0533)-2*6;
		int numberOfHorizontalSpaces = 4;
		double[] randomNumbers = new double[numberOfHorizontalSpaces];
		double sum = 0;
		for (int i = 0; i < numberOfHorizontalSpaces; i++) {
			randomNumbers[i] = Math.random();
			sum += randomNumbers[i];
		}
		for (int i = 0; i < numberOfHorizontalSpaces; i++) {
			randomNumbers[i] = randomNumbers[i]/sum*totalHorizontalBlank;
		}
		System.out.println("Kristin's horizontal spaces: "+Arrays.toString(randomNumbers));
		
		double totalVerticalBlank = 8-4.29;
		int numberOfVerticalSpaces = 2;
		randomNumbers = new double[numberOfVerticalSpaces];
		for (int i = 0; i < numberOfVerticalSpaces; i++) {
			randomNumbers[i] = Math.random()*totalVerticalBlank;
		}
		System.out.println("Kristin's vertical spaces: "+Arrays.toString(randomNumbers));
	}
	
	private ArrayList<Map<DenotatorPath,Double>> createNodePathAndValuesMapList(Form form, int[][] paths, double[][] values) {
		ArrayList<Map<DenotatorPath,Double>> list = new ArrayList<Map<DenotatorPath,Double>>();
		for (int i = 0; i < values.length; i++) {
			list.add(this.createNodePathAndValuesMap(form, paths, values[i]));
		}
		return list;
	}
	
	private ArrayList<DenotatorPath> createPathsList(DenotatorPath path, int amount) {
		ArrayList<DenotatorPath> list = new ArrayList<DenotatorPath>();
		for (int i = 0; i < amount; i++) {
			list.add(path);
		}
		return list;
	}
	
	private Map<DenotatorPath,Double> createNodePathAndValuesMap(Form form, int[][] paths, double[] values) {
		Map<DenotatorPath,Double> valuesMap = new TreeMap<DenotatorPath,Double>();
		for (int i = 0; i < paths.length; i++) {
			valuesMap.put(new DenotatorPath(form, paths[i]), values[i]);
		}
		//valuesMap.put(new DenotatorPath(this.objects.SOUND_NODE_FORM, new int[]{0,1}), pitch);
		return valuesMap;
	}
	
	private SelectedObjectsPaths createSelectedObjectsPaths(Form form, int[]... intPaths) {
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		for (int i = 0; i < intPaths.length; i++) {
			paths.add(new DenotatorPath(form, intPaths[i]));
		}
		return new SelectedObjectsPaths(paths, null);
	}

}

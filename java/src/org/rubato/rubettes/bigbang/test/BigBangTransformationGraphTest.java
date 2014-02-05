package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AddObjectsEdit;
import org.rubato.rubettes.bigbang.model.edits.ScalingEdit;
import org.rubato.rubettes.bigbang.model.edits.TranslationEdit;
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
		this.model.setOrAddComposition(this.objects.createInteger(3));
		TestCase.assertEquals(3, ((SimpleDenotator)this.model.getComposition()).getInteger());
		int[][] paths = new int[][]{{}};
		double[][] values = new double[][]{{12}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.INTEGER_FORM, paths, values),
				this.createPathsList(null, 1), false);
		TestCase.assertEquals(12, ((SimpleDenotator)this.model.getComposition()).getInteger());
	}
	
	public void testModifyLeadingToTooManyPaths() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66},{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsEdit);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		Set<BigBangObject> objects = this.getBBObjectsFromModel(0,3);
		TransformationProperties properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.rotateObjects(properties, new double[]{1,0}, Math.PI/2);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		this.model.modifyOperation(1, 0.5);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
	}
	
	public void testModifyWithSatellites() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66},{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsEdit);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		Set<BigBangObject> satelliteObjects = this.getBBObjectsFromModel(0, 2);
		BigBangObject anchorObject = this.getBBObjectsFromModel(3, 3).iterator().next();
		this.model.buildSatellites(new TreeSet<BigBangObject>(satelliteObjects), anchorObject, 0);
		/*TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		this.model.modifyOperation(2, 0.5);
		//less satellites built so one anchor and a non-added satellite in top level
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		this.model.modifyOperation(1, 0.5);
		//anchor tone not there anymore so satellites should not be built. all we get are two of the four initial notes
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//transform satellites
		//satelliteObjects = this.getBBObjectsFromModel(0, 1);
		TransformationProperties properties = new TransformationProperties(satelliteObjects, null, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.scaleObjects(properties , new double[]{2,2});
		
		//back to unmodified. satellites should now be transformed
		this.model.modifyOperation(1, 1.0);
		this.model.modifyOperation(2, 1.0);
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//test animation
		this.model.setOperationDurations(.1);
		this.model.toggleGraphAnimation();*/
	}
	
	public void testModifyWithSatellitesAddedDirectly() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66}};
		ArrayList<DenotatorPath> pathList = this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1}), 3);
		pathList.add(0, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}));
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				pathList, false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsEdit);
		//only one first-level note
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		this.model.modifyOperation(0, 0.5);
		//anchor tone not there anymore so satellites should not be built. all we get are two of the four initial notes
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//transform satellites
		Set<BigBangObject> satelliteObjects = this.getBBObjectsFromModel(1, 2);
		TransformationProperties properties = new TransformationProperties(satelliteObjects, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.scaleObjects(properties, new double[]{2,2});
		this.model.modifyOperation(1, 0.5);
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//add one more
		paths = new int[][]{{0,0},{0,1}};
		values = new double[][]{{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1}), 1), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsEdit);
		//only one first-level note
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//test animation
		this.model.setOperationDurations(.1);
		this.model.toggleGraphAnimation();
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
	}
	
	public void testFatefulModificationOfPast() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{1,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 2), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsEdit);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//translation so note0 ends up later than note1
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 0);
		//SelectedObjectPaths selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0});
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{2,0});
		System.out.println("\n\n\n\n");
		this.model.translateObjects(properties);
		Denotator expectedResult = this.objects.generator.createFlatSoundScore(new double[][]{{1,60,0,0,0,0},{2,60,0,0,0,0}});
		TestCase.assertEquals(expectedResult, this.model.getComposition());
		
		//translate note0 at its later position (the same set of objects can be used since BBObject kept track!) 
		this.model.translateObjects(properties);
		expectedResult = this.objects.generator.createFlatSoundScore(new double[][]{{1,60,0,0,0,0},{4,60,0,0,0,0}});
		TestCase.assertEquals(expectedResult, this.model.getComposition());
		
		//modify first translation so note0 is back at earlier position
		this.model.modifyOperation(2, 0.0);
		//note0 should still be affected by second translation since BigBangObjects should have recorded its identity
		//so (1,60),(2,60) and not (0,60),(3,60)
		expectedResult = this.objects.generator.createFlatSoundScore(new double[][]{{1,60,0,0,0,0},{2,60,0,0,0,0}});
		TestCase.assertEquals(expectedResult, this.model.getComposition());
	}
	
	public void testFatefulModificationOfPastWithSatellites() throws RubatoException {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{1,60},{2,65}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 3), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsEdit);
		TestCase.assertEquals(3, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//get objects and build satellites
		BigBangObject anchor = this.getBBObjectsFromModel(0, 0).iterator().next();
		Set<BigBangObject> satellites = this.getBBObjectsFromModel(1, 2);
		this.model.buildSatellites(new TreeSet<BigBangObject>(satellites), anchor, 0);
		
		//translate both satellites by 1 pitch
		TransformationProperties properties = new TransformationProperties(satellites, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		Denotator expectedNode = this.objects.createMultilevelNode(new double[][]{{1,1,0,0,0,0}});
		TestCase.assertEquals(expectedNode, this.model.getComposition().get(new int[]{0,1,0}));
		expectedNode = this.objects.createMultilevelNode(new double[][]{{2,6,0,0,0,0}});
		TestCase.assertEquals(expectedNode, this.model.getComposition().get(new int[]{0,1,1}));
		
		//modify so that second satellite not built
		this.model.modifyOperation(2, 0.5);
		//still both nodes should be transformed
		expectedNode = this.objects.createMultilevelNode(new double[][]{{1,1,0,0,0,0}});
		TestCase.assertEquals(expectedNode, this.model.getComposition().get(new int[]{0,1,0}));
		expectedNode = this.objects.createMultilevelNode(new double[][]{{2,66,0,0,0,0}});
		TestCase.assertEquals(expectedNode, this.model.getComposition().get(new int[]{1}));
	}
	
	public void testAddAlternativeEdge() {
		//add one note and perform two translations
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 0);
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getTransformationGraph().getEdgeCount());
		
		//add a translation starting at state 3 and check graph structure
		this.model.selectCompositionState(3);
		this.model.translateObjects(properties);
		TestCase.assertEquals(6, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		AbstractOperationEdit lastEdit = this.model.getTransformationGraph().getLastAddedOperation(); 
		TestCase.assertEquals(new Pair<Integer>(3,5), this.model.getTransformationGraph().getEndpoints(lastEdit));
	}
	
	public void testAddParallelEdge() {
		//add one note and perform two translations
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 0);
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getTransformationGraph().getEdgeCount());
		
		//add a parallel translation starting at state 2 and check graph structure
		this.model.selectOperation(this.model.getTransformationGraph().getLastAddedOperation());
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		AbstractOperationEdit lastEdit = this.model.getTransformationGraph().getLastAddedOperation(); 
		//System.out.println(this.model.getTransformationGraph());
		TestCase.assertEquals(new Pair<Integer>(3,4), this.model.getTransformationGraph().getEndpoints(lastEdit));
	}
	
	public void testInsertEdge() {
		//add one note and perform two translations
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 0);
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getTransformationGraph().getEdgeCount());
		
		//insert a translation before the second one and check graph structure
		this.model.setInsertionState(1);
		this.model.translateObjects(properties);
		TestCase.assertEquals(6, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
	}
	
	public void testSplitEdge() {
		//add one note and perform a translation and a scaling
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 0);
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.scaleObjects(properties, new double[]{0,2});
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getTransformationGraph().getEdgeCount());
		
		//move to beginning of the translation, select it, and split
		this.model.setGraphAnimationPosition(.6); //at .4 of translation (4*.1)
		this.model.selectOperation(this.model.getTransformationGraph().getOutEdges(2).iterator().next());
		this.model.splitOperation();
		
		//check number of nodes and edges and the shifts of the resulting translations
		TestCase.assertEquals(6, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		TranslationEdit firstPart = (TranslationEdit)this.model.getTransformationGraph().getOutEdges(2).iterator().next();
		TestCase.assertEquals(0.0, firstPart.getStartingPoint()[1]);
		//cope with rounding error
		TestCase.assertEquals(0.4, ((double)Math.round(firstPart.getEndingPoint()[1]*1000))/1000);
		TranslationEdit secondPart = (TranslationEdit)this.model.getTransformationGraph().getOutEdges(3).iterator().next();
		TestCase.assertEquals(0.4, ((double)Math.round(1000*secondPart.getStartingPoint()[1]))/1000);
		TestCase.assertEquals(1.0, ((double)Math.round(1000*secondPart.getEndingPoint()[1]))/1000);
		
		//move to beginning of the scaling, select it, and split
		this.model.setGraphAnimationPosition(.9); //at .5 of scaling (5*.1)
		this.model.selectOperation(this.model.getTransformationGraph().getOutEdges(4).iterator().next());
		this.model.splitOperation();
		
		//check number of nodes and edges and the shifts of the resulting scalings
		TestCase.assertEquals(7, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(6, this.model.getTransformationGraph().getEdgeCount());
		ScalingEdit firstScalingPart = (ScalingEdit)this.model.getTransformationGraph().getOutEdges(4).iterator().next();
		TestCase.assertEquals(0.0, firstScalingPart.getCenter()[1]);
		//cope with rounding error
		TestCase.assertEquals(1.5, ((double)Math.round(firstScalingPart.getScaleFactors()[1]*1000))/1000);
		ScalingEdit secondScalingPart = (ScalingEdit)this.model.getTransformationGraph().getOutEdges(5).iterator().next();
		TestCase.assertEquals(0.0, secondScalingPart.getCenter()[1]);
		TestCase.assertEquals(1.5, ((double)Math.round(1000*secondScalingPart.getScaleFactors()[1]))/1000);
	}
	
	public void testRemoveEdge() {
		//add one note and perform three translations
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 0);
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(6, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		
		//remove second translation and check graph structure
		this.model.removeOperation(this.model.getTransformationGraph().findEdge(3, 4));
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getTransformationGraph().getEdgeCount());
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
	
	private Set<BigBangObject> getBBObjectsFromModel(int fromIndex, int toIndex) {
		Iterator<BigBangObject> objectIterator = this.model.getObjects().getObjects().iterator();
		Set<BigBangObject> objects = new TreeSet<BigBangObject>();
		for (int i = 0; i <= toIndex; i++) {
			BigBangObject nextObject = objectIterator.next(); 
			if (i >= fromIndex) {
				objects.add(nextObject);
			}
		}
		return objects;
	}

}

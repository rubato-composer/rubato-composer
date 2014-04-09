package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.rubato.rubettes.bigbang.model.denotators.TransformationPaths;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.model.operations.AddObjectsOperation;
import org.rubato.rubettes.bigbang.model.operations.ScalingEdit;
import org.rubato.rubettes.bigbang.model.operations.TranslationEdit;
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
	
	
	
	public void testModifyLeadingToTooManyPaths() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66},{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsOperation);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		Set<BigBangObject> objects = this.getBBObjectsFromModel(0,4);
		TransformationProperties properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.rotateObjects(properties, new double[]{1,0}, Math.PI/2);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		this.model.modifyOperation(1, 0.5);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
	}
	
	public void testModifyWithSatellitesAddedDirectly() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66}};
		ArrayList<DenotatorPath> pathList = this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{0,1}), 3);
		pathList.add(0, new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}));
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				pathList, false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsOperation);
		//only one first-level note
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		this.model.modifyOperation(0, 0.5);
		//anchor tone not there anymore so satellites should not be built. all we get are two of the four initial notes
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//transform satellites
		Set<BigBangObject> satelliteObjects = this.getBBObjectsFromModel(1, 3);
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
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsOperation);
		//only one first-level note
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//test animation
		this.model.setOperationDurations(.1);
		//this.model.toggleGraphAnimation();
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
	}
	
	public void testModifyWithSatellites() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66},{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsOperation);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		Set<BigBangObject> satelliteObjects = this.getBBObjectsFromModel(0, 3);
		BigBangObject anchorObject = this.getBBObjectsFromModel(3, 4).iterator().next();
		this.model.buildSatellites(new TreeSet<BigBangObject>(satelliteObjects), anchorObject, 0);
		TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
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
		//this.model.toggleGraphAnimation();
		/*//satellites should still be satellites
		for (BigBangObject currentObject : this.model.getObjects().getObjects()) {
			System.out.println(currentObject.getParent());
		}
		//TestCase.assertEquals(1, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		TestCase.assertEquals(4, this.model.getObjects().getObjects().size());
		TestCase.assertEquals(3, this.model.getObjects().getObjects().iterator().next().getChildren().size());*/
	}
	
	public void testFatefulModificationOfPast() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{1,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 2), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsOperation);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//translation so note0 ends up later than note1
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 1);
		//SelectedObjectPaths selectedPaths = this.createSelectedObjectsPaths(this.objects.SOUND_SCORE_FORM, new int[]{0});
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{2,0});
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
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsOperation);
		TestCase.assertEquals(3, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		//get objects and build satellites
		BigBangObject anchor = this.getBBObjectsFromModel(0, 1).iterator().next();
		Set<BigBangObject> satellites = this.getBBObjectsFromModel(1, 3);
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
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 1);
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getTransformationGraph().getEdgeCount());
		
		//add a translation starting at state 3 and check graph structure
		this.model.selectCompositionState(this.model.getTransformationGraph().getCompositionStateAt(3));
		this.model.translateObjects(properties);
		TestCase.assertEquals(6, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		AbstractOperation lastEdit = this.model.getTransformationGraph().getLastAddedOperation(); 
		TestCase.assertEquals(new Pair<CompositionState>(this.getCompStateAt(3), this.getCompStateAt(5)), this.model.getTransformationGraph().getEndpoints(lastEdit));
	}
	
	public void testAddParallelEdge() {
		//add one note and perform two translations
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 1);
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
		AbstractOperation lastEdit = this.model.getTransformationGraph().getLastAddedOperation(); 
		//System.out.println(this.model.getTransformationGraph());
		TestCase.assertEquals(new Pair<CompositionState>(this.getCompStateAt(3), this.getCompStateAt(4)), this.model.getTransformationGraph().getEndpoints(lastEdit));
	}
	
	public void testInsertEdge() {
		//add one note and perform two translations
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 1), false);
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 1);
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
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 1);
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.scaleObjects(properties, new double[]{0,2});
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getTransformationGraph().getEdgeCount());
		
		//move to beginning of the translation, select it, and split
		this.model.setGraphAnimationPosition(.6); //at .4 of translation (4*.1)
		this.model.selectOperation(this.model.getTransformationGraph().getOutEdges(this.getCompStateAt(2)).iterator().next());
		this.model.splitOperation();
		
		//check number of nodes and edges and the shifts of the resulting translations
		TestCase.assertEquals(6, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		TranslationEdit firstPart = (TranslationEdit)this.model.getTransformationGraph().getOutEdges(this.getCompStateAt(2)).iterator().next();
		TestCase.assertEquals(0.0, firstPart.getStartingPoint()[1]);
		//cope with rounding error
		TestCase.assertEquals(0.4, ((double)Math.round(firstPart.getEndingPoint()[1]*1000))/1000);
		TranslationEdit secondPart = (TranslationEdit)this.model.getTransformationGraph().getOutEdges(this.getCompStateAt(3)).iterator().next();
		TestCase.assertEquals(0.4, ((double)Math.round(1000*secondPart.getStartingPoint()[1]))/1000);
		TestCase.assertEquals(1.0, ((double)Math.round(1000*secondPart.getEndingPoint()[1]))/1000);
		
		//move to beginning of the scaling, select it, and split
		this.model.setGraphAnimationPosition(.9); //at .5 of scaling (5*.1)
		this.model.selectOperation(this.model.getTransformationGraph().getOutEdges(this.getCompStateAt(4)).iterator().next());
		this.model.splitOperation();
		
		//check number of nodes and edges and the shifts of the resulting scalings
		TestCase.assertEquals(7, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(6, this.model.getTransformationGraph().getEdgeCount());
		ScalingEdit firstScalingPart = (ScalingEdit)this.model.getTransformationGraph().getOutEdges(this.getCompStateAt(4)).iterator().next();
		TestCase.assertEquals(0.0, firstScalingPart.getCenter()[1]);
		//cope with rounding error
		TestCase.assertEquals(1.5, ((double)Math.round(firstScalingPart.getScaleFactors()[1]*1000))/1000);
		ScalingEdit secondScalingPart = (ScalingEdit)this.model.getTransformationGraph().getOutEdges(this.getCompStateAt(5)).iterator().next();
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
		
		Set<BigBangObject> object = this.getBBObjectsFromModel(0, 1);
		TransformationProperties properties = new TransformationProperties(object, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{0,1});
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		this.model.translateObjects(properties);
		TestCase.assertEquals(6, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		
		//remove second translation and check graph structure
		this.model.removeOperation(this.model.getTransformationGraph().findEdge(this.getCompStateAt(3), this.getCompStateAt(4)));
		TestCase.assertEquals(5, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(4, this.model.getTransformationGraph().getEdgeCount());
	}
	
	public void testModifyCopyAndTransform() {
		this.model.setOrAddComposition(this.objects.flatSoundScore);
		TreeSet<BigBangObject> objects = this.getBBObjectsFromModel(0, 3);
		
		TransformationProperties properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), true, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{2,2});
		this.model.translateObjects(properties);
		Set<BigBangObject> objectsAfterTranslation = this.model.getObjects().getObjectsAt(null);
		TestCase.assertEquals(6, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(6, objectsAfterTranslation.size());
		
		this.model.modifyOperation(1, 0.0);
		TestCase.assertEquals(6, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(3, this.model.getObjects().getObjectsAt(null).size());
		
		this.model.modifyOperation(1, 1.0);
		TestCase.assertEquals(6, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(6, this.model.getObjects().getObjectsAt(null).size());
		TestCase.assertEquals(objectsAfterTranslation, this.model.getObjects().getObjectsAt(null));
	}
	
	public void testProjection() {
		//scale so that notes projected and made equal
		this.model.setOrAddComposition(this.objects.generator.createFlatSoundScore(
				new double[][]{{0,60,120,1,0,0},{1,60,120,1,0,0},{2,61,120,1,0,0}}));
		TreeSet<BigBangObject> objects = this.getBBObjectsFromModel(0, 3);
		TransformationProperties properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{-1,1});
		Set<BigBangObject> objectsBeforeProjection = this.model.getObjects().getObjectsAt(null);
		this.model.scaleObjects(properties, new double[]{0,2});
		TestCase.assertEquals(3, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(2, this.model.getObjects().getObjectsAt(null).size());
		
		//undo projection and see if objects back
		this.model.modifyOperation(1, 0.5);
		TestCase.assertEquals(3, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(3, this.model.getObjects().getObjectsAt(null).size());
		TestCase.assertEquals(objectsBeforeProjection, this.model.getObjects().getObjectsAt(null));
		
		//change to projection to one point
		properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{-1,-1});
		this.model.scaleObjects(properties, new double[]{0,0});
		TestCase.assertEquals(3, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(1, this.model.getObjects().getObjectsAt(null).size());
	}
	
	public void testTransformAnchors() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66},{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsOperation);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		List<BigBangObject> objects = new ArrayList<BigBangObject>(this.getBBObjectsFromModel(0, 4));
		this.model.buildSatellites(new TreeSet<BigBangObject>(objects.subList(3, 4)), objects.get(0), 0);
		this.model.buildSatellites(new TreeSet<BigBangObject>(objects.subList(2, 3)), objects.get(1), 0);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		TestCase.assertTrue(objects.get(3).getParent() == objects.get(0));
		TestCase.assertTrue(objects.get(2).getParent() == objects.get(1));
		
		//make transformation that switches paths of anchor objects
		TransformationProperties properties = new TransformationProperties(new TreeSet<BigBangObject>(objects.subList(0, 1)), null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{4,1});
		this.model.translateObjects(properties);
		AbstractOperation translation = this.model.getTransformationGraph().getLastAddedOperation();
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		//should still have the same parent
		TestCase.assertSame(objects.get(3).getParent(), objects.get(0));
		TestCase.assertSame(objects.get(2).getParent(), objects.get(1));
		//should not have the same paths anymore since parent paths changed
		TestCase.assertTrue(!objects.get(2).getTopDenotatorPathAt(translation).equals(objects.get(2).getTopDenotatorPathAt(null)));
		TestCase.assertTrue(!objects.get(3).getTopDenotatorPathAt(translation).equals(objects.get(3).getTopDenotatorPathAt(null)));
	}
	
	public void testCopyAndTransformAnchors() {
		this.model.setOrAddComposition(this.objects.generator.createEmptyScore());
		int[][] paths = new int[][]{{0,0},{0,1}};
		double[][] values = new double[][]{{0,60},{2,65},{3,66},{4,67}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.SOUND_SCORE_FORM, paths, values),
				this.createPathsList(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{}), 4), false);
		TestCase.assertTrue(this.model.getTransformationGraph().getLastAddedOperation() instanceof AddObjectsOperation);
		TestCase.assertEquals(4, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		
		List<BigBangObject> objects = new ArrayList<BigBangObject>(this.getBBObjectsFromModel(0, 4));
		this.model.buildSatellites(new TreeSet<BigBangObject>(objects.subList(3, 4)), objects.get(0), 0);
		this.model.buildSatellites(new TreeSet<BigBangObject>(objects.subList(2, 3)), objects.get(1), 0);
		TestCase.assertEquals(2, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		TestCase.assertTrue(objects.get(3).getParent() == objects.get(0));
		TestCase.assertTrue(objects.get(2).getParent() == objects.get(1));
		
		//make transformation that copies and transforms first anchor
		TransformationProperties properties = new TransformationProperties(new TreeSet<BigBangObject>(objects.subList(0, 1)), null, Arrays.asList(this.nodePaths), true, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{1,1});
		this.model.translateObjects(properties);
		TestCase.assertEquals(5, this.model.getTransformationGraph().getEdgeCount());
		//there should now be three anchors
		TestCase.assertEquals(3, ((PowerDenotator)this.model.getComposition()).getFactorCount());
		//there should now be a total of six objects
		TestCase.assertEquals(6, this.model.getObjects().getAllObjects().size());
		//second anchor should now be in third place
		TestCase.assertEquals(new DenotatorPath(this.objects.SOUND_SCORE_FORM, new int[]{2}), objects.get(1).getTopDenotatorPathAt(null));
	}
	
	public void testModifyWallpaper() {
		//test modifying transformation so that motif displaced. other wallpaper tests in DenotatorManagerTests
		this.model.setOrAddComposition(this.objects.flatSoundScore);
		TreeSet<BigBangObject> objects = this.getBBObjectsFromModel(0, 2);
		this.model.addWallpaperDimension(objects, 0, 5);
		
		TransformationProperties properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{1,1});
		this.model.translateObjects(properties);
		TestCase.assertEquals(3, this.model.getTransformationGraph().getEdgeCount());
		TestCase.assertEquals(13, this.model.getObjects().getAllObjects().size());
		
		//modify translation
		properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), false, false);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{-1,-1});
		this.model.translateObjects(properties);
		TestCase.assertEquals(3, this.model.getTransformationGraph().getEdgeCount());
		TestCase.assertEquals(13, this.model.getObjects().getAllObjects().size());
	}
	
	public void testRepeatedAddAndTransformWithLimit() {
		//add an integer
		this.model.setOrAddComposition(this.objects.createInteger(3));
		TestCase.assertEquals(1, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(3, ((SimpleDenotator)this.model.getComposition()).getInteger());
		BigBangObject addedObject = this.model.getObjects().getObjectsAt(null).iterator().next();
		
		//add with addEdit, should be replaced by new object
		int[][] paths = new int[][]{{}};
		double[][] values = new double[][]{{11}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.INTEGER_FORM, paths, values),
				this.createPathsList(null, 1), false);
		TestCase.assertEquals(1, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(11, ((SimpleDenotator)this.model.getComposition()).getInteger());
		//replaced object should be the same as the one added first!!
		TestCase.assertTrue(addedObject == this.model.getObjects().getObjectsAt(null).iterator().next());
		
		//add again, should be replaced by same object
		values = new double[][]{{12}};
		this.model.addObjects(this.createNodePathAndValuesMapList(this.objects.INTEGER_FORM, paths, values),
				this.createPathsList(null, 1), false);
		TestCase.assertEquals(1, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(12, ((SimpleDenotator)this.model.getComposition()).getInteger());
		//replaced object should be the same as the one added first!!
		TestCase.assertTrue(addedObject == this.model.getObjects().getObjectsAt(null).iterator().next());
		
		//transform and see if replaced
		Set<BigBangObject> objects = this.getBBObjectsFromModel(0,1);
		List<TransformationPaths> transformationPaths = Arrays.asList(this.objects.createStandardTransformationPaths(this.objects.INTEGER_FORM, new int[][]{{0},null}));
		TransformationProperties properties = new TransformationProperties(objects, null, transformationPaths, false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{1,0});
		this.model.scaleObjects(properties, new double[]{2,0});
		TestCase.assertEquals(1, this.model.getObjects().getAllObjects().size());
		TestCase.assertEquals(24, ((SimpleDenotator)this.model.getComposition()).getInteger());
		//replaced object should be the same as the one added first!!
		TestCase.assertTrue(addedObject == this.model.getObjects().getObjectsAt(null).iterator().next());
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
	
	private TreeSet<BigBangObject> getBBObjectsFromModel(int fromIndex, int toIndex) {
		List<BigBangObject> objectList = new ArrayList<BigBangObject>(this.model.getObjects().getAllObjects());
		return new TreeSet<BigBangObject>(objectList.subList(fromIndex, toIndex));
	}
	
	private CompositionState getCompStateAt(int index) {
		return this.model.getTransformationGraph().getCompositionStateAt(index);
	}

}

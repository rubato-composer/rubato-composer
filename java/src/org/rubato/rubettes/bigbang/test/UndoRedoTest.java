package org.rubato.rubettes.bigbang.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.denotators.TransformationPaths;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

public class UndoRedoTest extends TestCase {
	
	private BigBangModel model;
	private TestObjects objects;
	private TransformationPaths nodePaths;
	
	protected void setUp() {
		this.objects = new TestObjects();
		this.model = new BigBangModel();
		this.nodePaths = this.objects.createStandardTransformationPaths(
				this.objects.SOUND_NODE_FORM, new int[][]{{0,0},{0,1}});
	}
	
	public void testUndoRedoAddOperation() {
		//add score
		this.model.setOrAddComposition(this.objects.flatSoundScore);
		this.checkGraphAndResult(2, 1, new double[][]{{0,60,120,1,0},{1,63,116,1,0},{2,60,121,1,1}});
		
		//translate
		this.addOnsetPitchTranslation(-1, 2);
		this.checkGraphAndResult(3, 2, new double[][]{{-1,62,120,1,0},{0,65,116,1,0},{2,60,121,1,1}});
		
		//undo translate
		this.model.undo();
		this.checkGraphAndResult(2, 1, new double[][]{{0,60,120,1,0},{1,63,116,1,0},{2,60,121,1,1}});
		
		//undo add
		this.model.undo();
		this.checkGraphAndResult(1, 0, new double[][]{});
		
		//undo add
		this.model.redo();
		this.checkGraphAndResult(2, 1, new double[][]{{0,60,120,1,0},{1,63,116,1,0},{2,60,121,1,1}});
		
		//redo translate
		this.model.redo();
		this.checkGraphAndResult(3, 2, new double[][]{{-1,62,120,1,0},{0,65,116,1,0},{2,60,121,1,1}});
	}
	
	public void testUndoRedoAddAlternativeOperation() {
		//add score
		this.model.setOrAddComposition(this.objects.flatSoundScore);
		this.checkGraphAndResult(2, 1, new double[][]{{0,60,120,1,0},{1,63,116,1,0},{2,60,121,1,1}});
		
		//translate
		this.addOnsetPitchTranslation(-1, 2);
		this.checkGraphAndResult(3, 2, new double[][]{{-1,62,120,1,0},{0,65,116,1,0},{2,60,121,1,1}});
		
		//translate
		this.addOnsetPitchTranslation(-1, 2);
		this.checkGraphAndResult(4, 3, new double[][]{{-2,64,120,1,0},{-1,67,116,1,0},{2,60,121,1,1}});
		
		//select second state and alternative translate
		this.model.selectCompositionState(this.getCompStateAt(1));
		this.addOnsetPitchTranslation(2, -1);
		this.checkGraphAndResult(5, 4, new double[][]{{2,59,120,1,0},{3,62,116,1,0},{2,60,121,1,1}});
		
		//undo alternative translate
		this.model.undo();
		this.checkGraphAndResult(4, 3, new double[][]{{-2,64,120,1,0},{-1,67,116,1,0},{2,60,121,1,1}});
		
		//undo translate
		this.model.undo();
		this.checkGraphAndResult(3, 2, new double[][]{{-1,62,120,1,0},{0,65,116,1,0},{2,60,121,1,1}});
		
		//redo translate
		this.model.redo();
		this.checkGraphAndResult(4, 3, new double[][]{{-2,64,120,1,0},{-1,67,116,1,0},{2,60,121,1,1}});
		
		//redo alternative translate
		this.model.redo();
		this.checkGraphAndResult(5, 4, new double[][]{{2,59,120,1,0},{3,62,116,1,0},{2,60,121,1,1}});
	}
	
	public void testUndoRedoParallelTransformation() {
		//add score
		this.model.setOrAddComposition(this.objects.flatSoundScore);
		this.checkGraphAndResult(2, 1, new double[][]{{0,60,120,1,0},{1,63,116,1,0},{2,60,121,1,1}});
		
		//translate
		this.addOnsetPitchTranslation(-1, 2);
		this.checkGraphAndResult(3, 2, new double[][]{{-1,62,120,1,0},{0,65,116,1,0},{2,60,121,1,1}});
		
		//parallel translate
		this.model.selectOperation(this.model.getTransformationGraph().getLastAddedOperation());
		this.addOnsetPitchTranslation(-1, 2);
		this.checkGraphAndResult(3, 3, new double[][]{{-2,64,120,1,0},{-1,67,116,1,0},{2,60,121,1,1}});
		
		//add successive translate
		this.model.selectOperation(null);
		this.addOnsetPitchTranslation(2, -1);
		this.checkGraphAndResult(4, 4, new double[][]{{0,63,120,1,0},{1,66,116,1,0},{2,60,121,1,1}});
		
		//undo successive translate
		this.model.undo();
		this.checkGraphAndResult(3, 3, new double[][]{{-2,64,120,1,0},{-1,67,116,1,0},{2,60,121,1,1}});
		
		//undo parallel translate
		this.model.undo();
		this.checkGraphAndResult(3, 2, new double[][]{{-1,62,120,1,0},{0,65,116,1,0},{2,60,121,1,1}});
		
		//undo parallel translate
		this.model.redo();
		this.checkGraphAndResult(3, 3, new double[][]{{-2,64,120,1,0},{-1,67,116,1,0},{2,60,121,1,1}});
		
		//redo successive translate
		this.model.redo();
		this.checkGraphAndResult(4, 4, new double[][]{{0,63,120,1,0},{1,66,116,1,0},{2,60,121,1,1}});
	}
	
	public void testUndoRedoInsertOperation() {
		//add score
		this.model.setOrAddComposition(this.objects.flatSoundScore);
		this.checkGraphAndResult(2, 1, new double[][]{{0,60,120,1,0},{1,63,116,1,0},{2,60,121,1,1}});
		
		//scale
		this.addOnsetPitchScaling(2, 2);
		this.checkGraphAndResult(3, 2, new double[][]{{0,120,120,1,0},{2,126,116,1,0},{2,60,121,1,1}});
		
		//insert translation
		this.model.setInsertionState(1);
		this.addOnsetPitchTranslation(1, -2);
		this.checkGraphAndResult(4, 3, new double[][]{{2,116,120,1,0},{4,122,116,1,0},{2,60,121,1,1}});
		
		//add successive translate
		this.model.setInsertionState(null);
		this.addOnsetPitchTranslation(2, -1);
		this.checkGraphAndResult(5, 4, new double[][]{{4,115,120,1,0},{6,121,116,1,0},{2,60,121,1,1}});
		
		//undo successive translate
		this.model.undo();
		this.checkGraphAndResult(4, 3, new double[][]{{2,116,120,1,0},{4,122,116,1,0},{2,60,121,1,1}});
		
		//undo insert translation
		this.model.undo();
		this.checkGraphAndResult(3, 2, new double[][]{{0,120,120,1,0},{2,126,116,1,0},{2,60,121,1,1}});
		
		//redo insert translation
		this.model.redo();
		this.checkGraphAndResult(4, 3, new double[][]{{2,116,120,1,0},{4,122,116,1,0},{2,60,121,1,1}});
		
		//redo successive translate
		this.model.redo();
		this.checkGraphAndResult(5, 4, new double[][]{{4,115,120,1,0},{6,121,116,1,0},{2,60,121,1,1}});
	}
	
	public void testUndoRedoRemoveOperation() {
		//add score and scale
		this.model.setOrAddComposition(this.objects.flatSoundScore);
		this.checkGraphAndResult(2, 1, new double[][]{{0,60,120,1,0},{1,63,116,1,0},{2,60,121,1,1}});
		
		//scale
		this.addOnsetPitchScaling(2, 2);
		AbstractOperation scaling = this.model.getTransformationGraph().getLastAddedOperation();
		this.checkGraphAndResult(3, 2, new double[][]{{0,120,120,1,0},{2,126,116,1,0},{2,60,121,1,1}});
		
		//insert translation
		this.model.setInsertionState(1);
		this.addOnsetPitchTranslation(1, -2);
		AbstractOperation insertedTranslation = this.model.getTransformationGraph().getLastAddedOperation();
		this.checkGraphAndResult(4, 3, new double[][]{{2,116,120,1,0},{4,122,116,1,0},{2,60,121,1,1}});
		
		//add successive translate
		this.model.setInsertionState(null);
		this.addOnsetPitchTranslation(2, -1);
		this.checkGraphAndResult(5, 4, new double[][]{{4,115,120,1,0},{6,121,116,1,0},{2,60,121,1,1}});
		
		//remove scaling
		this.model.removeOperation(scaling);
		this.checkGraphAndResult(4, 3, new double[][]{{3,57,120,1,0},{4,60,116,1,0},{2,60,121,1,1}});
		
		//remove inserted translation
		this.model.removeOperation(insertedTranslation);
		this.checkGraphAndResult(3, 2, new double[][]{{2,59,120,1,0},{3,62,116,1,0},{2,60,121,1,1}});
		
		//undo remove inserted translation
		this.model.undo();
		this.checkGraphAndResult(4, 3, new double[][]{{3,57,120,1,0},{4,60,116,1,0},{2,60,121,1,1}});
		
		//undo remove scaling
		this.model.undo();
		this.checkGraphAndResult(5, 4, new double[][]{{4,115,120,1,0},{6,121,116,1,0},{2,60,121,1,1}});
		
		//redo remove scaling
		this.model.redo();
		this.checkGraphAndResult(4, 3, new double[][]{{3,57,120,1,0},{4,60,116,1,0},{2,60,121,1,1}});
		
		//redo remove insert translation
		this.model.redo();
		this.checkGraphAndResult(3, 2, new double[][]{{2,59,120,1,0},{3,62,116,1,0},{2,60,121,1,1}});
	}
	
	private void checkGraphAndResult(int expectedStates, int expectedOperations, double[][] expectedValues) {
		TestCase.assertEquals(expectedStates, this.model.getTransformationGraph().getVertexCount());
		TestCase.assertEquals(expectedOperations, this.model.getTransformationGraph().getEdgeCount());
		Denotator expectedResult = this.objects.generator.createFlatSoundScore(expectedValues);
		TestCase.assertEquals(expectedResult, this.model.getComposition());
	}
	
	//adds a translation to first two objects
	private void addOnsetPitchTranslation(int onsetDiff, int pitchDiff) {
		Set<BigBangObject> objects = this.getBBObjectsFromModel(0, 2);
		TransformationProperties properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{onsetDiff, pitchDiff});
		this.model.translateObjects(properties);
	}
	
	//adds a translation to first two objects
	private void addOnsetPitchScaling(int onsetFactor, int pitchFactor) {
		Set<BigBangObject> objects = this.getBBObjectsFromModel(0, 2);
		TransformationProperties properties = new TransformationProperties(objects, null, Arrays.asList(this.nodePaths), false, true);
		properties.setCenter(new double[]{0,0});
		properties.setEndPoint(new double[]{onsetFactor, pitchFactor});
		this.model.scaleObjects(properties, new double[]{onsetFactor, pitchFactor});
	}
	
	private TreeSet<BigBangObject> getBBObjectsFromModel(int fromIndex, int toIndex) {
		List<BigBangObject> objectList = new ArrayList<BigBangObject>(this.model.getObjects().getAllObjects());
		return new TreeSet<BigBangObject>(objectList.subList(fromIndex, toIndex));
	}
	
	private CompositionState getCompStateAt(int index) {
		return this.model.getTransformationGraph().getCompositionStateAt(index);
	}

}

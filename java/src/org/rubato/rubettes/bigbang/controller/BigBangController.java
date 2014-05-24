package org.rubato.rubettes.bigbang.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangController extends Controller {
	
	public static final String NEW_WINDOW = "newWindowAdded";
	public static final String UNDO = "undo";
	public static final String REDO = "redo";
	public static final String GRAPH = "graph";
	public static final String OPERATION_MODIFIED = "operationModified";
	public static final String MODIFY_OPERATION = "modifyOperation";
	public static final String SELECT_COMPOSITION_STATE = "selectCompositionState";
	public static final String SELECT_COMPOSITION_STATE_AT = "selectCompositionStateAt";
	public static final String SELECT_PREVIOUS_COMPOSITION_STATE = "selectPreviousCompositionState";
	public static final String SELECT_NEXT_COMPOSITION_STATE = "selectNextCompositionState";
	public static final String DESELECT_COMPOSITION_STATES = "deselectCompositionStates";
	public static final String SELECT_OPERATION = "selectOperation";
	public static final String DESELECT_OPERATIONS = "deselectOperations";
	public static final String INSERTION_STATE = "setInsertionState";
	public static final String REMOVE_OPERATION = "removeOperation";
	public static final String OPERATION_DURATION = "setOperationDuration";
	public static final String TOGGLE_GRAPH_ANIMATION = "toggleGraphAnimation";
	public static final String GRAPH_ANIMATION_POSITION = "setGraphAnimationPosition";
	public static final String SPLIT_OPERATION = "splitOperation";
	
	public static final String LAYERS = "layers";
	public static final String LAYER_NAME = "setLayerName";
	public static final String LAYER_ACTIVE = "setLayerActive";
	public static final String LAYER_AUDIBLE = "setLayerAudible";
	public static final String LAYER_VISIBLE = "setLayerVisible";
	public static final String ADD_TO_LAYER = "addObjectsToLayer";
	public static final String ADD_TO_NEW_LAYER = "addObjectsToNewLayer";
	public static final String MOVE_TO_LAYER = "moveObjectsToLayer";
	public static final String MOVE_TO_NEW_LAYER = "moveObjectsToNewLayer";
	
	public static final String MULTITOUCH = "MultiTouch";
	public static final String INPUT_ACTIVE = "InputActive";
	public static final String FORM = "setForm";
	public static final String COMPOSITION = "Composition";
	public static final String OBJECT_SELECTION = "ObjectSelection";
	public static final String ADD_OBJECTS = "addObjects";
	public static final String UNADD_OBJECTS = "unAddObjects";
	public static final String DELETE_OBJECTS = "deleteObjects";
	
	public static final String TRANSLATE_OBJECTS = "translateObjects";
	public static final String ROTATE_OBJECTS = "rotateObjects";
	public static final String SCALE_OBJECTS = "scaleObjects";
	public static final String REFLECT_OBJECTS = "reflectObjects";
	public static final String SHEAR_OBJECTS = "shearObjects";
	public static final String AFFINE_TRANSFORM_OBJECTS = "affineTransformObjects";
	
	public static final String SHAPE_OBJECTS = "shapeObjects";
	public static final String BUILD_SATELLITES = "buildSatellites";
	public static final String FLATTEN_OBJECTS = "flattenObjects";
	
	public static final String ADD_DIMENSION = "addWallpaperDimension";
	public static final String END_WALLPAPER = "endWallpaper";
	
	public static final String ADD_ALTERATION = "addAlteration";
	public static final String SET_ALTERATION_COMPOSITION = "setAlterationComposition";
	public static final String FIRE_ALTERATION_COMPOSITION = "fireAlterationComposition";
	public static final String ALTERATION_COORDINATES = "setAlterationCoordinates";
	public static final String ALTERATION_START_DEGREE = "setAlterationStartDegree";
	public static final String ALTERATION_END_DEGREE = "setAlterationEndDegree";
	public static final String ALTERATION_DEGREES_DIMENSION = "setAlterationDegreesDimension";
	
	public void newWindowAdded() {
		this.callModelMethod(BigBangController.NEW_WINDOW);
	}
	
	public void undo() {
		this.callModelMethod(BigBangController.UNDO);
	}
	
	public void redo() {
		this.callModelMethod(BigBangController.REDO);
	}
	
	public void operationModified() {
		this.callModelMethod(BigBangController.OPERATION_MODIFIED);
	}
	
	public void modifyOperation(int operationIndex, double ratio) {
		this.callModelMethod(BigBangController.MODIFY_OPERATION, operationIndex, ratio);
	}
	
	public void selectPreviousCompositionState() {
		this.callModelMethod(BigBangController.SELECT_PREVIOUS_COMPOSITION_STATE);
	}
	
	public void selectNextCompositionState() {
		this.callModelMethod(BigBangController.SELECT_NEXT_COMPOSITION_STATE);
	}
	
	public void selectCompositionState(CompositionState state) {
		this.callModelMethod(BigBangController.SELECT_COMPOSITION_STATE, state);
	}
	
	public void selectCompositionState(Integer stateIndex) {
		this.callModelMethod(BigBangController.SELECT_COMPOSITION_STATE_AT, stateIndex);
	}
	
	public void deselectCompositionStates() {
		this.callModelMethod(BigBangController.DESELECT_COMPOSITION_STATES);
	}
	
	public void selectOperation(AbstractOperation edge) {
		this.callModelMethod(BigBangController.SELECT_OPERATION, edge);
	}
	
	public void deselectOperations() {
		this.callModelMethod(BigBangController.DESELECT_OPERATIONS);
	}
	
	public void removeOperationFromGraph(AbstractOperation operation) {
		this.callModelMethod(BigBangController.REMOVE_OPERATION, operation);
	}
	
	public void insertOperation(Integer stateIndex) {
		this.callModelMethod(BigBangController.INSERTION_STATE, stateIndex);
	}
	
	public void setOperationDuration(AbstractOperation operation, Double duration) {
		this.callModelMethod(BigBangController.OPERATION_DURATION, operation, duration);
	}
	
	public void toggleGraphAnimation() {
		this.callModelMethod(BigBangController.TOGGLE_GRAPH_ANIMATION);
	}
	
	public void setAnimationPosition(Double position) {
		this.callModelMethod(BigBangController.GRAPH_ANIMATION_POSITION, position);
	}
	
	public void splitOperation() {
		this.callModelMethod(BigBangController.SPLIT_OPERATION);
	}
	
	public void setLayerName(int index, String name) {
		this.callModelMethod(BigBangController.LAYER_NAME, index, name);
	}
	
	public void setLayerActive(int index, boolean active) {
		this.callModelMethod(BigBangController.LAYER_ACTIVE, index, active);
	}
	
	public void setLayerAudible(int index, boolean audible) {
		this.callModelMethod(BigBangController.LAYER_AUDIBLE, index, audible);
	}
	
	public void setLayerVisible(int index, boolean visible) {
		this.callModelMethod(BigBangController.LAYER_VISIBLE, index, visible);
	}
	
	public void addObjectsToLayer(int layerIndex, Set<BigBangObject> objects) {
		this.callModelMethod(BigBangController.ADD_TO_LAYER, layerIndex, objects);
	}
	
	public void addObjectsToNewLayer(Set<BigBangObject> objects) {
		this.callModelMethod(BigBangController.ADD_TO_NEW_LAYER, objects);
	}
	
	public void moveObjectsToLayer(int layerIndex, Set<BigBangObject> objects) {
		this.callModelMethod(BigBangController.MOVE_TO_LAYER, layerIndex, objects);
	}
	
	public void moveObjectsToNewLayer(Set<BigBangObject> objects) {
		this.callModelMethod(BigBangController.MOVE_TO_NEW_LAYER, objects);
	}
	
	public void changeInputActive(boolean inputActive) {
		this.setModelProperty(BigBangController.INPUT_ACTIVE, inputActive);
	}
	
	public void setForm(Form form) {
		this.callModelMethod(BigBangController.FORM, form);
	}
	
	public void setMultiTouch(boolean multiTouch) {
		this.setModelProperty(BigBangController.MULTITOUCH, multiTouch);
	}
	
	public void addObjects(List<Map<DenotatorPath,Double>> denotatorValues, List<DenotatorPath> powersetPaths, boolean inPreviewMode) {
		this.callModelMethod(BigBangController.ADD_OBJECTS, denotatorValues, powersetPaths, inPreviewMode);
	}
	
	public void unAddObjects(Set<Map<DenotatorPath,Double>> pathsWithValues) {
		this.callModelMethod(BigBangController.UNADD_OBJECTS, pathsWithValues);
	}
	
	public void deleteObjects(Set<BigBangObject> objects) {
		this.callModelMethod(BigBangController.DELETE_OBJECTS, objects);
	}
	
	public void translateObjects(TransformationProperties properties) {
		this.callModelMethod(BigBangController.TRANSLATE_OBJECTS, properties);
	}
	
	public void rotateObjects(TransformationProperties properties, double[] startingPoint, double angle) {
		this.callModelMethod(BigBangController.ROTATE_OBJECTS, properties, startingPoint, angle);
	}
	
	public void scaleObjects(TransformationProperties properties, double[] scaleFactors) {
		this.callModelMethod(BigBangController.SCALE_OBJECTS, properties, scaleFactors);
	}
	
	public void reflectObjects(TransformationProperties properties, double[] reflectionVector) {
		this.callModelMethod(BigBangController.REFLECT_OBJECTS, properties, reflectionVector);
	}
	
	public void shearObjects(TransformationProperties properties, double[] shearingFactors) {
		this.callModelMethod(BigBangController.SHEAR_OBJECTS, properties, shearingFactors);
	}
	
	public void shapeObjects(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		this.callModelMethod(BigBangController.SHAPE_OBJECTS, properties, shapingLocations);
	}
	
	public void affineTransformObjects(TransformationProperties properties, double[] shift, RMatrix transform) {
		this.callModelMethod(BigBangController.AFFINE_TRANSFORM_OBJECTS, properties, shift, transform);
	}
	
	public void buildSatellites(Set<BigBangObject> objects, BigBangObject anchor, int powersetIndex) {
		this.callModelMethod(BigBangController.BUILD_SATELLITES, objects, anchor, powersetIndex);
	}
	
	public void flattenObjects(Set<BigBangObject> satellites) {
		this.callModelMethod(BigBangController.FLATTEN_OBJECTS, satellites);
	}
	
	public void addWallpaperDimension(Set<BigBangObject> objects, int rangeFrom, int rangeTo) {
		this.callModelMethod(BigBangController.ADD_DIMENSION, objects, rangeFrom, rangeTo);
	}
	
	public void endWallpaper() {
		this.callModelMethod(BigBangController.END_WALLPAPER);
	}
	
	public void addAlteration(DenotatorPath degreesDimensionPath) {
		this.callModelMethod(BigBangController.ADD_ALTERATION, degreesDimensionPath);
	}
	
	public void setAlterationComposition(int index, Set<DenotatorPath> nodePaths) {
		this.callModelMethod(BigBangController.SET_ALTERATION_COMPOSITION, nodePaths, index);
	}
	
	public void fireAlterationComposition(int index) {
		this.callModelMethod(BigBangController.FIRE_ALTERATION_COMPOSITION, index);
	}
	
	public void setAlterationCoordinates(List<Integer> coordinates) {
		this.callModelMethod(BigBangController.ALTERATION_COORDINATES, coordinates);
	}
	
	public void changeAlterationStartDegree(double value) {
		this.callModelMethod(BigBangController.ALTERATION_START_DEGREE, value);
	}
	
	public void changeAlterationEndDegree(double value) {
		this.callModelMethod(BigBangController.ALTERATION_END_DEGREE, value);
	}
	
	public void setAlterationDegreesDimension(DenotatorPath path) {
		this.callModelMethod(BigBangController.ALTERATION_DEGREES_DIMENSION, path);
	}

}

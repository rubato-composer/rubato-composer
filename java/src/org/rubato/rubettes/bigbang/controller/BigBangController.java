package org.rubato.rubettes.bigbang.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangController extends Controller {
	
	public static final String NEW_WINDOW = "newWindowAdded";
	public static final String UNDO = "undo";
	public static final String REDO = "redo";
	public static final String GRAPH = "graph";
	public static final String MODIFIED_OPERATION = "modifiedOperation";
	public static final String MODIFY_OPERATION = "modifyOperation";
	public static final String SELECT_COMPOSITION_STATE = "selectCompositionState";
	public static final String DESELECT_COMPOSITION_STATES = "deselectCompositionStates";
	public static final String INSERT_OPERATION = "insertOperation";
	public static final String REMOVE_OPERATION = "removeOperation";
	public static final String TOGGLE_GRAPH_ANIMATION = "toggleGraphAnimation";
	
	public static final String MULTITOUCH = "MultiTouch";
	public static final String INPUT_ACTIVE = "InputActive";
	public static final String FORM = "setForm";
	public static final String COMPOSITION = "Composition";
	public static final String ADD_OBJECTS = "addObjects";
	public static final String DELETE_OBJECTS = "deleteObjects";
	public static final String COPY_OBJECTS = "copyObjects";
	public static final String MOVE_OBJECTS = "moveObjects";
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
	
	public void newWindowAdded() {
		this.callModelMethod(BigBangController.NEW_WINDOW);
	}
	
	public void undo() {
		this.callModelMethod(BigBangController.UNDO);
	}
	
	public void redo() {
		this.callModelMethod(BigBangController.REDO);
	}
	
	public void modifiedOperation(boolean inPreviewMode) {
		this.callModelMethod(BigBangController.MODIFIED_OPERATION, inPreviewMode);
	}
	
	public void modifyOperation(int operationIndex, double ratio) {
		this.callModelMethod(BigBangController.MODIFY_OPERATION, operationIndex, ratio);
	}
	
	public void selectCompositionState(Integer vertex) {
		this.callModelMethod(BigBangController.SELECT_COMPOSITION_STATE, vertex);
	}
	
	public void deselectCompositionStates() {
		this.callModelMethod(BigBangController.DESELECT_COMPOSITION_STATES);
	}
	
	public void removeOperationFromGraph(AbstractOperationEdit operation) {
		this.callModelMethod(BigBangController.REMOVE_OPERATION, operation);
	}
	
	public void insertOperation(Integer vertex) {
		this.callModelMethod(BigBangController.INSERT_OPERATION, vertex);
	}
	
	public void toggleGraphAnimation() {
		this.callModelMethod(BigBangController.TOGGLE_GRAPH_ANIMATION);
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
	
	public void deleteObjects(List<DenotatorPath> objectsPaths) {
		this.callModelMethod(BigBangController.DELETE_OBJECTS, objectsPaths);
	}
	
	public void copyObjects(Set<DenotatorPath> objectsPaths, int layerIndex) {
		this.callModelMethod(BigBangController.COPY_OBJECTS, objectsPaths, layerIndex);
	}
	
	public void moveObjects(Set<DenotatorPath> objectsPaths, int layerIndex) {
		this.callModelMethod(BigBangController.MOVE_OBJECTS, objectsPaths, layerIndex);
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
	
	public void affineTransformObjects(TransformationProperties properties, double[] shift, double angle, double[] scaleFactors) {
		this.callModelMethod(BigBangController.AFFINE_TRANSFORM_OBJECTS, properties, shift, angle, scaleFactors);
	}
	
	public void buildSatellites(List<DenotatorPath> satellitePaths, DenotatorPath anchorNodePath, int powersetIndex) {
		this.callModelMethod(BigBangController.BUILD_SATELLITES, satellitePaths, anchorNodePath, powersetIndex);
	}
	
	public void flattenObjects(List<DenotatorPath> satellitePaths) {
		this.callModelMethod(BigBangController.FLATTEN_OBJECTS, satellitePaths);
	}
	
	public void addWallpaperDimension(SelectedObjectsPaths paths, int rangeFrom, int rangeTo) {
		this.callModelMethod(BigBangController.ADD_DIMENSION, paths, rangeFrom, rangeTo);
	}
	
	public void endWallpaper() {
		this.callModelMethod(BigBangController.END_WALLPAPER);
	}
	
	public void addAlteration() {
		this.callModelMethod(BigBangController.ADD_ALTERATION);
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

}

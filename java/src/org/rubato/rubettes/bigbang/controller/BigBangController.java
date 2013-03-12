package org.rubato.rubettes.bigbang.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.view.model.SelectedPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangController extends Controller {
	
	public static final String NEW_WINDOW = "newWindowAdded";
	public static final String UNDO = "undo";
	public static final String REDO = "redo";
	public static final String POST_EDIT = "postEdit";
	public static final String GRAPH = "graph";
	public static final String DESELECT_TRANSFORMATIONS = "deselectTransformations";
	public static final String MODIFIED_TRANSFORMATION = "modifiedTransformation";
	public static final String PLAY_MODE = "togglePlayMode";
	public static final String TEMPO = "setTempo";
	public static final String FM_MODEL = "FMModel";
	public static final String WAVEFORM = "Waveform";
	public static final String MULTITOUCH = "MultiTouch";
	public static final String INPUT_ACTIVE = "InputActive";
	public static final String FORM = "setForm";
	public static final String COMPOSITION = "Composition";
	public static final String PREVIEW = "Preview";
	public static final String ADD_OBJECT = "addObject";
	public static final String DELETE_NOTES = "deleteNotes";
	public static final String COPY_NOTES = "copyNotes";
	public static final String MOVE_NOTES = "moveNotes";
	public static final String TRANSLATE_NOTES = "translateNotes";
	public static final String ROTATE_NOTES = "rotateNotes";
	public static final String SCALE_NOTES = "scaleNotes";
	public static final String REFLECT_NOTES = "reflectNotes";
	public static final String SHEAR_NOTES = "shearNotes";
	public static final String AFFINE_TRANSFORM_NOTES = "affineTransformNotes";
	public static final String SHAPE_NOTES = "shapeNotes";
	public static final String SATELLITE_NOTES = "buildSatellites";
	public static final String FLATTEN_NOTES = "flattenNotes";
	public static final String ADD_AS_MODULATOR_NOTES = "buildModulators";
	public static final String REMOVE_NOTES_FROM_CARRIER = "removeNotesFromCarrier";
	
	public static final String START_WALLPAPER = "startWallpaper";
	public static final String ADD_DIMENSION = "addWallpaperDimension";
	public static final String UPDATE_WALLPAPER = "updateWallpaper";
	public static final String END_WALLPAPER = "endWallpaper";
	
	public static final String TOGGLE_ALTERATION_MODE = "toggleAlterationMode";
	public static final String ENTER_ALTERATION_MODE = "enterAlterationMode";
	public static final String EXIT_ALTERATION_MODE = "exitAlterationMode";
	public static final String SET_ALTERATION_COMPOSITION = "setAlterationComposition";
	public static final String FIRE_ALTERATION_COMPOSITION = "fireAlterationComposition";
	public static final String ALTERATION_COORDINATES = "setAlterationCoordinates";
	public static final String ALTERATION_START_DEGREE = "setAlterationStartDegree";
	public static final String ALTERATION_END_DEGREE = "setAlterationEndDegree";
	public static final String END_ALTERATION = "endAlteration";
	
	public void newWindowAdded(SelectedPaths paths) {
		this.callModelMethod(BigBangController.NEW_WINDOW, paths);
	}
	
	public void undo() {
		this.callModelMethod(BigBangController.UNDO);
	}
	
	public void redo() {
		this.callModelMethod(BigBangController.REDO);
	}
	
	public void deselectTransformations() {
		this.callModelMethod(BigBangController.DESELECT_TRANSFORMATIONS);
	}
	
	public void modifiedTransformation(boolean inPreviewMode) {
		this.callModelMethod(BigBangController.MODIFIED_TRANSFORMATION, inPreviewMode);
	}
	
	public void changeInputActive(boolean inputActive) {
		this.setModelProperty(BigBangController.INPUT_ACTIVE, inputActive);
	}
	
	public void setForm(Form form) {
		this.callModelMethod(BigBangController.FORM, form);
	}
	
	public void togglePlayMode() {
		this.callModelMethod(BigBangController.PLAY_MODE);
	}
	
	public void setTempo(int tempo) {
		this.callModelMethod(BigBangController.TEMPO, tempo);
	}
	
	public void changeFMModel(Object fmModel) {
		this.setModelProperty(BigBangController.FM_MODEL, fmModel);
	}
	
	public void changeWaveform(Object waveform) {
		this.setModelProperty(BigBangController.WAVEFORM, waveform);
	}
	
	public void setMultiTouch(boolean multiTouch) {
		this.setModelProperty(BigBangController.MULTITOUCH, multiTouch);
	}
	
	public void addObject(Map<DenotatorPath,Double> denotatorValues) {
		this.callModelMethod(BigBangController.ADD_OBJECT, denotatorValues);
	}
	
	public void deleteNotes(List<DenotatorPath> nodePaths) {
		this.callModelMethod(BigBangController.DELETE_NOTES, nodePaths);
	}
	
	public void copyNotes(Set<DenotatorPath> nodePaths, int layerIndex) {
		this.callModelMethod(BigBangController.COPY_NOTES, nodePaths, layerIndex);
	}
	
	public void moveNotes(Set<DenotatorPath> nodePaths, int layerIndex) {
		this.callModelMethod(BigBangController.MOVE_NOTES, nodePaths, layerIndex);
	}
	
	public void translateNotes(TransformationProperties properties) {
		this.callModelMethod(BigBangController.TRANSLATE_NOTES, properties);
	}
	
	public void rotateNotes(TransformationProperties properties, double angle) {
		this.callModelMethod(BigBangController.ROTATE_NOTES, properties, angle);
	}
	
	public void scaleNotes(TransformationProperties properties, double[] scaleFactors) {
		this.callModelMethod(BigBangController.SCALE_NOTES, properties, scaleFactors);
	}
	
	public void reflectNotes(TransformationProperties properties, double[] reflectionVector) {
		this.callModelMethod(BigBangController.REFLECT_NOTES, properties, reflectionVector);
	}
	
	public void shearNotes(TransformationProperties properties, double[] shearingFactors) {
		this.callModelMethod(BigBangController.SHEAR_NOTES, properties, shearingFactors);
	}
	
	public void shapeNotes(TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		this.callModelMethod(BigBangController.SHAPE_NOTES, properties, shapingLocations);
	}
	
	public void affineTransformNotes(TransformationProperties properties, double[] shift, double angle, double[] scaleFactors) {
		this.callModelMethod(BigBangController.AFFINE_TRANSFORM_NOTES, properties, shift, angle, scaleFactors);
	}
	
	public void buildSatellites(Set<DenotatorPath> satelliteNodePaths, DenotatorPath anchorNodePath) {
		this.callModelMethod(BigBangController.SATELLITE_NOTES, satelliteNodePaths, anchorNodePath);
	}
	
	public void flattenNotes(Set<DenotatorPath> satelliteNodePaths) {
		this.callModelMethod(BigBangController.FLATTEN_NOTES, satelliteNodePaths);
	}
	
	public void addAsModulators(Set<DenotatorPath> modulatorNodePaths, DenotatorPath carrierNodePath) {
		this.callModelMethod(BigBangController.ADD_AS_MODULATOR_NOTES, modulatorNodePaths, carrierNodePath);
	}
	
	public void removeNotesFromCarrier(Set<DenotatorPath> modulatorNodePaths) {
		this.callModelMethod(BigBangController.REMOVE_NOTES_FROM_CARRIER, modulatorNodePaths);
	}
	
	public void startWallpaper(List<DenotatorPath> nodePaths) {
		this.callModelMethod(BigBangController.START_WALLPAPER, nodePaths);
	}
	
	public void addWallpaperDimension(int rangeFrom, int rangeTo) {
		this.callModelMethod(BigBangController.ADD_DIMENSION, rangeFrom, rangeTo);
	}
	
	public void updateWallpaper(List<Integer> ranges) {
		this.callModelMethod(BigBangController.UPDATE_WALLPAPER, ranges);
	}
	
	public void endWallpaper() {
		this.callModelMethod(BigBangController.END_WALLPAPER);
	}
	
	public void toggleAlterationMode() {
		this.callModelMethod(BigBangController.TOGGLE_ALTERATION_MODE);
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
	
	public void endAlteration() {
		this.callModelMethod(BigBangController.END_ALTERATION);
	}

}

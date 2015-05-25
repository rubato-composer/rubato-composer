package org.rubato.rubettes.bigbang.view.controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.controller.Controller;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;
import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.ZoomChange;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.util.Dimension;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.PointND;

public class ViewController extends Controller {
	
	//modes
	public static final String DISPLAY_MODE = "setDisplayMode";
	
	//gui
	public static final String ADD_WINDOW = "addNewWindow";
	public static final String MAIN_OPTIONS_VISIBLE = "toggleMainOptionsVisible";
	public static final String VIEW_PARAMETERS_VISIBLE = "toggleViewParametersVisible";
	public static final String SHOW_WINDOW_PREFERENCES = "showWindowPreferences";
	
	//view manipulation
	public static final String VIEW_PARAMETERS = "ViewParameters";
	public static final String SELECTED_VIEW_PARAMETERS = "SelectedViewParameters";
	public static final String MANUAL_DENOTATOR_LIMITS = "setManualDenotatorLimits";
	public static final String PARAMETER_MIN_MAX = "setParameterMinAndMax";
	public static final String ZOOM_FACTORS = "setZoomFactors";
	public static final String ZOOM_CHANGE = "changeZoomFactors";
	public static final String DISPLAY_POSITION = "changeDisplayPosition";
	public static final String CENTER_VIEW = "centerView";
	public static final String SATELLITES_CONNECTED = "SatellitesConnected";
	public static final String LAYERS = "changeLayerState";
	
	//general functionality
	public static final String INPUT_ACTIVE = "InputActive";
	public static final String UNDO = "undo";
	public static final String REDO = "redo";
	public static final String SELECT_OPERATION = "selectOperation";
	public static final String DESELECT_OPERATIONS = "deselectOperations";
	public static final String SELECT_COMPOSITION_STATE = "selectCompositionState";
	public static final String SELECT_PREVIOUS_COMPOSITION_STATE = "selectPreviousCompositionState";
	public static final String SELECT_NEXT_COMPOSITION_STATE = "selectNextCompositionState";
	public static final String DESELECT_COMPOSITION_STATES = "deselectCompositionStates";
	
	//score manipulation - display
	public static final String FORM = "Form";
	public static final String FACTS_VIEW_CONTENTS = "FactsViewContents";
	public static final String DISPLAY_OBJECTS = "DisplayObjects";
	public static final String OBJECT_SELECTION = "toggleObjectSelection";
	public static final String ANCHOR_OBJECT_SELECTION = "toggleAnchorObjectSelection";
	public static final String SELECT_OBJECTS = "selectObjects";
	public static final String DISPLAY_TOOL = "setDisplayTool";
	public static final String CLEAR_DISPLAY_TOOL = "clearDisplayTool";
	
	//score manipulation - denotators
	public static final String STANDARD_DENOTATOR_VALUES = "setStandardDenotatorValue";
	public static final String ACTIVE_OBJECT = "setActiveObject";
	public static final String ACTIVE_COLIMIT_COORDINATE = "setActiveColimitCoordinate";
	public static final String MAX_SATELLITE_LEVEL = "setSatelliteLevels";
	public static final String ACTIVE_SATELLITE_LEVEL = "setActiveSatelliteLevel";
	public static final String ADD_OBJECTS = "addObjects";
	public static final String UNADD_OBJECTS = "unAddObjects";
	public static final String DELETE_OBJECTS = "deleteSelectedObjects";
	
	public static final String ADD_TO_LAYER = "addSelectedObjectsToLayer";
	public static final String ADD_TO_NEW_LAYER = "addSelectedObjectsToNewLayer";
	public static final String MOVE_TO_LAYER = "moveSelectedObjectsToLayer";
	public static final String MOVE_TO_NEW_LAYER = "moveSelectedObjectsToNewLayer";
	public static final String LAYER_SELECTED = "setObjectsOnLayerSelected";
	
	public static final String SATELLITE_OBJECTS = "addSelectedObjectsAsSatellitesTo";
	public static final String FLATTEN_OBJECTS = "flattenSelectedObjects";
	
	//score manipulation - transformations
	public static final String TRANSLATE_OBJECTS = "translateSelectedObjects";
	public static final String ROTATE_OBJECTS = "rotateSelectedObjects";
	public static final String SCALE_OBJECTS = "scaleSelectedObjects";
	public static final String REFLECT_OBJECTS = "reflectSelectedObjects";
	public static final String SHEAR_OBJECTS = "shearSelectedObjects";
	public static final String SHAPE_OBJECTS = "shapeSelectedObjects";
	public static final String AFFINE_TRANSFORM_OBJECTS = "affineTransformSelectedObjects";
	public static final String MODIFY_OPERATION = "modifyOperation";
	public static final String MODIFY_CENTER_OF_SELECTED_TRANSFORMATION = "modifyCenterOfSelectedTransformation";
	public static final String MODIFY_ENDPOINT_OF_SELECTED_TRANSFORMATION = "modifyEndPointOfSelectedTransformation";
	public static final String MODIFY_SELECTED_TRANSFORMATION = "modifySelectedTransformation";
	public static final String MODIFY_ROTATION = "modifyRotationAngle";
	
	//score manipulation - wallpaper
	public static final String ADD_WP_DIMENSION = "addWallpaperDimension";
	public static final String MODIFY_WALLPAPER_RANGE = "modifyWallpaperRange";
	
	//score manipulation - alteration
	public static final String ALTERATION = "addAlteration";
	public static final String ALTERATION_COMPOSITION = "setAlterationComposition";
	public static final String ALTERATION_COORDINATES = "setAlterationCoordinates";
	
	//playback feature
	public static final String PLAY_MODE = "togglePlayMode";
	public static final String RECORD_MODE = "toggleRecordMode";
	public static final String SYNTH_ACTIVE = "setSynthActive";
	public static final String MIDI_ACTIVE = "setMidiActive";
	public static final String MIDI_IN = "setMidiIn";
	public static final String MIDI_OUT = "setMidiOut";
	
	public static final String IS_LOOPING = "setIsLooping";
	public static final String TEMPO = "setTempo";
	public static final String PLAYBACK_POSITION = "setPlaybackPosition";
	public static final String WAVEFORM = "Waveform";
	public static final String PRESS_MIDI_KEY = "pressMidiKey";
	public static final String RELEASE_MIDI_KEY = "releaseMidiKey";
	public static final String CHANGE_OCTAVE = "changeOctave";
	public static final String CHANGE_VELOCITY = "changeVelocity";
	public static final String CHANGE_RATE_MODIFIER = "changeRateModifier";
	
	
	public void changeDisplayMode(DisplayModeAdapter newMode) {
		this.callModelMethod(ViewController.DISPLAY_MODE, newMode);
	}
	
	public void addNewWindow() {
		this.callModelMethod(ViewController.ADD_WINDOW);
	}
	
	public void toggleMainOptionsVisible() {
		this.callModelMethod(ViewController.MAIN_OPTIONS_VISIBLE);
	}
	
	public void changeLayerState(int layerIndex) {
		this.callModelMethod(ViewController.LAYERS, layerIndex);
	}
	
	public void toggleViewParametersVisible() {
		this.callModelMethod(ViewController.VIEW_PARAMETERS_VISIBLE);
	}
	
	public void changeViewParameters(int[] newViewParameters) {
		this.setModelProperty(ViewController.SELECTED_VIEW_PARAMETERS, newViewParameters);
	}
	
	public void setStandardDenotatorValue(int index, double value) {
		this.callModelMethod(ViewController.STANDARD_DENOTATOR_VALUES, index, value);
	}
	
	public void setActiveObject(int objectIndex) {
		this.callModelMethod(ViewController.ACTIVE_OBJECT, objectIndex);
	}
	
	public void setActiveColimitCoordinate(int colimitIndex, int coordinateIndex) {
		this.callModelMethod(ViewController.ACTIVE_COLIMIT_COORDINATE, colimitIndex, coordinateIndex);
	}
	
	public void setActiveSatelliteLevel(int satelliteLevel) {
		this.callModelMethod(ViewController.ACTIVE_SATELLITE_LEVEL, satelliteLevel);
	}
	
	public void showWindowPreferences() {
		this.callModelMethod(ViewController.SHOW_WINDOW_PREFERENCES);
	}
	
	public void changeDenotatorMinAndMax(int index, boolean manual, double min, double max) {
		this.callModelMethod(ViewController.MANUAL_DENOTATOR_LIMITS, index, manual, min, max);
	}
	
	public void changeParameterMinAndMax(int index, boolean relative, double min, double max, boolean cyclic) {
		this.callModelMethod(ViewController.PARAMETER_MIN_MAX, index, relative, min, max, cyclic);
	}
	
	public void changeZoomFactors(ZoomChange zoomChange) {
		this.callModelMethod(ViewController.ZOOM_CHANGE, zoomChange);
	}
	
	public void setZoomFactors(double zoomFactor) {
		this.callModelMethod(ViewController.ZOOM_FACTORS, zoomFactor, zoomFactor);
	}
	
	public void changeDisplayPosition(Dimension difference) {
		this.callModelMethod(ViewController.DISPLAY_POSITION, difference);
	}
	
	public void centerView() {
		this.callModelMethod(ViewController.CENTER_VIEW);
	}
	
	public void setDisplayPosition(Point center) {
		this.callModelMethod("setDisplayPosition", center);
	}
	
	public void toggleObjectSelection(Point location) {
		this.callModelMethod(ViewController.OBJECT_SELECTION, location);
	}
	
	public void toggleAnchorObjectSelection(Point location) {
		this.callModelMethod(ViewController.ANCHOR_OBJECT_SELECTION, location);
	}
	
	public void selectObjects(SelectionTool tool, boolean stillSelecting) {
		this.callModelMethod(ViewController.SELECT_OBJECTS, tool, stillSelecting);
	}
	
	public void changeDisplayTool(DisplayTool tool) {
		this.callModelMethod(ViewController.DISPLAY_TOOL, tool);
	}
	
	public void clearDisplayTool() {
		this.callModelMethod(ViewController.CLEAR_DISPLAY_TOOL);
	}
	
	public void modifyOperation(int operationNumber, int midiValue) {
		this.callModelMethod(ViewController.MODIFY_OPERATION, operationNumber, midiValue);
	}
	
	public void modifyCenterOfSelectedTransformation(Point2D newCenter) {
		this.callModelMethod(ViewController.MODIFY_CENTER_OF_SELECTED_TRANSFORMATION, newCenter);
	}
	
	public void modifyEndPointOfSelectedTransformation(Point2D newEndPoint) {
		this.callModelMethod(ViewController.MODIFY_ENDPOINT_OF_SELECTED_TRANSFORMATION, newEndPoint);
	}
	
	public void modifySelectedTransformation(double[] newValues) {
		this.callModelMethod(ViewController.MODIFY_SELECTED_TRANSFORMATION, newValues);
	}
	
	public void modifyRotationAngle(Double angle) {
		this.callModelMethod(ViewController.MODIFY_ROTATION, angle);
	}
	
	public void translateSelectedObjects(Point2D center, Point2D endPoint, boolean copyAndTranslate, boolean startNewTransformation) {
		this.callModelMethod(ViewController.TRANSLATE_OBJECTS, center, endPoint, copyAndTranslate, startNewTransformation);
	}
	
	public void rotateSelectedObjects(Point2D center, Point2D startPoint, Point2D endPoint, double angle, boolean copyAndTranslate, boolean startNewTransformation) {
		this.callModelMethod(ViewController.ROTATE_OBJECTS, center, startPoint, endPoint, angle, copyAndTranslate, startNewTransformation);
	}
	
	public void scaleSelectedObjects(Point2D center, Point2D endPoint, double[] scaleFactors, boolean copyAndTranslate, boolean startNewTransformation) {
		this.callModelMethod(ViewController.SCALE_OBJECTS, center, endPoint, scaleFactors, copyAndTranslate, startNewTransformation);
	}
	
	public void reflectSelectedObjects(Point2D center, Point2D endPoint, double[] reflectionVector, boolean copyAndTranslate, boolean startNewTransformation) {
		this.callModelMethod(ViewController.REFLECT_OBJECTS, center, endPoint, reflectionVector, copyAndTranslate, startNewTransformation);
	}
	
	public void shearSelectedObjects(Point2D center, Point2D endPoint, double[] shearingFactors, boolean copyAndTranslate, boolean startNewTransformation) {
		this.callModelMethod(ViewController.SHEAR_OBJECTS, center, endPoint, shearingFactors, copyAndTranslate, startNewTransformation);
	}
	
	public void affineTransformSelectedObjects(Point2D center, Point2D endPoint, double[] shift, RMatrix transform, boolean copyAndTransform, boolean startNewTransformation) {
		this.callModelMethod(ViewController.AFFINE_TRANSFORM_OBJECTS, center, endPoint, shift, transform, copyAndTransform, startNewTransformation);
	}
	
	public void shapeSelectedObjects(TreeMap<Integer,Integer> location, boolean copyAndTransform, boolean startNewTransformation) {
		this.callModelMethod(ViewController.SHAPE_OBJECTS, location, copyAndTransform, startNewTransformation);
	}

	public void addObjects(ArrayList<PointND> locations, boolean inPreviewMode) {
		this.callModelMethod(ViewController.ADD_OBJECTS, locations, inPreviewMode);
	}
	
	public void unAddObjects(ArrayList<PointND> locations) {
		this.callModelMethod(ViewController.UNADD_OBJECTS, locations);
	}
	
	public void deleteSelectedObjects() {
		this.callModelMethod(ViewController.DELETE_OBJECTS);
	}
	
	public void addSelectedObjectsTo(int layerIndex) {
		this.callModelMethod(ViewController.ADD_TO_LAYER, layerIndex);
	}
	
	public void addSelectedObjectsToNewLayer() {
		this.callModelMethod(ViewController.ADD_TO_NEW_LAYER);
	}
	
	public void moveSelectedObjectsTo(int layerIndex) {
		this.callModelMethod(ViewController.MOVE_TO_LAYER, layerIndex);
	}
	
	public void moveSelectedObjectsToNewLayer() {
		this.callModelMethod(ViewController.MOVE_TO_NEW_LAYER);
	}
	
	public void setObjectsOnLayerSelected(int layerIndex, boolean selected) {
		this.callModelMethod(ViewController.LAYER_SELECTED, layerIndex, selected);
	}
	
	public void addSelectedObjectsAsSatellitesTo(DisplayObject parentObject, int powersetIndex) {
		this.callModelMethod(ViewController.SATELLITE_OBJECTS, parentObject, powersetIndex);
	}
	
	public void flattenSelectedObjects() {
		this.callModelMethod(ViewController.FLATTEN_OBJECTS);
	}
	
	public void addWallpaperDimension() {
		this.callModelMethod(ViewController.ADD_WP_DIMENSION);
	}
	
	public void modifyWallpaperRange(boolean rangeTo, int value) {
		this.callModelMethod(ViewController.MODIFY_WALLPAPER_RANGE, rangeTo, value);
	}
	
	public void addAlteration() {
		this.callModelMethod(ViewController.ALTERATION);
	}
	
	public void changeAlterationComposition(int index) {
		this.callModelMethod(ViewController.ALTERATION_COMPOSITION, index);
	}
	
	public void setAlterationCoordinates(List<Integer> coordinates) {
		this.callModelMethod(ViewController.ALTERATION_COORDINATES, coordinates);
	}
	
	public void changeInputActive(boolean inputActive) {
		this.setModelProperty(ViewController.INPUT_ACTIVE, inputActive);
	}
	
	public void undo() {
		this.callModelMethod(ViewController.UNDO);
	}
	
	public void redo() {
		this.callModelMethod(ViewController.REDO);
	}
	
	public void selectOperation(AbstractOperation operation) {
		this.callModelMethod(ViewController.SELECT_OPERATION, operation);
	}
	
	public void deselectOperations() {
		this.callModelMethod(ViewController.DESELECT_OPERATIONS);
	}
	
	public void selectPreviousCompositionState() {
		this.callModelMethod(ViewController.SELECT_PREVIOUS_COMPOSITION_STATE);
	}
	
	public void selectNextCompositionState() {
		this.callModelMethod(ViewController.SELECT_NEXT_COMPOSITION_STATE);
	}
	
	public void selectCompositionState(CompositionState state) {
		this.callModelMethod(ViewController.SELECT_COMPOSITION_STATE, state);
	}
	
	public void selectCompositionState(Integer stateIndex) {
		this.callModelMethod(ViewController.SELECT_COMPOSITION_STATE, stateIndex);
	}
	
	public void deselectCompositionStates() {
		this.callModelMethod(ViewController.DESELECT_COMPOSITION_STATES);
	}
	
	public List<View> getViews() {
		return this.registeredViews;
	}
	
	public void togglePlayMode() {
		this.callModelMethod(ViewController.PLAY_MODE);
	}
	
	public void toggleRecordMode() {
		this.callModelMethod(ViewController.RECORD_MODE);
	}
	
	public void setSynthActive(boolean synthActive) {
		this.callModelMethod(ViewController.SYNTH_ACTIVE, synthActive);
	}
	
	public void setIsLooping(boolean isLooping) {
		this.callModelMethod(ViewController.IS_LOOPING, isLooping);
	}
	
	public void setTempo(int tempo) {
		this.callModelMethod(ViewController.TEMPO, tempo);
	}
	
	public void setPlaybackPosition(Point2D clickPosition) {
		this.callModelMethod(ViewController.PLAYBACK_POSITION, clickPosition);
	}
	
	public void changeWaveform(Object waveform) {
		this.setModelProperty(ViewController.WAVEFORM, waveform);
	}
	
	public void setMidiIn(String inDevice) {
		this.callModelMethod(ViewController.MIDI_IN, inDevice);
	}
	
	public void setMidiOut(String outDevice) {
		this.callModelMethod(ViewController.MIDI_OUT, outDevice);
	}
	
	public void pressMidiKey(Integer channel, Integer pitch, Integer velocity) {
		this.callModelMethod(ViewController.PRESS_MIDI_KEY, channel, pitch, velocity);
	}
	
	public void releaseMidiKey(Integer channel, Integer pitch) {
		this.callModelMethod(ViewController.RELEASE_MIDI_KEY, channel, pitch);
	}
	
	public void changeOctave(boolean up) {
		this.callModelMethod(ViewController.CHANGE_OCTAVE, up);
	}
	
	public void changeVelocity(int velocity) {
		this.callModelMethod(ViewController.CHANGE_VELOCITY, velocity);
	}
	
	public void changeRateModifier(int rateModifier) {
		this.callModelMethod(ViewController.CHANGE_RATE_MODIFIER, rateModifier);
	}

}

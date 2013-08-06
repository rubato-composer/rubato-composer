package org.rubato.rubettes.bigbang.view.model;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.model.Model;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.model.edits.AbstractLocalTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.ReflectionEdit;
import org.rubato.rubettes.bigbang.model.edits.RotationEdit;
import org.rubato.rubettes.bigbang.model.edits.ScalingEdit;
import org.rubato.rubettes.bigbang.model.edits.ShearingEdit;
import org.rubato.rubettes.bigbang.model.edits.TranslationEdit;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.DrawingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ReflectionModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.RotationModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ScalingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.ShearingModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.TranslationModeAdapter;
import org.rubato.rubettes.bigbang.view.controller.mode.temp.TemporaryDisplayMode;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.player.JSynScore;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangView extends Model implements View {
	
	private BigBangController controller;
	protected ViewController viewController;
	protected BigBangPlayer player;
	private boolean playingActive;
	protected JBigBangPanel panel;
	private LayerStates layerStates;
	private DisplayModeAdapter displayMode;
	protected Point displayPosition;
	private boolean modFilterOn;
	private int modLevel, modNumber;
	protected double xZoomFactor, yZoomFactor;
	private boolean mainOptionsVisible;
	private boolean viewParametersVisible;
	protected ViewParameters viewParameters;
	private Map<String,Double> standardDenotatorValues;
	private boolean satellitesConnected;
	DenotatorValueExtractor extractor;
	protected DisplayObjects displayNotes;
	private DisplayTool displayTool;
	//only used in preview mode
	private SelectedObjectsPaths selectedObjectsPaths;
	private boolean inWallpaperMode;
	private List<Integer> wallpaperRanges;
	private AbstractTransformationEdit selectedTransformation;
	
	public BigBangView(BigBangController controller) {
		this.controller = controller;
		this.controller.addView(this);
		this.initBigBangPlayer();
		this.initViewMVC();
		this.initViewParameterControls();
		this.initStandardDenotatorValues();
		this.extractor = new DenotatorValueExtractor(this.layerStates);
		this.setSatellitesConnected(true);
		this.setDisplayMode(new DrawingModeAdapter(viewController));
		//TODO:make this automatic when displaynotes loaded!!! depending on max/min and window size
		this.setDisplayPosition(new Point(20, 560));
		this.setZoomFactors(5.0, 5.0);
		this.setTempo(BigBangPlayer.INITIAL_BPM);
		this.modFilterOn = false;
		this.modNumber = -1;
		this.wallpaperRanges = new ArrayList<Integer>();
	}
	
	public void addNewWindow() {
		Frame parentFrame = JOptionPane.getFrameForComponent(this.panel);
		new BigBangAdditionalView(this.controller, parentFrame);
		SelectedObjectsPaths paths = this.displayNotes.getCategorizedSelectedObjectsPaths();
		this.controller.newWindowAdded(paths);
	}
	
	public void dispose() {
		this.controller.removeView(this);
	}
	
	private void initBigBangPlayer() {
		this.player = new BigBangPlayer();
		this.playingActive = false;
	}
	
	private void initViewMVC() {
		this.viewController = new ViewController();
		this.viewController.addModel(this);
		this.initViewParameters();
		this.initVisibleInterface();
		this.layerStates = new LayerStates(this.viewController);
	}
	
	protected void initViewParameters() {
		this.viewParameters = new ViewParameters(viewController, true);
	}
	
	protected void initVisibleInterface() {
		this.panel = new JBigBangPanel(this.viewController, this.controller, this.viewParameters, this.player);
	}
	
	private void initViewParameterControls() {
		//this.viewParametersVisible = false;
		//this.toggleViewParametersVisible();
		this.mainOptionsVisible = true;
		this.toggleMainOptionsVisible();
	}
	
	private void initStandardDenotatorValues() {
		this.standardDenotatorValues = new TreeMap<String,Double>();
		//insert some preset standard values
		this.standardDenotatorValues.put("Pitch Q", 60.0);
		this.standardDenotatorValues.put("Loudness Z", 120.0);
		this.standardDenotatorValues.put("Duration R", 1.0);
	}
	
	public void setDisplayPosition(Point position) {
		//Point oldValue = this.displayPosition;
		this.displayPosition = position;
		//System.out.println("dp " + position);
		this.firePropertyChange(ViewController.DISPLAY_POSITION, null, this.displayPosition);
	}
	
	public void setZoomFactors(Double xZoomFactor, Double yZoomFactor) {
		double[] oldValue = new double[]{this.xZoomFactor, this.yZoomFactor};
		this.xZoomFactor = xZoomFactor;
		this.yZoomFactor = yZoomFactor;
		double[] newValue = new double[]{this.xZoomFactor, this.yZoomFactor};
		//System.out.println("zf " + this.xZoomFactor + " " + this.yZoomFactor);
		this.firePropertyChange(ViewController.ZOOM_FACTORS, oldValue, newValue);
	}
	
	public void showWindowPreferences() {
		this.firePropertyChange(ViewController.SHOW_WINDOW_PREFERENCES, null, null);
	}
	
	public void setDisplayMode(DisplayModeAdapter newMode) {
		if (newMode instanceof TemporaryDisplayMode) {
			((TemporaryDisplayMode)newMode).setPreviousDisplayMode(this.displayMode);
		}
		this.displayMode = newMode;
		this.firePropertyChange(ViewController.DISPLAY_MODE, null, newMode);
	}
	
	public void toggleMainOptionsVisible() {
		boolean oldValue = this.mainOptionsVisible;
		this.mainOptionsVisible = !oldValue;
		this.firePropertyChange(ViewController.MAIN_OPTIONS_VISIBLE, oldValue, this.mainOptionsVisible);
	}
	
	public void toggleViewParametersVisible() {
		boolean oldValue = this.viewParametersVisible;
		this.viewParametersVisible = !oldValue;
		this.firePropertyChange(ViewController.VIEW_PARAMETERS_VISIBLE, oldValue, this.viewParametersVisible);
	}
	
	public void setSatellitesConnected(boolean satellitesConnected) {
		this.satellitesConnected = satellitesConnected;
		this.firePropertyChange(ViewController.SATELLITES_CONNECTED, null, this.satellitesConnected);
	}
	
	public void changeZoomFactors(ZoomChange zoomChange) {
		double oldXZoomFactor = this.xZoomFactor;
		double oldYZoomFactor = this.yZoomFactor;
		double xZoomFactor = this.xZoomFactor * zoomChange.getChangeFactor();
		double yZoomFactor = this.yZoomFactor * zoomChange.getChangeFactor();
		this.setZoomFactors(xZoomFactor, yZoomFactor);
		//zusammenfassen????
		int x = zoomChange.getX();
		int xPos = this.displayPosition.x;
		int xPosition = (int)Math.round(xPos + (x-xPos)*(1-(this.xZoomFactor/oldXZoomFactor)));
		int y = zoomChange.getY();
		int yPos = this.displayPosition.y;
		int yPosition = (int)Math.round(yPos + (y-yPos)*(1-(this.yZoomFactor/oldYZoomFactor)));
		this.setDisplayPosition(new Point(xPosition, yPosition));
	}
	
	public void changeDisplayPosition(Dimension difference) {
		Point position = this.displayPosition;
		position.translate(difference.width, difference.height);
		this.setDisplayPosition(position);
	}
	
	public void toggleModFilter() {
		this.modFilterOn = !this.modFilterOn;
		this.firePropertyChange(ViewController.TOGGLE_MOD_FILTER, null, this.modFilterOn);
		if (this.modFilterOn) {
			this.firePropertyChange(ViewController.MOD_FILTER_VALUES, null, new int[]{this.modLevel, this.modNumber});
		}
	}
	
	public void changeModFilter(Integer modLevel, Integer modNumber) {
		boolean changed = false;
		if (this.modLevel != modLevel) {
			this.modLevel = modLevel;
			changed = true;
		} else if (this.modNumber != modNumber) {
			this.modNumber = modNumber;
			changed = true;
		}
		if (changed && this.modFilterOn) {
			this.firePropertyChange(ViewController.MOD_FILTER_VALUES, null, new int[]{modLevel, modNumber});
		}
	}
	
	public void togglePlayMode() {
		if (!this.playingActive) {
			this.player.startPlaying();
		} else {
			this.player.stopPlaying();
		}
		this.playingActive = !this.playingActive;
		this.panel.toggleTimedRepaint();
		this.firePropertyChange(ViewController.PLAY_MODE, null, this.playingActive);
	}
	
	public void toggleNoteSelection(Point location) {
		int selectedNoteCount = this.displayNotes.selectTopOrDeselectAllNotes(location);
		this.firePropertyChange(ViewController.NOTE_SELECTION, null, selectedNoteCount);
	}
	
	public void toggleAnchorNoteSelection(Point location) {
		this.displayNotes.selectOrDeselectAnchorNote(location);
		this.firePropertyChange(ViewController.ANCHOR_NOTE_SELECTION, null, null);
	}
	
	public void selectNotes(SelectionTool tool, Boolean stillSelecting) {
		this.selectNotes(tool.getArea(), tool, stillSelecting);
	}
	
	protected void selectNotes(Rectangle2D.Double area, SelectionTool tool, boolean stillSelecting) {
		if (stillSelecting) {
			this.displayNotes.tempSelectNotes(area);
			this.firePropertyChange(ViewController.DISPLAY_TOOL, null, tool);
		} else {
			int selectedNoteCount = this.displayNotes.selectNotes(area);
			this.firePropertyChange(ViewController.NOTE_SELECTION, null, selectedNoteCount);
			this.firePropertyChange(ViewController.DISPLAY_TOOL, null, null);
		}
	}
	
	public void setDisplayTool(DisplayTool tool) {
		this.displayTool = tool;
		this.firePropertyChange(ViewController.DISPLAY_TOOL, null, this.displayTool);
	}
	
	public void clearDisplayTool() {
		this.displayTool = null;
		this.firePropertyChange(ViewController.DISPLAY_TOOL, null, null);
	}
	
	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(BigBangController.COMPOSITION)) {
			this.updateDisplayObjects(event);
		} else if (propertyName.equals(BigBangController.UNDO)) {
			this.firePropertyChange(ViewController.UNDO, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.REDO)) {
			this.firePropertyChange(ViewController.REDO, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.INPUT_ACTIVE)) {
			this.firePropertyChange(ViewController.INPUT_ACTIVE, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.ADD_OBJECT)) {
			//not every sounding object makes sense to be played alone (e.g. modulator, or overtone)
			//this.playObject((Denotator)event.getNewValue());
		} else if (propertyName.equals(BigBangController.DESELECT_COMPOSITION_STATES)) {
			this.firePropertyChange(ViewController.DESELECT_COMPOSITION_STATES, null, null);
		}
	}
	
	private void updateDisplayObjects(PropertyChangeEvent event) {
		ScoreChangedNotification notification = (ScoreChangedNotification)event.getNewValue();
		DisplayObjects newObjects = this.extractor.extractValues(this.viewController, notification, !notification.preview());
		
		if (this.modFilterOn) {
			newObjects.updateModulatorVisibility(this.modLevel, this.modNumber);
		}
		if (notification.preview()) {
			this.selectedObjectsPaths = notification.getSelectedObjectsPaths();
		} else {
			//TODO: WHY??
			this.selectedObjectsPaths = null;
		}
		this.updateDisplayObjects(newObjects, this.extractor.getMinValues(), this.extractor.getMaxValues());
		this.updatePlayerScore(this.extractor.getJSynScore(), notification.playback());
	}
	
	private void updateDisplayObjects(DisplayObjects displayNotes, List<Double> minValues, List<Double> maxValues) {
		if (this.displayNotes == null || displayNotes.getBaseForm() != this.displayNotes.getBaseForm()) {
			if (displayNotes.baseFormAllowsForSatellites()) {
				this.viewParameters.initSelections(displayNotes.getCoordinateSystemValueNames().size()-2);
			} else {
				this.viewParameters.initSelections(displayNotes.getCoordinateSystemValueNames().size());
			}
			this.viewController.removeView(this.displayNotes);
			this.displayNotes = displayNotes;
			this.firePropertyChange(ViewController.FORM, null, displayNotes);
			this.firePropertyChange(ViewController.ACTIVE_COLIMIT_COORDINATE, null, displayNotes.getActiveColimitCoordinates());
		}
		this.viewParameters.setDenotatorMinAndMaxValues(minValues, maxValues);
		//do not select parameters for satellite and sibling number...
		this.firePropertyChange(ViewController.DISPLAY_NOTES, null, this.displayNotes);
		this.firePropertyChange(ViewController.STANDARD_DENOTATOR_VALUES, null, this.getStandardDenotatorValues());
		this.firePropertyChange(ViewController.MAX_SATELLITE_LEVEL, null, this.displayNotes.getMaxSatelliteLevelOfActiveObject());
	}
	
	public void setStandardDenotatorValue(Integer index, Double value) {
		this.standardDenotatorValues.put(this.displayNotes.getCoordinateSystemValueNames().get(index), value);
		this.firePropertyChange(ViewController.STANDARD_DENOTATOR_VALUES, null, this.getStandardDenotatorValues());
	}
	
	public List<Double> getStandardDenotatorValues() {
		List<Double> values = new ArrayList<Double>();
		for (String currentValueName : this.displayNotes.getCoordinateSystemValueNames()) {
			if (this.standardDenotatorValues.containsKey(currentValueName)) {
				values.add(this.standardDenotatorValues.get(currentValueName));
			} else {
				values.add(null);
			}
		}
		return values;
	}
	
	public void setActiveObject(Integer objectTypeIndex) {
		this.displayNotes.setIndexOfActiveObjectType(objectTypeIndex);
		this.firePropertyChange(ViewController.ACTIVE_OBJECT, null, objectTypeIndex);
		this.firePropertyChange(ViewController.MAX_SATELLITE_LEVEL, null, this.displayNotes.getMaxSatelliteLevelOfActiveObject());
	}
	
	public void setActiveColimitCoordinate(Integer colimitIndex, Integer coordinateIndex) {
		this.displayNotes.setActiveColimitCoordinate(colimitIndex, coordinateIndex);
		this.firePropertyChange(ViewController.ACTIVE_COLIMIT_COORDINATE, null, this.displayNotes.getActiveColimitCoordinates());
	}
	
	public void setActiveSatelliteLevel(Integer satelliteLevel) {
		this.displayNotes.setActiveSatelliteLevel(satelliteLevel);
		this.firePropertyChange(ViewController.ACTIVE_SATELLITE_LEVEL, null, satelliteLevel);
	}
	
	public void selectCompositionState(Integer vertex) {
		this.firePropertyChange(ViewController.SELECT_COMPOSITION_STATE, null, vertex);
		this.controller.selectCompositionState(vertex);
	}
	
	public void deselectCompositionStates() {
		this.firePropertyChange(ViewController.DESELECT_COMPOSITION_STATES, null, null);
		this.controller.deselectCompositionStates();
	}
	
	public void selectTransformation(AbstractTransformationEdit edit) {
		this.selectedTransformation = edit;
		if (this.selectedTransformation != null) {
			//select perspective first
			this.viewParameters.setSelectedXYViewParameters(edit.getXYViewParameters());
			//TODO: center view?????
			
			//TODO: then select notes!!!
			
			//then select displaymode and convert values!!
			if (edit instanceof TranslationEdit) {
				double[] startingPoint = this.getXYDisplayValues(((TranslationEdit)edit).getStartingPoint());
				double[] endingPoint = this.getXYDisplayValues(((TranslationEdit)edit).getEndingPoint());
				this.setDisplayMode(new TranslationModeAdapter(this.viewController, startingPoint, endingPoint));
			} else {
				AbstractLocalTransformationEdit localEdit = (AbstractLocalTransformationEdit)edit;
				double[] startingPoint = this.getXYDisplayValues(localEdit.getCenter());
				double[] endingPoint = this.getXYDisplayValues(localEdit.getEndPoint());
				if (edit instanceof RotationEdit) {
					double angle = ((RotationEdit)edit).getAngle();
					this.setDisplayMode(new RotationModeAdapter(this.viewController, startingPoint, endingPoint, angle));
				} else if (edit instanceof ScalingEdit) {
					double[] scaleFactors = ((ScalingEdit)edit).getScaleFactors();
					this.setDisplayMode(new ScalingModeAdapter(this.viewController, startingPoint, endingPoint, scaleFactors));
				} else if (edit instanceof ShearingEdit) {
					double[] shearingFactors = ((ShearingEdit)edit).getShearingFactors();
					this.setDisplayMode(new ShearingModeAdapter(this.viewController, startingPoint, endingPoint, shearingFactors));
				} else if (edit instanceof ReflectionEdit) {
					this.setDisplayMode(new ReflectionModeAdapter(this.viewController, startingPoint, endingPoint));
				}
			}
		} else {
			this.clearDisplayTool();
			this.firePropertyChange(ViewController.SELECT_TRANSFORMATION, null, null);
		}
	}
	
	public void deselectTransformations() {
		this.selectTransformation(null);
	}
	
	public void modifySelectedTransformation(Point2D.Double endingPoint, Boolean inPreviewMode) {
		this.selectedTransformation.modify(this.getXYDenotatorValues(endingPoint));
		this.controller.modifiedTransformation(inPreviewMode);
	}
	
	public void modifySelectedTransformation(double[] newValues, Boolean inPreviewMode) {
		this.selectedTransformation.modify(newValues);
		this.controller.modifiedTransformation(inPreviewMode);
	}
	
	public void modifyRotationAngle(Double angle, Boolean inPreviewMode) {
		((RotationEdit)this.selectedTransformation).modifyAngle(angle);
		this.controller.modifiedTransformation(inPreviewMode);
	}
	
	public void translateSelectedNotes(Point2D.Double center, Point2D.Double endingPoint, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endingPoint, copyAndTransform, previewMode);
		this.controller.translateNotes(properties);
	}
	
	public void rotateSelectedNotes(Point2D.Double center, Point2D.Double endPoint, Double angle, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.rotateNotes(properties, angle);
	}
	
	public void scaleSelectedNotes(Point2D.Double center, Point2D.Double endPoint, double[] scaleFactors, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.scaleNotes(properties, scaleFactors);
	}
	
	public void reflectSelectedNotes(Point2D.Double center, Point2D.Double endPoint, double[] reflectionVector, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.reflectNotes(properties, reflectionVector);
	}
	
	public void shearSelectedNotes(Point2D.Double center, Point2D.Double endPoint, double[] shearingFactors, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.shearNotes(properties, shearingFactors);
	}
	
	public void affineTransformSelectedNotes(Point2D.Double center, Point2D.Double endPoint, double[] shift, Double angle, double[] scaleFactors, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.affineTransformNotes(properties, shift, angle, scaleFactors);
	}
	
	public void shapeSelectedNotes(TreeMap<Integer,Integer> locations, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getTransformationProperties(copyAndTransform, previewMode);
		this.controller.shapeNotes(properties, this.getXYDenotatorValues(locations));
	}
	
	public void addWallpaperDimension() {
		if (!this.inWallpaperMode) {
			this.wallpaperRanges = new ArrayList<Integer>();
			this.inWallpaperMode = true;
			this.firePropertyChange(ViewController.START_WALLPAPER, null, null);
			this.controller.startWallpaper(new ArrayList<DenotatorPath>(this.displayNotes.getSelectedObjectsPaths()));
		} else {
			this.firePropertyChange(ViewController.ADD_WP_DIMENSION, null, null);
		}
		this.wallpaperRanges.add(0);
		this.wallpaperRanges.add(5);
		this.controller.addWallpaperDimension(0, 5);
		this.firePropertyChange(ViewController.RANGE, null, this.wallpaperRanges);
	}
	
	public void endWallpaper() {
		this.inWallpaperMode = false;
		this.firePropertyChange(ViewController.END_WALLPAPER, null, null);
		this.controller.endWallpaper();
	}
	
	public void setRange(Integer dimension, Boolean rangeTo, Integer value) {
		int oldValue = this.wallpaperRanges.get(this.getRangeIndex(dimension, rangeTo));
		int otherValue = this.wallpaperRanges.get(this.getRangeIndex(dimension, !rangeTo));
		if (value != oldValue
				&& ((rangeTo && otherValue <= value) || (!rangeTo && otherValue >= value))) {
			this.wallpaperRanges.set(this.getRangeIndex(dimension, rangeTo), value);
			this.controller.updateWallpaper(this.wallpaperRanges);
			this.firePropertyChange(ViewController.RANGE, null, this.wallpaperRanges);
		}
	}
	
	private int getRangeIndex(Integer dimension, boolean rangeTo) {
		int index = 2*(dimension-1);
		if (rangeTo) {
			index++;
		}
		return index;
	}
	
	public void setAlterationComposition(Integer index) {
		Set<DenotatorPath> nodePaths = this.displayNotes.getSelectedObjectsPaths();
		this.controller.setAlterationComposition(index, nodePaths);
	}
	
	private TransformationProperties getLocalTransformationProperties(Point2D.Double center, Point2D.Double endPoint, boolean copyAndTransform, boolean previewMode) {
		//the end point is merely recorded for the display tool to be the same size....
		TransformationProperties properties = this.getTransformationProperties(copyAndTransform, previewMode);
		double[] denotatorCenter = this.getXYDenotatorValues(center);
		double[] denotatorEndPoint = this.getXYDenotatorValues(endPoint);
		if (properties.getAnchorNodePath() != null) {
			if (this.selectedObjectsPaths != null) {
				properties.setAnchorNodePath(this.selectedObjectsPaths.getAnchorPath());
			}
			double[] anchorValues = this.getXYDenotatorValues(this.displayNotes.getSelectedAnchorNodeCenter());
			denotatorCenter[0] -= anchorValues[0];
			denotatorCenter[1] -= anchorValues[1];
		}
		properties.setCenter(denotatorCenter);
		properties.setEndPoint(denotatorEndPoint);
		return properties;
	}
	
	private TransformationProperties getTransformationProperties(boolean copyAndTransform, boolean previewMode) {
		SelectedObjectsPaths objectsPaths;
		if (this.selectedObjectsPaths == null) {
			objectsPaths = this.displayNotes.getCategorizedSelectedObjectsPaths();
		} else {
			objectsPaths = this.selectedObjectsPaths;
		}
		List<TransformationPaths> valuePaths = this.getXYTransformationPaths();
		return new TransformationProperties(objectsPaths, valuePaths, copyAndTransform, previewMode, this.inWallpaperMode);
	}
	
	public void addObject(Point2D.Double location) {
		Map<DenotatorPath,Double> objectValues = this.displayNotes.getActiveObjectStandardValues(this.standardDenotatorValues);
		DenotatorPath objectPowersetPath = this.editObjectValuesAndFindClosestPowerset(location, objectValues);
		
		//only add object if there are some screen values to be converted
		if (!objectValues.isEmpty()) {
			if (objectPowersetPath != null) {
				this.controller.addObject(objectValues, objectPowersetPath);
			} else {
				this.controller.addObject(objectValues);
			}
		}
	}
	
	public void deleteSelectedObjects() {
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>(this.displayNotes.getSelectedObjectsPaths());
		if (paths.size() > 0) {
			this.controller.deleteObjects(paths);
		}
	}
	
	public void copySelectedNotesTo(Integer layerIndex) {
		Set<DenotatorPath> objectsPaths = this.displayNotes.getSelectedObjectsPaths();
		if (objectsPaths.size() > 0) {
			this.controller.copyNotes(objectsPaths, layerIndex);
		}
	}
	
	public void copySelectedNotesToNewLayer() {
		this.copySelectedNotesTo(this.layerStates.size());
	}
	
	public void moveSelectedNotesTo(Integer layerIndex) {
		Set<DenotatorPath> objectsPaths = this.displayNotes.getSelectedObjectsPaths();
		if (objectsPaths.size() > 0) {
			this.controller.moveNotes(objectsPaths, layerIndex);
		}
	}
	
	public void moveSelectedNotesToNewLayer() {
		Set<DenotatorPath> objectsPaths = this.displayNotes.getSelectedObjectsPaths();
		if (objectsPaths.size() > 0) {
			this.controller.moveNotes(objectsPaths, this.layerStates.size());
		}
	}
	
	public void addSelectedNotesAsSatellitesTo(DisplayObject anchorObject, Integer powersetIndex) {
		Set<DenotatorPath> satellitePaths = this.displayNotes.getSelectedObjectsPaths();
		DenotatorPath anchorPath = anchorObject.getTopDenotatorPath();
		this.controller.buildSatellites(satellitePaths, anchorPath, powersetIndex);
	}
	
	public void flattenSelectedNotes() {
		Set<DenotatorPath> objectsPaths = this.displayNotes.getSelectedObjectsPaths();
		this.controller.flattenNotes(objectsPaths);
	}
	
	/*public void addSelectedNotesAsModulatorsTo(DisplayObject parentNote) {
		Set<NotePath> modulatorNodePaths = this.displayNotes.getSelectedNodePaths();
		NotePath parentNodePath = parentNote.getOriginalPath();
		this.controller.addAsModulators(modulatorNodePaths, parentNodePath);
	}*/
	
	public void removeSelectedNotesFromCarrier() {
		Set<DenotatorPath> objectsPaths = this.displayNotes.getSelectedObjectsPaths();
		this.controller.removeNotesFromCarrier(objectsPaths);
	}
	
	private TreeMap<Double,Double> getXYDenotatorValues(TreeMap<Integer,Integer> locations) {
		Set<Integer> xPositions = locations.keySet();
		TreeMap<Double,Double> translatedValues = new TreeMap<Double,Double>();
		for (Integer currentXPosition: xPositions) {
			Point2D.Double currentPoint = new Point2D.Double(currentXPosition, locations.get(currentXPosition));
			double[] currentXYDenotatorValues = this.getXYDenotatorValues(currentPoint);
			translatedValues.put(currentXYDenotatorValues[0], currentXYDenotatorValues[1]);
		}
		return translatedValues;
	}
	
	private double[] getXYDisplayValues(double[] denotatorValues) {
		double xValue = this.getDisplayValue(denotatorValues[0], 0, this.displayPosition.x, this.xZoomFactor);
		double yValue = this.getDisplayValue(denotatorValues[1], 1, this.displayPosition.y, this.yZoomFactor);
		return new double[] {xValue, yValue};
	}
	
	private double[] getXYDenotatorValues(Point2D.Double location) {
		double xValue = this.getDenotatorValue(location.x, 0, this.displayPosition.x, this.xZoomFactor);
		double yValue = this.getDenotatorValue(location.y, 1, this.displayPosition.y, this.yZoomFactor);
		return new double[] {xValue, yValue};
	}
	
	private DenotatorPath editObjectValuesAndFindClosestPowerset(Point2D.Double location, Map<DenotatorPath,Double> denotatorValues) {
		DenotatorPath parentPowersetPath = this.displayNotes.getActiveObjectAndLevelPowersetPath();
		int[] xyParameters = this.viewParameters.getSelectedXYViewParameters();
		int xValueIndex = this.displayNotes.getActiveObjectValueIndex(xyParameters[0]);
		int yValueIndex = this.displayNotes.getActiveObjectValueIndex(xyParameters[1]);
		double[] xyDenotatorValues = new double[2];
		xyDenotatorValues[0] = this.getDenotatorValue(location.x, 0, this.displayPosition.x, this.xZoomFactor);
		xyDenotatorValues[1] = this.getDenotatorValue(location.y, 1, this.displayPosition.y, this.yZoomFactor);
		if (xValueIndex >= 0) {
			this.replaceDenotatorValue(location.x, xValueIndex, 0, this.displayPosition.x, this.xZoomFactor, denotatorValues);
		}
		if (yValueIndex >= 0 && (xValueIndex < 0 || yValueIndex != xValueIndex)) {
			this.replaceDenotatorValue(location.y, yValueIndex, 1, this.displayPosition.y, this.yZoomFactor, denotatorValues);
		}
		DenotatorPath closestPowersetPath = this.findClosestPowersetPath(xyParameters, xyDenotatorValues, parentPowersetPath);
		//System.out.println(parentPowersetPath + " " + closestPowersetPath);
		return closestPowersetPath;
	}
	
	private void replaceDenotatorValue(double displayValue, int valueIndex, int parameterIndex, int position, double zoomFactor, Map<DenotatorPath,Double> values) {
		double denotatorValue = this.getDenotatorValue(displayValue, parameterIndex, position, zoomFactor);
		DenotatorPath associatedPath = this.displayNotes.getActiveObjectValuePathAt(valueIndex);
		//null happens when parent value, satellite/sibling level is selected, or when path not in active colimits
		if (associatedPath != null) {
			values.put(associatedPath, denotatorValue);
		}
	}
	
	private DenotatorPath findClosestPowersetPath(int[] valueIndices, double[] denotatorValues, DenotatorPath parentPowersetPath) {
		if (parentPowersetPath != null) {
			DisplayObject closestObject = this.displayNotes.getClosestObject(valueIndices, denotatorValues, parentPowersetPath);
			if (closestObject != null) {
				return closestObject.getTopDenotatorPath().getDescendantPathAccordingTo(parentPowersetPath);
			}
		}
		return parentPowersetPath;
	}
	
	protected double getDenotatorValue(double displayValue, int parameterIndex, int position, double zoomFactor) {
		double value = (displayValue-position)/zoomFactor;
		return this.viewParameters.get(parameterIndex).translateDisplayValue(value);
	}
	
	protected double getDisplayValue(double denotatorValue, int parameterIndex, int position, double zoomFactor) {
		double displayValue = this.viewParameters.get(parameterIndex).translateDenotatorValue(denotatorValue);
		return (displayValue*zoomFactor)+position;
	}
	
	private List<TransformationPaths> getXYTransformationPaths() {
		List<TransformationPaths> paths = new ArrayList<TransformationPaths>();
		int[] xyParameters = this.viewParameters.getSelectedXYViewParameters();
		for (int i = 0; i < this.displayNotes.getObjectTypes().size(); i++) {
			TransformationPaths currentPaths = new TransformationPaths();
			//TODO: bad so redundant
			currentPaths.setXYCoordinates(xyParameters);
			paths.add(currentPaths);
		}
		//only one parameter might be selected... TODO does the list need to be null or can it just be empty??
		for (int i = 0; i < xyParameters.length; i++) {
			if (xyParameters[i] >= 0) {
				List<List<DenotatorPath>> currentPaths = this.displayNotes.getAllObjectConfigurationsValuePathsAt(xyParameters[i]);
				for (int j = 0; j < currentPaths.size(); j++) {
					paths.get(j).setDomainPaths(i, currentPaths.get(j));
					paths.get(j).setCodomainPaths(i, currentPaths.get(j));
				}
			} else {
				for (TransformationPaths currentPaths : paths) {
					currentPaths.setDomainPaths(i, null);
					currentPaths.setCodomainPaths(i, null);
				}
			}
		}
		return paths;
	}
	
	public void undo() {
		this.controller.undo();
	}
	
	public void redo() {
		this.controller.redo();
	}
	
	public void setInputActive(Boolean inputActive) {
		this.controller.changeInputActive(inputActive);
	}
	
	public JPanel getPanel() {
		return this.panel;
	}
	
	private void updatePlayerScore(JSynScore score, boolean play) {
		this.player.setScore(score);
		if (play && this.playingActive && !this.player.isPlaying()) {
			this.player.startPlaying();
		}
	}
	
	//not every sounding object makes sense to be played alone (e.g. modulator, or overtone)
	/*private void playObject(Denotator object) {
		if (this.playingActive) {
			DenotatorValueExtractor extractor = new DenotatorValueExtractor(object);
			this.player.playObject(extractor.getJSynScore().getObjects().get(0));
		}
	}*/
	
	public void setIsLooping(Boolean isLooping) {
		this.player.setIsLooping(isLooping);
		this.firePropertyChange(ViewController.IS_LOOPING, null, isLooping);
	}
	
	public void setTempo(Integer tempo) {
		this.player.setTempo(tempo);
		this.firePropertyChange(ViewController.TEMPO, null, tempo);
	}
	
	public void setPlaybackPosition(Point2D.Double location) {
		int timeAxisIndex = this.displayNotes.getTimeAxisIndex(this.viewParameters);
		if (timeAxisIndex != -1) {
			double[] xyDenotatorValues = this.getXYDenotatorValues(location);
			this.player.setPlaybackPosition(xyDenotatorValues[timeAxisIndex]);
		}
	}
	
	public void setFMModel(String fmModel) {
		//TODO: THINK ABOUT THIS!!! this.score.noteGenerator.setFMModel(fmModel);
		this.firePropertyChange(ViewController.FM_MODEL, null, fmModel);
	}
	
	public void setWaveform(String waveform) {
		this.player.setWaveform(waveform);
		this.firePropertyChange(ViewController.WAVEFORM, null, waveform);
	}

}

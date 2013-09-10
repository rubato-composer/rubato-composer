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

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.model.Model;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.model.edits.AbstractLocalTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AddWallpaperDimensionEdit;
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
import org.rubato.rubettes.bigbang.view.player.BigBangMidiReceiver;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.player.BigBangRecorder;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangView extends Model implements View {
	
	private BigBangController controller;
	protected ViewController viewController;
	protected BigBangPlayer player;
	protected BigBangRecorder recorder;
	protected BigBangMidiReceiver midiReceiver;
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
	protected DisplayObjects displayObjects;
	private DisplayTool displayTool;
	//only used in preview mode
	private SelectedObjectsPaths selectedObjectsPaths;
	private AbstractOperationEdit selectedOperation;
	
	public BigBangView(BigBangController controller) {
		this.controller = controller;
		this.controller.addView(this);
		this.player = new BigBangPlayer();
		this.initViewMVC();
		this.initViewParameterControls();
		this.initStandardDenotatorValues();
		this.extractor = new DenotatorValueExtractor(this.layerStates);
		this.setSatellitesConnected(true);
		this.setDisplayMode(new DrawingModeAdapter(viewController));
		//TODO:make this automatic when displaynotes loaded!!! depending on max/min and window size
		this.setDisplayPosition(new Point(20, 560));
		this.setZoomFactors(5.0, 5.0);
		this.midiReceiver = new BigBangMidiReceiver(this.viewController);
		this.recorder = new BigBangRecorder(this, this.player, this.controller);
		this.setTempo(BigBangPlayer.INITIAL_BPM);
		this.modFilterOn = false;
		this.modNumber = -1;
	}
	
	public void addNewWindow() {
		Frame parentFrame = JOptionPane.getFrameForComponent(this.panel);
		BigBangAdditionalView newView = new BigBangAdditionalView(this.controller, parentFrame);
		this.controller.newWindowAdded();
		newView.pack();
		newView.setVisible(true);
	}
	
	public void dispose() {
		this.controller.removeView(this);
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
		this.standardDenotatorValues.put("Pan R", 64.0);
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
		this.player.togglePlayMode();
		this.panel.toggleTimedRepaint();
		this.firePropertyChange(ViewController.PLAY_MODE, null, this.player.isPlaying());
		//stop recording if playing stopped
		if (!this.player.isPlaying() && this.recorder.isRecording()) {
			this.toggleRecordMode();
		}
	}
	
	public void toggleRecordMode() {
		this.recorder.toggleRecordMode();
		this.firePropertyChange(ViewController.RECORD_MODE, null, this.recorder.isRecording());
		//start playing if recording started
		if (this.recorder.isRecording() && !this.player.isPlaying()) {
			this.togglePlayMode();
		}
	}
	
	public void toggleObjectSelection(Point location) {
		int selectedObjectCount = this.displayObjects.selectTopOrDeselectAllObjects(location);
		this.firePropertyChange(ViewController.OBJECT_SELECTION, null, selectedObjectCount);
	}
	
	public void toggleAnchorObjectSelection(Point location) {
		this.displayObjects.selectOrDeselectAnchorObject(location);
		this.firePropertyChange(ViewController.ANCHOR_OBJECT_SELECTION, null, null);
	}
	
	public void selectObjects(SelectionTool tool, Boolean stillSelecting) {
		this.selectObjects(tool.getArea(), tool, stillSelecting);
	}
	
	protected void selectObjects(Rectangle2D.Double area, SelectionTool tool, boolean stillSelecting) {
		if (stillSelecting) {
			this.displayObjects.tempSelectObjects(area);
			this.firePropertyChange(ViewController.DISPLAY_TOOL, null, tool);
		} else {
			int selectedObjectCount = this.displayObjects.selectObjects(area);
			this.firePropertyChange(ViewController.OBJECT_SELECTION, null, selectedObjectCount);
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
		} else if (propertyName.equals(BigBangController.MODIFY_OPERATION)) {
			this.selectOperation((AbstractOperationEdit)event.getNewValue());
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
		this.player.setScore(this.extractor.getJSynScore());
	}
	
	private void updateDisplayObjects(DisplayObjects displayObjects, List<Double> minValues, List<Double> maxValues) {
		if (this.displayObjects == null || displayObjects.getBaseForm() != this.displayObjects.getBaseForm()) {
			if (displayObjects.baseFormAllowsForSatellites()) {
				this.viewParameters.initSelections(displayObjects.getCoordinateSystemValueNames().size()-2);
			} else {
				this.viewParameters.initSelections(displayObjects.getCoordinateSystemValueNames().size());
			}
			this.viewController.removeView(this.displayObjects);
			this.displayObjects = displayObjects;
			this.firePropertyChange(ViewController.FORM, null, displayObjects);
			this.firePropertyChange(ViewController.ACTIVE_COLIMIT_COORDINATE, null, displayObjects.getActiveColimitCoordinates());
		}
		this.viewParameters.setDenotatorMinAndMaxValues(minValues, maxValues);
		//do not select parameters for satellite and sibling number...
		this.firePropertyChange(ViewController.DISPLAY_OBJECTS, null, this.displayObjects);
		this.firePropertyChange(ViewController.STANDARD_DENOTATOR_VALUES, null, this.getStandardDenotatorValues());
		this.firePropertyChange(ViewController.MAX_SATELLITE_LEVEL, null, this.displayObjects.getMaxSatelliteLevelOfActiveObject());
	}
	
	public DisplayObjects getDisplayObjects() {
		return this.displayObjects;
	}
	
	public void setStandardDenotatorValue(Integer index, Double value) {
		this.standardDenotatorValues.put(this.displayObjects.getCoordinateSystemValueNames().get(index), value);
		this.firePropertyChange(ViewController.STANDARD_DENOTATOR_VALUES, null, this.getStandardDenotatorValues());
	}
	
	public List<Double> getStandardDenotatorValues() {
		List<Double> values = new ArrayList<Double>();
		for (String currentValueName : this.displayObjects.getCoordinateSystemValueNames()) {
			if (this.standardDenotatorValues.containsKey(currentValueName)) {
				values.add(this.standardDenotatorValues.get(currentValueName));
			} else {
				values.add(null);
			}
		}
		return values;
	}
	
	public void setActiveObject(Integer objectTypeIndex) {
		this.displayObjects.setIndexOfActiveObjectType(objectTypeIndex);
		this.firePropertyChange(ViewController.ACTIVE_OBJECT, null, objectTypeIndex);
		this.firePropertyChange(ViewController.MAX_SATELLITE_LEVEL, null, this.displayObjects.getMaxSatelliteLevelOfActiveObject());
	}
	
	public void setActiveColimitCoordinate(Integer colimitIndex, Integer coordinateIndex) {
		this.displayObjects.setActiveColimitCoordinate(colimitIndex, coordinateIndex);
		this.firePropertyChange(ViewController.ACTIVE_COLIMIT_COORDINATE, null, this.displayObjects.getActiveColimitCoordinates());
	}
	
	public void setActiveSatelliteLevel(Integer satelliteLevel) {
		this.displayObjects.setActiveSatelliteLevel(satelliteLevel);
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
	
	public void selectOperation(AbstractOperationEdit edit) {
		this.selectedOperation = edit;
		if (this.selectedOperation != null) {
			//select perspective first
			if (edit instanceof AbstractTransformationEdit) {
				AbstractTransformationEdit transformationEdit = (AbstractTransformationEdit)edit;
				this.viewParameters.setSelectedXYViewParameters(transformationEdit.getXYViewParameters());
				//TODO: center view?????
			
				//TODO: then select notes!!!
			}
			
			//then select displaymode and convert values!!
			if (edit instanceof TranslationEdit) {
				double[] startingPoint = this.getXYDisplayValues(((TranslationEdit)edit).getStartingPoint());
				double[] endingPoint = this.getXYDisplayValues(((TranslationEdit)edit).getEndingPoint());
				this.setDisplayMode(new TranslationModeAdapter(this.viewController, startingPoint, endingPoint));
			} else if (edit instanceof AbstractLocalTransformationEdit) {
				AbstractLocalTransformationEdit localEdit = (AbstractLocalTransformationEdit)edit;
				double[] center = this.getXYDisplayValues(localEdit.getCenter());
				double[] endingPoint = this.getXYDisplayValues(localEdit.getEndPoint());
				if (edit instanceof RotationEdit) {
					double[] startingPoint = ((RotationEdit)edit).getStartingPoint();
					double angle = ((RotationEdit)edit).getAngle();
					this.setDisplayMode(new RotationModeAdapter(this.viewController, center, startingPoint, endingPoint, angle));
				} else if (edit instanceof ScalingEdit) {
					double[] scaleFactors = ((ScalingEdit)edit).getScaleFactors();
					this.setDisplayMode(new ScalingModeAdapter(this.viewController, center, endingPoint, scaleFactors));
				} else if (edit instanceof ShearingEdit) {
					double[] shearingFactors = ((ShearingEdit)edit).getShearingFactors();
					this.setDisplayMode(new ShearingModeAdapter(this.viewController, center, endingPoint, shearingFactors));
				} else if (edit instanceof ReflectionEdit) {
					this.setDisplayMode(new ReflectionModeAdapter(this.viewController, center, endingPoint));
				}
			}
			this.firePropertyChange(ViewController.SELECT_OPERATION, null, this.selectedOperation);
		} else {
			this.clearDisplayTool();
			this.firePropertyChange(ViewController.SELECT_OPERATION, null, null);
		}
	}
	
	public void deselectOperations() {
		this.selectOperation(null);
	}
	
	public void modifySelectedTransformation(Point2D.Double endingPoint, Boolean inPreviewMode) {
		((AbstractTransformationEdit)this.selectedOperation).modify(this.getXYDenotatorValues(endingPoint));
		this.controller.modifiedOperation(inPreviewMode);
	}
	
	public void modifySelectedTransformation(double[] newValues, Boolean inPreviewMode) {
		((AbstractTransformationEdit)this.selectedOperation).modify(newValues);
		this.controller.modifiedOperation(inPreviewMode);
	}
	
	public void modifyRotationAngle(Double angle, Boolean inPreviewMode) {
		((RotationEdit)this.selectedOperation).modifyAngle(angle);
		this.controller.modifiedOperation(inPreviewMode);
	}
	
	public void modifyOperation(Integer operationIndex, Integer midiValue) {
		//ratio between 0 and 2
		double ratio = ((double)midiValue)/127*2;
		this.controller.modifyOperation(operationIndex, ratio);
	}
	
	public void translateSelectedObjects(Point2D.Double center, Point2D.Double endingPoint, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endingPoint, copyAndTransform, previewMode);
		this.controller.translateObjects(properties);
	}
	
	public void rotateSelectedObjects(Point2D.Double center, Point2D.Double startPoint, Point2D.Double endPoint, Double angle, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.rotateObjects(properties, new double[]{startPoint.x,startPoint.y}, angle);
	}
	
	public void scaleSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] scaleFactors, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.scaleObjects(properties, scaleFactors);
	}
	
	public void reflectSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] reflectionVector, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.reflectObjects(properties, reflectionVector);
	}
	
	public void shearSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] shearingFactors, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.shearObjects(properties, shearingFactors);
	}
	
	public void affineTransformSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] shift, Double angle, double[] scaleFactors, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, previewMode);
		this.controller.affineTransformObjects(properties, shift, angle, scaleFactors);
	}
	
	public void shapeSelectedObjects(TreeMap<Integer,Integer> locations, Boolean copyAndTransform, Boolean previewMode) {
		TransformationProperties properties = this.getTransformationProperties(copyAndTransform, previewMode);
		this.controller.shapeObjects(properties, this.getXYDenotatorValues(locations));
	}
	
	public void addWallpaperDimension() {
		this.controller.addWallpaperDimension(this.displayObjects.getCategorizedSelectedObjectsPaths(), 0, 5);
	}
	
	public void modifyWallpaperRange(Boolean rangeTo, Integer value) {
		((AddWallpaperDimensionEdit)this.selectedOperation).setRange(rangeTo, value);
		this.controller.modifiedOperation(false);
	}
	
	public void setAlterationComposition(Integer index) {
		Set<DenotatorPath> nodePaths = this.displayObjects.getSelectedObjectsPaths();
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
			double[] anchorValues = this.getXYDenotatorValues(this.displayObjects.getSelectedAnchorNodeCenter());
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
			objectsPaths = this.displayObjects.getCategorizedSelectedObjectsPaths();
		} else {
			objectsPaths = this.selectedObjectsPaths;
		}
		List<TransformationPaths> valuePaths = this.getXYTransformationPaths();
		return new TransformationProperties(objectsPaths, valuePaths, copyAndTransform, previewMode);
	}
	
	public void addObject(Point2D.Double location) {
		Map<DenotatorPath,Double> objectValues = this.displayObjects.getActiveObjectStandardValues(this.standardDenotatorValues);
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
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>(this.displayObjects.getSelectedObjectsPaths());
		if (paths.size() > 0) {
			this.controller.deleteObjects(paths);
		}
	}
	
	public void copySelectedObjectsTo(Integer layerIndex) {
		Set<DenotatorPath> objectsPaths = this.displayObjects.getSelectedObjectsPaths();
		if (objectsPaths.size() > 0) {
			this.controller.copyObjects(objectsPaths, layerIndex);
		}
	}
	
	public void copySelectedObjectsToNewLayer() {
		this.copySelectedObjectsTo(this.layerStates.size());
	}
	
	public void moveSelectedObjectsTo(Integer layerIndex) {
		Set<DenotatorPath> objectsPaths = this.displayObjects.getSelectedObjectsPaths();
		if (objectsPaths.size() > 0) {
			this.controller.moveObjects(objectsPaths, layerIndex);
		}
	}
	
	public void moveSelectedObjectsToNewLayer() {
		Set<DenotatorPath> objectsPaths = this.displayObjects.getSelectedObjectsPaths();
		if (objectsPaths.size() > 0) {
			this.controller.moveObjects(objectsPaths, this.layerStates.size());
		}
	}
	
	public void addSelectedObjectsAsSatellitesTo(DisplayObject anchorObject, Integer powersetIndex) {
		Set<DenotatorPath> satellitePaths = this.displayObjects.getSelectedObjectsPaths();
		DenotatorPath anchorPath = anchorObject.getTopDenotatorPath();
		this.controller.buildSatellites(satellitePaths, anchorPath, powersetIndex);
	}
	
	public void flattenSelectedObjects() {
		Set<DenotatorPath> objectsPaths = this.displayObjects.getSelectedObjectsPaths();
		this.controller.flattenObjects(objectsPaths);
	}
	
	/*public void addSelectedObjectsAsModulatorsTo(DisplayObject parentObject) {
		Set<ObjectPath> modulatorNodePaths = this.displayObjects.getSelectedNodePaths();
		ObjectPath parentNodePath = parentObject.getOriginalPath();
		this.controller.addAsModulators(modulatorNodePaths, parentNodePath);
	}*/
	
	public void removeSelectedObjectsFromCarrier() {
		Set<DenotatorPath> objectsPaths = this.displayObjects.getSelectedObjectsPaths();
		this.controller.removeObjectsFromCarrier(objectsPaths);
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
		DenotatorPath parentPowersetPath = this.displayObjects.getActiveObjectAndLevelPowersetPath();
		int[] xyParameters = this.viewParameters.getSelectedXYViewParameters();
		int xValueIndex = this.displayObjects.getActiveObjectValueIndex(xyParameters[0]);
		int yValueIndex = this.displayObjects.getActiveObjectValueIndex(xyParameters[1]);
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
		DenotatorPath associatedPath = this.displayObjects.getActiveObjectValuePathAt(valueIndex);
		//null happens when parent value, satellite/sibling level is selected, or when path not in active colimits
		if (associatedPath != null) {
			values.put(associatedPath, denotatorValue);
		}
	}
	
	private DenotatorPath findClosestPowersetPath(int[] valueIndices, double[] denotatorValues, DenotatorPath parentPowersetPath) {
		if (parentPowersetPath != null) {
			DisplayObject closestObject = this.displayObjects.getClosestObject(valueIndices, denotatorValues, parentPowersetPath);
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
		for (int i = 0; i < this.displayObjects.getObjectTypes().size(); i++) {
			TransformationPaths currentPaths = new TransformationPaths();
			//TODO: bad so redundant
			currentPaths.setXYCoordinates(xyParameters);
			paths.add(currentPaths);
		}
		//only one parameter might be selected... TODO does the list need to be null or can it just be empty??
		for (int i = 0; i < xyParameters.length; i++) {
			if (xyParameters[i] >= 0) {
				List<List<DenotatorPath>> currentPaths = this.displayObjects.getAllObjectConfigurationsValuePathsAt(xyParameters[i]);
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
	
	public JBigBangPanel getPanel() {
		return this.panel;
	}
	
	public void setIsLooping(Boolean isLooping) {
		this.player.setIsLooping(isLooping);
		this.firePropertyChange(ViewController.IS_LOOPING, null, isLooping);
	}
	
	public void setTempo(Integer tempo) {
		this.player.setTempo(tempo);
		this.firePropertyChange(ViewController.TEMPO, null, tempo);
	}
	
	public void setPlaybackPosition(Point2D.Double location) {
		int timeAxisIndex = this.displayObjects.getTimeAxisIndex(this.viewParameters);
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
	
	public void pressMidiKey(Integer pitch, Integer velocity) {
		if (this.recorder.isRecording()) {
			this.recorder.pressMidiKey(pitch, velocity);
			this.player.pressMidiKey(pitch, velocity, true);
		} else {
			this.player.pressMidiKey(pitch, velocity, false);
		}
	}
	
	public void releaseMidiKey(Integer pitch) {
		if (this.recorder.isRecording()) {
			this.recorder.releaseMidiKey(pitch);
			this.player.releaseMidiKey(pitch, true);
		} else {
			this.player.releaseMidiKey(pitch, false);
		}
	}
	
	public void changeOctave(Boolean up) {
		if (!this.recorder.isRecording()) {
			this.player.transposeAllScoreVersionsByOctave(up);
		}
	}
	
	public void changeVelocity(Integer velocity) {
		this.player.changeVelocity(velocity);
	}

}

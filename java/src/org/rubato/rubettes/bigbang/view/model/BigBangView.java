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
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.BigBangObjects;
import org.rubato.rubettes.bigbang.model.Model;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;
import org.rubato.rubettes.bigbang.model.edits.AbstractLocalTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.model.edits.AddObjectsEdit;
import org.rubato.rubettes.bigbang.model.edits.AddWallpaperDimensionEdit;
import org.rubato.rubettes.bigbang.model.edits.AlterationEdit;
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
import org.rubato.rubettes.bigbang.view.io.BigBangMidiReceiver;
import org.rubato.rubettes.bigbang.view.io.BigBangMidiTransmitter;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.bigbang.view.player.BigBangRecorder;
import org.rubato.rubettes.bigbang.view.player.JSynScore;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;
import org.rubato.rubettes.util.CoolFormRegistrant;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.PointND;

public class BigBangView extends Model implements View {
	
	private BigBangController controller;
	protected ViewController viewController;
	protected BigBangPlayer player;
	protected BigBangRecorder recorder;
	protected BigBangMidiReceiver midiReceiver;
	protected BigBangMidiTransmitter midiTransmitter;
	protected JBigBangPanel panel;
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
	protected DisplayObjects displayObjects;
	private DisplayTool displayTool;
	private AbstractOperationEdit selectedOperation;
	
	public BigBangView(BigBangController controller) {
		this.controller = controller;
		this.controller.addView(this);
		this.player = new BigBangPlayer();
		this.initViewMVC();
		this.initViewParameterControls();
		this.initStandardDenotatorValues();
		this.setSatellitesConnected(true);
		this.setDisplayMode(new DrawingModeAdapter(this.viewController));
		//TODO:make this automatic when displaynotes loaded!!! depending on max/min and window size
		this.setDisplayPosition(new Point(20, 660));
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
		this.standardDenotatorValues.put(CoolFormRegistrant.PITCH_NAME, 60.0);
		this.standardDenotatorValues.put(CoolFormRegistrant.LOUDNESS_NAME, 120.0);
		this.standardDenotatorValues.put(CoolFormRegistrant.DURATION_NAME, 1.0);
		this.standardDenotatorValues.put(CoolFormRegistrant.PAN_NAME, 64.0);
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
		/*if (!(newMode instanceof AlterationCompositionSelectionMode && this.selectedOperation instanceof AlterationEdit)) {
			this.deselectOperations();
		}*/
		this.firePropertyChange(ViewController.DISPLAY_MODE, null, newMode);
	}
	
	public boolean inDrawingMode() {
		return this.displayMode instanceof DrawingModeAdapter;
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
			//TODO WHY SELECTEDOBJECTCOUNT??
			this.firePropertyChange(ViewController.OBJECT_SELECTION, null, selectedObjectCount);
			this.firePropertyChange(ViewController.DISPLAY_TOOL, null, null);
		}
	}
	
	private void selectObjects(Set<BigBangObject> newSelectedObjects) {
		this.displayObjects.selectObjects(newSelectedObjects);
		this.firePropertyChange(ViewController.OBJECT_SELECTION, null, newSelectedObjects.size());
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
			this.initDisplayAndJSynObjects((BigBangObjects)event.getNewValue());
		} else if (propertyName.equals(BigBangController.OBJECT_SELECTION)) {
			this.selectObjects((Set<BigBangObject>)event.getNewValue());
		} else if (propertyName.equals(BigBangController.LAYERS)) {
			this.firePropertyChange(ViewController.LAYERS, null, event.getNewValue());
			this.player.update();
		} else if (propertyName.equals(BigBangController.UNDO)) {
			this.firePropertyChange(ViewController.UNDO, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.REDO)) {
			this.firePropertyChange(ViewController.REDO, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.INPUT_ACTIVE)) {
			this.firePropertyChange(ViewController.INPUT_ACTIVE, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.DESELECT_COMPOSITION_STATES)) {
			this.firePropertyChange(ViewController.DESELECT_COMPOSITION_STATES, null, null);
		} else if (propertyName.equals(BigBangController.MODIFY_OPERATION)) {
			this.selectOperation((AbstractOperationEdit)event.getNewValue());
		}
	}
	
	private void initDisplayAndJSynObjects(BigBangObjects newObjects) {
		if (this.displayObjects == null || !this.displayObjects.getBaseForm().equals(newObjects.getBaseForm())) {
			if (newObjects.baseFormAllowsForSatellites()) {
				//do not select parameters for satellite and sibling number...
				this.viewParameters.initSelections(newObjects.getCoordinateSystemValueNames().size()-2);
			} else {
				this.viewParameters.initSelections(newObjects.getCoordinateSystemValueNames().size());
			}
			this.displayObjects = new DisplayObjects(newObjects);
			this.player.setScore(new JSynScore(new TreeSet<BigBangObject>(), newObjects.getBaseForm()));
			this.firePropertyChange(ViewController.FORM, null, this.displayObjects);
			this.firePropertyChange(ViewController.ACTIVE_COLIMIT_COORDINATE, null, this.displayObjects.getActiveColimitCoordinates());
		}
		this.addDisplayAndJSynObjects(newObjects);
	}
	
	private void addDisplayAndJSynObjects(BigBangObjects newObjects) {
		//System.out.println("DO " +newObjects.getObjects()+ " " +this.displayObjects.size());
		//System.out.println("DO " + newObjects.size()  + " " +this.displayObjects.size());
		Set<BigBangObject> lastObjects = newObjects.getObjectsAt(null);
		this.displayObjects.addObjects(lastObjects);
		this.player.setScore(new JSynScore(lastObjects, newObjects.getBaseForm()));
		
		//TODO UPDATE ALL THESE THINGS!!!
		this.viewParameters.setDenotatorMinAndMaxValues(newObjects.getMinValues(), newObjects.getMaxValues());
		this.firePropertyChange(ViewController.DISPLAY_OBJECTS, null, this.displayObjects);
		this.firePropertyChange(ViewController.STANDARD_DENOTATOR_VALUES, null, this.getStandardDenotatorValues());
		this.firePropertyChange(ViewController.MAX_SATELLITE_LEVEL, null, this.displayObjects.getMaxSatelliteLevelOfActiveObject());
		
		//not every sounding object makes sense to be played alone (e.g. modulator, or overtone)
		//this.playObject((Denotator)event.getNewValue());
	}
	
	/*private void removeDisplayObjects(Set<BigBangObject> removedObjects) {
		this.displayObjects.removeObjects(removedObjects);
		
		//TODO REMOVE JSYNOBJECTS!!! this.player.setScore(this.extractor.getJSynScore());
		
		//TODO UPDATE ALL THESE THINGS!!!
		this.viewParameters.setDenotatorMinAndMaxValues(minValues, maxValues);
		this.firePropertyChange(ViewController.DISPLAY_OBJECTS, null, this.displayObjects);
		this.firePropertyChange(ViewController.MAX_SATELLITE_LEVEL, null, this.displayObjects.getMaxSatelliteLevelOfActiveObject());
	}*/
	
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
	
	public void selectOperation(AbstractOperationEdit operation) {
		if (operation != null) {
			//select perspective first
			if (operation instanceof AbstractTransformationEdit) {
				AbstractTransformationEdit transformationEdit = (AbstractTransformationEdit)operation;
				this.viewParameters.setSelectedXYViewParameters(transformationEdit.getXYViewParameters());
				//TODO: center view?????
			
				//TODO: then select notes!!!
			}
			
			//then select displaymode and convert values!!
			if (operation instanceof AddObjectsEdit) {
				this.setDisplayMode(new DrawingModeAdapter(this.viewController));
			} else if (operation instanceof TranslationEdit) {
				double[] startingPoint = this.getXYDisplayValues(((TranslationEdit)operation).getStartingPoint());
				double[] endingPoint = this.getXYDisplayValues(((TranslationEdit)operation).getEndingPoint());
				this.setDisplayMode(new TranslationModeAdapter(this.viewController, startingPoint, endingPoint));
			} else if (operation instanceof AbstractLocalTransformationEdit) {
				AbstractLocalTransformationEdit localEdit = (AbstractLocalTransformationEdit)operation;
				double[] center = this.getXYDisplayValues(localEdit.getCenter());
				double[] endingPoint = this.getXYDisplayValues(localEdit.getEndingPoint());
				if (operation instanceof RotationEdit) {
					double[] startingPoint = ((RotationEdit)operation).getStartingPoint();
					double angle = ((RotationEdit)operation).getAngle();
					this.setDisplayMode(new RotationModeAdapter(this.viewController, center, startingPoint, endingPoint, angle));
				} else if (operation instanceof ScalingEdit) {
					double[] scaleFactors = ((ScalingEdit)operation).getScaleFactors();
					this.setDisplayMode(new ScalingModeAdapter(this.viewController, center, endingPoint, scaleFactors));
				} else if (operation instanceof ShearingEdit) {
					double[] shearingFactors = ((ShearingEdit)operation).getShearingFactors();
					this.setDisplayMode(new ShearingModeAdapter(this.viewController, center, endingPoint, shearingFactors));
				} else if (operation instanceof ReflectionEdit) {
					double[] reflectionVector = ((ReflectionEdit)operation).getReflectionVector();
					this.setDisplayMode(new ReflectionModeAdapter(this.viewController, center, reflectionVector));
				}
			}
			
			//then select operation if not the same as the selected one
			if (!operation.equals(this.selectedOperation)) {
				this.selectedOperation = operation;
				this.firePropertyChange(ViewController.SELECT_OPERATION, null, operation);
				this.controller.selectOperation(operation);
			}
		} else if (operation == null && this.selectedOperation != null) {
			this.deselectOperations();
		}
	}
	
	public void deselectOperations() {
		if (this.selectedOperation != null) {
			this.selectedOperation = null;
			this.clearDisplayTool();
			this.firePropertyChange(ViewController.SELECT_OPERATION, null, null);
			this.controller.deselectOperations();
		}
	}
	
	public void modifyCenterOfSelectedTransformation(Point2D.Double newCenter) {
		((AbstractTransformationEdit)this.selectedOperation).modifyCenter(this.getXYDenotatorValues(newCenter));
		this.controller.operationModified();
	}
	
	public void modifyEndPointOfSelectedTransformation(Point2D.Double newEndPoint) {
		//TODO not great: only used in translation
		((AbstractTransformationEdit)this.selectedOperation).modify(this.getXYDenotatorValues(newEndPoint));
		this.controller.operationModified();
	}
	
	public void modifySelectedTransformation(double[] newValues) {
		if (this.selectedOperation != null) {
			((AbstractTransformationEdit)this.selectedOperation).modify(newValues);
			this.controller.operationModified();
		}
	}
	
	public void modifyRotationAngle(Double angle) {
		((RotationEdit)this.selectedOperation).modifyAngle(angle);
		this.controller.operationModified();
	}
	
	public void modifyOperation(Integer operationIndex, Integer midiValue) {
		//ratio between 0 and 2
		double ratio = ((double)midiValue)/127*2;
		this.controller.modifyOperation(operationIndex, ratio);
	}
	
	public void translateSelectedObjects(Point2D.Double center, Point2D.Double endingPoint, Boolean copyAndTransform, Boolean startNewTransformation) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endingPoint, copyAndTransform, startNewTransformation);
		this.controller.translateObjects(properties);
	}
	
	public void rotateSelectedObjects(Point2D.Double center, Point2D.Double startPoint, Point2D.Double endPoint, Double angle, Boolean copyAndTransform, Boolean startNewTransformation) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, startNewTransformation);
		this.controller.rotateObjects(properties, new double[]{startPoint.x,startPoint.y}, angle);
	}
	
	public void scaleSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] scaleFactors, Boolean copyAndTransform, Boolean startNewTransformation) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, startNewTransformation);
		this.controller.scaleObjects(properties, scaleFactors);
	}
	
	public void reflectSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] reflectionVector, Boolean copyAndTransform, Boolean startNewTransformation) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, startNewTransformation);
		this.controller.reflectObjects(properties, reflectionVector);
	}
	
	public void shearSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] shearingFactors, Boolean copyAndTransform, Boolean startNewTransformation) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, startNewTransformation);
		this.controller.shearObjects(properties, shearingFactors);
	}
	
	public void affineTransformSelectedObjects(Point2D.Double center, Point2D.Double endPoint, double[] shift, RMatrix transform, Boolean copyAndTransform, Boolean startNewTransformation) {
		TransformationProperties properties = this.getLocalTransformationProperties(center, endPoint, copyAndTransform, startNewTransformation);
		this.controller.affineTransformObjects(properties, shift, transform);
	}
	
	public void shapeSelectedObjects(TreeMap<Integer,Integer> locations, Boolean copyAndTransform, Boolean startNewTransformation) {
		TransformationProperties properties = this.getTransformationProperties(copyAndTransform, startNewTransformation);
		this.controller.shapeObjects(properties, this.getXYDenotatorValues(locations));
	}
	
	public void addWallpaperDimension() {
		this.controller.addWallpaperDimension(this.displayObjects.getSelectedBigBangObjects(), 0, 5);
	}
	
	public void modifyWallpaperRange(Boolean rangeTo, Integer value) {
		((AddWallpaperDimensionEdit)this.selectedOperation).setRange(rangeTo, value);
		this.controller.operationModified();
	}
	
	public void addAlteration() {
		this.controller.addAlteration();
	}
	
	public void setAlterationComposition(Integer index) {
		Set<BigBangObject> selectedObjects = this.displayObjects.getSelectedBigBangObjects();
		if (this.selectedOperation instanceof AlterationEdit) {
			if (index == 0) {
				((AlterationEdit)this.selectedOperation).setForegroundComposition(selectedObjects);
			} else {
				((AlterationEdit)this.selectedOperation).setBackgroundComposition(selectedObjects);
			}
		}
	}
	
	public void setAlterationCoordinates(ArrayList<Integer> selectedCoordinates) {
		if (this.selectedOperation instanceof AlterationEdit) {
			List<DenotatorPath> alterationCoordinates = new ArrayList<DenotatorPath>();
			for (int currentIndex : selectedCoordinates) {
				alterationCoordinates.add(this.displayObjects.getActiveObjectValuePathAt(currentIndex));
			}
			((AlterationEdit)this.selectedOperation).setAlterationCoordinates(alterationCoordinates);
		}
	}
	
	private TransformationProperties getLocalTransformationProperties(Point2D.Double center, Point2D.Double endPoint, boolean copyAndTransform, boolean startNewTransformation) {
		//the end point is merely recorded for the display tool to be the same size....
		TransformationProperties properties = this.getTransformationProperties(copyAndTransform, startNewTransformation);
		double[] denotatorCenter = this.getXYDenotatorValues(center);
		double[] denotatorEndPoint = this.getXYDenotatorValues(endPoint);
		if (properties.getAnchor() != null) {
			/*if (this.selectedObjectsPaths != null) {
				properties.setAnchorNodePath(this.selectedObjectsPaths.getAnchorPath());
			}*/
			double[] anchorValues = this.getXYDenotatorValues(this.displayObjects.getSelectedAnchorCenter());
			denotatorCenter[0] -= anchorValues[0];
			denotatorCenter[1] -= anchorValues[1];
		}
		properties.setCenter(denotatorCenter);
		properties.setEndPoint(denotatorEndPoint);
		return properties;
	}
	
	private TransformationProperties getTransformationProperties(boolean copyAndTransform, boolean startNewTransformation) {
		//System.out.println(this.selectedObjectsPaths + " ... " + objectsPaths + " ... " + this.displayObjects.getSelectedObjectsPaths() + " ... " + this.displayObjects.getCategorizedSelectedObjectsPaths());
		Set<BigBangObject> selectedObjects = this.displayObjects.getSelectedBigBangObjects();
		BigBangObject selectedAnchor = this.displayObjects.getSelectedBigBangAnchor();
		List<TransformationPaths> valuePaths = this.getXYTransformationPaths();
		
		return new TransformationProperties(selectedObjects, selectedAnchor, valuePaths, copyAndTransform, startNewTransformation);
	}
	
	public void addObjects(ArrayList<PointND> locations, Boolean inPreviewMode) {
		if (this.displayObjects != null && this.displayMode instanceof DrawingModeAdapter) {
			List<Map<DenotatorPath,Double>> objectValues = new ArrayList<Map<DenotatorPath,Double>>();
			List<DenotatorPath> powersetPaths = new ArrayList<DenotatorPath>();
			for (PointND currentLocation : locations) {
				Map<DenotatorPath,Double> currentValues = this.displayObjects.getActiveObjectStandardValues(this.standardDenotatorValues);
				//TODO PUT THIS ENTIRE CLOSEST POWERSET LOGIC IN MODEL!!!!
				DenotatorPath currentPowersetPath = this.editObjectValuesAndFindClosestPowerset(currentLocation, currentValues);
				//only add object if there are some screen values to be converted
				if (!currentValues.isEmpty()) {
					objectValues.add(currentValues);
					powersetPaths.add(currentPowersetPath);
				}
			}
			//TODO REALLY BAD!!!
			if (this.selectedOperation instanceof AddObjectsEdit) {
				((AddObjectsEdit)this.selectedOperation).addObjects(objectValues, powersetPaths, inPreviewMode);
				this.controller.operationModified();
			} else {
				this.controller.addObjects(objectValues, powersetPaths, inPreviewMode);
			}
		}
	}
	
	public void deleteSelectedObjects() {
		Set<BigBangObject> objects = this.displayObjects.getSelectedBigBangObjects();
		if (objects.size() > 0) {
			this.controller.deleteObjects(objects);
		}
	}
	
	public void setObjectsOnLayerSelected(Integer layerIndex, Boolean selected) {
		this.displayObjects.setObjectsOnLayerSelected(layerIndex, selected);
		this.firePropertyChange(ViewController.LAYER_SELECTED, null, layerIndex);
		this.firePropertyChange(ViewController.OBJECT_SELECTION, null, this.displayObjects.getSelectedBigBangObjects().size());
	}
	
	public void addSelectedObjectsToNewLayer() {
		this.controller.addObjectsToNewLayer(this.displayObjects.getSelectedBigBangObjects());
	}
	
	public void addSelectedObjectsToLayer(Integer layerIndex) {
		this.controller.addObjectsToLayer(layerIndex, this.displayObjects.getSelectedBigBangObjects());
	}
	
	public void moveSelectedObjectsToNewLayer() {
		this.controller.moveObjectsToNewLayer(this.displayObjects.getSelectedBigBangObjects());
	}
	
	public void moveSelectedObjectsToLayer(Integer layerIndex) {
		this.controller.moveObjectsToLayer(layerIndex, this.displayObjects.getSelectedBigBangObjects());
	}
	
	public void addSelectedObjectsAsSatellitesTo(DisplayObject anchor, Integer powersetIndex) {
		Set<BigBangObject> objects = this.displayObjects.getSelectedBigBangObjects();
		this.controller.buildSatellites(objects, anchor.getBigBangObject(), powersetIndex);
	}
	
	public void flattenSelectedObjects() {
		Set<BigBangObject> objects = this.displayObjects.getSelectedBigBangObjects();
		this.controller.flattenObjects(objects);
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
	
	private DenotatorPath editObjectValuesAndFindClosestPowerset(PointND location, Map<DenotatorPath,Double> denotatorValues) {
		DenotatorPath parentPowersetPath = this.displayObjects.getActiveObjectAndLevelPowersetPath();
		int[] xyzParameters = this.viewParameters.getSelectedViewParameters();
		int xValueIndex = this.displayObjects.getActiveObjectValueIndex(xyzParameters[0]);
		int yValueIndex = this.displayObjects.getActiveObjectValueIndex(xyzParameters[1]);
		int zValueIndex = this.displayObjects.getActiveObjectValueIndex(xyzParameters[2]);
		double[] xyzDenotatorValues = new double[location.getDimension()];
		xyzDenotatorValues[0] = this.getDenotatorValue(location.getCoord(0), 0, this.displayPosition.x, this.xZoomFactor);
		xyzDenotatorValues[1] = this.getDenotatorValue(location.getCoord(1), 1, this.displayPosition.y, this.yZoomFactor);
		double averageZoom = 2;
		if (location.getDimension() > 2) {
			xyzDenotatorValues[2] = this.getDenotatorValue(location.getCoord(2), 2, 0, averageZoom); // TODO fix zoom and display position
		}
		if (xValueIndex >= 0) {
			this.replaceDenotatorValue(location.getCoord(0), xValueIndex, 0, this.displayPosition.x, this.xZoomFactor, denotatorValues);
		}
		if (yValueIndex >= 0 && (xValueIndex < 0 || yValueIndex != xValueIndex)) {
			this.replaceDenotatorValue(location.getCoord(1), yValueIndex, 1, this.displayPosition.y, this.yZoomFactor, denotatorValues);
		}
		if (location.getDimension() > 2 && zValueIndex >= 0 && (zValueIndex != yValueIndex && zValueIndex != xValueIndex)) {
			this.replaceDenotatorValue(location.getCoord(2), zValueIndex, 2, 0, averageZoom, denotatorValues); // parameters may be wrong
		}
		DenotatorPath closestPowersetPath = this.displayObjects.findClosestPowersetPath(xyzParameters, xyzDenotatorValues, parentPowersetPath);
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
	
	
	
	protected double getDenotatorValue(double displayValue, int parameterIndex, int position, double zoomFactor) {
		double value = (displayValue-position)/zoomFactor;
		if (parameterIndex == 2) {
			// TODO this should probably be handled by the view along with the x and y parameters.
			ViewParameter param = this.viewParameters.get(parameterIndex); 
			param.setDenotatorLimitsIfNotManual(param.getMinGoalValue(), param.getMaxGoalValue()); 
		}
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
	
	public void setSynthActive(Boolean synthActive) {
		this.player.setSynthActive(synthActive);
		this.firePropertyChange(ViewController.SYNTH_ACTIVE, null, synthActive);
	}
	
	public void setMidiActive(Boolean midiActive) {
		this.player.setMidiActive(midiActive);
		this.firePropertyChange(ViewController.MIDI_ACTIVE, null, midiActive);
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

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

import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.model.Model;
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
import org.rubato.rubettes.bigbang.view.subview.DisplayObjectList;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.DenotatorValueFinder;

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
	private List<Integer> selectedColimitCoordinates;
	private boolean satellitesConnected;
	protected DisplayObjectList displayNotes;
	private DisplayTool displayTool;
	//just used in preview mode
	private Set<DenotatorPath> selectedNotes;
	private DenotatorPath selectedAnchor;
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
		this.initSelectedColimitCoordinates(0);
		this.setDisplayNotes(new DisplayObjectList(viewController, null), new ArrayList<Double>(), new ArrayList<Double>());
		this.setSatellitesConnected(true);
		this.setDisplayMode(new DrawingModeAdapter(viewController));
		//TODO:make this automatic when displaynotes loaded!!! depending on max/min and window size
		this.setDisplayPosition(new Point(20, 560));
		this.setZoomFactors(5.0, 5.0);
		this.modFilterOn = false;
		this.modNumber = -1;
		this.wallpaperRanges = new ArrayList<Integer>();
	}
	
	public void addNewWindow() {
		Frame parentFrame = JOptionPane.getFrameForComponent(this.panel);
		new BigBangAdditionalView(this.controller, parentFrame);
		SelectedPaths paths = new SelectedPaths(this.displayNotes.getSelectedNodePaths(), this.displayNotes.getSelectedAnchorNodePath());
		this.controller.newWindowAdded(paths);
	}
	
	public void dispose() {
		this.controller.removeView(this);
	}
	
	private void initBigBangPlayer() {
		this.player = new BigBangPlayer();
		this.playingActive = false;
		this.setTempo(BigBangPlayer.INITIAL_BPM);
	}
	
	private void initViewMVC() {
		this.viewController = new ViewController();
		this.viewController.addModel(this);
		this.initViewParameters();
		this.initVisibleInterface();
		this.layerStates = new LayerStates(viewController);
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
	
	private void initSelectedColimitCoordinates(int numberOfColimits) {
		this.selectedColimitCoordinates = new ArrayList<Integer>();
		for (int i = 0; i < numberOfColimits; i++) {
			this.selectedColimitCoordinates.add(0);
		}
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
		this.playingActive = !this.playingActive;
		if (this.playingActive) {
			this.player.startPlaying();
		} else {
			this.player.stopPlaying();
		}
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
			this.setDisplayNotes(event, false);
		} else if (propertyName.equals(BigBangController.PREVIEW)) {
			this.setDisplayNotes(event, true);
		} else if (propertyName.equals(BigBangController.UNDO)) {
			this.firePropertyChange(ViewController.UNDO, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.REDO)) {
			this.firePropertyChange(ViewController.REDO, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.INPUT_ACTIVE)) {
			this.firePropertyChange(ViewController.INPUT_ACTIVE, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.ADD_OBJECT)) {
			this.playObject((Denotator)event.getNewValue());
		}
	}
	
	private void setDisplayNotes(PropertyChangeEvent event, boolean preview) {
		this.viewController.removeView(this.displayNotes);
		ScoreChangedNotification notification = (ScoreChangedNotification)event.getNewValue();
		DenotatorValueExtractor extractor = new DenotatorValueExtractor(this.viewController, notification, !preview, this.layerStates);
		DisplayObjectList newObjects = extractor.getDisplayObjects();
		if (this.modFilterOn) {
			newObjects.updateModulatorVisibility(this.modLevel, this.modNumber);
		}
		if (preview) {
			this.selectedNotes = notification.getNotesToBeSelected();
			this.selectedAnchor = notification.getAnchorToBeSelected();
		} else {
			//TODO: WHY??
			this.selectedNotes = null;
			this.selectedAnchor = null;
		}
		this.setDisplayNotes(newObjects, extractor.getMinValues(), extractor.getMaxValues());
		this.updatePlayerScore(extractor.getJSynScore(), notification.playback());
	}
	
	private void setDisplayNotes(DisplayObjectList displayNotes, List<Double> minValues, List<Double> maxValues) {
		if (this.displayNotes == null || (displayNotes.getBaseForm() != this.displayNotes.getBaseForm())) {
			if (displayNotes.containsPowerset()) {
				this.viewParameters.initSelections(displayNotes.getValueNames().size()-2);
			} else {
				this.viewParameters.initSelections(displayNotes.getValueNames().size());
			}
			this.initSelectedColimitCoordinates(displayNotes.getTopDenotatorColimits().size());
			this.firePropertyChange(ViewController.FORM, null, displayNotes);
			this.firePropertyChange(ViewController.SELECTED_COLIMIT_COORDINATE, null, this.selectedColimitCoordinates);
		}
		this.displayNotes = displayNotes;
		this.viewParameters.setDenotatorMinAndMaxValues(minValues, maxValues);
		//do not select parameters for satellite and sibling number...
		this.firePropertyChange(ViewController.DISPLAY_NOTES, null, this.displayNotes);
		this.firePropertyChange(ViewController.STANDARD_DENOTATOR_VALUES, null, this.getStandardDenotatorValues());
	}
	
	public void setStandardDenotatorValue(Integer index, Double value) {
		this.standardDenotatorValues.put(this.displayNotes.getValueNames().get(index), value);
		this.firePropertyChange(ViewController.STANDARD_DENOTATOR_VALUES, null, this.getStandardDenotatorValues());
	}
	
	public List<Double> getStandardDenotatorValues() {
		List<Double> values = new ArrayList<Double>();
		for (String currentValueName : this.displayNotes.getValueNames()) {
			if (this.standardDenotatorValues.containsKey(currentValueName)) {
				values.add(this.standardDenotatorValues.get(currentValueName));
			} else {
				values.add(null);
			}
		}
		return values;
	}
	
	public void setSelectedColimitCoordinate(Integer colimitIndex, Integer coordinateIndex) {
		List<ColimitForm> topDenotatorColimits = this.displayNotes.getTopDenotatorColimits();
		if (coordinateIndex >= 0 && topDenotatorColimits.size() > colimitIndex && topDenotatorColimits.get(colimitIndex).getForms().size() >= coordinateIndex) {
			this.selectedColimitCoordinates.set(colimitIndex, coordinateIndex);
			//set all ColimitForms impossible to reach to -1
			//TODO: does not account for forms that contain the same colimit several times
			Form coordinateForm = topDenotatorColimits.get(colimitIndex).getForm(coordinateIndex);
			List<ColimitForm> subColimits = new DenotatorValueFinder(coordinateForm, false).getColimitsFoundInOrder();
			for (int i = colimitIndex+1; i < topDenotatorColimits.size(); i++) {
				if (!subColimits.contains(topDenotatorColimits.get(i))) {
					this.selectedColimitCoordinates.set(i, -1);
				} else if (this.selectedColimitCoordinates.get(i) == -1) {
					this.selectedColimitCoordinates.set(i, 0);
				}
			}
			this.firePropertyChange(ViewController.SELECTED_COLIMIT_COORDINATE, null, this.selectedColimitCoordinates);
		}
	}
	
	public void selectTransformation(AbstractTransformationEdit edit) {
		this.selectedTransformation = edit;
		if (this.selectedTransformation != null) {
			//select perspective first
			this.viewParameters.setSelectedXYViewParameters(this.getXYViewParameters(edit.getValuePaths()));
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
			this.controller.startWallpaper(new ArrayList<DenotatorPath>(this.displayNotes.getSelectedNodePaths()));
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
		Set<DenotatorPath> nodePaths = this.displayNotes.getSelectedNodePaths();
		this.controller.setAlterationComposition(index, nodePaths);
	}
	
	private TransformationProperties getLocalTransformationProperties(Point2D.Double center, Point2D.Double endPoint, boolean copyAndTransform, boolean previewMode) {
		//the end point is merely recorded for the display tool to be the same size....
		TransformationProperties properties = this.getTransformationProperties(copyAndTransform, previewMode);
		double[] denotatorCenter = this.getXYDenotatorValues(center);
		double[] denotatorEndPoint = this.getXYDenotatorValues(endPoint);
		DenotatorPath anchorNodePath = this.displayNotes.getSelectedAnchorNodePath();
		if (anchorNodePath != null) {
			if (this.selectedNotes != null) {
				anchorNodePath = this.selectedAnchor;
			}
			properties.setAnchorNodePath(anchorNodePath);
			double[] anchorValues = this.getXYDenotatorValues(this.displayNotes.getSelectedAnchorNodeCenter());
			denotatorCenter[0] -= anchorValues[0];
			denotatorCenter[1] -= anchorValues[1];
		}
		properties.setCenter(denotatorCenter);
		properties.setEndPoint(denotatorEndPoint);
		return properties;
	}
	
	private TransformationProperties getTransformationProperties(boolean copyAndTransform, boolean previewMode) {
		Set<DenotatorPath> objectPaths;
		if (this.selectedNotes == null) {
			objectPaths = this.displayNotes.getSelectedNodePaths();
		} else {
			objectPaths = this.selectedNotes;
		}
		List<DenotatorPath> valuePaths = this.getXYValuePaths();
		return new TransformationProperties(objectPaths, valuePaths, copyAndTransform, previewMode, this.inWallpaperMode);
	}
	
	public void addObject(Point2D.Double location) {
		Map<DenotatorPath,Double> denotatorValues = this.getDenotatorValues(location);
		//only add object if there are some screen values to be converted
		if (denotatorValues.size() > 0) {
			this.controller.addObject(denotatorValues);
		}
	}
	
	public void deleteSelectedNotes() {
		List<DenotatorPath> nodePaths = new ArrayList<DenotatorPath>(this.displayNotes.getSelectedNodePaths());
		if (nodePaths.size() > 0) {
			this.controller.deleteNotes(nodePaths);
		}
	}
	
	public void copySelectedNotesTo(Integer layerIndex) {
		Set<DenotatorPath> nodePaths = this.displayNotes.getSelectedNodePaths();
		if (nodePaths.size() > 0) {
			this.controller.copyNotes(nodePaths, layerIndex);
		}
	}
	
	public void copySelectedNotesToNewLayer() {
		Set<DenotatorPath> nodePaths = this.displayNotes.getSelectedNodePaths();
		if (nodePaths.size() > 0) {
			this.controller.copyNotes(nodePaths, this.layerStates.size());
		}
	}
	
	public void moveSelectedNotesTo(Integer layerIndex) {
		Set<DenotatorPath> nodePaths = this.displayNotes.getSelectedNodePaths();
		if (nodePaths.size() > 0) {
			this.controller.moveNotes(nodePaths, layerIndex);
		}
	}
	
	public void moveSelectedNotesToNewLayer() {
		Set<DenotatorPath> nodePaths = this.displayNotes.getSelectedNodePaths();
		if (nodePaths.size() > 0) {
			this.controller.moveNotes(nodePaths, this.layerStates.size());
		}
	}
	
	public void addSelectedNotesAsSatellitesTo(DisplayObject parentNote) {
		Set<DenotatorPath> satelliteNodePaths = this.displayNotes.getSelectedNodePaths();
		DenotatorPath parentNodePath = parentNote.getTopDenotatorPath();
		this.controller.buildSatellites(satelliteNodePaths, parentNodePath);
	}
	
	public void flattenSelectedNotes() {
		Set<DenotatorPath> nodePaths = this.displayNotes.getSelectedNodePaths();
		this.controller.flattenNotes(nodePaths);
	}
	
	/*public void addSelectedNotesAsModulatorsTo(DisplayObject parentNote) {
		Set<NotePath> modulatorNodePaths = this.displayNotes.getSelectedNodePaths();
		NotePath parentNodePath = parentNote.getOriginalPath();
		this.controller.addAsModulators(modulatorNodePaths, parentNodePath);
	}*/
	
	public void removeSelectedNotesFromCarrier() {
		Set<DenotatorPath> nodePaths = this.displayNotes.getSelectedNodePaths();
		this.controller.removeNotesFromCarrier(nodePaths);
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
	
	private Map<DenotatorPath,Double> getDenotatorValues(Point2D.Double location) {
		int XValueIndex = this.viewParameters.getValueIndex(0);
		int YValueIndex = this.viewParameters.getValueIndex(1);
		Map<DenotatorPath,Double> denotatorValues = this.displayNotes.getTopDenotatorStandardValues(this.standardDenotatorValues, this.selectedColimitCoordinates);
		if (XValueIndex >= 0 && YValueIndex >= 0) {
			this.replaceDenotatorValue(location.x, XValueIndex, 0, this.displayPosition.x, this.xZoomFactor, denotatorValues);
			if (YValueIndex != XValueIndex) {
				this.replaceDenotatorValue(location.y, YValueIndex, 1, this.displayPosition.y, this.yZoomFactor, denotatorValues);
			}
		} else if (YValueIndex < 0) {
			this.replaceDenotatorValue(location.x, XValueIndex, 0, this.displayPosition.x, this.xZoomFactor, denotatorValues);
		} else {
			this.replaceDenotatorValue(location.y, YValueIndex, 1, this.displayPosition.y, this.yZoomFactor, denotatorValues);
		}
		return denotatorValues;
	}
	
	private void replaceDenotatorValue(double displayValue, int valueIndex, int parameterIndex, int position, double zoomFactor, Map<DenotatorPath,Double> values) {
		if (valueIndex > -1) {
			DenotatorPath associatedPath = this.displayNotes.getPathInTopDenotatorValues(valueIndex);
			//null happens when satellite or sibling level is selected
			if (associatedPath != null && this.displayNotes.pathInAllowedColimitBranch(associatedPath, this.selectedColimitCoordinates)) {
				values.put(associatedPath, this.getDenotatorValue(displayValue, parameterIndex, position, zoomFactor));
			}
		}
	}
	
	protected double getDenotatorValue(double displayValue, int parameterIndex, int position, double zoomFactor) {
		double value = (displayValue-position)/zoomFactor;
		return this.viewParameters.get(parameterIndex).translateDisplayValue(value);
	}
	
	protected double getDisplayValue(double denotatorValue, int parameterIndex, int position, double zoomFactor) {
		double displayValue = this.viewParameters.get(parameterIndex).translateDenotatorValue(denotatorValue);
		return (displayValue*zoomFactor)+position;
	}
	
	private List<DenotatorPath> getXYValuePaths() {
		List<DenotatorPath> denotatorPaths = new ArrayList<DenotatorPath>();
		for (int i = 0; i < 4; i++) {
			int selectedViewParameter = this.viewParameters.getValueIndex(i%2);
			//only one parameter might be selected...
			if (selectedViewParameter >= 0) {
				denotatorPaths.add(this.displayNotes.getPathInTopDenotatorValues(selectedViewParameter));
			} else {
				denotatorPaths.add(null);
			}
		}
		return denotatorPaths;
	}
	
	private int[] getXYViewParameters(List<DenotatorPath> denotatorPaths) {
		int[] viewParameters = new int[2];
		List<DenotatorPath> topDenotatorValuePaths = this.displayNotes.getTopDenotatorValuePaths();
		for (int i = 0; i <= 1; i++) {
			for (int j = 0; j < topDenotatorValuePaths.size(); j++) {
				if (denotatorPaths.get(i).equals(topDenotatorValuePaths.get(j))) {
					viewParameters[i] = j;
				}
			}
		}
		return viewParameters;
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
	
	private void playObject(Denotator object) {
		if (this.playingActive) {
			DenotatorValueExtractor extractor = new DenotatorValueExtractor(object);
			this.player.playObject(extractor.getJSynScore().getObjects().get(0));
		}
	}
	
	public void setTempo(Integer tempo) {
		this.player.setTempo(tempo);
		if (this.playingActive) {
			this.togglePlayMode();
		}
		this.firePropertyChange(ViewController.TEMPO, null, tempo);
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

package org.rubato.rubettes.bigbang.view.model;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.BigBangObjects;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractLocalTransformation;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.model.operations.AbstractTransformation;
import org.rubato.rubettes.bigbang.model.operations.AddObjectsOperation;
import org.rubato.rubettes.bigbang.model.operations.AddWallpaperDimensionOperation;
import org.rubato.rubettes.bigbang.model.operations.AlterationOperation;
import org.rubato.rubettes.bigbang.model.operations.ReflectionTransformation;
import org.rubato.rubettes.bigbang.model.operations.RotationTransformation;
import org.rubato.rubettes.bigbang.model.operations.ScalingTransformation;
import org.rubato.rubettes.bigbang.model.operations.ShearingTransformation;
import org.rubato.rubettes.bigbang.model.operations.TranslationTransformation;
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
import org.rubato.rubettes.util.Point;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.PointND;
import org.rubato.rubettes.util.Rectangle2D;

public class BigBangSwingView extends BigBangView {
	
	protected BigBangPlayer player;
	protected BigBangRecorder recorder;
	protected BigBangMidiReceiver midiReceiver;
	protected BigBangMidiTransmitter midiTransmitter;
	protected JBigBangPanel panel;
	private DisplayModeAdapter displayMode;
	
	public BigBangSwingView(BigBangController controller) {
		super(controller);
		//this.controller = controller;
		//this.controller.addView(this);
		
		//this.initViewMVC();
		//this.initViewParameterControls();
		//this.initStandardDenotatorValues();
		//this.setSatellitesConnected(true);
		this.setDisplayMode(new DrawingModeAdapter(this.viewController));
		//TODO:make this automatic when displaynotes loaded!!! depending on max/min and window size
		this.setDisplayPosition(new Point(20, 660));
		this.setZoomFactors(5.0, 5.0);
		this.initMidiAndSound();
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
	
	@Override
	protected void initPlayer() {
		this.player = new BigBangPlayer();
	}
	
	@Override
	protected void initVisibleInterface() {
		this.panel = new JBigBangPanel(this.viewController, this.controller, this.factsViewState.getViewParameters());
	}
	
	private void initMidiAndSound() {
		this.midiReceiver = new BigBangMidiReceiver(this.viewController, this.controller);
		this.recorder = new BigBangRecorder(this, this.player, this.controller);
		this.setTempo(BigBangPlayer.INITIAL_BPM);
		this.firePropertyChange(ViewController.MIDI_IN, null, this.midiReceiver.getSelectedInDeviceName());
		this.firePropertyChange(ViewController.MIDI_OUT, null, this.player.getSelectedMidiOutDeviceName());
	}
	
	public void setDisplayPosition(Point position) {
		//Point oldValue = this.displayPosition;
		//System.out.println("dp " + position);
		this.firePropertyChange(ViewController.DISPLAY_POSITION, null, position);
	}
	
	public void setZoomFactors(Double xZoomFactor, Double yZoomFactor) {
		double[] newValue = new double[]{xZoomFactor, yZoomFactor};
		//System.out.println("zf " + this.xZoomFactor + " " + this.yZoomFactor);
		this.firePropertyChange(ViewController.ZOOM_FACTORS, null, newValue);
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
	
	public void toggleObjectSelection(Point2D location) {
		int selectedObjectCount = this.displayObjects.selectTopOrDeselectAllObjects(location);
		this.firePropertyChange(ViewController.OBJECT_SELECTION, null, selectedObjectCount);
	}
	
	public void toggleAnchorObjectSelection(Point2D location) {
		this.displayObjects.selectOrDeselectAnchorObject(location);
		this.firePropertyChange(ViewController.ANCHOR_OBJECT_SELECTION, null, null);
	}
	
	public void selectObjects(SelectionTool tool, Boolean stillSelecting) {
		this.selectObjects(tool.getArea(), tool, stillSelecting);
	}
	
	protected void selectObjects(Rectangle2D area, SelectionTool tool, boolean stillSelecting) {
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
			if (this.displayObjects != null) {
				this.displayObjects.updateObjectSelections();
				this.firePropertyChange(ViewController.DISPLAY_OBJECTS, null, this.displayObjects);
				this.player.updatePerformances();
			}
		} else if (propertyName.equals(BigBangController.UNDO)) {
			this.firePropertyChange(ViewController.UNDO, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.REDO)) {
			this.firePropertyChange(ViewController.REDO, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.INPUT_ACTIVE)) {
			this.firePropertyChange(ViewController.INPUT_ACTIVE, null, event.getNewValue());
		} else if (propertyName.equals(BigBangController.MODIFY_OPERATION)) {
			this.selectOperation((AbstractOperation)event.getNewValue());
		}
	}
	
	protected void initDisplayAndJSynObjects(BigBangObjects newObjects) {
		if (this.displayObjects == null || !this.displayObjects.getBaseForm().equals(newObjects.getBaseForm())) {
			if (newObjects.baseFormAllowsForSatellites()) {
				//do not select parameters for satellite and sibling number...
				this.factsViewState.getViewParameters().initSelections(newObjects.getCoordinateSystemValueNames().size()-2);
			} else {
				this.factsViewState.getViewParameters().initSelections(newObjects.getCoordinateSystemValueNames().size());
			}
			this.displayObjects = new DisplayObjects(newObjects);
			this.player.setScore(new JSynScore(new TreeSet<BigBangObject>(), newObjects.getBaseForm(), newObjects.arePlayedBackInTime()));
			this.firePropertyChange(ViewController.FORM, null, this.displayObjects);
			this.firePropertyChange(ViewController.ACTIVE_COLIMIT_COORDINATE, null, this.displayObjects.getActiveColimitCoordinates());
		}
		this.addDisplayAndJSynObjects(newObjects);
	}
	
	private void addDisplayAndJSynObjects(BigBangObjects newObjects) {
		//System.out.println("DO " +newObjects.getAllObjects()+ " " +this.displayObjects.size());
		//System.out.println("DO " + newObjects.size()  + " " +this.displayObjects.size());
		Set<BigBangObject> lastObjects = newObjects.getObjectsAt(null);
		this.displayObjects.addObjects(lastObjects);
		this.player.setScore(new JSynScore(lastObjects, newObjects.getBaseForm(), newObjects.arePlayedBackInTime()));
		
		//TODO UPDATE ALL THESE THINGS!!!
		this.factsViewState.getViewParameters().setDenotatorMinAndMaxValues(newObjects.getMinValues(), newObjects.getMaxValues());
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
	
	private List<Double> getStandardDenotatorValues() {
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
	
	public void selectPreviousCompositionState() {
		this.controller.selectPreviousCompositionState();
	}
	
	public void selectNextCompositionState() {
		this.controller.selectNextCompositionState();
	}
	
	public void selectCompositionState(CompositionState state) {
		this.controller.selectCompositionState(state);
	}
	
	public void selectCompositionState(Integer stateIndex) {
		this.controller.selectCompositionState(stateIndex);
	}
	
	public void deselectCompositionStates() {
		this.controller.deselectCompositionStates();
	}
	
	public void selectOperation(AbstractOperation operation) {
		if (operation != null) {
			//select perspective first
			if (operation instanceof AbstractTransformation) {
				AbstractTransformation transformationEdit = (AbstractTransformation)operation;
				this.factsViewState.getViewParameters().setSelectedXYViewParameters(transformationEdit.getXYViewParameters());
				//TODO: center view?????
			
				//TODO: then select notes!!!
			}
			
			//then select displaymode and convert values!!
			if (operation instanceof AddObjectsOperation) {
				this.setDisplayMode(new DrawingModeAdapter(this.viewController));
			} else if (operation instanceof TranslationTransformation) {
				double[] startingPoint = this.panel.getXYDisplayValues(((TranslationTransformation)operation).getStartingPoint());
				double[] endingPoint = this.panel.getXYDisplayValues(((TranslationTransformation)operation).getEndingPoint());
				this.setDisplayMode(new TranslationModeAdapter(this.viewController, startingPoint, endingPoint));
			} else if (operation instanceof AbstractLocalTransformation) {
				AbstractLocalTransformation localEdit = (AbstractLocalTransformation)operation;
				double[] center = this.panel.getXYDisplayValues(localEdit.getCenter());
				double[] endingPoint = this.panel.getXYDisplayValues(localEdit.getEndingPoint());
				if (operation instanceof RotationTransformation) {
					double[] startingPoint = ((RotationTransformation)operation).getStartingPoint();
					double angle = ((RotationTransformation)operation).getAngle();
					this.setDisplayMode(new RotationModeAdapter(this.viewController, center, startingPoint, endingPoint, angle));
				} else if (operation instanceof ScalingTransformation) {
					double[] scaleFactors = ((ScalingTransformation)operation).getScaleFactors();
					this.setDisplayMode(new ScalingModeAdapter(this.viewController, center, endingPoint, scaleFactors));
				} else if (operation instanceof ShearingTransformation) {
					double[] shearingFactors = ((ShearingTransformation)operation).getShearingFactors();
					this.setDisplayMode(new ShearingModeAdapter(this.viewController, center, endingPoint, shearingFactors));
				} else if (operation instanceof ReflectionTransformation) {
					double[] reflectionVector = ((ReflectionTransformation)operation).getReflectionVector();
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
	
	public void modifySelectedTransformation(double[] newValues) {
		if (this.selectedOperation != null) {
			((AbstractTransformation)this.selectedOperation).modify(newValues);
			this.controller.operationModified();
		}
	}
	
	public void modifyRotationAngle(Double angle) {
		((RotationTransformation)this.selectedOperation).modifyAngle(angle);
		this.controller.operationModified();
	}
	
	public void modifyOperation(Integer operationIndex, Integer midiValue) {
		//ratio between 0 and 2
		double ratio = ((double)midiValue)/127*2;
		this.controller.modifyOperation(operationIndex, ratio);
	}
	
	public void addWallpaperDimension() {
		this.controller.addWallpaperDimension(this.displayObjects.getSelectedBigBangObjects(), 0, 5);
	}
	
	public void modifyWallpaperRange(Boolean rangeTo, Integer value) {
		((AddWallpaperDimensionOperation)this.selectedOperation).setRange(rangeTo, value);
		this.controller.operationModified();
	}
	
	public void setAlterationComposition(Integer index) {
		Set<BigBangObject> selectedObjects = this.displayObjects.getSelectedBigBangObjects();
		if (this.selectedOperation instanceof AlterationOperation) {
			if (index == 0) {
				((AlterationOperation)this.selectedOperation).setForegroundComposition(selectedObjects);
			} else {
				((AlterationOperation)this.selectedOperation).setBackgroundComposition(selectedObjects);
			}
		}
	}
	
	public void setAlterationCoordinates(ArrayList<Integer> selectedCoordinates) {
		if (this.selectedOperation instanceof AlterationOperation) {
			List<DenotatorPath> alterationCoordinates = new ArrayList<DenotatorPath>();
			for (int currentIndex : selectedCoordinates) {
				alterationCoordinates.add(this.displayObjects.getActiveObjectValuePathAt(currentIndex));
			}
			((AlterationOperation)this.selectedOperation).setAlterationCoordinates(alterationCoordinates);
		}
	}
	
	//also in ViewParameters. this one just updates alteration
	public void setSelectedViewParameters(int[] newSelections) {
		this.controller.setAlterationDegreesDimension(this.displayObjects.getActiveObjectValuePathAt(newSelections[0]));
	}
	
	public void deleteSelectedObjects() {
		Set<BigBangObject> objects = this.displayObjects.getSelectedBigBangObjects();
		if (objects.size() > 0) {
			this.controller.deleteObjects(objects);
			this.displayObjects.deselectAllObjects();
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
	
	public void setIsLooping(Boolean isLooping) {
		this.player.setIsLooping(isLooping);
		this.firePropertyChange(ViewController.IS_LOOPING, null, isLooping);
	}
	
	public void setTempo(Integer tempo) {
		this.player.setTempo(tempo);
		this.firePropertyChange(ViewController.TEMPO, null, tempo);
	}
	
	public void setPlaybackPosition(Point2D location) {
		int timeAxisIndex = this.displayObjects.getTimeAxisIndex(this.factsViewState.getViewParameters());
		if (timeAxisIndex != -1) {
			double[] xyDenotatorValues = this.factsViewState.getXYZDenotatorValues(new PointND(location));
			this.player.setPlaybackPosition(xyDenotatorValues[timeAxisIndex]);
		}
	}
	
	public void setWaveform(String waveform) {
		this.player.setWaveform(waveform);
		this.firePropertyChange(ViewController.WAVEFORM, null, waveform);
	}
	
	public void setMidiIn(String inDeviceName) {
		this.midiReceiver.setSelectedInDevice(inDeviceName);
	}
	
	/*public void setMidiOut(String outDeviceName) {
		this.player.setMidiOutDevice(outDeviceName);
	}*/
	
	public void pressMidiKey(Integer channel, Integer pitch, Integer velocity) {
		if (channel < 0) {
			channel = this.getChannel();
		}
		if (velocity < 0) {
			velocity = this.getVelocity();
		}
		if (this.recorder.isRecording()) {
			this.recorder.pressMidiKey(channel, pitch, velocity);
			this.player.pressMidiKey(channel, pitch, velocity, true);
		} else {
			this.player.pressMidiKey(channel, pitch, velocity, false);
		}
	}
	
	public void releaseMidiKey(Integer channel, Integer pitch) {
		if (channel < 0) {
			channel = this.getChannel();
		}
		if (this.recorder.isRecording()) {
			this.recorder.releaseMidiKey(channel, pitch);
			this.player.releaseMidiKey(channel, pitch, true);
		} else {
			this.player.releaseMidiKey(channel, pitch, false);
		}
	}
	
	private int getVelocity() {
		return this.standardDenotatorValues.get(CoolFormRegistrant.LOUDNESS_NAME).intValue();
	}
	
	private int getChannel() {
		if (this.standardDenotatorValues.containsKey(CoolFormRegistrant.VOICE_NAME)) {
			return this.standardDenotatorValues.get(CoolFormRegistrant.VOICE_NAME).intValue();
		}
		return 0;
	}
	
	public void changeOctave(Boolean up) {
		if (!this.recorder.isRecording()) {
			this.player.transposeAllScoreVersionsByOctave(up);
		}
	}
	
	public void changeVelocity(Integer velocity) {
		this.player.changeVelocity(velocity);
	}
	
	public void changeRateModifier(Double rateModifier) {
		this.player.changeRate(rateModifier);
	}

}

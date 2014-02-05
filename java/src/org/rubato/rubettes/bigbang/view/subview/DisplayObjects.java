package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.BigBangObjects;
import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.util.DenotatorObject;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.FormValueFinder;
import org.rubato.rubettes.util.PerformanceCheck;

public class DisplayObjects {
	
	private BigBangObjects bbObjects;
	private Map<BigBangObject,DisplayObject> objectMap;
	//objects contains only objects currently present. has to be taken as a reference for selectedObjects 
	private TreeSet<DisplayObject> objects;
	//selectedObjects also contains objects currently not present that were selected, e.g. during animation 
	private Set<DisplayObject> selectedObjects;
	//selectedAnchor might also not be present currently
	private DisplayObject selectedAnchor;
	
	//active object is the one selected for being drawn etc
	private int indexOfActiveObjectType;
	private List<Integer> activeColimitCoordinates;
	private int activeSatelliteLevel;
	
	public DisplayObjects(BigBangObjects bbObjects) {
		PerformanceCheck.startTask("initDO");
		this.bbObjects = bbObjects;
		this.indexOfActiveObjectType = 0;
		this.initActiveColimitCoordinates(this.bbObjects.getNumberOfColimits(0));
		this.activeSatelliteLevel = 0;
		this.initObjects();
	}
	
	private void initActiveColimitCoordinates(int numberOfColimits) {
		this.activeColimitCoordinates = new ArrayList<Integer>();
		for (int i = 0; i < numberOfColimits; i++) {
			this.activeColimitCoordinates.add(0);
		}
	}
	
	private void initObjects() {
		this.objectMap = new HashMap<BigBangObject,DisplayObject>();
		this.objects = new TreeSet<DisplayObject>();
		this.selectedObjects = new TreeSet<DisplayObject>();
		this.addObjects(this.bbObjects.getObjects());
	}
	
	public void addObjects(Set<BigBangObject> newObjects) {
		PerformanceCheck.startTask("addDO");
		//reset object since they might temporarily not exist (e.g. during animation)
		this.objects = new TreeSet<DisplayObject>();
		for (BigBangObject currentObject : newObjects) {
			if (!this.objectMap.containsKey(currentObject)) {
				//create and add new objects
				DisplayObject newObject = new DisplayObject(currentObject);
				this.objects.add(newObject);
				this.objectMap.put(currentObject, newObject);
			} else {
				//add objects that existed before
				if (currentObject.getTopDenotatorPath() != null) {
					this.objects.add(this.objectMap.get(currentObject));
				}
			}
		}
	}
	
	public void removeObjects(Set<BigBangObject> removedObjects) {
		for (BigBangObject currentObject : removedObjects) {
			this.objects.remove(currentObject);
			this.objectMap.remove(currentObject);
		}
	}
	
	public void setIndexOfActiveObjectType(int indexOfActiveObjectType) {
		this.indexOfActiveObjectType = indexOfActiveObjectType;
	}
	
	public List<Integer> getActiveColimitCoordinates() {
		return this.activeColimitCoordinates;
	}
	
	public void setActiveColimitCoordinate(int colimitIndex, int coordinateIndex) {
		List<ColimitForm> activeObjectTypeColimits = this.getActiveObjectType().getColimits();
		if (coordinateIndex >= 0 && activeObjectTypeColimits.size() > colimitIndex && activeObjectTypeColimits.get(colimitIndex).getForms().size() >= coordinateIndex) {
			this.activeColimitCoordinates.set(colimitIndex, coordinateIndex);
			//set all ColimitForms impossible to reach to -1
			//TODO: does not account for forms that contain the same colimit several times
			Form coordinateForm = activeObjectTypeColimits.get(colimitIndex).getForm(coordinateIndex);
			List<ColimitForm> subColimits = new FormValueFinder(coordinateForm, false).getObjectAt(this.indexOfActiveObjectType).getColimits();
			for (int i = colimitIndex+1; i < activeObjectTypeColimits.size(); i++) {
				if (!subColimits.contains(activeObjectTypeColimits.get(i))) {
					this.activeColimitCoordinates.set(i, -1);
				} else if (this.activeColimitCoordinates.get(i) == -1) {
					this.activeColimitCoordinates.set(i, 0);
				}
			}
		}
	}
	
	public Form getBaseForm() {
		return this.bbObjects.getBaseForm();
	}
	
	public boolean baseFormAllowsForSatellites() {
		return this.bbObjects.baseFormAllowsForSatellites();
	}
	
	public Integer getMaxSatelliteLevelOfActiveObject() {
		return this.bbObjects.getMaxSatelliteLevel(this.indexOfActiveObjectType);
	}
	
	public void setActiveSatelliteLevel(int satelliteLevel) {
		this.activeSatelliteLevel = satelliteLevel;
	}
	
	public List<String> getCoordinateSystemValueNames() {
		return this.bbObjects.getCoordinateSystemValueNames();
	}
	
	public int getInstanceNumberOfCoordinateValueName(int coordinateSystemValueIndex) {
		return this.bbObjects.getInstanceNumberOfCoordinateValueName(coordinateSystemValueIndex);
	}
	
	public int getNumberOfNonAnalyticalCoordinateSystemValues() {
		return this.bbObjects.getNumberOfNonAnalyticalCoordinateSystemValues();
	}
	
	private List<DenotatorPath> getActiveObjectValuePaths() {
		return this.getActiveObjectType().getColimitConfigurationValuePaths(this.activeColimitCoordinates);
	}
	
	public DenotatorPath getActiveObjectValuePathAt(int valueIndex) {
		return this.getActiveObjectValuePaths().get(valueIndex);
	}
	
	public DenotatorObject getActiveObjectType() {
		return this.bbObjects.getObjectType(this.indexOfActiveObjectType);
	}
	
	public DenotatorPath getActiveObjectAndLevelPowersetPath() {
		DenotatorPath activeObjectPath = this.getActiveObjectType().getPath();
		return activeObjectPath.getPowersetPath(this.activeSatelliteLevel, activeObjectPath.getEndForm());
	}
	
	/**
	 * @return the top denotator standard values under assumption that the given value
	 * is selected in a colimit. if it is not in a colimit, just returns the standard values
	 */
	public Map<DenotatorPath,Double> getActiveObjectStandardValues(Map<String,Double> standardDenotatorValues) {
		Map<DenotatorPath,Double> objectStandardValues = new TreeMap<DenotatorPath,Double>();
		List<String> activeObjectValueNames = this.getActiveObjectType().getColimitConfigurationValueNames(this.activeColimitCoordinates);
		List<DenotatorPath> activeObjectValuePaths = this.getActiveObjectType().getColimitConfigurationValuePaths(this.activeColimitCoordinates);
		
		for (int i = 0; i < activeObjectValueNames.size(); i++) {
			String currentName = activeObjectValueNames.get(i);
			if (standardDenotatorValues.containsKey(currentName)) {
				objectStandardValues.put(activeObjectValuePaths.get(i), standardDenotatorValues.get(currentName));
			}
		}
		return objectStandardValues;
	}
	
	public int getActiveObjectValueIndex(int coordinateSystemValueIndex) {
		return this.bbObjects.getObjectValueIndex(coordinateSystemValueIndex, this.indexOfActiveObjectType, this.activeColimitCoordinates);
	}
	
	public int getActiveObjectFirstValueIndex(SimpleForm form) {
		return this.bbObjects.getObjectFirstValueIndex(form, this.indexOfActiveObjectType, this.activeColimitCoordinates);
	}
	
	public List<List<DenotatorPath>> getAllObjectConfigurationsValuePathsAt(int coordinateSystemValueIndex) {
		return this.bbObjects.getAllObjectConfigurationsValuePathsAt(coordinateSystemValueIndex);
	}
	
	public DenotatorPath findClosestPowersetPath(int[] coordinateSystemValueIndices, double[] values, DenotatorPath examplePowersetPath) {
		if (examplePowersetPath != null) {
			BigBangObject closestObject = this.bbObjects.getClosestObject(coordinateSystemValueIndices, values, examplePowersetPath);
			if (closestObject != null) {
				return closestObject.getTopDenotatorPath().getDescendantPathAccordingTo(examplePowersetPath);
			}
		}
		return examplePowersetPath;
	}
	
	public List<Form> getObjectTypes() {
		return this.bbObjects.getObjectTypes();
	}
	
	private DisplayObject getParent(DisplayObject object) {
		BigBangObject parent = object.getBigBangObject().getParent();
		if (parent != null) {
			return this.objectMap.get(parent);
		}
		return null;
	}
	
	private Set<DisplayObject> getChildren(DisplayObject object) {
		Set<DisplayObject> children = new TreeSet<DisplayObject>();
		Set<BigBangObject> bbChildren = object.getBigBangObject().getChildren();
		for (BigBangObject currentBBObject : object.getBigBangObject().getChildren()) {
			children.add(this.objectMap.get(currentBBObject));
		}
		return children;
	}
	
	
	//------------------------
	
	public void setDisplay(DisplayContents display) {
		for (DisplayObject currentObject : this.objects) {
			currentObject.setDisplay(display);
		}
	}
	
	public void tempSelectObjects(Rectangle2D.Double area) {
		for (DisplayObject currentObject: this.objects) {
			if (!this.selectedObjects.contains(currentObject)) {
				currentObject.setSelected(currentObject.intersects(area));
			}
		}
	}
	
	public int selectObjects(Rectangle2D.Double area) {
		for (DisplayObject currentObject: this.objects) {
			if (currentObject.intersects(area)) {
				this.selectObject(currentObject);
			}
		}
		return this.selectedObjects.size();
	}
	
	private void toggleSelected(DisplayObject note) {
		if (note.isSelected()) {
			this.deselectObject(note);
		} else {
			this.selectObject(note);
		}
	}
	
	public void selectObject(DisplayObject note) {
		if (this.isNotSelectedAnchorObject(note)) {
			note.setSelected(true);
			if (!this.selectedObjects.contains(note) && note.isActive()) {
				this.selectedObjects.add(note);
				this.deselectParents(note);
				this.deselectChildren(note);
			}
		}
	}
	
	private void deselectObject(DisplayObject note) {
		note.setSelected(false);
		this.selectedObjects.remove(note);
	}
	
	private boolean isNotSelectedAnchorObject(DisplayObject note) {
		return (this.selectedAnchor != null && !this.selectedAnchor.equals(note))
			|| this.selectedAnchor == null;
	}
	
	private void deselectParents(DisplayObject object) {
		DisplayObject parent = this.getParent(object); 
		if (parent != null) {
			this.deselectObject(parent);
			this.deselectParents(parent);
		}
	}
	
	private void deselectChildren(DisplayObject object) {
		for (DisplayObject currentChild: this.getChildren(object)) {
			this.deselectObject(currentChild);
			this.deselectChildren(currentChild);
		}
	}
	
	public int selectTopOrDeselectAllObjects(Point location) {
		//notes are saved from bottom to top... just takes one note
		for (DisplayObject currentObject: this.objects) {
			if (currentObject.getRectangle().contains(location)) {
				this.toggleSelected(currentObject);
				return this.selectedObjects.size();
			}
		}
		this.deselectAllObjects();
		return this.selectedObjects.size();
	}
	
	private void deselectAllObjects() {
		for (DisplayObject currentObject: this.selectedObjects) {
			currentObject.setSelected(false);
		}
		this.selectedObjects = new TreeSet<DisplayObject>();
	}
	
	public void deactivateSelectedObjects() {
		for (DisplayObject currentObject : this.selectedObjects) {
			currentObject.setActive(false);
		}
	}
	
	public void activateAllObjects() {
		for (DisplayObject currentObject : this.objects) {
			currentObject.setActive(true);
		}
	}
	
	public DisplayObject getObjectAt(Point location) {
		return this.getObjectAt(location, this.objects);
	}
	
	public boolean hasSelectedObjectAt(Point location) {
		//TODO rethink, selectedObjects might contain one that objects doesn't
		return this.getObjectAt(location, this.selectedObjects) != null;
	}
	
	private DisplayObject getObjectAt(Point location, Set<DisplayObject> objects) {
		for (DisplayObject currentObject : objects) {
			if (currentObject.getRectangle().contains(location)) {
				return currentObject;
			}
		}
		return null;
	}
	
	public void selectOrDeselectAnchorObject(Point location) {
		DisplayObject noteInLocation = this.getObjectAt(location);
		if (noteInLocation != null) {
			if (noteInLocation.equals(this.selectedAnchor)) {
				this.selectedAnchor = null;
			} else if (noteInLocation.getBigBangObject().hasChildren()) {
				this.setSelectedAnchor(noteInLocation);
			}
		} else {
			this.selectedAnchor = null;
		}
	}
	
	public void setSelectedAnchor(DisplayObject object) {
		this.selectedAnchor = object;
		this.selectedObjects.remove(object);
	}
	
	public Point2D.Double getSelectedAnchorCenter() {
		if (this.selectedAnchor != null && this.objects.contains(this.selectedAnchor)) {
			return this.selectedAnchor.getLocation();
		}
		return null;
	}
	
	public Set<BigBangObject> getSelectedBigBangObjects() {
		Set<BigBangObject> selectedObjects = new TreeSet<BigBangObject>();
		for (DisplayObject currentObject : this.selectedObjects) {
			if (this.objects.contains(currentObject)) {
				selectedObjects.add(currentObject.getBigBangObject());
			}
		}
		return selectedObjects; 
	}
	
	public BigBangObject getSelectedBigBangAnchor() {
		if (this.selectedAnchor != null && this.objects.contains(this.selectedAnchor)) {
			return this.selectedAnchor.getBigBangObject();
		}
		return null;
	}
	
	//TODO: differ between categorized selectedObjectPaths and not. only transformation ones need to be categorized!
	//other functions often only need selected paths by themselves and without anchor paths!!!!
	
	/*
	 * @return a list of sets of selected objects sorted by object type. some sets may be empty if no representative
	 * is selected!
	 *
	public SelectedObjectPaths getCategorizedSelectedObjectsPaths() {
		List<List<DenotatorPath>> selectedObjectPaths = new ArrayList<List<DenotatorPath>>();
		for (int i = 0; i < this.finder.getObjectCount(); i++) {
			selectedObjectPaths.add(new ArrayList<DenotatorPath>());
		}
		for (DisplayObject currentObject : this.selectedObjects) {
			int objectTypeIndex = this.finder.getObjectForms().indexOf(currentObject.getTopDenotatorPath().getEndForm());
			selectedObjectPaths.get(objectTypeIndex).add(currentObject.getTopDenotatorPath());
		}
		return new SelectedObjectPaths(selectedObjectPaths, this.getSelectedAnchorObjectPath());
	}
	
	public List<DenotatorPath> getSelectedObjectsPaths() {
		List<DenotatorPath> objectPaths = new ArrayList<DenotatorPath>();
		for (DisplayObject currentObject : this.selectedObjects) {
			objectPaths.add(currentObject.getTopDenotatorPath());
		}
		return objectPaths;
	}*/
	
	/*private void makeAllModulatorsVisible() {
		for (DisplayObject currentObject: this.objects) {
			currentObject.setVisibility(LayerState.active);
		}
	}
	
	public void updateModulatorVisibility(int modLevel, int siblingNumber) {
		/*TODO:for (DisplayObject currentObject: this) {
			if (currentObject.getValue(5) == modLevel
					&& (siblingNumber == -1 || currentObject.getValue(7) == siblingNumber)) {
				currentObject.setVisibility(LayerState.active);
			} else {
				currentObject.setVisibility(LayerState.invisible);
				this.deselectObject(currentObject);
			}
		}
	}
	
	private void updateVisibility(LayerStates states) {
		for (DisplayObject currentObject: this.objects) {
			LayerState currentState = states.get(currentObject.getLayer());
			currentObject.setVisibility(currentState);
			if (!currentState.equals(LayerState.active)) {
				this.deselectObject(currentObject);
			}
		}
		
		//deselect or select notes!!!
	}*/
	
	public void updateBounds(double xZoomFactor, double yZoomFactor, int xPosition, int yPosition) {
		for (DisplayObject currentObject : this.objects) {
			currentObject.updateBounds(xZoomFactor, yZoomFactor, xPosition, yPosition);
		}
	}
	
	public void paint(AbstractPainter painter) {
		try {
			this.paintConnectors(painter, this.objects);
			//paint active notes on top of inactive ones
			this.paintInactiveObjects(painter);
			this.paintActiveObjects(painter);
		} catch (ConcurrentModificationException e) {
			return;
		}
		//leads to some flipping problems, but necessary for clearness
		this.paintSelectedObjects(painter);
		this.paintSelectedAnchorObject(painter);
	}
	
	public void paintSelectedObjectsConnectors(AbstractPainter painter, int parentX, int parentY) {
		for (DisplayObject currentObject : this.selectedObjects) {
			currentObject.paintConnectors(painter, parentX, parentY);
		}
	}
	
	private void paintConnectors(AbstractPainter painter, Set<DisplayObject> notes) {
		for (DisplayObject currentObject : notes) {
			if (currentObject.isVisible()) {
				DisplayObject currentParent = this.getParent(currentObject);
				if (currentParent != null && currentParent.isVisible()) {
					currentObject.paintConnectors(painter, currentParent.getCenter().x, currentParent.getCenter().y);
				}
			}
		}
	}
	
	private void paintInactiveObjects(AbstractPainter painter) throws ConcurrentModificationException {
		for (DisplayObject currentObject : this.objects) {
			if (!currentObject.isActive()) {
				currentObject.paint(painter);
			}
		}
	}
	
	private void paintActiveObjects(AbstractPainter painter) throws ConcurrentModificationException {
		for (DisplayObject currentObject : this.objects) {
			if (currentObject.isActive()) {
				currentObject.paint(painter);
			}
		}
	}
	
	private void paintSelectedObjects(AbstractPainter painter) {
		for (DisplayObject currentObject : this.objects) {
			if (this.selectedObjects.contains(currentObject)) {
				currentObject.paint(painter);
			}
		}
	}
	
	private void paintSelectedAnchorObject(AbstractPainter painter) {
		if (this.selectedAnchor != null) {
			this.selectedAnchor.paintAnchorSelection(painter);
		}
	}

	/*public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.LAYERS)) {
			this.updateVisibility((LayerStates)event.getNewValue());
		} else if (propertyName.equals(ViewController.TOGGLE_MOD_FILTER)) {
			boolean filterOff = !(Boolean)event.getNewValue();
			if (filterOff) {
				this.makeAllModulatorsVisible();
			}
		} else if (propertyName.equals(ViewController.MOD_FILTER_VALUES)) {
			int[] selectedMods = (int[])event.getNewValue();
			this.updateModulatorVisibility(selectedMods[0], selectedMods[1]);
		}
	}*/
	
	//TODO: pretty lame, but used in two places: BigBangView and DisplayContents
	public int getTimeAxisIndex(ViewParameters viewParameters) {
		//TODO: take "Onset R" from somewhere
		int timeValueIndex = this.getCoordinateSystemValueNames().indexOf("Onset R");
		if (timeValueIndex == -1) {
			timeValueIndex = this.getCoordinateSystemValueNames().indexOf("BeatClass Z_16");
		}
		if (timeValueIndex != -1) {
			int onsetParameterIndex = viewParameters.getFirstIndexOfValue(timeValueIndex);
			if (onsetParameterIndex < 2) {
				return onsetParameterIndex;
			}
		}
		return -1;
	}
	
	public int size() {
		return this.objects.size();
	}

}

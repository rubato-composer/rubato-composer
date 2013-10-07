package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.LayerState;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.util.DenotatorObject;
import org.rubato.rubettes.util.DenotatorObjectConfiguration;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.FormValueFinder;

public class DisplayObjects implements View {
	
	//active object is the one selected for being drawn etc
	private Form baseForm;
	private FormValueFinder finder;
	private int indexOfActiveObjectType;
	private List<Integer> activeColimitCoordinates;
	//number for each object type, null if no satellites possible for the type
	private List<Integer> maxSatelliteLevels;
	private int activeSatelliteLevel;
	private List<String> coordinateSystemValueNames;
	private TreeSet<DisplayObject> objects;
	private Set<DisplayObject> selectedObjects;
	private DisplayObject selectedAnchor;
	
	public DisplayObjects(ViewController controller, Form baseForm) {
		controller.addView(this);
		this.baseForm = baseForm;
		
		this.finder = new FormValueFinder(this.baseForm, true);
		this.indexOfActiveObjectType = 0;
		this.initActiveColimitCoordinates(this.finder.getObjectAt(0).getColimits().size());
		this.maxSatelliteLevels = new ArrayList<Integer>();
		for (Boolean currentObjectCanBeSatellite : this.finder.getObjectsCanBeSatellites()) {
			if (currentObjectCanBeSatellite) {
				this.maxSatelliteLevels.add(-1);
			} else {
				this.maxSatelliteLevels.add(null);
			}
		}
		this.activeSatelliteLevel = 0;
		
		List<String> coordinateSystemValueNames = new ArrayList<String>(finder.getCoordinateSystemValueNames());
		if (this.finder.formAllowsForSatellites()) {
			coordinateSystemValueNames.add(DenotatorValueExtractor.SATELLITE_LEVEL);
			coordinateSystemValueNames.add(DenotatorValueExtractor.SIBLING_NUMBER);
		}
		if (this.finder.formContainsColimits()) {
			coordinateSystemValueNames.add(DenotatorValueExtractor.COLIMIT_INDEX);
		}
		this.coordinateSystemValueNames = coordinateSystemValueNames;
		
		this.objects = new TreeSet<DisplayObject>();
		this.selectedObjects = new TreeSet<DisplayObject>();
	}
	
	private void initActiveColimitCoordinates(int numberOfColimits) {
		this.activeColimitCoordinates = new ArrayList<Integer>();
		for (int i = 0; i < numberOfColimits; i++) {
			this.activeColimitCoordinates.add(0);
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
	
	public Integer getMaxSatelliteLevelOfActiveObject() {
		return this.maxSatelliteLevels.get(this.indexOfActiveObjectType);
	}
	
	public void setActiveSatelliteLevel(int satelliteLevel) {
		this.activeSatelliteLevel = satelliteLevel;
	}
	
	public Integer getActiveSatelliteLevel() {
		return this.activeSatelliteLevel;
	}
	
	public List<String> getCoordinateSystemValueNames() {
		return this.coordinateSystemValueNames;
	}
	
	public int getNumberOfNonAnalyticalCoordinateSystemValues() {
		return this.finder.getCoordinateSystemValueNames().size();
	}
	
	public Form getBaseForm() {
		return this.baseForm;
	}
	
	public boolean baseFormAllowsForSatellites() {
		return this.finder.formAllowsForSatellites();
	}
	
	public boolean baseFormContainsColimits() {
		return this.finder.formContainsColimits();
	}
	
	private List<DenotatorPath> getActiveObjectValuePaths() {
		return this.getActiveObjectType().getColimitConfigurationValuePaths(this.activeColimitCoordinates);
	}
	
	public void addObject(DisplayObject object) {
		this.objects.add(object);
		int objectIndex = this.finder.indexOf(object.getTopDenotatorPath().getEndForm());
		Integer currentLevel = this.maxSatelliteLevels.get(objectIndex);
		if (currentLevel != null) {
			int currentMax = Math.max(currentLevel, (int)object.getNthValue(DenotatorValueExtractor.SATELLITE_LEVEL, 0).doubleValue());
			this.maxSatelliteLevels.set(objectIndex, currentMax);
		}
	}
	
	public TreeSet<DisplayObject> getObjects() {
		return this.objects;
	}
	
	public void clearObjects() {
		this.objects = new TreeSet<DisplayObject>();
		this.selectedObjects = new TreeSet<DisplayObject>();
	}
	
	/**
	 * @return the number of the instance of the valueName of the value at the given index (number of previous
	 * instances + the instance itself. 1 if it is the first)
	 */
	public int getInstanceNumberOfCoordinateValueName(int coordinateSystemValueIndex) {
		return this.finder.getInstanceNumberOfCoordinateValueName(coordinateSystemValueIndex);
	}
	
	public DenotatorObjectConfiguration getObjectType(Form objectForm, DenotatorPath longestColimitPath) {
		return this.finder.getConfiguration(objectForm, longestColimitPath);
	}
	
	public DenotatorObjectConfiguration getStandardObjectType(Form objectForm) {
		return this.finder.getStandardConfiguration(objectForm);
	}
	
	public DenotatorPath getActiveObjectValuePathAt(int valueIndex) {
		return this.getActiveObjectValuePaths().get(valueIndex);
	}
	
	public List<List<DenotatorPath>> getAllObjectConfigurationsValuePathsAt(int coordinateSystemValueIndex) {
		return this.finder.getAllObjectConfigurationsValuePathsAt(coordinateSystemValueIndex);
	}
	
	/**
	 * @return the path of the closest powerset at the given coordinateSystemValueIndex, if it is closer to the given
	 * currentClosestPowersetPath
	 */
	public DisplayObject getClosestObject(int[] coordinateSystemValueIndices, double[] values, DenotatorPath examplePowersetPath) {
		Form formOfObjectInExamplePowerset = examplePowersetPath.getTopPath().getEndForm();
		DisplayObject closestObject = null;
		double shortestDistance = Double.MAX_VALUE;
			
		//if (this.indexOfActiveObjectType >= 0) {
			for (DisplayObject currentObject : this.objects) {
				if (currentObject.getTopDenotatorPath().getEndForm().equals(formOfObjectInExamplePowerset)
						&& currentObject.getTopDenotatorPath().size() == examplePowersetPath.getTopPath().size()) {
					//calculate Euclidean distance
					double currentDistance = 0;
					for (int i = 0; i < coordinateSystemValueIndices.length; i++) {
						String valueName = this.coordinateSystemValueNames.get(coordinateSystemValueIndices[i]);
						int nameIndex = this.getInstanceNumberOfCoordinateValueName(coordinateSystemValueIndices[i]);
						
						Double currentValue = currentObject.getNthValue(valueName, nameIndex);
						if (currentValue != null) {
							currentDistance += Math.pow(currentValue-values[i], 2);
						}
					}
					currentDistance = Math.sqrt(currentDistance);
					if (currentDistance < shortestDistance) {
						shortestDistance = currentDistance;
						closestObject = currentObject;
					}
				}
			}
		//}
		return closestObject;
	}
	
	public List<Form> getObjectTypes() {
		return this.finder.getObjectForms();
	}
	
	public DenotatorObject getActiveObjectType() {
		return this.finder.getObjectAt(this.indexOfActiveObjectType);
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
		return this.finder.getActiveObjectValueIndex(coordinateSystemValueIndex, this.indexOfActiveObjectType, this.activeColimitCoordinates);
	}
	
	public int getActiveObjectFirstValueIndex(SimpleForm form) {
		return this.finder.getActiveObjectFirstValueIndex(form, this.indexOfActiveObjectType, this.activeColimitCoordinates);
	}
	
	
	//------------------------
	
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
	
	private void deselectParents(DisplayObject note) {
		DisplayObject parent = note.getParent(); 
		if (parent != null) {
			this.deselectObject(parent);
			this.deselectParents(parent);
		}
	}
	
	private void deselectChildren(DisplayObject note) {
		for (DisplayObject currentChild: note.getChildren()) {
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
	
	public DisplayObject getObjectAt(Point location) {
		return this.getObjectAt(location, this.objects);
	}
	
	public boolean hasSelectedObjectAt(Point location) {
		return this.getObjectAt(location, this.selectedObjects) != null;
	}
	
	private DisplayObject getObjectAt(Point location, Set<DisplayObject> notes) {
		for (DisplayObject currentObject : notes) {
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
			} else if (noteInLocation.hasChildren()) {
				this.setSelectedAnchorObject(noteInLocation);
			}
		} else {
			this.selectedAnchor = null;
		}
	}
	
	public void setSelectedAnchorObject(DisplayObject note) {
		this.selectedAnchor = note;
		this.selectedObjects.remove(note);
	}
	
	public DenotatorPath getSelectedAnchorObjectPath() {
		if (this.selectedAnchor != null) {
			return this.selectedAnchor.getTopDenotatorPath();
		}
		return null;
	}
	
	public Point2D.Double getSelectedAnchorNodeCenter() {
		if (this.selectedAnchor != null) {
			return this.selectedAnchor.getLocation();
		}
		return null;
	}
	
	//TODO: differ between categorized selectedObjectPaths and not. only transformation ones need to be categorized!
	//other functions often only need selected paths by themselves and without anchor paths!!!!
	
	/*
	 * @return a list of sets of selected objects sorted by object type. some sets may be empty if no representative
	 * is selected!
	 */
	public SelectedObjectsPaths getCategorizedSelectedObjectsPaths() {
		List<List<DenotatorPath>> selectedObjectPaths = new ArrayList<List<DenotatorPath>>();
		for (int i = 0; i < this.finder.getObjectCount(); i++) {
			selectedObjectPaths.add(new ArrayList<DenotatorPath>());
		}
		for (DisplayObject currentObject : this.selectedObjects) {
			int objectTypeIndex = this.finder.getObjectForms().indexOf(currentObject.getTopDenotatorPath().getEndForm());
			selectedObjectPaths.get(objectTypeIndex).add(currentObject.getTopDenotatorPath());
		}
		return new SelectedObjectsPaths(selectedObjectPaths, this.getSelectedAnchorObjectPath());
	}
	
	public List<DenotatorPath> getSelectedObjectsPaths() {
		List<DenotatorPath> objectPaths = new ArrayList<DenotatorPath>();
		for (DisplayObject currentObject : this.selectedObjects) {
			objectPaths.add(currentObject.getTopDenotatorPath());
		}
		return objectPaths;
	}
	
	private void makeAllModulatorsVisible() {
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
		}*/
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
	}
	
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
			currentObject.paintConnectors(painter);
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
		for (DisplayObject currentObject : this.selectedObjects) {
			currentObject.paint(painter);
		}
	}
	
	private void paintSelectedAnchorObject(AbstractPainter painter) {
		if (this.selectedAnchor != null) {
			this.selectedAnchor.paintAnchorSelection(painter);
		}
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
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
	}
	
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
	
	public DisplayObject first() {
		return this.objects.first();
	}
	
	public DisplayObject last() {
		return this.objects.last();
	}
	
	public int size() {
		return this.objects.size();
	}

}

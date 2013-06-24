package org.rubato.rubettes.bigbang.view.subview;

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

import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.DenotatorValueExtractor;
import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.LayerState;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.util.DenotatorObject;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.FormValueFinder;

public class DisplayObjects implements View {
	
	//active object is the one selected for being drawn etc
	private Form baseForm;
	private FormValueFinder finder;
	private int indexOfActiveObjectType;
	private List<Integer> activeColimitCoordinates;
	private List<String> valueNames;
	private TreeSet<DisplayObject> objects;
	private Set<DisplayObject> selectedObjects;
	private DisplayObject selectedAnchor;
	
	public DisplayObjects(ViewController controller, Form baseForm) {
		controller.addView(this);
		this.baseForm = baseForm;
		
		this.finder = new FormValueFinder(this.baseForm, true);
		this.indexOfActiveObjectType = 0;
		this.initActiveColimitCoordinates(this.finder.getObjectAt(0).getColimits().size());
		
		List<String> valueNames = finder.getCoordinateSystemValueNames();
		if (this.finder.formAllowsForSatellites()) {
			valueNames.add(DenotatorValueExtractor.SATELLITE_LEVEL);
			valueNames.add(DenotatorValueExtractor.SIBLING_NUMBER);
		}
		if (this.finder.formContainsColimits()) {
			valueNames.add(DenotatorValueExtractor.COLIMIT_INDEX);
		}
		this.valueNames = valueNames;
		
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
	
	public List<String> getValueNames() {
		return this.valueNames;
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
	
	public List<DenotatorPath> getObjectValuePaths() {
		return this.getActiveObjectType().getColimitConfigurationValuePaths(this.activeColimitCoordinates);
	}
	
	public void addObject(DisplayObject object) {
		this.objects.add(object);
	}
	
	public TreeSet<DisplayObject> getObjects() {
		return this.objects;
	}
	
	public void clearObjects() {
		this.objects = new TreeSet<DisplayObject>();
	}
	
	public DenotatorPath getObjectValuePathAt(int valueIndex) {
		return this.getObjectValuePaths().get(valueIndex);
	}
	
	public DisplayObject getClosestObject(int valueIndex, double value, DenotatorPath powersetPath) {
		String valueName = this.valueNames.get(valueIndex);
		//DenotatorPath valuePath = this.getPathOfValueAt(valueIndex);
		DisplayObject closestObject = null;
		double shortestDistance = Double.MAX_VALUE;
		if (this.indexOfActiveObjectType > 0) {
			for (DisplayObject currentObject : this.objects) {
				//has to be same type of object. TODO: lenghth of course is not the deciding thing!!!!
				if (currentObject.getTopDenotatorPath().size() == powersetPath.getTopPath().size()) {
					Double currentValue = currentObject.getValue(valueName);
					if (currentValue != null) {
						double currentDistance = Math.abs(currentValue-value);
						if (currentDistance < shortestDistance) {
							shortestDistance = currentDistance;
							closestObject = currentObject;
						}
					}
				}
			}
			return closestObject;
		}
		return null;
	}
	
	public List<Form> getObjectTypes() {
		return this.finder.getObjectForms();
	}
	
	public DenotatorObject getActiveObjectType() {
		return this.finder.getObjectAt(this.indexOfActiveObjectType);
	}
	
	/*private List<DenotatorPath> getActiveObjectColimitPaths(List<Integer> colimitCoordinates) {
		List<DenotatorPath> colimitCoordinatePaths = new ArrayList<DenotatorPath>();
		for (int i = 0; i < colimitCoordinates.size(); i++) {
			int currentSelectedCoordinate = colimitCoordinates.get(i);
			if (currentSelectedCoordinate >= 0) {
				colimitCoordinatePaths.add(this.getActiveObjectType().getColimitPaths().get(i).getChildPath(currentSelectedCoordinate));
			}
		}
		return colimitCoordinatePaths;
	}*/
	
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
		return this.getActiveObjectType().getColimitConfigurationValueNames(this.activeColimitCoordinates).indexOf(this.finder.getCoordinateSystemValueNames().get(coordinateSystemValueIndex));
	}
	
	/*public boolean pathInAllowedColimitBranch(DenotatorPath path) {
		return this.inAllowedColimitBranch(path, this.getActiveObjectColimitPaths(this.activeColimitCoordinates));
	}
	
	private boolean inAllowedColimitBranch(DenotatorPath path, List<DenotatorPath> selectedColimitCoordinatePaths) {
		for (DenotatorPath currentColimitPath : path.getParentColimitPaths()) {
			boolean containedInSelectedPaths = false;
			for (DenotatorPath currentSelectedPath : selectedColimitCoordinatePaths) {
				if (currentColimitPath.equals(currentSelectedPath.getParentPath())) {
					containedInSelectedPaths = true;
					if (!path.subPath(0, currentColimitPath.size()+1).equals(currentSelectedPath)) {
						return false;
					}
				}
			}
			if (!containedInSelectedPaths) {
				if (path.subPath(0, currentColimitPath.size()+1).getLastIndex() != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/*public boolean inConflictingColimitPositions(int valueIndex1, int valueIndex2) {
		return this.getPathOfValueAt(valueIndex1).inConflictingColimitPositions(this.getPathOfValueAt(valueIndex2));
	}*/
	
	public void tempSelectNotes(Rectangle2D.Double area) {
		for (DisplayObject currentNote: this.objects) {
			if (!this.selectedObjects.contains(currentNote)) {
				currentNote.setSelected(currentNote.intersects(area));
			}
		}
	}
	
	public int selectNotes(Rectangle2D.Double area) {
		for (DisplayObject currentNote: this.objects) {
			if (currentNote.intersects(area)) {
				this.selectNote(currentNote);
			}
		}
		return this.selectedObjects.size();
	}
	
	private void toggleSelected(DisplayObject note) {
		if (note.isSelected()) {
			this.deselectNote(note);
		} else {
			this.selectNote(note);
		}
	}
	
	public void selectNote(DisplayObject note) {
		if (this.isNotSelectedAnchorNote(note)) {
			note.setSelected(true);
			if (!this.selectedObjects.contains(note) && note.isActive()) {
				this.selectedObjects.add(note);
				this.deselectParents(note);
				this.deselectChildren(note);
			}
		}
	}
	
	private void deselectNote(DisplayObject note) {
		note.setSelected(false);
		this.selectedObjects.remove(note);
	}
	
	private boolean isNotSelectedAnchorNote(DisplayObject note) {
		return (this.selectedAnchor != null && !this.selectedAnchor.equals(note))
			|| this.selectedAnchor == null;
	}
	
	private void deselectParents(DisplayObject note) {
		DisplayObject parent = note.getParent(); 
		if (parent != null) {
			this.deselectNote(parent);
			this.deselectParents(parent);
		}
	}
	
	private void deselectChildren(DisplayObject note) {
		for (DisplayObject currentChild: note.getChildren()) {
			this.deselectNote(currentChild);
			this.deselectChildren(currentChild);
		}
	}
	
	public int selectTopOrDeselectAllNotes(Point location) {
		//notes are saved from bottom to top... just takes one note
		for (DisplayObject currentNote: this.objects) {
			if (currentNote.getRectangle().contains(location)) {
				this.toggleSelected(currentNote);
				return this.selectedObjects.size();
			}
		}
		this.deselectAllNotes();
		return this.selectedObjects.size();
	}
	
	private void deselectAllNotes() {
		for (DisplayObject currentNote: this.selectedObjects) {
			currentNote.setSelected(false);
		}
		this.selectedObjects = new TreeSet<DisplayObject>();
	}
	
	public DisplayObject getNoteAt(Point location) {
		return this.getNoteAt(location, this.objects);
	}
	
	public boolean hasSelectedNoteAt(Point location) {
		return this.getNoteAt(location, this.selectedObjects) != null;
	}
	
	private DisplayObject getNoteAt(Point location, Set<DisplayObject> notes) {
		for (DisplayObject currentNote : notes) {
			if (currentNote.getRectangle().contains(location)) {
				return currentNote;
			}
		}
		return null;
	}
	
	public void selectOrDeselectAnchorNote(Point location) {
		DisplayObject noteInLocation = this.getNoteAt(location);
		if (noteInLocation != null) {
			if (noteInLocation.equals(this.selectedAnchor)) {
				this.selectedAnchor = null;
			} else if (noteInLocation.hasChildren()) {
				this.setSelectedAnchorNote(noteInLocation);
			}
		} else {
			this.selectedAnchor = null;
		}
	}
	
	public void setSelectedAnchorNote(DisplayObject note) {
		this.selectedAnchor = note;
		this.selectedObjects.remove(note);
	}
	
	public DenotatorPath getSelectedAnchorNodePath() {
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
	
	public Set<DenotatorPath> getSelectedObjectPaths() {
		TreeSet<DenotatorPath> objectPaths = new TreeSet<DenotatorPath>();
		for (DisplayObject currentObject : this.selectedObjects) {
			//nodePaths.add(new DenotatorPath(currentNote.getOriginalPath()));
			objectPaths.add(currentObject.getTopDenotatorPath());
		}
		return objectPaths;
	}
	
	private void makeAllModulatorsVisible() {
		for (DisplayObject currentNote: this.objects) {
			currentNote.setVisibility(LayerState.active);
		}
	}
	
	public void updateModulatorVisibility(int modLevel, int siblingNumber) {
		/*TODO:for (DisplayObject currentNote: this) {
			if (currentNote.getValue(5) == modLevel
					&& (siblingNumber == -1 || currentNote.getValue(7) == siblingNumber)) {
				currentNote.setVisibility(LayerState.active);
			} else {
				currentNote.setVisibility(LayerState.invisible);
				this.deselectNote(currentNote);
			}
		}*/
	}
	
	private void updateVisibility(LayerStates states) {
		for (DisplayObject currentNote: this.objects) {
			LayerState currentState = states.get(currentNote.getLayer());
			currentNote.setVisibility(currentState);
			if (!currentState.equals(LayerState.active)) {
				this.deselectNote(currentNote);
			}
		}
		
		//deselect or select notes!!!
	}
	
	public void updateBounds(double xZoomFactor, double yZoomFactor, int xPosition, int yPosition) {
		for (DisplayObject currentNote : this.objects) {
			currentNote.updateBounds(xZoomFactor, yZoomFactor, xPosition, yPosition);
		}
	}
	
	public void paint(AbstractPainter painter) {
		this.paintConnectors(painter, this.objects);
		this.paintInactiveNotes(painter);
		this.paintActiveNotes(painter);
		//leads to some flipping problems, but necessary for clearness
		this.paintSelectedNotes(painter);
		this.paintSelectedAnchorNote(painter);
	}
	
	public void paintSelectedNotesConnectors(AbstractPainter painter, int parentX, int parentY) {
		for (DisplayObject currentNote : this.selectedObjects) {
			currentNote.paintConnectors(painter, parentX, parentY);
		}
	}
	
	private void paintConnectors(AbstractPainter painter, Set<DisplayObject> notes) {
		for (DisplayObject currentNote : notes) {
			currentNote.paintConnectors(painter);
		}
	}
	
	private void paintInactiveNotes(AbstractPainter painter) {
		for (DisplayObject currentNote : this.objects) {
			if (!currentNote.isActive()) {
				currentNote.paint(painter);
			}
		}
	}
	
	private void paintActiveNotes(AbstractPainter painter) {
		for (DisplayObject currentNote : this.objects) {
			if (currentNote.isActive()) {
				currentNote.paint(painter);
			}
		}
	}
	
	public void paintSelectedNotes(AbstractPainter painter) {
		for (DisplayObject currentNote : this.selectedObjects) {
			currentNote.paint(painter);
		}
	}
	
	private void paintSelectedAnchorNote(AbstractPainter painter) {
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
		int timeValueIndex = this.getValueNames().indexOf("Onset R");
		if (timeValueIndex == -1) {
			timeValueIndex = this.getValueNames().indexOf("BeatClass Z_16");
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
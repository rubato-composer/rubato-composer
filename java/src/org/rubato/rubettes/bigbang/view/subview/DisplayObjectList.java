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
import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.LayerState;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
import org.rubato.rubettes.util.DenotatorPath;

public class DisplayObjectList extends TreeSet<DisplayObject> implements View {
	
	private Form baseForm;
	private Map<String,DenotatorPath> topDenotatorValues;
	private List<ColimitForm> topDenotatorColimits;
	private Map<ColimitForm,DenotatorPath> topDenotatorColimitsAndPaths;
	private boolean containsPowerset;
	private List<String> valueNames;
	private Set<DisplayObject> selectedNotes;
	private DisplayObject selectedAnchorNote;
	
	public DisplayObjectList(ViewController controller, Form baseForm) {
		controller.addView(this);
		this.baseForm = baseForm;
		this.topDenotatorColimits = new ArrayList<ColimitForm>();
		this.topDenotatorColimitsAndPaths = new TreeMap<ColimitForm,DenotatorPath>();
		this.selectedNotes = new TreeSet<DisplayObject>();
		this.valueNames = new ArrayList<String>();
	}
	
	public void setValueNames(List<String> valueNames) {
		this.valueNames = valueNames;
	}
	
	public List<String> getValueNames() {
		return this.valueNames;
	}
	
	public Form getBaseForm() {
		return this.baseForm;
	}
	
	public void setContainsPowerset(boolean containsPowerset) {
		this.containsPowerset = containsPowerset;
	}
	
	public boolean containsPowerset() {
		return this.containsPowerset;
	}
	
	public void setTopDenotatorValues(Map<String,DenotatorPath> valuesNamesAndPaths) {
		this.topDenotatorValues = valuesNamesAndPaths;
	}
	
	public List<DenotatorPath> getTopDenotatorValuePaths() {
		List<DenotatorPath> paths = new ArrayList<DenotatorPath>();
		for (String currentValueName : this.valueNames) {
			paths.add(this.topDenotatorValues.get(currentValueName));
		}
		return paths;
	}
	
	public DenotatorPath getPathInTopDenotatorValues(int valueIndex) {
		return this.topDenotatorValues.get(this.valueNames.get(valueIndex));
	}
	
	public void setTopDenotatorColimits(List<ColimitForm> colimits) {
		this.topDenotatorColimits = colimits;
	}
	
	public List<ColimitForm> getTopDenotatorColimits() {
		return this.topDenotatorColimits;
	}
	
	public void setTopDenotatorColimitsAndPaths(Map<ColimitForm,DenotatorPath> colimitsAndPaths) {
		this.topDenotatorColimitsAndPaths = colimitsAndPaths;
	}
	
	public List<DenotatorPath> getTopDenotatorColimitPaths(List<Integer> colimitCoordinates) {
		List<DenotatorPath> colimitCoordinatePaths = new ArrayList<DenotatorPath>();
		for (int i = 0; i < colimitCoordinates.size(); i++) {
			int currentSelectedCoordinate = colimitCoordinates.get(i);
			colimitCoordinatePaths.add(this.topDenotatorColimitsAndPaths.get(this.topDenotatorColimits.get(i)).getChildPath(currentSelectedCoordinate));
		}
		return colimitCoordinatePaths;
	}
	
	/**
	 * @return the top denotator standard values under assumption that the given value
	 * is selected in a colimit. if it is not in a colimit, just returns the standard values
	 */
	public Map<DenotatorPath,Double> getTopDenotatorStandardValues(Map<String,Double> standardDenotatorValues, List<Integer> selectedColimitCoordinates) {
		List<DenotatorPath> selectedColimitCoordinatePaths = this.getTopDenotatorColimitPaths(selectedColimitCoordinates);
		Map<DenotatorPath,Double> topDenotatorStandardValues = new TreeMap<DenotatorPath,Double>();
		for (String currentName : this.topDenotatorValues.keySet()) {
			if (standardDenotatorValues.containsKey(currentName)) {
				DenotatorPath currentPath = this.topDenotatorValues.get(currentName);
				if (this.inAllowedColimitBranch(currentPath, selectedColimitCoordinatePaths)) {
					topDenotatorStandardValues.put(currentPath, standardDenotatorValues.get(currentName));
				}
			}
		}
		return topDenotatorStandardValues;
	}
	
	public boolean pathInAllowedColimitBranch(DenotatorPath path, List<Integer> selectedColimitCoordinates) {
		return this.inAllowedColimitBranch(path, this.getTopDenotatorColimitPaths(selectedColimitCoordinates));
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
	
	public boolean inConflictingColimitPositions(int valueIndex1, int valueIndex2) {
		return this.getPathInTopDenotatorValues(valueIndex1).inConflictingColimitPositions(this.getPathInTopDenotatorValues(valueIndex2));
	}
	
	public void tempSelectNotes(Rectangle2D.Double area) {
		for (DisplayObject currentNote: this) {
			if (!this.selectedNotes.contains(currentNote)) {
				currentNote.setSelected(currentNote.intersects(area));
			}
		}
	}
	
	public int selectNotes(Rectangle2D.Double area) {
		for (DisplayObject currentNote: this) {
			if (currentNote.intersects(area)) {
				this.selectNote(currentNote);
			}
		}
		return this.selectedNotes.size();
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
			if (!this.selectedNotes.contains(note) && note.isActive()) {
				this.selectedNotes.add(note);
				this.deselectParents(note);
				this.deselectChildren(note);
			}
		}
	}
	
	private void deselectNote(DisplayObject note) {
		note.setSelected(false);
		this.selectedNotes.remove(note);
	}
	
	private boolean isNotSelectedAnchorNote(DisplayObject note) {
		return (this.selectedAnchorNote != null && !this.selectedAnchorNote.equals(note))
			|| this.selectedAnchorNote == null;
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
		for (DisplayObject currentNote: this) {
			if (currentNote.getRectangle().contains(location)) {
				this.toggleSelected(currentNote);
				return this.selectedNotes.size();
			}
		}
		this.deselectAllNotes();
		return this.selectedNotes.size();
	}
	
	private void deselectAllNotes() {
		for (DisplayObject currentNote: this.selectedNotes) {
			currentNote.setSelected(false);
		}
		this.selectedNotes = new TreeSet<DisplayObject>();
	}
	
	public DisplayObject getNoteAt(Point location) {
		return this.getNoteAt(location, this);
	}
	
	public boolean hasSelectedNoteAt(Point location) {
		return this.getNoteAt(location, this.selectedNotes) != null;
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
			if (noteInLocation.equals(this.selectedAnchorNote)) {
				this.selectedAnchorNote = null;
			} else if (noteInLocation.hasChildren()) {
				this.setSelectedAnchorNote(noteInLocation);
			}
		} else {
			this.selectedAnchorNote = null;
		}
	}
	
	public void setSelectedAnchorNote(DisplayObject note) {
		this.selectedAnchorNote = note;
		this.selectedNotes.remove(note);
	}
	
	public DenotatorPath getSelectedAnchorNodePath() {
		if (this.selectedAnchorNote != null) {
			return this.selectedAnchorNote.getTopDenotatorPath();
		}
		return null;
	}
	
	public Point2D.Double getSelectedAnchorNodeCenter() {
		if (this.selectedAnchorNote != null) {
			return this.selectedAnchorNote.getLocation();
		}
		return null;
	}
	
	public Set<DenotatorPath> getSelectedNodePaths() {
		TreeSet<DenotatorPath> objectPaths = new TreeSet<DenotatorPath>();
		for (DisplayObject currentObject : this.selectedNotes) {
			//nodePaths.add(new DenotatorPath(currentNote.getOriginalPath()));
			objectPaths.add(currentObject.getTopDenotatorPath());
		}
		return objectPaths;
	}
	
	private void makeAllModulatorsVisible() {
		for (DisplayObject currentNote: this) {
			currentNote.setVisibility(LayerState.active);
		}
	}
	
	public void updateModulatorVisibility(int modLevel, int siblingNumber) {
		for (DisplayObject currentNote: this) {
			if (currentNote.getValue(5) == modLevel
					&& (siblingNumber == -1 || currentNote.getValue(7) == siblingNumber)) {
				currentNote.setVisibility(LayerState.active);
			} else {
				currentNote.setVisibility(LayerState.invisible);
				this.deselectNote(currentNote);
			}
		}
	}
	
	private void updateVisibility(LayerStates states) {
		for (DisplayObject currentNote: this) {
			LayerState currentState = states.get(currentNote.getLayer());
			currentNote.setVisibility(currentState);
			if (!currentState.equals(LayerState.active)) {
				this.deselectNote(currentNote);
			}
		}
		
		//deselect or select notes!!!
	}
	
	public void updateBounds(double xZoomFactor, double yZoomFactor, int xPosition, int yPosition) {
		for (DisplayObject currentNote : this) {
			currentNote.updateBounds(xZoomFactor, yZoomFactor, xPosition, yPosition);
		}
	}
	
	public void paint(AbstractPainter painter) {
		this.paintConnectors(painter, this);
		this.paintInactiveNotes(painter);
		this.paintActiveNotes(painter);
		//leads to some flipping problems, but necessary for clearness
		this.paintSelectedNotes(painter);
		this.paintSelectedAnchorNote(painter);
	}
	
	public void paintSelectedNotesConnectors(AbstractPainter painter, int parentX, int parentY, int relation) {
		for (DisplayObject currentNote : this.selectedNotes) {
			currentNote.paintConnectors(painter, parentX, parentY, relation);
		}
	}
	
	private void paintConnectors(AbstractPainter painter, Set<DisplayObject> notes) {
		for (DisplayObject currentNote : notes) {
			currentNote.paintConnectors(painter);
		}
	}
	
	private void paintInactiveNotes(AbstractPainter painter) {
		for (DisplayObject currentNote : this) {
			if (!currentNote.isActive()) {
				currentNote.paint(painter);
			}
		}
	}
	
	private void paintActiveNotes(AbstractPainter painter) {
		for (DisplayObject currentNote : this) {
			if (currentNote.isActive()) {
				currentNote.paint(painter);
			}
		}
	}
	
	public void paintSelectedNotes(AbstractPainter painter) {
		for (DisplayObject currentNote : this.selectedNotes) {
			currentNote.paint(painter);
		}
	}
	
	private void paintSelectedAnchorNote(AbstractPainter painter) {
		if (this.selectedAnchorNote != null) {
			this.selectedAnchorNote.paintAnchorSelection(painter);
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

}

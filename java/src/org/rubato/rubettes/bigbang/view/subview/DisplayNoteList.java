package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.DisplayNote;
import org.rubato.rubettes.bigbang.view.model.LayerState;
import org.rubato.rubettes.bigbang.view.model.LayerStates;
import org.rubato.rubettes.util.NotePath;

public class DisplayNoteList extends TreeSet<DisplayNote> implements View {
	
	private Set<DisplayNote> selectedNotes;
	private DisplayNote selectedAnchorNote;
	
	public DisplayNoteList(ViewController controller) {
		controller.addView(this);
		this.selectedNotes = new TreeSet<DisplayNote>();
	}
	
	public void tempSelectNotes(Rectangle2D.Double area) {
		for (DisplayNote currentNote: this) {
			if (!this.selectedNotes.contains(currentNote)) {
				currentNote.setSelected(currentNote.intersects(area));
			}
		}
	}
	
	public int selectNotes(Rectangle2D.Double area) {
		for (DisplayNote currentNote: this) {
			if (currentNote.intersects(area)) {
				this.selectNote(currentNote);
			}
		}
		return this.selectedNotes.size();
	}
	
	private void toggleSelected(DisplayNote note) {
		if (note.isSelected()) {
			this.deselectNote(note);
		} else {
			this.selectNote(note);
		}
	}
	
	public void selectNote(DisplayNote note) {
		if (this.isNotSelectedAnchorNote(note)) {
			note.setSelected(true);
			if (!this.selectedNotes.contains(note) && note.isActive()) {
				this.selectedNotes.add(note);
				this.deselectParents(note);
				this.deselectChildren(note);
			}
		}
	}
	
	private void deselectNote(DisplayNote note) {
		note.setSelected(false);
		this.selectedNotes.remove(note);
	}
	
	private boolean isNotSelectedAnchorNote(DisplayNote note) {
		return (this.selectedAnchorNote != null && !this.selectedAnchorNote.equals(note))
			|| this.selectedAnchorNote == null;
	}
	
	private void deselectParents(DisplayNote note) {
		DisplayNote parent = note.getParent(); 
		if (parent != null) {
			this.deselectNote(parent);
			this.deselectParents(parent);
		}
	}
	
	private void deselectChildren(DisplayNote note) {
		for (DisplayNote currentChild: note.getChildren()) {
			this.deselectNote(currentChild);
			this.deselectChildren(currentChild);
		}
	}
	
	public int selectTopOrDeselectAllNotes(Point location) {
		//notes are saved from bottom to top... just takes one note
		for (DisplayNote currentNote: this) {
			if (currentNote.getRectangle().contains(location)) {
				this.toggleSelected(currentNote);
				return this.selectedNotes.size();
			}
		}
		this.deselectAllNotes();
		return this.selectedNotes.size();
	}
	
	private void deselectAllNotes() {
		for (DisplayNote currentNote: this.selectedNotes) {
			currentNote.setSelected(false);
		}
		this.selectedNotes = new TreeSet<DisplayNote>();
	}
	
	public DisplayNote getNoteAt(Point location) {
		return this.getNoteAt(location, this);
	}
	
	public boolean hasSelectedNoteAt(Point location) {
		return this.getNoteAt(location, this.selectedNotes) != null;
	}
	
	private DisplayNote getNoteAt(Point location, Set<DisplayNote> notes) {
		for (DisplayNote currentNote : notes) {
			if (currentNote.getRectangle().contains(location)) {
				return currentNote;
			}
		}
		return null;
	}
	
	public void selectOrDeselectAnchorNote(Point location) {
		DisplayNote noteInLocation = this.getNoteAt(location);
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
	
	public void setSelectedAnchorNote(DisplayNote note) {
		this.selectedAnchorNote = note;
		this.selectedNotes.remove(note);
	}
	
	public NotePath getSelectedAnchorNodePath() {
		if (this.selectedAnchorNote != null) {
			return this.selectedAnchorNote.getOriginalPath();
		}
		return null;
	}
	
	public Point2D.Double getSelectedAnchorNodeCenter() {
		if (this.selectedAnchorNote != null) {
			return this.selectedAnchorNote.getLocation();
		}
		return null;
	}
	
	public Set<NotePath> getSelectedNodePaths() {
		TreeSet<NotePath> notePaths = new TreeSet<NotePath>();
		for (DisplayNote currentNote : this.selectedNotes) {
			//nodePaths.add(new DenotatorPath(currentNote.getOriginalPath()));
			notePaths.add(currentNote.getOriginalPath());
		}
		return notePaths;
	}
	
	private void makeAllModulatorsVisible() {
		for (DisplayNote currentNote: this) {
			currentNote.setVisibility(LayerState.active);
		}
	}
	
	public void updateModulatorVisibility(int modLevel, int siblingNumber) {
		for (DisplayNote currentNote: this) {
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
		for (DisplayNote currentNote: this) {
			LayerState currentState = states.get(currentNote.getLayer());
			currentNote.setVisibility(currentState);
			if (!currentState.equals(LayerState.active)) {
				this.deselectNote(currentNote);
			}
		}
		
		//deselect or select notes!!!
	}
	
	public void updateBounds(double xZoomFactor, double yZoomFactor, int xPosition, int yPosition) {
		for (DisplayNote currentNote : this) {
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
		for (DisplayNote currentNote : this.selectedNotes) {
			currentNote.paintConnectors(painter, parentX, parentY, relation);
		}
	}
	
	private void paintConnectors(AbstractPainter painter, Set<DisplayNote> notes) {
		for (DisplayNote currentNote : notes) {
			currentNote.paintConnectors(painter);
		}
	}
	
	private void paintInactiveNotes(AbstractPainter painter) {
		for (DisplayNote currentNote : this) {
			if (!currentNote.isActive()) {
				currentNote.paint(painter);
			}
		}
	}
	
	private void paintActiveNotes(AbstractPainter painter) {
		for (DisplayNote currentNote : this) {
			if (currentNote.isActive()) {
				currentNote.paint(painter);
			}
		}
	}
	
	public void paintSelectedNotes(AbstractPainter painter) {
		for (DisplayNote currentNote : this.selectedNotes) {
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

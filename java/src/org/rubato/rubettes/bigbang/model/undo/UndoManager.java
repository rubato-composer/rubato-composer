package org.rubato.rubettes.bigbang.model.undo;

import java.util.ArrayList;
import java.util.List;

/**
 * A light-weight implementation of an UndoManager so that it can be  used on non-Swing platforms
 * 
 * @author florian thalmann
 *
 */
public class UndoManager {
	
	private List<AbstractUndoableEdit> edits;
	private int currentPosition;
	
	public UndoManager() {
		this.init();
	}
	
	private void init() {
		this.edits = new ArrayList<AbstractUndoableEdit>();
		this.currentPosition = 0;
	}
	
	public void postEdit(AbstractUndoableEdit edit) {
		if (this.edits.size() > this.currentPosition) {
			this.edits = this.edits.subList(0, this.currentPosition);
		}
		this.edits.add(edit);
		this.currentPosition++;
	}
	
	public boolean canUndo() {
		return this.currentPosition > 0;
	}
	
	public boolean canRedo() {
		return this.edits.size() > this.currentPosition;
	}
	
	public void undo() {
		if (this.canUndo()) {
			this.edits.get(this.currentPosition-1).undo();
			this.currentPosition--;
		}
	}
	
	public void redo() {
		if (this.canRedo()) {
			this.edits.get(this.currentPosition).redo();
			this.currentPosition++;
		}
	}
	
	public String getUndoPresentationName() {
		if (this.canUndo()) {
			return this.edits.get(this.currentPosition-1).getPresentationName();
		}
		return "Nothing to undo";
	}
	
	public String getRedoPresentationName() {
		if (this.canRedo()) {
			return this.edits.get(this.currentPosition).getPresentationName();
		}
		return "Nothing to redo";
	}
	
	public void discardAllEdits() {
		this.init();
	}

}

package org.rubato.rubettes.bigbang.model;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.Controller;

public class UndoRedoModel extends Model {
	
	private UndoManager undoManager;
	private UndoableEditSupport undoSupport;
	
	public UndoRedoModel(Controller controller) {
		controller.addModel(this);
		this.undoManager = new UndoManager();
		this.undoSupport = new UndoableEditSupport();
		this.undoSupport.addUndoableEditListener(new UndoAdaptor(this.undoManager));
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
	}
	
	public void undo() {
		try {
			this.undoManager.undo();
		} catch (Exception e) { e.printStackTrace(); }
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
	}
	
	public void redo() {
		this.undoManager.redo();
		this.firePropertyChange(BigBangController.REDO, null, this.undoManager);
	}
	
	public void postEdit(AbstractUndoableEdit edit) {
		this.undoSupport.postEdit(edit);
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
	}
	
	public void reset() {
		this.undoManager.discardAllEdits();
	}

}

package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.Controller;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;

public class UndoRedoModel extends Model {
	
	private UndoManager undoManager;
	private List<String> transformationNames;
	private UndoableEditSupport undoSupport;
	
	public UndoRedoModel(Controller controller) {
		controller.addModel(this);
		this.undoManager = new UndoManager();
		this.transformationNames = new ArrayList<String>();
		this.undoSupport = new UndoableEditSupport();
		this.undoSupport.addUndoableEditListener(new UndoAdaptor(this.undoManager));
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationNames);
	}
	
	public void undo() {
		try {
			this.undoManager.undo();
			this.transformationNames.remove(this.transformationNames.size()-1);
		} catch (Exception e) { e.printStackTrace(); }
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationNames);
	}
	
	public void redo() {
		this.undoManager.redo();
		//TODO: check if transformation!!!!
		this.transformationNames.add(this.undoManager.getUndoPresentationName().substring(5));
		this.firePropertyChange(BigBangController.REDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationNames);
	}
	
	public void postEdit(AbstractUndoableEdit edit) {
		this.undoSupport.postEdit(edit);
		if (edit instanceof AbstractTransformationEdit) {
			this.transformationNames.add(edit.getPresentationName());
		}
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformationNames);
	}
	
	public void reset() {
		this.undoManager.discardAllEdits();
		this.transformationNames = new ArrayList<String>();
	}

}

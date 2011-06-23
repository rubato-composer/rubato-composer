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
	private UndoableEditSupport undoSupport;
	private List<AbstractTransformationEdit> transformations;
	private int selectedTransformationIndex;
	
	public UndoRedoModel(Controller controller) {
		controller.addModel(this);
		this.undoManager = new UndoManager();
		this.undoSupport = new UndoableEditSupport();
		this.undoSupport.addUndoableEditListener(new UndoAdaptor(this.undoManager));
		this.reset();
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void undo() {
		try {
			this.undoManager.undo();
			this.transformations.remove(this.transformations.size()-1);
		} catch (Exception e) { e.printStackTrace(); }
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void redo() {
		this.undoManager.redo();
		//TODO: add to transformations if transformation!!!!
		this.firePropertyChange(BigBangController.REDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void postEdit(AbstractUndoableEdit edit) {
		this.undoSupport.postEdit(edit);
		if (edit instanceof AbstractTransformationEdit) {
			this.transformations.add((AbstractTransformationEdit)edit);
		}
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void selectTransformation(AbstractTransformationEdit transformation) {
		this.selectedTransformationIndex = this.transformations.indexOf(transformation);
		this.firePropertyChange(BigBangController.SELECT_TRANSFORMATION, null, transformation);
	}
	
	public void reset() {
		this.undoManager.discardAllEdits();
		this.transformations = new ArrayList<AbstractTransformationEdit>();
		this.selectedTransformationIndex = -1;
	}

}

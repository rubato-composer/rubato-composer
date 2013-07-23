package org.rubato.rubettes.bigbang.model;

import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.Controller;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPathss;

public class UndoRedoModel extends Model {
	
	private UndoManager undoManager;
	private UndoableEditSupport undoSupport;
	private BigBangTransformationGraph operations;
	private List<AbstractOperationEdit> undoneOperations;
	
	public UndoRedoModel(Controller controller) {
		controller.addModel(this);
		this.undoManager = new UndoManager();
		this.undoSupport = new UndoableEditSupport();
		this.undoSupport.addUndoableEditListener(new UndoAdaptor(this.undoManager));
		this.reset();
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.operations);
	}
	
	public void newWindowAdded(SelectedObjectsPathss paths) {
		this.firePropertyChange(BigBangController.GRAPH, null, this.operations);
	}
	
	public void undo() {
		//try {
		//this.undoManager.undo();
		this.undoneOperations.add(this.operations.removeLast());
		//} catch (Exception e) { e.printStackTrace(); }
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.operations);
	}
	
	public void redo() {
		//this.undoManager.redo();
		this.operations.add(this.undoneOperations.remove(this.undoneOperations.size()-1));
		this.firePropertyChange(BigBangController.REDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.operations);
	}
	
	public void previewTransformationAtEnd(AbstractUndoableEdit edit) {
		if (edit instanceof AbstractOperationEdit) {
			this.operations.previewTransformationAtEnd((AbstractTransformationEdit)edit);
		}
	}
	
	public void postEdit(AbstractUndoableEdit edit) {
		//this.undoSupport.postEdit(edit);
		if (edit instanceof AbstractOperationEdit) {
			this.operations.add((AbstractOperationEdit)edit);
		}
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.operations);
	}
	
	public AbstractOperationEdit getLastEdit() {
		if (this.operations.size() > 0) {
			return this.operations.get(this.operations.size()-1);
		}
		return null;
	}
	
	public void modifiedOperation(Boolean inPreviewMode) {
		this.operations.updateScore(inPreviewMode);
	}
	
	public void reset() {
		this.undoManager.discardAllEdits();
		this.operations = new BigBangTransformationGraph();
		this.firePropertyChange(BigBangController.GRAPH, null, this.operations);
	}

}

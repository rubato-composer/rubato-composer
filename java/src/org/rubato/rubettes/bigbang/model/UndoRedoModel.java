package org.rubato.rubettes.bigbang.model;

import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.controller.Controller;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.view.model.SelectedPaths;

public class UndoRedoModel extends Model {
	
	private UndoManager undoManager;
	private UndoableEditSupport undoSupport;
	private BigBangTransformationGraph transformations;
	private List<AbstractTransformationEdit> undoneTransformations;
	
	public UndoRedoModel(Controller controller) {
		controller.addModel(this);
		this.undoManager = new UndoManager();
		this.undoSupport = new UndoableEditSupport();
		this.undoSupport.addUndoableEditListener(new UndoAdaptor(this.undoManager));
		this.reset();
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void newWindowAdded(@SuppressWarnings("unused") SelectedPaths paths) {
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void undo() {
		//try {
		//this.undoManager.undo();
		this.undoneTransformations.add(this.transformations.removeLast());
		//} catch (Exception e) { e.printStackTrace(); }
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void redo() {
		//this.undoManager.redo();
		this.transformations.add(this.undoneTransformations.remove(this.undoneTransformations.size()-1));
		this.firePropertyChange(BigBangController.REDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void previewTransformationAtEnd(AbstractUndoableEdit edit) {
		if (edit instanceof AbstractTransformationEdit) {
			this.transformations.previewTransformationAtEnd((AbstractTransformationEdit)edit);
		}
	}
	
	public void postEdit(AbstractUndoableEdit edit) {
		//this.undoSupport.postEdit(edit);
		if (edit instanceof AbstractTransformationEdit) {
			this.transformations.add((AbstractTransformationEdit)edit);
		}
		this.firePropertyChange(BigBangController.UNDO, null, this.undoManager);
		this.firePropertyChange(BigBangController.GRAPH, null, this.transformations);
	}
	
	public void deselectTransformations() {
		this.firePropertyChange(BigBangController.DESELECT_TRANSFORMATIONS, null, null);
	}
	
	public void modifiedTransformation(Boolean inPreviewMode) {
		this.transformations.updateScore(inPreviewMode);
	}
	
	public void reset() {
		this.undoManager.discardAllEdits();
		this.transformations = new BigBangTransformationGraph();
		this.deselectTransformations();
	}

}

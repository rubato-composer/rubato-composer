package org.rubato.rubettes.bigbang.model.edits;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.graph.BigBangTransformationGraph;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

public class AddOrInsertOperationEdit extends AbstractUndoableEdit {
	
	private BigBangTransformationGraph graph;
	private AbstractOperation operation;
	private Integer startingState;
	private boolean isInsertion;
	
	public AddOrInsertOperationEdit(AbstractOperation operation, BigBangTransformationGraph graph) {
		this.graph = graph;
		this.operation = operation;
		this.startingState = this.graph.getInsertionState();
		if (this.startingState != null) {
			this.isInsertion = true;
		} else {
			this.isInsertion = false;
			this.startingState = this.graph.getSelectedCompositionState();
		}
	}
	
	public void execute() {
		Integer previouslySelectedState = this.graph.getSelectedCompositionState();
		if (this.isInsertion) {
			this.graph.setInsertionState(this.startingState);
		} else {
			this.graph.selectCompositionState(this.startingState);
		}
		this.graph.addOrInsertOperation(this.operation, false);
		this.graph.selectCompositionState(previouslySelectedState);
	}
	
	@Override
	public void undo() {
		super.undo();
		this.graph.removeOperation(this.operation);
	}
	
	@Override
	public void redo() {
		super.redo();
		this.execute();
	}
	
	@Override
	public String getPresentationName() {
		String name = this.isInsertion ? "Insert " : "Add ";
		return name + this.operation.toString();
	}

}

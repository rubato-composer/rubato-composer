package org.rubato.rubettes.bigbang.model.edits;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.graph.BigBangTransformationGraph;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

public class RemoveOperationEdit extends AbstractUndoableEdit {
	
	private BigBangTransformationGraph graph;
	private AbstractOperation operation;
	private boolean hasToBeReinserted;
	private int startingState;
	
	public RemoveOperationEdit(AbstractOperation operation, BigBangTransformationGraph graph) {
		this.graph = graph;
		this.operation = operation;
	}
	
	public void execute() {
		this.hasToBeReinserted = this.graph.removeEdge(this.operation);
	}
	
	@Override
	public void undo() {
		super.undo();
		if (this.hasToBeReinserted) {
			this.graph.setInsertionState(this.startingState);
		} else {
			this.graph.setInsertionState(null);
		}
		this.graph.addOrInsertOperation(this.operation, false);
	}
	
	@Override
	public void redo() {
		super.redo();
		this.execute();
	}
	
	@Override
	public String getPresentationName() {
		return "Remove " + this.operation.toString();
	}

}

package org.rubato.rubettes.bigbang.model.edits;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.graph.BigBangOperationGraph;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

public class RemoveOperationEdit extends AbstractUndoableEdit {
	
	private BigBangOperationGraph graph;
	private AbstractOperation operation;
	private boolean hasToBeReinserted;
	private Integer startingState;
	
	public RemoveOperationEdit(AbstractOperation operation, BigBangOperationGraph graph) {
		this.graph = graph;
		this.operation = operation;
	}
	
	public void execute() {
		this.startingState = this.graph.getEndpoints(this.operation).getFirst().getIndex();
		this.hasToBeReinserted = this.graph.removeOperation(this.operation);
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

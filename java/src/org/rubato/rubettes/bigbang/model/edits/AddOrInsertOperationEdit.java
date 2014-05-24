package org.rubato.rubettes.bigbang.model.edits;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.graph.BigBangOperationGraph;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

public class AddOrInsertOperationEdit extends AbstractUndoableEdit {
	
	private BigBangOperationGraph graph;
	private AbstractOperation operation;
	private Integer startingState;
	private boolean isInsertion;
	private AbstractOperation parallelOperation;
	
	public AddOrInsertOperationEdit(AbstractOperation operation, BigBangOperationGraph graph) {
		this.graph = graph;
		this.operation = operation;
		this.startingState = this.graph.getInsertionState();
		if (this.startingState != null) {
			this.isInsertion = true;
		} else {
			this.isInsertion = false;
			this.startingState = this.graph.getSelectedCompositionStateIndex();
		}
		this.parallelOperation = this.graph.getSelectedOperation();
	}
	
	public void execute() {
		Integer previouslySelectedState = this.graph.getSelectedCompositionStateIndex();
		if (this.isInsertion) {
			this.graph.setInsertionState(this.startingState);
		} else {
			this.graph.setInsertionState(null);
			this.graph.selectCompositionStateAt(this.startingState);
		}
		this.graph.selectOperation(this.parallelOperation);
		this.graph.addOrInsertOperation(this.operation, true);
		//only show selected state if insertion. otherwise show last (new) state
		if (this.isInsertion) {
			this.graph.selectCompositionStateAt(previouslySelectedState);
		}/* else {
			this.graph.selectCompositionState(null);
		}*/
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

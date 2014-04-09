package org.rubato.rubettes.bigbang.model.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class BigBangTransformationGraph extends DirectedSparseMultigraph<CompositionState,AbstractOperation> {
	
	private List<CompositionState> compositionStates;
	private CompositionState selectedCompositionState;
	private CompositionState insertionState;
	private AbstractOperation selectedOperation;
	private List<AbstractOperation> allOperationsInAddedOrder;
	//all edits in the graph in their logical order of execution 
	private List<AbstractOperation> allOperationsInLogicalOrder;
	//all edits executed on way to selectedCompositionState in order of execution
	private List<AbstractOperation> currentlyExecutedOperationsInOrder;
	//all states reached on way to selectedCompositionState and the time when they are reached
	private Map<CompositionState,Double> currentlyReachedStatesAndTimes;
	
	public BigBangTransformationGraph() {
		this.compositionStates = new ArrayList<CompositionState>();
		this.allOperationsInLogicalOrder = new ArrayList<AbstractOperation>();
		this.allOperationsInAddedOrder = new ArrayList<AbstractOperation>();
		this.addCompositionState();
	}
	
	private CompositionState addCompositionState() {
		CompositionState newState = new CompositionState(this.compositionStates.size());
		this.compositionStates.add(newState);
		return newState;
	}
	
	private CompositionState insertNewCompositionStateAfter(CompositionState state) {
		int stateIndex = this.compositionStates.indexOf(state);
		//increment indices of later states
		for (int i = stateIndex+1; i <  this.compositionStates.size(); i++) {
			this.compositionStates.get(i).incrementIndex();
		}
		//add new state
		CompositionState newState = new CompositionState(stateIndex+1);
		this.compositionStates.add(stateIndex+1, newState);
		this.addVertex(newState);
		return newState;
	}
	
	private void removeCompositionState(CompositionState state) {
		//remove state and decrement indices of later ones
		int stateIndex = this.compositionStates.indexOf(state);
		this.removeVertex(state);
		this.compositionStates.remove(stateIndex);
		for (int i = stateIndex; i <  this.compositionStates.size(); i++) {
			this.compositionStates.get(i).decrementIndex();
		}
	}
	
	public void update() {
		this.updateCurrentlyExecutedEditsAndStatesAndTimes();
	}
	
	/**
	 * sets the composition state to be shown. can be null, which means that the final state is shown
	 */
	public void selectCompositionState(CompositionState state) {
		this.selectedCompositionState = state;
		this.updateCurrentlyExecutedEditsAndStatesAndTimes();
	}
	
	public void selectCompositionStateAt(Integer index) {
		this.selectedCompositionState = this.getCompositionStateAt(index);
		this.updateCurrentlyExecutedEditsAndStatesAndTimes();
	}
	
	private CompositionState getCompositionStateAt(Integer index) {
		if (index != null && index < this.compositionStates.size()) {
			return this.compositionStates.get(index);
		}
		return null;
	}
	
	public void deselectCompositionStates() {
		this.selectCompositionState(null);
	}
	
	public CompositionState getSelectedCompositionState() {
		return this.selectedCompositionState;
	}
	
	public void selectOperation(AbstractOperation operation) {
		this.selectedOperation = operation;
	}
	
	public AbstractOperation getSelectedOperation() {
		return this.selectedOperation;
	}
	
	public void setInsertionState(CompositionState state) {
		this.insertionState = state;
	}
	
	public CompositionState getInsertionState() {
		return this.insertionState;
	}
	
	/**
	 * If the insertionState is selected, insert the given operation. Otherwise add.
	 * @return the state to be selected afterwards
	 */
	public CompositionState addOrInsertOperation(AbstractOperation edit, boolean inPreviewMode) {
		if (this.insertionState != null) {
			this.insertOperation(edit, this.insertionState);
			CompositionState stateToBeSelected = this.compositionStates.get(this.compositionStates.indexOf(this.insertionState)+1);
			if (!inPreviewMode) {
				this.insertionState = null;
			}
			return stateToBeSelected;
		}
		this.addOperation(edit);
		return this.getLastState();
	}
	
	private boolean insertOperation(AbstractOperation edit, CompositionState state) {
		CompositionState newState = this.insertNewCompositionStateAfter(state);
		
		//reconnect edges to new state
		for (AbstractOperation currentOperation : new TreeSet<AbstractOperation>(this.getOutEdges(state))) {
			CompositionState currentEndpoint = this.getEndpoints(currentOperation).getSecond();
			this.removeEdge(currentOperation);
			this.addEdge(currentOperation, newState, currentEndpoint, EdgeType.DIRECTED);
		}
		
		//add new edge
		boolean added = this.addEdge(edit, state, newState);
		if (added) {
			int index = this.getInsertionIndex(newState);
			this.allOperationsInLogicalOrder.add(index, edit);
			this.allOperationsInAddedOrder.add(edit);
			this.updateCurrentlyExecutedEditsAndStatesAndTimes();
		}
		return added;
	}
	
	//returns the index in operationsInAddedOrder before all outgoing edges of the given state 
	private int getInsertionIndex(CompositionState state) {
		int minIndex = Integer.MAX_VALUE;
		for (AbstractOperation currentEdit : this.getOutEdges(state)) {
			minIndex = Math.min(minIndex, this.allOperationsInLogicalOrder.indexOf(currentEdit));
		}
		return minIndex;
	}
	
	private boolean addOperation(AbstractOperation edit) {
		//startingVertex is either current selected vertex or the last vertex
		CompositionState startVertex, endVertex = null;
		if (this.selectedOperation != null) {
			startVertex = this.getSource(this.selectedOperation);
			endVertex = this.getDest(this.selectedOperation);
		} else if (this.selectedCompositionState != null) {
			startVertex = this.selectedCompositionState;
			//delete selectedCompositionState for appropriate preview to be shown
			this.selectedCompositionState = null;
		} else {
			startVertex = this.getLastState();
		}
		
		if (endVertex == null) {
			endVertex = this.addCompositionState();
		}
		
		boolean added = this.addEdge(edit, startVertex, endVertex);
		if (added) {
			this.allOperationsInLogicalOrder.add(edit);
			this.allOperationsInAddedOrder.add(edit);
			this.updateCurrentlyExecutedEditsAndStatesAndTimes();
		}
		
		return added;
	}
	
	private void replaceOperation(AbstractOperation oldOperation, AbstractOperation newOperation) {
		this.addEdge(newOperation, this.getSource(oldOperation), this.getDest(oldOperation));
		this.removeEdge(oldOperation);
		int index = this.allOperationsInLogicalOrder.indexOf(oldOperation);
		this.allOperationsInLogicalOrder.set(index, newOperation);
		index = this.allOperationsInAddedOrder.indexOf(oldOperation);
		this.allOperationsInAddedOrder.set(index, newOperation);
		this.updateCurrentlyExecutedEditsAndStatesAndTimes();
	}
	
	private void addParallelOperation(AbstractOperation operation, AbstractOperation newOperation) {
		this.addEdge(newOperation, this.getSource(operation), this.getDest(operation));
		int index = this.allOperationsInLogicalOrder.indexOf(operation)+1;
		this.allOperationsInLogicalOrder.add(index, newOperation);
		this.allOperationsInAddedOrder.add(newOperation);
		this.updateCurrentlyExecutedEditsAndStatesAndTimes();
	}
	
	/**
	 * Splits the currently selected operation and all parallel operations at the given position
	 * @param position in seconds
	 */
	public void splitSelectedAndParallelOperations(double position) {
		double operationStartingTime = this.getOperationStartingTime(this.selectedOperation);
		double operationDuration = this.selectedOperation.getDuration();
		if (operationStartingTime < position && position < operationStartingTime+operationDuration) {
			double ratio = (position-operationStartingTime)/operationDuration;
			List<AbstractOperation> parallelOperations = new ArrayList<AbstractOperation>(this.findEdgeSet(this.getSource(this.selectedOperation), this.getDest(this.selectedOperation)));
			parallelOperations.remove(this.selectedOperation);
			
			//first split selected operation
			List<AbstractOperation> lastSplitOperations = this.selectedOperation.getSplitOperations(ratio);
			//insert first part before operation that is split
			this.insertOperation(lastSplitOperations.get(0), this.getSource(this.selectedOperation));
			//replace operation with second part
			this.replaceOperation(this.selectedOperation, lastSplitOperations.get(1));
			
			//now split and add parallel operations if there are any
			for (AbstractOperation currentOperation : parallelOperations) {
				List<AbstractOperation> currentSplitOperations = currentOperation.getSplitOperations(ratio);
				this.removeOperation(currentOperation);
				this.addParallelOperation(lastSplitOperations.get(0), currentSplitOperations.get(0));
				this.addParallelOperation(lastSplitOperations.get(1), currentSplitOperations.get(1));
				lastSplitOperations = currentSplitOperations;
			}
		}
	}
	
	/*
	 * updates currentOperationsInExecutedOrder to include all operations that start and end between state 0
	 * and the selected composition states. simultaneously updates currentlyReachedStatesAndTimes
	 */
	private void updateCurrentlyExecutedEditsAndStatesAndTimes() {
		if (!this.containsVertex(this.selectedCompositionState)) {
			this.selectedCompositionState = null;
		}
		if (!this.containsEdge(this.selectedOperation)) {
			this.selectedOperation = null;
		}
		Set<AbstractOperation> currentlyExecutedEdits = new HashSet<AbstractOperation>();
		this.currentlyReachedStatesAndTimes = new HashMap<CompositionState,Double>();
		this.currentlyReachedStatesAndTimes.put(this.getFirstState(), 0.0);
		Set<CompositionState> currentStates = new HashSet<CompositionState>();
		currentStates.add(this.getFirstState());
		while (currentStates.size() > 0) {
			Set<CompositionState> nextStates = new HashSet<CompositionState>();
			for (CompositionState currentState : currentStates) {
				Collection<AbstractOperation> currentOutEdges = this.getOutEdges(currentState);
				if (currentOutEdges != null) {
					for (AbstractOperation currentEdit : this.getOutEdges(currentState)) {
						CompositionState editDestination = this.getDest(currentEdit);
						if (editDestination.equals(this.getShownState()) || this.connectedByPath(editDestination, this.getShownState())) {
							currentlyExecutedEdits.add(currentEdit);
							nextStates.add(editDestination);
							double timeReached = this.currentlyReachedStatesAndTimes.get(currentState) + currentEdit.getDuration();
							this.currentlyReachedStatesAndTimes.put(editDestination, timeReached);
						}
					}
				}
			}
			currentStates = nextStates;
		}
		this.currentlyExecutedOperationsInOrder = this.putOperationsInLogicalOrder(currentlyExecutedEdits);
	}
	
	private CompositionState getShownState() {
		if (this.selectedCompositionState != null) {
			return this.selectedCompositionState;
		}
		return this.getLastState();
	}
	
	//returns a list with the operations in the given set put in order they have be to be executed in
	private List<AbstractOperation> putOperationsInLogicalOrder(Set<AbstractOperation> edits) {
		List<AbstractOperation> editsInOrder = new ArrayList<AbstractOperation>();
		for (AbstractOperation currentEdit: this.allOperationsInLogicalOrder) {
			if (edits.contains(currentEdit)) {
				editsInOrder.add(currentEdit);
			}
		}
		return editsInOrder;
	}

	private boolean connectedByPath(CompositionState state1, CompositionState state2) {
		DijkstraShortestPath<CompositionState,AbstractOperation> dijkstra = new DijkstraShortestPath<CompositionState,AbstractOperation>(this);
		return dijkstra.getPath(state1, state2).size() > 0;
	}
	
	/**
	 * @return true if composition states needed to be consolidated
	 */
	public boolean removeOperation(AbstractOperation operation) {
		if (this.containsEdge(operation)) {
			CompositionState operationInitialpoint = this.getEndpoints(operation).getFirst();
			CompositionState operationEndpoint = this.getEndpoints(operation).getSecond();
			super.removeEdge(operation);
			this.allOperationsInLogicalOrder.remove(operation);
			this.allOperationsInAddedOrder.remove(operation);
			
			//consolidate nodes if no edges there anymore
			if (!this.connectedByPath(operationInitialpoint, operationEndpoint)) {
				//needs to be done this way :( changing vertex names leads to problems with jung
				this.combineStates(operationInitialpoint, operationEndpoint);
				this.updateCurrentlyExecutedEditsAndStatesAndTimes();
				return true;
			}
			
			this.updateCurrentlyExecutedEditsAndStatesAndTimes();
			return false;
		}
		return false;
	}
	
	/*
	 * 
	 */
	private void combineStates(CompositionState state1, CompositionState state2) {
		//move all state2 edges to state1
		for (AbstractOperation currentOperation : this.getOutEdges(state2)) {
			CompositionState endPoint = this.getEndpoints(currentOperation).getSecond();
			this.removeEdge(currentOperation);
			this.addEdge(currentOperation, state1, endPoint, EdgeType.DIRECTED);
		}
		this.removeCompositionState(state2);
	}
	
	public AbstractOperation removeLastAddedOperation() {
		AbstractOperation lastAddedEdit = this.getLastAddedOperation();
		this.removeOperation(lastAddedEdit);
		return lastAddedEdit;
	}
	
	public CompositionState getFirstState() {
		return this.compositionStates.get(0);
	}
	
	public CompositionState getCompositionStateAt(int index) {
		return this.compositionStates.get(index);
	}
	
	public CompositionState getLastState() {
		return this.compositionStates.get(this.compositionStates.size()-1);
	}
	
	public void setDurations(double duration) {
		for (AbstractOperation currentEdit : this.edges.keySet()) {
			currentEdit.setDuration(duration);
		}
	}
	
	public AbstractOperation getLastAddedOperation() {
		if (this.getEdgeCount() > 0) {
			return this.allOperationsInAddedOrder.get(this.allOperationsInAddedOrder.size()-1);
		}
		return null;
	}
	
	public List<AbstractOperation> getCurrentlyExecutedOperationsInOrder() {
		return this.currentlyExecutedOperationsInOrder;
	}
	
	public double getCurrentTotalAnimationTime() {
		return this.currentlyReachedStatesAndTimes.get(this.getShownState());
	}
	
	public double getOperationStartingTime(AbstractOperation edit) {
		return this.currentlyReachedStatesAndTimes.get(this.getSource(edit));
	}

}

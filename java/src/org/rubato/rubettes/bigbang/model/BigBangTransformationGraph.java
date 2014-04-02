package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class BigBangTransformationGraph extends DirectedSparseMultigraph<Integer,AbstractOperationEdit> {
	
	private Integer selectedCompositionState;
	private Integer insertionState;
	private AbstractOperationEdit selectedOperation;
	private List<AbstractOperationEdit> allOperationsInAddedOrder;
	//all edits in the graph in their logical order of execution 
	private List<AbstractOperationEdit> allOperationsInLogicalOrder;
	//all edits executed on way to selectedCompositionState in order of execution
	private List<AbstractOperationEdit> currentlyExecutedOperationsInOrder;
	//all states reached on way to selectedCompositionState and the time when they are reached
	private Map<Integer,Double> currentlyReachedStatesAndTimes;
	
	public BigBangTransformationGraph() {
		this.allOperationsInLogicalOrder = new ArrayList<AbstractOperationEdit>();
		this.allOperationsInAddedOrder = new ArrayList<AbstractOperationEdit>();
		this.addVertex(0);
	}
	
	public void update() {
		this.updateCurrentlyExecutedEditsAndStatesAndTimes();
	}
	
	/**
	 * sets the composition state to be shown. can be null, which means that the final state is shown
	 */
	public void selectCompositionState(Integer vertex) {
		if (vertex == null || vertex <= this.getEdgeCount()) {
			this.selectedCompositionState = vertex;
			this.updateCurrentlyExecutedEditsAndStatesAndTimes();
		}
	}
	
	public void deselectCompositionStates() {
		this.selectCompositionState(null);
	}
	
	public Integer getSelectedCompositionState() {
		return this.selectedCompositionState;
	}
	
	public void selectOperation(AbstractOperationEdit operation) {
		this.selectedOperation = operation;
	}
	
	public AbstractOperationEdit getSelectedOperation() {
		return this.selectedOperation;
	}
	
	public void setInsertionState(Integer state) {
		this.insertionState = state;
	}
	
	/**
	 * If the insertionState is selected, insert the given operation. Otherwise add.
	 * @return the state to be selected afterwards
	 */
	public Integer addOrInsertOperation(AbstractOperationEdit edit, boolean inPreviewMode) {
		if (this.insertionState != null) {
			this.insertOperation(edit, this.insertionState);
			int stateToBeSelected = this.insertionState+1;
			if (!inPreviewMode) {
				this.insertionState = null;
			}
			return stateToBeSelected;
		}
		this.addOperation(edit);
		return this.getLastState();
	}
	
	private boolean insertOperation(AbstractOperationEdit edit, Integer state) {
		Integer endingVertex = this.getVertexCount();
		this.addVertex(endingVertex);
		//move all edges including the ones involving the given state back by 1
		this.moveEdges(state-1, 1);
		
		boolean added = this.addEdge(edit, state, state+1);
		if (added) {
			int index = this.getInsertionIndex(state+1);
			this.allOperationsInLogicalOrder.add(index, edit);
			this.allOperationsInAddedOrder.add(edit);
			this.updateCurrentlyExecutedEditsAndStatesAndTimes();
		}
		return added;
	}
	
	//returns the index in operationsInAddedOrder before all outgoing edges of the given state 
	private int getInsertionIndex(Integer state) {
		int minIndex = Integer.MAX_VALUE;
		for (AbstractOperationEdit currentEdit : this.getOutEdges(state)) {
			minIndex = Math.min(minIndex, this.allOperationsInLogicalOrder.indexOf(currentEdit));
		}
		return minIndex;
	}
	
	private boolean addOperation(AbstractOperationEdit edit) {
		//startingVertex is either current selected vertex or the last vertex
		Integer startVertex, endVertex = null;
		if (this.selectedOperation != null) {
			startVertex = this.getSource(this.selectedOperation);
			endVertex = this.getDest(this.selectedOperation);
		} else if (this.selectedCompositionState != null) {
			startVertex = this.selectedCompositionState;
			//delete selectedCompositionState for appropriate preview to be shown
			this.selectedCompositionState = null;
		} else {
			startVertex = this.getVertexCount()-1;
		}
		
		if (endVertex == null) {
			endVertex = this.getVertexCount();
			this.addVertex(endVertex);
		}
		
		boolean added = this.addEdge(edit, startVertex, endVertex);
		if (added) {
			this.allOperationsInLogicalOrder.add(edit);
			this.allOperationsInAddedOrder.add(edit);
			this.updateCurrentlyExecutedEditsAndStatesAndTimes();
		}
		
		return added;
	}
	
	private void replaceOperation(AbstractOperationEdit oldOperation, AbstractOperationEdit newOperation) {
		this.addEdge(newOperation, this.getSource(oldOperation), this.getDest(oldOperation));
		this.removeEdge(oldOperation);
		int index = this.allOperationsInLogicalOrder.indexOf(oldOperation);
		this.allOperationsInLogicalOrder.set(index, newOperation);
		index = this.allOperationsInAddedOrder.indexOf(oldOperation);
		this.allOperationsInAddedOrder.set(index, newOperation);
		this.updateCurrentlyExecutedEditsAndStatesAndTimes();
	}
	
	private void addParallelOperation(AbstractOperationEdit operation, AbstractOperationEdit newOperation) {
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
			List<AbstractOperationEdit> parallelOperations = new ArrayList<AbstractOperationEdit>(this.findEdgeSet(this.getSource(this.selectedOperation), this.getDest(this.selectedOperation)));
			parallelOperations.remove(this.selectedOperation);
			
			//first split selected operation
			List<AbstractOperationEdit> lastSplitOperations = this.selectedOperation.getSplitOperations(ratio);
			//insert first part before operation that is split
			this.insertOperation(lastSplitOperations.get(0), this.getSource(this.selectedOperation));
			//replace operation with second part
			this.replaceOperation(this.selectedOperation, lastSplitOperations.get(1));
			
			//now split and add parallel operations if there are any
			for (AbstractOperationEdit currentOperation : parallelOperations) {
				List<AbstractOperationEdit> currentSplitOperations = currentOperation.getSplitOperations(ratio);
				this.removeOperation(currentOperation);
				this.addParallelOperation(lastSplitOperations.get(0), currentSplitOperations.get(0));
				this.addParallelOperation(lastSplitOperations.get(1), currentSplitOperations.get(1));
				lastSplitOperations = currentSplitOperations;
			}
		}
	}
	
	/*
	 * updates currentOperationsInExecutedOrder to include all operations that start and end between state 0 and the
	 * selected composition states. simultaneously updates currentlyReachedStatesAndTimes
	 */
	private void updateCurrentlyExecutedEditsAndStatesAndTimes() {
		if (!this.containsVertex(this.selectedCompositionState)) {
			this.selectedCompositionState = null;
		}
		if (!this.containsEdge(this.selectedOperation)) {
			this.selectedOperation = null;
		}
		Set<AbstractOperationEdit> currentlyExecutedEdits = new HashSet<AbstractOperationEdit>();
		this.currentlyReachedStatesAndTimes = new TreeMap<Integer,Double>();
		this.currentlyReachedStatesAndTimes.put(0, 0.0);
		Set<Integer> currentStates = new TreeSet<Integer>();
		currentStates.add(0);
		while (currentStates.size() > 0) {
			Set<Integer> nextStates = new TreeSet<Integer>();
			for (Integer currentState : currentStates) {
				for (AbstractOperationEdit currentEdit : this.getOutEdges(currentState)) {
					Integer editDestination = this.getDest(currentEdit);
					if (editDestination == this.getShownState() || this.connectedByPath(editDestination, this.getShownState())) {
						currentlyExecutedEdits.add(currentEdit);
						nextStates.add(editDestination);
						double timeReached = this.currentlyReachedStatesAndTimes.get(currentState) + currentEdit.getDuration();
						this.currentlyReachedStatesAndTimes.put(editDestination, timeReached);
					}
				}
			}
			currentStates = nextStates;
		}
		this.currentlyExecutedOperationsInOrder = this.putOperationsInLogicalOrder(currentlyExecutedEdits);
	}
	
	private Integer getShownState() {
		if (this.selectedCompositionState != null) {
			return this.selectedCompositionState;
		}
		return this.getLastState();
	}
	
	//returns a list with the operations in the given set put in order they have be to be executed in
	private List<AbstractOperationEdit> putOperationsInLogicalOrder(Set<AbstractOperationEdit> edits) {
		List<AbstractOperationEdit> editsInOrder = new ArrayList<AbstractOperationEdit>();
		for (AbstractOperationEdit currentEdit: this.allOperationsInLogicalOrder) {
			if (edits.contains(currentEdit)) {
				editsInOrder.add(currentEdit);
			}
		}
		return editsInOrder;
	}

	private boolean connectedByPath(Integer state1, Integer state2) {
		DijkstraShortestPath<Integer,AbstractOperationEdit> dijkstra = new DijkstraShortestPath<Integer,AbstractOperationEdit>(this);
		return dijkstra.getPath(state1, state2).size() > 0;
	}
	
	public boolean removeOperation(AbstractOperationEdit operation) {
		if (this.containsEdge(operation)) {
			Integer operationInitialpoint = this.getEndpoints(operation).getFirst();
			Integer operationEndpoint = this.getEndpoints(operation).getSecond();
			super.removeEdge(operation);
			this.allOperationsInLogicalOrder.remove(operation);
			this.allOperationsInAddedOrder.remove(operation);
			
			//consolidate nodes if no edges there anymore
			if (!this.connectedByPath(operationInitialpoint, operationEndpoint)) {
				//needs to be done this way :( changing vertex names leads to problems with jung
				this.moveEdges(operationInitialpoint, -1);
				//remove the previous last state
				this.removeVertex(this.getLastState());
			}
			this.updateCurrentlyExecutedEditsAndStatesAndTimes();
			
			return true;
		}
		return false;
	}
	
	/*
	 * moves the edgePoints of all edges after the given state by the given amount (can be negative),
	 * e.g. -1 moves all edges forward by 1
	 */
	private void moveEdges(Integer state, int amount) {
		for (AbstractOperationEdit currentEdge : new ArrayList<AbstractOperationEdit>(this.getEdges())) {
			int startPoint = this.getEndpoints(currentEdge).getFirst();
			int endPoint = this.getEndpoints(currentEdge).getSecond();
			if (startPoint > state && endPoint > state) {
				if (startPoint > state) {
					startPoint += amount;
				}
				if (endPoint > state) {
					endPoint += amount;
				}
			}
			super.removeEdge(currentEdge);
			this.addEdge(currentEdge, startPoint, endPoint, EdgeType.DIRECTED);
		}
	}
	
	public AbstractOperationEdit removeLastAddedOperation() {
		AbstractOperationEdit lastAddedEdit = this.getLastAddedOperation();
		this.removeOperation(lastAddedEdit);
		return lastAddedEdit;
	}
	
	public Integer getLastState() {
		return this.getVertexCount()-1;
	}
	
	public void setDurations(double duration) {
		for (AbstractOperationEdit currentEdit : this.edges.keySet()) {
			currentEdit.setDuration(duration);
		}
	}
	
	public AbstractOperationEdit getLastAddedOperation() {
		if (this.getEdgeCount() > 0) {
			return this.allOperationsInAddedOrder.get(this.allOperationsInAddedOrder.size()-1);
		}
		return null;
	}
	
	public List<AbstractOperationEdit> getCurrentlyExecutedOperationsInOrder() {
		return this.currentlyExecutedOperationsInOrder;
	}
	
	public double getCurrentTotalAnimationTime() {
		return this.currentlyReachedStatesAndTimes.get(this.getShownState());
	}
	
	public double getOperationStartingTime(AbstractOperationEdit edit) {
		return this.currentlyReachedStatesAndTimes.get(this.getSource(edit));
	}

}

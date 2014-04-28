package org.rubato.rubettes.bigbang.model.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

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
		this.addVertex(newState);
		return newState;
	}
	
	private CompositionState insertNewCompositionStateAfter(CompositionState state) {
		int stateIndex = this.compositionStates.indexOf(state);
		if (stateIndex >= 0) {
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
		return null;
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
	
	public Integer getSelectedCompositionState() {
		if (this.selectedCompositionState != null) {
			return this.selectedCompositionState.getIndex();
		}
		return null;
	}
	
	public void selectOperation(AbstractOperation operation) {
		this.selectedOperation = operation;
	}
	
	public AbstractOperation getSelectedOperation() {
		return this.selectedOperation;
	}
	
	public void setInsertionState(Integer stateIndex) {
		if (stateIndex != null && stateIndex >= 0) {
			this.insertionState = this.compositionStates.get(stateIndex);
		} else {
			this.insertionState = null;
		}
	}
	
	public Integer getInsertionState() {
		if (this.insertionState != null) {
			return this.insertionState.getIndex();
		}
		return null;
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
	
	public BigBangTransformationGraph clone(BigBangModel model) {
		BigBangTransformationGraph clone = new BigBangTransformationGraph();
		while (clone.compositionStates.size() < this.compositionStates.size()) {
			clone.addCompositionState();
		}
		clone.cloneOperations(this, model);
		return clone;
	}
	
	//adds all operations of the given graph to this graph and connects them to the given model
	private void cloneOperations(BigBangTransformationGraph otherGraph, BigBangModel model) {
		this.allOperationsInAddedOrder = new ArrayList<AbstractOperation>();
		for (AbstractOperation currentOperation : otherGraph.allOperationsInAddedOrder) {
			//clone operation and add
			AbstractOperation clonedOperation = currentOperation.clone(model);
			this.allOperationsInAddedOrder.add(clonedOperation);
			//add edge to graph
			Pair<CompositionState> currentEndpoints = otherGraph.getEndpoints(currentOperation);
			CompositionState currentFirstState = this.compositionStates.get(currentEndpoints.getFirst().getIndex());
			CompositionState currentSecondState = this.compositionStates.get(currentEndpoints.getSecond().getIndex());
			this.addEdge(clonedOperation, currentFirstState, currentSecondState);
		}
		//update logical order and executed operations TODO: BUILD IN AS BELOW!!!!!
		for (AbstractOperation currentOperation : otherGraph.allOperationsInLogicalOrder) {
			int addedIndex = otherGraph.allOperationsInAddedOrder.indexOf(currentOperation);
			this.allOperationsInLogicalOrder.add(this.allOperationsInAddedOrder.get(addedIndex));
		}
		this.updateCurrentlyExecutedEditsAndStatesAndTimes();
	}

	private static final String GRAPH_TAG = "TransformationGraph";
	private static final String NUMBER_OF_STATES_ATTR = "numberOfStates";
	private static final String OPERATION_TAG = "Operation";
	private static final String CLASSNAME_ATTR = "className";
	private static final String HEAD_ATTR = "head";
	private static final String TAIL_ATTR = "tail";
	private static final String LOGICAL_POSITION_ATTR = "logicalPosition";
	
	public void toXML(XMLWriter writer) {
		writer.openBlock(GRAPH_TAG, NUMBER_OF_STATES_ATTR, this.compositionStates.size());
		for (AbstractOperation currentOperation : this.allOperationsInAddedOrder) {
			Pair<CompositionState> currentEndpoints = this.getEndpoints(currentOperation);
			int currentHead = currentEndpoints.getFirst().getIndex();
			int currentTail = currentEndpoints.getSecond().getIndex();
			writer.openBlock(OPERATION_TAG, CLASSNAME_ATTR, currentOperation.getClass().getName(),
					HEAD_ATTR, currentHead, TAIL_ATTR, currentTail,
					LOGICAL_POSITION_ATTR, this.allOperationsInLogicalOrder.indexOf(currentOperation));
			currentOperation.toXML(writer);
			writer.closeBlock();
		}
		writer.closeBlock();
	}
	
	public static BigBangTransformationGraph fromXML(BigBangModel model, XMLReader reader, Element element) {
		BigBangTransformationGraph graph = new BigBangTransformationGraph();
		Element graphElement = XMLReader.getChild(element, GRAPH_TAG);
		int numberOfStates = XMLReader.getIntAttribute(graphElement, NUMBER_OF_STATES_ATTR, 0);
		while (graph.getVertexCount() < numberOfStates) {
			graph.addCompositionState();
		}
		Element currentOperationElement = XMLReader.getChild(graphElement, OPERATION_TAG);
		while (currentOperationElement != null) {
			String currentName = XMLReader.getStringAttribute(currentOperationElement, CLASSNAME_ATTR);
			int currentHead = XMLReader.getIntAttribute(currentOperationElement, HEAD_ATTR, 0);
			int currentTail = XMLReader.getIntAttribute(currentOperationElement, TAIL_ATTR, 0);
			int currentLogicalPosition = XMLReader.getIntAttribute(currentOperationElement, LOGICAL_POSITION_ATTR, 0);
			//System.out.println(currentName + " " +model + " " + reader + " " + currentOperationElement);
			try {
				Class<? extends AbstractOperation> operationClass = (Class<? extends AbstractOperation>)Class.forName(currentName);
				/*AbstractOperation currentOperation = (AbstractOperation)operationClass
						.getDeclaredMethod("fromXML", BigBangModel.class, XMLReader.class, Element.class)
						.invoke(model, reader, currentOperationElement);*/
				AbstractOperation currentOperation = operationClass
						.getDeclaredConstructor(BigBangModel.class, XMLReader.class, Element.class)
						.newInstance(model, reader, currentOperationElement);
				graph.allOperationsInAddedOrder.add(currentOperation);
				graph.addEdge(currentOperation, graph.compositionStates.get(currentHead), graph.compositionStates.get(currentTail));
				graph.allOperationsInLogicalOrder.add(currentLogicalPosition, currentOperation);
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentOperationElement = XMLReader.getNextSibling(currentOperationElement, OPERATION_TAG);
		}
		graph.updateCurrentlyExecutedEditsAndStatesAndTimes();
		return graph;
	}

}

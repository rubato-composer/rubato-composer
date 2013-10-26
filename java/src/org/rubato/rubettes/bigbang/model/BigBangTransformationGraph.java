package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.util.DenotatorPath;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class BigBangTransformationGraph extends DirectedSparseMultigraph<Integer,AbstractOperationEdit> {
	
	private UndoRedoModel model;
	private Integer selectedCompositionState;
	
	public BigBangTransformationGraph(UndoRedoModel model) {
		this.model = model;
		this.addVertex(0);
	}
	
	/**
	 * sets the composition state to be shown. can be null, which means that the final state is shown
	 */
	public void selectCompositionState(Integer vertex, boolean update) {
		if (vertex == null || vertex <= this.getEdgeCount()) {
			this.selectedCompositionState = vertex;
			if (update) {
				this.updateComposition(false);
			}
		}
	}
	
	public void previewInsertedTransformationAt(AbstractTransformationEdit edit, Integer state) {
		this.insertOperation(edit, state, true);
		this.removeOperation(edit, false);
	}
	
	public void previewTransformationAtEnd(AbstractTransformationEdit edit) {
		this.add(edit, true);
		this.removeLastOperation(false);
	}
	
	public boolean insertOperation(AbstractOperationEdit edit, Integer state) {
		return this.insertOperation(edit, state, false);
	}
	
	private boolean insertOperation(AbstractOperationEdit edit, Integer state, boolean inPreviewMode) {
		Integer endingVertex = this.getVertexCount();
		this.addVertex(endingVertex);
		//move all edges including the ones involving the given state back by 1
		this.moveEdges(state-1, 1);
		
		boolean added = this.addEdge(edit, state, state+1);
		if (added) {
			//select right composition state so that new transformation is shown!!
			Integer previouslySelectedState = this.selectedCompositionState;
			if (inPreviewMode && previouslySelectedState != null && previouslySelectedState <= state) {
				this.selectCompositionState(state+1, false);
			}
			this.updateComposition(inPreviewMode);
			//deselect state after operation in case it was selected for preview purposes
			if (inPreviewMode && previouslySelectedState != null && previouslySelectedState <= state) {
				this.selectCompositionState(previouslySelectedState, false);
			//if state was selected before, select the one after the inserted operation 
			} else if (previouslySelectedState != null && previouslySelectedState == state) {
				this.selectCompositionState(state+1, true);
			}
		}
		return added;
	}
	
	public boolean addOperation(AbstractOperationEdit edit) {
		return this.add(edit, false);
	}
	
	private boolean add(AbstractOperationEdit edit, boolean inPreviewMode) {
		//startingVertex is either current selected vertex or the last vertex
		Integer startVertex;
		Integer previouslySelectedCompositionState = this.selectedCompositionState;
		if (this.selectedCompositionState != null) {
			startVertex = this.selectedCompositionState;
			//delete selectedCompositionState for appropriate preview to be shown
			this.selectedCompositionState = null;
		} else {
			startVertex = this.getVertexCount()-1;
		}
		
		Integer endVertex = this.getVertexCount();
		this.addVertex(endVertex);
		
		boolean added = this.addEdge(edit, startVertex, endVertex);
		if (added) {
			//deselect composition state so that new transformation is shown!!
			if (!inPreviewMode) {
				this.model.deselectCompositionStates();
			}
			this.updateComposition(inPreviewMode);
		}
		
		//restore selected composition state
		if (inPreviewMode == true && previouslySelectedCompositionState != null) {
			this.selectedCompositionState = previouslySelectedCompositionState;
		}
		return added;
	}
	
	public void updateComposition(boolean inPreviewMode) {
		if (this.getEdgeCount() > 0) {
			List<Map<DenotatorPath,DenotatorPath>> pathDifferences = new ArrayList<Map<DenotatorPath,DenotatorPath>>();
			
			List<AbstractOperationEdit> shortestPath = this.getCurrentShortestPath();
			
			AbstractOperationEdit lastEdgeOfPath = shortestPath.get(shortestPath.size()-1); 
			lastEdgeOfPath.setInPreviewMode(inPreviewMode);
			//TODO pretty bad...
			lastEdgeOfPath.getScoreManager().resetScore();
			
			//System.out.println(this + " " + lastEdgeOfPath);
			for (int i = 0; i < shortestPath.size(); i++) {
				//only send composition change with last one!!!!!!
				pathDifferences = shortestPath.get(i).execute(pathDifferences, i==shortestPath.size()-1);
			}
		}
	}
	
	public List<AbstractOperationEdit> getCurrentShortestPath() {
		Integer shownState = this.getLastState();
		if (this.selectedCompositionState != null) {
			shownState = this.selectedCompositionState;
		}
		DijkstraShortestPath<Integer,AbstractOperationEdit> dijkstra = new DijkstraShortestPath<Integer,AbstractOperationEdit>(this);
		return dijkstra.getPath(0, shownState);
	}
	
	public boolean removeOperation(AbstractOperationEdit operation, boolean update) {
		if (this.containsEdge(operation)) {
			Integer operationInitialpoint = this.getEndpoints(operation).getFirst();
			super.removeEdge(operation);
			//needs to be done this way :( changing vertex names leads to problems with jung
			this.moveEdges(operationInitialpoint, -1);
			//remove the previous last state
			this.removeVertex(this.getLastState());
			if (update) {
				this.updateComposition(false);
			}
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
	
	public AbstractOperationEdit removeLastOperation(boolean update) {
		AbstractOperationEdit lastEdit = this.getLastEdit();
		this.removeEdge(lastEdit);
		this.removeVertex(this.getLastState());
		if (update) {
			this.updateComposition(false);
		}
		return lastEdit;
	}
	
	public Integer getLastState() {
		return this.getVertexCount()-1;
	}
	
	public AbstractOperationEdit getLastEdit() {
		//TODO won't work for several edges going in last state! AND won't work with edges ending in old vertices!!
		return this.getInEdges(this.getEdgeCount()).iterator().next();
	}
	
	public void setDurations(double duration) {
		for (AbstractOperationEdit currentEdit : this.edges.keySet()) {
			currentEdit.setDuration(duration);
		}
	}

}

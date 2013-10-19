package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.util.DenotatorPath;

import sun.tools.tree.ThisExpression;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
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
	public void selectCompositionState(Integer vertex) {
		if (vertex == null || vertex <= this.getEdgeCount()) {
			this.selectedCompositionState = vertex;
			this.updateComposition(false);
		}
	}
	
	public void previewTransformationAtEnd(AbstractTransformationEdit edit) {
		this.add(edit, true);
		this.removeLastWithoutUpdate();
	}
	
	public boolean add(AbstractOperationEdit edit) {
		return this.add(edit, false);
	}
	
	private boolean add(AbstractOperationEdit edit, boolean inPreviewMode) {
		//startingVertex is either current selected vertex or the last vertex
		Integer startingVertex;
		Integer previouslySelectedCompositionState = this.selectedCompositionState;
		if (this.selectedCompositionState != null) {
			startingVertex = this.selectedCompositionState;
			//delete selectedCompositionState for appropriate preview to be shown
			this.selectedCompositionState = null;
		} else {
			startingVertex = this.getVertexCount()-1;
		}
		
		Integer endingVertex = this.getVertexCount();
		this.addVertex(endingVertex);
		boolean added = this.addEdge(edit, startingVertex, endingVertex);
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
			//TODO for now just sets inPreviewMode for the last edge.
			AbstractOperationEdit lastEdge = this.getLastEdit(); 
			lastEdge.setInPreviewMode(inPreviewMode);
			//TODO pretty bad...
			lastEdge.getScoreManager().resetScore();
			
			List<AbstractOperationEdit> shortestPath = this.getCurrentShortestPath();
			
			if (shortestPath != null) {
				for (int i = 0; i < shortestPath.size(); i++) {
					//only send composition change with last one!!!!!!
					pathDifferences = shortestPath.get(i).execute(pathDifferences, i==shortestPath.size()-1);
				}
			} else {
				lastEdge.getScoreManager().fireCompositionChange();
			}
		}
	}
	
	public List<AbstractOperationEdit> getCurrentShortestPath() {
		Integer shownState = this.getLastState();
		if (this.selectedCompositionState != null) {
			shownState = this.selectedCompositionState;
		}
		if (shownState != null) {
			DijkstraShortestPath<Integer,AbstractOperationEdit> dijkstra = new DijkstraShortestPath<Integer,AbstractOperationEdit>(this);
			return dijkstra.getPath(0, shownState);
		}
		return null;
	}
	
	public boolean removeOperation(AbstractOperationEdit operation) {
		if (this.containsEdge(operation)) {
			Integer operationInitialpoint = this.getEndpoints(operation).getFirst();
			super.removeEdge(operation);
			/*//iterate through all edges leaving the endpoint of the removed one, remove them and reconnect them to their
			//respective end point and the initial point of the removed one
			for (AbstractOperationEdit currentOutEdge : this.getOutEdges(operationEndpoint)) {
				CompositionState currentEdgeEndpoint = this.getEndpoints(currentOutEdge).getSecond();
				super.removeEdge(currentOutEdge);
				this.addEdge(currentOutEdge, operationInitialpoint, currentEdgeEndpoint, EdgeType.DIRECTED);
			}*/
			//needs to be done this way :( changing vertex names leads to problems with jung
			for (AbstractOperationEdit currentEdge : new ArrayList<AbstractOperationEdit>(this.getEdges())) {
				int initialPoint = this.getEndpoints(currentEdge).getFirst();
				int endPoint = this.getEndpoints(currentEdge).getSecond();
				if (initialPoint > operationInitialpoint && endPoint > operationInitialpoint) {
					super.removeEdge(currentEdge);
					this.addEdge(currentEdge, initialPoint-1, endPoint-1, EdgeType.DIRECTED);
				}
				
			}
			//remove the previous last state
			this.removeVertex(this.getLastState());
			this.updateComposition(false);
			return true;
		}
		return false;
	}
	
	public AbstractOperationEdit removeLast() {
		AbstractOperationEdit removed = this.removeLastWithoutUpdate();
		this.updateComposition(false);
		return removed;
	}
	
	private AbstractOperationEdit removeLastWithoutUpdate() {
		AbstractOperationEdit lastEdit = this.getLastEdit();
		this.removeEdge(lastEdit);
		this.removeVertex(this.getLastState());
		return lastEdit;
	}
	
	public Integer getLastState() {
		return this.getVertexCount()-1;
	}
	
	public AbstractOperationEdit getLastEdit() {
		//TODO does not work for several edges going in last state!
		//List<AbstractOperationEdit> path = this.getCurrentShortestPath();
		return this.findEdgeSet(this.getEdgeCount()-1, this.getEdgeCount()).iterator().next();
	}
	
	public void setDurations(double duration) {
		for (AbstractOperationEdit currentEdit : this.edges.keySet()) {
			currentEdit.setDuration(duration);
		}
	}

}

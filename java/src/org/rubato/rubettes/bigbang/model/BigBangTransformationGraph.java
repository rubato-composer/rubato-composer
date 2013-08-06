package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.util.DenotatorPath;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class BigBangTransformationGraph extends DirectedSparseGraph<Integer,AbstractOperationEdit> {
	
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
		Integer startingVertex = this.selectedCompositionState != null? this.selectedCompositionState : this.getVertexCount()-1;
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
		return added;
	}
	
	public void updateComposition(boolean inPreviewMode) {
		if (this.getEdgeCount() > 0) {
			List<Map<DenotatorPath,DenotatorPath>> pathDifferences = new ArrayList<Map<DenotatorPath,DenotatorPath>>();
			//TODO for now just sets inPreviewMode for the last edge.
			AbstractOperationEdit lastEdge = this.getInEdges(this.getEdgeCount()).iterator().next(); 
			lastEdge.setInPreviewMode(inPreviewMode);
			//TODO pretty bad...
			lastEdge.getScoreManager().resetScore();
			int shownState = this.getEdgeCount();
			if (!inPreviewMode && this.selectedCompositionState != null) {
				shownState = this.selectedCompositionState;
			}
			if (shownState > 0) {
				DijkstraShortestPath<Integer,AbstractOperationEdit> dijkstra = new DijkstraShortestPath<Integer,AbstractOperationEdit>(this);
			    List<AbstractOperationEdit> shortestPath = dijkstra.getPath(0, shownState);
			    for (int i = 0; i < shortestPath.size(); i++) {
					//only send composition change with last one!!!!!!
					pathDifferences = shortestPath.get(i).execute(pathDifferences, i==shortestPath.size()-1);
				}
			} else {
				lastEdge.getScoreManager().fireCompositionChange();
			}
		}
	}
	
	public AbstractOperationEdit removeLast() {
		AbstractOperationEdit removed = this.removeLastWithoutUpdate();
		this.updateComposition(false);
		return removed;
	}
	
	private AbstractOperationEdit removeLastWithoutUpdate() {
		AbstractOperationEdit lastEdit = this.getLastEdit();
		this.removeEdge(lastEdit);
		this.removeVertex(this.getVertexCount()-1);
		return lastEdit;
	}
	
	public AbstractOperationEdit getLastEdit() {
		int lastVertex = this.getEdgeCount();
		return this.findEdge(lastVertex-1, lastVertex);
	}

}

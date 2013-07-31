package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.util.DenotatorPath;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class BigBangTransformationGraph extends DirectedSparseGraph<Integer,AbstractOperationEdit> {
	
	private Integer selectedCompositionState;
	
	public BigBangTransformationGraph() {
		this.addVertex(0);
	}
	
	public boolean add(AbstractOperationEdit edit) {
		return this.add(edit, false);
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
	
	private boolean add(AbstractOperationEdit edit, boolean inPreviewMode) {
		this.addVertex(this.getVertexCount());
		boolean added = this.addEdge(edit, this.getVertexCount()-2, this.getVertexCount()-1);
		if (added) {
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
			int lastState = this.selectedCompositionState != null ? this.selectedCompositionState : this.getEdgeCount();
			if (lastState > 0) {
				for (int i = 0; i < lastState; i++) {
					AbstractOperationEdit currentEdit = this.findEdge(i, i+1);
					//only send composition change with last one!!!!!!
					pathDifferences = currentEdit.execute(pathDifferences, i==lastState-1);
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

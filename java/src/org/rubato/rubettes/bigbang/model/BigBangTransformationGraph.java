package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.util.DenotatorPath;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class BigBangTransformationGraph extends DirectedSparseGraph<Integer,AbstractOperationEdit> {
	
	public BigBangTransformationGraph() {
		this.addVertex(0);
	}
	
	public boolean add(AbstractOperationEdit edit) {
		return this.add(edit, false);
	}
	
	public void previewTransformationAtEnd(AbstractTransformationEdit edit) {
		this.add(edit, true);
		this.removeLastWithoutUpdate();
	}
	
	private boolean add(AbstractOperationEdit edit, boolean inPreviewMode) {
		this.addVertex(this.getVertexCount());
		boolean added = this.addEdge(edit, this.getVertexCount()-2, this.getVertexCount()-1);
		if (added) {
			this.updateScore(inPreviewMode);
		}
		return added;
	}
	
	public void updateScore(boolean inPreviewMode) {
		if (this.getEdgeCount() > 0) {
			List<Map<DenotatorPath,DenotatorPath>> pathDifferences = new ArrayList<Map<DenotatorPath,DenotatorPath>>();
			//TODO for now just sets inPreviewMode for the last edge.
			AbstractOperationEdit lastEdge = this.getInEdges(this.getEdgeCount()).iterator().next(); 
			lastEdge.setInPreviewMode(inPreviewMode);
			//TODO pretty bad...
			lastEdge.getScoreManager().resetScore();
			for (int i = 0; i < this.getEdgeCount(); i++) {
				AbstractOperationEdit currentEdit = this.findEdge(i, i+1);
				//only send composition change with last one!!!!!!
				pathDifferences = currentEdit.execute(pathDifferences, i==this.getEdgeCount()-1);
			}
		}
	}
	
	public AbstractOperationEdit removeLast() {
		AbstractOperationEdit removed = this.removeLastWithoutUpdate();
		this.updateScore(false);
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

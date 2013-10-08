package org.rubato.rubettes.bigbang.view.subview.graph;

import org.apache.commons.collections15.Factory;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;

import edu.uci.ics.jung.algorithms.layout.Layout;

public class VertexFactory implements Factory<Integer> {
	
	Layout<Integer,AbstractOperationEdit> layout;
	
	public VertexFactory(Layout<Integer,AbstractOperationEdit> layout) {
		this.layout = layout;
	}

	public Integer create() {
		return this.layout.getGraph().getEdgeCount();
	}

}

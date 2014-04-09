package org.rubato.rubettes.bigbang.view.subview.graph;

import org.apache.commons.collections15.Factory;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

import edu.uci.ics.jung.algorithms.layout.Layout;

public class VertexFactory implements Factory<CompositionState> {
	
	Layout<CompositionState,AbstractOperation> layout;
	
	public VertexFactory(Layout<CompositionState,AbstractOperation> layout) {
		this.layout = layout;
	}

	public CompositionState create() {
		return new CompositionState(this.layout.getGraph().getEdgeCount());
	}

}

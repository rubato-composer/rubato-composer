package org.rubato.rubettes.bigbang.view.subview.graph;

import org.apache.commons.collections15.Factory;
import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

import edu.uci.ics.jung.algorithms.layout.Layout;

public class EdgeFactory implements Factory<AbstractOperation> {
	
	Layout<CompositionState,AbstractOperation> layout;
	
	public EdgeFactory(Layout<CompositionState,AbstractOperation> layout) {
		this.layout = layout;
	}

	public AbstractOperation create() {
		return this.layout.getGraph().getEdges().iterator().next(); //TODO makeCopy!!
	}

}

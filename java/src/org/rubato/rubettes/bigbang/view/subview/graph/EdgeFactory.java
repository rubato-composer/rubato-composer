package org.rubato.rubettes.bigbang.view.subview.graph;

import org.apache.commons.collections15.Factory;
import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;

import edu.uci.ics.jung.algorithms.layout.Layout;

public class EdgeFactory implements Factory<AbstractOperationEdit> {
	
	Layout<Integer,AbstractOperationEdit> layout;
	
	public EdgeFactory(Layout<Integer,AbstractOperationEdit> layout) {
		this.layout = layout;
	}

	public AbstractOperationEdit create() {
		return this.layout.getGraph().getEdges().iterator().next(); //TODO makeCopy!!
	}

}

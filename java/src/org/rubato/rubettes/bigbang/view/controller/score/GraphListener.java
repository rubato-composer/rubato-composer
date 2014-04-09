package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.rubato.rubettes.bigbang.model.graph.CompositionState;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

import edu.uci.ics.jung.visualization.VisualizationViewer;

public class GraphListener implements ItemListener {
	
	private ViewController controller;
	private VisualizationViewer<CompositionState,AbstractOperation> viewer;
	
	public GraphListener(ViewController controller, VisualizationViewer<CompositionState,AbstractOperation> viewer) {
		this.controller = controller;
		this.viewer = viewer;
	}
	
	public void itemStateChanged(ItemEvent event) {
		if (event.getItem() instanceof AbstractOperation) {
			AbstractOperation edge = (AbstractOperation)event.getItem();
			if (this.viewer.getPickedEdgeState().isPicked(edge)) {
            	this.controller.selectOperation(edge);
    		} else {
    			this.controller.deselectOperations();
    		}
        } else if (event.getItem() instanceof CompositionState) {
        	CompositionState vertex = (CompositionState)event.getItem();
        	if (this.viewer.getPickedVertexState().isPicked(vertex)) {
            	this.controller.selectCompositionState(vertex);
    		} else {
    			this.controller.deselectCompositionStates();
    		}
        }
	}

}

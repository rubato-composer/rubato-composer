package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

import edu.uci.ics.jung.visualization.VisualizationViewer;

public class GraphListener implements ItemListener {
	
	private ViewController controller;
	private VisualizationViewer<Integer,AbstractOperationEdit> viewer;
	
	public GraphListener(ViewController controller, VisualizationViewer<Integer,AbstractOperationEdit> viewer) {
		this.controller = controller;
		this.viewer = viewer;
	}
	
	public void itemStateChanged(ItemEvent event) {
		if (event.getItem() instanceof AbstractOperationEdit) {
			AbstractOperationEdit edit = (AbstractOperationEdit)event.getItem();
            if (this.viewer.getPickedEdgeState().isPicked(edit)) {
            	if (edit instanceof AbstractTransformationEdit) {
            		this.controller.selectTransformation((AbstractTransformationEdit)edit);
            	}
    		} else {
    			this.controller.deselectTransformations();
    		}
        }
	}

}

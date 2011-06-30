package org.rubato.rubettes.bigbang.view.controller.score;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class GraphListener implements ListSelectionListener {
	
	private ViewController controller;
	
	public GraphListener(ViewController controller) {
		this.controller = controller;
	}

	public void valueChanged(ListSelectionEvent event) {
		AbstractTransformationEdit transformation = (AbstractTransformationEdit)((JList)event.getSource()).getSelectedValue();
		if (transformation != null) {
			this.controller.selectTransformation(transformation);
		} else {
			this.controller.deselectTransformations();
		}
	}

}

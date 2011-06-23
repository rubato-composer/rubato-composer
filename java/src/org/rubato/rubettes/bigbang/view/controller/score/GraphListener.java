package org.rubato.rubettes.bigbang.view.controller.score;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;

public class GraphListener implements ListSelectionListener {
	
	private BigBangController controller;
	
	public GraphListener(BigBangController controller) {
		this.controller = controller;
	}

	public void valueChanged(ListSelectionEvent event) {
		AbstractTransformationEdit transformation = (AbstractTransformationEdit)((JList)event.getSource()).getSelectedValue();
		this.controller.transformationSelected(transformation);
	}

}

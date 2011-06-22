package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JToolBar;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class JGraphToolBar extends JToolBar implements View {
	
	private ViewController controller;
	private JList transformations;
	
	public JGraphToolBar(ViewController controller, BigBangController bbController) {
		this.controller = controller;
		controller.addView(this);
		bbController.addView(this);
		this.transformations = new JList();
		this.add(this.transformations);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(BigBangController.GRAPH)) {
			Object[] transformationNames = ((List<?>)event.getNewValue()).toArray();
			this.updateTransformationNames(transformationNames);
		}
	}
	
	private void updateTransformationNames(Object[] transformationNames) {
		this.transformations.setListData(transformationNames);
	}

}

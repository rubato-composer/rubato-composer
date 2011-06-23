package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JToolBar;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.GraphListener;

public class JGraphToolBar extends JToolBar implements View {
	
	private ViewController controller;
	private JList transformations;
	
	public JGraphToolBar(ViewController controller, BigBangController bbController) {
		this.controller = controller;
		controller.addView(this);
		bbController.addView(this);
		this.transformations = new JList();
		this.transformations.addListSelectionListener(new GraphListener(bbController));
		this.add(this.transformations);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(BigBangController.GRAPH)) {
			Object[] transformations = ((List<?>)event.getNewValue()).toArray();
			this.updateTransformations(transformations);
		}
	}
	
	private void updateTransformations(Object[] transformations) {
		this.transformations.setListData(transformations);
	}

}

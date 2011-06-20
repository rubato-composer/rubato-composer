package org.rubato.rubettes.bigbang.view.controller.display;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.subview.ViewParametersTableModel;

public class ViewParametersTableModelListener implements TableModelListener {
	
	private ViewController controller;
	
	public ViewParametersTableModelListener(ViewController controller) {
		this.controller = controller;
	}
	
	public void tableChanged(TableModelEvent event) {
		int[] viewParameters = ((ViewParametersTableModel)event.getSource()).getData();
		this.controller.changeViewParameters(viewParameters);
	}
	
}

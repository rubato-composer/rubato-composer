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
		ViewParametersTableModel model = (ViewParametersTableModel)event.getSource();
		if (event.getColumn() == model.getColumnCount()-1) { 
			double value = (Double)model.getValueAt(event.getFirstRow(), event.getColumn());
			this.controller.setStandardDenotatorValue(event.getFirstRow(), value);
		} else {
			this.controller.changeViewParameters(model.getData());
		}
	}
	
}

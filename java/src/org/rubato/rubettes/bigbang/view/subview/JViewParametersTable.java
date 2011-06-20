package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.JTable;

import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.display.ViewParametersTableModelListener;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;

public class JViewParametersTable extends JTable implements View {
	
	public JViewParametersTable(ViewController controller) {
		super(new ViewParametersTableModel());
		this.setCellSelectionEnabled(false);
		this.setPreferredSize(new Dimension(100, 100));
		this.getModel().addTableModelListener(new ViewParametersTableModelListener(controller));
	}
	
	private void updateViewParameters(int[] newParameters) {
		((ViewParametersTableModel)this.getModel()).setData(newParameters);
	}
	
	private void updateColumnNames(ViewParameters newParameters) {
		((ViewParametersTableModel)this.getModel()).setColumnNames(newParameters);
	}
	
	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.VIEW_PARAMETERS)) {
			this.updateColumnNames((ViewParameters)event.getNewValue());
		} else if (propertyName.equals(ViewController.SELECTED_VIEW_PARAMETERS)) {
			this.updateViewParameters((int[]) event.getNewValue());
		} else if (propertyName.equals(ViewController.VIEW_PARAMETERS_VISIBLE)) {
			this.setVisible(((Boolean)event.getNewValue()).booleanValue());
		}
	}

}

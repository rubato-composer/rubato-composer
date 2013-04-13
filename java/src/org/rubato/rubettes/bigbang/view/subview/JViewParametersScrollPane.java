package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.display.ViewParametersTableModelListener;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;

public class JViewParametersScrollPane extends JScrollPane implements View {
	
	private JTable viewParametersTable;
	private JTable rowNameTable;
	
	public JViewParametersScrollPane(ViewController controller, ViewParameters viewParameters) {
		this.viewParametersTable = new JTable(new ViewParametersTableModel(viewParameters));
		this.viewParametersTable.setCellSelectionEnabled(false);
		this.viewParametersTable.getModel().addTableModelListener(new ViewParametersTableModelListener(controller));
		this.viewParametersTable.setDragEnabled(false);
		this.rowNameTable = new JTable(new ViewParametersRowHeaderTableModel());
		this.rowNameTable.setEnabled(false);
		this.rowNameTable.setDragEnabled(false);
		this.rowNameTable.getColumnModel().getColumn(0).setHeaderValue("");
		this.setViewportView(this.viewParametersTable);
		this.setRowHeaderView(this.rowNameTable);
		this.setCorner(JScrollPane.UPPER_LEFT_CORNER, this.rowNameTable.getTableHeader());
		this.setPreferredSize(new Dimension(250, 100));
		this.viewParametersTable.setPreferredScrollableViewportSize(new Dimension(10, 10));
		this.viewParametersTable.setAutoCreateColumnsFromModel(false);
		this.viewParametersTable.getColumnModel().getColumn(this.viewParametersTable.getColumnModel().getColumnCount()-1).setMinWidth(40);
	}
	
	private void updateViewParameters(int[] newParameters) {
		((ViewParametersTableModel)this.viewParametersTable.getModel()).setData(newParameters);
	}
	
	private void updateRowNames(List<String> rowNames) {
		((ViewParametersRowHeaderTableModel)this.rowNameTable.getModel()).setValues(rowNames);
		int rowCount = this.rowNameTable.getRowCount();
		((ViewParametersTableModel)this.viewParametersTable.getModel()).setRowCount(rowCount);
		int maxNameWidth = 0;
		for (int row = 0; row < rowCount; row++) {
			TableCellRenderer renderer = this.rowNameTable.getCellRenderer(row, 0);
			maxNameWidth = Math.max(this.rowNameTable.prepareRenderer(renderer, row, 0).getPreferredSize().width, maxNameWidth);
		}
		this.rowNameTable.setPreferredScrollableViewportSize(new Dimension(maxNameWidth+10, 100));
		this.setPreferredSize(new Dimension(110+maxNameWidth, 100/6*rowCount+20));
	}
	
	private void updateStandardValues(List<Double> standardValues) {
		((ViewParametersTableModel)this.viewParametersTable.getModel()).setStandardValues(standardValues);
	}
	
	@SuppressWarnings("unchecked")
	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.FORM)) {
			this.updateRowNames(((DisplayObjectList)event.getNewValue()).getValueNames());
		} else if (propertyName.equals(ViewController.SELECTED_VIEW_PARAMETERS)) {
			this.updateViewParameters((int[]) event.getNewValue());
		} else if (propertyName.equals(ViewController.VIEW_PARAMETERS_VISIBLE)) {
			this.setVisible((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.STANDARD_DENOTATOR_VALUES)) {
			this.updateStandardValues((List<Double>)event.getNewValue());
		}
	}

}

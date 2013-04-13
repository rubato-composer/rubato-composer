package org.rubato.rubettes.bigbang.view.subview;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.rubato.rubettes.bigbang.view.model.ViewParameters;

public class ViewParametersTableModel extends AbstractTableModel {
	
	private String[] columnNames;
	private int[] data;
	private int rowCount;
	
	private List<Double> standardValues;
	
	public ViewParametersTableModel(ViewParameters viewParameters) {
		this.data = new int[viewParameters.size()];
		this.generateColumnNames(viewParameters);
		this.setStandardValues(new ArrayList<Double>());
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = -1;
		}
		this.rowCount = 0;
	}
	
	private void generateColumnNames(ViewParameters viewParameters) {
		this.columnNames = new String[viewParameters.size()+1];
		for (int i = 0; i < viewParameters.size(); i++) {
			this.columnNames[i] = Character.toString(viewParameters.getParameterNames()[i].charAt(0));
		}
		this.columnNames[this.columnNames.length-1] = "StV";
	}
	
	public void setStandardValues(List<Double> standardValues) {
		this.standardValues = standardValues;
	}
	
	public List<Double> getStandardValues() {
		return this.standardValues;
	}
	
	public void setData(int[] data) {
		this.data = data.clone();
	}
	
	public int[] getData() {
		return this.data;
	}
	
	public int getColumnCount() {
        return this.data.length+1;
    }
	
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	
	public int getRowCount() {
        return this.rowCount;
    }
	
	public String getColumnName(int col) {
    	return this.columnNames[col];
    }

    public Object getValueAt(int row, int col) {
    	if (col < this.data.length) {
    		return this.data[col] == row;
    	}
    	return this.standardValues.get(row);
    }

    public Class<?> getColumnClass(int col) {
    	if (col < this.data.length) {
    		return Boolean.class;
    	}
    	return Double.class;
    }

    public boolean isCellEditable(int row, int col) {
        return true;
    }

    public void setValueAt(Object value, int row, int col) {
    	if (col < this.data.length) {
    		int oldRow = this.data[col];
        	if (oldRow == row) {
        		this.data[col] = -1;
        	} else {
        		this.data[col] = new Integer(row).intValue();
        		this.fireTableCellUpdated(row, col);
        	}
        	this.fireTableCellUpdated(oldRow, col);
    	} else {
    		this.standardValues.set(row, (Double)value);
    		this.fireTableCellUpdated(row, col);
    	}
    }
    
}

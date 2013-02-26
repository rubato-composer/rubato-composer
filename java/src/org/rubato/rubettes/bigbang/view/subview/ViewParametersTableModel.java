package org.rubato.rubettes.bigbang.view.subview;

import javax.swing.table.AbstractTableModel;

import org.rubato.rubettes.bigbang.view.model.ViewParameters;

public class ViewParametersTableModel extends AbstractTableModel {
	
	private String[] columnNames;
	private int[] data;
	private int rowCount;
	
	public ViewParametersTableModel() {
		this.data = new int[ViewParameters.VIEW_PARAMETER_COUNT];
		this.generateColumnNames();
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = -1;
		}
		this.rowCount = 0;
	}
	
	private void generateColumnNames() {
		this.columnNames = new String[ViewParameters.VIEW_PARAMETER_COUNT];
		for (int i = 0; i < this.columnNames.length; i++) {
			this.columnNames[i] = Character.toString(ViewParameters.VIEW_PARAMETER_NAMES[i].charAt(0));
		}
	}
	
	public void setData(int[] data) {
		this.data = data.clone();
	}
	
	public int[] getData() {
		return this.data;
	}
	
	public int getColumnCount() {
        return this.data.length;
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
        return this.data[col] == row;
    }

    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return true;
    }

    public void setValueAt(Object value, int newRow, int col) {
    	int oldRow = this.data[col];
    	if (oldRow == newRow) {
    		this.data[col] = -1;
    	} else {
    		this.data[col] = new Integer(newRow).intValue();
    		fireTableCellUpdated(newRow, col);
    	}
        fireTableCellUpdated(oldRow, col);
    }
    
}

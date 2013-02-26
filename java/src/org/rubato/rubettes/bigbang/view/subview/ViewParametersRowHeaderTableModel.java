package org.rubato.rubettes.bigbang.view.subview;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ViewParametersRowHeaderTableModel extends AbstractTableModel {

	private List<String> valueNames;
	
	public void setValues(List<String> valueNames) {
		this.valueNames = valueNames;
	}
	
	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return this.valueNames.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return this.valueNames.get(rowIndex);
	}

}

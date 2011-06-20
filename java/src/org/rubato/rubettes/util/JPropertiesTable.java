/*
 * Copyright (C) 2006 Florian Thalmann
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package org.rubato.rubettes.util;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * A JTable providing row adding, moving and removing functions and implementing a similar
 * apply/revert concept as rubettes.
 * 
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public abstract class JPropertiesTable extends JTable {
	
	public JPropertiesTable(String[] columnNames) {
		super(new DefaultTableModel(new Object[][]{}, columnNames));
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	}
	
	/**
	 * Removes all rows in this table.
	 */
	public void clear() {
		this.stopEditing();
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}
	}
	
	/**
	 * Removes all rows that are currently selected.
	 */
	public void removeSelectedRows() {
		this.stopEditing();
		int[] selectedRows = this.getSelectedRows();
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		for (int i = selectedRows.length-1; i >= 0; i--) {
			model.removeRow(selectedRows[i]);
		}
	}
	
	/**
	 * Moves the currently selected rows up one row.
	 */
	public void moveSelectedRowsUp() {
		this.stopEditing();
		int[] selectedRows = this.getSelectedRows();
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		if (selectedRows != null && selectedRows.length > 0 && selectedRows[0] > 0) {
            int lastIndex = selectedRows.length-1;
			for (int i = 0; i <= lastIndex; i++) {
				int currentRow = selectedRows[i];
				model.moveRow(currentRow, currentRow, currentRow-1);
			}
			this.setRowSelectionInterval(selectedRows[0]-1, selectedRows[lastIndex]-1);
		}
	}
	
	/**
	 * Moves the currently selected rows down one row.
	 */
	public void moveSelectedRowsDown() {
		this.stopEditing();
		int[] selectedRows = this.getSelectedRows();
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		if (selectedRows != null && selectedRows.length > 0 && selectedRows[selectedRows.length-1] < this.getRowCount()-1) {
            int lastIndex = selectedRows.length-1;
			for (int i = 0; i < selectedRows.length; i++) {
				int currentRow = selectedRows[i];
				model.moveRow(currentRow, currentRow, currentRow+1);
			}
			this.setRowSelectionInterval(selectedRows[0]+1, selectedRows[lastIndex]+1);
		}
	}
	
	/**
	 * Resets all values of the specified column to null.
	 */
	public void resetColumn(int column) {
		this.stopEditing();
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setValueAt(null, i, column);
		}
	}
	
	/*
	 * stops editing. important before a moving or removal operation
	 */
	protected void stopEditing() {
		if (this.isEditing()) {
			this.getCellEditor(this.getEditingRow(), this.getEditingColumn()).stopCellEditing();
		}
	}
	
	/**
	 * Makes changes in the table permanent.
	 * 
	 * @return true iff the values in the table are correct
	 */
	public abstract boolean applyChanges();
	
	/**
	 * Reverts values in the table to the previouly applied values.
	 */
	public abstract void revertChanges();

}

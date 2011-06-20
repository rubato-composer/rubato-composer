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

package org.rubato.rubettes.wallpaper;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.Form;

/**
 * A cell editor used for table cells defining the coordinates that are to be mapped by a morphism.
 * The coordinates string is visible and the select coordinates dialog opens, when the cell is
 * doubleclicked.
 * 
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public class SimpleFormsCellEditor extends AbstractCellEditor
								implements TableCellEditor, MouseListener {
	
	private JWallpaperDimensionsTable morphismsTable;
	private JLabel button;
	private JSelectSimpleForms dialog;
	
	public SimpleFormsCellEditor() {
		this.button = new JLabel();
		this.button.addMouseListener(this);
	}
	
	/**
	 * When the cell is double clicked, the select coordinates dialog opens.
	 */
	public void mouseClicked(MouseEvent e) {
    	if (e.getClickCount() > 1) {
    		if (this.dialog != null) {
        		this.dialog.setVisible(true);
        	}
    		this.fireEditingStopped();
    	}
    }

	/**
     * The value returned are the coordinates selected in the dialog.
     */
	public Object getCellEditorValue() {
    	Object elements = null;
    	if (this.dialog != null) {
    		elements = this.dialog.getElements();
    	}
    	return elements;
    }

	/**
     * The cell contains a button. The select coordinates dialog is created in this method.
     */
    @SuppressWarnings("unchecked")
	public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
    	this.morphismsTable = (JWallpaperDimensionsTable)table;
    	WallpaperRubette rubette = this.morphismsTable.getRubette();
    	Frame owner = this.morphismsTable.getRubette().getPropertiesFrame();
    	Form form = rubette.getTempInputForm();
    	ModuleMorphism morphism = (ModuleMorphism)table.getValueAt(row, 0);
    	if (form == null) {
    		this.dialog = null;
    		this.morphismsTable.setStatuslineText("Define form first");
    	} else if (morphism == null) {
    		this.dialog = null;
    		this.morphismsTable.setStatuslineText("Define morphism first");
    	} else {
    		List<List<Integer>> elements = (List<List<Integer>>)value;
    		this.morphismsTable.setStatuslineText("");
    		this.dialog = new JSelectSimpleForms(rubette, morphism);
    		this.dialog.setLocationRelativeTo(owner);
    		if (elements != null) {
    			this.button.setText(elements.toString());
    			this.dialog.setElements(elements);
    		}
    	}
        return this.button;
    }
    
    //these methods don't need to be overridden
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}

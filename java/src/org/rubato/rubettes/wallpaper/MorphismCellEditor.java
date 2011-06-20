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

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.rubato.composer.dialogs.morphisms.JMorphismDialog;
import org.rubato.math.module.morphism.ModuleMorphism;

/**
 * A cell editor used for table cells containg morphisms. The morphism string is visible and the
 * editing dialog opens, when the cell is doubleclicked.
 * 
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public class MorphismCellEditor extends AbstractCellEditor
								implements TableCellEditor, MouseListener {
	
	private JLabel button;
	private JMorphismDialog dialog;
	private ModuleMorphism initialMorphism;
	
	public MorphismCellEditor() {
		this.button = new JLabel();
		this.button.addMouseListener(this);
	}
    
	/**
	 * Ensures that, when the cell is double clicked, the morphism editing dialog is opened.
	 */
    public void mouseClicked(MouseEvent e) {
    	if (e.getClickCount() > 1) {
    		this.dialog.setVisible(true);
    		this.fireEditingStopped();
    	}
    }

    /**
     * The value returned is the morphism selected in the dialog.
     */
    public Object getCellEditorValue() {
        return this.dialog.getMorphism();
    }

    /**
     * The cell contains a button. The morphism editing dialog is created in this method.
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
    	this.initialMorphism = (ModuleMorphism)value;
    	JWallpaperDimensionsTable mTable = (JWallpaperDimensionsTable)table;
    	Frame owner = mTable.getRubette().getPropertiesFrame();
    	if (this.initialMorphism != null) {
    		this.button.setText(this.initialMorphism.toString());
    		this.dialog = new JMorphismDialog(owner, false, this.initialMorphism);
    		this.dialog.setLocationRelativeTo(owner);
    	} else {
    		this.dialog = new JMorphismDialog(owner, true, false);
    		this.dialog.setLocationRelativeTo(owner);
    	}
        return this.button;
    }
    
    //these methods don't need to be overridden
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}

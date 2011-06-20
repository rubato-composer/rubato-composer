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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.rubettes.util.JPropertiesTable;

/**
 * A table that stores all information about a wallpaper's morphisms and that can be edited. There
 * is a saving mechanism: revertChanges() always goes back to the state saved with applyChanges().
 *  
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public class JWallpaperDimensionsTable
        extends JPropertiesTable
        implements MouseWheelListener {
	
	private WallpaperRubette rubette;
	
	private List<ModuleMorphism> morphisms;
	private Map<ModuleMorphism, Integer> rangeFroms;
	private Map<ModuleMorphism, Integer> rangeTos;
	private Map<ModuleMorphism, List<List<Integer>>> coordinates;
	private List<ModuleMorphism> tempMorphisms;
	private Map<ModuleMorphism, Integer> tempRangeFroms;
	private Map<ModuleMorphism, Integer> tempRangeTos;
	private Map<ModuleMorphism, List<List<Integer>>> tempCoordinates;
	
	private static final String[] columnNames
		= new String[] {"morphism","from","to","coordinates"};
	private static final String[] columnToolTip
        = new String[] {"Morphism", "From", "To", "Coordinates"};
    
    /**
	 * Creates a table corresponding to the specified WallpaperRubette.
	 * 
	 * @param rubette the rubette, the wallpaper of which is defined by the morphisms of this table 
	 */
	public JWallpaperDimensionsTable(WallpaperRubette rubette) {
		super(columnNames);
		this.rubette = rubette;
		//make ColumnNames visible!
		this.getColumnModel().getColumn(0).setCellEditor(new MorphismCellEditor());
        this.getColumnModel().getColumn(1).setPreferredWidth(2);
        this.getColumnModel().getColumn(1).setCellEditor(new NaturalNumberCellEditor(this));
		this.getColumnModel().getColumn(2).setPreferredWidth(2);
        this.getColumnModel().getColumn(2).setCellEditor(new NaturalNumberCellEditor(this));
		this.getColumnModel().getColumn(3).setCellEditor(new SimpleFormsCellEditor());
		
		this.morphisms = new ArrayList<ModuleMorphism>();
		this.rangeFroms = new HashMap<ModuleMorphism, Integer>();
		this.rangeTos = new HashMap<ModuleMorphism, Integer>();
		this.coordinates = new HashMap<ModuleMorphism, List<List<Integer>>>();
		
		this.tempMorphisms = new ArrayList<ModuleMorphism>();
		this.tempRangeFroms = new HashMap<ModuleMorphism, Integer>();
		this.tempRangeTos = new HashMap<ModuleMorphism, Integer>();
		this.tempCoordinates = new HashMap<ModuleMorphism, List<List<Integer>>>();
        
        addMouseWheelListener(this);
	}
    
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        JComponent c = (JComponent)super.prepareRenderer(renderer, row, column);
        c.setToolTipText(columnToolTip[column]);
        return c;
    }
    
    public void mouseWheelMoved(MouseWheelEvent e) {
        int row = getEditingRow();
        int col = getEditingColumn();
        TableCellEditor c = getCellEditor(row, col);
        if (c != null && c instanceof NaturalNumberCellEditor) {
            if (e.getWheelRotation() < 0) {
                ((NaturalNumberCellEditor)c).up();
            }
            else {
                ((NaturalNumberCellEditor)c).down();
            }
        }
    }
	
	/**
	 * Returns a JWallpaperPanel with the graphical representation of the wallpaper defined by the
	 * values of this table.
	 */
	public JComponent getMorphismsView() {
		if (this.updateTempAttributes()) {
			return new JWallpaperViewPanel(this);
		} else {
			return new JWallpaperViewPanel();
		}
	}
	
	/**
	 * Adds a new empty row to this table.
	 */
	public void addMorphism() {
		this.addMorphism(null, 0, 1, null);
	}
	
	/**
	 * Adds a new row with the specified values to this table.
	 * 
	 * @param morphism the morphism this row represents
	 * @param rangeFrom the start of the morphism's range 
	 * @param rangeTo the end of the morphism's range
	 * @param coordinates the coordinates the morphism affects
	 */
	public void addMorphism(ModuleMorphism morphism, int rangeFrom, int rangeTo, List<List<Integer>> coordinates) {
		Object[] newRow = new Object[]
		    {morphism, new Integer(rangeFrom).toString(), new Integer(rangeTo).toString(), coordinates};
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		model.addRow(newRow);
	}
	
	/**
	 * Resets all selected coordinates to null.
	 */
	public void resetSelectedCoordinates() {
		this.resetColumn(3);
	}
	
	public boolean applyChanges() {
		this.stopEditing();
		boolean updatedCorrectly = this.updateTempAttributes();
		if (updatedCorrectly) {
			this.morphisms = this.tempMorphisms;
			this.rangeFroms = this.tempRangeFroms;
			this.rangeTos = this.tempRangeTos;
			this.coordinates = this.tempCoordinates;
		}
		return updatedCorrectly;
	}
	
	/*
	 * the values in the table are saved in temporary attributes. for example when a graphical
	 * representation needs to be created. all attributes are valuated
	 */
	@SuppressWarnings("unchecked")
	private boolean updateTempAttributes() {
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		this.tempMorphisms = new ArrayList<ModuleMorphism>();
		for (int i = 0; i < model.getRowCount(); i++) {
			ModuleMorphism currentMorphism = (ModuleMorphism)model.getValueAt(i, 0);
			//add morphism
			if (currentMorphism == null) {
				this.setStatuslineText("Morphism " + (i+1) + " is not defined");
				return false;
			}
			this.tempMorphisms.add(currentMorphism);
			//set ranges
			try {
				this.tempRangeFroms.put(currentMorphism, Integer.parseInt((String)model.getValueAt(i, 1)));
				this.tempRangeTos.put(currentMorphism, Integer.parseInt((String)model.getValueAt(i, 2)));
			} catch (NumberFormatException e) {
				this.setStatuslineText("No valid range for morphism " + (i+1));
				return false;
			}
			//set coordinates
			this.tempCoordinates.put(currentMorphism, (List<List<Integer>>)model.getValueAt(i, 3));
			if (this.tempCoordinates.get(currentMorphism) == null) {
				this.setStatuslineText("Not all coordinates selected for morphism " + (i+1));
				return false;
			}
		}
		return true;
	}
	
	public void revertChanges() {
		this.stopEditing();
		DefaultTableModel model = (DefaultTableModel)this.getModel();
		int appliedMorphismCount = this.morphisms.size();
		model.setNumRows(appliedMorphismCount);
		for (int i = 0; i < appliedMorphismCount; i++) {
			ModuleMorphism currentMorphism = this.morphisms.get(i);
			model.setValueAt(currentMorphism, i, 0);
			model.setValueAt(this.rangeFroms.get(currentMorphism).toString(), i, 1);
			model.setValueAt(this.rangeTos.get(currentMorphism).toString(), i, 2);
			model.setValueAt(this.coordinates.get(currentMorphism), i, 3);
		}
	}
	
	/*
	 * sets the status line the the wallpaper rubette's properties window to the specified text 
	 */
	protected void setStatuslineText(String text) {
		this.rubette.setStatuslineText(text);
	}
	
	/**
	 * Returns the WallpaperRubette this table belongs to
	 */
	public WallpaperRubette getRubette() {
		return this.rubette;
	}
	
	//temporary properties
	
	/**
	 * Returns the number of morphisms currently in the table (dynamically changed)
	 */
	public int getMorphismCount() {
		return this.getModel().getRowCount();
	}
	
	/**
	 * Returns the morphism temporarily saved for the indicated row.
	 * 
	 * @param row - the index of the table's row
	 */
	public ModuleMorphism getTempMorphism(int row) {
		return this.tempMorphisms.get(row);
	}
	
	/**
	 * Returns the start of the range temporarily saved for the specified morphism.
	 * 
	 * @param morphism - a morphism
	 */
	public int getTempRangeFrom(ModuleMorphism morphism) {
		return this.tempRangeFroms.get(morphism).intValue();
	}
	
	/**
	 * Returns the end of the range temporarily saved for the specified morphism.
	 * 
	 * @param morphism - a morphism
	 */
	public int getTempRangeTo(ModuleMorphism morphism) {
		return this.tempRangeTos.get(morphism).intValue();
	}
	
	/**
	 * Returns the coordinates temporarily saved for the specified morphism.
	 * 
	 * @param morphism - a morphism
	 */
	public List<List<Integer>> getTempCoordinates(ModuleMorphism morphism) {
		return this.tempCoordinates.get(morphism);
	}
	
	//applied properties
	
	/**
	 * Returns a list with the morphisms saved in this table (when applyChanges() was last executed).
	 */
	public List<ModuleMorphism> getMorphisms() {
		return this.morphisms;
	}
	
	/**
	 * Returns the morphisms saved in this table in the specified row (when applyChanges() was last executed).
	 * 
	 * @param row - the index of a row in this table
	 */
	public ModuleMorphism getMorphism(int row) {
		return this.morphisms.get(row);
	}
	
	/**
	 * Returns the start of the range saved for the specified morphism (when applyChanges was last executed).
	 * 
	 * @param morphism - a morphism
	 */
	public int getRangeFrom(ModuleMorphism morphism) {
		return this.rangeFroms.get(morphism).intValue();
	}
	
	/**
	 * Returns the end of the range saved for the specified morphism (when applyChanges was last executed)
	 * 
	 * @param morphism - a morphism
	 */
	public int getRangeTo(ModuleMorphism morphism) {
		return this.rangeTos.get(morphism).intValue();
	}
	
	/**
	 * Returns the coordinates saved for the specified morphism (when applyChanges was last executed)
	 * 
	 * @param morphism - a morphism
	 */
	public List<List<Integer>> getCoordinates(ModuleMorphism morphism) {
		return this.coordinates.get(morphism);
	}

}

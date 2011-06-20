package org.rubato.rubettes.wallpaper;
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rubato.math.yoneda.SimpleForm;

/**
 * A JDialog that shows a JWallpaperView. The two coordinates that are to be shown can be selected
 * using combo boxes.
 * 
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public class JWallpaperViewPanel extends JPanel implements ActionListener {
	
	JWallpaperDimensionsTable table;
	JWallpaperView view;
	JComboBox xCoordinateBox, yCoordinateBox;
	
	/**
	 * Creates a JWallpaperViewPanel for the wallpaper dimensions in specified table.
	 * 
	 * @param table - the table, the morphisms of which are to be shown
	 */
	public JWallpaperViewPanel(JWallpaperDimensionsTable table) {
		this.table = table;
		this.view = new JWallpaperView(this.table);
		this.setLayout(new BorderLayout());
		this.add(this.view, BorderLayout.CENTER);
		this.add(this.makeSelectCoordinatesPanel(), BorderLayout.SOUTH);
		this.setSize(300, 380);
	}
	
	/**
	 * Creates an empty JWallpaperViewPanel.
	 */
	public JWallpaperViewPanel() {
		this.setSize(300, 380);
	}
	
	/*
	 * returns a panel with the two combo boxes for selecting the coordinates to be shown
	 */
	private JPanel makeSelectCoordinatesPanel() {
		JPanel comboBoxPanel = new JPanel();
		this.xCoordinateBox = this.makeCoordinatesBox(0);
		this.yCoordinateBox = this.makeCoordinatesBox(1);
		comboBoxPanel.add(new JLabel("x:"));
		comboBoxPanel.add(this.xCoordinateBox);
		comboBoxPanel.add(new JLabel("y:"));
		comboBoxPanel.add(this.yCoordinateBox);
		return comboBoxPanel;
	}
	
	/*
	 * returns a new JComboBox with the specified initial value. it contains all simple forms
	 * that can be chosen to be mapped by a morphism
	 */
	private JComboBox makeCoordinatesBox(int initialValue) {
		List<SimpleForm> coordinateForms = this.table.getRubette().getSimpleForms();
		String[] coordinateNames = new String[coordinateForms.size()];
		for (int i = 0; i < coordinateForms.size(); i++) {
			coordinateNames[i] = coordinateForms.get(i).getNameString();
		}
		JComboBox coordinatesBox = new JComboBox(coordinateNames);
		coordinatesBox.addActionListener(this);
		coordinatesBox.setSelectedIndex(initialValue);
		return coordinatesBox;
	}
	
	/**
	 * Processes all events coming from the combo boxes.
	 */
	public void actionPerformed(ActionEvent e) {
		JComboBox src = (JComboBox)e.getSource();
		if (src == this.xCoordinateBox) {
			this.view.SetXCoordinateShown(this.xCoordinateBox.getSelectedIndex());
		} else if (src == this.yCoordinateBox) {
			this.view.SetYCoordinateShown(this.yCoordinateBox.getSelectedIndex());
		}
	}

}

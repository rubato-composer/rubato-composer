/*
 * Copyright (C) 2005 Gérard Milmeister
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

package org.rubato.composer.preferences;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.rubato.math.arith.Rational;

public class MainPreferences extends JPreferencesPanel {

    public MainPreferences(UserPreferences userPrefs) {
        super(userPrefs);
    }


    protected void createLayout() {
        zigzagBox = new JComboBox(linkTypes);
        zigzagBox.setSelectedIndex(userPrefs.getLinkType());
        addPreference(Messages.getString("MainPreferences.defaultlink"), zigzagBox); //$NON-NLS-1$
        
        saveGeometryButton = new JCheckBox();
        saveGeometryButton.setSelected(userPrefs.getGeometrySaved());
        addPreference(Messages.getString("MainPreferences.savesize"), saveGeometryButton); //$NON-NLS-1$

        askBeforeLeavingButton = new JCheckBox();
        askBeforeLeavingButton.setSelected(userPrefs.getAskBeforeLeaving());
        addPreference(Messages.getString("MainPreferences.askleaving"), askBeforeLeavingButton); //$NON-NLS-1$

        Box box = Box.createHorizontalBox();
        defaultQuantField = new JTextField();
        defaultQuantField.setText(Integer.toString(userPrefs.getDefaultQuantization()));
        defaultQuantField.setMinimumSize(new Dimension(100, 0));
        defaultQuantField.setPreferredSize(new Dimension(100, 0));
        box.add(defaultQuantField);
        box.add(Box.createHorizontalStrut(5));
        JButton quantReset = new JButton(Messages.getString("MainPreferences.reset")); //$NON-NLS-1$
        quantReset.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               defaultQuantField.setText("1920"); //$NON-NLS-1$
           } 
        });
        box.add(quantReset);
        addPreference(Messages.getString("MainPreferences.defaultquant"), box); //$NON-NLS-1$ //$NON-NLS-2$
        
        showProgressButton= new JCheckBox();
        showProgressButton.setSelected(userPrefs.getShowProgress());
        addPreference("Show progress dialog:", showProgressButton);
    }
    
    
    public void apply() {
        userPrefs.setLinkType(zigzagBox.getSelectedIndex());
        userPrefs.setGeometrySaved(saveGeometryButton.isSelected());
        userPrefs.setAskBeforeLeaving(askBeforeLeavingButton.isSelected());
        userPrefs.setDefaultQuantization(Integer.parseInt(defaultQuantField.getText()));
        userPrefs.setShowProgress(showProgressButton.isSelected());
        Rational.setDefaultQuantization(userPrefs.getDefaultQuantization());
    }
    
    
    public String getTitle() {
        return Messages.getString("MainPreferences.main"); //$NON-NLS-1$
    }
    
    
    private final static String[] linkTypes = {
        Messages.getString("MainPreferences.line"), //$NON-NLS-1$
        Messages.getString("MainPreferences.zigzag"), //$NON-NLS-1$
        Messages.getString("MainPreferences.curve") //$NON-NLS-1$
    };
    
    private   JComboBox  zigzagBox;
    private   JCheckBox  saveGeometryButton;
    private   JCheckBox  askBeforeLeavingButton;
    private   JCheckBox  showProgressButton;
    protected JTextField defaultQuantField;
}

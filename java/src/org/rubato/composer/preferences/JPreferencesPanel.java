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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;


public abstract class JPreferencesPanel extends JPanel {
    
    public JPreferencesPanel(UserPreferences userPrefs) {
        this.userPrefs = userPrefs;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        setLayout(gridbag);
        createLayout();
    }
    
    
    public abstract void apply();

    
    public abstract String getTitle();

    
    protected abstract void createLayout();
    
    
    protected void addPreference(String label, JComponent comp) {
        JLabel jlabel = new JLabel(label);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = rightPadding;
        c.gridwidth = GridBagConstraints.RELATIVE;

        gridbag.setConstraints(jlabel, c);
        add(jlabel);
        
        c.insets = noPadding;
        c.gridwidth = GridBagConstraints.REMAINDER;        
        gridbag.setConstraints(comp, c);
        add(comp);        
    }

    
    protected UserPreferences    userPrefs;
    protected GridBagLayout      gridbag;
    protected GridBagConstraints c;
    private   Insets             rightPadding = new Insets(0, 0, 0, 15); 
    private   Insets             noPadding    = new Insets(0, 0, 0, 0); 
}

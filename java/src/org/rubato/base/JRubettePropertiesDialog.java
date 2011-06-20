/*
 * Copyright (C) 2007 Gérard Milmeister
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

package org.rubato.base;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

public class JRubettePropertiesDialog extends JPanel {

    public JRubettePropertiesDialog(RubetteProperties properties) {
        this.properties = properties;
        createLayout();
    }
    
    
    private void createLayout() {
        JPanel panel = new JPanel();
        setLayout(new BorderLayout());
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(layout);

        ArrayList<RubetteProperty> props = new ArrayList<RubetteProperty>(properties.getProperties());
        Collections.sort(props);

        c.insets = new Insets(5, 5, 5, 5); 

        for (RubetteProperty property : props) {
            c.weightx = 0.0;
            c.gridwidth = 1;
            c.weighty = 0.0;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.NONE;
            JLabel label = new JLabel(property.getName()+":"); //$NON-NLS-1$
            layout.setConstraints(label, c);
            panel.add(label);

            c.weightx = 1.0;
            c.gridwidth = GridBagConstraints.REMAINDER;            
            c.fill = GridBagConstraints.HORIZONTAL;
            JComponent comp = property.getJComponent();
            if (comp instanceof JScrollPane) {
                c.fill = GridBagConstraints.BOTH;
                c.weighty = 1.0;
            }
            layout.setConstraints(comp, c);
            panel.add(comp);
        }
        
        add(panel, BorderLayout.CENTER);
    }
    
    
    private RubetteProperties properties;

    private static final long serialVersionUID = 1L;
}

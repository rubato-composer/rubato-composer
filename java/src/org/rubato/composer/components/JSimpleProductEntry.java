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

package org.rubato.composer.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rubato.math.module.*;

public class JSimpleProductEntry extends JSimpleEntry implements KeyListener {

    public JSimpleProductEntry(ProductFreeModule module) {
        this.module = module;
        createContent();
    }
    
    
    private void createContent() {
        int dim = module.getDimension();
        simpleEntries = new ArrayList<ArrayList<JSimpleEntry>>();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gbl);
        
        c.ipadx = 10;
        c.anchor = GridBagConstraints.NORTH;
        for (int i = 0; i < dim; i++) {
            c.weightx = 0.0;
            c.fill = GridBagConstraints.NONE;
            c.gridwidth = GridBagConstraints.RELATIVE;
            JLabel label = (dim == 1)?new JLabel(""):new JLabel(Messages.getString("JSimpleProductEntry.value")+" #"+i+":"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            gbl.setConstraints(label, c);
            add(label);
            c.weightx = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;
            JPanel valuePanel = createValuePanel((ProductRing)module.getComponentModule(i));
            gbl.setConstraints(valuePanel, c);
            add(valuePanel);
        }        
    }


    public void keyTyped(KeyEvent e) {
        fireActionEvent();
    }
    
    
    @SuppressWarnings("all")
    public void keyPressed(KeyEvent e) {}
    
    
    @SuppressWarnings("all")
    public void keyReleased(KeyEvent e) {}

    
    private JPanel createValuePanel(ProductRing ring) {
        ArrayList<JSimpleEntry> valueEntries = new ArrayList<JSimpleEntry>();
        JPanel valuePanel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        valuePanel.setLayout(gbl);
        c.anchor = GridBagConstraints.NORTH;
        c.ipadx = 10;
        for (int i = 0; i < ring.getFactorCount(); i++) {
            c.weightx = 0.0;
            c.fill = GridBagConstraints.NONE;
            c.gridwidth = GridBagConstraints.RELATIVE;
            JLabel label = new JLabel("#"+i+":"); //$NON-NLS-1$ //$NON-NLS-2$
            gbl.setConstraints(label, c);
            valuePanel.add(label);
            c.weightx = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;
            JSimpleEntry simpleEntry = JSimpleEntry.make(ring.getFactor(i));
            simpleEntry.addKeyListener(this);
            gbl.setConstraints(simpleEntry, c);
            valuePanel.add(simpleEntry);
            valueEntries.add(simpleEntry);
        }
        simpleEntries.add(valueEntries);
        return valuePanel;
    }
    
    
    public void clear() {
        for (ArrayList<JSimpleEntry> s : simpleEntries) {
            for (JSimpleEntry e : s) {
                e.clear();
            }
        }
    }

    
    public boolean valueIsValid() {
        return false;
    }

    
    public ModuleElement getValue() {
        boolean error = false;
        ProductElement[] elements = new ProductElement[simpleEntries.size()];
        for (int i = 0; i < simpleEntries.size(); i++) {
            ArrayList<JSimpleEntry> valueEntries = simpleEntries.get(i);
            RingElement[] factors = new RingElement[valueEntries.size()];
            for (int j = 0; j < valueEntries.size(); j++) {
                JSimpleEntry entry = valueEntries.get(j);
                factors[j] = (RingElement)entry.getValue();
                if (factors[j] == null) {
                    error = true;
                }
            }
            if (error) {
                return null;
            }
            elements[i] = ProductElement.make(factors);
        }
        return ProductProperFreeElement.make(elements);
    }
    
    
    public void setValue(ModuleElement element) {
        ProductFreeElement el = (ProductFreeElement)element;
        for (int i = 0; i < simpleEntries.size(); i++) {
            ProductElement pel = (ProductElement)el.getComponent(i);
            ArrayList<JSimpleEntry> valueEntries = simpleEntries.get(i);
            for (int j = 0; j < valueEntries.size(); j++) {
                JSimpleEntry entry = valueEntries.get(j);
                entry.setValue(pel.getFactor(j));
            }
        }
    }
    
    
    private ArrayList<ArrayList<JSimpleEntry>> simpleEntries;
    private ProductFreeModule module = null;
}

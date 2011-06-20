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

package org.rubato.composer.dialogs.morphisms;

import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.composer.components.JMorphismEntry;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.CompositionMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;

class JCompositionMorphismType
        extends JMorphismType
        implements ActionListener, ChangeListener {

    public JCompositionMorphismType(JMorphismContainer container) {
        this.container = container;
        morphismCount = MIN_COMPONENTS;
        layoutPanel();
    }
    
    
    private void layoutPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        StringBuilder buf = new StringBuilder(20);
        buf.append("x"); //$NON-NLS-1$
        for (int i = 0; i < morphismCount; i++) {
            buf.append(")"); //$NON-NLS-1$
            buf.insert(0, "f"+(i+1)+"("); //$NON-NLS-1$ //$NON-NLS-2$
        }
        add(JMorphismDialog.createTitle(buf.toString()));
        
        JPanel morphismCountPanel = new JPanel();
        morphismCountPanel.setLayout(new BorderLayout());
        
        morphismCountSpinner = new JSpinner();
        morphismCountSpinner.setValue(morphismCount);
        morphismCountSpinner.addChangeListener(this);
        morphismCountSpinner.setToolTipText(Messages.getString("JCompositionMorphism.setcompnumber")); //$NON-NLS-1$
        morphismCountPanel.setBorder(makeTitledBorder(Messages.getString("JCompositionMorphism.compnumber"))); //$NON-NLS-1$
        morphismCountPanel.add(morphismCountSpinner);        
        add(morphismCountPanel);
        
        morphisms = new JMorphismEntry[morphismCount];
        for (int i = 0; i < morphismCount; i++) {
            morphisms[i] = new JMorphismEntry(null, null);
            morphisms[i].addActionListener(this);
            morphisms[i].setName(Integer.toString(i));
            morphisms[i].setBorder(makeTitledBorder("f"+(i+1)+"(x)")); //$NON-NLS-1$ //$NON-NLS-2$
            add(morphisms[i]);
        }
        morphisms[0].setDomain(container.getDomain());
        morphisms[morphismCount-1].setCodomain(container.getCodomain());
        
        container.pack();
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JMorphismEntry) {
            JMorphismEntry entry = (JMorphismEntry)src;
            int pos = Integer.parseInt(entry.getName());
            ModuleMorphism morphism = entry.getMorphism();
            if (morphism != null) {
                if (pos > 0) {
                    morphisms[pos-1].setCodomain(morphism.getDomain());
                }
                if (pos < morphismCount-1) {
                    morphisms[pos+1].setDomain(morphism.getCodomain());
                }
            }
            // check if all morphisms are defined
            for (int i = 0; i < morphismCount; i++) {
                if (morphisms[i].getMorphism() == null) {
                    // one morphism is not defined
                    return;
                }
            }
            try {
                ModuleMorphism f = morphisms[0].getMorphism();
                for (int i = 1; i < morphismCount; i++) {
                    ModuleMorphism g = morphisms[i].getMorphism(); 
                    f = g.compose(f);
                }
                container.setMorphism(f);
            }
            catch (CompositionException e1) {
                // composition failed
            }
        }
    }

    
    public void stateChanged(ChangeEvent e) {
        int v = (Integer)morphismCountSpinner.getValue();
        if (v < MIN_COMPONENTS) { 
            v = MIN_COMPONENTS;
            morphismCountSpinner.setValue(MIN_COMPONENTS);
        }
        else if (v > MAX_COMPONENTS) {
            v = MAX_COMPONENTS;
            morphismCountSpinner.setValue(MAX_COMPONENTS);
        }
        if (v != morphismCount) {
            container.setMorphism(null);
            morphismCount = v;
            removeAll();
            layoutPanel();
            morphismCountSpinner.grabFocus();
        }
    }

    
    public void editMorphism(ModuleMorphism morphism) {
        CompositionMorphism m = (CompositionMorphism)morphism;
        morphismCount = 2;
        removeAll();
        layoutPanel();
        morphisms[1].setMorphism(m.getFirstMorphism());
        morphisms[0].setMorphism(m.getSecondMorphism());
    }

    
    private JMorphismContainer container;
    private JSpinner           morphismCountSpinner;
    private int                morphismCount;
    private JMorphismEntry[]   morphisms;
    
    // the minimum number of components
    private final static int MIN_COMPONENTS = 2;
    // the maximum number of components
    private final static int MAX_COMPONENTS = 20;
}
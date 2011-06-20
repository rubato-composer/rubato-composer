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

package org.rubato.composer.dialogs.denotators;

import static org.rubato.composer.Utilities.getJDialog;
import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.rubato.composer.components.JMorphismEntry;
import org.rubato.composer.components.JSimpleEntry;
import org.rubato.math.module.DomainException;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.*;

public class JSimpleDenotatorEntry
        extends AbstractDenotatorEntry
        implements ActionListener {

    public JSimpleDenotatorEntry(SimpleForm form) {
        this.form = form;
        setLayout(new BorderLayout());
        
        // address type buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        Box addressButtonBox = Box.createHorizontalBox();
        addressButtonBox.add(Box.createHorizontalGlue());
        
        nonNullButton = new JRadioButton(Messages.getString("JSimpleDenotatorEntry.nonnull")); //$NON-NLS-1$
        nonNullButton.addActionListener(this);
        buttonGroup.add(nonNullButton);
        addressButtonBox.add(nonNullButton);
        addressButtonBox.add(Box.createHorizontalStrut(10));
        
        nullButton = new JRadioButton(Messages.getString("JSimpleDenotatorEntry.null")); //$NON-NLS-1$
        nullButton.addActionListener(this);
        buttonGroup.add(nullButton);
        addressButtonBox.add(nullButton);
        addressButtonBox.add(Box.createHorizontalGlue());
        
        addressButtonBox.setBorder(makeTitledBorder(Messages.getString("JSimpleDenotatorEntry.addresstype"))); //$NON-NLS-1$

        nullButton.setSelected(true);
        
        add(addressButtonBox, BorderLayout.NORTH);
        
        // element and morphism panel            
        valuePanel = new JPanel();
        valuePanel.setBorder(makeTitledBorder(Messages.getString("JSimpleDenotatorEntry.value"))); //$NON-NLS-1$
        valuePanel.setLayout(new BorderLayout());
        fillValuePanel();
        add(valuePanel, BorderLayout.CENTER);
    }
        
    
    public Denotator getDenotator(String name) {
        ModuleElement element = null;
        ModuleMorphism morphism = null;
        SimpleDenotator denotator;
        if (simpleEntry != null) {
            element = simpleEntry.getValue();
        }
        if (morphismEntry != null) {
            morphism = morphismEntry.getMorphism();
        }
        if (element == null && morphism == null) {
            denotator = null;
        }
        else {
            try {
                NameDenotator nameDenotator = null;
                if (name != null) {
                    nameDenotator = NameDenotator.make(name);
                }
                if (element != null) {
                    denotator = new SimpleDenotator(nameDenotator, form, element);
                }
                else  {
                    denotator = new SimpleDenotator(nameDenotator, form, morphism);
                }
            }
            catch (DomainException e) {
                throw new AssertionError("This should never happen!"); //$NON-NLS-1$
            }
        }
        return denotator;
    }
    
    
    public boolean canCreate() {
        if (simpleEntry != null) {
            return simpleEntry.getValue() != null;
        }
        else if (morphismEntry != null) {
            return morphismEntry.getMorphism() != null;
        }
        else {
            return false;
        }
    }

    
    public void clear() {
        if (simpleEntry != null) {
            simpleEntry.clear();
        }
        if (morphismEntry != null) {
            morphismEntry.clear();
        }        
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == nullButton || src == nonNullButton) {
            fillValuePanel();
            getJDialog(this).pack();
        }
        fireActionEvent();
    }
    
    
    private void fillValuePanel() {
        valuePanel.removeAll();
        if (nullButton.isSelected()) {
            simpleEntry = JSimpleEntry.make(form.getModule());
            simpleEntry.addActionListener(this);
            morphismEntry = null;
            valuePanel.add(simpleEntry, BorderLayout.NORTH);
        }
        else if (nonNullButton.isSelected()) {
            morphismEntry = new JMorphismEntry(null, form.getModule());
            morphismEntry.addActionListener(this);
            simpleEntry = null;
            valuePanel.add(morphismEntry, BorderLayout.NORTH);
        }
    }


    private SimpleForm     form;
    private JRadioButton   nullButton;
    private JRadioButton   nonNullButton;
    private JPanel         valuePanel;
    private JSimpleEntry   simpleEntry   = null;
    private JMorphismEntry morphismEntry = null;
}

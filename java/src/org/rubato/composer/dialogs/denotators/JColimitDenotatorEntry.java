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

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.rubato.base.Repository;
import org.rubato.base.RubatoDictionary;
import org.rubato.base.RubatoException;
import org.rubato.composer.components.JSelectDenotator;
import org.rubato.math.yoneda.*;

public class JColimitDenotatorEntry
        extends AbstractDenotatorEntry
        implements ActionListener {

    public JColimitDenotatorEntry(ColimitForm form) {
        this(form, Repository.systemRepository());
    }

    
    public JColimitDenotatorEntry(ColimitForm form, RubatoDictionary dict) {
        this.form = form;
        this.dict = dict;
        setLayout(new BorderLayout());
        createLayout();
    }

    
    public Denotator getDenotator(String name) {
        Denotator d = selectDenotator.getDenotator();
        Denotator res;
        try {
            res = new ColimitDenotator(NameDenotator.make(name), form, currentIndex, d);
        }
        catch (RubatoException e) {
            res = null;
        }
        return res;
    }
    
    
    public boolean canCreate() {
        return selectDenotator.getDenotator() != null;
    }

    
    public void clear() {
        selectDenotator.clear();
        fireActionEvent();
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == comboBox) {
            int index = comboBox.getSelectedIndex();
            if (index != currentIndex) {
                currentIndex = index;
                remove(selectDenotator);
                selectDenotator = new JSelectDenotator(dict, form.getForm(index));
                add(selectDenotator, BorderLayout.SOUTH);
                getJDialog(this).pack();
                fireActionEvent();
            }
        }
        fireActionEvent();
    }
    
    
    private void createLayout() {
        final int formCount = form.getFormCount();
        
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.setLayout(new BorderLayout());
        String title = form.hasLabels()?Messages.getString("JColimitDenotatorEntry.label"):Messages.getString("JColimitDenotatorEntry.index"); //$NON-NLS-1$ //$NON-NLS-2$
        comboBoxPanel.setBorder(makeTitledBorder(title));
        comboBox = new JComboBox();
        for (int i = 0; i < formCount; i++) {
            String labelName = form.indexToLabel(i);
            comboBox.addItem(labelName);
        }
        
        comboBox.addActionListener(this);
        comboBoxPanel.add(comboBox, BorderLayout.CENTER);
        add(comboBoxPanel, BorderLayout.NORTH);
        
        selectDenotator = new JSelectDenotator(dict, form.getForm(0));
        selectDenotator.addActionListener(this);
        add(selectDenotator, BorderLayout.SOUTH);
    }

    
    private ColimitForm      form;
    private JComboBox        comboBox;
    private JSelectDenotator selectDenotator;
    private int              currentIndex = 0;
    private RubatoDictionary dict;
}

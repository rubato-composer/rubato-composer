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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.rubato.base.Repository;
import org.rubato.base.RubatoDictionary;
import org.rubato.base.RubatoException;
import org.rubato.composer.components.JSelectDenotator;
import org.rubato.math.yoneda.*;

public class JLimitDenotatorEntry
        extends AbstractDenotatorEntry
        implements ActionListener {

    public JLimitDenotatorEntry(LimitForm form) {
        this(form, Repository.systemRepository());
    }


    public JLimitDenotatorEntry(LimitForm form, RubatoDictionary dict) {
        this.form = form;
        this.dict = dict;
        setLayout(new BorderLayout());
        createLayout();
    }


    public Denotator getDenotator(String name) {
        LinkedList<Denotator> denoList = new LinkedList<Denotator>();
        for (int i = 0; i < selectDenotator.length; i++) {
            Denotator d = selectDenotator[i].getDenotator();
            if (d == null) {
                return null;
            }
            else {
                denoList.add(d);
            }
        }
        LimitDenotator res;
        try {
            res = new LimitDenotator(NameDenotator.make(name), form, denoList);
        }
        catch (RubatoException e) {
            res = null;
        }
        return res;
    }
    
    
    public boolean canCreate() {
        for (int i = 0; i < selectDenotator.length; i++) {
            if (selectDenotator[i].getDenotator() == null) {
                return false;
            }
        }
        return true;
    }
    
    
    public void clear() {
        for (int i = 0; i < selectDenotator.length; i++) {
            selectDenotator[i].clear();
        }
    }

    
    private void createLayout() {
        final int formCount = form.getFormCount();
        selectDenotator = new JSelectDenotator[formCount];
        JPanel denotatorPanel = new JPanel();
        denotatorPanel.setLayout(new GridLayout(formCount, 1));
        for (int i = 0; i < formCount; i++) {
            selectDenotator[i] = new JSelectDenotator(dict, form.getForm(i), form.indexToLabel(i));
            selectDenotator[i].addActionListener(this);
            denotatorPanel.add(selectDenotator[i]);
        }
        add(denotatorPanel, BorderLayout.SOUTH);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        fireActionEvent();
    }
    

    private LimitForm          form;
    private JSelectDenotator[] selectDenotator;
    private RubatoDictionary   dict;
}

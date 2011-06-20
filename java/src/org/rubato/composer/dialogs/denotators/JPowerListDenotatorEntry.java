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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.base.Repository;
import org.rubato.base.RubatoDictionary;
import org.rubato.base.RubatoException;
import org.rubato.composer.dialogs.JSelectDenotatorDialog;
import org.rubato.math.yoneda.*;

public class JPowerListDenotatorEntry
        extends AbstractDenotatorEntry
        implements ActionListener, ListSelectionListener {

    public JPowerListDenotatorEntry(PowerForm form) {
        this(form, Repository.systemRepository());
    }


    public JPowerListDenotatorEntry(ListForm form) {
        this(form, Repository.systemRepository());
    }

    
    public JPowerListDenotatorEntry(PowerForm form, RubatoDictionary dict) {
        powerForm = form;
        listForm = null;
        baseForm = form.getForm();
        isList = false;
        this.dict = dict;
        setLayout(new BorderLayout(5, 5));
        setBorder(emptyBorder);
        createLayout();
    }

    
    public JPowerListDenotatorEntry(ListForm form, RubatoDictionary dict) {
        powerForm = null;
        listForm = form;
        baseForm = form.getForm();
        isList = true;
        this.dict = dict;
        setLayout(new BorderLayout(5, 5));
        setBorder(emptyBorder);
        createLayout();
    }
    
    
    public Denotator getDenotator(String name) {
        LinkedList<Denotator> denoList = new LinkedList<Denotator>();
        for (int i = 0; i < listModel.getSize(); i++) {
            DenotatorInfo di = (DenotatorInfo)listModel.getElementAt(i);
            denoList.add(di.denotator);
        }
        Denotator res = null;
        try {
            if (isList) {            
                res = new ListDenotator(NameDenotator.make(name), listForm, denoList);
            }
            else {
                res = new PowerDenotator(NameDenotator.make(name), powerForm, denoList);
            }
        }
        catch (RubatoException e) { /* do nothing */ }
        return res;
    }
    
    
    public boolean canCreate() {
        return true;
    }
    
    
    public void clear() {
        listModel.removeAllElements();
    }

    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addButton) {
            addDenotator(JSelectDenotatorDialog.showDialog(this, dict, baseForm));
        }
        else if (src == addNewButton) {
            addDenotator(JDenotatorDialog.showDialog(this, false, baseForm));
            
        }
        else if (src == removeButton) {
            listModel.removeElementAt(denoJList.getSelectedIndex());
        }
        else if (src == upButton) {
            int i = denoJList.getSelectedIndex();
            if (i > 0) {
                Object obj = listModel.getElementAt(i);
                listModel.removeElementAt(i);
                listModel.add(i-1, obj);
                denoJList.setSelectedIndex(i-1);
            }
        }
        else if (src == downButton) {
            int i = denoJList.getSelectedIndex();
            if (i < listModel.getSize()-1) {
                Object obj = listModel.getElementAt(i);
                listModel.removeElementAt(i);
                listModel.add(i+1, obj);
                denoJList.setSelectedIndex(i+1);
            }
        }
        fireActionEvent();
    }

    
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (denoJList.isSelectionEmpty()) {
                removeButton.setEnabled(false);
                if (isList) {
                    upButton.setEnabled(false);
                    downButton.setEnabled(false);
                }
                displayDenotator(null);
            }
            else {
                removeButton.setEnabled(true);
                if (isList) {
                    if (denoJList.getSelectedIndex() > 0) {
                        upButton.setEnabled(true);
                    }
                    if (denoJList.getSelectedIndex() < listModel.getSize()-1) {
                        downButton.setEnabled(true);
                    }
                }
                Denotator d = ((DenotatorInfo)denoJList.getSelectedValue()).denotator;
                displayDenotator(d);
            }
        }
    }

    
    private void createLayout() {
        listModel = new DenotatorListModel();
        denoJList = new JList(listModel);
        denoJList.addListSelectionListener(this);
        denoJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(denoJList), BorderLayout.CENTER);
        
        // bottom box
        Box bottomBox = Box.createVerticalBox();
        
        // display area
        display = new JTextArea(5, 10);
        display.setEditable(false);
        bottomBox.add(new JScrollPane(display));
        bottomBox.add(Box.createVerticalStrut(5));
        
        // button box
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        addButton = new JButton(Messages.getString("JPowerListDenotatorEntry.add")); //$NON-NLS-1$
        addButton.addActionListener(this);
        buttonBox.add(addButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        addNewButton = new JButton(Messages.getString("JPowerListDenotatorEntry.addnew")); //$NON-NLS-1$
        addNewButton.addActionListener(this);
        buttonBox.add(addNewButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        removeButton = new JButton(Messages.getString("JPowerListDenotatorEntry.remove")); //$NON-NLS-1$
        removeButton.setEnabled(false);
        removeButton.addActionListener(this);
        buttonBox.add(removeButton);
        if (isList) {
            buttonBox.add(Box.createHorizontalStrut(5));
            upButton = new JButton(Messages.getString("JPowerListDenotatorEntry.up")); //$NON-NLS-1$
            upButton.setEnabled(false);
            upButton.addActionListener(this);
            buttonBox.add(upButton);
            buttonBox.add(Box.createHorizontalStrut(5));
            downButton = new JButton(Messages.getString("JPowerListDenotatorEntry.down")); //$NON-NLS-1$
            downButton.setEnabled(false);
            downButton.addActionListener(this);
            buttonBox.add(downButton);
        }
        buttonBox.add(Box.createHorizontalGlue());
        
        bottomBox.add(buttonBox);
        add(bottomBox, BorderLayout.SOUTH);
    }

    
    private void displayDenotator(Denotator d) {
        if (d != null) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            d.display(new PrintStream(bs));
            display.setText(bs.toString());
            display.setCaretPosition(0);
        }
        else {
            display.setText(""); //$NON-NLS-1$
        }
    }
    
    
    private void addDenotator(Denotator d) {
        listModel.addDenotator(d);
    }
    
    
    protected class DenotatorListModel extends DefaultListModel {
        
        public void setDenotators(DenotatorInfo[] infos) {
            for (DenotatorInfo info : infos) {
                addElement(info);
            }
        }
        
        public void addDenotator(Denotator d) {
            if (d != null) {
                addElement(new DenotatorInfo(d));
            }
        }
    }
    
    
    private class DenotatorInfo implements Comparable<DenotatorInfo> {
        
        public DenotatorInfo(Denotator d) {
            denotator = d;
            name = d.getNameString();
            if (name.length() == 0) {
                name = ((Object)d).toString();
            }
        }
        
        public int compareTo(DenotatorInfo object) {
            return denotator.compareTo(object.denotator);
        }
        
        public String toString() {
            return name;
        }
        
        public Denotator denotator;        
        public String    name;        
    }


    private PowerForm powerForm;
    private ListForm  listForm;
    private Form      baseForm;
    private boolean   isList;
    private JList     denoJList;
    private JTextArea display;
    private JButton   addButton    = null;
    private JButton   addNewButton = null;
    private JButton   removeButton = null;
    private JButton   upButton     = null;
    private JButton   downButton   = null;

    private DenotatorListModel listModel;
    
    private RubatoDictionary dict;

    private final static Border emptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
}

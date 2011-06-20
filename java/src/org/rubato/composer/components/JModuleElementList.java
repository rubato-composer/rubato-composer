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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;


/**
 * @author Gérard Milmeister
 */
public class JModuleElementList
        extends JPanel
        implements ActionListener, ListSelectionListener {

    public JModuleElementList(Module module) {
        this.module = module;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        layoutPanel();
    }
    
    
    public List<ModuleElement> getElements() {
        LinkedList<ModuleElement> list = new LinkedList<ModuleElement>();
        for (int i = 0; i < listModel.size(); i++) {
            ElementEntry entry = (ElementEntry)listModel.get(i);
            list.add(entry.element);
        }
        return list;
    }
    
    
    public void addElement(ModuleElement element) {
        listModel.addModuleElement(element);
        clearButton.setEnabled(true);
        removeButton.setEnabled(!elementList.isSelectionEmpty());
    }

    
    public void addElements(List<ModuleElement> elements) {
        for (ModuleElement e : elements) {            
            listModel.addModuleElement(e);
        }
        if (listModel.size() > 0) {
            clearButton.setEnabled(true);
        }
        removeButton.setEnabled(!elementList.isSelectionEmpty());
    }

    
    public void clear() {
        listModel.removeAllElements();
        removeButton.setEnabled(false);
    }

    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addButton) {
            ModuleElement el = simpleEntry.getValue();
            if (el != null) {
                listModel.addModuleElement(el);
                simpleEntry.clear();
            }
        }
        else if (src == removeButton) {
            Object[] selectedValues = elementList.getSelectedValues();
            for (Object obj : selectedValues) {
                listModel.removeElement(obj);
            }
        }
        else if (src == clearButton) {
            listModel.removeAllElements();
        }
        else if (src == upButton) {
            int i = elementList.getSelectedIndex();
            Object obj = listModel.remove(i);
            listModel.add(i-1, obj);
            elementList.setSelectedIndex(i-1);
        }
        else if (src == downButton) {
            int i = elementList.getSelectedIndex();
            Object obj = listModel.remove(i);
            listModel.add(i+1, obj);
            elementList.setSelectedIndex(i+1);
        }
        removeButton.setEnabled(!elementList.isSelectionEmpty());
        clearButton.setEnabled(listModel.size() > 0);
    }

    
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            removeButton.setEnabled(true);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            if (elementList.getSelectedIndex() > 0) {
                upButton.setEnabled(true);
            }
            if (elementList.getSelectedIndex() < listModel.size()-1) {
                downButton.setEnabled(true);
            }
        }
    }

    
    private void layoutPanel() {
        listModel = new ElementListModel();
        elementList = new JList(listModel);
        elementList.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(elementList);
        add(scrollPane);
        add(Box.createVerticalStrut(5));
        
        simpleEntry = JSimpleEntry.make(module);
        add(simpleEntry);
        add(Box.createVerticalStrut(5));
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        addButton = new JButton(ADD);
        addButton.setToolTipText(ADD_TIP);
        addButton.addActionListener(this);
        buttonBox.add(addButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        removeButton = new JButton(REMOVE);
        removeButton.setToolTipText(REMOVE_TIP);
        removeButton.setEnabled(false);
        removeButton.addActionListener(this);
        buttonBox.add(removeButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        clearButton = new JButton(CLEAR);
        clearButton.setToolTipText(CLEAR_TIP);
        clearButton.setEnabled(false);
        clearButton.addActionListener(this);
        buttonBox.add(clearButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        upButton = new JButton(UP);
        upButton.setToolTipText(UP_TIP);
        upButton.setEnabled(false);
        upButton.addActionListener(this);
        buttonBox.add(upButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        downButton = new JButton(DOWN);
        downButton.setToolTipText(DOWN_TIP);
        downButton.setEnabled(false);
        downButton.addActionListener(this);
        buttonBox.add(downButton);
        buttonBox.add(Box.createHorizontalGlue());
        add(buttonBox);
    }

    
    private class ElementEntry {
        public ElementEntry(ModuleElement e) {
            element = e;
        }
        public String toString() {
            return element.stringRep();
        }
        ModuleElement element;
    }

    
    protected class ElementListModel extends DefaultListModel {        
        public void addModuleElement(ModuleElement e) {
            if (e != null) {
                addElement(new ElementEntry(e));
            }
        }
    }
    
    
    private JSimpleEntry simpleEntry;
    private JButton      clearButton;
    private JButton      addButton;
    private JButton      removeButton;
    private JButton      upButton;
    private JButton      downButton;

    private JList            elementList;
    private ElementListModel listModel;

    private Module          module;

    private final static String ADD        = Messages.getString("JModuleElementList.add"); //$NON-NLS-1$
    private final static String REMOVE     = Messages.getString("JModuleElementList.remove"); //$NON-NLS-1$
    private final static String CLEAR      = Messages.getString("JModuleElementList.clear"); //$NON-NLS-1$
    private final static String UP         = Messages.getString("JModuleElementList.up"); //$NON-NLS-1$
    private final static String DOWN       = Messages.getString("JModuleElementList.down"); //$NON-NLS-1$
    private final static String ADD_TIP    = Messages.getString("JModuleElementList.addtooltip"); //$NON-NLS-1$
    private final static String REMOVE_TIP = Messages.getString("JModuleElementList.removetooltip"); //$NON-NLS-1$
    private final static String CLEAR_TIP  = Messages.getString("JModuleElementList.cleartooltip"); //$NON-NLS-1$
    private final static String UP_TIP     = Messages.getString("JModuleElementList.uptip"); //$NON-NLS-1$
    private final static String DOWN_TIP   = Messages.getString("JModuleElementList.downtip"); //$NON-NLS-1$
}

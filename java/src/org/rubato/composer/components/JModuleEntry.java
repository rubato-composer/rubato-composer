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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import org.rubato.composer.dialogs.JModuleDialog;
import org.rubato.composer.dialogs.JModuleListDialog;
import org.rubato.math.module.Module;

/**
 * @author Gérard Milmeister
 */
public class JModuleEntry extends JPanel implements ActionListener {

    public JModuleEntry() {
        setLayout(new BorderLayout(5, 0));
        
        moduleLabel = new JLabelField();
        moduleLabel.addActionListener(this);
        add(moduleLabel, BorderLayout.CENTER);
        
        Box box = Box.createHorizontalBox();
        
        box.add(Box.createHorizontalStrut(5));
        
        createButton = new JButton(CREATE_BUTTON);
        createButton.setToolTipText(CREATE_BUTTON_TIP);
        createButton.addActionListener(this);
        box.add(createButton);
        
        box.add(Box.createHorizontalStrut(5));
        
        selectButton = new JButton(SELECT_BUTTON);
        selectButton.setToolTipText(SELECT_BUTTON_TIP);
        selectButton.addActionListener(this);
        box.add(selectButton);
        
        add(box, BorderLayout.EAST);
    }

    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == selectButton) {
            select();
        }
        else if (src == createButton) {
            create();
        }
        else if (src == moduleLabel) {
            create();
        }
    }
    

    private void select() {
        JModuleListDialog listDialog = new JModuleListDialog(JOptionPane.getFrameForComponent(this));
        listDialog.setLocationRelativeTo(this);
        listDialog.setVisible(true);
        Module m = listDialog.getModule();
        if (m != null) {
            module = m;
            updateField();
        }
    }
    
    
    private void create() {
        Module m = JModuleDialog.showDialog(this, false);
        if (m != null) {
            module = m;
            updateField();
        }            
    }
    
    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }


    protected void fireActionEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 0, EMPTY_STRING);
                }
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }
    
    
    public Module getModule() {
        return module;
    }
    
    
    public void setModule(Module module) {
        this.module = module;
        if (module != null) {
            moduleLabel.setText(module.toVisualString());
        }
    }
    
    public void clear() {
        module = null;
        updateField();
    }
    
    
    private void updateField() {
        if (module != null) {
            moduleLabel.setText(module.toVisualString());
            moduleLabel.setToolTipText(moduleLabel.getText());
        }
        else {
            moduleLabel.clear();
            moduleLabel.setToolTipText(null);
        }
        fireActionEvent();
    }
    
    
    private JLabelField moduleLabel;
    private JButton     selectButton;
    private JButton     createButton;
    private Module      module = null;
    
    private ActionEvent actionEvent = null;
    private EventListenerList listenerList = new EventListenerList();   

    private final static String EMPTY_STRING = ""; //$NON-NLS-1$
    private final static String SELECT_BUTTON = Messages.getString("JModuleEntry.select"); //$NON-NLS-1$;
    private final static String SELECT_BUTTON_TIP = Messages.getString("JModuleEntry.selectmodule"); //$NON-NLS-1$
    private final static String CREATE_BUTTON = Messages.getString("JModuleEntry.create"); //$NON-NLS-1$
    private final static String CREATE_BUTTON_TIP = Messages.getString("JModuleEntry.createmodule"); //$NON-NLS-1$
}

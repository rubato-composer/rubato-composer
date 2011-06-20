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

import org.rubato.composer.dialogs.morphisms.JMorphismDialog;
import org.rubato.composer.dialogs.morphisms.JMorphismListDialog;
import org.rubato.math.module.Module;
import org.rubato.math.module.morphism.ModuleMorphism;


/**
 * @author Gérard Milmeister
 */
public class JMorphismEntry extends JPanel implements ActionListener {

    public JMorphismEntry(Module domain, Module codomain) {
        setLayout(new BorderLayout(5, 0));
        
        this.domain = domain;
        this.codomain = codomain;
        
        morphismLabel = new JLabelField();
        morphismLabel.addActionListener(this);
        add(morphismLabel, BorderLayout.CENTER);
        
        Box box = Box.createHorizontalBox();
        
        selectButton = new JButton(Messages.getString("JMorphismEntry.select")); //$NON-NLS-1$
        selectButton.setToolTipText(Messages.getString("JMorphismEntry.selectMorph")); //$NON-NLS-1$
        selectButton.addActionListener(this);
        box.add(selectButton);
        
        box.add(Box.createHorizontalStrut(10));

        editButton = new JButton(Messages.getString("JMorphismEntry.editbutton")); //$NON-NLS-1$
        editButton.setToolTipText(Messages.getString("JMorphismEntry.edittooltip")); //$NON-NLS-1$
        editButton.addActionListener(this);
        editButton.setEnabled(morphism != null);
        box.add(editButton);

        box.add(Box.createHorizontalStrut(10));

        createButton = new JButton(Messages.getString("JMorphismEntry.create")); //$NON-NLS-1$
        createButton.setToolTipText(Messages.getString("JMorphismEntry.createMorph")); //$NON-NLS-1$
        createButton.addActionListener(this);
        box.add(createButton);
        
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
        else if (src == editButton) {
            edit();
        }
        else if (src == morphismLabel) {
            if (morphism != null) {
                edit();
            }
            else {
                create();
            }
        }
    }
    
    
    private void select() {
        JMorphismListDialog listDialog = new JMorphismListDialog(JOptionPane.getFrameForComponent(this), domain, codomain);
        listDialog.setLocationRelativeTo(this);
        listDialog.setVisible(true);
        ModuleMorphism m = listDialog.getMorphism();
        if (m != null) {
            morphism = m;
            updateField();
        }
    }
    
    
    private void create() {
        JMorphismDialog moduleDialog = new JMorphismDialog(JOptionPane.getFrameForComponent(this), true, false, domain, codomain);
        moduleDialog.setLocationRelativeTo(this);
        moduleDialog.setVisible(true);
        ModuleMorphism m = moduleDialog.getMorphism();
        if (m != null) {
            morphism = m;
            updateField();
        }            
    }

    
    private void edit() {
        JMorphismDialog moduleDialog = new JMorphismDialog(JOptionPane.getFrameForComponent(this), false, morphism);
        moduleDialog.setLocationRelativeTo(this);
        moduleDialog.setVisible(true);
        ModuleMorphism m = moduleDialog.getMorphism();
        if (m != null) {
            morphism = m;
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
    
    
    public void clear() {
        setMorphism(null);
    }
    
    
    public ModuleMorphism getMorphism() {
        return morphism;
    }
    
    
    public void setMorphism(ModuleMorphism m) {
        morphism = m;
        updateField();
    }
    
    
    public void setDomain(Module domain) {
        this.domain = domain;
    }
    
    
    public Module getDomain() {
        return domain;
    }
    
    
    public void setCodomain(Module codomain) {
        this.codomain = codomain;
    }
    
    
    public Module getCodomain() {
        return codomain;
    }
    
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        selectButton.setEnabled(enabled);
        createButton.setEnabled(enabled);
    }
    
    
    private void updateField() {
        if (morphism != null) {
            morphismLabel.setText(morphism.toString());
            morphismLabel.setToolTipText(morphismLabel.getText());
        }
        else {
            morphismLabel.clear();
            morphismLabel.setToolTipText(null);
        }
        editButton.setEnabled(morphism != null);
        fireActionEvent();
    }
    
    
    private JLabelField    morphismLabel;
    private JButton        selectButton;
    private JButton        createButton;
    private JButton        editButton;
    
    private ModuleMorphism morphism    = null;
    private Module         domain      = null;
    private Module         codomain    = null;
    private ActionEvent    actionEvent = null;

    private EventListenerList listenerList = new EventListenerList();
    
    private final static String EMPTY_STRING = ""; //$NON-NLS-1$
}

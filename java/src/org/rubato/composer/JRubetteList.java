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

package org.rubato.composer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.base.Rubette;
import org.rubato.composer.network.NetworkModel;
import org.rubato.composer.rubette.JRubette;
import org.rubato.rubettes.builtin.MacroRubette;

public final class JRubetteList extends JPanel implements ActionListener {

    public JRubetteList(JComposer jcomposer) {
        this.jcomposer = jcomposer;
        setLayout(new BorderLayout());
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(1.0);
        splitPane.setContinuousLayout(true);
        
        listModel = new RubetteListModel();
        rubetteList = new JList(listModel);
        rubetteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rubetteList.setCellRenderer(listModel.getListCellRenderer());
        rubetteList.setDragEnabled(true);
        rubetteList.setTransferHandler(new RubetteTransferHandler());
        rubetteList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                singleClick();
                if (e.getClickCount() == 2) {
                    doubleClick();
                }
                super.mouseClicked(e);
            }
        });
        rubetteList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    update();
                }
            }
        });
        splitPane.add(new JScrollPane(rubetteList));
        
        rubetteDoc = new JTextArea(10, 0);
        rubetteDoc.setLineWrap(true);
        rubetteDoc.setWrapStyleWord(true);
        rubetteDoc.setEditable(false);
        splitPane.add(new JScrollPane(rubetteDoc));
        
        add(splitPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        addButton = new JButton(Messages.getString("JRubetteList.add")); //$NON-NLS-1$
        addButton.setEnabled(false);
        buttonPanel.add(addButton);
        
        editButton = new JButton(Messages.getString("JRubetteList.edit")); //$NON-NLS-1$
        editButton.setEnabled(false);
        editButton.addActionListener(this);
        buttonPanel.add(editButton);
        
        removeButton = new JButton(Messages.getString("JRubetteList.remove")); //$NON-NLS-1$
        removeButton.setEnabled(false);
        removeButton.addActionListener(this);
        buttonPanel.add(removeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void addAddButtonAction(Action action) {
        addButton.addActionListener(action);
    }


    public void addRubette(Rubette rubette) {
        listModel.addRubette(rubette);
    }


    public void removeRubette(Rubette rubette) {
        listModel.removeRubette(rubette);
    }

    
    private RubetteInfo getSelectedValue() {
        RubetteInfo info = (RubetteInfo)rubetteList.getSelectedValue();
        if (info == null || info.isGroup()) {
            return null;
        }
        
        return info;
    }

    
    public JRubette getCurrentRubette() {
        RubetteInfo info = getSelectedValue();
        if (info == null) {
            return null;
        }
        Rubette rubette = info.getRubette().newInstance();
        String name = rubette.getName()+" #"+(nameCounter++); //$NON-NLS-1$
        if (rubette instanceof MacroRubette) {
            ((MacroRubette)rubette).setName(name);
        }
        JRubette jrubette = new JRubette(0, 0, rubette, name);
        return jrubette;
    }
    
    
    public JRubette createJRubette(Rubette rubette) {
        Rubette arubette = rubette.newInstance();
        String name = arubette.getName()+" #"+(nameCounter++); //$NON-NLS-1$
        if (arubette instanceof MacroRubette) {
            ((MacroRubette)arubette).setName(name);
        }
        JRubette jrubette = new JRubette(0, 0, arubette, name);
        return jrubette;        
    }
    
    
    public JRubette duplicate(Rubette rubette) {
        Rubette arubette = rubette.duplicate();
        String name = arubette.getName()+" #"+(nameCounter++); //$NON-NLS-1$
        if (arubette instanceof MacroRubette) {
            ((MacroRubette)arubette).setName(name);
        }
        JRubette jrubette = new JRubette(0, 0, arubette, name);
        return jrubette;        
    }

    
    protected void singleClick() {
        RubetteInfo info = (RubetteInfo)rubetteList.getSelectedValue();
        if (info != null) {
            if (info.isGroup()) {
                listModel.toggleGroup(info.getGroup());
            }
        }
    }
    
    
    protected void doubleClick() {
        RubetteInfo info = (RubetteInfo)rubetteList.getSelectedValue();
        if (info != null) {
            if (info.isGroup()) {
                listModel.toggleGroup(info.getGroup());
            }
            else {
                addButton.doClick();
            }
        }
    }
    
    
    protected void update() {
        RubetteInfo info = (RubetteInfo)rubetteList.getSelectedValue();
        if (info != null) {
            if (info.isGroup()) {
                rubetteDoc.setText(null);
                addButton.setEnabled(false);
                editButton.setEnabled(false);
                removeButton.setEnabled(false);
            }
            else {
                Rubette rubette = info.getRubette();
                rubetteDoc.setText(rubette.getLongDescription());
                addButton.setEnabled(true);
                boolean b = rubette instanceof MacroRubette;
                editButton.setEnabled(b);
                removeButton.setEnabled(b);
            }
        }
    }
    
    
    private void edit() {
        RubetteInfo info = getSelectedValue();
        if (info != null && (info.getRubette() instanceof MacroRubette)) {
            MacroRubette nrubette = (MacroRubette)info.getRubette();
            NetworkModel networkModel = nrubette.getNetworkModel();
            jcomposer.addJMacroRubetteView(networkModel, null);
        }
    }
    
    
    private void remove() {
        RubetteInfo info = (RubetteInfo)rubetteList.getSelectedValue();
        if (info != null && (info.getRubette()instanceof MacroRubette)) {
            MacroRubette nrubette = (MacroRubette)info.getRubette();
            NetworkModel networkModel = nrubette.getNetworkModel();
            jcomposer.removeJNetworkForModel(networkModel);
            jcomposer.getRubetteManager().removeRubette(nrubette);
            listModel.removeRubette(nrubette);            
        }        
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == editButton) {
            edit();
        }
        else if (e.getSource() == removeButton) {
            remove();
        }
    }

    
    private JButton          addButton;
    private JButton          editButton;
    private JButton          removeButton;

    private JList            rubetteList;
    private JTextArea        rubetteDoc;
    private RubetteListModel listModel;

    private JComposer        jcomposer;
    
    private int              nameCounter = 1;

}

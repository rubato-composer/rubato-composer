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

package org.rubato.composer.dialogs;

import static org.rubato.composer.Utilities.installEnterKey;
import static org.rubato.composer.Utilities.installEscapeKey;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import org.rubato.base.Repository;
import org.rubato.math.module.Module;


/**
 * @author Gérard Milmeister
 */
public final class JModuleListDialog
        extends JDialog
        implements ActionListener {
    
    public JModuleListDialog(Frame frame) {
        super(frame, Messages.getString("JModuleListDialog.selectmodule"), true); //$NON-NLS-1$
        setLayout(new BorderLayout());
        moduleList = new JList(getModuleNames());
        moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moduleList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String name = (String)moduleList.getSelectedValue();
                    if (name == null) {
                        module = null;
                    }
                    else {
                        module = rep.getModule(name);
                        setVisible(false);
                    }
                }
                super.mouseClicked(e);
            }
        });
        add(new JScrollPane(moduleList), BorderLayout.CENTER);
        JPanel buttonBox = new JPanel();
        cancelButton = new JButton(Messages.getString("JModuleListDialog.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(this);
        buttonBox.add(cancelButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        okButton = new JButton(Messages.getString("JModuleListDialog.ok")); //$NON-NLS-1$
        okButton.addActionListener(this);
        buttonBox.add(okButton);
        add(buttonBox, BorderLayout.SOUTH);
        
        installEscapeKey(this);
        Action enterAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                okButton.doClick();
            }
        };
        installEnterKey(this, enterAction);

        pack();
    }

    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            module = null;
            setVisible(false);
        }
        else if (e.getSource() == okButton) {
            String name = (String)moduleList.getSelectedValue();
            if (name == null) {
                module = null;
            }
            else {
                module = rep.getModule(name);
            }
            setVisible(false);
        }
    }
    
    
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width*3/2, size.height*3/2);
    }

    
    public Module getModule() {
        return module;
    }
    
    
    private String[] getModuleNames() {
        List<String> list = rep.getModuleNames();
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }
    
    
    protected Module  module = null;
    protected JList   moduleList;
    protected JButton cancelButton;
    protected JButton okButton;
    
    protected static final Repository rep = Repository.systemRepository();
}

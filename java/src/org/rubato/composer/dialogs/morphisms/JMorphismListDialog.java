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

import static org.rubato.composer.Utilities.installEnterKey;
import static org.rubato.composer.Utilities.installEscapeKey;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import org.rubato.base.Repository;
import org.rubato.math.module.Module;
import org.rubato.math.module.morphism.ModuleMorphism;

/**
 * @author Gérard Milmeister
 */
public class JMorphismListDialog
        extends JDialog
        implements ActionListener {

    public JMorphismListDialog(Frame frame, Module domain, Module codomain) {
        super(frame, Messages.getString("JMorphismListDialog.selectmorphism"), true); //$NON-NLS-1$
        setLayout(new BorderLayout());
        
        this.domain = domain;
        this.codomain = codomain;
        
        morphismList = new JList(getModuleNames());
        morphismList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        morphismList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String name = (String)morphismList.getSelectedValue();
                    if (name == null) {
                        morphism = null;
                    }
                    else {
                        morphism = rep.getModuleMorphism(name);
                        setVisible(false);
                    }
                }
                super.mouseClicked(e);
            }
        });
        add(new JScrollPane(morphismList), BorderLayout.CENTER);
        
        JPanel buttonBox = new JPanel();
        
        cancelButton = new JButton(Messages.getString("JMorphismListDialog.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(this);
        buttonBox.add(cancelButton);
        
        buttonBox.add(Box.createHorizontalStrut(10));
        
        okButton = new JButton(Messages.getString("JMorphismListDialog.ok")); //$NON-NLS-1$
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
            morphism = null;
            setVisible(false);
        }
        else if (e.getSource() == okButton) {
            String name = (String)morphismList.getSelectedValue();
            if (name == null) {
                morphism = null;
            }
            else {
                morphism = rep.getModuleMorphism(name);
            }
            setVisible(false);
        }
    }
    
    
    public ModuleMorphism getMorphism() {
        return morphism;
    }
    
    
    private String[] getModuleNames() {
        List<String> list = rep.getModuleMorphismNames(domain, codomain);
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }
    
    
    protected ModuleMorphism morphism = null;
    private   Module         domain = null;
    private   Module         codomain = null;
    protected JList          morphismList;
    private   JButton        cancelButton;
    protected JButton        okButton;
    
    protected static final Repository rep = Repository.systemRepository();
}

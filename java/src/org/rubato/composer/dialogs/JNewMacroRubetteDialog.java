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

import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public final class JNewMacroRubetteDialog extends JDialog implements ActionListener {

    public JNewMacroRubetteDialog(Frame frame) {
        super(frame, Messages.getString("JNewMacroRubetteDialog.newmacrorubette"), true); //$NON-NLS-1$
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        namePanel.setBorder(makeTitledBorder(Messages.getString("JNewMacroRubetteDialog.name"))); //$NON-NLS-1$
        nameField = new JTextField();
        namePanel.add(nameField, BorderLayout.CENTER);
        topPanel.add(namePanel);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBorder(makeTitledBorder(Messages.getString("JNewMacroRubetteDialog.info"))); //$NON-NLS-1$
        infoField = new JTextField();
        infoPanel.add(infoField, BorderLayout.CENTER);
        topPanel.add(infoPanel);
        
        JPanel shortDescPanel = new JPanel();
        shortDescPanel.setLayout(new BorderLayout());
        shortDescPanel.setBorder(makeTitledBorder(Messages.getString("JNewMacroRubetteDialog.shortdescription"))); //$NON-NLS-1$
        shortDescField = new JTextField();
        shortDescPanel.add(shortDescField, BorderLayout.CENTER);
        topPanel.add(shortDescPanel);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel longDescPanel = new JPanel();
        longDescPanel.setLayout(new BorderLayout());
        longDescPanel.setBorder(makeTitledBorder(Messages.getString("JNewMacroRubetteDialog.longdescription"))); //$NON-NLS-1$
        longDescArea = new JTextArea();
        longDescArea.setWrapStyleWord(true);
        longDescArea.setLineWrap(true);
        longDescPanel.add(new JScrollPane(longDescArea), BorderLayout.CENTER);
        longDescPanel.setPreferredSize(new Dimension(230, 100));
        add(longDescPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cancelButton = new JButton(Messages.getString("JNewMacroRubetteDialog.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        okButton = new JButton(Messages.getString("JNewMacroRubetteDialog.ok")); //$NON-NLS-1$
        okButton.addActionListener(this);
        buttonPanel.add(okButton);
        bottomPanel.add(buttonPanel);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        pack();
        setAlwaysOnTop(true);
    }
    

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            ok();
        }
        else if (e.getSource() == cancelButton) {
            cancel();
        }
    }
    
    public void ok() {
        setVisible(false);
        ok = true;
    }
    
    
    public void cancel() {
        setVisible(false);
        ok = false;
    }
    
    
    public boolean isOk() {
        return ok;
    }
    
    
    public String getName() {
        return nameField.getText().trim();
    }

    
    public String getInfo() {
        return infoField.getText().trim();
    }

    
    public String getShortDescription() {
        return shortDescField.getText().trim();
    }
    

    public String getLongDescription() {
        return longDescArea.getText().trim();
    }
    

    private JTextField   nameField;
    private JTextField   infoField;
    private JTextField   shortDescField;
    private JTextArea    longDescArea;
    private JButton      okButton;
    private JButton      cancelButton;
    private boolean      ok = false;
}

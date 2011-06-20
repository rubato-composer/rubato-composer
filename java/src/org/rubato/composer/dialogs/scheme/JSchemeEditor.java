/*
 * Copyright (C) 2006 Gérard Milmeister
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

package org.rubato.composer.dialogs.scheme;

import static org.rubato.composer.Utilities.installEscapeKey;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import org.rubato.base.Repository;

public class JSchemeEditor
        extends JDialog
        implements ActionListener, KeyListener, Observer {

    public JSchemeEditor(Frame frame) {
        super(frame, Messages.getString("JSchemeEditor.schemeeditor"), false); //$NON-NLS-1$
        setLayout(new BorderLayout());

        rep = Repository.systemRepository();
        rep.addObserver(this);
        
        textFont = Font.decode("monospaced"); //$NON-NLS-1$
        
        codeArea = new JTextArea(30, 80);
        codeArea.setFont(textFont);
        codeArea.addKeyListener(this);
        codeArea.setText(rep.getSchemeCode());
        add(new JScrollPane(codeArea), BorderLayout.CENTER);
        
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        
        errorArea = new JTextArea(1, 0);
        errorArea.setForeground(Color.RED);
        errorArea.setFocusable(false);
        errorArea.setEditable(false);
        bottomPanel.add(new JScrollPane(errorArea), BorderLayout.NORTH);        
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        
        buttonBox.add(Box.createHorizontalGlue());

        saveButton = new JButton(Messages.getString("JSchemeEditor.savebutton")); //$NON-NLS-1$
        saveButton.setToolTipText(Messages.getString("JSchemeEditor.savetooltip")); //$NON-NLS-1$
        saveButton.addActionListener(this);
        buttonBox.add(saveButton);

        buttonBox.add(Box.createHorizontalGlue());
        
        bottomPanel.add(buttonBox);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        installEscapeKey(this);

        pack();
    }
    

    public void keyTyped(KeyEvent e) {}


    public void keyPressed(KeyEvent e) {}

    
    public void keyReleased(KeyEvent e) {}

    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == saveButton) {
            errorArea.setText(" "); //$NON-NLS-1$
            String error = rep.setSchemeCode(codeArea.getText());
            if (error != null) {
                errorArea.setForeground(Color.RED);
                errorArea.setText(error);
            }
            else {
                errorArea.setForeground(Color.GREEN);
                errorArea.setText(Messages.getString("JSchemeEditor.savedmsg")); //$NON-NLS-1$
            }
        }
    }

    
    public void update(Observable o, Object arg) {}

    
    private JTextArea  codeArea;
    private JTextArea  errorArea;
    private JPanel     bottomPanel;
    private JButton    saveButton;
    private Repository rep;
    
    private Font textFont;
}

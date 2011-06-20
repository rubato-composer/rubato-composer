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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class JProgress extends JDialog {

    public JProgress(Frame frame, Component comp) {
        super(frame, "Running ...", false); //$NON-NLS-1$
        createLayout();
        setLocationRelativeTo(comp);
    }
    
    public void makeProgress(int value) {
        progressBar.setValue(value);
    }
    
    
    public void addMessage(String msg) {
        messageListModel.add(msg);
        messageList.ensureIndexIsVisible(messageListModel.getSize()-1);
    }
    
    
    public void reset(int max) {
        progressBar.setMinimum(0);
        progressBar.setMaximum(max);
        messageListModel.clear();
    }

    
    private void createLayout() {
        setLayout(new BorderLayout());
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progressBar.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(progressBar, BorderLayout.NORTH);
        messageListModel = new MessageListModel();
        messageList = new JList(messageListModel);
        JPanel pbPanel = new JPanel();
        pbPanel.setLayout(new BorderLayout());
        pbPanel.setBorder(new EmptyBorder(0, 20, 10, 20));
        pbPanel.add(new JScrollPane(messageList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                    BorderLayout.CENTER);
        add(pbPanel, BorderLayout.CENTER);
        pack();
    }
    
    
    protected class MessageListModel extends DefaultListModel {

        public void clear() {
            removeAllElements();
        }
        
        public void add(String msg) {
            addElement(msg);
        }
    }
    
    private JProgressBar     progressBar;
    private JList            messageList;
    private MessageListModel messageListModel;
}

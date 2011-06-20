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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class JMessageLog extends JPanel {

    public JMessageLog() {
        setLayout(new BorderLayout());
        messageListModel = new MessageListModel(this);
        messageList = new JList(messageListModel);
        messageList.setCellRenderer(new MessageCellRenderer());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        clearButton = new JButton(Messages.getString("JMessageLog.clear")); //$NON-NLS-1$
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearMessages();
            }
        });
        buttonPanel.add(clearButton);
        
        ActionListener checkListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkAction();
            }
        };
        infoCheck = new JCheckBox(Messages.getString("JMessageLog.info")); //$NON-NLS-1$
        infoCheck.addActionListener(checkListener);
        infoCheck.setSelected(true);
        buttonPanel.add(infoCheck);
        warningCheck = new JCheckBox(Messages.getString("JMessageLog.warnings")); //$NON-NLS-1$
        warningCheck.addActionListener(checkListener);
        warningCheck.setSelected(true);
        buttonPanel.add(warningCheck);
        errorCheck = new JCheckBox(Messages.getString("JMessageLog.errors")); //$NON-NLS-1$
        errorCheck.addActionListener(checkListener);        
        errorCheck.setSelected(true);
        buttonPanel.add(errorCheck);
        
        add(buttonPanel, BorderLayout.EAST);
    }
 
    
    public void addMessage(String s, int type) {
        Message msg = new Message(s, type);
        messageListModel.addMessage(msg);
        messageList.ensureIndexIsVisible(messageListModel.getSize()-1);
    }
    
    
    public void clearMessages() {
        messageListModel.clearMessages();        
    }
    
    
    public void checkAction() {
        checks[JComposer.STATUS_ERROR] = errorCheck.isSelected();
        checks[JComposer.STATUS_WARNING] = warningCheck.isSelected();
        checks[JComposer.STATUS_INFO] = infoCheck.isSelected();
        messageListModel.refresh();
        messageList.ensureIndexIsVisible(messageListModel.getSize()-1);
    }
    
    
    private JList            messageList;
    private MessageListModel messageListModel;
    private JButton          clearButton;
    private JCheckBox        errorCheck;
    private JCheckBox        warningCheck;
    private JCheckBox        infoCheck;
    protected boolean[]        checks = { true, true, true };
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy/HH:mm:ss"); //$NON-NLS-1$
    
    
    private class MessageCellRenderer
        extends JLabel
        implements ListCellRenderer {

        public MessageCellRenderer() {
            setOpaque(true);
        }
        
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            Message msg = (Message)value;
            Color fg = JComposer.statusColor[msg.type];
            setText(dateFormat.format(msg.date)+" - "+msg.msg); //$NON-NLS-1$
            setBackground(isSelected ? fg: Color.WHITE);
            setForeground(isSelected ? Color.WHITE: fg);
            return this;
        }        

        private static final long serialVersionUID = 1865583250979794976L;
    }
    
    
    private class MessageListModel extends DefaultListModel {
        
        public MessageListModel(JMessageLog messageLog) {
            super();
            this.messageLog = messageLog;
        }
        
        public void addMessage(Message msg) {
            messages.add(msg);
            refresh();
        }
        
        public void refresh() {
            removeAllElements();
            for (Message msg : messages) {
                if (messageLog.checks[msg.type]) {
                    addElement(msg);                    
                }
            }
        }
        
        public void clearMessages() {
            messages.clear();
            refresh();
        }
        
        private ArrayList<Message> messages = new ArrayList<Message>();
        private JMessageLog messageLog;

        private static final long serialVersionUID = -7342021455381465541L;
    }
    
    
    private class Message {
        
        public Message(String msg, int type) {
            date = new Date();
            this.msg = msg;
            this.type = type;
        }
        
        public String toString() {
            return date+": "+msg; //$NON-NLS-1$
        }
        
        public Date   date;
        public String msg;
        public int    type;
    }

    
    private static final long serialVersionUID = 7340698071774964726L;
}

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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.JTextField;
import javax.swing.event.EventListenerList;


public class JBooleanLabelCheckbox extends JTextField {

    public JBooleanLabelCheckbox() {
        this(null, STANDARD_WIDTH);
    }

    
    public JBooleanLabelCheckbox(String text, int width) {
        Color bgColor = getBackground();
        this.width = width; 
        setEditable(false);
        setText(text);
        Dimension size = new Dimension(width, 0);
        setPreferredSize(size);
        setMinimumSize(size);
        setBackground(bgColor);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fireActionEvent();
            }
        });
    }
    
    
    public void setText(String text) {
        if (text == null || text.length() == 0) {
            clear();
        }
        else {
            super.setText(text);
        }
    }
    
    
    public String getText() {
        return super.getText().trim();
    }
    
    
    public void clear() {
        super.setText(""); //$NON-NLS-1$
    }
    
    
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        return new Dimension(width, dim.height);
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
                    actionEvent = new ActionEvent(this, 0, ""); //$NON-NLS-1$
                }
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }
    
    private ActionEvent       actionEvent  = null;
    private EventListenerList listenerList = new EventListenerList();
    private int               width;
    
    private final static int STANDARD_WIDTH = 170; 
}

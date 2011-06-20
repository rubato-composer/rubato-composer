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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.rubato.util.TextUtils;

public class JStatusline extends JTextField {

    public JStatusline() {
        super();
        setEditable(false);
        setForeground(ERROR_COLOR);
        setBorder(BorderFactory.createEmptyBorder());
        setFont(getFont().deriveFont(Font.BOLD));
        timer = new Timer(TIMEOUT, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
    }
    
    
    public void setError(String msg) {
        setForeground(ERROR_COLOR);
        setText(msg);
        timer.restart();
    }
    
    
    public void setError(String msg, Object ... args) {
        setError(TextUtils.replaceStrings(msg, args));
    }
    
    
    public void setWarning(String msg) {
        setForeground(WARNING_COLOR);
        setText(msg);
        timer.restart();
    }
    
    
    public void setWarning(String msg, Object ... args) {
        setWarning(TextUtils.replaceStrings(msg, args));
    }
    
    
    public void setInfo(String msg) {
        setForeground(INFO_COLOR);
        setText(msg);
        timer.restart();
    }
    
    
    public void setInfo(String msg, Object ... args) {
        setInfo(TextUtils.replaceStrings(msg, args));
    }
    
    
    public void clear() {
        setText(""); //$NON-NLS-1$
        timer.stop();
    }    


    private final static Color ERROR_COLOR   = Color.RED;
    private final static Color WARNING_COLOR = Color.ORANGE;
    private final static Color INFO_COLOR    = Color.GREEN;
    
    private final static int TIMEOUT = 5000;
    
    private Timer timer;
}

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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JIntegerSpinner
        extends JSpinner
        implements ChangeListener, MouseWheelListener {

    public JIntegerSpinner() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    
    public JIntegerSpinner(int min, int max) {
        super();
        this.min = min;
        this.max = max;
        addChangeListener(this);
        addMouseWheelListener(this);
    }
    
    
    public int getInteger() {
        return ((Integer)getValue()).intValue();
    }
    
    
    public void stateChanged(ChangeEvent e) {
        int i = getInteger();
        if (i < min) {
            i = min;
        }
        if (i > max) {
            i = max;
        }
        setValue(i);
    }
    
    public void mouseWheelMoved(MouseWheelEvent e) {
        int i = getInteger();
        if (e.getWheelRotation() < 0) {
            i++;
        }
        else if (e.getWheelRotation() > 0) {
            i--;
        }
        if (i < min) {
            i = min;
        }
        if (i > max) {
            i = max;
        }
        setValue(i);
    }

    
    private int min;
    private int max;
}

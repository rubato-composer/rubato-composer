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

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JConnectorSliders extends JPanel implements ChangeListener {
    
    public JConnectorSliders(boolean in, boolean out) {
        int n = 0;
        if (in) n++;
        if (out) n++;
        setLayout(new GridLayout(n, 1, 5, 5));
        if (in) {
            inSlider = new JSlider();
            inBorder = new TitledBorder(emptyBorder, Messages.getString("JConnectorSliders.numberInputs")); //$NON-NLS-1$
            inSlider.setBorder(inBorder);
            inSlider.setPaintTicks(true);
            inSlider.setMajorTickSpacing(1);
            inSlider.setSnapToTicks(true);
            inSlider.addChangeListener(this);
            setInLimits(0, 8);
            add(inSlider);
        }
        if (out) {
            outSlider = new JSlider();
            outBorder = new TitledBorder(emptyBorder, Messages.getString("JConnectorSliders.numberOutputs")); //$NON-NLS-1$
            outSlider.setBorder(outBorder);
            outSlider.setPaintTicks(true);
            outSlider.setMajorTickSpacing(1);
            outSlider.setSnapToTicks(true);
            outSlider.addChangeListener(this);
            setOutLimits(0, 8);
            add(outSlider);
        }
    }

    
    public void addChangeListener(ChangeListener c) {
        if (inSlider != null) {
            inSlider.addChangeListener(c);
        }
        if (outSlider != null) {
            outSlider.addChangeListener(c);
        }
    }
    
    
    public void setInLimits(int min, int max) {
        inSlider.setMinimum(min);
        inSlider.setMaximum(max);
    }
    
    
    public void setOutLimits(int min, int max) {
        outSlider.setMinimum(min);
        outSlider.setMaximum(max);
    }
    
    
    public void setInValue(int n) {
        if (n < inSlider.getMinimum()) {
            n = inSlider.getMinimum();
        }
        if (n > inSlider.getMaximum()) {
            n = inSlider.getMaximum();
        }
        inSlider.setValue(n);
        inBorder.setTitle(Messages.getString("JConnectorSliders.numberInputs")+": "+n); //$NON-NLS-1$ //$NON-NLS-2$
    }    

    
    public int getInValue() {
        return inSlider.getValue();
    }
    
    
    public void setOutValue(int n) {
        if (n < outSlider.getMinimum()) {
            n = outSlider.getMinimum();
        }
        if (n > outSlider.getMaximum()) {
            n = outSlider.getMaximum();
        }
        outSlider.setValue(n);
        outBorder.setTitle(Messages.getString("JConnectorSliders.numberOutputs")+": "+n); //$NON-NLS-1$ //$NON-NLS-2$
    }

    
    public int getOutValue() {
        return outSlider.getValue();
    }
    
    
    public void stateChanged(ChangeEvent e) {
        if (inSlider != null) {
            inBorder.setTitle(Messages.getString("JConnectorSliders.numberInputs")+": "+getInValue()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (outSlider != null) {
            outBorder.setTitle(Messages.getString("JConnectorSliders.numberOutputs")+": "+getOutValue()); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    
    private JSlider      inSlider = null;
    private TitledBorder inBorder;
    private JSlider      outSlider = null;
    private TitledBorder outBorder;
    
    private final static Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
}

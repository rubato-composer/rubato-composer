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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.rubato.composer.preferences.UserPreferences;
import org.rubato.math.module.*;

public class JSimpleNumberEntry
        extends JSimpleEntry
        implements CaretListener {

    public JSimpleNumberEntry(Module module) {
        this.module = module;
        int dim = module.getDimension();
        
        inputFields = new JTextField[dim];        
        for (int i = 0; i < dim; i++) {
            inputFields[i] = new JTextField();
        }
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gbl);

        c.ipadx = 10;
        c.anchor = GridBagConstraints.NORTH;
        for (int i = 0; i < dim; i++) {
            c.weightx = 0.0;
            c.fill = GridBagConstraints.NONE;
            c.gridwidth = GridBagConstraints.RELATIVE;
            JLabel label = (dim == 1)?new JLabel(getSymbol(module)+":"):new JLabel("#"+i+" "+getSymbol(module)+":");     //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            gbl.setConstraints(label, c);
            add(label);
            
            c.weightx = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = GridBagConstraints.REMAINDER;
            gbl.setConstraints(inputFields[i], c);
            inputFields[i].addCaretListener(this);
            add(inputFields[i]);
        }
    }
    

    public void caretUpdate(CaretEvent e) {
        fireActionEvent();
    }
    
    public boolean valueIsValid() {
        return false;
    }

    
    public void clear() {
        for (int i = 0; i < inputFields.length; i++) {
            inputFields[i].setText("");  //$NON-NLS-1$
        }
    }
    
    
    public ModuleElement getValue() {
        boolean error = false;
        LinkedList<ModuleElement> list = new LinkedList<ModuleElement>();
        Ring ring = module.getRing();
        for (int i = 0; i < inputFields.length; i++) {
            String s = inputFields[i].getText();
            ModuleElement element = ring.parseString(s);            
            if (element == null) {
                error = true;
                inputFields[i].setBackground(prefs.getEntryErrorColor());
            }
            else {
                inputFields[i].setBackground(Color.WHITE);
                list.add(element);
            }
        }
        return error?null:module.createElement(list);
    }
    
    
    public void setValue(ModuleElement element) {
        for (int i = 0; i < inputFields.length; i++) {
            String s = element.getComponent(i).stringRep();
            inputFields[i].setText(s);
        }
    }

    
    public static String getSymbol(Module module) {
        String s = "";  //$NON-NLS-1$
        if (module instanceof ZFreeModule) {
            s = "Z"; //$NON-NLS-1$
        }
        else if (module instanceof ZnFreeModule) {
            s = "Z"+((ZnRing)module.getRing()).getModulus(); //$NON-NLS-1$
        }
        else if (module instanceof RFreeModule) {
            s = "R"; //$NON-NLS-1$
        }
        else if (module instanceof CFreeModule) {
            s = "C"; //$NON-NLS-1$
        }
        else if (module instanceof QFreeModule) {
            s = "Q"; //$NON-NLS-1$
        }
        else if (module instanceof ZStringRing) {
            s = "Z-String"; //$NON-NLS-1$
        }
        else if (module instanceof ZnStringRing) {
            s = "Z_"+((ZnRing)module.getRing()).getModulus()+"-String"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if (module instanceof RStringRing) {
            s = "R-String"; //$NON-NLS-1$
        }
        else if (module instanceof QStringRing) {
            s = "Q-String"; //$NON-NLS-1$
        }
        else if (module instanceof PolynomialRing) {
            PolynomialRing r = (PolynomialRing)module;
            s = getSymbol(r.getCoefficientRing())+"["+r.getIndeterminate()+"]"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if (module instanceof ModularPolynomialRing) {
            ModularPolynomialRing r = (ModularPolynomialRing)module;
            s = getSymbol(r.getCoefficientRing())+"["+r.getIndeterminate()+"]/("+r.getModulus().stringRep()+")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return s;
    }
    
    
    private JTextField[] inputFields;
    private Module       module = null;
    
    private final static UserPreferences prefs = UserPreferences.getUserPreferences();
}

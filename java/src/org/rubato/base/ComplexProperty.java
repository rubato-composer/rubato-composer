/*
 * Copyright (C) 2007 Gérard Milmeister
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

package org.rubato.base;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.rubato.composer.preferences.UserPreferences;
import org.rubato.math.arith.Complex;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class ComplexProperty
        extends RubetteProperty
        implements ActionListener, CaretListener {

    public ComplexProperty(String key, String name, Complex value) {
        super(key, name);
        this.value = value;
        this.tmpValue = value;
    }
    
    
    public ComplexProperty(ComplexProperty prop) {
        super(prop);
        this.value = prop.value;
        this.tmpValue = prop.tmpValue;
    }
    
    
    public Object getValue() {
        return value;
    }
    
    
    public void setValue(Object value) {
        if (value instanceof Complex) {
            setComplex((Complex)value);
        }
    }
    
    
    public Complex getComplex() {
        return value; 
    }
    
    
    public void setComplex(Complex value) {
        this.value = value;
        this.tmpValue = value;
    }
    

    public JComponent getJComponent() {
        textField = new JTextField();
        textField.setText(getComplex().toString());
        textField.addCaretListener(this);
        textField.addActionListener(this);
        bgColor = textField.getBackground(); 
        return textField;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        update();
    }
    
    
    public void caretUpdate(CaretEvent e) {
        update();
    }
    
    
    public void update() {
        textField.setBackground(bgColor);
        String s = textField.getText();
        try {
            tmpValue = Complex.parseComplex(s);
        }
        catch (NumberFormatException e) { /* do nothing */ }
        textField.setBackground(prefs.getEntryErrorColor());
    }
    
    
    public void apply() {
        setComplex(tmpValue);
    }
    
    
    public void revert() {
        tmpValue = value;
        textField.setText(value.toString());
    }
    
    
    public ComplexProperty clone() {
        return new ComplexProperty(this);
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.empty(getKey(), VALUE_ATTR, value);
    }
    
    
    public RubetteProperty fromXML(XMLReader reader, Element element) {
        ComplexProperty property = clone();
        String s = XMLReader.getStringAttribute(element, VALUE_ATTR);
        try {
            property.setValue(Complex.parseComplex(s));
        }
        catch (NumberFormatException e) {
            property.setValue(value);
        }
        return property;
    }
    
    
    @SuppressWarnings("nls")
    public String toString() {
        return "ComplexProperty["+getOrder()+","+getKey()+","+getName()+","+value+"]";
    }

    
    private Complex value;
    private Complex tmpValue;
    private JTextField textField = null;
    
    private Color bgColor = null;
    private static final UserPreferences prefs = UserPreferences.getUserPreferences();
}

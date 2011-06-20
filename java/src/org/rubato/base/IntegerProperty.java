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
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class IntegerProperty
        extends RubetteProperty
        implements ActionListener, CaretListener {

    public IntegerProperty(String key, String name, int value, int min, int max) {
        super(key, name);
        this.value = value;
        this.tmpValue = value;
        if (min > max) {
            int t = min;
            min = max;
            max = t;
        }
        this.min = min;
        this.max = max;
    }
    
    
    public IntegerProperty(String key, String name, int value) {
        this(key, name, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    
    public IntegerProperty(IntegerProperty prop) {
        super(prop);
        this.value = prop.value;
        this.tmpValue = prop.tmpValue;
        this.min = prop.min;
        this.max = prop.max;
    }
    
    
    public Object getValue() {
        return value;
    }
    
    
    public void setValue(Object value) {
        if (value instanceof Integer) {
            setInt((Integer)value);
        }
    }
    
    
    public int getInt() {
        return value; 
    }
    
    
    public void setInt(int value) {
        if (value < min) {
            value = min;
        }
        else if (value > max) {
            value = max;
        }
        this.value = value;
        this.tmpValue = value;
    }
    
    
    public JComponent getJComponent() {
        textField = new JTextField();
        textField.setText(Integer.toString(getInt()));
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
            int i = Integer.parseInt(s);
            if (i >= min && i <= max) {
                tmpValue = i;
                return;
            }
        }
        catch (NumberFormatException e) { /* do nothing */ }
        textField.setBackground(prefs.getEntryErrorColor());
    }
    
    
    public void apply() {
        setInt(tmpValue);
    }
    
    
    public void revert() {
        tmpValue = value;
        textField.setText(Integer.toString(value));
    }
    
    
    public IntegerProperty clone() {
        return new IntegerProperty(this);
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.empty(getKey(), VALUE_ATTR, value);
    }
    
    
    public RubetteProperty fromXML(XMLReader reader, Element element) {
        IntegerProperty property = clone();
        property.setValue(XMLReader.getIntAttribute(element, VALUE_ATTR, min, max, value));
        return property;
    }

    
    @SuppressWarnings("nls")
    public String toString() {
        return "IntegerProperty["+getOrder()+","+getKey()+","+getName()+","+value+","+min+","+max+"]";
    }

    
    private int value;
    private int min;
    private int max;
    private int tmpValue;
    private JTextField textField = null;
    
    private Color bgColor = null;
    private static final UserPreferences prefs = UserPreferences.getUserPreferences();
}

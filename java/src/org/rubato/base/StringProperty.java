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

public class StringProperty
        extends RubetteProperty
        implements ActionListener, CaretListener {

    public StringProperty(String key, String name, String value, int min, int max) {
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
    
    
    public StringProperty(String key, String name, String value, int min) {
        this(key, name, value, min, Integer.MAX_VALUE);
    }
    
    
    public StringProperty(String key, String name, String value) {
        this(key, name, value, 0);
    }
    
    
    public StringProperty(StringProperty prop) {
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
        if (value instanceof String) {
            setString((String)value);
        }
    }
    
    
    public String getString() {
        return value; 
    }
    
    
    public void setString(String value) {
        if (value.length() < min) {
            value = fillStringToLength(value, min);
        }
        else if (value.length() > max) {
            value = value.substring(0, max);
        }
        this.value = value;
        this.tmpValue = value;
    }

    
    private String fillStringToLength(String val, int minLength) {
        String res = val;
        for (int i = val.length(); i < minLength; i++) {
            res += "X"; //$NON-NLS-1$
        }
        return res;
    }
    
    
    public JComponent getJComponent() {
        textField = new JTextField();
        textField.setText(value);
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
        if (s.length() >= min && s.length() <= max) {
            tmpValue = s;
            return;
        }
        textField.setBackground(prefs.getEntryErrorColor());
    }
    
    
    public void apply() {
        setString(tmpValue);
    }
    
    
    public void revert() {
        tmpValue = value;
        textField.setText(value);
    }
    
    
    public StringProperty clone() {
        return new StringProperty(this);
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.empty(getKey(), VALUE_ATTR, value);
    }
    
    
    public RubetteProperty fromXML(XMLReader reader, Element element) {
        StringProperty property = clone();
        property.setValue(XMLReader.getStringAttribute(element, VALUE_ATTR));
        return property;
    }

    
    @SuppressWarnings("nls")
    public String toString() {
        return "StringProperty["+getOrder()+","+getKey()+","+getName()+","+value+","+min+","+max+"]";
    }

    
    private String value;
    private int min;
    private int max;
    private String tmpValue;
    private JTextField textField = null;
    
    private Color bgColor = null;
    private static final UserPreferences prefs = UserPreferences.getUserPreferences();
}

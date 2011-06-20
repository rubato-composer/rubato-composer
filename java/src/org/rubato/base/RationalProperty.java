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
import org.rubato.math.arith.Rational;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class RationalProperty
        extends RubetteProperty
        implements ActionListener, CaretListener {

    public RationalProperty(String key, String name, Rational value, Rational min, Rational max) {
        super(key, name);
        this.value = value;
        this.tmpValue = value;
        if (min.compareTo(max) > 0) {
            Rational t = min;
            min = max;
            max = t;
        }
        this.min = min;
        this.max = max;
    }
    
    
    public RationalProperty(String key, String name, Rational value) {
        this(key, name, value, new Rational(Integer.MIN_VALUE, 1), new Rational(Integer.MAX_VALUE, 1));
    }
    
    
    public RationalProperty(RationalProperty prop) {
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
        if (value instanceof Rational) {
            setRational((Rational)value);
        }
    }
    
    
    public Rational getRational() {
        return value; 
    }
    
    
    public void setRational(Rational value) {
        if (value.compareTo(min) < 0) {
            value = min;
        }
        else if (value.compareTo(max) > 0) {
            value = max;
        }
        this.value = value;
        this.tmpValue = value;
    }
    

    public JComponent getJComponent() {
        textField = new JTextField();
        textField.setText(getRational().toString());
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
            Rational d = Rational.parseRational(s);
            if (d.compareTo(min) >= 0 && d.compareTo(max) <= 0) {
                tmpValue = d;
                return;
            }
        }
        catch (NumberFormatException e) {}
        textField.setBackground(prefs.getEntryErrorColor());
    }
    
    
    public void apply() {
        setRational(tmpValue);
    }
    
    
    public void revert() {
        tmpValue = value;
        textField.setText(value.toString());
    }
    
    
    public RationalProperty clone() {
        return new RationalProperty(this);
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.empty(getKey(), VALUE_ATTR, value);
    }
    
    
    public RubetteProperty fromXML(XMLReader reader, Element element) {
        RationalProperty property = clone();
        String s = XMLReader.getStringAttribute(element, VALUE_ATTR);
        try {
            property.setValue(Rational.parseRational(s));
        }
        catch (NumberFormatException e) {
            property.setValue(value);
        }
        return property;
    }
    
    
    @SuppressWarnings("nls")
    public String toString() {
        return "RationalProperty["+getOrder()+","+getKey()+","+getName()+","+value+","+min+","+max+"]";
    }

    
    private Rational value;
    private Rational min;
    private Rational max;
    private Rational tmpValue;
    private JTextField textField = null;
    
    private Color bgColor = null;
    private static final UserPreferences prefs = UserPreferences.getUserPreferences();
}

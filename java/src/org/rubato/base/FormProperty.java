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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import org.rubato.composer.components.JSelectForm;
import org.rubato.math.yoneda.Form;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class FormProperty
        extends RubetteProperty
        implements ActionListener {

    public FormProperty(String key, String name, Form value) {
        super(key, name);
        this.value = value;
    }
    
    
    public FormProperty(String key, String name) {
        this(key, name, null);
    }
    
    
    public FormProperty(FormProperty prop) {
        super(prop);
        this.value = prop.value;
    }
    
    
    public Object getValue() {
        return value;
    }
    
    
    public void setValue(Object value) {
        if (value instanceof Form) {
            setForm((Form)value);
        }
    }
    
    
    public Form getForm() {
        return value; 
    }
    
    
    public void setForm(Form value) {
        this.value = value;
        this.tmpValue = value;
    }
    
    
    public JComponent getJComponent() {
        selectForm = new JSelectForm(rep);
        selectForm.disableBorder();
        selectForm.addActionListener(this);
        selectForm.setForm(value);
        return selectForm;
    }

    
    public void actionPerformed(ActionEvent e) {
        tmpValue = selectForm.getForm();
    }
    
    
    public void apply() {
        setForm(tmpValue);
    }
    
    
    public void revert() {
        tmpValue = value;
        selectForm.setForm(tmpValue);
    }
    
    
    public FormProperty clone() {
        return new FormProperty(this);
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlock(getKey());
        if (value != null) {
            writer.writeFormRef(value);
        }
        writer.closeBlock();
    }
    
    
    public RubetteProperty fromXML(XMLReader reader, Element element) {
        FormProperty property = clone();
        Element child = XMLReader.getChild(element, "Form"); //$NON-NLS-1$
        if (child == null) {
            property.setForm(null);
        }
        else {
            Form f = reader.parseAndResolveForm(child);
            property.setForm(f);
        }
        return property;
    }

    
    @SuppressWarnings("nls")
    public String toString() {
        return "FormProperty["+getOrder()+","+getKey()+","+getName()+","+value+"]";
    }

    
    private Form value;
    private Form tmpValue;
    private JSelectForm selectForm;
    
    private static Repository rep = Repository.systemRepository();
}

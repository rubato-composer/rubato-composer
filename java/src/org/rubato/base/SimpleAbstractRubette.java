/*
 * Copyright (C) 2006 Gérard Milmeister
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

import javax.swing.JComponent;

import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public abstract class SimpleAbstractRubette extends AbstractRubette {

    /**
     * Returns the rubette properties of this rubette.
     */
    public final RubetteProperties getRubetteProperties() {
        if (properties == null) {
            properties = new RubetteProperties();
        }
        return properties;
    }


    /**
     * Sets a new property.
     */
    public void putProperty(RubetteProperty prop) {
        getRubetteProperties().put(prop);
    }
    

    /**
     * Returns the property of the given <code>key</code>.
     * Returns null, iff no property with this key exists.
     */
    public RubetteProperty getProperty(String key) {
        return getRubetteProperties().get(key);
    }

    
    public boolean hasProperties() {
        return properties != null;
    }

    
    public JComponent getProperties() {
        if (dialog == null) {
            dialog = new JRubettePropertiesDialog(properties);
        }
        return dialog;
    }

    
    public boolean applyProperties() {
        if (properties != null) {
            properties.apply();
        }
        return true;
    }
    
    
    public void revertProperties() {
        if (properties != null) {
            properties.revert();
        }
    }

    
    public Rubette duplicate() {
        SimpleAbstractRubette rubette = (SimpleAbstractRubette)newInstance();
        rubette.properties = properties.clone();
        return rubette;
    }
    
    
    public final Rubette fromXML(XMLReader reader, Element element) {
        RubetteProperties newProp = null;
        if (properties != null) {
             newProp = properties.fromXML(reader, element);
        }
        SimpleAbstractRubette rubette = (SimpleAbstractRubette)newInstance();
        rubette.properties = newProp;
        return rubette;
    }

    
    public final void toXML(XMLWriter writer) {
        if (properties != null) {
            properties.toXML(writer);
        }
    }
    
    
    private RubetteProperties properties = null;
    private JRubettePropertiesDialog dialog = null;
}

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RubetteProperties implements Cloneable {

    public RubetteProperties() {
        properties = new HashMap<String,RubetteProperty>();
    }
    
    
    public void put(RubetteProperty prop) {
        properties.put(prop.getKey(), prop);
    }
    
    
    public RubetteProperty get(String key) {
        return properties.get(key);
    }
    
    
    public Collection<RubetteProperty> getProperties() {
        return properties.values();
    }
    
    
    public void apply() {
        for (RubetteProperty prop : properties.values()) {
            prop.apply();
        }
    }
    
    
    public void revert() {
        for (RubetteProperty prop : properties.values()) {
            prop.revert();
        }
    }
    
    
    public void toXML(XMLWriter writer) {
        for (RubetteProperty prop : properties.values()) {
            prop.toXML(writer);
        }
    }
    
    
    public RubetteProperties fromXML(XMLReader reader, Element element) {        
        RubetteProperties newProp = clone();
        Node node = element.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)node;
                String key = e.getTagName();
                RubetteProperty property = newProp.get(key);
                if (property != null) {
                    property = property.fromXML(reader, e);
                    newProp.put(property);
                }
            }
            node = node.getNextSibling();
        }
        return newProp;
    }
    
    
    public RubetteProperties clone() {
        RubetteProperties newProp = new RubetteProperties();
        for (Entry<String,RubetteProperty> entry : properties.entrySet()) {
            newProp.properties.put(entry.getKey(), entry.getValue().clone());
        }
        return newProp;
    }
    

    @SuppressWarnings("nls")
    public String toString() {
        String s;
        s = "------------------------------------------------\n";
        for (RubetteProperty prop : properties.values()) {
            s += prop+"\n";
        }
        s += "------------------------------------------------\n";
        return s;
    }

    
    private HashMap<String,RubetteProperty> properties;
}

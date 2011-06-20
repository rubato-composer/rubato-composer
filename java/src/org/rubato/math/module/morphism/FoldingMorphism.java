/*
 * Copyright (C) 2001, 2005 Gérard Milmeister
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

package org.rubato.math.module.morphism;

import static org.rubato.xml.XMLConstants.MODULEELEMENT;
import static org.rubato.xml.XMLConstants.MODULEMORPHISM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.HashMap;
import java.util.LinkedList;

import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.xml.XMLInputOutput;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism that represents the folding of a set of ModuleElements.
 * 
 * @author Gérard Milmeister
 */
public final class FoldingMorphism extends ModuleMorphism {

    /**
     * Create a morphism that maps each of <code>elements</code> to its fold number.
     */
    public FoldingMorphism(ModuleElement[] elements) {
        super(elements[0].getModule(), RRing.ring);
        this.elements = elements;
        double[] f = elements[0].fold(elements);
        values = new HashMap<ModuleElement,RElement>();
        for (int i = 0; i < f.length; i++) {
            values.put(elements[i], new RElement(f[i]));
        }
    }

    
    public ModuleElement map(ModuleElement element) {
        return values.get(element);
    }
    

    public double mapValue(ModuleElement element) {
        RElement res = (RElement)map(element);
        if (res != null) {
            return res.getValue();
        }
        else {
            return 0;
        }
    }

    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(getDomain().getRing());
    }

    
    public int compareTo(ModuleMorphism object) {
        if (object instanceof FoldingMorphism) {
            FoldingMorphism morphism = (FoldingMorphism)object;
            int hc1 = values.hashCode();
            int hc2 = morphism.values.hashCode();
            return hc1-hc2;
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean equals(Object object) {
        if (object instanceof DifferenceMorphism) {
            FoldingMorphism morphism = (FoldingMorphism)object;
            return values.equals(morphism.values);
        }
        else {
            return false;
        }
    }
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("FoldingMorphism[");
        if (elements.length > 0) {
            buf.append(elements[0].toString());
            for (int i = 1; i < elements.length; i++) {
                buf.append(",");
                buf.append(elements[i]);
            }
        }
        buf.append("]");
        return buf.toString();
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULEMORPHISM, getElementTypeName());
        for (int i = 0; i < elements.length; i++) {
            elements[i].toXML(writer);
        }
        writer.closeBlock();
    }
    
    
    public ModuleMorphism fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULEELEMENT);
        if (childElement != null) {
            LinkedList<ModuleElement> elements0 = new LinkedList<ModuleElement>();
            ModuleElement moduleElement = reader.parseModuleElement(childElement);
            if (moduleElement == null) {
                return null;
            }
            elements0.add(moduleElement);
            Element next = XMLReader.getNextSibling(childElement, MODULEELEMENT);
            while (next != null) {
                moduleElement = reader.parseModuleElement(next);
                if (moduleElement == null) {
                    return null;
                }
                elements0.add(moduleElement);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            ModuleElement[] newElements = new ModuleElement[elements0.size()];
            int i = 0;
            for (ModuleElement e : elements0) { 
                newElements[i++] = e;
            }
            ModuleMorphism result = new FoldingMorphism(newElements);
            return result;
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }
    
    
    private static final XMLInputOutput<ModuleMorphism> xmlIO =
        new FoldingMorphism(new RElement[] { new RElement(0), new RElement(0) });

    public static XMLInputOutput<ModuleMorphism> getXMLInputOutput() {
        return xmlIO;
    }
    

    public String getElementTypeName() {
        return "FoldingMorphism";
    }
    
    
    private HashMap<ModuleElement,RElement> values;
    private ModuleElement[] elements;
}

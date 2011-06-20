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

package org.rubato.math.module;

import static org.rubato.xml.XMLConstants.MODULE;
import static org.rubato.xml.XMLConstants.MODULEELEMENT;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.TranslationMorphism;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Module with arbitrary modules as components.
 * @see org.rubato.math.module.DirectSumElement
 * 
 * @author Gérard Milmeister
 */
public final class DirectSumModule implements Module {

    /**
     * Creates a direct sum with the given <code>components</code>.
     * All components must be modules of the same ring.
     */
    public static DirectSumModule make(Module ... components) {
        if (components.length < 1) {
            throw new IllegalArgumentException("There must be at least one component module.");
        }
        else if (!checkComponents(components)) {
            throw new IllegalArgumentException("Component modules must all have the same ring.");            
        }
        else {
            return new DirectSumModule(components);
        }
    }
    
    
    /**
     * Creates a direct sum with no components of the given <code>ring</code>.
     */
    public static DirectSumModule makeNullModule(Ring ring) {
        return new DirectSumModule(ring);
    }
    

    public DirectSumModule getNullModule() {
        return makeNullModule(getRing());
    }
    
    
    public boolean isNullModule() {
        return components == null;
    }    
    
    
    public boolean isRing() {
        return false;
    }
    
    
    public Ring getRing() {
        return ring;
    }
    
    
    public DirectSumElement getZero() {
        if (getDimension() == 0) {
            return DirectSumElement.makeNullElement(getRing());
        }
        ModuleElement[] newComponents = new ModuleElement[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            newComponents[i] = getComponentModule(i).getZero();
        }
        return DirectSumElement.make(newComponents);
    }
    

    public ModuleMorphism getIdentityMorphism() {
        return ModuleMorphism.getIdentityMorphism(this);
    }
    

    public int getDimension() {
        return components.length;
    }
    

    public Module getComponentModule(int i) {
        return components[i];
    }
    

    public boolean hasElement(ModuleElement element) {
        if (element instanceof DirectSumElement) {
            if (element.getLength() == getDimension()) {
                for (int i = 0; i < getDimension(); i++) {
                    if (!getComponentModule(i).hasElement(element.getComponent(i))) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    

    public DirectSumElement createElement(List<ModuleElement> elements) {        	
	   if (elements.size() < getDimension()) {
	       return null;
	   }

       if (getDimension() == 0) {
           return DirectSumElement.makeNullElement(getRing());
       }
       
       ModuleElement[] values = new ModuleElement[getDimension()];        
	   int i = 0;
	
       for (ModuleElement e : elements) {
           values[i] = getComponentModule(i).cast(e);
           if (values[i] == null) {
               return null;
           }
	       i++;
	   }
	
	   return DirectSumElement.make(values);
    }

    
    public DirectSumElement fill(List<ModuleElement> elements) {
        ModuleElement[] newElements = new ModuleElement[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            Module component = getComponentModule(i);
            if (component instanceof DirectSumModule) {
                newElements[i] = ((DirectSumModule)component).fill(elements);
                if (newElements[i] == null) {
                    return null;
                }
            }
            else {
                LinkedList<ModuleElement> moduleElements = new LinkedList<ModuleElement>();
                for (int j = 0; j < component.getDimension(); j++) {
                    if (elements.size() > 0) {
                        moduleElements.add(elements.remove(0));
                    }
                    else {
                        return null;
                    }
                }
                newElements[i] = component.createElement(moduleElements);
                if (newElements[i] == null) {
                    return null;
                }
            }
        }
        return DirectSumElement.make(newElements);
    }

    
    public DirectSumElement cast(ModuleElement element) {
        if (element instanceof DirectSumElement) {
            return (DirectSumElement)element.cast(this);
        }
        else {
            LinkedList<ModuleElement> elements = new LinkedList<ModuleElement>();
            for (int i = 0; i < element.getLength(); i++) {
                elements.add(element.getComponent(i));
            }
            return fill(elements);
        }
    }


    public ModuleMorphism getTranslation(ModuleElement element) {
        return TranslationMorphism.make(this, element);
    }


    public int compareTo(Module object) {
        if (object instanceof DirectSumModule) {
            DirectSumModule module = (DirectSumModule)object; 
            int d = getDimension()-module.getDimension();
            if (d != 0) {
                return d;
            }
            else {
                for (int i = 0; i < getDimension(); i++) {
                    int c = getComponentModule(i).compareTo(module.getComponentModule(i));
                    if (c != 0) {
                        return c;
                    }
                }
                return 0;
            }
        }
        else {
            return toString().compareTo(object.toString());
        }
    }

    
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof DirectSumModule) {
            DirectSumModule module = (DirectSumModule)object;
            for (int i = 0; i < module.getDimension(); i++) {
                if (!module.getComponentModule(i).equals(getComponentModule(i))) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    
    public DirectSumElement parseString(String string) {
        ArrayList<String> m = parse(TextUtils.unparenthesize(string));
        if (m.size() != getDimension()) {
            return null;
        }
        
        ModuleElement[] comps = new ModuleElement[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            ModuleElement element = getComponentModule(i).parseString(m.get(i));
            if (element == null) {
                return null;
            }
            comps[i] = element;
        }
        return DirectSumElement.make(comps); 
    }
    
    
    private ArrayList<String> parse(String s) {
        int pos = 0;
        int lastpos = 0;
        int level = 0;
        ArrayList<String> m = new ArrayList<String>();
        while (pos < s.length()) {
            if (s.charAt(pos) == '(') {
                pos++;
                level++;
            }
            else if (s.charAt(pos) == ')') {
                pos++;
                level--;
            }
            else if (s.charAt(pos) == ',' && level == 0) {
                m.add(s.substring(lastpos, pos));                
                pos++;
                lastpos = pos;
            }
            else {
                pos++;
            }
        }
        m.add(s.substring(lastpos,pos));
        return m;
    }
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder(50);
        buf.append("DirectSumModule[");
        buf.append(getDimension());
        buf.append(",");
        buf.append(getRing());
        buf.append("][");
        if (getDimension() > 0) {
            buf.append(components[0]);
            for (int i = 1; i < getDimension(); i++) {
                buf.append(",");
                buf.append(components[i]);
            }
        }
        buf.append("]");
        return buf.toString();
    }

    
    public String toVisualString() {
        StringBuilder buf = new StringBuilder(30);
        buf.append("DirectSumModule[");
        buf.append(getDimension());
        buf.append(",");
        buf.append(getRing());
        buf.append("][");
        if (getDimension() > 0) {
            buf.append(components[0].toVisualString());
            for (int i = 1; i < getDimension(); i++) {
                buf.append(",");
                buf.append(components[i].toVisualString());
            }
        }
        buf.append("]");
        return buf.toString();
    }
    

    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MODULE, getElementTypeName());
        for (int i = 0; i < components.length; i++) {
            components[i].toXML(writer);
        }
        writer.closeBlock();
    }
    
    
    public Module fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(getElementTypeName()));
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement != null) {
            LinkedList<Module> elements = new LinkedList<Module>();
            Module module = reader.parseModule(childElement);
            if (module == null) {
                return null;
            }
            elements.add(module);
            Element next = XMLReader.getNextSibling(childElement, MODULE);
            while (next != null) {
                module = reader.parseModule(next);
                if (module == null) {
                    return null;
                }
                elements.add(module);
                next = XMLReader.getNextSibling(next, MODULEELEMENT);
            }
            Module[] coefficients = new Module[elements.size()];
            int i = 0;
            for (Module m : elements) {
                coefficients[i++] = m;
            }
            try {
                return new DirectSumModule(coefficients);
            }
            catch (IllegalArgumentException e) {
                reader.setError(e.getMessage());
                return null;
            }
        }
        else {
            reader.setError("Type %%1 is missing children of type <%2>.", getElementTypeName(), MODULEELEMENT);
            return null;
        }
    }

    
    public String getElementTypeName() {
        return "DirectSumModule";
    }

    
    public int hashCode() {
        int hash = 37*basicHash+getDimension();
        hash = 37*hash + getRing().hashCode();
        for (int i = 0; i < getDimension(); i++) {
            hash = 37*hash + components[i].hashCode();
        }
        return hash;
    }

    
    private DirectSumModule(Module[] components) {
        this.ring = components[0].getRing();
        this.components = components;
    }

    
    private DirectSumModule(Ring ring) {
        this.ring = ring;
        this.components = null;
    }

    
    private static boolean checkComponents(Module[] components) {
        Ring ring = components[0].getRing();
        for (int i = 1; i < components.length; i++) {
            if (!ring.equals(components[i].getRing())) {
                return false;
            }
        }
        return true;
    }

    
    private final static int basicHash = "DirectSumModule".hashCode();

    private Module[] components;
    private Ring	 ring;
}

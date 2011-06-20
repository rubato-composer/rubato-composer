/*
 * Copyright (C) 2002, 2005 Gérard Milmeister
 * Copyright (C) 2002 Stefan Müller
 * Copyright (C) 2002 Stefan Göller
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

package org.rubato.math.yoneda;

import static org.rubato.xml.XMLConstants.*;

import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.LinkedList;

import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Simple form class.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class SimpleForm extends Form {

    /**
     * Generic simple form constructor.
     */
    public SimpleForm(NameDenotator name, Morphism identifier) {
        super(name, identifier);
    }


    /**
     * simple identity form constructor.
     */
    public SimpleForm(NameDenotator name, Module m) {
        super(name, new RepresentableIdentityMorphism(m));
    }

    
    /**
     * simple identity form constructor with a range (for folding).
     */
    public SimpleForm(NameDenotator name, Module m,
                      ModuleElement lo, ModuleElement hi) {
        super(name, new RepresentableIdentityMorphism(m, lo, hi));
    }
        

    /**
     * Returns the type of the form.
     */
    public int getType() {
        return SIMPLE;
    }
    

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof SimpleForm) {
            return equals((SimpleForm)object);
        }
        else {
            return false;
        }
    }


    public boolean equals(SimpleForm f) {
        if (registered && f.registered) {
            return getName().equals(f.getName());
        }
        else {
            return fullEquals(f);
        }
    }


    public boolean fullEquals(SimpleForm f) {
        return fullEquals(f, new IdentityHashMap<Object,Object>());
    }


    public boolean fullEquals(SimpleForm f, IdentityHashMap<Object,Object> s) {
        if (this == f) {
            return true;
        }
        else if (!getName().equals(f.getName())) {
            return false;
        }
        s.put(this, f);
        return identifier.fullEquals(f.identifier, s);
    }
    

    public boolean iscomplete() {
        return identifier != null;
    }


    /**
     * Returns the number of coordinate forms.
     */
    public int getFormCount() {
        return 0;
    }
    

    /**
     * Returns a coordinate form.
     * @param i the coordinate position
     * @return the form at coordinate position i
     */
    public Form getForm(int i) {
         throw new IllegalStateException("Forms of type simple have no coordinate forms");
    }

    
    /**
     * Returns a default denotator of this simple form.
     */
    public Denotator createDefaultDenotator() {
        Denotator res = null;
        try {
            res = new SimpleDenotator(null, this, getModule().getZero());
        }
        catch (RubatoException e) {
            e.printStackTrace();
        }
        return res;
    }

    
    /**
     * Returns a default denotator of this simple form with the given address.
     */
    public Denotator createDefaultDenotator(Module address) {
        Denotator res = null;
        try {
            res = new SimpleDenotator(null, this, address, getModule().getZero());
        }
        catch (RubatoException e) {
            e.printStackTrace();
        }
        return res;
    }

    
    /**
     * Returns the module of this simple form.
     */
    public Module getModule() {
       return getIdentifier().getCodomainModule();
    }
    

    protected LinkedList<Form> getDependencies(LinkedList<Form> list) {
        if (!list.contains(this)) {
            list.add(this);
        }
        return list;
    }
    
    
    private static final String TYPE_VALUE = "simple";
    private static final String LOVALUE    = "LoValue";
    private static final String HIVALUE    = "HiValue";
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlock(FORM, TYPE_ATTR, TYPE_VALUE, NAME_ATTR, getNameString());
        getModule().toXML(writer);
        RepresentableIdentityMorphism morphism = (RepresentableIdentityMorphism)identifier;
        if (morphism.hasBounds()) {
            writer.openBlock(LOVALUE);
            morphism.getLowValue().toXML(writer);
            writer.closeBlock();
            writer.openBlock(HIVALUE);
            morphism.getHighValue().toXML(writer);
            writer.closeBlock();
        }
        writer.closeBlock();
    }


    /**
     * Reads XML representation from <code>reader</code> starting with <code>element</code>.
     * 
     * @return a simple form or null if parsing failed
     */
    public static SimpleForm fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(TYPE_VALUE));
        if (!element.hasAttribute(NAME_ATTR)) {
            reader.setError("Type %%1 of element <%2> is missing attribute %%3.", TYPE_VALUE, FORM, NAME_ATTR);
            return null;                                                
        }
        
        Element childElement = XMLReader.getChild(element, MODULE);
        if (childElement == null) {
            reader.setError("Type %%1 of element <%2> is missing element <%3>.", TYPE_VALUE, FORM, MODULE);
            return null;
        }
        
        Module module = reader.parseModule(childElement);
        if (module == null) {
            return null;
        }
        
        childElement = XMLReader.getNextSibling(childElement, LOVALUE);
        if (childElement != null) {
            ModuleElement loValue = null;
            ModuleElement hiValue = null;
            Element loValueElement = XMLReader.getChild(childElement, MODULEELEMENT);
            if (loValueElement != null) {
                loValue = reader.parseModuleElement(loValueElement);                
            }
            Element hiValueElement = XMLReader.getNextSibling(loValueElement, HIVALUE);
            if (hiValueElement != null) {
                hiValue = reader.parseModuleElement(loValueElement);                                
            }
            
            if (hiValue != null && loValue != null) {
                return new SimpleForm(NameDenotator.make(element.getAttribute(NAME_ATTR)), module, loValue, hiValue);
            }
            else {
                reader.setError("<%1> and <%2> of element <%3> of type %%4 don't have the right shape.", LOVALUE, HIVALUE, FORM, TYPE_VALUE);
                return null;
            }
        }
        
        return new SimpleForm(NameDenotator.make(element.getAttribute(NAME_ATTR)), module);
    }
    
        
    public String toString() {
        StringBuilder buf = new StringBuilder(50);
        buf.append("[");
        buf.append(getNameString());
        buf.append(":.simple(");
        buf.append(getModule());
        RepresentableIdentityMorphism morphism = (RepresentableIdentityMorphism)identifier;
        if (morphism.hasBounds()) {
            buf.append(",");
            buf.append(morphism.getLowValue());
            buf.append(",");
            buf.append(morphism.getHighValue());
        }
        buf.append(")]");
        return buf.toString();
    }
    
    
    protected void display(PrintStream out, LinkedList<Form> recursionCheckStack, int indent) {
        indent(out, indent);
        out.print("Name: \""+getNameString()+"\"");
        out.println("; Type: simple");
        indent(out, indent+4);
        out.println("Module: "+getModule());
    }    

    
    protected double getDimension(int maxDepth, int depth) {
        return 1.0;
    }
}

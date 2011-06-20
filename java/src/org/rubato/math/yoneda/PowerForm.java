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

import static org.rubato.xml.XMLConstants.FORM;
import static org.rubato.xml.XMLConstants.TYPE_ATTR;

import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.LinkedList;

import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Power form class.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class PowerForm extends Form {

    /**
     * Generic power form constructor.
     */
    public PowerForm(NameDenotator name, Morphism identifier) {
        super(name, identifier);
    }


    /**
     * Builds a power identity form.
     */
    public PowerForm(NameDenotator name, Form form) {
        super(name, new ProperIdentityMorphism(new FormDiagram(form), POWER));
    }

    
    /**
     * Returns the type of the form.
     */
    public int getType() {
        return POWER;
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof PowerForm) {
            return equals((PowerForm)object);
        }
        else {
            return false;
        }
    }


    /**
     * Tests for equality of two power forms.
     */
    public boolean equals(PowerForm f) {
        if (registered && f.registered) {
            return getName().equals(f.getName());
        }
        else {
            return fullEquals(f);
        }
    }


    public boolean fullEquals(PowerForm f) {
        return fullEquals(f, new IdentityHashMap<Object,Object>());
    }


    public boolean fullEquals(PowerForm f, IdentityHashMap<Object,Object> s) {
        if (this == f) {
            return true;
        }
        else if (!getName().equals(f.getName())) {
            return false;
        }
        else {
            s.put(this, f);
            return identifier.fullEquals(f.identifier, s);
        }
    }

    
    /**
     * Returns the number of coordinate forms.
     */
    public int getFormCount() {
        return getFormDiagram().getFormCount();
    }
    

    /**
     * Returns a coordinate form.
     * @param i the coordinate position
     * @return the form at coordinate position i
     */
    public Form getForm(int i) {
        return getFormDiagram().getForm(0);
    }

    
    /**
     * Returns the single coordinate form.
     */
    public Form getForm() {
        return getForm(0);
    }


    public FormDiagram getFormDiagram() {
        return (FormDiagram)getIdentifier().getCodomainDiagram();
    }
    
    
    protected LinkedList<Form> getDependencies(LinkedList<Form> list) {
        if (!list.contains(this)) {
            list.add(this);
            return identifier.getFormDependencies(list);
        }
        return list;
    }
    
    
    private static final String NAME_ATTR  = "name";
    private static final String TYPE_VALUE = "power";
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlock(FORM, TYPE_ATTR, TYPE_VALUE, NAME_ATTR, getNameString());
        if (identifier instanceof ProperIdentityMorphism) {
            ProperIdentityMorphism im = (ProperIdentityMorphism)identifier;
            if (im.getDiagram() instanceof FormDiagram) {
                writer.writeFormRef(getForm());
                writer.closeBlock();
                return;
            }
        }

        identifier.toXML(writer);            
        writer.closeBlock();
    }


    /**
     * Reads XML representation from <code>reader</code> starting with <code>element</code>.
     * 
     * @return a power form or null if parsing failed
     */
    public static PowerForm fromXML(XMLReader reader, Element element) {
        assert(element.getAttribute(TYPE_ATTR).equals(TYPE_VALUE));
        if (!element.hasAttribute(NAME_ATTR)) {
            reader.setError("Type %%1 of element <%2> is missing attribute %%3.", TYPE_VALUE, FORM, NAME_ATTR);
            return null;                                                
        }

        Element childElement = XMLReader.getChild(element, FORM);
        if (childElement == null) {
            reader.setError("Type %%1 of element <%2> is missing element <%2>.", TYPE_VALUE, FORM);
            return null;
        }
        
        Form form = reader.parseForm(childElement);
        if (form == null) {
            return null;
        }
                
        PowerForm powerForm = new PowerForm(NameDenotator.make(element.getAttribute("name")), form);
        if (form instanceof FormReference) {
            reader.addFormToBeResolved(powerForm);
        }
        return powerForm;
    }
    
        
    /**
     * Returns a default denotator of this power form.
     */
    public Denotator createDefaultDenotator() {
        Denotator res = null;
        try {
            res = new PowerDenotator(null, this, new LinkedList<Denotator>());
        } 
        catch (RubatoException e) {
            e.printStackTrace();
        }
        return res;
    }

    
    /**
     * Returns a default denotator of this power form with the given address.
     */
    public Denotator createDefaultDenotator(Module address) {
        Denotator res = null;
        try {
            res = new PowerDenotator(null, address, this, new LinkedList<Denotator>());
        } 
        catch (RubatoException e) {
            e.printStackTrace();
        }
        return res;
    }

    
    public String toString() {
        return "["+getNameString()+":.power("+getForm().getNameString()+")]";
    }
    
    
    protected void display(PrintStream out, LinkedList<Form> recursionCheckStack, int indent) {
        indent(out, indent);
        out.print("Name: \""+getNameString()+"\"");
        out.println("; Type: power");

        indent += 4;
    
        if (recursionCheck(recursionCheckStack)) {
            indent(out, indent);
            out.println("...");
        }
        else {
	        recursionCheckStack.addFirst(this);
	        getForm(0).display(out, recursionCheckStack, indent);
	        recursionCheckStack.removeFirst();
        }
    }


    protected double getDimension(int maxDepth, int depth) {
        if (depth > maxDepth) return 1.0;
        
        Form f = getForm(0);
        return f.getDimension(maxDepth, depth + 1);
    }
}

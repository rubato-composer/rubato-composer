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

import java.util.IdentityHashMap;
import java.util.LinkedList;

import org.rubato.base.RubatoDictionary;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Identity morphism representing a module "object".
 * Thus it is "representable". 
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class RepresentableIdentityMorphism extends IdentityMorphism {

    /**
     * Creates an identity morphism representing the given module.
     */
    public RepresentableIdentityMorphism(Module module) {
        this.module = module;
        this.lowValue = null;
        this.highValue = null;
    }


    /**
     * Creates an identity morphism representing the given module.
     * This variant specifies lower and upper bounds for the values
     * contained in the module.
     */
    public RepresentableIdentityMorphism(Module module, ModuleElement lowValue, ModuleElement highValue) {
        this.module = module;
        this.lowValue = lowValue;
        this.highValue = highValue;
    }


    public Diagram getDiagram() {
        return FormDiagram.emptyFormDiagram;
    }


    public Module getModule() {
        return module;
    }
    

    public ModuleElement getLowValue() {
        return lowValue;
    }

    
    public ModuleElement getHighValue() {
        return highValue;
    }


    public boolean hasBounds() {
        return (lowValue != null) && (highValue != null);
    }


    public int getType() {
        return SIMPLE;
    }


    public boolean isRepresentable() {
        return true;
    }
    

    public Morphism at(ModuleElement element) {
        return this;
    }

    
    public Morphism changeAddress(Module address) {
        return this;
    }


    public Morphism changeAddress(ModuleMorphism morphism) {
        return this;
    }

    
    public RepresentableIdentityMorphism copy() {
        return new RepresentableIdentityMorphism(module, lowValue, highValue); 
    }


    public int compareTo(Morphism object) {
        if (this == object) {
            return 0;
        }
        else if (object instanceof RepresentableIdentityMorphism) {
            RepresentableIdentityMorphism m = (RepresentableIdentityMorphism)object;
            int c = module.compareTo(m.getModule());
            if (c == 0) {
                if (highValue != null) {
                    if (m.highValue != null) {
                        int comp1 = highValue.compareTo(m.highValue);
                        if (comp1 == 0) {
                            return lowValue.compareTo(m.lowValue);
                        }
                        else {
                            return comp1;
                        }
                    }
                    else {
                        return 1;
                    }                    
                }
            }
            return c;
        }
        else {
            return super.compareTo(object);
        }
    }

    
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        else if (object instanceof RepresentableIdentityMorphism) {
            RepresentableIdentityMorphism m = (RepresentableIdentityMorphism)object;
            if (module.equals(m.module)) {
                return compareBounds(m);
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    
    public boolean fullEquals(Morphism m, IdentityHashMap<Object,Object> s) {
        return equals(m);
    }
    
    
    public LinkedList<Form> getFormDependencies(LinkedList<Form> list) {
        return list;
    }
    
    
    public LinkedList<Denotator> getDenotatorDependencies(LinkedList<Denotator> list) {
        return list;
    }
    
    
    public String toString() {
        return "RepresentableIdentityMorphism["+module+"]";
    }
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlock(MORPHISM, getElementTypeName());
        module.toXML(writer);
        if (lowValue != null && highValue != null) {
            lowValue.toXML(writer);
            highValue.toXML(writer);
        }
        writer.closeBlock();
    }
    
    
    public Morphism fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, MODULE);
        if (child == null) {
            reader.setError("Expected element of type <%1>", MODULE);
            return null;
        }
        Module m = reader.parseModule(child);
        if (m == null) {
            return null;
        }
        ModuleElement l = null;
        ModuleElement h = null;
        child = XMLReader.getNextSibling(child, MODULEELEMENT);
        if (child != null) {
            l = reader.parseModuleElement(child);
            if (l != null) {
                child = XMLReader.getNextSibling(child, MODULEELEMENT);
                h = reader.parseModuleElement(child);
            }
        }
        if (l == null || h == null) {
            return new RepresentableIdentityMorphism(m);
        }
        else {
            return new RepresentableIdentityMorphism(m, l, h);
        }
    }
    
    
    public String getElementTypeName() {
        return "RepresentableIdentityMorphism"; //$NON-NLS-1$
    }
    
    
    public int hashCode() {
        int hash = 7;
        hash = 37*hash + module.hashCode();
        if (lowValue != null) {
            hash = 37*hash + lowValue.hashCode();
        }
        if (highValue != null) {
            hash = 37*hash + highValue.hashCode();
        }
        return hash;
    }
    

    public int getMorphOrder() {
        return 0xEDDA;
    }


    boolean resolveReferences(RubatoDictionary dict, IdentityHashMap<?,?> history) {
        return true;
    }
    
    
    private boolean compareBounds(RepresentableIdentityMorphism m) {
        if (hasBounds() && m.hasBounds()) {
            return lowValue.equals(m.lowValue) && highValue.equals(m.highValue);
        }
        else {
            return hasBounds() == m.hasBounds();
        }
    }

    
    private Module        module;
    private ModuleElement lowValue;
    private ModuleElement highValue;
}

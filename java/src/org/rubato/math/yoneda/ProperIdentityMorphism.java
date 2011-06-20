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

import static org.rubato.xml.XMLConstants.MORPHISM;

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
 * Morphism of an "object" that is not representable.
 * For "module objects", RepresentableIdentityMorphism is used.
 * The "object" is represented by a diagram.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class ProperIdentityMorphism extends IdentityMorphism {

    /**
     * Creates an identity morphism with the specified diagram of the given type.
     */
    public ProperIdentityMorphism(Diagram diagram, int type) {
        this.diagram = diagram;
        this.type = type;
    }

    
    public Diagram getDiagram() {
        return diagram;
    }


    public Module getModule() {
        throw new UnsupportedOperationException("Morphism is not representable");
    }

    
    public int getType() {
        return type;
    }

    
    public Diagram getDomainDiagram() {
        return getDiagram();
    }

    
    public Diagram getCodomainDiagram() {
        return getDiagram();
    }

    
    public Module getDomainModule() {
        return getModule();
    }

    
    public Module getCodomainModule() {
        return getModule();
    }

    
    public int getDomainType() {
        return getType();
    }

    
    public int getCodomainType() {
        return getType();
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

    
    public ProperIdentityMorphism copy() {
        return new ProperIdentityMorphism(diagram, type); 
    }
    

    public boolean isRepresentable() {
        return false;
    }

    
    public int compareTo(Morphism object) {
        if (this == object) {
            return 0;
        }
        else if (object instanceof ProperIdentityMorphism) {
            ProperIdentityMorphism m = (ProperIdentityMorphism)object;
            int c = diagram.compareTo(m.getDomainDiagram());
            
            if (c != 0) {
                return c;
            }
            else {
                return type-m.getType();
            }
        }
        else {
            return super.compareTo(object);
        }
    }
    
    
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        else if (object instanceof ProperIdentityMorphism) {
            ProperIdentityMorphism m = (ProperIdentityMorphism)object;
            if (type == m.type) {
                return diagram.equals(m.diagram);
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
        if (this == m) {
            return true;
        }
        else if (!(m instanceof ProperIdentityMorphism)) {
            return false;
        }
        else {
            ProperIdentityMorphism pm = (ProperIdentityMorphism)m;
            return (type == pm.type) && (diagram.fullEquals(pm.diagram, s));
        }
    }
    
    
    public LinkedList<Form> getFormDependencies(LinkedList<Form> list) {
        return diagram.getFormDependencies(list);
    }
    
    
    public LinkedList<Denotator> getDenotatorDependencies(LinkedList<Denotator> list) {
        return diagram.getDenotatorDependencies(list);
    }
    
    
    boolean resolveReferences(RubatoDictionary dict, IdentityHashMap<?,?> history) {
        return diagram.resolveReferences(dict, history);
    }
    
    
    public String toString() {
        return "ProperIdentityMorphism["+diagram+","+Form.typeToString(type)+"]";
    }

    
    private final static String FORMTYPE_ATTR = "formType";
    
    
    public void toXML(XMLWriter writer) {
        writer.openBlockWithType(MORPHISM, getElementTypeName(),
                                 FORMTYPE_ATTR, Form.typeToString(getType()).toLowerCase());
        diagram.toXML(writer);
        writer.closeBlock();
    }
    
    
    public Morphism fromXML(XMLReader reader, Element element) {
        // TODO: not yet implemented
        throw new UnsupportedOperationException("Not implemented");
    }
    
    
    public String getElementTypeName() {
        return "ProperIdentityMorphism";
    }
    
    
    public int hashCode() {
        int hash = 7;
        hash = 37*hash + (type+1);
        hash = 37*hash + diagram.hashCode();
        return hash;
    }


    protected int getMorphOrder() {
        return 0xFAFA;
    }
    

    private Diagram diagram;
    private int     type;
}

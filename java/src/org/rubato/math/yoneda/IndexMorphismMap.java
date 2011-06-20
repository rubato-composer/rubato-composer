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
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * Morphism map containing a morphism and an index (for type colimit).
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public final class IndexMorphismMap implements MorphismMap {

    /**
     * Creates an IndexMorphismMap.
     */
    public IndexMorphismMap(int index, Denotator factor) { 
        this.index = index;
        this.factor = factor;
    }
    
    
    /**
     * Creates an empty IndexMorphismMap.
     */
    public IndexMorphismMap() {
        index = -1;
        factor = null;
    }
    
    
    /**
     * Returns the index of the factor contained in the map.
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Returns the factor contained in the map.
     */
    public Denotator getFactor() {
        return factor;
    }

    /**
     * Sets the factor with the given index in the map.
     */
    public void setFactor(int index, Denotator factor) {
        this.index = index;
        this.factor = factor;
    }   
    

    public int compareTo(MorphismMap object) {        
        if (this == object) {
            return 0;
        }
        else if (object instanceof IndexMorphismMap) {
            return compareTo((IndexMorphismMap)object);
        }
        else {
            return getClass().toString().compareTo(object.getClass().toString());
        }
    }

    
    public int compareTo(IndexMorphismMap other) {    
        final int aIndex = this.getIndex();        
        final int bIndex = other.getIndex();
        if (aIndex == bIndex) {
            return this.getFactor().compareTo(other.getFactor());        
        }
        else {
            return aIndex-bIndex;
        }
    }

    public boolean fullEquals(MorphismMap map, IdentityHashMap<Object,Object> s) {
        if (this == map) {
            return true;
        }
        else if (map instanceof IndexMorphismMap) {
            IndexMorphismMap m = (IndexMorphismMap)map;
            if (getIndex() != m.getIndex()) {
                return false;
            }
            if (getFactor().equals(m.getFactor())) {
                return true;
            }
        }
        return false;
    }
    

    public MorphismMap at(ModuleElement element)
            throws MappingException {
        Denotator d = factor.at(element);
        if (d == factor) {            
            return this;
        }
        else {
            return new IndexMorphismMap(index, d);
        }
    }
    

    public MorphismMap changeAddress(Module address) {
        Denotator d = factor.changeAddress(address);
        if (d != null) {
            return new IndexMorphismMap(index, d);
        }
        else {
            return null;
        }
    }
    
    
    public MorphismMap changeAddress(ModuleMorphism morphism) {
        Denotator d = factor.changeAddress(morphism);
        if (d != null) {
            return new IndexMorphismMap(index, d);
        }
        else {
            return null;
        }
    }

    
    private final static String INDEX_ATTR = "index";
    
    
    public void toXML(XMLWriter writer) {        
        writer.openBlockWithType(MORPHISMMAP, getElementTypeName(),
                                 INDEX_ATTR, index);
        factor.toXML(writer);
        writer.closeBlock();
    }
    
    
    public MorphismMap fromXML(XMLReader reader, Element element) {
        int i = XMLReader.getIntAttribute(element, INDEX_ATTR, 0);
        Element child = XMLReader.getChild(element, DENOTATOR);
        if (child == null) {
            reader.setError("Expected element of type <%1>", DENOTATOR);
            return null;
        }
        Denotator denotator = reader.parseDenotator(child);
        if (denotator == null) {
            return null;
        }
        return new IndexMorphismMap(i, denotator);
    }
    
    
    public final String getElementTypeName() {
        return "IndexMorphismMap";
    }
    
    
    public LinkedList<Form> getFormDependencies(LinkedList<Form> list) {
        return list;
    }
    
    
    public LinkedList<Denotator> getDenotatorDependencies(LinkedList<Denotator> list) {
        return factor.getDependencies(list);
    }
    
    
    public boolean resolveReferences(RubatoDictionary dict, IdentityHashMap<?,?> history) {
        Denotator d = getFactor();
        if (d instanceof DenotatorReference) {
            Denotator newDenotator = dict.getDenotator(d.getNameString());
            if (newDenotator == null) {
                return false;
            }
            setFactor(getIndex(), newDenotator);
            return true;
        }
        else {
            return d.resolveReferences(dict, history);
        }
    }
    
    
    public boolean isConstant() {
        return factor.isConstant();
    }
    
    
    public Object clone() {
        return copy();
    }
    
    
    public IndexMorphismMap copy() {
        return new IndexMorphismMap(index, factor.copy()); 
    }
    

    public boolean equals(Object object) {
        if (object instanceof IndexMorphismMap) {
            return equals((IndexMorphismMap)object);
        }
        else {
            return false;
        }
    }
    
    
    public boolean equals(IndexMorphismMap other) {
        return (getIndex() == other.getIndex() &&
                getFactor().equals(other.getFactor()));
    }

    
    public int hashCode() {
        int hash = 7;
        hash = 37*hash+index;
        hash = 37*hash+factor.hashCode();
        return hash;
    }


    private int       index;
    private Denotator factor;
}

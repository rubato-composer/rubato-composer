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

import java.util.IdentityHashMap;
import java.util.LinkedList;

import org.rubato.base.RubatoDictionary;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLInputOutput;

/**
 * Abstract base class for morphisms.
 *
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public abstract class Morphism
    implements Yoneda, XMLInputOutput<Morphism>, Comparable<Morphism> {

    /**
     * Returns the domain diagram of this morphism.
     */
    public abstract Diagram getDomainDiagram();

    /**
     * Returns the codomain diagram of this morphism.
     */
    public abstract Diagram getCodomainDiagram();

    /**
     * Returns the domain module of this morphism.
     */
    public abstract Module getDomainModule();

    /**
     * Returns the codomain module of this morphism.
     */
    public abstract Module getCodomainModule();

    /**
     * Returns the domain denotator type of this morphism.
     */
    public abstract int getDomainType();

    /**
     * Returns the codomain denotator type of this morphism.
     */
    public abstract int getCodomainType();
    
    /**
     * Returns the domain identity morphism (object) of this morphism.
     */
    public abstract IdentityMorphism getDomain();

    /**
     * Returns the codomain identity morphism (object) of this morphism.
     */
    public abstract IdentityMorphism getCodomain();

    /**
     * Returns the implementation of this morphism.
     */
    public abstract MorphismMap getMap();

    /**
     * Sets the implementation of this morphism.
     */
    public abstract void setMap(MorphismMap map);
    
    /**
     * Makes an address change.
     * 
     * @param address the new address of the denotator
     * @return a copy of this morphism with address <code>newAddress</code>
     *         or null if address changed fails
     */
    public abstract Morphism changeAddress(Module address);
    
    /**
     * Makes an address change using a module morphism.
     * 
     * @param morphism the address changing morphism
     * @return a copy of this morphism with the new address
     *         or null if address change fails
     */
    public abstract Morphism changeAddress(ModuleMorphism morphism);
    
    /**
     * Returns a deep copy of this morphism.
     */
    public final Object clone() {
        return copy();
    }
    
    /**
     * Returns a deep copy of this morphism.
     */
    public abstract Morphism copy();

    /**
     * Evaluates this morphism at address <code>element</code>.
     * 
     * @throws MappingException if evaluation fails
     */
    public abstract Morphism at(ModuleElement element)
        throws MappingException;
    
    /**
     * Returns true iff this morphism is an identity morphism.
     */
    public boolean isIdentity() {
        return (getDomainType() == getCodomainType()) &&
               (getDomainDiagram().compareTo(getCodomainDiagram()) == 0);
    }

    /**
     * Returns true iff this morphism is representable.
     */
    public boolean isRepresentable() {
        return (getDomainType() == SIMPLE) && (getCodomainType() == SIMPLE);
    }
    
    /**
     * Adds the form dependencies of this morphism to <code>list</code>.
     */
    public abstract LinkedList<Form> getFormDependencies(LinkedList<Form> list);

    /**
     * Adds the denotator dependencies of this morphism to <code>list</code>.
     */
    public abstract LinkedList<Denotator> getDenotatorDependencies(LinkedList<Denotator> list);
    
    public int compareTo(Morphism object) {
         return getClass().getName().compareTo(object.getClass().getName());
    }

    /**
     * Returns true iff this morphism is equal to the specified object.
     */
    public abstract boolean equals(Object object);
    
    public abstract boolean fullEquals(Morphism m, IdentityHashMap<Object,Object> s);
    
    /**
     * Resolve the references in this morphism resulting from parsing.
     * 
     * @return true iff all references have been resolved.
     */
    abstract boolean resolveReferences(RubatoDictionary dict, IdentityHashMap<?,?> history);
    
    /**
     * Returns a hash code for this morphism.
     */
    public abstract int hashCode();

    /**
     * Returns an integer indicating the position in the morphism ordering.
     */
    protected abstract int getMorphOrder();
}

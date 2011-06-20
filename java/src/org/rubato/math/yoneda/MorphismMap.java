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

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.LinkedList;

import org.rubato.base.RubatoDictionary;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLInputOutput;

/**
 * The implementation or "formula" of a morphism.
 * Every morphism contains such a map.
 * 
 * @author Gérard Milmeister
 * @author Stefan Müller
 * @author Stefan Göller
 */
public interface MorphismMap 
    extends Cloneable, Comparable<MorphismMap>, Serializable,
            XMLInputOutput<MorphismMap> {

    /**
     * Evaluates the map at an element.
     * This applies when the morphism map is not null addressed.
     * If it is null addressed, it is returned unchanged.
     * 
     * @param element evaluate at this element
     * @return a new morphism map if there has been any change
     * @throws MappingException if mapping fails
     */
    public MorphismMap at(ModuleElement element)
        throws MappingException;
    
    /**
     * Returns a deep copy of this MorphismMap.
     */
    public Object clone();

    /**
     * Returns a deep copy of this MorphismMap.
     */
    public MorphismMap copy();
    
    public int compareTo(MorphismMap object);
    
    /**
     * Makes an address change.
     * 
     * @param address the new address of the denotator
     * @return a copy of this morphism with address <code>newAddress</code>
     *         or null if address changed fails
     */
    public MorphismMap changeAddress(Module address);
    
    /**
     * Makes an address change using a module morphism.
     * 
     * @param morphism the address changing morphism
     * @return a copy of this morphism with the new address
     *         or null if address change fails
     */
    public MorphismMap changeAddress(ModuleMorphism morphism);

    /**
     * Every morphism map must have a hash function.
     * 
     * @return the hashcode of this morphism map
     */
    public int hashCode();
    
    public boolean fullEquals(MorphismMap m, IdentityHashMap<Object,Object> s);
    
    public LinkedList<Form> getFormDependencies(LinkedList<Form> list);
    
    public LinkedList<Denotator> getDenotatorDependencies(LinkedList<Denotator> list);
   
    public boolean resolveReferences(RubatoDictionary dict, IdentityHashMap<?,?> history);
}

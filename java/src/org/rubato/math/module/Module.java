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

import java.io.Serializable;
import java.util.List;

import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.XMLInputOutput;

/**
 * The interface for modules.
 * @see org.rubato.math.module.ModuleElement
 * 
 * @author Gérard Milmeister
 */
public interface Module
        extends Serializable, Comparable<Module>,
                XMLInputOutput<Module> {

    /**
     * Returns the zero element in this module.
     */
    public ModuleElement getZero();

    /**
     * Returns the identity morphism in this module.
     */
    public ModuleMorphism getIdentityMorphism();

    /**
     * Returns the dimension of this module.
     */
    public int getDimension();

    /**
     * Returns the null-module corresponding to this module.
     */
    public Module getNullModule();
    
    /**
     * Returns true iff this is a null-module.
     */
    public boolean isNullModule();
    
    /**
     * Returns true iff this module is a ring.
     */
    public boolean isRing();
    
    /**
     * Returns the underlying ring of this module.
     */
    public Ring getRing();

    /**
     * Returns the <code>i</code>-th component module.
     */
    public Module getComponentModule(int i);

    /**
     * Returns true iff <code>element</code> is an element of this module.
     */
    public boolean hasElement(ModuleElement element);

    /**
     * Returns a morphism that translates by <code>element</code>.
     */
    public ModuleMorphism getTranslation(ModuleElement element);

    /**
     * Casts <code>element</code> to an element in this module if possible.
     * @return null if cast is not possible
     */
    public ModuleElement cast(ModuleElement element);   
    
    /**
     * Creates an element in this module from a list of module elements.
     * @return null if no element in this module can be created from
     *         the arguments.
     */
    public ModuleElement createElement(List<ModuleElement> elements);

    /**
     * Creates an element in this module from a string representation.
     * @return null if the string is in the wrong format
     */
    public ModuleElement parseString(String string);

    /**
     * Returns the hash code for this module.
     */
    public int hashCode();

    /**
     * Returns true iff this module is equals to <code>object</code>.
     */
    public boolean equals(Object object);

    /**
     * Compares this module with <code>object</code>.
     */
    public int compareTo(Module object);

    /**
     * Returns a human readable string representation of this module.
     * The representation is not meant to be parseable.
     */
    public String toString();

    /**
     * Returns a human readable string representation of this module.
     * The representation is not meant to be parseable.
     * The string should be a short representation, possibly using
     * Unicode characters.
     */
    public String toVisualString();
}

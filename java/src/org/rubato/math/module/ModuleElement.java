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

import org.rubato.xml.XMLInputOutput;

/**
 * The interface for elements in a module.
 * @see org.rubato.math.module.Module
 * 
 * @author Gérard Milmeister
 */
public interface ModuleElement
        extends Cloneable, Serializable, Comparable<ModuleElement>,
                XMLInputOutput<ModuleElement> {    

    /**
     * Returns true iff this element is zero.
     */
    public boolean isZero();
    
    /**
     * Returns the product of this element with <code>element</code>.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public ModuleElement scaled(RingElement element)
        throws DomainException;

    /**
     * Multiplies this element with <code>element</code>.
     * This is a destructive operation.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public void scale(RingElement element)
        throws DomainException;

    /**
     * Returns the length of the element.
     */
    public int getLength();

    /**
     * Returns the <code>i</code>-th component element.
     */
    public ModuleElement getComponent(int i);

    /**
     * Returns the sum of this module element and <code>element</code>.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public ModuleElement sum(ModuleElement element)
        throws DomainException;

    /**
     * Adds <code>element</code> to this module element.
     * This is a destructive operation.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public void add(ModuleElement element)
        throws DomainException;

    /**
     * Returns the difference of this module element and <code>element</code>.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public ModuleElement difference(ModuleElement element)
        throws DomainException;

    /**
     * Subtracts <code>element</code> from this module element.
     * This is a destructive operation.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public void subtract(ModuleElement element)
        throws DomainException;
    
    /**
     * Returns the negative of this module element.
     */
    public ModuleElement negated();

    /**
     * Negate this module element.
     * This is a destructive operation.
     */
    public void negate();    

    /**
     * Fold <code>elements</code> assuming they are of this same type.
     */
    public double[] fold(ModuleElement[] elements);

    /**
     * Returns the module that this module element is an element of.
     */
    public Module getModule();

    /**
     * Tries to cast this element to an element in the given module.
     * @return a new module element in the required module
     *         and null if the cast cannot be performed. 
     */
    public ModuleElement cast(Module module);

    /**
     * Returns a string representation of this module element.
     * The representation is meant to be parseable.
     * If the argument parens is present then the the representation
     * is parenthesized if necessary.
     */
    public String stringRep(boolean ... parens);

    /**
     * Returns a human readable string representation of this module element.
     * The representation is not meant to be parseable.
     */
    public String toString();

    /**
     * Returns true iff this element is equal to <code>object</code>.
     */
    public boolean equals(Object object);
    
    /**
     * Compares this module element with <code>object</code>. 
     */
    public int compareTo(ModuleElement object);

    /**
     * Returns a deep copy of this module element.
     */
    public ModuleElement clone();
    
    /**
     * Returns the hash code for this module element.
     */
    public int hashCode();
}

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

import java.util.Iterator;

/**
 * The interface for elements in a free module.
 * @see org.rubato.math.module.FreeModule
 * 
 * @author Gérard Milmeister
 */
public interface FreeElement
        extends ModuleElement, Iterable<RingElement> {

    /**
     * Returns the <code>i</code>-th component of this free element.
     */
    public abstract ModuleElement getComponent(int i);
    
    /**
     * Returns the <code>i</code>-th ring component of this free element.
     */
    public abstract RingElement getRingElement(int i);

    /**
     * Returns the length of this free element.
     */
    public abstract int getLength();
    
    /**
     * Returns the componentwise product of this module element and <code>element</code>.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public ModuleElement productCW(ModuleElement element)
        throws DomainException;

    /**
     * Multiply this module element componentwise with <code>element</code>.
     * This is a destructive operation.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public void multiplyCW(ModuleElement element)
        throws DomainException;

    /**
     * Returns an iterator to the factors of the element.
     */
    public abstract Iterator<RingElement> iterator();
    
    /**
     * Returns this free element resized to length <code>n</code>.
     * If the new length <code>n</code> is greater than the old length,
     * the new values are filled with the zero of the underlying ring.
     * If the new length <code>n</code> is less than the old length,
     * the vector of values is simply truncated. 
     */
    public abstract FreeElement resize(int n);
}

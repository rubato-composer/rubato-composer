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

import java.util.Collections;
import java.util.Iterator;


/**
 * The abstract base class for ring elements.
 * Ring elements always have length 1.
 * @see org.rubato.math.module.Ring
 * 
 * @author Gérard Milmeister
 */
public abstract class RingElement implements FreeElement {
    
    /**
     * Returns true if this ring element is one.
     */
    public abstract boolean isOne();
    
    
    public RingElement sum(RingElement element)
            throws DomainException {
        return (RingElement)sum((ModuleElement)element);
    }
    
    
    public RingElement difference(RingElement element)
            throws DomainException {
        return (RingElement)difference((ModuleElement)element);
    }    
    
    /**
     * Returns the product of this ring element with <code>element</code>.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public abstract RingElement product(RingElement element)
        throws DomainException;

    
    /**
     * Multiplies this ring element with <code>element</code>.
     * 
     * @throws DomainException if <code>element</code> is not in domain
     */
    public abstract void multiply(RingElement element)
        throws DomainException;

    
    public ModuleElement productCW(ModuleElement element)
            throws DomainException {
        if (element instanceof RingElement) {
            return product((RingElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getModule());
        }
    }

    
    public void multiplyCW(ModuleElement element)
            throws DomainException {
        if (element instanceof RingElement) {
            multiply((RingElement)element);
        }
        else {
            throw new DomainException(RRing.ring, element.getModule());
        }
    }
    
    
    /**
     * Returns true if this ring element is invertible.
     */
    public abstract boolean isInvertible();
    
    
    /**
     * Returns the inverse of this ring element, if it has an inverse.
     */
    public abstract RingElement inverse();
    
    
    /**
     * Inverts this ring element, if it has an inverse.
     */
    public abstract void invert();

    
    /**
     * Returns the solution <i>x</i> of
     * <code>element</code>*<i>x</i> = <code>this</code>,
     * if it exists, otherwise a DivisionException is thrown. 
     */
    public abstract RingElement quotient(RingElement element)
        throws DomainException, DivisionException;
    
    
    /**
     * Replaces <code>this</code> by the solution <i>x</i> of
     * <code>element</code>*<i>x</i> = <code>this</code>, if it exists,
     * otherwise a DivisionException is thrown.
     */
    public abstract void divide(RingElement element)
        throws DomainException, DivisionException;


    /**
     * Return true iff the solution <i>x</i> of 
     * <code>this</code>*<i>x</i> = <code>element</code>
     * exists.
     */
    public abstract boolean divides(RingElement element);

    
    /**
     * Raises this ring element to the power <code>n</code>.
     */
    public RingElement power(int n) {
        if (n == 0) {
            return getRing().getOne();
        }

        RingElement factor = this.clone();
        
        if (n < 0) {
            factor.invert();
            n = -n;
        }
        
        // Finding leading bit in the exponent n
        int bpos = 31; // bits per int
        while ((n & (1 << bpos)) == 0) {
            bpos--;
        }

        RingElement result = getRing().getOne();
        try {
            while (bpos >= 0) {
                result = result.product(result);
                if ((n & (1 << bpos)) != 0) {
                    result = result.product(factor);
                }
                bpos--;
            }
        }
        catch (DomainException e) {}

        return result;
    }

    
    /**
     * Returns the length of this ring element.
     * @return always 1
     */
    public int getLength() {
        return 1;
    }
    
    
    public ModuleElement getComponent(int i) {
        return this;
    }

    
    public RingElement getRingElement(int i) {
        return this;
    }

    
    public Iterator<RingElement> iterator() {
        return Collections.singleton(this).iterator();
    }
    
    
    /**
     * Returns the ring this element is a member of.
     */
    public Ring getRing() {
        return (Ring)getModule();
    }
    
    
    public int compareTo(ModuleElement object) {
        return getModule().compareTo(object.getModule());
    }

    
    public abstract String stringRep(boolean ... parens);
    
    
    public abstract RingElement clone();
}

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

import java.util.List;

import org.rubato.math.module.morphism.ModuleMorphism;

/**
 * The abstract base class for rings with RingString elements.
 * @see org.rubato.math.module.StringElement
 * 
 * @author Gérard Milmeister
 */
public abstract class StringRing extends Ring {

    public abstract StringElement getZero();
    
    public abstract StringElement getOne();
    
    public ModuleMorphism getIdentityMorphism() {
        return ModuleMorphism.getIdentityMorphism(this);
    }
    
    public StringRing getComponentModule(int i) {
        return this;
    }
    
    public abstract boolean hasElement(ModuleElement e);
    
    public StringElement createElement(List<ModuleElement> elements) {
        if (!elements.isEmpty()) {
            return (StringElement)elements.get(0).cast(this);
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns the ring of the factors.
     */
    public abstract Ring getFactorRing();
    
    public int compareTo(Module object) {
        if (object instanceof StringRing) {
            Ring other = ((StringRing)object).getFactorRing();
            return getFactorRing().compareTo(other);
        }
        else {
            return super.compareTo(object);
        }
    }
    
    public abstract boolean equals(Object obj);
    
    public static FreeModule make(Ring ring) {
        return make(ring, 1);
    }

    public static FreeModule make(Ring ring, int dimension) {
        if (ring instanceof ZRing) {
            return ZStringProperFreeModule.make(dimension);
        }
        else if (ring instanceof ZnRing) {
            return ZnStringProperFreeModule.make(dimension, ((ZnRing)ring).getModulus());
            
        }
        else if (ring instanceof QRing) {
            return QStringProperFreeModule.make(dimension);
        }
        else if (ring instanceof RRing) {
            return RStringProperFreeModule.make(dimension);
        }
        else {
            return null;
        }
    }
}

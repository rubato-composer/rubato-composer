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

package org.rubato.math.module.morphism;

import org.rubato.math.module.*;

/**
 * The abstract base class for morphisms in a free <i>Zn</i>-module.
 * 
 * @author Gérard Milmeister
 */
public abstract class ZnFreeAbstractMorphism extends ModuleMorphism {

    public ZnFreeAbstractMorphism(int domDim, int codomDim, int m) {
        super(ZnProperFreeModule.make(domDim, m), ZnProperFreeModule.make(codomDim, m));
        modulus = m;
    }

    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        if (getDomain().hasElement(x)) {
            int[] v;
            if (x instanceof ZnProperFreeElement) {
                v = ((ZnProperFreeElement) x).getValue();
            }
            else {
                v = new int[x.getLength()];
                for (int i = 0; i < x.getLength(); i++) {
                    v[i] = ((ZnElement) x.getComponent(i)).getValue();
                }
            }
            return ZnProperFreeElement.make(mapValue(v), modulus);
        }
        else {
            throw new MappingException("ZnFreeAbstractMorphism.map: ", x, this);
        }
    }

    
    /**
     * The low-level map method.
     * This must be implemented in subclasses.
     */
    public abstract int[] mapValue(int[] x);


    /**
     * Returns the modulus of the (co)domain modular ring.
     */
    public int getModulus() {
        return modulus;
    }

    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(getDomain().getRing());
    }

    
    private int modulus;
}

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

import org.rubato.math.arith.Complex;
import org.rubato.math.module.*;

/**
 * The abstract base class for morphisms in a free <i>C</i>-module.
 * 
 * @author Gérard Milmeister
 */
public abstract class CFreeAbstractMorphism extends ModuleMorphism {

    public CFreeAbstractMorphism(int domDim, int codomDim) {
        super(CProperFreeModule.make(domDim), CProperFreeModule.make(codomDim));
    }

    
    public final ModuleElement map(ModuleElement x)
            throws MappingException {
        if (getDomain().hasElement(x)) {
            Complex[] rv;
            if (x instanceof CProperFreeElement) {
                rv = ((CProperFreeElement) x).getValue();
            }
            else {
                rv = new Complex[x.getLength()];
                for (int i = 0; i < x.getLength(); i++) {
                    rv[i] = ((CElement) x.getComponent(i)).getValue();
                }
            }
            return CProperFreeElement.make(mapValue(rv));
        }
        else {
            throw new MappingException("CFreeAbstractMorphism.map: ", x, this);
        }
    }

    
    /**
     * The low-level map method.
     * This must be implemented by subclasses. 
     */
    public abstract Complex[] mapValue(Complex[] rv);

    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(CRing.ring);
    }
}

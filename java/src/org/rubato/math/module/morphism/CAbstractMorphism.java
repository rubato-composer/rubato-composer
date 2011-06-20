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
import org.rubato.math.module.CElement;
import org.rubato.math.module.CRing;
import org.rubato.math.module.ModuleElement;

/**
 * The abstract base class for morphisms in <i>C</i>.
 * @see CRing
 * 
 * @author Gérard Milmeister
 */
public abstract class CAbstractMorphism extends ModuleMorphism {

    /**
     * Creates a new morphism from complex numbers to complex numbers.
     */
    public CAbstractMorphism() {
        super(CRing.ring, CRing.ring);
    }


    public final ModuleElement map(ModuleElement x)
            throws MappingException {
        if (x instanceof CElement) {
            Complex value = ((CElement)x.getComponent(0)).getValue();
            return new CElement(mapValue(value));
        }
        else {
            throw new MappingException("CAbstractMorphism.map: ", x, this);
        }
    }

    
    /**
     * Maps the complex value <code>c</code> under this morphism.
     * This method must be implemented by a specific morphism.
     */
    public abstract Complex mapValue(Complex c);
    
    
    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(CRing.ring);
    }
}

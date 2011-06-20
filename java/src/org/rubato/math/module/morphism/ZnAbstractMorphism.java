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

import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ZnElement;
import org.rubato.math.module.ZnRing;

/**
 * The abstract base class for morphisms in <i>Zn</i>.
 * 
 * @author Gérard Milmeister
 */
public abstract class ZnAbstractMorphism extends ModuleMorphism {

    public ZnAbstractMorphism(int modulus) {
        super(ZnRing.make(modulus), ZnRing.make(modulus));
        this.modulus = modulus;
    }

    
    public ModuleElement map(ModuleElement x)
            throws MappingException {
        if (getDomain().hasElement(x)) {
            int v = ((ZnElement) x.getComponent(0)).getValue();
            return new ZnElement(mapValue(v), modulus);
        }
        else {
            throw new MappingException("ZnAbstractMorphism.map: ", x, this);
        }
    }

    
    /**
     * The low-level map method.
     * This must be implemented in subclasses.
     */
    public abstract int mapValue(int x);


    /**
     * Returns the modulus of the (co)domain modular ring.
     */
    public int getModulus() {
        return modulus;
    }


    protected int modulus;


    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(getDomain().getRing());
    }
}

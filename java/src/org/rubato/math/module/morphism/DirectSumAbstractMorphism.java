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

import org.rubato.math.module.DirectSumElement;
import org.rubato.math.module.DirectSumModule;
import org.rubato.math.module.ModuleElement;

/**
 * The abstract base class for morphisms in a composite module.
 * 
 * @author Gérard Milmeister
 */
public abstract class DirectSumAbstractMorphism extends ModuleMorphism {

    public DirectSumAbstractMorphism(DirectSumModule domain, DirectSumModule codomain) {
        super(domain, codomain);
    }

    
    public final ModuleElement map(ModuleElement x)
            throws MappingException {
        if (getDomain().hasElement(x)) {
            ModuleElement[] components = new ModuleElement[x.getLength()];
            for (int i = 0; i < x.getLength(); i++) {
                components[i] = x.getComponent(i);
            }
            return mapValue(DirectSumElement.make(components));
        }
        else {
            throw new MappingException("DirectSumAbstractMorphism.map: ", x, this);
        }
    }

    
    /**
     * The low-level map method.
     * This must be implemented by subclasses.
     */
    public abstract DirectSumElement mapValue(DirectSumElement x);
}

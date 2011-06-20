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

import org.rubato.math.arith.Rational;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.QElement;
import org.rubato.math.module.QRing;

/**
 * The abstract base class for morphisms in <i>Q</i>.
 * 
 * @author Gérard Milmeister
 */
public abstract class QAbstractMorphism extends ModuleMorphism {

    public QAbstractMorphism() {
        super(QRing.ring, QRing.ring);
    }

    
    public final ModuleElement map(ModuleElement x)
            throws MappingException {
        if (getDomain().hasElement(x)) {
            Rational value = ((QElement)x).getValue();
            return new QElement(mapValue(value));
        }
        else {
            throw new MappingException("QAbstractMorphism.map: ", x, this);
        }
    }

    
    /**
     * The low-level map method.
     * This must be implemented in subclasses.
     */
    public abstract Rational mapValue(Rational r);


    public ModuleMorphism getRingMorphism() {
        return getIdentityMorphism(QRing.ring);
    }
}

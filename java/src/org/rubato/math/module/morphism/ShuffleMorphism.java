/*
 * Copyright (C) 2005 Gérard Milmeister
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
import org.rubato.math.arith.Rational;
import org.rubato.math.matrix.*;
import org.rubato.math.module.*;

/**
 * This morphism reorders the components of an element of
 * the domain.
 * 
 * @author Gérard Milmeister
 */
public abstract class ShuffleMorphism {

    /**
     * Creates a morphisms that reorder an element from <code>domain</code>,
     * according to the array <code>shuffle</code>.
     * This means, that the component at <code>i</code> is mapped to <code>shuffle[i]</code>.
     * Components not hit are set to zero. 
     */
    public static ModuleMorphism make(FreeModule domain, FreeModule codomain, int[] shuffle) {
        if (domain.getRing().equals(codomain.getRing())) {
            Ring ring = domain.getRing();
            if (shuffle.length != domain.getDimension()) {
                return null;
            }
            for (int i = 0; i < shuffle.length; i++) {
                if (shuffle[i] > codomain.getDimension()-1) {
                    return null;
                }
                if (shuffle[i] >= 0) {
                    for (int j = 0; j < i; j++) {
                        if (shuffle[i] == shuffle[j]) {
                            return null;
                        }
                    }
                }
            }
            if (domain.getDimension() == codomain.getDimension()) {
                boolean id = true;
                for (int i = 0; i < shuffle.length; i++) {
                    if (shuffle[i] != i) {
                        id = false;
                    }
                }
                if (id) {
                    return new IdentityMorphism(domain);
                }
            }
            return createShuffleMorphism(ring, domain, codomain, shuffle);
        }
        else {
            return null;
        }        
    }
    
    
    private static ModuleMorphism createShuffleMorphism(Ring ring, FreeModule domain, FreeModule codomain, int[] shuffle) {
        ModuleMorphism res = null;
        if (ring instanceof ZRing) {
            ZMatrix m = new ZMatrix(codomain.getDimension(), domain.getDimension());
            int[] v = new int[codomain.getDimension()];
            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    if (shuffle[j] == i) {
                        m.set(i, j, 1);
                    }
                    else {
                        m.set(i, j, 0);
                    }
                }
                v[i] = 0;
            }
            res = ZFreeAffineMorphism.make(m, v);
        }
        else if (ring instanceof QRing) {
            QMatrix m = new QMatrix(codomain.getDimension(), domain.getDimension());
            Rational[] v = new Rational[codomain.getDimension()];
            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    if (shuffle[j] == i) {
                        m.set(i, j, Rational.getOne());
                    }
                    else {
                        m.set(i, j, Rational.getZero());
                    }
                }
                v[i] = Rational.getZero();
            }
            res = QFreeAffineMorphism.make(m, v);
        }
        else if (ring instanceof RRing) {
            RMatrix m = new RMatrix(codomain.getDimension(), domain.getDimension());
            double[] v = new double[codomain.getDimension()];
            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    if (shuffle[j] == i) {
                        m.set(i, j, 1);
                    }
                    else {
                        m.set(i, j, 0);
                    }
                }
                v[i] = 0;
            }
            res = RFreeAffineMorphism.make(m, v);
        }
        else if (ring instanceof CRing) {
            CMatrix m = new CMatrix(codomain.getDimension(), domain.getDimension());
            Complex[] v = new Complex[codomain.getDimension()];
            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    if (shuffle[j] == i) {
                        m.set(i, j, Complex.getOne());
                    }
                    else {
                        m.set(i, j, Complex.getZero());
                    }
                }
                v[i] = Complex.getZero();
            }
            res = CFreeAffineMorphism.make(m, v);
        }
        else if (ring instanceof ZnRing) {
            ZnMatrix m = new ZnMatrix(codomain.getDimension(), domain.getDimension(), ((ZnRing)ring).getModulus());
            int[] v = new int[codomain.getDimension()];
            for (int i = 0; i < m.getRowCount(); i++) {
                for (int j = 0; j < m.getColumnCount(); j++) {
                    if (shuffle[j] == i) {
                        m.set(i, j, 1);
                    }
                    else {
                        m.set(i, j, 0);
                    }
                }
                v[i] = 0;
            }
            res = ZnFreeAffineMorphism.make(m, v);
        }
        else {
            int dim = domain.getDimension();
            int codim = codomain.getDimension();
            GenericAffineMorphism morphism = new GenericAffineMorphism(ring, dim, codim);
            for (int i = 0; i < codim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (shuffle[j] == i) {
                        morphism.setMatrix(i, j, ring.getOne());
                    }
                }
            }
            res = morphism;
        }
        
        return res;
    }
    
    
    private ShuffleMorphism() {}
}

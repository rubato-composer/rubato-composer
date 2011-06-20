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

package org.rubato.util;

import org.rubato.math.arith.Rational;
import org.rubato.math.module.*;


/**
 * Subclasses of this class specialize in converting module
 * elements to and from double.
 * 
 * @author Gérard Milmeister
 */
public abstract class DoubleConverter {

    /**
     * Converts the module element <code>x</code> to double.
     */
    public abstract double toDouble(ModuleElement x);

    
    /**
     * Converts the double <code>x</code> to a module element.
     */
    public abstract ModuleElement fromDouble(double x);
    
    
    /**
     * Creates a new converter that converts between double
     * and the elements from the module <code>m</code>.
     * 
     * @return null if no such converter could be created
     */
    public static DoubleConverter makeDoubleConverter(Module m) {
        if (m.equals(RRing.ring)) {
            return rconverter;
        }
        else if (m.equals(ZRing.ring)) {
            return zconverter;            
        }
        else if (m instanceof ZnRing) {
            return new ZnConverter(((ZnRing)m).getModulus());            
        }
        else if (m instanceof QRing) {
            return qconverter;            
        }
        else {
            return null;
        }
    }

    private static class ZnConverter extends DoubleConverter {
        public ZnConverter(int modulus) {
            this.modulus = modulus;
        }
        public double toDouble(ModuleElement x) {
            return ((ZnElement)x).getValue();
        }
        public ModuleElement fromDouble(double x) {
            return new ZnElement((int)Math.round(x), modulus);
        }
        private int modulus;
    }
    
    private static DoubleConverter rconverter = new DoubleConverter() {
        public double toDouble(ModuleElement x) {
            return ((RElement)x).getValue();
        }
        public ModuleElement fromDouble(double x) {
            return new RElement(x);
        }
    };

    private static DoubleConverter zconverter = new DoubleConverter() {
        public double toDouble(ModuleElement x) {
            return ((ZElement)x).getValue();
        }
        public ModuleElement fromDouble(double x) {
            return new ZElement((int)Math.round(x));
        }
    };

    private static DoubleConverter qconverter = new DoubleConverter() {
        public double toDouble(ModuleElement x) {
            return ((QElement)x).getValue().doubleValue();
        }
        public ModuleElement fromDouble(double x) {
            return new QElement(new Rational(x));
        }
    };
}

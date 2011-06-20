/*
 * Copyright (C) 2006 Gérard Milmeister
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

package org.rubato.scheme;

import org.rubato.math.arith.Complex;

/**
 * The class of real values.
 * 
 * @author Gérard Milmeister
 */
public final class SReal extends SNumber {

    /**
     * Creates a Scheme value from the real number <code>r</code>.
     */
    public static SReal make(double r) {
        return new SReal(r);
    }
    
    public boolean eq_p(SExpr sexpr) {
        return this == sexpr;
    }
    
    public boolean eqv_p(SExpr sexpr) {
        return (sexpr instanceof SReal) && ((SReal)sexpr).r == r;
    }
    
    public boolean equal_p(SExpr sexpr) {
        return (sexpr instanceof SReal) && ((SReal)sexpr).r == r;
    }
    
    public boolean equals(Object obj) {
        return (obj instanceof SReal) && ((SReal)obj).r == r;
    }
    
    public boolean isReal() {
        return true;
    }
    
    public SNumber add(SNumber n) {
        return n.add(this);
    }

    public SNumber add(SInteger n) {
        return SReal.make(r+n.getInt());
    }

    public SNumber add(SRational n) {
        return SReal.make(r+n.getRational().doubleValue());
    }

    public SNumber add(SReal n) {
        return SReal.make(n.r+r);
    }

    public SNumber add(SComplex n) {
        return SComplex.make(n.getComplex().sum(r));
    }
    
    public SNumber subtract(SNumber n) {
        return n.subtractFrom(this);
    }

    public SNumber subtractFrom(SInteger n) {
        return SReal.make(n.getInt()-r);
    }

    public SNumber subtractFrom(SRational n) {
        return SReal.make(n.getRational().doubleValue()-r);
    }

    public SNumber subtractFrom(SReal n) {
        return SReal.make(n.r-r);
    }

    public SNumber subtractFrom(SComplex n) {
        return SComplex.make(n.getComplex().difference(r));
    }

    public SNumber multiply(SNumber n) {
        return n.multiply(this);
    }
    
    public SNumber multiply(SInteger n) {
        return SReal.make(r*n.getInt());
    }

    public SNumber multiply(SRational n) {
        return SReal.make(r*n.getRational().doubleValue());
    }

    public SNumber multiply(SReal n) {
        return SReal.make(r*n.r);
    }

    public SNumber multiply(SComplex n) {
        return SComplex.make(n.getComplex().product(r));
    }

    public SNumber divide(SNumber n) {
        return n.divideInto(this);
    }
    
    public SNumber divideInto(SInteger n) {
        return SReal.make(n.getInt()/r);
    }
    
    public SNumber divideInto(SRational n) {
        return SReal.make(n.getRational().doubleValue()/r);
    }
    
    public SNumber divideInto(SReal n) {
        return SReal.make(n.r/r);
    }
    
    public SNumber divideInto(SComplex n) {
        return SComplex.make(n.getComplex().quotient(r));
    }
    
    public SNumber neg() {
        return new SReal(-r);
    }
    
    public SNumber abs() {
        if (r < 0) {
            return neg();
        }
        else {
            return this;
        }
    }
    
    public boolean zero_p() {
        return r == 0.0;
    }
    
    public boolean positive_p() {
        return r > 0.0;
    }
    
    public boolean negative_p() {
        return r < 0.0;
    }
    
    public SNumber acos() {
        if (r >= -1.0 || r <= 1.0) {
            return SReal.make(Math.acos(r));
        }
        else {
            return new SComplex(new Complex(r, 0).acos());
        }
    }

    public SNumber asin() {
        if (r >= -1.0 || r <= 1.0) {
            return SReal.make(Math.asin(r));
        }
        else {
            return new SComplex(new Complex(r, 0).asin());
        }
    }

    public SNumber atan(SNumber n) {
        if (n.isComplex()) {
            return new SComplex(new Complex(r)).atan(n);
        }
        else {
            double x = n.toReal().r;
            return SReal.make(Math.atan2(r, x));
        }
    }

    public SNumber ceiling() {
        return SReal.make(Math.ceil(r));
    }

    public SNumber cos() {
        return SReal.make(Math.cos(r));
    }

    public SNumber exp() {
        return SReal.make(Math.exp(r));
    }

    public SNumber floor() {
        return SReal.make(Math.floor(r));
    }

    public SNumber log() {
        return SReal.make(Math.log(r));
    }

    public SNumber round() {
        return SReal.make(Math.round(r));
    }

    public SNumber sin() {
        return SReal.make(Math.sin(r));
    }

    public SNumber tan() {
        return SReal.make(Math.tan(r));
    }

    public SNumber truncate() {
        if (r > 0.0) {
            return floor();
        }
        else if (r < 0.0) {
            return ceiling();
        }
        else {
            return this;
        }
    }

    public SNumber angle() {
        return r >= 0.0?SReal.make(0):SReal.make(Math.PI);
    }

    public SNumber expt(SNumber n) {
        if (n.isComplex()) {
            return new SComplex(new Complex(r)).expt(n);
        }
        else {
            double x = n.toReal().r;
            return SReal.make(Math.exp(x*Math.log(r)));
        }
    }

    public SNumber imagPart() {
        return SReal.make(0);
    }

    public SNumber realPart() {
        return this;
    }

    public SNumber sqrt() {
        return new SReal(Math.sqrt(r));
    }

    public SReal toReal() {
        return this;
    }
    
    public SNumber toInexact() {
        return this;
    }
    
    public String toString() {
        return Double.toString(r);
    }
    
    public String display() {
        return Double.toString(r);
    }

    public static double truncate(double r) {
        if (r > 0.0) {
            return Math.floor(r);
        }
        else if (r < 0.0) {
            return Math.ceil(r);
        }
        else {
            return r;
        }
    }

    /**
     * Returns the real number in this Scheme value.
     */
    public double getDouble() {
        return r;
    }
    
    private SReal(double r) {
        this.r = r;
    }
    
    private double r; 
}

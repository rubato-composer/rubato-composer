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
 * The class of complex number values.
 * 
 * @author Gérard Milmeister
 */
public final class SComplex extends SNumber {

    /**
     * Creates a Scheme value from the complex number <code>c</code>.
     * If <code>c</code> is a real, an SReal instance is created.
     */
    public static SNumber make(Complex c) {
        if (c.isReal()) {
            return SReal.make(c.getReal());
        }
        else {
            return new SComplex(c);
        }
    }

    /**
     * Creates a Scheme complex number value from <code>c</code>.
     */
    public SComplex(Complex c) {
        this.c = c;
    }    

    public boolean eq_p(SExpr sexpr) {
        return this == sexpr;
    }
    
    public boolean eqv_p(SExpr sexpr) {
        return (sexpr instanceof SComplex) && ((SComplex)sexpr).c.equals(c);
    }
    
    public boolean equal_p(SExpr sexpr) {
        return (sexpr instanceof SComplex) && ((SComplex)sexpr).c.equals(c);
    }
    
    public boolean equals(Object obj) {
        return (obj instanceof SComplex) && ((SComplex)obj).c.equals(c);
    }
    
    public boolean isComplex() {
        return true;
    }
    
    public SNumber add(SNumber n) {
        return n.add(this);
    }

    public SNumber add(SInteger n) {
        return new SComplex(c.sum(new Complex(n.getInt())));
    }

    public SNumber add(SRational n) {
        return new SComplex(c.sum(new Complex(n.getRational().doubleValue())));
    }

    public SNumber add(SReal n) {
        return new SComplex(c.sum(new Complex(n.getDouble())));
    }

    public SNumber add(SComplex n) {
        return new SComplex(c.sum(n.c));
    }
    
    public SNumber subtract(SNumber n) {
        return n.subtractFrom(this);
    }

    public SNumber subtractFrom(SInteger n) {
        return new SComplex(c.difference(new Complex(n.getInt())));
    }

    public SNumber subtractFrom(SRational n) {
        return new SComplex(c.difference(new Complex(n.getRational().doubleValue())));
    }

    public SNumber subtractFrom(SReal n) {
        return SComplex.make(c.difference(new Complex(n.getDouble())));
    }

    public SNumber subtractFrom(SComplex n) {
        return SComplex.make(n.c.difference(n.c));
    }

    public SNumber multiply(SNumber n) {
        return n.multiply(this);
    }
    
    public SNumber multiply(SInteger n) {
        return SComplex.make(c.product(n.getInt()));
    }

    public SNumber multiply(SRational n) {
        return SComplex.make(c.product(n.getRational().doubleValue()));
    }

    public SNumber multiply(SReal n) {
        return SComplex.make(c.product(n.getDouble()));
    }

    public SNumber multiply(SComplex n) {
        return SComplex.make(c.product(n.c));
    }

    public SNumber divide(SNumber n) {
        return n.divideInto(this);
    }
    
    public SNumber divideInto(SInteger n) {
        return SComplex.make(c.inverse().product(n.getInt()));
    }
    
    public SNumber divideInto(SRational n) {
        return SComplex.make(c.inverse().product(n.getRational().doubleValue()));
    }
    
    public SNumber divideInto(SReal n) {
        return SComplex.make(c.inverse().product(n.getDouble()));
    }
    
    public SNumber divideInto(SComplex n) {
        return SComplex.make(n.c.quotient(c));
    }
    
    public SNumber neg() {
        return SComplex.make(c.negated());
    }
    
    public SNumber abs() {
        return SReal.make(c.abs());
    }
    
    public SNumber acos() {
        return new SComplex(c.acos());
    }

    public SNumber asin() {
        return new SComplex(c.asin());
    }

    public SNumber atan(SNumber n) {
        if (n.isComplex()) {
            return new SComplex(c.atan(((SComplex)n).c));
        }
        else {
            return new SComplex(c.atan(new Complex(n.toReal().getDouble())));
        }
    }

    public SNumber ceiling() {
        return SComplex.make(new Complex(Math.ceil(c.getReal()), Math.ceil(c.getImag())));        
    }

    public SNumber cos() {
        return new SComplex(c.cos());
    }

    public SNumber exp() {
        return SComplex.make(c.exp());
    }

    public SNumber floor() {
        return SComplex.make(new Complex(Math.floor(c.getReal()), Math.floor(c.getImag())));        
    }

    public SNumber log() {
        return new SComplex(c.log());
    }

    public SNumber round() {
        return SComplex.make(new Complex(Math.round(c.getReal()), Math.round(c.getImag())));        
    }

    public SNumber sin() {
        return new SComplex(c.sin());
    }

    public SNumber tan() {
        return new SComplex(c.tan());
    }

    public SNumber truncate() {
        return SComplex.make(new Complex(SReal.truncate(c.getReal()), SReal.truncate(c.getImag())));        
    }

    public SNumber angle() {
        return SReal.make(c.arg());
    }

    public SNumber expt(SNumber n) {
        if (n.isInteger()) {
            if (n.negative_p()) {
                return new SComplex(c.inverse()).expt(n.neg());
            }
            else {
                int e = ((SInteger)n).getInt();
                Complex res = new Complex(1);
                while (e > 0) {
                    res.multiply(c);
                    e--;
                }
                return new SComplex(res); 
            }
        }
        else if (n.isComplex()) {
            return new SComplex(c.expt(((SComplex)n).c));
        }
        else {
            return new SComplex(c.expt(new Complex(n.toReal().getDouble())));
        }
    }

    public SNumber imagPart() {
        return SReal.make(c.getImag());
    }

    public SNumber realPart() {
        return SReal.make(c.getReal());
    }

    public SNumber sqrt() {
        return SComplex.make(c.sqrt()); 
    }

    public boolean zero_p() {
        return c.isZero();
    }
    
    public boolean positive_p() {
        return c.getReal() > 0.0;
    }
    
    public boolean negative_p() {
        return c.getReal() < 0.0;
    }
    
    public SReal toReal() {
        return SReal.make(c.abs());
    }
    
    public SNumber toInexact() {
        return this;
    }
    
    public String toString() {
        return c.toString();
    }
    
    public String display() {
        return c.toString();
    }
    
    /**
     * Returns the complex number in this Scheme value.
     */
    public Complex getComplex() {
        return c;
    }
    
    private Complex c;
}

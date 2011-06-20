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

import org.rubato.math.arith.Rational;

/**
 * The class of integer values.
 * 
 * @author Gérard Milmeister
 */
public final class SInteger extends SNumber {

    /**
     * Creates a Scheme integer value from <code>i</code>.
     */
    public SInteger(int i) {
        this.i = i;
    }
    
    public boolean eq_p(SExpr sexpr) {
        return this == sexpr;
    }
    
    public boolean eqv_p(SExpr sexpr) {
        return (sexpr instanceof SInteger) && ((SInteger)sexpr).i == i;
    }
    
    public boolean equal_p(SExpr sexpr) {
        return (sexpr instanceof SInteger) && ((SInteger)sexpr).i == i;
    }
    
    public boolean equals(Object obj) {
        return (obj instanceof SInteger) && ((SInteger)obj).i == i;
    }
    
    public boolean isInteger() {
        return true;
    }

    public SNumber add(SNumber n) {
        return n.add(this);
    }

    public SNumber add(SInteger n) {
        return new SInteger(n.i+i);
    }

    public SNumber add(SRational n) {
        return SRational.make(n.getRational().sum(i));
    }

    public SNumber add(SReal n) {
        return SReal.make(n.getDouble()+i);
    }

    public SNumber add(SComplex n) {
        return SComplex.make(n.getComplex().sum(i));
    }
    
    public SNumber subtract(SNumber n) {
        return n.subtractFrom(this);
    }    


    public SNumber subtractFrom(SInteger n) {
        return new SInteger(n.i-i);
    }

    public SNumber subtractFrom(SRational n) {
        return SRational.make(n.getRational().difference(i));
    }

    public SNumber subtractFrom(SReal n) {
        return SReal.make(n.getDouble()-i);
    }

    public SNumber subtractFrom(SComplex n) {
        return SComplex.make(n.getComplex().sum(i));
    }

    public SNumber multiply(SNumber n) {
        return n.multiply(this);
    }
    
    public SNumber multiply(SInteger n) {
        return new SInteger(n.i*i);
    }

    public SNumber multiply(SRational n) {
        return SRational.make(n.getRational().product(i));
    }

    public SNumber multiply(SReal n) {
        return SReal.make(n.getDouble()*i);
    }

    public SNumber multiply(SComplex n) {
        return SComplex.make(n.getComplex().product(i));
    }

    public SNumber divide(SNumber n) {
        return n.divideInto(this);
    }
    
    public SNumber divideInto(SInteger n) {
        if (i != 0) {
            return SRational.make(new Rational(n.i, i));
        }
        else {
            throw new ArithmeticException("division by zero");
        }
    }
    
    public SNumber divideInto(SRational n) {
        if (i != 0) {
            return SRational.make(n.getRational().quotient(i));
        }
        else {
            throw new ArithmeticException("division by zero");
        }
    }
    
    public SNumber divideInto(SReal n) {
        if (i != 0) {
            return SReal.make(n.getDouble()/i);
        }
        else {
            throw new ArithmeticException("division by zero");
        }
    }
    
    public SNumber divideInto(SComplex n) {
        if (i != 0) {
            return SComplex.make(n.getComplex().quotient(i));
        }
        else {
            throw new ArithmeticException("division by zero");
        }
    }
    
    public SNumber neg() {
        return new SInteger(-i);
    }
    
    public SNumber abs() {
        return new SInteger(Math.abs(i));
    }
    
    public SNumber acos() {
        return toReal().acos();
    }

    public SNumber asin() {
        return toReal().asin();
    }

    public SNumber atan(SNumber n) {
        return toReal().atan(n);
    }

    public SNumber ceiling() {
        return this;
    }

    public SNumber cos() {
        return SReal.make(Math.cos(i));
    }

    public SNumber exp() {
        return SReal.make(Math.exp(i));
    }

    public SNumber floor() {
        return this;
    }

    public SNumber log() {
        return SReal.make(Math.log(i));
    }

    public SNumber round() {
        return this;
    }

    public SNumber sin() {
        return SReal.make(Math.sin(i));
    }

    public SNumber tan() {
        return SReal.make(Math.tan(i));
    }

    public SNumber truncate() {
        return this;
    }

    public SNumber angle() {
        return i >= 0?SReal.make(0):SReal.make(Math.PI);
    }

    public SNumber expt(SNumber n) {
        if (i == 1 || i == 0) {
            return this;
        }
        else if (n instanceof SInteger) {
            int e = ((SInteger)n).i;
            if (e == 0) {
                return new SInteger(1);
            }
            else if (e > 0) {
                int res = 1;
                while (e > 0) {
                    res *= i;
                    e--;
                }
                return new SInteger(res); 
            }
            else {
                return SRational.make(new Rational(1, i)).expt(n.neg());
            }
        }
        else {
            return toReal().expt(n);
        }
    }

    public SNumber imagPart() {
        return new SInteger(0);
    }

    public SNumber realPart() {
        return this;
    }

    public SNumber sqrt() {
        return SReal.make(Math.sqrt(i));
    }

    public boolean zero_p() {
        return i == 0;
    }

    public boolean positive_p() {
        return i > 0;
    }

    public boolean negative_p() {
        return i < 0;
    }

    public SReal toReal() {
        return SReal.make(i);
    }
    
    public SReal toInexact() {
        return toReal();
    }
    
    public String toString() {
        return Integer.toString(i);
    }
    
    public String display() {
        return Integer.toString(i);
    }
    
    /**
     * Returns the integer in this Scheme value.
     */
    public int getInt() {
        return i;
    }
    
    private int i; 
}

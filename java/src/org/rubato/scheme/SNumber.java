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

/**
 * The base class of all numerical values.
 * 
 * @author Gérard Milmeister
 */
public abstract class SNumber extends SExpr {

    public boolean isNumber() { return true; }
    
    /**
     * Returns the sum of this number and number <code>n</code>.
     */
    public abstract SNumber add(SNumber n);

    /**
     * Returns the sum of this number and integer <code>n</code>.
     */
    public abstract SNumber add(SInteger n);

    /**
     * Returns the sum of this number and rational number <code>n</code>.
     */
    public abstract SNumber add(SRational n);

    /**
     * Returns the sum of this number and real number <code>n</code>.
     */
    public abstract SNumber add(SReal n);

    /**
     * Returns the sum of this number and complex number <code>n</code>.
     */
    public abstract SNumber add(SComplex n);
    
    /**
     * Returns the difference of this number and number <code>n</code>.
     */
    public abstract SNumber subtract(SNumber n);

    /**
     * Returns the difference of the integer <code>n</code> and this number.
     */
    public abstract SNumber subtractFrom(SInteger n);

    /**
     * Returns the difference of the rational <code>n</code> and this number.
     */
    public abstract SNumber subtractFrom(SRational n);

    /**
     * Returns the difference of the real number <code>n</code> and this number.
     */
    public abstract SNumber subtractFrom(SReal n);

    /**
     * Returns the difference of the complex number <code>n</code> and this number.
     */
    public abstract SNumber subtractFrom(SComplex n);

    /**
     * Returns the product of this number and the number <code>n</code>.
     */
    public abstract SNumber multiply(SNumber n);

    /**
     * Returns the product of this number and the integer <code>n</code>.
     */
    public abstract SNumber multiply(SInteger n);

    /**
     * Returns the product of this number and the rational <code>n</code>.
     */
    public abstract SNumber multiply(SRational n);

    /**
     * Returns the product of this number and the real number <code>n</code>.
     */
    public abstract SNumber multiply(SReal n);

    /**
     * Returns the product of this number and the complex number <code>n</code>.
     */
    public abstract SNumber multiply(SComplex n);
    
    /**
     * Returns the quotient of this number by the number <code>n</code>.
     */
    public abstract SNumber divide(SNumber n);
    
    /**
     * Returns the quotient of the integer <code>n</code> by this number.
     */
    public abstract SNumber divideInto(SInteger n);

    /**
     * Returns the quotient of the rational <code>n</code> by this number.
     */
    public abstract SNumber divideInto(SRational n);

    /**
     * Returns the quotient of the real number <code>n</code> by this number.
     */
    public abstract SNumber divideInto(SReal n);

    /**
     * Returns the quotient of the complex number <code>n</code> by this number.
     */
    public abstract SNumber divideInto(SComplex n);

    /**
     * Returns the negative of this number.
     */
    public abstract SNumber neg();

    /**
     * Returns the absolute value of this number.
     */
    public abstract SNumber abs();

    /**
     * Returns true iff this number is zero.
     */
    public abstract boolean zero_p();

    /**
     * Returns true iff this number is positive.
     */
    public abstract boolean positive_p();

    /**
     * Returns true iff this number is negative.
     */
    public abstract boolean negative_p();
    
    /**
     * Returns the floor of this number.
     */
    public abstract SNumber floor();
    
    /**
     * Returns the ceiling of this number.
     */
    public abstract SNumber ceiling();
    
    /**
     * Returns this number truncated.
     */
    public abstract SNumber truncate();
    
    /**
     * Returns this number rounded.
     */
    public abstract SNumber round();
    
    /**
     * Returns e^(this number).
     */
    public abstract SNumber exp();
    
    /**
     * Returns the natural logarithm of this number.
     */
    public abstract SNumber log();

    /**
     * Returns the sine of this number.
     */
    public abstract SNumber sin();

    /**
     * Returns the cosine of this number.
     */
    public abstract SNumber cos();

    /**
     * Returns the tangent of this number.
     */
    public abstract SNumber tan();

    /**
     * Returns the arcsine of this number.
     */
    public abstract SNumber asin();

    /**
     * Returns the arccosine of this number.
     */
    public abstract SNumber acos();

    /**
     * Returns the arctangent of this number.
     */
    public abstract SNumber atan(SNumber n);

    /**
     * Returns the square root of this number.
     */
    public abstract SNumber sqrt();

    /**
     * Returns (this number)^<code>n</code>.
     */
    public abstract SNumber expt(SNumber n);

    /**
     * Returns the real part of this number.
     */
    public abstract SNumber realPart();

    /**
     * Returns the imaginary part of this number.
     */
    public abstract SNumber imagPart();

    /**
     * Returns the angle of this number.
     */
    public abstract SNumber angle();
    
    /**
     * Returns this number converted to a real number.
     */
    public abstract SReal toReal();

    /**
     * Returns this number converted to an inexact value.
     */
    public abstract SNumber toInexact();
    
    protected SNumber() {}
}

/*
 * Copyright (C) 2001 Gérard Milmeister
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

package org.rubato.math.arith;

/**
 * Complex number arithmetic.
 * 
 * @author Gérard Milmeister
 */
@SuppressWarnings("nls")
public final class Complex extends Number implements Comparable<Complex>, Cloneable {
    
    /**
     * Creates the complex number 0+i0.
     */
    public Complex() {
        real = 0.0;
        imag = 0.0;
    }
        

    /**
     * Creates the complex number real+i*imag. 
     */
    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }
    
    
    /**
     * Creates the complex number real+i0.
     */
    public Complex(double real) {
        this.real = real;
        this.imag = 0.0;
    }
    
    
    /**
     * Creates the complex number real+i0.
     */
    public Complex(int real) {
        this.real = real;
        this.imag = 0.0;
    }
    
    
    /**
     * Creates a new complex number from <code>c</code>.
     */
    public Complex(Complex c) {
        real = c.real;
        imag = c.imag;
    }
    

    /**
     * Creates a new complex number from its string representation.
     * @param s must be of the form "x+i*y" where x and y are
     *          string representations of doubles,
     *          or of one of the forms "i*x" or "i"
     */
    public Complex(String s) {
        try {
            int pos = s.indexOf("+i*");
            if (pos >= 0) {
                real = Double.parseDouble(s.substring(0, pos));
                imag = Double.parseDouble(s.substring(pos+3));
                return;
            }
            pos = s.indexOf("i*");
            if (pos == 0) {
                imag = Double.parseDouble(s.substring(pos+2));
                real = 0.0;
                return;
            }
            pos = s.indexOf("i");
            if (pos == 0 && s.length() == 1) {
                imag = 1.0;
                real = 0.0;
                return;
            }

            real = Double.parseDouble(s);
            imag = 0.0;
        }
        catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    
    /**
     * Creates a complex number with the polar representation r*e^(i*phi).
     * @param r   the absolute value of the complex number
     * @param phi the argument of the complex number
     */
    public static Complex fromPolar(double r, double phi) {
        return new Complex(r*Math.cos(phi), r*Math.sin(phi));
    }

    
    /**
     * Returns the real part of the complex number rounded to an integer.
     */
    public int intValue() {
        return (int)Math.round(real);
    }

    
    /**
     * Returns the real part of the complex number rounded to a long.
     */
    public long longValue() {
        return Math.round(real);
    }

    
    /**
     * Returns the real part of the complex number as a float.
     */
    public float floatValue() {
        return (float)real;
    }

    
    /**
     * Returns the real part of the complex number as a double.
     */
    public double doubleValue() {
        return real;
    }

    
    /**
     * Returns the complex number 1+i0.
     */
    static public Complex getOne() {
        return new Complex(1.0);
    }

    
    /**
     * Returns the complex number 0+i0.
     */
    static public Complex getZero() {
        return new Complex(0.0);
    }


    /**
     * Returns the sum of this number and <code>c</code>.
     */
    public Complex sum(Complex c) {
        return new Complex(real+c.real, imag+c.imag);
    }


    /**
     * Returns the sum of this number and <code>x</code>.
     */
    public Complex sum(double x) {
        return new Complex(real+x, imag);
    }


    /**
     * Adds the complex number <code>c</code> to this number.
     */
    public void add(Complex c) {
        real += c.real;
        imag += c.imag;
    }

    
    /**
     * Adds the real number <code>x</code> to this number.
     */
    public void add(double x) {
        real += x;
    }

    
    /**
     * Returns the difference of this number and <code>c</code>.
     */
    public Complex difference(Complex c) {
        return new Complex(real-c.real, imag-c.imag);
    }

    
    /**
     * Returns the difference of this number and <code>x</code>.
     */
    public Complex difference(double x) {
        return new Complex(real-x, imag);
    }

    
    /**
     * Subtracts the complex number <code>c</code> from this number.
     */
    public void subtract(Complex c) {
        real -= c.real;
        imag -= c.imag;
    }

    
    /**
     * Returns the product of this number and <code>c</code>.
     */
    public Complex product(Complex c) {
        return new Complex(real*c.real-imag*c.imag, real*c.imag+imag*c.real);
    }

    
    /**
     * Returns the product of this number and the real number <code>x</code>.
     */
    public Complex product(double x) {
        return new Complex(x*real, x*imag);
    }

    
    /**
     * Multiplies this number with <code>c</code>.
     */
    public void multiply(Complex c) {
        double newr = real*c.real-imag*c.imag;
        double newi = real*c.imag+imag*c.real;
        real = newr;
        imag = newi;
    }
    
    
    /**
     * Returns the quotient of this number by <code>c</code>.
     */
    public Complex quotient(Complex c) {
        double d = c.real*c.real+c.imag*c.imag;
        double newr = (real*c.real+imag*c.imag)/d;
        double newi = (imag*c.real-real*c.imag)/d;        
        return new Complex(newr, newi);
    }

    
    /**
     * Returns the quotient of this number by <code>x</code>.
     */
    public Complex quotient(double x) {
        double newr = real/x;
        double newi = imag/x;        
        return new Complex(newr, newi);
    }

    
    /**
     * Divides this number by <code>c</code>.
     */
    public void divide(Complex c) {
        double d = c.real*c.real+c.imag*c.imag;
        double newr = (real*c.real+imag*c.imag)/d;
        double newi = (imag*c.real-real*c.imag)/d;
        real = newr;
        imag = newi;
    }

    
    /**
     * Divides this number by <code>x</code>.
     */
    public void divide(double x) {
        real = real/x;
        imag = imag/x;
    }

    
    /**
     * Returns the inverse of this number.
     */
    public Complex inverse() {
        double d = real*real+imag*imag;
        return new Complex(real/d, -imag/d);
    }

    
    /**
     * Inverts this number.
     */
    public void invert() {
        double d = real*real+imag*imag;
        real = real/d;
        imag = -imag/d;
    }

    
    /**
     * Returns the negative of this number.
     */
    public Complex negated() {
        return new Complex(-real, -imag);
    }

    
    /**
     * Negates this number.
     */
    public void negate() {
        real = -real;
        imag = -imag;
    }

    
    /**
     * Returns the conjugate of this number.
     */
    public Complex conjugated() {
        return new Complex(real, -imag);
    }

    
    /**
     * Conjugates this number.
     */
    public void conjugate() {
        imag = -imag;
    }


    /**
     * Returns true iff this number is 0+i0.
     */
    public boolean isZero() {
        return real == 0.0 && imag == 0.0;
    }

    
    /**
     * Returns true iff this number is 1+i0.
     */
    public boolean isOne() {
        return real == 1.0 && imag == 0.0;
    }

    
    /**
     * Returns the real part of this number.
     */
    public double getReal() {
        return real;
    }

    
    /**
     * Returns the imaginary part of this number.
     */
    public double getImag() {
        return imag;
    }

    
    /**
     * Returns true iff the imaginary part is zero.
     */
    public boolean isReal() {
        return imag == 0.0;
    }

    
    /**
     * Returns the absolute value of this number.
     */
    public double abs() {
        return Math.sqrt(real*real+imag*imag);
    }
    

    /**
     * Returns the argument of this number.
     */
    public double arg() {
        return Math.atan2(imag, real);
    }

    
    /**
     * Returns the square root of this number.
     */
    public Complex sqrt() {
        double m = Math.sqrt(abs());
        double a = arg()/2;
        return fromPolar(m, a);
    }
    
    
    /**
     * Returns the natural logarithm of this number.
     */
    public Complex log() {
        double a = Math.log(abs());
        double b = arg();
        return new Complex(a, b);
    }
    
    
    /**
     * Returns the exponential of this number.
     */
    public Complex exp() {
        double r = Math.exp(getReal());
        double i = getImag();
        return fromPolar(r, i);
    }
    
    
    /**
     * Returns this number raised to the power <code>c</code>.
     */
    public Complex expt(Complex c) {
        double a1 = abs();
        double b1 = arg();
        Complex c1 = c.product(Math.log(a1)).exp();
        Complex c2 = new Complex(0, b1).product(c).exp();
        c1.multiply(c2);
        return c1;
    }
    
    
    /**
     * Returns the sine of this number.
     */
    public Complex sin() {       
        Complex a = new Complex(0, 1);
        a.multiply(this);
        a = a.exp();

        Complex b = new Complex(0, -1);
        b.multiply(this);
        b = b.exp();
        
        a.subtract(b);
        a.divide(new Complex(0, 2));
        
        return a;
    }
    

    /**
     * Returns the cosine of this number.
     */
    public Complex cos() {       
        Complex a = new Complex(0, 1);
        a.multiply(this);
        a = a.exp();

        Complex b = new Complex(0, -1);
        b.multiply(this);
        b = b.exp();
        
        a.add(b);
        a.divide(new Complex(2, 0));
        
        return a;
    }

    
    /**
     * Returns the tangent of this number.
     */
    public Complex tan() {       
        Complex a = new Complex(0, 2);
        a.multiply(this);
        a = a.exp();        

        Complex b = new Complex(a);
        
        a.real--;
        b.real++;
        
        b.multiply(new Complex(0, 1));
        a.divide(b);
        
        return a;
    }

    
    /**
     * Returns the arcsine of this number.
     */
    public Complex asin() {
        Complex a = this.product(this);
        a.negate();
        a.add(1);
        a = a.sqrt();
        a.add(this.product(new Complex(0, 1)));
        a = a.log();
        a.multiply(new Complex(0,1));
        a.negate();
        return a;
    }
    
    
    /**
     * Returns the arccosine of this number.
     */
    public Complex acos() {
        Complex a = this.product(this);
        a.negate();
        a.add(1);
        a = a.sqrt();
        a.add(this.product(new Complex(0, 1)));
        a = a.log();
        a.multiply(new Complex(0,1));
        a.add(Math.PI/2);
        return a;
    }
    
    
    /**
     * Returns the arctangent of this number and <code>c</code>.
     */
    public Complex atan(Complex c) {
        // TODO: not yet implemented
        return null;
    }
    
    
    /**
     * Returns hashcode for this number.
     */
    public int hashCode() {
        long h = Double.doubleToRawLongBits(real);
        h = h*17+Double.doubleToLongBits(imag);
        return (int)((h >> 32)^h);
    }

    
    /**
     * Returns the string representation of this number.
     */
    public String toString() {
        return real+"+i*"+imag;
    }
    
    
    /**
     * Returns the complex number with the string representation <code>s</code>
     */
    public static Complex parseComplex(String s) {
        if (s.length() == 0) {
            throw new NumberFormatException("Empty string makes no complex");
        }
        else if (s.charAt(0) == '(' && s.charAt(s.length()-1) == ')') {
            s = s.substring(1, s.length()-1);
        }
        return new Complex(s);
    }
    
    
    public Object clone() {
        return new Complex(real, imag);
    }

    
    /**
     * Compares this number with <code>object</code>.
     * Since complex numbers are not linearly ordered, the comparison
     * is lexicographic.
     */
    public int compareTo(Complex c) {
        if (real < c.real) {
            return -1;
        }
        else if (real > c.real) {
            return 1;
        }
        else if (imag < c.imag) {
            return -1;
        }
        else if (imag > c.imag) {
            return 1;
        }
        else {
            return 0;
        }
    }


    private double real;
    private double imag;
}

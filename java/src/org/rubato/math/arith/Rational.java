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

import org.rubato.util.TextUtils;

/**
 * Rational number arithmetic.
 * 
 * @author Gérard Milmeister
 */
@SuppressWarnings("nls")
public final class Rational extends Number implements Comparable<Rational>, Cloneable {   

    private final static int INITIAL_DEFAULT_QUANT = 128*3*5;
    private static int DEFAULT_QUANT = 128*3*5;
    
    /**
     * Creates a new rational number <code>n</code>/1.
     */
    public Rational(int n) {
        num = n;
        denom = 1;
    }


    /**
     * Creates a new rational <code>n</code>/<code>d</code>.
     * @param n is the numerator
     * @param d is the denominator
     */
    public Rational(int n, int d) {
        num = n;
        denom = d;
        reduce();
    }


    /**
     * Creates a new rational from <code>r</code>.
     * Copy constructor.
     */
    public Rational(Rational r) {
        num = r.num;
        denom = r.denom;
    }


    /**
     * Creates a new rational from a double <code>d</code>.
     * Converts <code>d</code> with quantization <code>quant</code>.
     */
    public Rational(double d, int quant) {
        num = (int)Math.round(d*quant);
        denom = quant;
        reduce();
    }


    /**
     * Creates a new rational from a double <code>d</code>.
     * Converts <code>d</code> with default quantization.
     */
    public Rational(double d) {
        num = (int)Math.round(d*DEFAULT_QUANT);
        denom = DEFAULT_QUANT;
        reduce();
    }


    /**
     * Creates a new rational from another rational using given quantization.
     * A new rational <code>x</code>/<code>quant</code> is created
     * such that <code>x</code>/<code>quant</code> <= <code>r</code> < (<code>x</code>+1)/<code>quant</code>.
     */
    public Rational(Rational r, int quant) {
        int qu = quant;
        if (qu < 0) qu = -qu;
        int numerator = r.getNumerator();
        int denominator = r.getDenominator();
        int sign = 1; 
        if (numerator < 0) {
            sign = -1;
            numerator = -numerator;
        }
        long q = (numerator*qu)/denominator;
        long s = (numerator*qu)%denominator;
        if (s*2 > denominator) q++;
        num = sign*(int)q;
        denom = quant;
        reduce();
    }


    /**
     * Creates a new rational from its string representation <code>s</code>.
     * @param s has the format "n/d" where n and d are integers
     */
    public Rational(String s) {
        int divpos = s.indexOf("/");
        if (divpos > -1) {
            try {
                num = Integer.parseInt(s.substring(0, divpos));
                denom = Integer.parseInt(s.substring(divpos + 1));
                reduce();
            }
            catch (Exception e) {
                throw new NumberFormatException();
            }
        }
        else {
            try {
                num = Integer.parseInt(s);
                denom = 1;
            }
            catch (Exception e) {
                throw new NumberFormatException();
            }
        }
    }


    /**
     * Returns the rational 0/1.
     */
    static public Rational getZero() {
        return new Rational(0);
    }


    /**
     * Returns the rational 1/1.
     */
    static public Rational getOne() {
        return new Rational(1);
    }


    public boolean equals(Object object) {
        if (object instanceof Rational) {
            return equals((Rational)object);
        }
        return false;
    }


    public boolean equals(Rational r) {
        if (r.num == num && r.denom == denom) {
            return true;
        }
        return false;
    }


    /**
     * Returns true iff this number is 0/1.
     */
    public boolean isZero() {
        return num == 0;
    }


    /**
     * Returns true iff this number is 1/1.
     */
    public boolean isOne() {
        return num == 1 && denom == 1;
    }


    public int compareTo(Rational r) {
        int a, b;
        a = num * r.getDenominator();
        b = r.getNumerator() * denom;
        return a-b;
    }


    /**
     * Returns the sum of this number and <code>r</code>.
     */
    public Rational sum(Rational r) {
        return new Rational(num * r.denom + denom * r.num, denom * r.denom);
    }


    /**
     * Returns the sum of this number and the integer <code>n</code>.
     */
    public Rational sum(int n) {
        return new Rational(num + n * denom, denom);
    }


    /**
     * Adds <code>r</code> to this number.
     */
    public void add(Rational r) {
        num = num * r.denom + denom * r.num;
        denom = denom * r.denom;
        reduce();
    }


    /**
     * Adds the integer <code>n</code> to this number.
     */
    public void add(int n) {
        num = num + n * denom;
    }


    /**
     * Returns the difference of this number and <code>r</code>.
     */
    public Rational difference(Rational r) {
        return new Rational(num * r.denom - denom * r.num, denom * r.denom);
    }


    /**
     * Returns the difference of this number and the integer <code>n</code>.
     */
    public Rational difference(int n) {
        return new Rational(num - n * denom, denom);
    }


    /**
     * Subtracts <code>r</code> from this number.
     */
    public void subtract(Rational r) {
        num = num * r.denom - denom * r.num;
        denom = denom * r.denom;
        reduce();
    }


    /**
     * Subtracts the integer <code>n</code> from this number.
     */
    public void subtract(int n) {
        num = num - n * denom;
    }


    /**
     * Returns the product of this number and <code>r</code>.
     */
    public Rational product(Rational r) {
        int g = NumberTheory.gcd(r.denom, num) * NumberTheory.gcd(r.num, denom);
        Rational newr = new Rational();
        newr.num = num * r.num / g;
        newr.denom = denom * r.denom / g;
        return newr;
    }


    /**
     * Returns the product of this number and the integer <code>n</code>.
     */
    public Rational product(int n) {
        int g = NumberTheory.gcd(n, denom);
        Rational r = new Rational();
        r.num = num * n / g;
        r.denom = denom / g;
        return r;
    }


    /**
     * Multiplies this number with <code>r</code>.
     */
    public void multiply(Rational r) {
        int g = NumberTheory.gcd(r.denom, num) * NumberTheory.gcd(r.num, denom);
        num = num * r.num / g;
        denom = denom * r.denom / g;
    }


    /**
     * Multiplies this number with the integer <code>n</code>.
     */
    public void multiply(int n) {
        int g = NumberTheory.gcd(n, denom);
        num = num * n / g;
        denom = denom / g;
    }


    /**
     * Returns the quotient of this number and <code>r</code>.
     */
    public Rational quotient(Rational r) {
        int g = NumberTheory.gcd(r.denom, denom) * NumberTheory.gcd(r.num, num);
        int n = num * r.denom / g;
        int d = denom * r.num / g;
        if (d == 0) {
            throw new ArithmeticException();
        }
        if (d < 0) {
            n = -n;
            d = -d;
        }
        Rational newr = new Rational();
        newr.num = n;
        newr.denom = d;
        return newr;
    }


    /**
     * Returns the quotient of this number and the integer <code>n</code>.
     */
    public Rational quotient(int n) {
        int g = NumberTheory.gcd(n, num);
        int m = num / g;
        int d = denom * (n / g);
        if (d == 0) {
            throw new ArithmeticException();
        }
        if (d < 0) {
            m = -m;
            d = -d;
        }
        Rational r = new Rational();
        r.num = m;
        r.denom = d;
        return r;
    }


    /**
     * Divides this number by <code>r</code>.
     */
    public void divide(Rational r) {
        int g = NumberTheory.gcd(r.denom, denom) * NumberTheory.gcd(r.num, num);
        num = num * r.denom / g;
        System.out.println(denom);
        denom = denom * r.num / g;
        if (denom == 0) {
            throw new ArithmeticException();
        }
        if (denom < 0) {
            num = -num;
            denom = -denom;
        }
    }


    /**
     * Divides this number by the integer <code>n</code>.
     */
    public void divide(int x) {
        int g = NumberTheory.gcd(x, num);
        num = num / g;
        denom = denom * x / g;
        if (denom == 0) {
            throw new ArithmeticException();
        }
        if (denom < 0) {
            num = -num;
            denom = -denom;
        }
    }


    /**
     * Returns the inverse of this rational.
     */
    public Rational inverse() {
        Rational r = new Rational();
        if (num == 0) {
            throw new ArithmeticException();
        }
        if (num < 0) {
            r.num = -denom;
            r.denom = -num;
        }
        else {
            r.num = denom;
            r.denom = num;
        }
        return r;
    }


    /**
     * Inverts this rational.
     */
    public void invert() {
        int t;
        if (num == 0) {
            throw new ArithmeticException();
        }
        if (num < 0) {
            t = num;
            num = -denom;
            denom = -t;
        }
        else {
            t = num;
            num = denom;
            denom = t;
        }
    }


    /**
     * Returns -this number.
     */
    public Rational negated() {
        Rational r = new Rational();
        r.num = -num;
        r.denom = denom;
        return r;
    }


    /**
     * Negates this rational.
     */
    public void negate() {
        num = -num;
    }


    /**
     * Returns the numerator of this rational.
     */
    public int getNumerator() {
        return num;
    }

    
    /**
     * Returns the denominator of this rational.
     */
    public int getDenominator() {
        return denom;
    }

    
    
    /**
     * Returns the absolute value of this rational.
     */
    public Rational abs() {
        Rational res = new Rational();
        if (num < 0) {
            res.num = -num;
        }
        else {
            res.num = num;
        }
        res.denom = denom;
        return res;
    }
    
    
    /**
     * Converts this rational to a double.
     */
    public double doubleValue() {
        return (double)num/(double)denom;
    }


    /**
     * Converts this rational to a float.
     */
    public float floatValue() {
        return (float)doubleValue();
    }


    public long longValue() {
        return Math.round(doubleValue());
    }

    
    public int intValue() {
        return (int)Math.round(doubleValue());
    }

    
    /**
     * Returns the rational correspoding to its string representation <code>s</code>.
     */
    public static Rational parseRational(String s) {
        return new Rational(TextUtils.unparenthesize(s));
    }

    
    public boolean isIntegral() {
        return denom == 1;
    }
    
    /**
     * Returns the string representation of this rational.
     */
    public String toString() {
        if (denom == 1) {
            return ""+num;
        }
        else {            
            return num+"/"+denom;
        }
    }


    public Object clone() {
        Rational res = new Rational();
        res.num = num;
        res.denom = denom;
        return res;
    }


    /**
     * Returns the hashcode for this rational.
     */
    public int hashCode() {
        return num ^ denom;
    }


    /**
     * Sets the default quantization to the given value.
     * The quantization is taken to be the absolute value of <code>quant</code>.
     * If <code>quant</code> is 0, the initial default value is
     * used. 
     */
    public static void setDefaultQuantization(int quant) {
        int q = Math.abs(quant);
        if (q == 0) {
            DEFAULT_QUANT = INITIAL_DEFAULT_QUANT;
        }
        else {
            DEFAULT_QUANT = q;
        }
    }
    

    /**
     * Resets the default quantization to the initial value.
     */
    public static void resetDefaultQuantization() {
        DEFAULT_QUANT = INITIAL_DEFAULT_QUANT;
    }
    
    
    /**
     * Returns the current default quantization;
     */
    public static int getDefaultQuantization() {
        return DEFAULT_QUANT;
    }
    
    
    /**
     * Reduces numerator and denominator to least values
     * and ensure that denominator is always positive.
     */
    private void reduce() {
        int g = NumberTheory.gcd(num, denom);
        num = num / g;
        denom = denom / g;
        if (denom < 0) {
            denom = -denom;
            num = -num;
        }
    }

    
    /**
     * Private constructor for internal purposes only.
     */
    private Rational() { /* do nothing */ }

    
    private int num;
    private int denom;
}

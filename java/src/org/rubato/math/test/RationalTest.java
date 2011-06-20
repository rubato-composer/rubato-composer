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

package org.rubato.math.test;

import junit.framework.TestCase;

import org.rubato.math.arith.Rational;

public class RationalTest extends TestCase {
    
    Rational r1;
    Rational r2;
    Rational r;

    public RationalTest(String name) {
        super(name);
    }

    protected void setUp() {
        r1 = new Rational(2*3*5, 7*11);
        r2 = new Rational(-3*7, 5*11*13);
    }

    public void testRationalEquals() {
        Rational x = new Rational(-15, 35);
        Rational y = new Rational(6, -14);
        assertTrue(x.equals(y));
    } 

    public void testRationalSum() {
        r = r1.sum(r2);
        assertEquals(new Rational(1803, 5005), r);
        r = r1.sum(5);
        assertEquals(new Rational(415, 77), r);
    }

    public void testRationalAdd() {
        r = new Rational(r1);
        r.add(r2);
        assertEquals(new Rational(1803, 5005), r);
        r = new Rational(r1);
        r.add(5);
        assertEquals(new Rational(415, 77), r);
    }

    public void testRationalDifference() {
        r = r1.difference(r2);
        assertEquals(new Rational(2097, 5005), r);
        r = r1.difference(5);
        assertEquals(new Rational(-355, 77), r);
    }

    public void testRationalSubtract() {
        r = new Rational(r1);
        r.subtract(r2);
        assertEquals(new Rational(2097, 5005), r);
        r = new Rational(r1);
        r.subtract(5);
        assertEquals(new Rational(-355, 77), r);
    }

    public void testRationalProduct() {
        r = r1.product(r2);
        assertEquals(new Rational(-18, 1573), r);
        r = r1.product(-5);
        assertEquals(new Rational(-150, 77), r);
    }

    public void testRationalMultiply() {
        r = new Rational(r1);
        r.multiply(r2);
        assertEquals(new Rational(-18, 1573), r);
        r = new Rational(r1);
        r.multiply(-5);
        assertEquals(new Rational(-150, 77), r);
    }

    public void testRationalQuotient() {
        r = r1.quotient(r2);
        assertEquals(new Rational(-650, 49), r);
        r = r1.quotient(-5);
        assertEquals(new Rational(-6, 77), r);
        try {
            r = r1.quotient(0);
            fail("Should raise an ArithmeticException");
        } catch (ArithmeticException e) { /* continue */ }
    }

    public void testRationalDivide() {
        r = new Rational(r1);
        r.divide(r2);
        assertEquals(new Rational(-650, 49), r);
        r = new Rational(r1);
        r.divide(-5);
        assertEquals(new Rational(-6, 77), r);
        try {
            r = new Rational(r1);
            r.divide(0);
            fail("Should raise an ArithmeticException");
        } catch (ArithmeticException e) { /* continue */ }
    }

    public void testRationalInverse() {
        r = r1.inverse();
        assertEquals(new Rational(7*11, 2*3*5), r);
        r = r2.inverse();
        assertEquals(new Rational(-5*11*13, 3*7), r);
        r = new Rational(0, 5);
        try {
            r = r.inverse();
            fail("Should raise an ArithmeticException");
        } catch (ArithmeticException e) { /* continue */ }
    }

    public void testRationalInvert() {
        r = new Rational(r1);
        r.invert();
        assertEquals(new Rational(7*11, 2*3*5), r);
        r = new Rational(r2);
        r.invert();
        assertEquals(new Rational(-5*11*13, 3*7), r);
        r = new Rational(0, 5);
        try {
            r.invert();
            fail("Should raise an ArithmeticException");
        } catch (ArithmeticException e) { /* continue */ }		 
    }

    public void testRationalCompareTo() {
        int res;
        res = r1.compareTo(r2);
        assertTrue(res > 0);
        res = r2.compareTo(r1);
        assertTrue(res < 0);
        res = r1.compareTo(r1);
        assertEquals(0, res);
        res = r2.compareTo(r2);
        assertEquals(0, res);
        assertEquals(new Rational(0), Rational.getZero());
        assertEquals(new Rational(1), Rational.getOne());
    }

    public void testRationalParseRational() {
        r = Rational.parseRational("60/154");
        assertEquals(new Rational(30, 77), r);
        r = Rational.parseRational("-60/154");
        assertEquals(new Rational(-30, 77), r);
        r = Rational.parseRational("60/-154");
        assertEquals(new Rational(-30, 77), r);
    }

    public void testRationalQuant() {
        double d1;
        d1 = 47.125;
        assertEquals(new Rational(377, 8), new Rational(d1, 16));
        d1 = 47.324;
        assertEquals(new Rational(379, 8), new Rational(d1, 8));
        d1 = -47.125;
        assertEquals(new Rational(-377, 8), new Rational(d1, 16));
        d1 = -47.324;
        assertEquals(new Rational(-379, 8), new Rational(d1, 8));
    }

    public void testRationalToString() {
        String s = new Rational("60/-154").toString();
        assertEquals("-30/77", s);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(RationalTest.class);
    }
}

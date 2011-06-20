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

import org.rubato.math.arith.NumberTheory;
import org.rubato.math.arith.ZeroDivisorException;

public class NumTheoryTest extends TestCase {

    public NumTheoryTest(String name) {
        super(name);
    }

    public void testNumTheoryGcd() {
        int g;
        g = NumberTheory.gcd(10, 5);
        assertEquals(5, g);
        g = NumberTheory.gcd(5, 10);
        assertEquals(5, g);
        g = NumberTheory.gcd(0, 10);
        assertEquals(10, g);
        g = NumberTheory.gcd(10, 0);
        assertEquals(10, g);
        g = NumberTheory.gcd(0, 0);
        assertEquals(0, g);
        g = NumberTheory.gcd(1155, 2275);
        assertEquals(35, g);
        g = NumberTheory.gcd(2275, 1155);
        assertEquals(35, g);
        g = NumberTheory.gcd(3770, 2387);
        assertEquals(1, g);
        g = NumberTheory.gcd(2387, 3770);
        assertEquals(1, g);
        g = NumberTheory.gcd(1155, -2275);
        assertEquals(35, g);
        g = NumberTheory.gcd(-1155, 2275);
        assertEquals(35, g);
        g = NumberTheory.gcd(-1155, -2275);
        assertEquals(35, g);
    }

    public void testNumTheoryExgcd() {
        int g;
        int res[] = new int[2];
        g = NumberTheory.exgcd(30, 18, res);
        assertEquals(6, g);
        assertEquals(-1, res[0]);
        assertEquals(2, res[1]);
        g = NumberTheory.exgcd(18, 30, res);
        assertEquals(6, g);
        assertEquals(2, res[0]);
        assertEquals(-1, res[1]);
        g = NumberTheory.exgcd(0, 10, res);
        assertEquals(10, g);
        assertEquals(0, res[0]);
        assertEquals(1, res[1]);
        g = NumberTheory.exgcd(10, 0, res);
        assertEquals(10, g);
        assertEquals(1, res[0]);
        assertEquals(0, res[1]);
        g = NumberTheory.exgcd(3770, 2387, res);
        assertEquals(1, g);
        assertEquals(359, res[0]);
        assertEquals(-567, res[1]);
        g = NumberTheory.exgcd(-3770, 2387, res);
        assertEquals(1, g);
        assertEquals(-359, res[0]);
        assertEquals(-567, res[1]);
        g = NumberTheory.exgcd(3770, -2387, res);
        assertEquals(1, g);
        assertEquals(359, res[0]);
        assertEquals(567, res[1]);
        g = NumberTheory.exgcd(-3770, -2387, res);
        assertEquals(1, g);
        assertEquals(-359, res[0]);
        assertEquals(567, res[1]);
    }

    public void testNumTheoryInverseMod() {
        int q;
        q = NumberTheory.inverseMod(3, 7);
        assertEquals(5, q);
        q = NumberTheory.inverseMod(1365, 187);
        assertEquals(177, q);
        q = NumberTheory.inverseMod(187, 1365);
        assertEquals(73, q);
        q = NumberTheory.inverseMod(-1365, 187);
        assertEquals(10, q);
        try {
            q = NumberTheory.inverseMod(5, 15);
            fail("Should raise a ZeroDivisorException");
        } catch (ZeroDivisorException e) { /* continue */ }
        try {
            q = NumberTheory.inverseMod(5, 0);
            fail("Should raise a ZeroDivisorException");
        } catch (ZeroDivisorException e) { /* continue */ }
    }

    public void testNumTheoryDivideMod() {
        int q;
        q = NumberTheory.divideMod(3, 5, 7);
        assertEquals(2, q);
        q = NumberTheory.divideMod(11, 1365, 187);
        assertEquals(77, q);
        q = NumberTheory.divideMod(-11, 1365, 187);
        assertEquals(110, q);
        q = NumberTheory.divideMod(11, -1365, 187);
        assertEquals(110, q);
        q = NumberTheory.divideMod(-11, -1365, 187);
        assertEquals(77, q);
        try {
            q = NumberTheory.divideMod(1365, 11, 187);
            fail("Should raise a ZeroDivisorException");
        } catch (ZeroDivisorException e) { /* continue */ }
        try {
            q = NumberTheory.divideMod(5, 0, 7);
            fail("Should raise a ZeroDivisorException");
        } catch (ZeroDivisorException e) { /* continue */ }	 
    }

    public void testNumTheoryPowMod() {
        int y;
        y = NumberTheory.powerMod(2, 7, 13);
        assertEquals(11, y);
        y = NumberTheory.powerMod(38, 1928982983, 281);
        assertEquals(216, y);
        y = NumberTheory.powerMod(-38, 1928982983, 281);
        assertEquals(65, y);
        y = NumberTheory.powerMod(38, -1928982983, 281);
        assertEquals(134, y);
        y = NumberTheory.powerMod(-38, -1928982983, 281);
        assertEquals(147, y);
        y = NumberTheory.powerMod(38, 0, 281);
        assertEquals(1, y);
        y = NumberTheory.powerMod(38, 1, 281);
        assertEquals(38, y);
        try {
            y = NumberTheory.powerMod(11, -1928982983, 187);
            fail("Should raise a ZeroDivisorException");
        } catch (ZeroDivisorException e) { /* continue */ }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(NumTheoryTest.class);
    }
}

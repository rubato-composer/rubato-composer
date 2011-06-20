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

/**
 * This is a generic pair class, where the
 * coordinate classes are specified using template arguments.
 * 
 * @author Gérard Milmeister
 */
public class Triple<A extends Comparable<A>,B extends Comparable<B>,C extends Comparable<C>>
    implements Comparable<Triple<A,B,C>> {

    /** First component */
    public A first;
    /** Second component */
    public B second;
    /** Third component */
    public C third;
    
    
    /**
     * Two triples are equal if their respective components are equal.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Triple) {
            return first.equals(((Triple<?,?,?>)obj).first) &&
                   second.equals(((Triple<?,?,?>)obj).second) &&
                   third.equals(((Triple<?,?,?>)obj).third);
        }
        return false;
    }
    
    
    /**
     * The comparison of triples is according to lexicographical order.
     */
    public int compareTo(Triple<A,B,C> triple) {
        int c = first.compareTo(triple.first);
        if (c == 0) {
            c = second.compareTo(triple.second);
            if (c == 0) {
                c = third.compareTo(triple.third);
            }
        }
        return c;
    }
    
    
    public String toString() {
        return "("+first.toString()+","+second.toString()+","+third.toString()+")";
    }

    
    public int hashCode() {
        return (first.hashCode()*41+second.hashCode())*41+third.hashCode();
    }
    
    
    /**
     * Creates a triple from the three arguments.
     */
    public static <A extends Comparable<A>,B extends Comparable<B>,C extends Comparable<C>>
    Triple<A,B,C> makeTriple(A a, B b, C c) {
        return new Triple<A,B,C>(a, b, c);
    }

    
    private Triple(A a, B b, C c) {
        first = a;
        second = b;
        third = c;
    }
}

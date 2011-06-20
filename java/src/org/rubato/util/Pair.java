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
 * This is a generic pair class, where the first and second
 * coordinate classes are specified using template arguments.
 * 
 * @author Gérard Milmeister
 */
public class Pair<A extends Comparable<A>,B extends Comparable<B>>
    implements Comparable<Pair<A,B>> {

    /** First component */
    public A first;
    /** Second component */
    public B second;

    
    /**
     * Creates a pair with first and second coordinates set to <code>null</code>.
     */
    public Pair() {
        first = null;
        second = null;
    }

    
    /**
     * Creates a pair with first coordinate set to <code>a</code> and
     * second coordinate set to <code>b</code>.
     */
    private Pair(A a, B b) {
        first = a;
        second = b;
    }

    
    /**
     * Two pairs are equal if their respective components are equal.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            return first.equals(((Pair<?,?>)obj).first) &&
                   second.equals(((Pair<?,?>)obj).second);
        }
        return false;
    }
    
    
    /**
     * The comparison of triples is according to lexicographical order.
     */
    public int compareTo(Pair<A,B> pair) {
        int c = first.compareTo(pair.first);
        if (c == 0) {
            c = second.compareTo(pair.second);
        }
        return c;
    }
    
    
    public Pair<A,B> copy() {
        return new Pair<A,B>(first, second);
    }
    
    
    public String toString() {
        return "("+first.toString()+","+second.toString()+")";
    }

    
    public int hashCode() {
        return first.hashCode()*41+second.hashCode();
    }
    
    
    public static <A extends Comparable<A>,B extends Comparable<B>>
    Pair<A,B> makePair(A a, B b) {
        return new Pair<A,B>(a, b);
    }
}

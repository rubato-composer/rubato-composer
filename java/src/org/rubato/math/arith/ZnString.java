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

import static java.lang.Math.min;

import java.util.*;

import org.rubato.util.TextUtils;

/**
 * The ring of strings with integer factors mod <i>p</i>.
 */
@SuppressWarnings("nls")
public final class ZnString extends RingString {

    public ZnString(String word, int modulus) {
        this.modulus = modulus;
        dict = new HashMap<String,Object>();
        dict.put(word, getObjectOne());
    }

    
    public ZnString(String word, int factor, int modulus) {
        this.modulus = modulus;
        dict = new HashMap<String,Object>();
        int f = NumberTheory.mod(factor, modulus);
        if (f != 0) {
            add(word, f);
        }
    }
    

    public ZnString(String[] words, int[] factors, int modulus) {
        this.modulus = modulus;
        dict = new HashMap<String,Object>();
        int len = min(factors.length, words.length);
        for (int i = 0; i < len; i++) {
            int f = NumberTheory.mod(factors[i], modulus);
            if (f != 0) {
                add(words[i], f);
            }
        }
    }

    
    public ZnString(List<String> words, List<Integer> factors, int modulus) {
        this.modulus = modulus;
        dict = new HashMap<String,Object>();
        int len = Math.min(factors.size(), words.size());
        Iterator<String> witer = words.iterator();
        Iterator<Integer> fiter = factors.iterator();
        for (int i = 0; i < len; i++) {            
            String w = witer.next();
            int f = NumberTheory.mod(fiter.next(), modulus);
            if (f != 0) {
                add(w, f);
            }
        }
    }
    

    public ZnString(int modulus, Object ... objects) {
        for (int i = 0; i < objects.length; i += 2) {
            String w = (String)objects[i]; 
            int f = NumberTheory.mod(((Integer)objects[i+1]), modulus);
            if (f != 0) {
                add(w, f);
            }
        }
    }
    

    public ZnString(RingString rs, int modulus) {
        this.modulus = modulus;
        dict = new HashMap<String,Object>();
        for (String key : rs.dict.keySet()) {
            Object value = rs.dict.get(key);
            int i = NumberTheory.mod(ObjectInteger(value), modulus);
            if (i != 0) {
                add(key, i);
            }
        }
    }

    
    public ZnString(int i, int modulus) {
        this("", i, modulus);
    }

    
    public ZnString(Rational r, int modulus) {
        this("", r.intValue(), modulus);
    }

    
    public ZnString(double d, int modulus) {
        this("", (int)Math.round(d), modulus);
    }
    
    
    public ZnString(Complex c, int modulus) {
        this("", c.intValue(), modulus);
    }
    
    
    static public ZnString getZero(int modulus) {
        ZnString res = new ZnString(modulus);
        res.dict = new HashMap<String,Object>();
        return res;
    }

    
    static public ZnString getOne(int modulus) {
        return new ZnString("", modulus);
    }

    
    static public ZnString parseZnString(String string, int modulus) {
        String[] terms = TextUtils.split(string.trim(), '+');
        if (terms.length == 0) {
            return getOne(modulus);
        }
        
        LinkedList<String> words = new LinkedList<String>();
        LinkedList<Integer> factors = new LinkedList<Integer>();
        for (int i = 0; i < terms.length; i++) {
            String[] term = TextUtils.split(terms[i].trim(), '*');
            if (term.length < 2) {
                throw new NumberFormatException();
            }
            int f = Integer.parseInt(term[0]);
            String w = TextUtils.unquote(term[1]);
            factors.add(f);
            words.add(w);
        }
        
        return new ZnString(words, factors, modulus);
    }
    

    protected ZnString(int modulus) {
        this.modulus = modulus;
    }

    
    protected Object sum(Object x, Object y) {
        int ix = ((Integer)x).intValue();
        int iy = ((Integer)y).intValue();
        return Integer.valueOf(NumberTheory.mod(ix + iy, modulus));
    }

    
    protected Object difference(Object x, Object y) {
        int ix = ((Integer)x).intValue();
        int iy = ((Integer)y).intValue();
        return Integer.valueOf(NumberTheory.mod(ix - iy, modulus));
    }

    
    protected Object product(Object x, Object y) {
        int ix = ((Integer)x).intValue();
        int iy = ((Integer)y).intValue();
        return Integer.valueOf(NumberTheory.mod(ix * iy, modulus));
    }

    
    protected Object neg(Object x) {
        int ix = ((Integer)x).intValue();
        return Integer.valueOf(NumberTheory.mod(-ix, modulus));
    }

    
    protected boolean equals(Object x, Object y) {
        int ix = ((Integer)x).intValue();
        int iy = ((Integer)y).intValue();
        return ix == iy;
    }

    
    protected int compare(Object x, Object y) {
        int ix = ((Integer)x).intValue();
        int iy = ((Integer)y).intValue();
        return ix-iy;
    }

    
    protected Object getObjectOne() {
        return Integer.valueOf(1);
    }

    
    protected Object getObjectZero() {
        return Integer.valueOf(0);
    }

    
    protected boolean isObjectZero(Object x) {
        int ix = ((Integer)x).intValue();
        return ix == 0;
    }

    
    protected double ObjectToDouble(Object x) {
        return ((Integer)x).doubleValue();
    }

    
    public Object clone() {
        ZnString res = new ZnString(modulus);
        res.modulus = modulus;
        res.dict = new HashMap<String,Object>(dict);
        return res;
    }

    
    public int getModulus() {
        return modulus;
    }

    
    private int modulus;
}

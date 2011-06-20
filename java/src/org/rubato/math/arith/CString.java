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
 * The ring of strings with complex factors.
 */
@SuppressWarnings("nls")
public final class CString extends RingString {

    public CString(String word) {
        dict = new HashMap<String,Object>();
        dict.put(word, getObjectOne());
    }

    
    public CString(String word, Complex factor) {
        dict = new HashMap<String,Object>();
        if (!factor.isZero()) {
            add(word, factor);
        }
    }

    
    public CString(String[] words, Complex[] factors) {
        dict = new HashMap<String,Object>();
        int len = min(factors.length, words.length);
        for (int i = 0; i < len; i++) {
            if (!factors[i].isZero()) {
                add(words[i], factors[i]);
            }
        }
    }

    
    public CString(List<String> words, List<Complex> factors) {
        dict = new HashMap<String,Object>();
        int len = min(factors.size(), words.size());
        Iterator<String> witer = words.iterator();
        Iterator<Complex> fiter = factors.iterator();
        for (int i = 0; i < len; i++) {            
            String w = witer.next();
            Complex f = fiter.next();
            if (!f.isZero()) {
                add(w, i);
            }
        }
    }    

    
    public CString(Object ... objects) {
        for (int i = 0; i < objects.length; i += 2) {
            String w = (String)objects[i]; 
            Complex f = (Complex)objects[i+1];
            if (!f.isZero()) {
                add(w, f);
            }
        }
    }
    

    public CString(RingString rs) {
        if (rs instanceof CString) {
            dict = new HashMap<String,Object>(rs.dict);
        }
        else {
            dict = new HashMap<String,Object>();
            for (String key : rs.dict.keySet()) {
                Object value = rs.dict.get(key);
                Complex f = ObjectComplex(value);
                if (!f.isZero()) {
                    add(key, f);
                }
            }
        }
    }

    
    public CString(int i) {
        this("", new Complex(i));
    }

    
    public CString(Rational r) {
        this("", new Complex(r.doubleValue()));
    }

    
    public CString(double d) {
        this("", new Complex(d));
    }
    
    
    public CString(Complex c) {
        this("", c);
    }
    
    
    static public CString getZero() {
        CString res = new CString();
        res.dict = new HashMap<String,Object>();
        return res;
    }

    
    static public CString getOne() {
        return new CString("");
    }

    
    static public CString parseCString(String string) {
        String[] terms = TextUtils.split(string.trim(), '+');
        if (terms.length == 0) {
            return getOne();
        }
        
        LinkedList<String> words = new LinkedList<String>();
        LinkedList<Complex> factors = new LinkedList<Complex>();
        for (int i = 0; i < terms.length; i++) {
            String[] term = TextUtils.split(terms[i].trim(), '*');
            if (term.length < 2) {
                throw new NumberFormatException();
            }
            Complex f = Complex.parseComplex(term[0]);
            String w = TextUtils.unquote(term[1]);
            factors.add(f);
            words.add(w);
        }
        
        return new CString(words, factors);
    }
    

    protected Object sum(Object x, Object y) {
        return ((Complex)x).sum((Complex)y);
    }

    
    protected Object difference(Object x, Object y) {
        return ((Complex)x).difference((Complex)y);
    }

    
    protected Object product(Object x, Object y) {
        return ((Complex)x).product((Complex)y);
    }

    
    protected Object neg(Object x) {
        return ((Complex)x).negated();
    }

    
    protected boolean equals(Object x, Object y) {
        return x.equals(y);
    }

    
    protected int compare(Object x, Object y) {
        Complex rx = (Complex)x;
        Complex ry = (Complex)y;
        return rx.compareTo(ry);
    }

    
    protected Object getObjectOne() {
        return Complex.getOne();
    }

    
    protected Object getObjectZero() {
        return Complex.getZero();
    }

    
    protected boolean isObjectZero(Object x) {
        return ((Complex)x).isZero();
    }

    
    protected double ObjectToDouble(Object x) {
        return ((Complex)x).doubleValue();
    }

    
    public Object clone() {
        CString res = new CString();
        res.dict = new HashMap<String,Object>(dict);
        return res;
    }
}

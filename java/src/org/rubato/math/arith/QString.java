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
 * The ring of strings with rational factors.
 */
@SuppressWarnings("nls")
public final class QString extends RingString {

    public QString(String word) {
        dict = new HashMap<String,Object>();
        dict.put(word, getObjectOne());
    }
    

    public QString(String word, Rational factor) {
        dict = new HashMap<String,Object>();
        if (!factor.isZero()) {
            add(word, factor);
        }
    }
    

    public QString(String[] words, Rational[] factors) {
        dict = new HashMap<String,Object>();
        int len = min(factors.length, words.length);
        for (int i = 0; i < len; i++) {
            if (!factors[i].isZero()) {
                add(words[i], factors[i]);
            }
        }
    }
    

    public QString(List<String> words, List<Rational> factors) {
        dict = new HashMap<String,Object>();
        int len = min(factors.size(), words.size());
        Iterator<String> witer = words.iterator();
        Iterator<Rational> fiter = factors.iterator();
        for (int i = 0; i < len; i++) {            
            String w = witer.next();
            Rational f = fiter.next();
            if (!f.isZero()) {
                add(w, i);
            }
        }
    }
    

    public QString(Object ... objects) {
        for (int i = 0; i < objects.length; i += 2) {
            String w = (String)objects[i]; 
            Rational f = (Rational)objects[i+1];
            if (!f.isZero()) {
                add(w, f);
            }
        }
    }
    

    public QString(RingString rs) {
        if (rs instanceof QString) {
            dict = new HashMap<String,Object>(rs.dict);
        }
        else {
            dict = new HashMap<String,Object>();
            for (String key : rs.dict.keySet()) {
                Object value = rs.dict.get(key);
                Rational f = ObjectRational(value);
                if (!f.isZero()) {
                    add(key, f);
                }
            }
        }
    }
    

    public QString(int i) {
        this("", new Rational(i));
    }

    
    public QString(Rational r) {
        this("", r);
    }

    
    public QString(double d) {
        this("", new Rational(d));
    }
    
    
    public QString(Complex c) {
        this("", new Rational(c.doubleValue()));
    }
    
    
    static public QString getZero() {
        QString res = new QString();
        res.dict = new HashMap<String,Object>();
        return res;
    }
    

    static public QString getOne() {
        return new QString("");
    }
    
    
    static public QString parseQString(String string) {
        String[] terms = TextUtils.split(string.trim(), '+');
        if (terms.length == 0) {
            return getOne();
        }
        
        LinkedList<String> words = new LinkedList<String>();
        LinkedList<Rational> factors = new LinkedList<Rational>();
        for (int i = 0; i < terms.length; i++) {
            String[] term = TextUtils.split(terms[i].trim(), '*');
            if (term.length < 2) {
                throw new NumberFormatException();
            }
            Rational f = Rational.parseRational(term[0]);
            String w = TextUtils.unquote(term[1]);
            factors.add(f);
            words.add(w);
        }
        
        return new QString(words, factors);
    }
    

    protected QString() { /* do nothing */ }

    
    protected Object sum(Object x, Object y) {
        return ((Rational)x).sum((Rational)y);
    }
    

    protected Object difference(Object x, Object y) {
        return ((Rational)x).difference((Rational)y);
    }

    
    protected Object product(Object x, Object y) {
        return ((Rational)x).product((Rational)y);
    }

    
    protected Object neg(Object x) {
        return ((Rational)x).negated();
    }

    
    protected boolean equals(Object x, Object y) {
        return x.equals(y);
    }

    
    protected int compare(Object x, Object y) {
        Rational rx = (Rational)x;
        Rational ry = (Rational)y;
        return rx.compareTo(ry);
    }

    
    protected Object getObjectOne() {
        return new Rational(1);
    }

    
    protected Object getObjectZero() {
        return new Rational(0);
    }

    
    protected boolean isObjectZero(Object x) {
        return ((Rational)x).isZero();
    }

    
    protected double ObjectToDouble(Object x) {
        return ((Rational)x).doubleValue();
    }

    
    public Object clone() {
        QString res = new QString();
        res.dict = new HashMap<String,Object>(dict);
        return res;
    }
}

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

import static java.lang.Math.PI;
import static java.lang.Math.atan;

import java.io.Serializable;
import java.util.*;

import org.rubato.util.TextUtils;

/**
 * The ring of strings.
 * Strings are represented by sum(a_i*s_i), 
 * where the a_i are elements in a ring
 * and the s_i are character strings (<code>String</code>).
 */
@SuppressWarnings("nls")
public abstract class RingString implements Comparable<RingString>, Serializable, Cloneable {

    /**
     * Creates a new <code>RingString</code> instance.
     * This is the Zero RingString.
     */
    protected RingString() {
        dict = new HashMap<String,Object>();
    }

    /**
     * Creates a new <code>RingString</code> instance.
     * The resulting string is represented as factor*word.
     */
    protected RingString(String word, Object factor) {
        super();
        dict.put(word, factor);
    }

    /**
     * Creates a new <code>RingString</code> instance.
     * The resulting string is represented as sum(factors[i]*words[i]).
     */
    protected RingString(String[] words, Object[] factors) {
        super();
        int len;
        if (factors.length < words.length) {
            len = factors.length;
        }
        else {
            len = words.length;
        }
        for (int i = 0; i < len; i++) {
            add(words[i], factors[i]);
        }
    }

    /**
     * Creates a new <code>RingString</code> instance.
     * Copy constructor.
     */
    public RingString(RingString rs) {
        dict = new HashMap<String,Object>(rs.dict);
    }

    /**
     * Returns one character string in the RingString.
     * If the RingString has more than one term, the returned
     * string may be any one of these.
     * If the RingString is empty, null is returned.
     */
    public String getString() {
        Set<String> keys = dict.keySet();
        if (keys.size() > 0) {
            return keys.iterator().next();
        }
        return null;
    }

    /**
     * Returns a Set of all the strings in the
     * terms of the RingString.
     */
    public Set<String> getStrings() {
        return dict.keySet();
    }

    /**
     * Returns the factor for the character string.
     */
    public Object getFactorForString(String word) {
        if (dict.containsKey(word)) {
            return dict.get(word);
        }

        return getObjectZero();
    }

    /**
     * Returns the sum of this and <code>x</code>.
     * @return a new RingString object
     */
    public RingString sum(RingString x) {
        RingString res = (RingString)clone();
        res.add(x);
        return res;
    }

    /**
     * Add <code>x</code> to this.
     */
    public void add(RingString x) {
        for (String key : x.dict.keySet()) {
            Object factor = x.dict.get(key);
            add(key, factor);
        }
    }

    /**
     * Returns the difference of this and <code>x</code>.
     * @return a new RingString object.
     */
    public RingString difference(RingString x) {
        RingString res = (RingString)clone();
        res.subtract(x);
        return res;
    }

    /**
     * Subtract <code>x</code> from this.
     */
    public void subtract(RingString x) {
        for (String key : x.dict.keySet()) {
            Object factor = x.dict.get(key);
            subtract(key, factor);
        }
    }

    /**
     * Returns the product of this and <code>x</code>
     * @return a new RingString object
     */
    public RingString product(RingString x) {
        RingString res = (RingString)clone();
        res.multiply(x);
        return res;
    }

    /**
     * Multiply this by <code>x</code>.
     */
    public void multiply(RingString x) {
        Iterator<String> keys = x.dict.keySet().iterator();
        HashMap<String,Object> myDict = dict;
        dict = new HashMap<String,Object>();
        while (keys.hasNext()) {
            String key = keys.next();
            Object factor = x.dict.get(key);
            for (String myKey : myDict.keySet()) {
                Object myFactor = myDict.get(myKey);
                add(myKey + key, product(factor, myFactor));
            }
        }
    }

    /**
     * Returns this with all factors negated.
     */
    public RingString negated() {
        RingString res = (RingString)clone();
        res.negate();
        return res;
    }

    /**
     * Negate all factors in this.
     */
    public void negate() {
        for (String key : dict.keySet()) {
            Object newFactor = neg(dict.get(key));
            dict.put(key, newFactor);
        }
    }

    /**
     * Returns this scaled by <code>x</code>.
     * @return a new RingString object
     */
    public RingString scaled(Object x) {
        RingString res = (RingString)clone();
        res.scale(x);
        return res;
    }

    /**
     * Scale this by <code>x</code>.
     */
    public void scale(Object x) {
        for (String key : dict.keySet()) {
            Object newFactor = product(x, dict.get(key));
            if (isObjectZero(newFactor)) {
                dict.remove(key);
            }
            else {
                dict.put(key, newFactor);
            }
        }
    }

    /**
     * Two RingString's are equal if they have the same
     * character strings and the corresponding factors
     * of each are equal.
     */
    public boolean equals(Object object) {
        if (object instanceof RingString) {
            Map<String,Object> ht = ((RingString)object).dict;
            Set<String> obj_set = ht.keySet();
            Set<String> set = dict.keySet();
            if (obj_set.size() == set.size()) {
                for (String key : set) {
                    if (!equals(dict.get(key), ht.get(key))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public int compareTo(RingString rs) {
        Object[] obj_strs = rs.dict.keySet().toArray();
        Object[] strs = dict.keySet().toArray();
        int len;
        Arrays.sort(obj_strs);
        Arrays.sort(strs);
        if (obj_strs.length < strs.length) {
            len = obj_strs.length;
        }
        else {
            len = strs.length;
        }
        for (int i = 0; i < len; i++) {
            int str_comp = ((String)strs[i]).compareTo((String)obj_strs[i]);
            if (str_comp == 0) {
                int comp = compare(dict.get(strs[i]), rs.dict.get(obj_strs[i]));
                if (comp != 0) {
                    return comp;
                }
            }
            else if (str_comp != 0) {
                return str_comp;
            }
        }
        return (strs.length - obj_strs.length);
    }

    /**
     * Returns the sum of two factor objects.
     */
    protected abstract Object sum(Object x, Object y);

    /**
     * Returns the difference of two factor objects.
     */
    protected abstract Object difference(Object x, Object y);

    /**
     * Returns the product of two factor objects.
     */
    protected abstract Object product(Object x, Object y);

    /**
     * Returns the negative of a factor object.
     */
    protected abstract Object neg(Object x);

    /**
     * The equality operation of two factor objects.
     */
    protected abstract boolean equals(Object x, Object y);

    /**
     * Compare two factor objects, like compareTo.
     */
    protected abstract int compare(Object x, Object y);

    /**
     * Returns the unit factor object.
     */
    protected abstract Object getObjectOne();

    /**
     * Returns the zero factor object.
     */
    protected abstract Object getObjectZero();

    /**
     * True, if <code>x</code> is the zero factor object.
     */
    protected abstract boolean isObjectZero(Object x);

    /**
     * Returns the double value for factor object.
     */
    protected abstract double ObjectToDouble(Object x);
    
    protected static Integer ObjectInteger(Object x) {
        if (x instanceof Integer) {
            return (Integer)x;
        }
        else if (x instanceof Number) {
            return new Integer(((Number)x).intValue());
        }
        else {
            return null;
        }
    }

    protected static Double ObjectDouble(Object x) {
        if (x instanceof Double) {
            return (Double)x;
        }
        else if (x instanceof Number) {
            return Double.valueOf(((Number)x).doubleValue());
        }
        else {
            return null;
        }
    }

    protected static Rational ObjectRational(Object x) {
        if (x instanceof Rational) {
            return (Rational)x;
        }
        else if (x instanceof Integer) {
            return new Rational(((Integer)x).intValue());
        }
        else if (x instanceof Number) {
            return new Rational(((Number)x).doubleValue());
        }
        else {
            return null;
        }
    }

    protected static Complex ObjectComplex(Object x) {
        if (x instanceof Complex) {
            return (Complex)x;
        }
        else if (x instanceof Number) {
            return new Complex(((Number)x).doubleValue());
        }
        else {
            return null;
        }
    }

    public abstract Object clone();

    /**
     * Add string <code>word</code> with factor <code>factor</code> to this.
     */
    protected void add(String word, Object factor) {
        if (dict.containsKey(word)) {
            Object newFactor = sum(dict.get(word), factor);
            if (!isObjectZero(newFactor)) {
                dict.put(word, newFactor);
            }
            else {
                dict.remove(word);
            }
        }
        else if (!isObjectZero(factor)) {
            dict.put(word, factor);
        }
    }

    /**
     * Subtract string <code>word</code> with factor <code>factor</code> to this.
     */
    protected void subtract(String word, Object factor) {
        if (dict.containsKey(word)) {
            Object newFactor = difference(dict.get(word), factor);
            if (!isObjectZero(newFactor)) {
                dict.put(word, newFactor);
            }
            else {
                dict.remove(word);
            }
        }
        else if (!isObjectZero(factor)) {
            dict.put(word, factor);
        }
    }
    
    public int hashCode() {
        return dict.hashCode();
    }
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder(dict.size()*10);
        buf.append("[");

        String word;
        Object factor;
        Iterator<String> keys = dict.keySet().iterator();
        if (keys.hasNext()) {
            word = keys.next();
            factor = dict.get(word);
            buf.append(factor);
            buf.append("*");
            buf.append("\"");
            buf.append(TextUtils.escape(word));
            buf.append("\"");
            while (keys.hasNext()) {
                word = keys.next();
                factor = dict.get(word);
                buf.append("+");
                buf.append(factor);
                buf.append("*");
                buf.append("\"");
                buf.append(TextUtils.escape(word));
                buf.append("\"");
            }
        }
        buf.append("]");
        return buf.toString();
    }    
    
    public String stringRep() {
    	String word;
    	Object factor;
    	StringBuilder buf = new StringBuilder();
    	Set<String> keyset = dict.keySet();
    	if (keyset.isEmpty()) return "Null";
        Iterator<String> keys = keyset.iterator();
        if (keys.hasNext()) {
            word = keys.next();
            factor = dict.get(word);
            buf.append(factor);
            buf.append("*");
            buf.append("\"");
            buf.append(TextUtils.escape(word));
            buf.append("\"");
            while (keys.hasNext()) {
                word = keys.next();
                factor = dict.get(word);
                buf.append("+");
                buf.append(factor);
                buf.append("*");
                buf.append("\"");
                buf.append(TextUtils.escape(word));
                buf.append("\"");
            }
        }
        return buf.toString();
    }

    public static double StringToDouble(String s) {
        double sum = 0.0;
        double oneByAscii = 1.0 / 256.0;
        double factor = oneByAscii;
        for (int i = 0; i < s.length(); i++) {
            sum += factor * Character.getNumericValue(s.charAt(i));
            factor *= oneByAscii;
        }
        return sum;
    }

    /*
     * Implementation of RingString Folding.
     */

    class Word implements Comparable<Word> {

        public Word(String s, double f) {
            word = s;
            word_value = StringToDouble(s);
            word_factor = f;
        }

        public int compareTo(Word x) {
            if (word_value < x.word_value) {
                return -1;
            }
            else if (word_value > x.word_value) {
                return 1;
            }
            else {
                if (word_factor < x.word_factor) {
                    return -1;
                }
                else if (word_factor > x.word_factor) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        }

        public String toString() {
            return "["+word+","+word_value+","+word_factor+"]";
        }

        String word;
        double word_value;
        double word_factor;
        double word_low;
        double word_high;
        double fold_value;
        double next_fold;
    }

    private Word[] wordToArray() {
        Word res[] = new Word[dict.size()];
        String word;
        Object factor;
        int i = 0;
        Iterator<String> keys = dict.keySet().iterator();
        while (keys.hasNext()) {
            word = keys.next();
            factor = dict.get(word);
            res[i++] = new Word(word, ObjectToDouble(factor));
        }
        Arrays.sort(res);
        return res;
    }

    public static double[] fold(RingString[] elements) {
        Word[][] strings = new Word[elements.length][];
        int nr_words = 0;
        for (int i = 0; i < elements.length; i++) {
            strings[i] = elements[i].wordToArray();
            nr_words += strings[i].length;
        }

        // All words into a sorted array
        Word[] words = new Word[nr_words];
        int i = 0;
        for (int j = 0; j < elements.length; j++) {
            for (int k = 0; k < strings[j].length; k++) {
                words[i] = strings[j][k];
                i++;
            }
        }
        Arrays.sort(words);

        // Special case: all words are identical
        if (words[0].word_value == words[words.length - 1].word_value) {
            double l = words[0].word_value - 1.0 / 2;
            double h = words[0].word_value + 1.0 / 2;
            for (int j = 0; j < words.length; j++) {
                words[j].word_low = l;
                words[j].word_high = h;
            }
            computeOneFold(words);
        }
        else {
            // General case
            computeHighLow(words);
            computeOneFold(words);
            computeNextFold(words);
        }

        double[] res = new double[elements.length];
        for (int j = 0; j < res.length; j++) {
            res[j] = computeWordFold(strings[j]);
        }

        return res;
    }

    private static void computeHighLow(Word[] words) {
        for (int i = 0; i < words.length; i++) {
            words[i].word_low = (words[i].word_value + searchLow(words, i)) / 2;
            words[i].word_high = (words[i].word_value + searchHigh(words, i)) / 2;
        }
    }

    private static double searchLow(Word[] words, int i) {
        int j = i - 1;
        while (j >= 0 && words[j].word_value == words[i].word_value)
            j--;
        if (j < 0)
            return words[i].word_value + words[i].word_value - searchHigh(words, i);
        return words[j].word_value;
    }

    private static double searchHigh(Word[] words, int i) {
        int j = i + 1;
        while (j < words.length && words[j].word_value == words[i].word_value)
            j++;
        if (j == words.length)
            return words[i].word_value + words[i].word_value - searchLow(words, i);
        return words[j].word_value;
    }

    private static void computeOneFold(Word[] words) {
        for (int i = 0; i < words.length; i++) {
            //	    words[i].fold_value = Folding.foldElement(words[i].word_value,
            //						      words[i].word_factor,
            //						      words[i].word_low,
            //						      words[i].word_high);
            words[i].fold_value =
                simpleArcTan(words[i].word_factor, words[i].word_low, words[i].word_high);
        }
    }

    private static void computeNextFold(Word[] words) {
        double last_fold_value = words[words.length - 1].fold_value;
        words[words.length - 1].next_fold = 0;
        for (int i = words.length - 2; i >= 0; i--) {
            if (words[i].fold_value == last_fold_value) {
                words[i].next_fold = words[i + 1].next_fold;
            }
            else {
                words[i].next_fold = words[i + 1].fold_value;
                last_fold_value = words[i].fold_value;
            }
        }
    }

    private static double computeWordFold(Word[] string) {
        int len = string.length;
        double res = 0.0;
        if (len > 0) {
            res = string[len - 1].fold_value;
            for (int i = len - 2; i >= 0; i--) {
                res = simpleArcTan(res, string[i].fold_value, string[i].next_fold);
            }
        }
        return res;
    }

    private static double simpleArcTan(double x, double a, double b) {
        double v = atan(x);
        return ((b - a) / PI) * v + (a + b) / 2;
    }

    protected HashMap<String,Object> dict;
}

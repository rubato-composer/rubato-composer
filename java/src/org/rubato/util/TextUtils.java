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

import java.util.ArrayList;

/**
 * Static utility functions for text handling.
 * 
 * @author Gérard Milmeister
 */
public final class TextUtils {

    /**
     * Returns a string generated from a pattern <code>string</code>,
     * where for all <i>n</i>, all occurrences of %n and %nn
     * are replaced by <i>n</i>-th argument in the <code>objects</code> list.
     * %n is replaced by the the string representation of the object,
     * %%n is equivalent to \"%n\".
     */
    public static String replaceStrings(String string, Object ... objects) {
        for (int i = objects.length; i > 0; i--) {
            string = string.replaceAll("%%"+i, "\""+objects[i-1]+"\"");
        }
        for (int i = objects.length; i > 0; i--) {
            string = string.replaceAll("%"+i, objects[i-1].toString());
        }
        return string;
    }
    
    
    /**
     * Escapes all characters as needed in the given string.
     */
    public static String escape(String string) {
        return string.replaceAll("\"", "\\\\\"");
    }

    
    /**
     * Unescapes all escaped characters in the given string.
     */
    public static String unescape(String string) {
        return string.replaceAll("\\\\\"", "\"");
    }
    
    
    /**
     * Surrounds the given string with double quotes (").
     */
    public static String quote(String string) {
        return "\""+string+"\"";
    }
    

    /**
     * Removes the double quotes at the beginning and the
     * end of the given string.
     */
    public static String unquote(String string) {
        // warning: not entirely correct
        if (string.length() >= 2) {
            if (string.charAt(0) == '\"') {
                if (string.charAt(string.length()-1) == '\"') {
                    if (string.charAt(string.length()-2) != '\\') {
                        return string.substring(1, string.length()-1);
                    }
                }
            }
        }
        return string;
    }

    
    /**
     * Splits the given string into its parts that are separated
     * by the character <code>c</code>.
     */
    public static String[] split(String string, char c) {
        boolean instring = false;
        ArrayList<String> list = new ArrayList<String>();
        char[] cs = string.toCharArray();
        StringBuilder buf = new StringBuilder();
        int i = 0;
        while (i < cs.length) {
            if (cs[i] == c && !instring) {
                list.add(buf.toString());
                buf = new StringBuilder();
                i++;
            }
            else if (cs[i] == '\\') {
                i++;
                if (i < cs.length) {
                    char ci; 
                    if (cs[i] == 'n') {
                        ci = '\n';
                    }
                    else {
                        ci = cs[i];
                    }
                    buf.append(ci);
                    i++;
                }
            }
            else {
                if (cs[i] == '"') { instring = !instring; }
                buf.append(cs[i]);
                i++;
            }
        }
        list.add(buf.toString());
        return list.toArray(new String[0]);
    }
    
    
    /**
     * Adds parenthesizes to the string.
     */
    public static String parenthesize(String s) {
        return "("+s+")";
    }
    

    /**
     * Strips any paired parentheses from around the string.
     * This only works for correctly balanced expressions.
     */
    public static String unparenthesize(String s) {        
        int i = 0;
        int j = s.length()-1;
        s = s.trim();
        if (s.length() > 1) {
            while (s.charAt(i) == '(' && s.charAt(j) == ')' && paren_correct(s, i+1, j-1)) {
                i++;
                j--;
            }
            s = s.substring(i, j+1);
        }
        return s;
    }

    
    private static boolean paren_correct(String s, int i, int j) {
        int n = 0;
        for (int k = i; k <= j; k++) {
            if (s.charAt(k) == '(') { n++; }
            if (s.charAt(k) == ')') { n--; }
            if (n < 0) { return false; }
        }
        return true;
    }
}

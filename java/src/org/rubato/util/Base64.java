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
 * The class converts an array of integers to base-64 string
 * representation. This is useful for handling binary objects
 * stricly with the limits of ASCII.
 * 
 * @author Gérard Milmeister
 */
public final class Base64 {

    /**
     * Encodes the integer array <code>a</code> as a base-64
     * string.
     */
    public static String encodeIntArray(int[] a) {
        int len = a.length*4;
        StringBuilder buf = new StringBuilder((len*8)/6);
        char b[] = new char[len];
        int i = 0;
        int j = 0;
        while (i < a.length) {
            int n = a[i];
            for (int k = 0; k < 4; k++) {
                b[j++] = (char)(n & 255);
                n = n >> 8;
            }
            i++;
        }
            
        int e = 0;
        int c = 0;
        int cpos = 0;
        for (i = 0; i < b.length; i++) {
            if ((i % 3) == 0) {
                e = (b[i] >> 2) & 63;
                c = b[i] & 3;
                encodeValue(e, cpos++, buf);
            }
            else if ((i % 3) == 1) {
                e = ((b[i] >> 4) & 15) | (c << 4);
                c = b[i] & 15;
                encodeValue(e, cpos++, buf);
            }
            else {
                e = ((b[i] >> 6) & 3) | (c << 2);
                encodeValue(e, cpos++, buf);
                c = b[i] & 63;
                encodeValue(c, cpos++, buf);
                c = 0;
            }
        }
        if ((i % 3) == 0) {
            encodeValue(c, cpos++, buf);
        }
        if ((i % 3) == 1) {
            e = c << 4;
            encodeValue(e, cpos++, buf);
        }
        return buf.toString();
    }

    
    private static void encodeValue(int e, int cpos, StringBuilder buf) {
        if (cpos > 0 && cpos % 72 == 0) {
            buf.append('\n');
        }
        buf.append(CODING_TABLE[e]);
    }
    
    
    /**
     * Decodes the base-64 string <code>s</code> and returns
     * the decoded array of integers.
     */
    public static int[] decodeIntArray(String s) {
        int l = (int)Math.ceil(s.length()*6.0/8.0);
        while (l % 4 != 0) l++;
        int[] d = new int[l];
        int j = 0;
        int e = 0;
        int a = 0;
        int len = s.length();
        int pos = 0;
        int i = 0;
        while (pos < len) {
            int c = DECODING_TABLE[s.charAt(pos)];
            if (c < 0) { pos++; continue; }
            
            if (i % 4 == 0) {            
                e = c << 2;
                pos++; i++;
            }
            else if (i % 4 == 1) {
                a = (c >> 4) & 3;
                d[j++] = e | a;
                e = (c & 15) << 4;
                pos++; i++;
            }
            else if (i % 4 == 2){
                a = (c >> 2) & 15;
                d[j++] = e | a;
                e = (c & 3) << 6;
                pos++; i++;
            }
            else {
                a = c;
                d[j++] = e | a;
                e = 0;
                pos++; i++;
            }
        }
        
        l = (int)Math.ceil(j/4.0);
        int res[] = new int[l];
        j = 0;
        i = 0;
        while (j < l) {
            int n = 0;
            for (int k = 0; k < 4; k++) {
                n = n | (d[i] << k*8);
                i++;
            }
            res[j] = n;
            j++;
        }

        return res;
    }
    
    
    private static char[] CODING_TABLE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'
    };
    
    
    private Base64() { /* Pure static class */ }    

    private static int[] DECODING_TABLE = new int[256];    

    static {
        for (int i = 0; i < 256; i++) { DECODING_TABLE[i] = -1; }
        int j = 0;
        for (int i = 'A'; i <= 'Z'; i++) {
            DECODING_TABLE[i] = j;
            j++;
        }
        j = 26;
        for (int i = 'a'; i <= 'z'; i++) {
            DECODING_TABLE[i] = j;
            j++;
        }
        j = 52;
        for (int i = '0'; i <= '9'; i++) {
            DECODING_TABLE[i] = j;
            j++;
        }
        DECODING_TABLE['+'] = 62;
        DECODING_TABLE['/'] = 63;
    }
}

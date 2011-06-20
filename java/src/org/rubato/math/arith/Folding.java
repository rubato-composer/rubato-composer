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

/**
 * Various functions for folding a set of real numbers to a real interval.
 */
public class Folding {

    private static void swap(double[] elements, int[] perm, int a, int b) {
        double t1;
        int t2;
        t1 = elements[a];
        elements[a] = elements[b];
        elements[b] = t1;
        t2 = perm[a];
        perm[a] = perm[b];
        perm[b] = t2;
    }

    private static int partition(double[] elements, int l, int r, int[] perm) {
        int last;
        swap(elements, perm, l, (l + r) / 2);
        last = l;
        for (int i = l + 1; i <= r; i++) {
            if (elements[i] < elements[l]) {
                swap(elements, perm, ++last, i);
            }
        }
        swap(elements, perm, l, last);
        return last;
    }

    private static void sort1(double[] elements, int l, int r, int[] perm) {
        int m;
        if (l < r) {
            m = partition(elements, l, r, perm);
            sort1(elements, l, m - 1, perm);
            sort1(elements, m + 1, r, perm);
        }
    }

    private static void sort(double[] elements, int[] perm) {
        for (int i = 0; i < elements.length; i++)
            perm[i] = i;
        sort1(elements, 0, elements.length - 1, perm);
    }

    private static double searchLow(double[] elements, int i) {
        int j = i - 1;
        while (j >= 0 && elements[j] == elements[i])
            j--;
        if (j < 0)
            return elements[i] + elements[i] - searchHigh(elements, i);
        return elements[j];
    }

    private static double searchHigh(double[] elements, int i) {
        int j = i + 1;
        while (j < elements.length && elements[j] == elements[i])
            j++;
        if (j == elements.length)
            return elements[i] + elements[i] - searchLow(elements, i);
        return elements[j];
    }

    public static void computeLowHigh(double[] elements, double[] low, double[] high) {
        int[] perm = new int[elements.length];
        double[] elements_tmp = new double[elements.length];
        double[] low_tmp = new double[elements.length];
        double[] high_tmp = new double[elements.length];

        for (int i = 0; i < elements.length; i++) {
            elements_tmp[i] = elements[i];
        }

        sort(elements_tmp, perm);

        // Special case: all elements are identical
        if (elements_tmp[0] == elements_tmp[elements_tmp.length - 1]) {
            double l = elements_tmp[0] - 1.0 / 2;
            double h = elements_tmp[0] + 1.0 / 2;
            for (int i = 0; i < elements_tmp.length; i++) {
                low[i] = l;
                high[i] = h;
            }
            return;
        }

        // General case
        for (int i = 0; i < elements.length; i++) {
            low_tmp[i] = (elements_tmp[i] + searchLow(elements_tmp, i)) / 2.0;
            high_tmp[i] = (elements_tmp[i] + searchHigh(elements_tmp, i)) / 2.0;
        }
        for (int i = 0; i < elements.length; i++) {
            low[perm[i]] = low_tmp[i];
            high[perm[i]] = high_tmp[i];
        }
    }

    public static double[] fold(double[][] elements) {
        int eltnr = elements.length;
        double[] x = new double[eltnr];
        double[] y = new double[eltnr];
        double[] low = new double[eltnr];
        double[] high = new double[eltnr];
        fold(elements, x, y, low, high);
        return x;
    }

    public static double[] fold(
        double[][] elements,
        double[] x,
        double[] y,
        double[] low,
        double[] high) {
        int eltnr = elements.length;
        int eltlen = elements[0].length;

        // If the length of the elements is 1, treat
        // them like doubles, that is the folded
        // value is the value of the double
        if (eltlen == 1) {
            for (int i = 0; i < eltnr; i++) {
                x[i] = elements[i][0];
            }
            return x;
        }

        for (int i = 0; i < eltnr; i++) {
            x[i] = elements[i][0];
        }
        // x always contains the last computed fold
        // y contains the value with which the x
        // are folded
        for (int j = 1; j < eltlen; j++) {
            for (int i = 0; i < eltnr; i++) {
                y[i] = elements[i][j];
            }
            Folding.computeLowHigh(x, low, high);
            // Fold x and y, the result will be in x
            Folding.foldElements(x, y, low, high, x);
        }
        return x;
    }

    public static double foldElement(double x, double value, double low, double high) {
        return phi(high, high, low, low, x, -1, 1, -1, 1, value);
    }

    public static void foldElements(
        double[] x,
        double value[],
        double[] low,
        double[] high,
        double[] res) {
        for (int i = 0; i < x.length; i++) {
            res[i] = foldElement(x[i], value[i], low[i], high[i]);
        }
    }

    private static double phi(
        double o,
        double op,
        double u,
        double up,
        double t,
        double a,
        double b,
        double h,
        double l,
        double x) {
        double v;
        double ta, tb;
        double P2;
        v = Math.atan((b - a) * (x - l) / (h - l) + a);
        ta = Math.atan(a);
        tb = Math.atan(b);
        P2 = Math.PI / 2;
        if (v <= ta)
            return (up - u) * (v + P2) / (P2 + ta) + u;
        else if (ta < v && v <= 0)
            return - (t - up) * (v - ta) / ta + up;
        else if (0 < v && v <= tb)
            return (op - t) * v / tb + t;
        else
            return (o - op) * (v - tb) / (P2 - tb) + op;
    }
}

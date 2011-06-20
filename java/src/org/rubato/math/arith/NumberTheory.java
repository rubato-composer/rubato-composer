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
 * Various number theoretic functions.
 * 
 * @author Gérard Milmeister
 */
public final class NumberTheory {
	
    /**
     * The greatest common divisor of two integers.
     * 
     * @return the gcd of x and y, always non negative
     */
    public static int gcd(int x, int y) {
        int r0, r1, r;
        r0 = x;
        r1 = y;
        while (r1 != 0) {
            r = r1;
            r1 = r0 % r1;
            r0 = r;
        }
        
        if (r0 < 0) {
            return -r0;
        }

        return r0;
    }

    
    /**
     * The extended Euclidean algorithm.
     * 
     * @param res is an integer array that will contain two values such that:
     *            res[0]*x + res[1]*y = gcd(x,y)
     * @return the greatest common divisor of x and y, always non negative
     */
    public static int exgcd(int x, int y, int res[]) {
        int q, r, s, t;
        int r0 = x;
        int s0 = 1;
        int t0 = 0;
        int r1 = y;
        int s1 = 0;
        int t1 = 1;

        while (r1 != 0) {
            q = r0 / r1;
            r = r1;
            s = s1;
            t = t1;
            r1 = r0 - q * r1;
            s1 = s0 - q * s1;
            t1 = t0 - q * t1;
            r0 = r;
            s0 = s;
            t0 = t;
        }

        if (r0 < 0) {
            res[0] = -s0;
            res[1] = -t0;
            return -r0;
        } 

        res[0] = s0;
        res[1] = t0;
        return r0;
    }

    
    /**
     * The least common multiple of x and y.
     * 
     * @return lcm of x and y
     */
    public static int lcm(int x, int y) {
        return x * y / gcd(x, y);
    }

    
    /**
     * Returns <code>x</code> mod <code>n</code>.
     * 
     * @param n the modulus
     * @return <code>x</code> mod <code>n</code>, always non-negative.
     */
    public static int mod(int x, int n) {
        int y = x % n;
        if (y < 0) {
            y += n;
        }
        return y;
    }

    
    /**
     * Divide <code>x</code> by <code>y</code> modulo <code>n</code>.
     * 
     * @param n the modulus of the division
     * @return the quotient of the division
     * @exception ZeroDivisorException if gcd(y,n) != 1
     */
    public static int divideMod(int x, int y, int n)
            throws ZeroDivisorException {
        return mod(x * inverseMod(y, n), n);
    }

    
    /**
     * Returns an integer <code>y</code> such that <code>x</code> * <code>y</code> = 1 mod <code>n</code>.
     * 
     * @param x the integer to be inverted
     * @param n the modulus of the division
     * @exception ZeroDivisorException if gcd(x,p) != 1
     */
    public static int inverseMod(int x, int n) throws ZeroDivisorException {
        int g, r, s, r0, r1, s0, s1, q;
        r0 = x;
        s0 = 1;
        r1 = n;
        s1 = 0;

        // g = exgcd(x, p, res);
        // manual inlining

        while (r1 != 0) {
            q = r0 / r1;
            r = r1;
            s = s1;
            r1 = r0 - q * r1;
            s1 = s0 - q * s1;
            r0 = r;
            s0 = s;
        }

        if (r0 < 0) {
            s = -s0;
            g = -r0;
        }
        else {
            s = s0;
            g = r0;
        }

        if (g != 1) {
            throw new ZeroDivisorException();
        }

        return mod(s, n);
    }

    
    /**
     * Raises <code>x</code> to the <code>n</code>-th power modulo <code>p</code>.
     * 
     * @return x^n mod p
     * @exception ZeroDivisorException if n < 0 and gcd(x,p) != 1
     */
    public static int powerMod(int x, int n, int p) throws ZeroDivisorException {
        int res, bpos;

        if (n < 0) {
            x = inverseMod(x, p);
            n = -n;
        }

        if (x < 0) {
            x = mod(x, p);
        }

        if (n == 0) {
            return 1;
        }

        // Finding leading bit in the exponent n
        bpos = 31; // bits per int
        while ((n & (1 << bpos)) == 0) {
            bpos--;
        }

        res = 1;
        while (bpos >= 0) {
            res = mod(res * res, p);
            if ((n & (1 << bpos)) != 0) {
                res = mod(res * x, p);
            }
            bpos--;
        }

        return res;
    }
    
    
    private static final int PRIMES_LIST_LENGTH = 5000;
    
    
    /**
     * Returns true iff <code>n</code> is a prime integer.
     */
    public static boolean isPrime(int n) {    	
    	if (primes == null) {
    		buildPrimeList();
    	}
    	if (n % 2 == 0) {
    		return (n == 2);
    	}
    	else if (n % 3 == 0) {
    		return (n == 3);
    	}
    	else if (n % 5 == 0) {
    		return (n == 5);
    	}
    	else if (n > primes[PRIMES_LIST_LENGTH-1]) {
    		return checkPrime(n);
    	}
    	else {
    		return searchPrime(n, 0, PRIMES_LIST_LENGTH-1);
    	}
    }
    
    
    private static void buildPrimeList() {
    	primes = new int[PRIMES_LIST_LENGTH];
    	primes[0] = 2;
    	primes[1] = 3;
    	primes[2] = 5;    	
    	int i = 3;
    	int n = 7;
    	while (i < PRIMES_LIST_LENGTH) {
    		int m = (int)Math.sqrt(n);
    		int j = 0;
    		boolean prime = true;
    		while (j < i && primes[j] <= m && prime) {
    			if (n % primes[j] == 0) {
    				prime = false;
    			}
    			j++;
    		}
    		if (prime) {
    			primes[i] = n;
        		i++;
    		}
    		n++;
    	}
    }
    
    
    private static boolean checkPrime(int n) {
    	int m = (int)Math.sqrt(n);
    	int i = 3;
    	while (primes[i] <= m) {
    		if (n % primes[i] == 0) {
    			return false;
    		}
    		i++;
    	}
    	return true;
    }
    
    
    private static boolean searchPrime(int n, int min, int max) {
    	if (min > max) {
    		return false;
    	}

        int i = (min+max)/2;
    	if (primes[i] == n) {
    	    return true;
    	}
    	else if (n < primes[i]) {
    	    return searchPrime(n, min, i-1);
    	}
    	else {
    	    return searchPrime(n, i+1, max);
    	}
    }
    
    private static int[] primes = null;
    
    private NumberTheory() { /* not allowed */ }
}
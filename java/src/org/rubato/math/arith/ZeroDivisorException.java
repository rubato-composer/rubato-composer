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
 * Exception thrown by various functions involving modular arithmetic.
 * Zero divisors modulo <code>n</code> are numbers <code>a</code> and <code>b</code>,
 * both not zero, such that <code>a</code>*<code>b</code> = 0 mod <code>n</code>.
 */
public final class ZeroDivisorException extends java.lang.ArithmeticException {
    
    public ZeroDivisorException() { /* do nothing */ }

    public ZeroDivisorException(String msg) {
    	super(msg);
    }
}

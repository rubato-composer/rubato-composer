/*
 * Copyright (C) 2006 Gérard Milmeister
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

package org.rubato.scheme;

/**
 * This is the abstract base class for all types of Scheme values,
 * e.g., numbers, symbols, conses, closures, etc.
 * 
 * @author Gérard Milmeister
 */
public abstract class SExpr {
    
    /**
     * The unique instance of the Scheme () value.
     */
    public static final SExpr NULL = new Null();
    
    /**
     * The unique instance representating a non-value result.
     */
    public static final SExpr VOID = new Void();
    
    /**
     * Returns the cons of <code>car</code> and <code>cdr</code>.
     */
    public static final SCons cons(SExpr car, SExpr cdr) { return new SCons(car, cdr); }

    /**
     * Returns the car of <code>sexpr</code>, which must be an SCons.
     */
    public static final SExpr car(SExpr sexpr) { return sexpr.getCar(); }

    /**
     * Returns the car of <code>sexpr</code>, which must be an SCons.
     */
    public static final SExpr cdr(SExpr sexpr) { return sexpr.getCdr(); }

    /**
     * Returns true iff this is equal to <code>sexpr</code> in the eq? sense.
     */
    public abstract boolean eq_p(SExpr sexpr);

    /**
     * Returns true iff this is equal to <code>sexpr</code> in the eqv? sense.
     */
    public abstract boolean eqv_p(SExpr sexpr);

    /**
     * Returns true iff this is equal to <code>sexpr</code> in the equal? sense.
     */
    public abstract boolean equal_p(SExpr sexpr);
    
    /**
     * Returns true iff this is equal to <code>obj</code>. Normally this
     * returns the same result as {@link #equal_p}.
     */
    public abstract boolean equals(Object obj);

    /**
     * Returns a string representation of this SExpr.
     */
    public abstract String toString();

    /**
     * Returns a string representation of this SExpr. This is the
     * representation intended to show the result of an evaluation,
     * and should be a valid Scheme expression if possible.
     */
    public abstract String display();

    /**
     * Returns true iff this is the Scheme () value.
     */
    public boolean isNull() { return false; }

    /**
     * Returns true iff this is the Scheme no-value.
     */
    public boolean isVoid() { return false; }

    /**
     * Returns true iff this is a cons.
     */
    public boolean isCons() { return false; }

    /**
     * Returns true iff this is a number.
     */
    public boolean isNumber() { return false; }
    
    /**
     * Returns true iff this is an integer.
     */
    public boolean isInteger() { return false; }
    
    /**
     * Returns true iff this is a rational.
     */
    public boolean isRational() { return false; }

    /**
     * Returns true iff this is a real number.
     */
    public boolean isReal() { return false; }

    /**
     * Returns true iff this is a complex number.
     */
    public boolean isComplex() { return false; }
    
    /**
     * Returns true iff this is a Scheme string.
     */
    public boolean isString() { return false; }

    /**
     * Returns true iff this is a character.
     */
    public boolean isChar() { return false; }

    /**
     * Returns true iff this is a boolean.
     */
    public boolean isBoolean() { return false; }

    /**
     * Returns true iff this is a Scheme vector.
     */
    public boolean isVector() { return false; }

    /**
     * Returns true iff this is a Scheme symbol.
     */
    public boolean isSymbol() { return false; }

    /**
     * Returns true iff this is a closure.
     */
    public boolean isClosure() { return false; }
    
    /**
     * Returns true iff this is a primitive function.
     */
    public boolean isPrimitive() { return false; }
    
    /**
     * Returns true iff this is a Scheme denotator object.
     */
    public boolean isDenotator() { return false; }

    /**
     * Returns true iff this is a Scheme form object.
     */
    public boolean isForm() { return false; }

    /**
     * Returns true iff this is a list.
     */
    public boolean isList() { return isCons() || isNull(); }
    
    /**
     * Returns the car of this SExpr.
     * 
     * @return null if this is not a cons
     */
    public SExpr getCar() { return null; }
    
    /**
     * Returns the cdr of this SExpr.
     * 
     * @return null if this is not a cons
     */
    public SExpr getCdr() { return null; }
    
    /**
     * Sets the car of this SExpr. Does nothing if this is not a cons.
     */
    public void setCar(SExpr sexpr) {}
    
    /**
     * Sets the cdr of this SExpr. Does nothing if this is not a cons.
     */
    public void setCdr(SExpr sexpr) {}
    
    /**
     * Returns true iff this is a cons.
     */
    public boolean isPair() { return false; }
    
    /**
     * Returns the length if this is a regular list, otherwise -1.
     */
    public int getLength() { return -1; }
    
    /**
     * Returns the <code>i</code>-th element if this is a regular
     * list, otherwise null.
     */
    public SExpr nth(int i) { return null; }
    
    protected SExpr() {}
    
    protected static class Null extends SExpr {
        public boolean isNull() { return true; }
        public boolean eq_p(SExpr sexpr) { return this == sexpr; }
        public boolean eqv_p(SExpr sexpr) { return this == sexpr; }
        public boolean equal_p(SExpr sexpr) { return this == sexpr; }
        public boolean equals(Object obj) { return this == obj; }
        public int getLength() { return 0; }
        public String toString() { return "()"; }
        public String display() { return "()"; }
    }
    
    protected static class Void extends SExpr {
        public boolean isVoid() { return true; }
        public boolean eq_p(SExpr sexpr) { return this == sexpr; }
        public boolean eqv_p(SExpr sexpr) { return this == sexpr; }
        public boolean equal_p(SExpr sexpr) { return this == sexpr; }
        public boolean equals(Object obj) { return this == obj; }
        public String toString() { return "NoValue"; }
        public String display() { return ""; }
    }
}

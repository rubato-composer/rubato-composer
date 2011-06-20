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
 * The class representing primitive functions as Scheme values.
 * 
 * @author Gérard Milmeister
 */
public final class SPrimitive extends SExpr {

    /**
     * Creates a primitive function value from the primitive <code>p</code>.
     */
    public SPrimitive(Primitive p) {
        this.p = p;
    }
    
    public boolean eq_p(SExpr sexpr) {
        return this == sexpr;
    }
    
    public boolean eqv_p(SExpr sexpr) {
        return this == sexpr;
    }
    
    public boolean equal_p(SExpr sexpr) {
        return this == sexpr;
    }
    
    public boolean equals(Object obj) {
        return this == obj;
    }
    
    public boolean isPrimitive() {
        return true;
    }
    
    public String toString() {
        return "#<primitive:"+p.getName()+">";
    }
    
    public String display() {
        return "#<primitive:"+p.getName()+">";
    }
    
    /**
     * Returns the primitive function in this Scheme value.
     */
    public Primitive getPrimitive() {
        return p;
    }
    
    private Primitive p; 
}

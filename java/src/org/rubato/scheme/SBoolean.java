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
 * The class of Boolean values. There are only two instances,
 * <code>TRUE</code> and <code>FALSE</code>.
 * 
 * @author Gérard Milmeister
 */
public final class SBoolean extends SExpr {

    /**
     * The Scheme value "#t".
     */
    public static SBoolean TRUE = new SBoolean(true);    

    /**
     * The Scheme value "#f".
     */
    public static SBoolean FALSE = new SBoolean(false);
    
    
    /**
     * Returns the Scheme value corresponding to the boolean <code>b</code>.
     */
    public static SBoolean make(boolean b) {
        return b?TRUE:FALSE;
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
    
    
    public String toString() {
        return "#"+(b?"t":"f");
    }
    
    
    public String display() {
        return toString();
    }
    
    
    public boolean isBoolean() {
        return true;
    }
    
    
    private SBoolean(boolean b) {
        this.b = b;
    }
    
    
    private boolean b;
}

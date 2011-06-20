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

import java.util.HashMap;

/**
 * Class representing Scheme symbol calues. Symbols are unique, i.e., there is always
 * at most one object for each string representation of a symbol.
 * 
 * @author Gérard Milmeister
 */
public final class Symbol extends SExpr {

    /**
     * Creates a symbol with string representation <code>s</code>.
     */
    public static Symbol make(String s) {
        Symbol sym = symtab.get(s);
        if (sym == null) {
            sym = new Symbol();
            sym.name = s;
            symtab.put(s, sym);
        }
        return sym;
    }

    
    /**
     * Returns the string representation of the symbol.
     */
    public String getName() {
        return name;
    }
    
    
    public boolean isSymbol() {
        return true;
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
        return name;
    }

    
    public String display() {
        return name;
    }
    

    private Symbol() {}

    private String name;    
    
    private static HashMap<String,Symbol> symtab = new HashMap<String,Symbol>(256);
}

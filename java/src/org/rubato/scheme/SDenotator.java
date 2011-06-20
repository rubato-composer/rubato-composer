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

import org.rubato.math.yoneda.Denotator;


/**
 * The class representing denotators as Scheme values.
 * 
 * @author Gérard Milmeister
 */
public final class SDenotator extends SExpr {

    /**
     * Creates a Scheme value from the denotator <code>d</code>.
     */
    public SDenotator(Denotator d) {
        this.d = d;
    }
    
    
    public boolean eq_p(SExpr sexpr) {
        return this == sexpr;
    }

    
    public boolean eqv_p(SExpr sexpr) {
        return this == sexpr;
    }

    
    public boolean equal_p(SExpr sexpr) {
        return equals(sexpr);
    }

    
    public boolean equals(Object obj) {
        return (obj instanceof SDenotator) && ((SDenotator)obj).d.equals(d);
    }


    public boolean isDenotator() {
        return true;
    }
    

    public String toString() {
        return "#<denotator:"+d.toString()+">";
    }
    

    public String display() {
        return "#<denotator:"+d.toString()+">";
    }
    
    
    /**
     * Returns the denotator in this Scheme value.
     */
    public Denotator getDenotator() {
        return d;
    }

    
    private Denotator d;
}

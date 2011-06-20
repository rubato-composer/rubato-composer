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

import org.rubato.math.yoneda.Form;

/**
 * The class representing forms as Scheme values.
 * 
 * @author Gérard Milmeister
 */
public final class SForm extends SExpr {

    /**
     * Creates a Scheme value from the form <code>f</code>.
     */
    public SForm(Form f) {
        this.f = f;
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
        return (obj instanceof SForm) && ((SForm)obj).f.equals(f);
    }

    
    public boolean isForm() {
        return true;
    }
    

    public String toString() {
        return "#<form:"+f.toString()+">";
    }
    

    public String display() {
        return "#<form:"+f.toString()+">";
    }

    
    /**
     * Returns the form in this Scheme value.
     */
    public Form getForm() {
        return f;
    }
    
    
    private Form f;
}

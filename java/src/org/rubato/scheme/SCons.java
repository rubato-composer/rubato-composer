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
 * The class representing Scheme pairs.
 * 
 * @author Gérard Milmeister
 */
public final class SCons extends SExpr {

    /**
     * Creates an complete pair where both car and cdr are ().
     */
    public SCons() {
        this.car = SExpr.NULL;
        this.cdr = SExpr.NULL;
    }
   
    /**
     * Creates a pair with the given <code>car</code> and <code>cdr</code>.
     */
    public SCons(SExpr car, SExpr cdr) {
        this.car = car;
        this.cdr = cdr;
    }    
    
    
    public SExpr getCar() {
        return car;
    }
    
    
    public SExpr getCdr() {
        return cdr;
    }
    
    
    public void setCar(SExpr car) {
        this.car = car;
    }
    
    
    public void setCdr(SExpr cdr) {
        this.cdr = cdr;
    }
    
    
    public int getLength() {
        int i = 0;
        SExpr cur = this;
        while  (cur.isCons()) {
            i++;
            cur = cur.getCdr();
        }
        if (cur.isNull()) {
            return i;
        }
        else {
            return -1;
        }
    }
    
    
    public SExpr nth(int i) {
        SExpr cur = this;
        while (i > 0) {
            cur = cur.getCdr();
            i--;
        }
        return cur.getCar();
    }
    
    
    public boolean eq_p(SExpr sexpr) {
        return this == sexpr;
    }

    
    public boolean eqv_p(SExpr sexpr) {
        return this == sexpr;
    }

    
    public boolean equal_p(SExpr sexpr) {
        if (sexpr instanceof SCons) {
            return car.equal_p(car(sexpr)) &&
                   cdr.equal_p(cdr(sexpr));
        }
        else {
            return false;
        }
    }

    
    public boolean equals(Object obj) {
        if (obj instanceof SCons) {
            return car.equals(((SCons)obj).car) &&
                   cdr.equals(((SCons)obj).cdr);
        }
        else {
            return false;
        }
    }
    
    
    public boolean isCons() {
        return true;
    }
    
    
    public String toString() {
        if (cdr.isCons() || cdr.isNull()) {
            SCons cur_cons = this;
            StringBuilder buf = new StringBuilder();
            buf.append("(");
            while (cur_cons != null) {
                buf.append(cur_cons.car);
                if (cur_cons.cdr.isCons()) {
                    buf.append(" ");
                    cur_cons = (SCons)cur_cons.cdr;
                }
                else if (cur_cons.cdr.isNull()) {
                    cur_cons = null;
                }
                else {
                    buf.append(" . ");
                    buf.append(cur_cons.cdr);
                    cur_cons = null;
                }
            }
            buf.append(")");
            return buf.toString();
        }
        else {
            return "("+car+" . "+cdr+")";
        }
    }
    
    public String display() {
        if (cdr.isCons() || cdr.isNull()) {
            SCons cur_cons = this;
            StringBuilder buf = new StringBuilder();
            buf.append("(");
            while (cur_cons != null) {
                buf.append(cur_cons.car.display());
                if (cur_cons.cdr.isCons()) {
                    buf.append(" ");
                    cur_cons = (SCons)cur_cons.cdr;
                }
                else if (cur_cons.cdr.isNull()) {
                    cur_cons = null;
                }
                else {
                    buf.append(" . ");
                    buf.append(cur_cons.cdr.display());
                    cur_cons = null;
                }
            }
            buf.append(")");
            return buf.toString();
        }
        else {
            return "("+car.display()+" . "+cdr.display()+")";
        }
    }
    
    
    private SExpr car;
    private SExpr cdr;
}

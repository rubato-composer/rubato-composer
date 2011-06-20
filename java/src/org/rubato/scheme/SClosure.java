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
 * The class representing Scheme closures.
 * 
 * @author Gérard Milmeister
 */
public final class SClosure extends SExpr {

    /**
     * Creates a closure with list of arguments <code>args</code>,
     * the given <code>body</code> and environment <code>env</code>.
     */
    public SClosure(SExpr args, SExpr body, Env env) {
        this.args = args;
        this.body = body;
        this.env = env;
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
        if (obj instanceof SClosure) {
            SClosure closure = (SClosure)obj;
            return args.equals(closure.args) &&
                   body.equals(closure.body) &&
                   env.equals(closure.env);
        }
        else {
            return false;
        }
    }

    
    public boolean isClosure() {
        return true;
    }
    
    
    public String toString() {
        return "#<closure:"+hashCode()+">";
    }

    
    public String display() {
        return "#<closure:"+hashCode()+">";
    }


    /**
     * Returns the environment of this closure.
     */
    public Env getEnv() {
        return env;
    }

    
    /**
     * Returns the arguments of this closure;
     */
    public SExpr getArgs() {
        return args;
    }

    
    /**
     * Returns the body of this closure;
     */
    public SExpr getBody() {
        return body;
    }

    
    private SExpr args;
    private SExpr body;
    private Env   env;
}

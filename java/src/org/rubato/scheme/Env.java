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

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Map.Entry;

/**
 * The class representing Scheme environments. Environments
 * are linked as stacks. Searching for a symbol starts at top
 * environment and follows links until the ground environment
 * is reached.
 * 
 * @author Gérard Milmeister
 */
public final class Env extends SExpr {

    /**
     * Creates an empty ground environment.
     */
    public Env() {
        this(null);
    }
    

    /**
     * Creates an empty environment on top of the environment <code>link</code>.
     */
    public Env(Env link) {
        this.link = link;
    }
    

    /**
     * Returns a new empty environment pushed on top of this.
     */
    public Env push() {
        return new Env(this);
    }
    

    /**
     * Returns the environment that this environment is linked to.
     */
    public Env pop() {
        return link;
    }
    
    
    /**
     * Binds the symbol <code>sym</code> to the expression <code>sexpr</code>
     * in this environment.
     */
    public void put(Symbol sym, SExpr sexpr) {
        env.put(sym, sexpr);
    }
    
    
    /**
     * Binds the symbol with string representation <code>sym</code>
     * to the expression <code>sexpr</code> in this environment.
     */
    public void put(String sym, SExpr expr) {
        env.put(Symbol.make(sym), expr);
    }
    
    
    /**
     * Returns the expression bound to the symbol <code>sym</code>.
     * If there is no binding in this environment, follows the link.
     * If there is no binding at all, returns null.
     */
    public SExpr get(Symbol sym) {
        SExpr res = env.get(sym);
        if (res == null && link != null) {
            res = link.get(sym);
        }
        return res;
    }
    
    
    /**
     * Binds the symbol with string representation <code>sym</code>
     * to the expression <code>sexpr</code> in this environment.
     * The binding only succeeds, if the symbol has already been bound.
     * In this case true is returned, otherwise nothing happens, and
     * false is returned.
     */
    public boolean set(Symbol sym, SExpr expr) {
        if (env.get(sym) != null) {
            env.put(sym, expr);
            return true;
        }
        else if (link != null) {
            return link.set(sym, expr);
        }
        else {
            return false;
        }
    }
    
    
    /**
     * Returns all bindings in this environment.
     */
    public Set<Entry<Symbol,SExpr>> getBindings() {
        return env.entrySet();
    }
    

    /**
     * Binds primitive function <code>p</code> to its
     * name in this environment.
     */
    public void addPrimitive(Primitive p) {
        put(p.getName(), new SPrimitive(p));
    }


    /**
     * Extends this environment with bindings for each
     * symbol in <code>vars</code> to its correspondings
     * value in <code>vals</code>.
     */
    public boolean extendEnv(SExpr vars, SExpr vals) {
        if (vars.isNull() && vals.isNull()) {
            return true;
        }
        if (vars.isNull()) {
            return false;
        }
        if (vars.isSymbol()) {
            env.put((Symbol)vars, vals);
            return true;
        }
        if (vals.isNull()) {
            return false;
        }

        if (!extendEnv(vars.getCdr(), vals.getCdr())) {
            return false;
        }
        
        SExpr sym = vars.getCar();
        SExpr val = vals.getCar();
        if (sym.isSymbol()) {            
            put((Symbol)sym, val);
            return true;
        }
        else {
            return false;
        }
    }
    
    
    public boolean eq_p(SExpr sexpr) {
        return equals(sexpr);
    }

    
    public boolean eqv_p(SExpr sexpr) {
        return equals(sexpr);
    }

    
    public boolean equal_p(SExpr sexpr) {
        return equals(sexpr);
    }

    
    public boolean equals(Object obj) {
        return this == obj;
    }
    
    
    public String toString() {
        return "#<environment:"+hashCode()+">";
    }

    
    public String display() {
        return "#<environment:"+hashCode()+">";
    }    
    
    
    private IdentityHashMap<Symbol,SExpr> env = new IdentityHashMap<Symbol,SExpr>();
    private Env link = null;


    /**
     * Creates a ground environment containing bindings for
     * all standard and all Rubato-specific primitives.
     */
    public static Env makeGlobalEnvironment() {
        Env env = new Env();
        env.fillGlobalEnvironment();
        return env;
    }

    
    /**
     * Creates a ground environment containing bindings for
     * all standard primitives.
     */
    public static Env makeStandardEnvironment() {
        Env env = new Env();
        env.fillStandardEnvironment();
        return env;
    }

    
    /**
     * Resets this environment to contain only standard 
     * and Rubato-specific primitives. 
     */
    public void resetGlobal() {
        link = null;
        env = new IdentityHashMap<Symbol,SExpr>();
        fillGlobalEnvironment();
    }


    /**
     * Resets this environment to contain only standard primitives. 
     */
    public void resetStandard() {
        link = null;
        env = new IdentityHashMap<Symbol,SExpr>();
        fillGlobalEnvironment();
    }

    
    private void fillGlobalEnvironment() {
        fillStandardEnvironment();
        RubatoPrimitives.fillEnvironment(this);
    }

    
    private void fillStandardEnvironment() {
        ArithPrimitives.fillEnvironment(this);
        ListPrimitives.fillEnvironment(this);
        VectorPrimitives.fillEnvironment(this);
        StringPrimitives.fillEnvironment(this);
        ControlPrimitives.fillEnvironment(this);
        InputOutputPrimitives.fillEnvironment(this);
    }
}

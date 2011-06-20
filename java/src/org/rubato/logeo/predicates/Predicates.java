/*
 * Copyright (C) 2002 Gérard Milmeister
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

package org.rubato.logeo.predicates;

import org.rubato.base.Repository;
import org.rubato.base.RubatoException;
import org.rubato.logeo.functions.Function;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;


/**
 * Several functions used for combining predicates.
 *
 * @author Gérard Milmeister
 */
public final class Predicates {

    /**
     * Returns a predicate that is the
     * conjuction of <code>p</code> and <code>q</code>.
     * Both predicates must have same arity.
     */
    public static Predicate and(Predicate p, Predicate q)
            throws RubatoException {
        return new Conjunction(p, q);
    }
    
    
    /**
     * Returns a predicate that is the
     * disjunction of <code>p</code> and <code>p</code>.
     * Both predicates must have same arity.
     */
    public static Predicate or(Predicate p, Predicate q)
            throws RubatoException {
        return new Disjunction(p, q);
    }
        

    /**
     * Returns a predicate that is the negation of <code>p</code>.
     */    
    public static Predicate not(Predicate p) {
        return new Negation(p);
    }


    /**
     * Returns a predicate of arity 1 that is always true.
     */
    public static Predicate getTruePredicate() {
        return truePredicate;
    }


    /**
     * Returns a predicate of arity <code>n</code> that is always true.
     */
    public static Predicate getTruePredicate(int n) {
        if (n == 1) {
            return getTruePredicate();
        }
        return new TruePredicate(n);
    }
    
    
    /**
     * Returns a predicate based on a function that returns
     * a denotator of form "Boolean". 
     */
    public static Predicate fromFunction(Function f) {
        return new FunctionPredicate(f);
    }


    private static Predicate truePredicate;
    
    static {
        truePredicate = new TruePredicate(1);
    }


    //
    // private classes
    //

    private static class TruePredicate extends AbstractPredicate {
        
        public TruePredicate(int arity) {
            this.arity = arity;
        }
        
        public int getArity() { return arity; }
        
        public boolean call(Denotator ... denotators) { return true; }
        
        private int arity;

        public Form getInputForm(int i) {
            return null;
        }
    }

    private static class Conjunction extends AbstractPredicate {        

        public Conjunction(Predicate p, Predicate q) 
                throws RubatoException {
            if (!p.isCompatible(q)) {
                throw new RubatoException("Both predicates must be compatible");
            }
            this.p = p;
            this.q = q;
        }
        
        public boolean call(Denotator ... denotators)
                throws RubatoException {
            return p.call(denotators) && q.call(denotators);
        }
        
        public int getArity() {
            return p.getArity();
        }
        
        public Form getInputForm(int i) {
            return p.getInputForm(i);
        }
        
        public String getName() {
            return "("+p.getName()+" && "+q.getName()+")";    
        }
        
        private Predicate p, q; 
    }

    private static class Disjunction extends AbstractPredicate {

        public Disjunction(Predicate p, Predicate q) 
                throws RubatoException {
            if (!p.isCompatible(q)) {
                throw new RubatoException("Both predicates must be compatible.");
            }
            this.p = p;
            this.q = q;
        }
        
        public boolean call(Denotator ... denotators)
                throws RubatoException {
            return p.call(denotators) || q.call(denotators);
        }
        
        public int getArity() {
            return p.getArity();
        }
        
        public Form getInputForm(int i) {
            return p.getInputForm(i);
        }

        public String getName() {
            return "("+p.getName()+" || "+q.getName()+")";    
        }
        
        private Predicate p, q; 
    }

    private static class Negation extends AbstractPredicate {

        public Negation(Predicate p) {
            this.p = p;
        }
        
        public boolean call(Denotator ... denotators)
                throws RubatoException {
            return !p.call(denotators);
        }
        
        public int getArity() {
            return p.getArity();
        }
        
        public Form getInputForm(int i) {
            return p.getInputForm(i);
        }

        public String getName() {
            return "!"+p.getName();    
        }
        
        private Predicate p; 
    }
    
    private static class FunctionPredicate extends AbstractPredicate {

        public FunctionPredicate(Function f) {
            this.f = f;
        }

        public boolean call(Denotator ... denotators)
                throws RubatoException {
            return f.evaluate(denotators).equals(trueDeno);
        }
        
        public int getArity() {
            return f.getArity();
        }
        
        public Form getInputForm(int i) {
            return f.getInputForm(i);
        }

        public String getName() {
            return f.getName();    
        }
        
        private Function f;
    }
    
    protected static Denotator trueDeno;
    
    static {
        Repository rep = Repository.systemRepository();
        trueDeno = rep.getDenotator("True");
    }
}

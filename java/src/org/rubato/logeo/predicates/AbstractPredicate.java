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

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;


/**
 * This class implements predicates on denotators.
 * Predicates may have any arity.
 * The signature of predicate is indicated by implementing the 
 * getInputForm method.
 *
 * @author Gérard Milmeister
 */
public abstract class AbstractPredicate implements Predicate {

    /**
     * Calls the predicate with a list of arguments.
     * The predicate must have arity equal to the length of the list.
     */
    public abstract boolean call(Denotator ... denotators)
            throws RubatoException;


    /**
     * Implements the compare method required by the Comparator interface.
     * The predicate must have arity 2 and call(a,b) is interpreted
     * as a < b.
     */
    public int compare(Predicate o1, Predicate o2) {
        try {
            if (call((Denotator)o1, (Denotator)o2)) {
                return -1;
            }
            else if (call((Denotator)o2, (Denotator)o1)) {
                return 1;
            }
            else {
                return 0;
            }
        }
        catch (RubatoException e) {
            throw new IllegalArgumentException("Could not compare "+o1+" and "+o2+".");
        }
    }

    /**
     * Returns a predicate that is the conjuction of this and p.
     * Both predicates must have same arity.
     */
    public Predicate and(Predicate p1)
            throws RubatoException {
        return Predicates.and(this, p1);    
    }
    
    
    /**
     * Returns a predicate that is the disjunction of this and p.
     * Both predicates must have same arity.
     */
    public Predicate or(Predicate p)
            throws RubatoException {
        return Predicates.or(this, p);    
    }
    
    
    /**
     * Returns a predicate that is the negation of this.
     */    
    public Predicate negated() {
        return Predicates.not(this);
    }


    /**
     * Returns the arity of the predicate.
     * Must be implemented in a subclass.
     */
    public abstract int getArity();


    /**
     * Returns the ith argument form.
     * Must be implemented in a subclass.
     */
    public abstract Form getInputForm(int i);
    

    /**
     * Returns the name of the predicate.
     * @return "anonymous" by default.
     */
    public String getName() {
        return "anonymous";
    }    


    public String toString() {
        return getName();
    }


    /**
     * Returns true if p is compatible to this.
     * Two predicates are compatible if both have the same
     * arity and if their respective argument forms are the
     * same or one of them is null.
     */
    public boolean isCompatible(Predicate p) {
        if (getArity() != p.getArity()) {
            return false;
        }

        for (int i = 0; i < getArity(); i++) {
            if (getInputForm(i) != null && p.getInputForm(i) != null) {
                if (!getInputForm(i).equals(p.getInputForm(i))) {
                    return false;
                }
            }
        }

        return true;
    }
}

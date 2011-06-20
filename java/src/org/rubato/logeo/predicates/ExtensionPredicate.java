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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.rubato.math.yoneda.*;


/**
 * A predicate defined by its extension.
 * 
 * @author Gérard Milmeister
 */
public class ExtensionPredicate extends AbstractPredicate {

    /**
     * Creates a predicate with extension defined by a list of denotators.
     */
    public ExtensionPredicate(List<Denotator> denoList) {
        denoSet = new HashSet<Denotator>();
        denoSet.addAll(denoList);
    }


    /**
     * Creates a predicate with extension defined by <code>denotators</code>.
     */
    public ExtensionPredicate(Denotator ... denotators) {
        denoSet = new HashSet<Denotator>();
        Collections.addAll(denoSet, denotators);
    }


    /**
     * Creates a predicate with extension defined by the set
     * of denotators in the PowerDenotator <code>d</code>.
     */
    public ExtensionPredicate(PowerDenotator d) {
        denoSet = new HashSet<Denotator>();
        denoSet.addAll(d.getFactors());
    }


    /**
     * Creates a predicate with extension defined by the set
     * of denotators in the ListDenotator <code>d</code>.
     */
    public ExtensionPredicate(ListDenotator d) {
        denoSet = new HashSet<Denotator>();
        denoSet.addAll(d.getFactors());
    }


    public boolean call(Denotator ... denotators) {
        return denoSet.contains(denotators[0]);
    }


    public int getArity() {
        return 1;
    }


    public Form getInputForm(int i) {
        return null;
    }


    /**
     * Adds all coordinate denotators from <code>d</code> to the extension.
     * @param d a PowerDenotator
     */
    public ExtensionPredicate addAll(PowerDenotator d) {
        denoSet.addAll(d.getFactors());
        return this;
    }


    /**
     * Adds all coordinate denotators from d to the extension.
     * @param d a ListDenotator
     */
    public ExtensionPredicate addAll(ListDenotator d) {
        denoSet.addAll(d.getFactors());
        return this;
    }

    
    /**
     * Adds <code>denotators</code> to the extension.
     */
    public ExtensionPredicate add(Denotator ... denotators) {
        Collections.addAll(denoSet, denotators);
        return this;
    }

    
    private HashSet<Denotator> denoSet;
}

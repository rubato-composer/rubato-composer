/*
 * Copyright (C) 2003 Gérard Milmeister
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

package org.rubato.logeo;

import org.rubato.base.RubatoException;
import org.rubato.logeo.functions.Function;
import org.rubato.logeo.predicates.FormPredicate;
import org.rubato.logeo.predicates.Predicate;
import org.rubato.math.yoneda.*;

/**
 * This class implements an apply operation. A function
 * is applied to every part of a denotator that matches some given
 * criteria.
 * The denotator may be modified inplace, so this should be
 * considered a destructive operation. However an applied function
 * does not need to modify anything and could simply collect information. 
 * 
 * @author Gérard Milmeister
 */
public class Apply {

    /**
     * Applies the function <code>f</code> to the
     * denotator <code>d</code> for every part of <code>d</code>
     * that matches the given form.
     * @param d the denotator that the function is applied to
     * @param form the form of the parts the function is applied to
     * @param f the function that is applied
     */
    public static void apply(Denotator d, Form form, Function f)
            throws RubatoException {
        Predicate p = new FormPredicate(form);
        apply(d, p, f);
    }


    /**
     * Applies the function <code>f</code> to the
     * denotator <code>d</code> for every part of <code>d</code>
     * that satisfies the predicate <code>p</code>.
     * @param d the denotator that the function is applied to
     * @param p the predicate that a part must satisfy for the function to be applied
     * @param f the function that is applied
     */
    public static void apply(Denotator d, Predicate p, Function f)
            throws RubatoException {
        doApply(d, p, f);
    }

  
    private static void doApply(Denotator d, Predicate p, Function f)
            throws RubatoException {
        if (p.call(d)) {
            f.evaluate(d);
        }

        switch (d.getForm().getType()) {
            case Form.LIST: {
                applyList((ListDenotator)d, p, f);
                break;
            }
                
            case Form.POWER: {
                applyPower((PowerDenotator)d, p, f);
                break;
            }
            case Form.LIMIT: {
                applyLimit((LimitDenotator)d, p, f);
                break;
            }
            case Form.COLIMIT: {
                applyColimit((ColimitDenotator)d, p, f);
                break;
            }
        }
    }   


    private static void applyList(ListDenotator d, Predicate p, Function f)
            throws RubatoException {
        for (Denotator deno : d) {
            apply(deno, p, f);
        }
    }

    
    private static void applyPower(PowerDenotator d, Predicate p, Function f)
            throws RubatoException {
        for (Denotator deno : d) {
            apply(deno, p, f);
        }
    }
    
    
    private static void applyLimit(LimitDenotator d, Predicate p, Function f)
            throws RubatoException {
        for (Denotator deno : d) {
            apply(deno, p, f);
        }
    }


    private static void applyColimit(ColimitDenotator d, Predicate p, Function f)
            throws RubatoException {
        apply(d.getFactor(), p, f);
    }


    private Apply() { /* not allowed */ }
}

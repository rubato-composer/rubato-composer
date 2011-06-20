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

import java.util.LinkedList;

import org.rubato.base.RubatoException;
import org.rubato.logeo.functions.Function;
import org.rubato.logeo.predicates.FormPredicate;
import org.rubato.logeo.predicates.Predicate;
import org.rubato.math.yoneda.*;


/**
 * Static methods for mapping function onto denotators.
 *  
 * @author Gérard Milmeister
 */
public final class Map {

    /**
     * Maps the function <code>fun</code> to the denotator
     * <code>d</code> for every part of denotator
     * that matches the given <code>form</code>.
     * @param d the denotator that the function is applied to
     * @param form the form of the parts that get mapped
     * @param f the function that is applied
     * @return the mapped denotator
     * @throws RubatoException if mapping fails
     */
    public static Denotator map(Denotator d, Form form, Function f)
            throws RubatoException {
        return map(d, new FormPredicate(form), f);
    }


    /**
     * Maps the function <code>fun</code> to the denotator 
     * <code>d</code> for every part of the denotator
     * that satisfies the given predicate <code>p</code>.
     * @param d the denotator that the function is applied to
     * @param p the predicate that a part must satisfy for the function to be applied
     * @param f the function that is applied
     * @return the mapped denotator
     * @throws RubatoException if mapping fails
     */
    public static Denotator map(Denotator d, Predicate p, Function f)
            throws RubatoException {
        return doMap(d, p, f);
    }

    
    private static Denotator doMap(Denotator d, Predicate p, Function f)
            throws RubatoException {
        if (p.call(d)) {
            d = f.evaluate(d);
        }

        switch (d.getForm().getType()) {
            case Form.LIST: {
                return mapList((ListDenotator)d, p, f);
            }
            case Form.POWER: {
                return mapPower((PowerDenotator)d, p, f);
            }
            case Form.LIMIT: {
                return mapLimit((LimitDenotator)d, p, f);
            }
            case Form.COLIMIT: {
                return mapColimit((ColimitDenotator)d, p, f);
            }
            case Form.SIMPLE: {
                return d;
            }
            default: {
                return d;
            }
        }
    }   


    private static ListDenotator mapList(ListDenotator d, Predicate p, Function f)
            throws RubatoException {
        LinkedList<Denotator> denoList = new LinkedList<Denotator>(); 
        boolean changed = false;
        
        for (Denotator deno : d) {
            Denotator newDeno = map(deno, p, f);
            if (newDeno != deno) {
                changed = true;
            }
            denoList.add(newDeno);
        }
       
        if (changed) {
            return new ListDenotator(null, d.getListForm(), denoList);
        }
        else {
            return d;
        }
    }

    
    private static PowerDenotator mapPower(PowerDenotator d, Predicate p, Function f)
            throws RubatoException {
        LinkedList<Denotator> denoList = new LinkedList<Denotator>(); 
        boolean changed = false;
        
        for (Denotator deno : d) {
            Denotator newDeno = map(deno, p, f);
            if (newDeno != deno) {
                changed = true;
            }
            denoList.add(newDeno);
        }
       
        if (changed) {
            return new PowerDenotator(null, d.getPowerForm(), denoList);
        }
        else {
            return d;
        }
    }

    
    private static LimitDenotator mapLimit(LimitDenotator d, Predicate p, Function f)
            throws RubatoException {
        LinkedList<Denotator> denoList = new LinkedList<Denotator>(); 
        boolean changed = false;
        
        for (Denotator deno : d) {
            Denotator newDeno = map(deno, p, f);
            if (newDeno != deno) {
                changed = true;
            }
            denoList.add(newDeno);
        }
       
        if (changed) {
            return new LimitDenotator(null, d.getLimitForm(), denoList);
        }
        else {
            return d;
        }
    }


    private static ColimitDenotator mapColimit(ColimitDenotator d, Predicate p, Function f)
            throws RubatoException {
        Denotator deno = d.getFactor(0);
        int index = d.getIndex();

        Denotator newDeno = map(deno, p, f);

        if (newDeno != deno) {
            return new ColimitDenotator(null, d.getColimitForm(), index, newDeno);
        }
        else {
            return d;
        }
    }

    
    private Map() { /* not allowed */ }
}

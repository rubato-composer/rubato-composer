/*
 * Copyright (C) 2002, 2006 Gérard Milmeister
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

import java.util.*;

import org.rubato.base.RubatoException;
import org.rubato.logeo.functions.Function;
import org.rubato.logeo.predicates.Predicate;
import org.rubato.math.module.Module;
import org.rubato.math.yoneda.*;


/**
 * This class implements various operations on denotators of type list.
 * Arguments to these methods must not be null.
 * 
 * @author Gérard Milmeister
 */
public final class Lists {

    /**
     * Returns a list denotator that is the
     * concatenation of <code>d1</code> and <code>d2</code>.
     * @throws RubatoException if <code>d1</code> or <code>d2</code>
     *                         is not of the required form
     */
    public static ListDenotator concat(ListDenotator d1, ListDenotator d2)
            throws RubatoException {
        Form form = processArguments(d1, d2);
        if (form == null) {
            throw new RubatoException("Lists.concat: "+d1+" and "+d2+" do not have the same base form");
        }

        List<Denotator> factors1 = d1.getFactors();
        List<Denotator> factors2 = d2.getFactors();
        if (!d1.getAddress().equals(d2.getAddress())) {
            Module newAddress = Address.getCommonModule(d1.getAddress(), d2.getAddress());
            if (newAddress == null) {
                throw new RubatoException("Lists.concat: Could not find a common address for "+d1+" and "+d2);
            }
            factors1 = readdress(factors1, newAddress);
            factors2 = readdress(factors2, newAddress);
            List<Denotator> factors = new LinkedList<Denotator>(factors1);
            factors.addAll(factors2);
            return new ListDenotator(null, d1.getListForm(), factors);        
        }
        else {
            List<Denotator> factors = new LinkedList<Denotator>(factors1);
            factors.addAll(factors2);
            return ListDenotator._make_unsafe(null, d1.getAddress(), d1.getListForm(), factors);
        }
    }

    
    /**
     * Returns a list denotator that is the concatenation
     * of the argument list denotators.
     * @throws RubatoException if the denotators do not have the required form
     */
    public static ListDenotator concat(ListDenotator ... denoList)
            throws RubatoException {
        if (denoList.length == 0) {
            return null;
        }
        ListDenotator result = denoList[0];
        for (int i = 1; i < denoList.length; i++) {
            result = concat(result, denoList[i]);
        }
        return result;
    }

    
    /**
     * Appends the elements of <code>d2</code> to <code>d1</code>.
     * This operation is destructive on the first argument.
     * @throws RubatoException if <code>d1</code> or <code>d2</code>
     *                         is not of the required form, or if they
     *                         do not have the same address
     */
    public static void appendTo(ListDenotator d1, ListDenotator d2)
            throws RubatoException {
        Form form = processArguments(d1, d2);
        if (form == null) {
            // different base forms
            throw new RubatoException("Lists.appendTo: "+d1+" and "+d2+" do not have the same base form");
        }
        else if (d1.getAddress().equals(d2.getAddress())) {
            // different addresses
            throw new RubatoException("Lists.appendTo: "+d1+" and "+d2+" do not have the same address");
        }
        else {
            ListMorphismMap m1 = (ListMorphismMap)d1.getCoordinate().getMap();
            ListMorphismMap m2 = (ListMorphismMap)d2.getCoordinate().getMap();
            for (int i = 0; i < m2.getFactorCount(); i++) {
                m1.appendFactor(m2.getFactor(i));
            }
        }
    }

    
    /**
     * Appends an element denotator to a list denotator.
     */
    public static ListDenotator appendElement(ListDenotator d, Denotator element)
            throws RubatoException {
        ListForm form = d.getListForm();
        
        if (!form.getForm().equals(element.getForm())) {
            throw new RubatoException("Lists.appendElement: Expected form "+
                                      "%1, but got form %2", form.getForm(), element.getForm());            
        }
        
        List<Denotator> factors = d.getFactors();
        Denotator factor = element;
        if (!d.getAddress().equals(element.getAddress())) {
            Module newAddress = Address.getCommonModule(d.getAddress(), element.getAddress());
            if (newAddress == null) {
                throw new RubatoException("List.appendElement: Could not find a common address for "+d+" and "+element);
            }
            factors = readdress(factors, newAddress);
            factor = factor.changeAddress(newAddress);
            factors.add(factor);
            return new ListDenotator(null, d.getListForm(), factors);        
        }
        else {
            factors.add(factor);
            return ListDenotator._make_unsafe(null, d.getAddress(), form, factors);
        }
    }
    
    
    /**
     * Appends a denotator to a list denotator.
     * This operation is destructive on the first argument.
     */
    public static void appendElementTo(ListDenotator d, Denotator element)
            throws RubatoException {
        d.appendFactor(d);
    }
    
    
    /**
     * Prepend the elements of <i>d2</i> to <i>d1</i>.
     * This operation is destructive on the first argument.
     * @throws RubatoException if d1 or d2 is not of the required form
     */
    public static void prependTo(ListDenotator d1, ListDenotator d2)
            throws RubatoException {
        Form form = processArguments(d1, d2);

        if (form == null) {
            throw new RubatoException("Lists.prepentTo: "+d1+" and "+d2+" have not the same factor form");
        }

        ListMorphismMap m1 = (ListMorphismMap)d1.getCoordinate().getMap();
        ListMorphismMap m2 = (ListMorphismMap)d2.getCoordinate().getMap();

        for (int i = 0; i < m2.getFactorCount(); i++) {
            m1.prependFactor(m2.getFactor(i));
        }
    }

    
    /**
     * Prepends a denotator to a list denotator.
     */
    public static ListDenotator prependElement(ListDenotator d, Denotator element)
            throws RubatoException {
        ListForm form = d.getListForm();
        
        if (!form.getForm().equals(element.getForm())) {
            throw new RubatoException("Lists.prependElement: Expected element of form "+
                                      "%1, but got %2", form.getForm(), element.getForm());            
        }
        
        List<Denotator> factors = d.getFactors();
        Denotator factor = element;
        if (!d.getAddress().equals(element.getAddress())) {
            Module newAddress = Address.getCommonModule(d.getAddress(), element.getAddress());
            if (newAddress == null) {
                throw new RubatoException("Lists.prepentElement: Could not find a common address for "+d+" and "+element);
            }
            factors = readdress(factors, newAddress);
            factor = factor.changeAddress(newAddress);
            factors.add(0, factor);
            return new ListDenotator(null, d.getListForm(), factors);        
        }
        else {
            factors.add(0, factor);
            return ListDenotator._make_unsafe(null, d.getAddress(), form, factors);
        }
    }

    
    /**
     * Prepends a denotator to a list denotator.
     * This operation is destructive on the first argument.
     */
    public static void prependElementTo(ListDenotator d, Denotator element)
            throws RubatoException {
        d.prependFactor(element);
    }
    
    
    /**
     * Returns a denotator, with <code>f</code> applied to the elements of <code>d</code>.
     * @param f the function to apply to the arguments, must have arity == 1
     * @throws RubatoException if <code>d</code> is not of the required form or
     *         <code>f</code> has arity != 1
     */
    public static Denotator map(Function f, ListDenotator d)
            throws RubatoException {
        boolean changed = false;

        ListForm form = d.getListForm();

        if (f.getArity() != 1) {
            throw new RubatoException("Lists.map: Expected arity 1, "+
                                      "but got %1", f.getArity());
        }
    
        ListMorphismMap m = (ListMorphismMap)d.getCoordinate().getMap();
        int len = m.getFactorCount();

        if (len == 0) {
            return d;
        }

        ArrayList<Denotator> denoList = new ArrayList<Denotator>(len);
        for (int i = 0; i < len; i++) {
            Denotator factor = m.getFactor(i);
            Denotator res = f.evaluate(factor);
            if (factor != res) {
                changed = true;
            }
            denoList.add(i, res);
        }

        if (!changed) {
            // the mapping hasn't changed anything so return the input denotator
            return d;
        }

        Form resForm = denoList.get(0).getForm();
        if (!resForm.equals(m.getFactor(0).getForm())) {
            form = new ListForm(NameDenotator.make("List",resForm), resForm);
        }

        return new ListDenotator(null, form, denoList);
    }
    

    /**
     * Applies the function <code>f</code> to every factor of the argument denotator.
     * @param f the function to apply to the argument factors, must have arity 1
     * @throws RubatoException if <code>f</code> has arity != 1
     */
    public static void apply(Function f, ListDenotator d)
            throws RubatoException {
        if (f.getArity() != 1) {
            throw new RubatoException("List.apply: Expected arity "+
                                      "1, but got %1", f.getArity());
        }
    
        ListMorphismMap map = (ListMorphismMap)d.getCoordinate().getMap();
        int len = map.getFactorCount();
        for (int i = 0; i < len; i++) {
            f.evaluate(map.getFactor(i));
        }
    }


    /**
     * Returns a denotator, where only the elements from the argument denotator
     * are contained that satisfy predicate p.
     * @param p the predicate that the elements must satisfy, must have arity 1
     * @throws RubatoException if <code>d</code> has not the required form or
     *                                  <code>p</code> has arity != 1
     */
    public static ListDenotator select(Predicate p, ListDenotator d)
            throws RubatoException {
        ListForm form = d.getListForm();
   
        if (p.getArity() != 1) {
            throw new RubatoException("Lists.select: Expected arity "+
                                      "1, but got %1", p.getArity());
        }

        ListMorphismMap map = (ListMorphismMap)d.getCoordinate().getMap();
        LinkedList<Denotator> denoList = new LinkedList<Denotator>();

        boolean changed = false;
        for (int i = 0; i < map.getFactorCount(); i++) {
            Denotator deno = map.getFactor(i);
            if (p.call(deno)) {
                denoList.add(deno);
            }
            else {
                changed = true;
            }
        }

        if (!changed) {
            // select returns the whole denotator
            return d;
        }

        return new ListDenotator(null, form, denoList);        
    }


    /**
     * Zips the function on the two argument lists.
     * If d1 is a list denotator [d1_1,...,d1_m] and
     * d2 is a list denotator [d2_1,...,d2_n], then 
     * zip(f,d1,d2) returns a list denotator 
     * [f(d1_1, d2_1),...,f(d1_k,d2_k)], where k = min(n,m).
     * @param f a function of arity 2, must have a first argument of the same
     * form as a list element of d1 and a second argument of the same form as a
     * list element of d2
     * @throws RubatoException if d1 or d2 is not of the required form
     * or f has not the required type
     */
    public static ListDenotator zip(Function f, ListDenotator d1, ListDenotator d2)
            throws RubatoException {
        ListForm f1 = d1.getListForm();
        ListForm f2 = d2.getListForm();
        ListForm form;
                
        Form fe1 = f1.getForm(0);
        Form fe2 = f2.getForm(0);
        
        if (f.getArity() != 2 || !f.getInputForm(0).equals(fe1) || !f.getInputForm(1).equals(fe2)) {
            throw new RubatoException("Lists.zip: function has not the required type");
        }
        
        ListMorphismMap m1 = (ListMorphismMap)d1.getCoordinate().getMap();
        ListMorphismMap m2 = (ListMorphismMap)d2.getCoordinate().getMap();
        
        LinkedList<Denotator> denoList = new LinkedList<Denotator>();
        
        for (int i = 0; i < Math.min(m1.getFactorCount(), m2.getFactorCount()); i++) {
            Denotator deno1 = m1.getFactor(i);
            Denotator deno2 = m2.getFactor(i);
            denoList.add(f.evaluate(deno1, deno2));
        }
        
        if (f.getOutputForm().equals(fe1)) {
            form = f1;
        }
        else if (f.getOutputForm().equals(fe2)) {
            form = f2;
        }
        else {
            form = new ListForm(NameDenotator.make("List",f.getOutputForm()),
                                f.getOutputForm());
        }
        
        return new ListDenotator(null, form, denoList);
    }    


    /**
     * Reduces the argument list denotator using the function f.
     * If d is a list denotator [d1_1,...,d1_m], then reduce(f, d) returns
     * f(...f(f(d_1, d_2),d_3)...d_m)
     * @param f a function of arity 2, must have both arguments of the
     * same form as the element form of the list denotator.
     * @throws RubatoException if d is not of the required form
     * or f has arity != 2
     */
    public static Denotator reduce(Function f, ListDenotator d)
            throws RubatoException{
        if (f.getArity() != 2) {
            throw new RubatoException("Lists.reduce: Expected arity 2, "+
                                      "but got %1", f.getArity());
        }
            
        ListMorphismMap map = (ListMorphismMap)d.getCoordinate().getMap();
        
        if (map.getFactorCount() < 2) {
            throw new RubatoException("Lists.reduce: denotator must have at least 2 factors");
        }
        
        Denotator res = f.evaluate(map.getFactor(0), map.getFactor(1));
        for (int i = 2; i < map.getFactorCount(); i++) {
            res = f.evaluate(res, map.getFactor(i));
        }
            
        return res;
    }
    

    /**
     * Sorts the list denotator according to canonical order.
     */
    public static ListDenotator sort(ListDenotator d) {
        List<Denotator> denoList = d.getFactors();
        Collections.sort(denoList);
        return ListDenotator._make_unsafe(null, d.getAddress(), d.getListForm(), denoList);
    }
    
    
    /**
     * Sorts the list denotator according to order induced by
     * the comparator <code>c</code>, which can also be a Predicate.
     */
    public static ListDenotator sort(ListDenotator d, Comparator<Denotator> c) {
        List<Denotator> denoList = d.getFactors();
        Collections.sort(denoList, c);
        return ListDenotator._make_unsafe(null, d.getAddress(), d.getListForm(), denoList);
    }


    /**
     * Removes duplicates from the list denotator.
     * Requires that the denotator has been sorted before
     * according to canonical order.
     */
    public static ListDenotator removeDuplicates(ListDenotator d) { 
        if (d.getFactorCount() < 2) {
            return d;
        }

        Iterator<Denotator> iter = d.iterator();
        LinkedList<Denotator> newList = new LinkedList<Denotator>();
        Denotator element = iter.next();
        newList.add(element);
        
        while (iter.hasNext()) {        
            Denotator nextElement = iter.next();
            if (!element.equals(nextElement)) {
                newList.add(nextElement);
                element = nextElement;
            }
        }
        
        return ListDenotator._make_unsafe(null, d.getAddress(), d.getListForm(), newList);
    }


    static private Form processArguments(ListDenotator d1, ListDenotator d2) {
        Form form1 = d1.getListForm().getForm();
        Form form2 = d2.getListForm().getForm();
        if (!form1.equals(form2)) {
            return null;
        }
        else {
            return form1;
        }
    }
    
    
    static private List<Denotator> readdress(List<Denotator> denotators, Module address) {
        List<Denotator> res = new LinkedList<Denotator>();
        for (Denotator d : denotators) {
            res.add(d.changeAddress(address));
        }
        return res;
    }

    
    private Lists() { /* not allowed */ }
}

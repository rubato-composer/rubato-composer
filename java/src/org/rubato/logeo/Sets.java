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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rubato.base.RubatoException;
import org.rubato.logeo.functions.Function;
import org.rubato.logeo.predicates.Predicate;
import org.rubato.math.module.Module;
import org.rubato.math.yoneda.*;


/**
 * This static class implements the usual set operations
 * on denotators of type power.
 * 
 * @author Gérard Milmeister
 */
public final class Sets {
    
    //
    // Set union
    //
    
    /**
     * Returns the set union of the specified power denotators.
     * @throws RubatoException if <code>d1</code> or <code>d2</code>
     *                         is not of the required form
     */
    public static PowerDenotator union(PowerDenotator d1, PowerDenotator d2)
            throws RubatoException {
        Form form = processArguments(d1, d2);
        if (form == null) {   
            throw new RubatoException("Sets.union: "+d1+" and "+d2+" do not have the same base form");
        }
        
        List<Denotator> factors1 = d1.getFactors();
        List<Denotator> factors2 = d2.getFactors();
        if (!d1.getAddress().equals(d2.getAddress())) {
            Module newAddress = Address.getCommonModule(d1.getAddress(), d2.getAddress());
            if (newAddress == null) {
                throw new RubatoException("Sets.union: Could not find a common address for "+d1+" and "+d2);
            }
            factors1 = readdress(factors1, newAddress);
            factors2 = readdress(factors2, newAddress);
            List<Denotator> factors = mergeLists(factors1, factors2);
            PowerDenotator res = new PowerDenotator(null, d1.getPowerForm(), factors);
            return res;
        }
        else {
            List<Denotator> factors = mergeLists(factors1, factors2);
            return PowerDenotator._make_unsafe(null, d1.getAddress(), d1.getPowerForm(), factors);
        }
    }

    
    /**
     * Returns the set union of argument list of denotators. 
     * @param denoList input array of power denotators must have
     *                 length > 0 and must have no null elements
     * @throws RubatoException if the denotators do not have the required form
     */
    public static PowerDenotator union(PowerDenotator ... denoList)
            throws RubatoException {
        if (denoList.length == 0) {
            return null;
        }
        PowerDenotator result = denoList[0];
        for (int i = 1; i < denoList.length; i++) {
            result = union(result, denoList[i]);
        }
        return result;
    }

    
    //
    // Set intersection
    //
    
    /**
     * Returns the set intersection of argument denotators.
     * @throws RubatoException if d1 and d2 are not of the required form
     */
    public static PowerDenotator intersection(PowerDenotator d1, PowerDenotator d2)
            throws RubatoException {
        Form form = processArguments(d1, d2);
        if (form == null) {
            throw new RubatoException("Sets.intersection: "+d1+" and "+d2+" have not the same factor form");
        }
        
        List<Denotator> factors1 = d1.getFactors();
        List<Denotator> factors2 = d2.getFactors();
        if (!d1.getAddress().equals(d2.getAddress())) {
            Module newAddress = Address.getCommonModule(d1.getAddress(), d2.getAddress());
            if (newAddress == null) {
                throw new RubatoException("Sets.intersection: Could not find a common address for "+d1+" and "+d2);
            }
            factors1 = readdress(factors1, newAddress);
            factors2 = readdress(factors2, newAddress);
            List<Denotator> factors = intersectLists(factors1, factors2);
            return new PowerDenotator(null, d1.getPowerForm(), factors);       
        }
        else {
            List<Denotator> factors = intersectLists(factors1, factors2);
            return PowerDenotator._make_unsafe(null, d1.getAddress(), d1.getPowerForm(), factors);
        }
    }


    /**
     * Returns the set intersection of argument list of denotators.
     * @param denoList input array of power denotators must have
     *                 length > 0 and must have no null elements
     * @throws RubatoException if the denotators have not the required form
     */
    public static PowerDenotator intersection(PowerDenotator ... denoList)
            throws RubatoException {
        if (denoList.length == 0) {
            return null;
        }
        PowerDenotator result = denoList[0];
        for (int i = 1; i < denoList.length; i++) {
            result = intersection(result, denoList[i]);
        }
        return result;
    }

    
    //
    // Set difference
    //

    /**
     * Returns the set difference of argument denotators.
     * @throws RubatoException if d1 and d2 are not of the required form
     */
    public static PowerDenotator difference(PowerDenotator d1, PowerDenotator d2)
            throws RubatoException {
        Form form = processArguments(d1, d2);
        if (form == null) {
            throw new RubatoException("Sets.difference: "+d1+" and "+d2+" have not the same factor form");
        }
        
        List<Denotator> factors1 = d1.getFactors();
        List<Denotator> factors2 = d2.getFactors();
        if (!d1.getAddress().equals(d2.getAddress())) {
            Module newAddress = Address.getCommonModule(d1.getAddress(), d2.getAddress());
            if (newAddress == null) {
                throw new RubatoException("Sets.difference: Could not find a common address for "+d1+" and "+d2);
            }
            factors1 = readdress(factors1, newAddress);
            factors2 = readdress(factors2, newAddress);
            List<Denotator> factors = differenceLists(factors1, factors2);
            return new PowerDenotator(null, d1.getPowerForm(), factors);       
        }
        else {
            List<Denotator> factors = differenceLists(factors1, factors2);
            return PowerDenotator._make_unsafe(null, d1.getAddress(), d1.getPowerForm(), factors);
        }
    }
    
    
    /**
     * Returns the set difference of argument list of denotators.
     * @throws RubatoException if the denotators have not the required form
     */
    public static PowerDenotator difference(PowerDenotator ... denoList)
            throws RubatoException {
        if (denoList.length == 0) {
            return null;
        }
        PowerDenotator result = denoList[0];
        for (int i = 1; i < denoList.length; i++) {
            result = difference(result, denoList[i]);
        }
        return result;
    }

    
    //
    // Symmetric set difference
    //
    
    /**
     * Returns the symmetric set difference of argument denotators.
     * @throws RubatoException if d1 and d2 are not of the required form
     */
    public static PowerDenotator symmetric(PowerDenotator d1, PowerDenotator d2)
            throws RubatoException {
        Form form = processArguments(d1, d2);
        if (form == null) {
            throw new RubatoException("Sets.symmetric: "+d1+" and "+d2+" have not the same factor form");
        }

        List<Denotator> factors1 = d1.getFactors();
        List<Denotator> factors2 = d2.getFactors();
        if (!d1.getAddress().equals(d2.getAddress())) {
            Module newAddress = Address.getCommonModule(d1.getAddress(), d2.getAddress());
            if (newAddress == null) {
                throw new RubatoException("Sets.symmetric: Could not find a common address for "+d1+" and "+d2);
            }
            factors1 = readdress(factors1, newAddress);
            factors2 = readdress(factors2, newAddress);
            List<Denotator> factors = symmetricdiffLists(factors1, factors2);
            return new PowerDenotator(null, d1.getPowerForm(), factors);       
        }
        else {
            List<Denotator> factors = symmetricdiffLists(factors1, factors2);
            return PowerDenotator._make_unsafe(null, d1.getAddress(), d1.getPowerForm(), factors);
        }
    }
    

    /**
     * Returns the symmetric set difference of argument list of denotators.
     * @throws RubatoException if the denotators have not the required form
     */
    public static PowerDenotator symmetric(PowerDenotator ... denoList)
            throws RubatoException {
        if (denoList.length == 0) {
            return null;
        }
        PowerDenotator result = denoList[0];
        for (int i = 1; i < denoList.length; i++) {
            result = symmetric(result, denoList[i]);
        }
        return result;
    }

    
    /**
     * Return a denotator, where the function f is applied on the elements of
     * the argument denotator.
     * @param f the function to apply to the arguments, must have arity 1
     * @throws RubatoException if d is not of type power or f has arity != 1
     */
    public static Denotator map(Function f, PowerDenotator d)
            throws RubatoException {
        boolean changed = false;        
        PowerForm form = d.getPowerForm();

        if (f.getArity() != 1) {
            throw new RubatoException("Sets.map: Expected arity 1, "+
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
            form = FormFactory.makePowerForm("Set("+resForm.getNameString()+")", resForm);
        }

        return new PowerDenotator(null, form, denoList);
    }


    /**
     * Returns a denotator, where only the elements from the argument denotator
     * are contained that satisfy predicate p.
     * @param p the predicate that the elements must satisfy, must have arity 1
     * @throws RubatoException if d is not of type power or p has arity != 1
     */
    public static Denotator select(Predicate p, PowerDenotator d)
            throws RubatoException {
        boolean changed = false;
        PowerForm form = d.getPowerForm();

        if (p.getArity() != 1) {
            throw new RubatoException("Sets.select: Expected arity 1, "+
                                      "but got %1", p.getArity());
        }

        ListMorphismMap m = (ListMorphismMap)d.getCoordinate().getMap();
        LinkedList<Denotator> denoList = new LinkedList<Denotator>();

        for (int i = 0; i < m.getFactorCount(); i++) {
            Denotator deno = m.getFactor(i);
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

        return new PowerDenotator(null, form, denoList);        
    }
    
    
    /**
     * Returns the Cartesian product of two denotators of type power.
     * @throws RubatoException if d1 or d2 is not of type power
     */
    public static PowerDenotator cartesian(PowerForm resForm, PowerDenotator d1, PowerDenotator d2)
            throws RubatoException {
        ListMorphismMap m1 = (ListMorphismMap)d1.getCoordinate().getMap();
        ListMorphismMap m2 = (ListMorphismMap)d2.getCoordinate().getMap();

        if (m1.getFactorCount() == 0 || m2.getFactorCount() == 0) {
            return null;
        }
        
        LimitForm pf = (LimitForm)resForm.getForm();
        LinkedList<Denotator> denoList = new LinkedList<Denotator>();

        for (int i = 0; i < m1.getFactorCount(); i++) {
            for (int j = 0; j < m2.getFactorCount(); j++) {
                Denotator df1 = m1.getFactor(i);
                Denotator df2 = m2.getFactor(j);
                LinkedList<Denotator> denoList1 = new LinkedList<Denotator>();
                denoList1.add(df1);
                denoList1.add(df2);
                Denotator d = new LimitDenotator(null, pf, denoList1);
                denoList.add(d);
            }
        }
        
        return new PowerDenotator(null, resForm, denoList);
    }
    
    
    /**
     * Returns the disjoint union of two denotators of type power.
     * @throws RubatoException if d1 or d2 is not of type power
     */
    public static Denotator disjointUnion(PowerDenotator d1, PowerDenotator d2)
            throws RubatoException {
        PowerForm form1 = d1.getPowerForm();
        PowerForm form2 = d2.getPowerForm();

        ListMorphismMap m1 = (ListMorphismMap)d1.getCoordinate().getMap();
        ListMorphismMap m2 = (ListMorphismMap)d2.getCoordinate().getMap();

        Form f1 = form1.getForm(0);
        Form f2 = form2.getForm(0);
        
        ColimitForm colimitForm = FormFactory.makeColimitForm("Limit("+f1.getNameString()+","+f2.getNameString()+")",f1,f2);
        PowerForm resForm = FormFactory.makePowerForm("Set("+colimitForm.getNameString()+")",colimitForm);
        
        LinkedList<Denotator> denoList = new LinkedList<Denotator>();
        for (int i = 0; i < m1.getFactorCount(); i++) {
            denoList.add(new ColimitDenotator(null, colimitForm, 0, m1.getFactor(i)));
        }
        for (int i = 0; i < m2.getFactorCount(); i++) {
            denoList.add(new ColimitDenotator(null, colimitForm, 1, m2.getFactor(i)));
        }
        
        return new PowerDenotator(null, resForm, denoList);
    }
    
    
    //
    // Adding elements
    //
    
    public static PowerDenotator addElement(PowerDenotator d, Denotator element)
            throws RubatoException {        
        List<Denotator> newList = new LinkedList<Denotator>();
        newList.addAll(d.getFactors());
        newList.add(element);
        return new PowerDenotator(null, d.getPowerForm(), newList);
    }

    
    public static PowerDenotator addElements(PowerDenotator d, Denotator ... elements)
            throws RubatoException {        
        List<Denotator> newList = new LinkedList<Denotator>();
        newList.addAll(d.getFactors());
        for (int i = 0; i < elements.length; i++) {
            newList.add(elements[i]);
        }
        return new PowerDenotator(null, d.getPowerForm(), newList);
    }
    

    public static PowerDenotator addElements(PowerDenotator d, List<Denotator> elements)
            throws RubatoException {        
        List<Denotator> newList = new LinkedList<Denotator>();
        newList.addAll(d.getFactors());
        newList.addAll(elements);
        return new PowerDenotator(null, d.getPowerForm(), newList);
    }

    
    //
    // Relations
    //
    
    /**
     * Returns true iff <code>d1</code> is a subset of <code>d2</code>.
     */
    public static boolean subset(PowerDenotator d1, PowerDenotator d2) {
        Form form = processArguments(d1, d2);
        if (form == null) {
            return false;
        }
        for (Denotator d : d1) {
            if (_contains(d2, d)) {
                return false;
            }
        }
        return true;
    }

    
    /**
     * Returns true iff <code>p</code> contains <code>d</code>.
     */
    public static boolean contains(PowerDenotator p, Denotator d) {
        if (!p.getPowerForm().getForm().equals(d.getForm())) {
            return false;
        }
        else {
            return _contains(p, d);
        }
    }
    
    
    private static boolean _contains(PowerDenotator p, Denotator d) {
        ArrayList<Denotator> factors = p.getListMorphismMap().getFactors();
        int i = 0;
        int j = factors.size()-1;
        while (i <= j) {
            int t = (i+j)/2;
            Denotator s = factors.get(t);
            int c = d.compareTo(s);
            if (c == 0) {
                return true;
            }
            else if (c < 0) {
                j = t-1;
            }
            else {
                i = t+1;
            }
        }   
        return false;
    }

    
    //
    // Private methods
    //

    static private List<Denotator> mergeLists(List<Denotator> list1, List<Denotator> list2) {
        LinkedList<Denotator> newList = new LinkedList<Denotator>();
        int count1 = list1.size();
        int count2 = list2.size();
        int i1 = 0;
        int i2 = 0;
        while (i1 < count1 && i2 < count2) {
            Denotator d1 = list1.get(i1);
            Denotator d2 = list2.get(i2);
            int comp = d1.compareTo(d2);
            if (comp < 0) {
                newList.add(d1);
                i1++;
            }
            else if (comp > 0) {
                newList.add(d2);
                i2++;
            }
            else {
                newList.add(d1);
                i1++;
                i2++;
            }            
        }
        while (i1 < count1) {
            newList.add(list1.get(i1));
            i1++;
        }
        while (i2 < count2) {
            newList.add(list2.get(i2));
            i2++;
        }
        return newList;
    }

    
    static private List<Denotator> intersectLists(List<Denotator> list1, List<Denotator> list2) {
        LinkedList<Denotator> newList = new LinkedList<Denotator>();
        int count1 = list1.size();
        int count2 = list2.size();
        int i1 = 0;
        int i2 = 0;
        while (i1 < count1 && i2 < count2) {
            while (i1 < count1 && i2 < count2 && list1.get(i1).compareTo(list2.get(i2)) < 0) {
                i1++;
            }
            while (i1 < count1 && i2 < count2 && list1.get(i1).compareTo(list2.get(i2)) > 0) {
                i2++;
            }
            if (i1 < count1 && i2 < count2 && list1.get(i1).equals(list2.get(i2))) {
                newList.add(list1.get(i1));
                i1++;
                i2++;
            }
        }
        return newList;
    }


    static private List<Denotator> differenceLists(List<Denotator> list1, List<Denotator> list2) {
        LinkedList<Denotator> newList = new LinkedList<Denotator>();
        int count1 = list1.size();
        int count2 = list2.size();
        int i1 = 0;
        int i2 = 0;
        while (i1 < count1 && i2 < count2) {
            Denotator d1 = list1.get(i1);
            Denotator d2 = list2.get(i2);
            int comp = d1.compareTo(d2);
            if (comp < 0) {
                newList.add(d1);
                i1++;
            }
            else if (comp > 0) {
                i2++;
            }
            else {
                i1++;
                i2++;
            }            
        }
        while (i1 < count1) {
            newList.add(list1.get(i1));
            i1++;
        }
        return newList;
    }


    static private List<Denotator> symmetricdiffLists(List<Denotator> list1, List<Denotator> list2) {
        LinkedList<Denotator> newList = new LinkedList<Denotator>();
        int count1 = list1.size();
        int count2 = list2.size();
        int i1 = 0;
        int i2 = 0;
        while (i1 < count1 && i2 < count2) {
            while (i1 < count1 && i2 < count2 && list1.get(i1).compareTo(list2.get(i2)) < 0) {
                newList.add(list1.get(i1));
                i1++;
            }
            while (i1 < count1 && i2 < count2 && list1.get(i1).compareTo(list2.get(i2)) > 0) {
                newList.add(list2.get(i2));
                i2++;
            }
            if (i1 < count1 && i2 < count2 && list1.get(i1).equals(list2.get(i2))) {
                i1++;
                i2++;
            }
        }
        return newList;
    }


    static private Form processArguments(PowerDenotator d1, PowerDenotator d2) {
        if (!d1.getPowerForm().getForm().equals(d2.getPowerForm().getForm())) {
            return null;
        }
        return d1.getPowerForm().getForm();
    }

    
    static private List<Denotator> readdress(List<Denotator> denotators, Module address) {
        List<Denotator> res = new LinkedList<Denotator>();
        for (Denotator d : denotators) {
            res.add(d.changeAddress(address));
        }
        return res;
    }

    
    private Sets() { /* not allowed */ }          
}

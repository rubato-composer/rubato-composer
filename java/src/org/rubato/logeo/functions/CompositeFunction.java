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

package org.rubato.logeo.functions;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;


/**
 * A function that is the composition of two functions.
 *
 * @author Gérard Milmeister
 */
public class CompositeFunction extends AbstractFunction {

    /**
     * Creates a composed function h from f and g, i.e., h(x) = f(g(x)).
     */
    public CompositeFunction(Function f, Function g) {
        this.f = f;
        fArity = f.getArity();
        this.g = new Function[1];
        this.g[0] = g;
        gArity = g.getArity();
        name = "("+f.getName()+" o "+g.getName()+")";
    }


    /**
     * Creates a composed function h from f and a list g.
     * I.e. h(x) = f(g1(x),...,gn(x)).
     * @param g must be of length > 0
     */
    public CompositeFunction(Function f, Function g[])
            throws RubatoException {
        fArity = f.getArity();
        gArity = g[0].getArity();
        if (g.length != fArity) {
            throw new RubatoException("Number of g must match arity of f");
        }
        for (int i = 1; i < g.length; i++) {            
            if (g[i] == null || gArity != g[i].getArity()) {
                throw new RubatoException("Member "+i+" of g has different arity");
            }
        }
        this.f = f;
        this.g = g;
        
        StringBuilder buf = new StringBuilder(20);
        buf.append("(");
        buf.append(f.getName());
        buf.append(" o ");
        buf.append("(");
        buf.append(g[0].getName());
        for (int i = 1; i < g.length; i++) {
            buf.append(",");
            buf.append(g[i].getName());
        }
        buf.append("))");
        name = buf.toString();
    }
    

    /**
     * Evaluates the function with a list of arguments.
     * The function must have arity equal to the length of the list.
     */
    public Denotator evaluate(Denotator ... denotators)
            throws RubatoException {
        Denotator[] grList = new Denotator[fArity];
        for (int i = 0; i < fArity; i++) {
            grList[i] = g[i].evaluate(denotators);
        }
        return callf(grList);
    }


    /**
     * Returns the arity of the function.
     */
    public int getArity() {
        return gArity;
    }


    /**
     * Returns the name of the function.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the ith argument form.
     */
    public Form getInputForm(int i) {
        return g[0].getInputForm(i);
    }


    /**
     * Returns the result form.
     */
    public Form getOutputForm() {
        return f.getOutputForm();
    }


    private Denotator callf(Denotator ... denotators)
            throws RubatoException {
        return f.evaluate(denotators);
    }


    private Function   f;
    private int        fArity;
    
    private Function[] g;
    private int        gArity;
    
    private String     name;
}

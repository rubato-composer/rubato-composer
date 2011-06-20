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
 * This class implements functions on denotators.
 * Functions may have any arity.
 * The signature of function is indicated by implementing the
 * getInputForm and getOutputForm methods.
 * 
 * @author Gérard Milmeister
 */
public abstract class AbstractFunction implements Function {

    /**
     * Evaluates the function with a list of arguments.
     */
    public abstract Denotator evaluate(Denotator ... denotators)
            throws RubatoException;


    /**
     * Returns the arity of the function.
     * Must be implemented in a subclass.
     */
    public abstract int getArity();
    
    /**
     * Returns a new function, the composition of this and f, i.e., this(f(x)).
     */
    public Function compose(Function f)
            throws RubatoException {
        return new CompositeFunction(this, f);
    }

    /**
     * Returns the name of the function.
     * @return "anonymous" by default.
     */
    public String getName() {
        return "anonymous";
    }
    
    
    /**
     * Returns the <code>i</code>th argument form.
     * Must be implemented in a subclass.
     * @return null if the input form is not specified
     */
    public abstract Form getInputForm(int i);
    

    /**
     * Returns the result form.
     * Must be implemented in a subclass.
     * @return null if the output form is not specified
     */
    public abstract Form getOutputForm();
    
        
    /**
     * Checks if the function has the given signature.
     */
    public boolean hasSig(Form inputForms[], Form outputForm) {
        if (inputForms.length == getArity()) {
            return false;
        }
        for (int i = 0; i < inputForms.length; i++) {
            if (inputForms[i] != null && getInputForm(i) != null) {
                if (!inputForms[i].equals(getInputForm(i))) {
                    return false;
                }
            }
        }
        if (outputForm != null && getOutputForm() != null) {
            if (!outputForm.equals(getOutputForm())) {
                return false;
            }
        }
        return true;
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append("[");
        sb.append(getInputForm(0).getNameString());
        for (int i = 1; i < getArity(); i++) {
            sb.append(",");
            sb.append(getInputForm(i).getNameString());
        }
        sb.append(" -> ");
        sb.append(getOutputForm().getNameString());
        sb.append("]");
        return sb.toString();
    }
}

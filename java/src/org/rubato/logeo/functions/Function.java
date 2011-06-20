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
 * This is the general interface for functions with an arbitrary
 * number of arguments.
 * Arguments and results are always denotators.
 * To implement a function, extend the AbstractFunction class or one
 * of the specialized function classes like IntegerFunction.
 *
 * @author Gérard Milmeister
 */
public interface Function {

    /**
     * Evaluates the function with a list of arguments.
     */
    Denotator evaluate(Denotator ... denotators)
        throws RubatoException;

    /**
     * Returns the arity of the function.
     */
    int getArity();

    /**
     * Returns a new function, the composition of this and f, i.e., this(f(x)).
     * @throws RubatoException is composition fails
     */
    Function compose(Function f) throws RubatoException;

    /**
     * Returns the name of the function.
     */
    String getName();

    /**
     * Returns the ith argument form.
     */
    Form getInputForm(int i);

    /**
     * Returns the result form.
     * Must be implemented in a subclass.
     */
    Form getOutputForm();    
    
    /**
     * Checks if the function has the given signature.
     */
    boolean hasSig(Form inputForms[], Form outputForm);
}

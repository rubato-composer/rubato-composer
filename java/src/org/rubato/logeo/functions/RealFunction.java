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

import static org.rubato.logeo.DenoFactory.makeDenotator;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleDenotator;


/**
 * This class can be used to implement function on reals.
 * 
 * @author Gérard Milmeister
 */
public abstract class RealFunction extends AbstractFunction {

    public abstract double evaluate(double ... doubles);

    public Denotator evaluate(Denotator ... denotators)
            throws RubatoException {
        SimpleDenotator[] simples = new SimpleDenotator[denotators.length];
        for (int i = 0; i < denotators.length; i++) {
            if (denotators[i] instanceof SimpleDenotator) {
                simples[i] = (SimpleDenotator)denotators[i];
            }
            else {
                throw new RubatoException("Argument denotators must be of type simple");
            }
        }
        return evaluate(simples);
    }

    
    public Denotator evaluate(SimpleDenotator ... denotators) {
        double reals[] = new double[denotators.length];
        for (int i = 0; i < reals.length; i++) {
            reals[i] = denotators[i].getInteger();
        }
        return makeDenotator(denotators[0].getSimpleForm(), evaluate(reals));
    }


    public abstract int getArity();

    public Form getInputForm(int i) {
        return null;
    }

    public Form getOutputForm() {
        return null;
    }
}

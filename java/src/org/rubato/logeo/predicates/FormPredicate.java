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

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;


/**
 * A predicate that matches denotators of a given form.
 * 
 * @author Gérard Milmeister
 */

public class FormPredicate extends AbstractPredicate {
        
    /**
     * Creates a predicate that satisfies denotator with the given form.
     */
    public FormPredicate(Form form) {
        this.form = form;
    }

    
    public boolean call(Denotator ... denotators) {
        return denotators[0].hasForm(form);
    }
    
    
    public int getArity() {
        return 1;
    }
    
    
    public Form getInputForm(int i) {
        return null;
    }
    
    
    private Form form;
}

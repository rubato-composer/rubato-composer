/*
 * Copyright (C) 2004 Gérard Milmeister
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
import org.rubato.logeo.DenoFactory;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.*;


/**
 * A function from a simple denotator to a simple denotator,
 * implement with a module morphism on the underlying modules.
 * 
 * @author Gérard Milmeister
 */
public class ModuleMorphismFunction extends AbstractFunction {

    /**
     * Creates a function implemented by <code>morphism</code>.
     * The domain and the codomain must be equal.
     * The result denotator has the same form as the input
     * denotator.
     * 
     * @throws RubatoException
     */

    public ModuleMorphismFunction(ModuleMorphism morphism)
            throws RubatoException {
        this.morphism = morphism;
        this.resultForm = null;
        if (resultForm.getModule().equals(morphism.getCodomain())) {
            throw new RubatoException("Domain and codomain must be equal");
        }
    }


    /**
     * Creates a function implemented by <code>morphism</code>.
     * The domain and codomain are arbitrary, therefore
     * the form of the result denotator must be specified.
     * This (simple) form must have a module equal to
     * the codomain of the morphism.
     * 
     * @throws RubatoException
     */
    public ModuleMorphismFunction(ModuleMorphism morphism, SimpleForm resultForm)
            throws RubatoException {
        this.morphism = morphism;
        this.resultForm = resultForm;
        if (resultForm.getModule().equals(morphism.getCodomain())) {
            throw new RubatoException("Module of form must match codomain of morphism");
        }
    }

    
    public Denotator evaluate(Denotator ... denotators)
            throws RubatoException {
        if (denotators.length != 1) {
            throw new RubatoException("Number of arguments must be "+getArity());
        }
        else if (denotators[0].getType() != Denotator.SIMPLE) {
            throw new RubatoException("Argument denotator must be of type simple");
        }
        else {
            SimpleDenotator denotator = (SimpleDenotator)denotators[0];
            ModuleElement element = denotator.getElement();
            if (morphism.getDomain().hasElement(element)) {
                ModuleElement result = morphism.map(element);
                Form form = (resultForm == null)?denotator.getForm():resultForm;
                return DenoFactory.makeDenotator(form, result);
            }
            else {
                throw new RubatoException("Argument denotator has wrong module");
            }
        }
    }

    
    public int getArity() {
        return 1;
    }

    
    public Function compose(Function f)
            throws RubatoException {
        if (f instanceof ModuleMorphismFunction) {
            ModuleMorphism m = ((ModuleMorphismFunction)f).getModuleMorphism();
            ModuleMorphism cm = morphism.compose(m);
            ModuleMorphismFunction result = new ModuleMorphismFunction();
            result.morphism = cm;
            result.resultForm = resultForm;
            return result;
        }
        else {
            return super.compose(f);
        }
    }

    
    public String getName() {
        return morphism.toString();
    }

    
    public Form getInputForm(int i) {
        return null;
    }
    

    public Form getOutputForm() {
        return null;
    }

    
    public ModuleMorphism getModuleMorphism() {
        return morphism;
    }

    
    private ModuleMorphismFunction() {}
    
    private ModuleMorphism morphism;
    private SimpleForm     resultForm;
}

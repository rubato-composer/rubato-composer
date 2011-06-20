/*
 * Copyright (C) 2006 Gérard Milmeister
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

package org.rubato.base;

import java.util.List;

import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;

public interface RubatoDictionary {

    /**
     * Returns a list of all forms.
     */
    public List<Form> getForms();
    
    /**
     * Returns the form for the given <code>name</code>.
     * @return null if there is no form of the given name
     */
    public Form getForm(String name);
    
    /**
     * Returns a list of all denotators.
     */
    public List<Denotator> getDenotators();
    
    /**
     * Returns the denotator for the given <code>name</code>.
     * @return null if there is no denotator of the given name
     */
    public Denotator getDenotator(String name);

    /**
     * Returns the module for the given <code>name</code>.
     * @return null if there is no module of the given name
     */
    public Module getModule(String name);
    
    /**
     * Returns the module element for the given <code>name</code>.
     * @return null if there is no module element of the given name
     */
    public ModuleElement getModuleElement(String name);
    
    /**
     * Returns the module morphism for the given <code>name</code>.
     * @return null if there is no module morphism of the given name
     */
    public ModuleMorphism getModuleMorphism(String name);
}

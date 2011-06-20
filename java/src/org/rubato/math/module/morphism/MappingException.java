/*
 * Copyright (C) 2001, 2005 Gérard Milmeister
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

package org.rubato.math.module.morphism;

import org.rubato.base.RubatoException;
import org.rubato.math.module.ModuleElement;

/**
 * Exception thrown when a module morphism fails to map an element.
 * This exception is thrown, for example, when the element
 * is not in the domain of the morphism.
 * It can, however, also be thrown, when arithmetical error occurs,
 * for example, division by zero.
 * 
 * @author Gérard Milmeister
 */
public final class MappingException extends RubatoException {

    /**
     * Creates a MappingException with a standard message string.
     * The standard message indicates the module element, the required
     * domain, and morphism where the failure occurred.
     *
     * @param msg a message to prepend to the standard message
     * @param element the ModuleElement to be mapped
     * @param morphism the ModuleMorphism where the exception occurred 
     */
    public MappingException(String msg, ModuleElement element, ModuleMorphism morphism) {
        super(msg+"Failed to map "+element+" in domain "+morphism.getDomain()+" in "+
              "morphism "+morphism);
        this.element = element;
        this.morphism = morphism;
    }
    

    /**
     * Creates a MappingException with a standard message string.
     * The standard message indicates the module element, the required
     * domain, and morphism where the failure occurred.
     *
     * @param element the ModuleElement to be mapped
     * @param morphism the ModuleMorphism where the exception occurred 
     */
    public MappingException(ModuleElement element, ModuleMorphism morphism) {
        this("", element, morphism);
    }

    
    /**
     * Returns the element that failed to be mapped.
     */
    public ModuleElement getElement() {
        return element;
    }

    
    /**
     * Returns the morphism where the failure occurred.
     */
    public ModuleMorphism getModuleMorphism() {
        return morphism;
    }

    
    private ModuleMorphism morphism;
    private ModuleElement  element;
}

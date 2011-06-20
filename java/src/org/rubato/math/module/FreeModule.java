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

package org.rubato.math.module;

import org.rubato.math.module.morphism.ModuleMorphism;

/**
 * The interface for free modules over rings.
 * @see org.rubato.math.module.FreeElement
 * 
 * @author Gérard Milmeister
 */
public interface FreeModule extends Module {

    /**
     * Returns true if this free module is a vector space.
     */
    public boolean isVectorspace();
    
    /**
     * Returns the unit vector with 1 at position <code>i</code>.
     */
    public ModuleElement getUnitElement(int i);
    
    /**
     * Returns a module morphism that projects the free module
     * at the component <code>index</code>.
     * @param index the number of the component to project to,
     *        the index will be clamped between 0 and the dimension-1 of
     *        the free module
     */
    public ModuleMorphism getProjection(int index);

    /**
     * Returns a module morphism that injects a ring into the free module
     * at the component <code>index</code>.
     * @param index the number of the component to project to,
     *        the index will be clamped between 0 and the dimension-1 of
     *        the free module
     */
    public ModuleMorphism getInjection(int index);
}

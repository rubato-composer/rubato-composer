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
import org.rubato.math.module.morphism.TranslationMorphism;

/**
 * The abstract base class for <code>proper</code> free modules.
 * All free modules that are <code>proper</code> free modules are derived
 * from this. <code>Proper</code> means that the module is not of
 * dimension 1, because in that case the corresponding ring is used.
 * @see org.rubato.math.module.ProperFreeElement
 * 
 * @author Gérard Milmeister
 */
public abstract class ProperFreeModule implements FreeModule {

    public ProperFreeModule(int dimension) {
        dimension = (dimension < 0)?0:dimension;
        this.dimension = dimension;
    }
    
    
    public final ModuleMorphism getIdentityMorphism() {
        return ModuleMorphism.getIdentityMorphism(this);
    }

    
    public final ModuleMorphism getTranslation(ModuleElement element) {
        return TranslationMorphism.make(this, element);
    }

    
    public final ModuleMorphism getProjection(int index) {
        if (index < 0) { index = 0; }
        if (index > getDimension()-1) { index = getDimension()-1; }
        return _getProjection(index);
    }    
    
    
    public final ModuleMorphism getInjection(int index) {
        if (index < 0) { index = 0; }
        if (index > getDimension()-1) { index = getDimension()-1; }
        return _getInjection(index);
    }    
    
    
    public final boolean isRing() {
        return false;
    }

    
    public int compareTo(Module object) {
        if (object instanceof FreeModule) {
            FreeModule m = (FreeModule)object;
            int c;
            if ((c = getRing().compareTo(m.getRing())) != 0) {
                return c;
            }
            else {
                return getDimension()-m.getDimension(); 
            }
        }
        else {
            return toString().compareTo(object.toString());
        }
    }
    
    
    public final int getDimension() {
        return dimension;
    }

    
    protected abstract ModuleMorphism _getProjection(int index);
    
    protected abstract ModuleMorphism _getInjection(int index);

    private final int dimension;
}

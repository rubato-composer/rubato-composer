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

package org.rubato.composer.plugin;

import org.rubato.composer.dialogs.morphisms.JMorphismContainer;
import org.rubato.composer.dialogs.morphisms.JMorphismType;
import org.rubato.math.module.Module;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.xml.Dispatcher;

public abstract class ModuleMorphismPlugin implements Plugin {

    public final void init() {
        Dispatcher.getDispatcher().addModuleMorphism(getPrototype());
    }
    
    public abstract ModuleMorphism getPrototype();
    
    public JMorphismType getJMorphismType(JMorphismContainer container) {
        return null;
    }
    
    public ModuleMorphism getModuleMorphism(Module domain, Module codomain) {
        return null;
    }
    
    public abstract boolean checkSignature(Module domain, Module codomain);
    
    public abstract String getName();
}

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

package org.rubato.logeo.reform;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.Ring;
import org.rubato.math.module.morphism.CanonicalMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.*;

abstract class SimpleLimitReformer extends SimpleReformer {

    /**
     * Creates a reformer from the simple form <code>from</code>
     * to the limit form <code>to</code>.
     * The factor forms of the limit form must be simple forms. 
     */
    public static SimpleReformer make(SimpleForm from, LimitForm to) {
        List<Module> toModules = new LinkedList<Module>();
        List<Module> fromModules = new LinkedList<Module>();
        List<ModuleMorphism> maps = new LinkedList<ModuleMorphism>();
        
        // retrieve the modules from the limit form
        // and compute the total dimension
        if (!collectModules(to, toModules)) {
            return null;
        }
        int dim = 0;
        for (Module toModule : toModules) {
            dim += toModule.getDimension();
        }
        
        if (dim == from.getModule().getDimension()) {
            Ring ring = from.getModule().getRing();
            for (Module toModule : toModules) {
                Module fromModule = ring.getFreeModule(toModule.getDimension());
                ModuleMorphism map = CanonicalMorphism.make(fromModule, toModule);
                if (map == null) {
                    return null;
                }
                else {
                    maps.add(map);
                    fromModules.add(fromModule);
                }
            }
            return new SimpleLimitFreeReformer(fromModules, maps, to);
        }
        else {
            return null;
        }
    }

    
    private static boolean collectModules(LimitForm to, List<Module> toModules) {
        for (int i = 0; i < to.getFormCount(); i++) {
            Form f = to.getForm(i);
            if (f instanceof SimpleForm) {
                Module fromModule = ((SimpleForm)f).getModule(); 
                toModules.add(fromModule);
            }
            else if (f instanceof LimitForm) {
                if (!collectModules((LimitForm)f, toModules)) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return true;
    }

    
    private static class SimpleLimitFreeReformer extends SimpleLimitReformer {
        
        public SimpleLimitFreeReformer(List<Module> fromModules, List<ModuleMorphism> maps, LimitForm to) {
            this.fromModules = fromModules;
            this.maps = maps;
            this.to = to;
        }
    
        public Denotator reform(Denotator d)
                throws RubatoException {
            Module address = d.getAddress();
            ModuleElement fromElement = ((SimpleDenotator)d).getElement();
            List<ModuleElement> elements = new LinkedList<ModuleElement>();
            List<ModuleElement> toElements = new LinkedList<ModuleElement>();
            
            int pos = 0;
            Iterator<ModuleMorphism> maps_iter = maps.iterator();
            for (Module fromModule : fromModules) {
                elements.clear();
                for (int j = 0; j < fromModule.getDimension(); j++) {
                    elements.add(fromElement.getComponent(pos));
                    pos++;
                }
                ModuleMorphism map = maps_iter.next();
                toElements.add(map.map(fromModule.createElement(elements)));
            }

            return distributeElements(toElements, address, to);
        }
        
        private Denotator distributeElements(List<ModuleElement> toElements, Module address, LimitForm toForm) {
            List<Denotator> list = new LinkedList<Denotator>();
            for (Form f : toForm.getForms()) {
                if (f instanceof LimitForm) {
                    list.add(distributeElements(toElements, address, (LimitForm)f));
                }
                else if (f instanceof SimpleForm) {
                    list.add(SimpleDenotator._make_unsafe(null, (SimpleForm)f, address, toElements.remove(0)));
                }
            }
            return LimitDenotator._make_unsafe(null, address, toForm, list);
        }
        
        private List<Module>         fromModules;
        private List<ModuleMorphism> maps;
        private LimitForm            to;
    }
}

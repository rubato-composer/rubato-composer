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

abstract class LimitSimpleReformer extends LimitReformer {

    /**
     * Creates a reformer from the limitform <code>from</code>
     * to the simple form <code>to</code>.
     * The factor forms of the limit form must be simple forms. 
     */
    public static LimitReformer make(LimitForm from, SimpleForm to) {
        List<Module> fromModules = new LinkedList<Module>();
        List<ModuleMorphism> maps = new LinkedList<ModuleMorphism>();
        
        // retrieve the modules from the limit form
        // and compute the total dimension
        if (!collectModules(from, fromModules)) {
            return null;
        }
        int dim = 0;
        for (Module fromModule : fromModules) {
            dim += fromModule.getDimension();
        }
        
        if (dim == to.getModule().getDimension()) {
            Ring ring = to.getModule().getRing();
            for (Module fromModule : fromModules) {
                Module toModule = ring.getFreeModule(fromModule.getDimension());
                ModuleMorphism map = CanonicalMorphism.make(fromModule, toModule);
                if (map == null) {
                    return null;
                }
                else {
                    maps.add(map);
                }
            }
            return new LimitSimpleFreeReformer(maps, to);
        }
        else {
            return null;
        }
    }

    
    private static boolean collectModules(LimitForm from, List<Module> fromModules) {
        for (int i = 0; i < from.getFormCount(); i++) {
            Form f = from.getForm(i);
            if (f instanceof SimpleForm) {
                Module fromModule = ((SimpleForm)f).getModule(); 
                fromModules.add(fromModule);
            }
            else if (f instanceof LimitForm) {
                if (!collectModules((LimitForm)f, fromModules)) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return true;
    }
    
    
    private static class LimitSimpleFreeReformer extends LimitReformer {
        
        public LimitSimpleFreeReformer(List<ModuleMorphism> maps, SimpleForm to) {
            this.maps = maps;
            this.to = to;
        }
    
        public Denotator reform(Denotator d)
                throws RubatoException {
            Module address = d.getAddress();
            LimitDenotator ld = (LimitDenotator)d;
            List<ModuleElement> fromElements = new LinkedList<ModuleElement>();
            if (!collectElements(ld, fromElements)) {
                throw new RubatoException();
            }
            
            Iterator<ModuleElement> element_iter = fromElements.iterator();
            Iterator<ModuleMorphism> map_iter = maps.iterator();
            
            List<ModuleElement> toElements = new LinkedList<ModuleElement>();
            while (map_iter.hasNext()) {
                ModuleElement fromElement = element_iter.next();
                ModuleMorphism map = map_iter.next();
                ModuleElement toElement = map.map(fromElement);
                for (int j = 0; j < toElement.getLength(); j++) {
                    toElements.add(toElement.getComponent(j));
                }
            }
            
            ModuleElement toElement = to.getModule().createElement(toElements);
            return SimpleDenotator._make_unsafe(null, to, address, toElement);
        }
        
        private boolean collectElements(LimitDenotator d, List<ModuleElement> elements) {
            for (Denotator deno : d.getFactors()) {
                if (deno instanceof SimpleDenotator) {
                    ModuleElement element = ((SimpleDenotator)deno).getElement(); 
                    elements.add(element);
                }
                else if (deno instanceof LimitDenotator) {
                    if (!collectElements((LimitDenotator)deno, elements)) {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            return true;
        }
                
        private List<ModuleMorphism> maps;
        private SimpleForm           to;
    }
}

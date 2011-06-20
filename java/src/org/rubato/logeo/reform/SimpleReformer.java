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

import java.util.LinkedList;
import java.util.List;

import org.rubato.base.RubatoException;
import org.rubato.logeo.DenoFactory;
import org.rubato.math.module.Module;
import org.rubato.math.module.morphism.CanonicalMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.*;

abstract class SimpleReformer extends Reformer {

    public static SimpleReformer make(SimpleForm from, Form to) {
        SimpleReformer reformer = null;
        if (to instanceof SimpleForm) {
            // simple to simple
            reformer = makeSimple(from, (SimpleForm)to);
        }
        else if (to instanceof LimitForm) {
            // simple to limit
            reformer = makeLimit(from, (LimitForm)to);
        }
        else if (to instanceof ColimitForm) {
            // simple to colimit
            reformer = makeColimit(from, (ColimitForm)to);
        }
        else if (to instanceof PowerForm) {
            // simple to power
            reformer = makePower(from, (PowerForm)to);
        }
        else if (to instanceof ListForm) {
            // simple to list            
            reformer = makeList(from, (ListForm)to);
        }
        return reformer;
    }
    
    
    private static SimpleReformer makeSimple(SimpleForm from, SimpleForm to) {
        Module fromModule = from.getModule();
        Module toModule = to.getModule();
        if (fromModule.equals(toModule)) {
            return new SimpleIdentityReformer(to);
        }
        ModuleMorphism morphism = CanonicalMorphism.make(fromModule, toModule);
        if (morphism != null) {
            return new SimpleSimpleReformer(to, morphism);
        }
        else {
            return null;
        }
    }
    
    
    private static SimpleReformer makeLimit(SimpleForm from, LimitForm to) {
        return SimpleLimitReformer.make(from, to);
    }
    
    
    private static SimpleReformer makeColimit(SimpleForm from, ColimitForm to) {
        Reformer reformer = null;
        int toCount = to.getFormCount(); 
            
        int i = 0;
        while (i < toCount && reformer == null) {
            if (from.equals(to.getForm(i))) {
                reformer = Reformer._make(from, to.getForm(i));
            }
            i++;
        }
        
        if (reformer == null) {
            i = 0;
            while (i < toCount && reformer == null) {
                reformer = Reformer._make(from, to.getForm(i));
                i++;
            }
        }

        if (reformer != null) {
            return new SimpleColimitReformer(to, reformer, i-1);
        }
        else {
            return null;
        }
    }
    

    private static SimpleReformer makePower(SimpleForm from, PowerForm to) {
        Reformer reformer = Reformer._make(from, to.getForm());
        if (reformer != null) {
            return new SimplePowerReformer(to, reformer);
        }
        else {
            return null;
        }
    }

    
    private static SimpleReformer makeList(SimpleForm from, ListForm to) {
        Reformer reformer = Reformer._make(from, to.getForm());
        if (reformer != null) {
            return new SimpleListReformer(to, reformer);
        }
        else {
            return null;
        }
    }

    
    private static class SimpleSimpleReformer extends SimpleReformer {

        public SimpleSimpleReformer(SimpleForm to, ModuleMorphism morphism) {
            this.to = to;
            this.morphism = morphism;
        }
        
        public Denotator reform(Denotator from)
                throws RubatoException {
            SimpleDenotator sfrom = (SimpleDenotator)from;
            if (sfrom.nullAddressed()) {
                return DenoFactory.makeDenotator(to, morphism.map(sfrom.getElement()));
            }
            else {
                return DenoFactory.makeDenotator(to, morphism.compose(sfrom.getModuleMorphism()));
            }
        }
        
        private SimpleForm to;
        private ModuleMorphism morphism;
    }
    
    
    private static class SimpleIdentityReformer extends SimpleReformer {
        
        public SimpleIdentityReformer(SimpleForm to) {
            this.to = to;
        }
        
        public Denotator reform(Denotator from) {
            SimpleDenotator sfrom = (SimpleDenotator)from;
            if (sfrom.nullAddressed()) {
                return DenoFactory.makeDenotator(to, sfrom.getElement());
            }
            else {
                return DenoFactory.makeDenotator(to, sfrom.getModuleMorphism());
            }
        }
        
        private SimpleForm to;
    }
    
    
    private static class SimpleColimitReformer extends SimpleReformer {
        
        public SimpleColimitReformer(ColimitForm to, Reformer reformer, int index) {
            this.to = to;
            this.reformer = reformer;
            this.index = index;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            Denotator res = reformer.reform(d);
            return ColimitDenotator._make_unsafe(null, d.getAddress(), to, index, res);
        }
        
        private ColimitForm to;
        private Reformer    reformer;
        private int         index;
    }

    
    private static class SimplePowerReformer extends SimpleReformer {
        
        public SimplePowerReformer(PowerForm to, Reformer reformer) {
            this.to = to;
            this.reformer = reformer;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            Denotator res = reformer.reform(d);
            List<Denotator> resList = new LinkedList<Denotator>();
            resList.add(res);
            return PowerDenotator._make_unsafe(null, d.getAddress(), to, resList);
        }

        private PowerForm to;
        private Reformer  reformer;
    }    


    private static class SimpleListReformer extends SimpleReformer {
        
        public SimpleListReformer(ListForm to, Reformer reformer) {
            this.to = to;
            this.reformer = reformer;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            Denotator res = reformer.reform(d);
            List<Denotator> resList = new LinkedList<Denotator>();
            resList.add(res);
            return ListDenotator._make_unsafe(null, d.getAddress(), to, resList);
        }

        private ListForm to;
        private Reformer reformer;
    }    
}

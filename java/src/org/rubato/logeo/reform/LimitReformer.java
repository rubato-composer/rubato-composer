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
import org.rubato.math.module.Module;
import org.rubato.math.yoneda.*;

abstract class LimitReformer extends Reformer {

    public static LimitReformer make(LimitForm from, Form to) {
        LimitReformer reformer = null;
        if (to instanceof SimpleForm) {
            // limit to simple
            reformer = makeSimple(from, (SimpleForm)to);
        }
        else if (to instanceof LimitForm) {
            // limit to limit
            reformer = makeLimit(from, (LimitForm)to);
        }
        else if (to instanceof ColimitForm) {
            // limit to colimit
            reformer = makeColimit(from, (ColimitForm)to);
        }
        else if (to instanceof PowerForm) {
            // limit to power
            reformer = makePower(from, (PowerForm)to);
        }
        else if (to instanceof ListForm) {
            // limit to list
            reformer = makeList(from, (ListForm)to);
        }
        return reformer;
    }

    
    private static LimitReformer makeSimple(LimitForm from, SimpleForm to) {
        return LimitSimpleReformer.make(from, to);
    }
    
    
    private static LimitReformer makeLimit(LimitForm from, LimitForm to) {
        int fromCount = from.getFormCount();
        int toCount = to.getFormCount();
        int n = Math.min(fromCount, toCount);
        Reformer[] reformers = new Reformer[n];
        for (int i = 0; i < n; i++) {
            reformers[i] = Reformer._make(from.getForm(i), to.getForm(i));
            if (reformers[i] == null) {
                return RecursiveLimitReformer.make(from, to);
            }
        }
        return new LimitLimitReformer(to, reformers);
    }
    
    
    private static LimitReformer makeColimit(LimitForm from, ColimitForm to) {
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
            return new LimitColimitReformer(to, reformer, i-1); 
        }
        else {
            return null;
        }
    }
    

    private static LimitReformer makePower(LimitForm from, PowerForm to) {
        Reformer reformer = Reformer._make(from, to.getForm());
        if (reformer != null) {
            return new LimitPowerReformer(to, reformer);
        }
        else {
            return null;
        }
    }

    
    private static LimitReformer makeList(LimitForm from, ListForm to) {
        Reformer reformer = Reformer._make(from, to.getForm());
        if (reformer != null) {
            return new LimitListReformer(to, reformer);
        }
        else {
            return null;
        }
    }

    
    private static class LimitLimitReformer extends LimitReformer {
        
        public LimitLimitReformer(LimitForm to, Reformer[] reformers) {
            this.to = to;
            this.reformers = reformers;
            this.count = reformers.length;
            this.toCount = to.getFormCount();
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            Module address = d.getAddress();
            LimitDenotator res = (LimitDenotator)d;
            List<Denotator> list = new LinkedList<Denotator>();
            
            for (int i = 0; i < count; i++) {
                list.add(reformers[i].reform(res.getFactor(i)));
            }
            for (int i = count; i < toCount; i++) {
                list.add(to.getForm(i).createDefaultDenotator(address));
            }
            
            return LimitDenotator._make_unsafe(null, address, to, list);
        }
        
        private LimitForm  to;
        private int        toCount;
        private Reformer[] reformers;
        private int        count;
    }

    
    private static class LimitColimitReformer extends LimitReformer {
        
        public LimitColimitReformer(ColimitForm to, Reformer reformer, int index) {
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

    
    private static class LimitPowerReformer extends LimitReformer {
        
        public LimitPowerReformer(PowerForm to, Reformer reformer) {
            this.to = to;
            this.reformer = reformer;
        }
    
        public Denotator reform(Denotator d)
                throws RubatoException {
            Denotator res = reformer.reform(d);
            List<Denotator> list = new LinkedList<Denotator>();
            list.add(res);
            return PowerDenotator._make_unsafe(null, d.getAddress(), to, list);
        }
        
        private PowerForm to;
        private Reformer  reformer;
    }
    
    
    private static class LimitListReformer extends LimitReformer {
        
        public LimitListReformer(ListForm to, Reformer reformer) {
            this.to = to;
            this.reformer = reformer;
        }
    
        public Denotator reform(Denotator d)
                throws RubatoException {
            Denotator res = reformer.reform(d);
            List<Denotator> list = new LinkedList<Denotator>();
            list.add(res);
            return ListDenotator._make_unsafe(null, d.getAddress(), to, list);
        }
        
        private ListForm to;
        private Reformer reformer;
    }    
}

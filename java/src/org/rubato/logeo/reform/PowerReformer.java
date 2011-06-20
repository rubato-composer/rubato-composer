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

abstract class PowerReformer extends Reformer {

    public static PowerReformer make(PowerForm from, Form to) {
        PowerReformer reformer = null;
        if (to instanceof LimitForm) {
            // power to limit
            reformer = makeLimit(from, (LimitForm)to);
        }
        else if (to instanceof ColimitForm) {
            // power to colimit
            reformer = makeColimit(from, (ColimitForm)to);
        }
        else if (to instanceof PowerForm) {
            // power to power
            reformer = makePower(from, (PowerForm)to);
        }
        else if (to instanceof ListForm) {
            // power to list
            reformer = makeList(from, (ListForm)to);
        }
        return reformer;
    }
    
    
    private static PowerReformer makeLimit(PowerForm from, LimitForm to) {
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
            return new PowerLimitReformer(to, reformer, i-1); 
        }
        else {
            return null;
        }
    }
    
    
    private static PowerReformer makeColimit(PowerForm from, ColimitForm to) {
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
            return new PowerColimitReformer(to, reformer, i-1); 
        }
        else {
            return null;
        }
    }
    

    private static PowerReformer makePower(PowerForm from, PowerForm to) {
        // create a reformer between the base forms
        Reformer reformer = Reformer._make(from.getForm(), to.getForm());
        if (reformer != null) {
            return new PowerPowerReformer(to, reformer);
        }
        else {
            return null;
        }
    }
    
    
    private static PowerReformer makeList(PowerForm from, ListForm to) {
        // create a reformer between the base forms
        Reformer reformer = Reformer._make(from.getForm(), to.getForm());
        if (reformer != null) {
            return new PowerListReformer(to, reformer);
        }
        else {
            return null;
        }
    }
    
    
    private static class PowerLimitReformer extends PowerReformer {
        
        public PowerLimitReformer(LimitForm to, Reformer reformer, int index) {
            this.to = to;
            this.reformer = reformer;
            this.index = index;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            Module address = d.getAddress();
            Denotator res = reformer.reform(d);
            List<Denotator> list = new LinkedList<Denotator>();
            for (int i = 0; i < to.getFormCount(); i++) {
                if (i == index) {
                    list.add(res);
                }
                else {
                    list.add(to.getForm(i).createDefaultDenotator(address));
                }
            }
            return LimitDenotator._make_unsafe(null, address, to, list);
        }
        
        private LimitForm to;
        private Reformer  reformer;
        private int       index;
    }

    
    private static class PowerColimitReformer extends PowerReformer {
        
        public PowerColimitReformer(ColimitForm to, Reformer reformer, int index) {
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

    
    private static class PowerPowerReformer extends PowerReformer {
        
        public PowerPowerReformer(PowerForm to, Reformer reformer) {
            this.to = to;
            this.reformer = reformer;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            List<Denotator> factors = ((PowerDenotator)d).getFactors();
            List<Denotator> newfactors = new LinkedList<Denotator>();
            for (Denotator f : factors) {
                newfactors.add(reformer.reform(f));
            }
            return new PowerDenotator(null, d.getAddress(), to, newfactors);
        }
        
        private PowerForm to;
        private Reformer  reformer;
    }
    
    
    private static class PowerListReformer extends PowerReformer {
        
        public PowerListReformer(ListForm to, Reformer reformer) {
            this.to = to;
            this.reformer = reformer;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            List<Denotator> factors = ((PowerDenotator)d).getFactors();
            List<Denotator> newfactors = new LinkedList<Denotator>();
            for (Denotator f : factors) {
                newfactors.add(reformer.reform(f));
            }
            return ListDenotator._make_unsafe(null, d.getAddress(), to, newfactors);
        }
        
        private ListForm to;
        private Reformer reformer;
    }
}

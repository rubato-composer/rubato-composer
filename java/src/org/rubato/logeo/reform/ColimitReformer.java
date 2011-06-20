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
import org.rubato.math.yoneda.*;

abstract class ColimitReformer extends Reformer {

    public static ColimitReformer make(ColimitForm from, Form to) {
        ColimitReformer reformer = null;
        if (to instanceof ColimitForm) {
            // colimit to colimit
            reformer = makeColimit(from, (ColimitForm)to);
        }
        else if (to instanceof LimitForm) {
            // colimit to limit
            reformer = makeLimit(from, (LimitForm)to);
        }
        else if (to instanceof PowerForm) {
            // colimit to power
            reformer = makePower(from, (PowerForm)to);
        }
        else if (to instanceof ListForm) {
            // colimit to list
            reformer = makeList(from, (ListForm)to);
        }
        
        if (reformer == null) {
            // reform all cofactors to the destination form
            reformer = makeAny(from, to);
        }
        
        return reformer;
    }
    
    
    private static ColimitReformer makeColimit(ColimitForm from, ColimitForm to) {
        int fromCount = from.getFormCount();
        int toCount = to.getFormCount();
        // number of cofactors of destination form must be equal or larger
        // than the number of cofactors of source form
        if (fromCount <= toCount) {
            // create reformers for the corresponding cofactors
            Iterator<Form> from_iter = from.getForms().iterator();
            Iterator<Form> to_iter = to.getForms().iterator();
            Reformer[] reformers = new Reformer[fromCount];
            for (int i = 0; i < fromCount; i++) {
                Form from_form = from_iter.next();
                Form to_form = to_iter.next();
                Reformer r = Reformer._make(from_form, to_form);
                if (r == null) {
                    return null;
                }
                reformers[i] = r;
            }
            return new ColimitColimitReformer(to, reformers);
        }
        else {
            return null;
        }
    }
    
    
    private static ColimitReformer makeLimit(ColimitForm from, LimitForm to) {
        int fromCount = from.getFormCount();
        int toCount = to.getFormCount();
        // number of factors of destination form must be equal or larger
        // than the number of cofactors of source form
        if (fromCount <= toCount) {
            // create reformers for the corresponding cofactors/factors
            Iterator<Form> from_iter = from.getForms().iterator();
            Iterator<Form> to_iter = to.getForms().iterator();
            Reformer[] reformers = new Reformer[fromCount];
            for (int i = 0; i < fromCount; i++) {
                Form from_form = from_iter.next();
                Form to_form = to_iter.next();
                Reformer r = Reformer._make(from_form, to_form);
                if (r == null) {
                    return null;
                }
                reformers[i] = r;
            }
            return new ColimitLimitReformer(to, reformers);
        }
        else {
            return null;
        }
    }
    
    
    private static ColimitReformer makePower(ColimitForm from, PowerForm to) {
        Reformer reformer = Reformer._make(from, to.getForm());
        if (reformer != null) {
            return new ColimitPowerReformer(to, reformer);
        }
        else {
            return null;
        }
    }

    
    private static ColimitReformer makeList(ColimitForm from, ListForm to) {
        Reformer reformer = Reformer._make(from, to.getForm());
        if (reformer != null) {
            return new ColimitListReformer(to, reformer);
        }
        else {
            return null;
        }
    }

    
    private static ColimitReformer makeAny(ColimitForm from, Form to) {
        int fromCount = from.getFormCount();
        Reformer[] reformers = new Reformer[fromCount];
        Iterator<Form> from_iter = from.getForms().iterator();
        for (int i = 0; i < fromCount; i++) {
            Form from_form = from_iter.next(); 
            Reformer r = Reformer._make(from_form, to);
            if (r == null) {
                return null;
            }
            reformers[i] = r;
        }
        return new ColimitAnyReformer(reformers);
    }
    
    
    private static class ColimitColimitReformer extends ColimitReformer {

        public ColimitColimitReformer(ColimitForm to, Reformer[] reformers) {
            this.to = to;
            this.reformers = reformers;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            ColimitDenotator cd = (ColimitDenotator)d;
            int index = cd.getIndex();
            Denotator f = reformers[index].reform(cd.getFactor());
            return ColimitDenotator._make_unsafe(null, d.getAddress(), to, index, f);
        }
        
        private ColimitForm to;
        private Reformer[]  reformers;
    }

    
    private static class ColimitLimitReformer extends ColimitReformer {

        public ColimitLimitReformer(LimitForm to, Reformer[] reformers) {
            this.to = to;
            this.reformers = reformers;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            ColimitDenotator cd = (ColimitDenotator)d;
            Module address = d.getAddress();
            int index = cd.getIndex();
            List<Denotator> resList = new LinkedList<Denotator>();
            for (int i = 0; i < to.getFormCount(); i++) {
                Denotator f;
                if (i == index) {
                    f = reformers[index].reform(cd.getFactor());
                }
                else {
                    f = to.getForm(i).createDefaultDenotator(address);
                }
                resList.add(f);
            }
            return LimitDenotator._make_unsafe(null, d.getAddress(), to, resList);
        }
        
        private LimitForm  to;
        private Reformer[] reformers;
    }

    
    private static class ColimitPowerReformer extends ColimitReformer {
        
        public ColimitPowerReformer(PowerForm to, Reformer reformer) {
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
    
    
    private static class ColimitListReformer extends ColimitReformer {
        
        public ColimitListReformer(ListForm to, Reformer reformer) {
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
    
    
    private static class ColimitAnyReformer extends ColimitReformer {

        public ColimitAnyReformer(Reformer[] reformers) {
            this.reformers = reformers;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            ColimitDenotator cd = (ColimitDenotator)d;
            int index = cd.getIndex();
            Denotator f = reformers[index].reform(cd.getFactor());
            return f;
        }
        
        private Reformer[] reformers;
    }
}

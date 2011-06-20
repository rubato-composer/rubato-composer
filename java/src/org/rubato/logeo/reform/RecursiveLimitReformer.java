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

class RecursiveLimitReformer extends LimitReformer {

    public static RecursiveLimitReformer make(LimitForm from, LimitForm to) {
        RecursiveLimitReformer reformer = null;
        List<Form> fromForms = new LinkedList<Form>();
        collectForms(from, fromForms);
        List<Form> toForms = new LinkedList<Form>();
        collectForms(to, toForms);
        if (fromForms.size() == toForms.size()) {
            Reformer[] reformers = new Reformer[fromForms.size()];
            Iterator<Form> from_iter = fromForms.iterator();
            Iterator<Form> to_iter = toForms.iterator();
            for (int i = 0; i < reformers.length; i++) {
                reformers[i] = Reformer._make(from_iter.next(), to_iter.next());
                if (reformers[i] == null) {
                    return null;
                }
            }
            reformer = new RecursiveLimitReformer(to, reformers);
        }
        return reformer;
    }

    
    private static void collectForms(LimitForm form, List<Form> forms) {
        for (Form f : form.getForms()) {
            if (f instanceof LimitForm) {
                collectForms((LimitForm)f, forms);
            }
            else {
                forms.add(f);
            }
        }
    }
    
    
    public Denotator reform(Denotator d)
            throws RubatoException {
        LimitDenotator ld = (LimitDenotator)d;
        List<Denotator> fromList = new LinkedList<Denotator>();
        collectDenotators(ld, fromList);
        List<Denotator> toList = new LinkedList<Denotator>();
        int i = 0;
        for (Denotator from : fromList) {
            toList.add(reformers[i++].reform(from));
        }
        return distributeDenotators(toList, d.getAddress(), to);
    }
    
    
    private void collectDenotators(LimitDenotator deno, List<Denotator> denos) {
        for (Denotator d : deno.getFactors()) {
            if (d instanceof LimitDenotator) {
                collectDenotators((LimitDenotator)d, denos);
            }
            else {
                denos.add(d);
            }
        }
    }
    
    
    private Denotator distributeDenotators(List<Denotator> denos, Module address, LimitForm toForm) {
        List<Denotator> list = new LinkedList<Denotator>();
        for (Form f : toForm.getForms()) {
            if (f instanceof LimitForm) {
                list.add(distributeDenotators(denos, address, (LimitForm)f));
            }
            else {
                list.add(denos.remove(0));
            }
        }
        return LimitDenotator._make_unsafe(null, address, toForm, list);
    }
    
    
    private RecursiveLimitReformer(LimitForm to, Reformer[] reformers) {
        this.to = to;
        this.reformers = reformers;
    }
    
    
    private LimitForm  to;
    private Reformer[] reformers;
}

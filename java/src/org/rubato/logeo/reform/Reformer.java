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

import java.util.HashMap;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.*;


/**
 * A reformer transforms a denotator of a given form to
 * a new denotator of another given form. 
 * Instances are not created directly, but by using the
 * static make method. For each pair of forms, the
 * corresponding reformer is cached.
 * 
 * @author Gérard Milmeister
 */
public abstract class Reformer {

    /**
     * Creates a new denotator from <code>d</code>
     * with the configured form.
     *
     * @throws RubatoException if <code>d</code> does not have the
     *                         configured form
     */
    public abstract Denotator reform(Denotator d)
        throws RubatoException;
    

    /**
     * Creates a reformer that converts a denotator
     * of form <code>from</code> to a denotator of
     * form <code>to</code>.
     * 
     * @return null if a reformer for the specified
     *              forms could not be created
     */
    public static Reformer make(Form from, Form to) {
        Reformer reformer = _make(from, to);
        if (reformer == null) {
            return null;
        }
        else {
            return new ReformerWrapper(from, reformer);
        }
    }
    
    
    /**
     * Creates a reformer that converts a denotator
     * of form <code>from</code> to a denotator of
     * form <code>to</code>.
     * 
     * @return null if a reformer for the specified
     *              forms could not be created
     */
    static Reformer _make(Form from, Form to) {
        if (from.equals(to)) {
            return identityReformer;
        }
        FormPair formPair = new FormPair(from, to);
        Reformer reformer = reformers.get(formPair);
        if (reformer == null) {
            switch (from.getType()) {
            case Form.SIMPLE: {
                reformer = makeSimpleReformer((SimpleForm)from, to);
                break;
            }
            case Form.LIMIT: {
                reformer = makeLimitReformer((LimitForm)from, to);
                break;
            }
            case Form.COLIMIT: {
                reformer = makeColimitReformer((ColimitForm)from, to);
                break;
            }
            case Form.POWER: {
                reformer = makePowerReformer((PowerForm)from, to);
                break;
            }
            case Form.LIST: {
                reformer = makeListReformer((ListForm)from, to);
                break;
            }
            }
            
            if (reformer != null) {
                reformers.put(formPair, reformer);
            }
        }
        return reformer;
    }

    
    private static Reformer makeSimpleReformer(SimpleForm from, Form to) {
        return SimpleReformer.make(from, to);
    }
    
    
    private static Reformer makeLimitReformer(LimitForm from, Form to) {
        return LimitReformer.make(from, to);
    }
    
    
    private static Reformer makeColimitReformer(ColimitForm from, Form to) {
        return ColimitReformer.make(from, to);
    }
    
    
    private static Reformer makePowerReformer(PowerForm from, Form to) {
        return PowerReformer.make(from, to);
    }
    
    
    private static Reformer makeListReformer(ListForm from, Form to) {
        return ListReformer.make(from, to);
    }
    
    
    private static class ReformerWrapper extends Reformer {
        
        public ReformerWrapper(Form from, Reformer reformer) {
            this.from = from;
            this.reformer = reformer;
        }
        
        public Denotator reform(Denotator d)
                throws RubatoException {
            if (d.getForm().equals(from)) {
                return reformer.reform(d);
            }
            else {
                throw new RubatoException("Reformer.reform: Expected form %%1, but got form %%2", from, d.getForm());
            }
        }
        
        private Form     from;
        private Reformer reformer;
    }

    
    protected static class IdentityReformer extends Reformer {
        
        public Denotator reform(Denotator from) {
            return from;
        }
    }
    
    
    private static IdentityReformer identityReformer = new IdentityReformer();
    
    
    private static class FormPair {
        
        public FormPair(Form from, Form to) {
            this.from = from;
            this.to = to;
        }
        
        public boolean equals(Object obj) {
            if (obj instanceof FormPair) {
                FormPair p = (FormPair)obj;
                return from == p.from && to == p.to;
            }
            else {
                return true;
            }
        }
        
        public int hashCode() {
            return from.hashCode()*47+to.hashCode();
        }
        
        private Form from;
        private Form to;
    }
    
    
    private static HashMap<FormPair,Reformer> reformers;
    
    static {
        reformers = new HashMap<FormPair,Reformer>();      
    }
    
    protected Reformer() {}
}

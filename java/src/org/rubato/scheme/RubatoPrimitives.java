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

package org.rubato.scheme;

import static org.rubato.scheme.SExpr.*;

import java.util.LinkedList;
import java.util.List;

import org.rubato.base.Repository;
import org.rubato.logeo.DenoFactory;
import org.rubato.math.module.*;
import org.rubato.math.yoneda.*;


/**
 * Rubato-specific primitive procedures.
 * 
 * @author Gérard Milmeister
 */
abstract class RubatoPrimitives {

    public static void fillEnvironment(Env env) {
        env.addPrimitive(form_p);
        env.addPrimitive(denotator_p);
        env.addPrimitive(get_form);
        env.addPrimitive(get_denotator);
        env.addPrimitive(get_all_forms);
        env.addPrimitive(get_all_denotators);
        env.addPrimitive(type_simple_p);
        env.addPrimitive(type_limit_p);
        env.addPrimitive(type_colimit_p);
        env.addPrimitive(type_power_p);
        env.addPrimitive(type_list_p);
        env.addPrimitive(type_of);
        env.addPrimitive(name_of);
        env.addPrimitive(form_of);
        env.addPrimitive(forms_of);
        env.addPrimitive(form_count_of);
        env.addPrimitive(factors_of);
        env.addPrimitive(factor_count_of);
        env.addPrimitive(index_of);
        env.addPrimitive(element_of);
        env.addPrimitive(make_denotator);
        env.addPrimitive(register);
    }

    private static Primitive form_p = new Primitive() {
        public String getName() { return "form?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr car = car(args);
                return SBoolean.make(car.isForm());
            }
            else {
                eval.addError("form?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive denotator_p = new Primitive() {
        public String getName() { return "denotator?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr car = car(args);
                return SBoolean.make(car.isDenotator());
            }
            else {
                eval.addError("denotator?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive get_form = new Primitive() {
        public String getName() { return "get-form"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr car = car(args);
                if (car.isSymbol()) {
                    Form f = rep.getForm(((Symbol)car).getName());
                    return (f == null)?NULL:new SForm(f);
                }
                else if (car.isString()) {
                    Form f = rep.getForm(((SString)car).getString());
                    return (f == null)?NULL:new SForm(f);
                }
                else {
                    eval.addError("get-form: expected argument of type symbol or string, but got %1", car);
                    return null;
                }
            }
            else {
                eval.addError("get-form: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive get_denotator = new Primitive() {
        public String getName() { return "get-denotator"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr car = car(args);
                if (car.isSymbol()) {
                    Denotator d = rep.getDenotator(((Symbol)car).getName());
                    return (d == null)?NULL:new SDenotator(d);
                }
                else if (car.isString()) {
                    Denotator d = rep.getDenotator(((SString)car).getString());
                    return (d == null)?NULL:new SDenotator(d);
                }
                else {
                    eval.addError("get-denotator: expected argument of type symbol or string, but got %1", car);
                    return null;
                }
            }
            else {
                eval.addError("get-denotator: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive get_all_forms = new Primitive() {
        public String getName() { return "get-all-forms"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 0) {
                SExpr res = NULL;
                for (Form form : rep.getForms()) {
                    res = cons(new SForm(form), res);
                }
                return res;
            }
            else {
                eval.addError("get-all-forms: expected number of arguments is 0, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive get_all_denotators = new Primitive() {
        public String getName() { return "get-all-denotators"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 0) {
                SExpr res = NULL;
                for (Denotator d : rep.getDenotators()) {
                    res = cons(new SDenotator(d), res);
                }
                return res;
            }
            else {
                eval.addError("get-all-denotators: expected number of arguments is 0, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive type_simple_p = new Primitive() {
        public String getName() { return "type-simple?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    return SBoolean.make(((SForm)arg).getForm().getType() == Form.SIMPLE);
                }
                else if (arg instanceof SDenotator) {
                    return SBoolean.make(((SDenotator)arg).getDenotator().getType() == Denotator.SIMPLE);
                }
                else {
                    eval.addError("type-simple?: expected argument of type form or denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("type-simple?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive type_limit_p = new Primitive() {
        public String getName() { return "type-limit?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    return SBoolean.make(((SForm)arg).getForm().getType() == Form.LIMIT);
                }
                else if (arg instanceof SDenotator) {
                    return SBoolean.make(((SDenotator)arg).getDenotator().getType() == Denotator.LIMIT);
                }
                else {
                    eval.addError("type-limit?: expected argument of type form or denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("type-limit?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive type_colimit_p = new Primitive() {
        public String getName() { return "type-colimit?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    return SBoolean.make(((SForm)arg).getForm().getType() == Form.COLIMIT);
                }
                else if (arg instanceof SDenotator) {
                    return SBoolean.make(((SDenotator)arg).getDenotator().getType() == Denotator.COLIMIT);
                }
                else {
                    eval.addError("type-colimit?: expected argument of type form or denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("type-colimit?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive type_power_p = new Primitive() {
        public String getName() { return "type-power?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    return SBoolean.make(((SForm)arg).getForm().getType() == Form.POWER);
                }
                else if (arg instanceof SDenotator) {
                    return SBoolean.make(((SDenotator)arg).getDenotator().getType() == Denotator.POWER);
                }
                else {
                    eval.addError("type-power?: expected argument of type form or denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("type-power?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive type_list_p = new Primitive() {
        public String getName() { return "type-list?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    return SBoolean.make(((SForm)arg).getForm().getType() == Form.LIST);
                }
                else if (arg instanceof SDenotator) {
                    return SBoolean.make(((SDenotator)arg).getDenotator().getType() == Denotator.LIST);
                }
                else {
                    eval.addError("type-list?: expected argument of type form or denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("type-list?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive type_of = new Primitive() {
        public String getName() { return "type-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    return type_of(((SForm)arg).getForm().getType());
                }
                else if (arg instanceof SDenotator) {
                    return type_of(((SDenotator)arg).getDenotator().getType());
                }
                else {
                    eval.addError("type-of: expected argument of type form or denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("type-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive name_of = new Primitive() {
        public String getName() { return "name-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    return new SString(((SForm)arg).getForm().getNameString());
                }
                else if (arg instanceof SDenotator) {
                    return new SString(((SDenotator)arg).getDenotator().getNameString());
                }
                else {
                    eval.addError("name-of: expected argument of type form or denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("name-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive form_of = new Primitive() {
        public String getName() { return "form-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SDenotator) {
                    return new SForm(((SDenotator)arg).getDenotator().getForm());
                }
                else {
                    eval.addError("form-of: expected argument of type denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("form-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive forms_of = new Primitive() {
        public String getName() { return "forms-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    Form f = ((SForm)arg).getForm();
                    if (f instanceof LimitForm) {
                        return formListToScheme(((LimitForm)f).getForms());
                    }
                    else if (f instanceof ColimitForm) {
                        return formListToScheme(((ColimitForm)f).getForms());
                    }
                    else if (f instanceof PowerForm) {
                        return formListToScheme(((PowerForm)f).getForms());
                    }
                    else if (f instanceof ListForm) {
                        return formListToScheme(((ListForm)f).getForms());
                    }
                    else {
                        return NULL;
                    }                    
                }
                else {
                    eval.addError("forms-of: expected argument of type form, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("forms-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive form_count_of = new Primitive() {
        public String getName() { return "form-count-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SForm) {
                    Form f = ((SForm)arg).getForm();
                    if (f instanceof LimitForm) {
                        return new SInteger(((LimitForm)f).getFormCount());
                    }
                    else if (f instanceof ColimitForm) {
                        return new SInteger(((ColimitForm)f).getFormCount());
                    }
                    else if (f instanceof PowerForm) {
                        return new SInteger(((PowerForm)f).getFormCount());
                    }
                    else if (f instanceof ListForm) {
                        return new SInteger(((ListForm)f).getFormCount());
                    }
                    else {
                        return new SInteger(0);
                    }                    
                }
                else {
                    eval.addError("form-count-of: expected argument of type form, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("form-count-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive factors_of = new Primitive() {
        public String getName() { return "factors-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SDenotator) {
                    Denotator d = ((SDenotator)arg).getDenotator();
                    if (d instanceof LimitDenotator) {
                        return denoListToScheme(((LimitDenotator)d).getFactors());
                    }
                    else if (d instanceof ColimitDenotator) {
                        return denoListToScheme(((ColimitDenotator)d).getFactors());
                    }
                    else if (d instanceof PowerDenotator) {
                        return denoListToScheme(((PowerDenotator)d).getFactors());
                    }
                    else if (d instanceof ListDenotator) {
                        return denoListToScheme(((ListDenotator)d).getFactors());
                    }
                    else {
                        return NULL;
                    }                    
                }
                else {
                    eval.addError("factors-of: expected argument of type denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("factors-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive factor_count_of = new Primitive() {
        public String getName() { return "factor-count-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SDenotator) {
                    Denotator d = ((SDenotator)arg).getDenotator();
                    if (d instanceof LimitDenotator) {
                        return new SInteger(((LimitDenotator)d).getFactorCount());
                    }
                    else if (d instanceof ColimitDenotator) {
                        return new SInteger(((ColimitDenotator)d).getFactorCount());
                    }
                    else if (d instanceof PowerDenotator) {
                        return new SInteger(((PowerDenotator)d).getFactorCount());
                    }
                    else if (d instanceof ListDenotator) {
                        return new SInteger(((ListDenotator)d).getFactorCount());
                    }
                    else {
                        return new SInteger(0);
                    }                    
                }
                else {
                    eval.addError("factor-count-of: expected argument of type denotator, but got %1", arg);
                    return null;
                }
            }
            else {
                eval.addError("factor-count-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive index_of = new Primitive() {
        public String getName() { return "index-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SDenotator) {
                    Denotator d = ((SDenotator)arg).getDenotator();
                    if (d instanceof ColimitDenotator) {
                        return new SInteger(((ColimitDenotator)d).getIndex());
                    }
                }
                eval.addError("index-of: expected argument of type colimit denotator, but got %1", arg);
                return null;
            }
            else {
                eval.addError("index-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive element_of = new Primitive() {
        public String getName() { return "element-of"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SDenotator) {
                    Denotator d = ((SDenotator)arg).getDenotator();
                    if (d instanceof SimpleDenotator) {
                        ModuleElement element = ((SimpleDenotator)d).getElement();
                        SExpr sexpr = moduleElementToSExpr(element);
                        if (sexpr != null) {
                            return sexpr;
                        }
                        else {
                            eval.addError("element-of: could not convert module element to Scheme expression");
                            return null;
                        }
                    }
                }
                eval.addError("element-of: expected argument of type simple denotator, but got %1", arg);
                return null;
            }
            else {
                eval.addError("element-of: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive make_denotator = new Primitive() {
        public String getName() { return "make-denotator"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 3) {
                SExpr arg1 = car(args);
                SExpr arg2 = car(cdr(args));
                SExpr arg3 = car(cdr(cdr(args)));
                String name = null;
                Form form;
                // retrieve name
                if (arg1.isString()) {
                    name = ((SString)arg1).getString();
                }
                else if (arg1.isSymbol()) {
                    name = ((Symbol)arg1).toString();
                }
                if (!arg2.isForm()) {
                    eval.addError("make-denotator: expected 2nd argument of type form, but got %1", arg2);
                    return null;
                }
                form = ((SForm)arg2).getForm();
                Denotator d = null;
                switch (form.getType()) {
                case Form.SIMPLE: {
                    d = makeSimple(eval, name, form, arg3);
                    break;
                }
                case Form.LIMIT: {
                    d = makeLimit(eval, name, form, arg3);
                    break;
                }
                case Form.COLIMIT: {
                    d = makeColimit(eval, name, form, arg3);
                    break;
                }
                case Form.POWER: {
                    d = makePowerList(eval, name, form, arg3);
                    break;
                }
                case Form.LIST: {
                    d = makePowerList(eval, name, form, arg3);
                    break;
                }
                }
                if (d != null) {
                    return new SDenotator(d);
                }
                else {
                    return null;
                }
            }
            else {
                eval.addError("make-denotator: expected number of arguments is 3, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive register = new Primitive() {
        public String getName() { return "register"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg = car(args);
                if (arg instanceof SDenotator) {
                    Denotator d = ((SDenotator)arg).getDenotator();
                    if (d.getNameString().length() > 0) {
                        rep.register(d);
                    }
                }
                eval.addError("register: expected argument of type denotator, but got %1", arg);
                return null;
            }
            else {
                eval.addError("register: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    protected static SExpr formListToScheme(List<Form> formList) {
        SExpr res = NULL;
        for (Form form : formList) {
            res = cons(new SForm(form), res);
        }
        return ListPrimitives.reverse(res, NULL);
    }    
    
    protected static SExpr denoListToScheme(List<Denotator> denoList) {
        SExpr res = NULL;
        for (Denotator deno : denoList) {
            res = cons(new SDenotator(deno), res);
        }
        return ListPrimitives.reverse(res, NULL);
    }    
    
    protected static Symbol type_of(int type) {
        if (type == Yoneda.SIMPLE) {
            return simple_sym;
        }
        else if (type == Yoneda.LIMIT) {
            return limit_sym;
        }
        else if (type == Yoneda.COLIMIT) {
            return colimit_sym;
        }
        else if (type == Yoneda.POWER) {
            return power_sym;
        }
        else if (type == Yoneda.LIST) {
            return list_sym;
        }
        else {
            return none_sym;
        }
    }
    
    protected static Denotator makeSimple(Evaluator eval, String name, Form form, SExpr arg) {
        Denotator d = null;
        ModuleElement element = sexprToModuleElement(arg);
        if (element != null) {
            d = DenoFactory.makeDenotator(name, form, element);
        }
        if (d == null) {
            eval.addError("make-denotator: could not create a simple denotator from arguments");
        }
        return d;
    }
    
    protected static Denotator makeLimit(Evaluator eval, String name, Form form, SExpr arg) {
        if (arg.isList()) {
            List<Denotator> denoList = new LinkedList<Denotator>();
            while (!arg.isNull()) {
                SExpr sexpr = car(arg);
                if (sexpr.isDenotator()) {
                    denoList.add(((SDenotator)sexpr).getDenotator());
                }
                else {
                    eval.addError("make-denotator: argument list must contain denotators");
                    return null;
                }
            }
            Denotator d = DenoFactory.makeDenotator(name, form, denoList);
            if (d == null) {
                eval.addError("make-denotator: could not create denotator", arg);
            }
            return d;
        }
        else {
            eval.addError("make-denotator: expected argument is a list, but got %1", arg);
            return null;
        }
    }
    
    protected static Denotator makeColimit(Evaluator eval, String name, Form form, SExpr arg) {
        Denotator d = null;
        return d;
    }
    
    protected static Denotator makePowerList(Evaluator eval, String name, Form form, SExpr arg) {
        if (arg.isList()) {
            List<Denotator> denoList = new LinkedList<Denotator>();
            while (!arg.isNull()) {
                SExpr sexpr = car(arg);
                if (sexpr.isDenotator()) {
                    denoList.add(((SDenotator)sexpr).getDenotator());
                }
                else {
                    eval.addError("make-denotator: argument list must contain denotators");
                    return null;
                }
            }
            Denotator d = DenoFactory.makeDenotator(name, form, denoList);
            if (d == null) {
                eval.addError("make-denotator: could not create denotator", arg);
            }
            return d;
        }
        else {
            eval.addError("make-denotator: expected argument is a list, but got %1", arg);
            return null;
        }
    }
    
    protected static SExpr moduleElementToSExpr(ModuleElement element) {
        if (element instanceof ZElement) {
            return new SInteger(((ZElement)element).getValue());
        }
        else if (element instanceof ZnElement) {
            return new SInteger(((ZnElement)element).getValue());
        }
        else if (element instanceof QElement) {
            return SRational.make(((QElement)element).getValue());
        }
        else if (element instanceof RElement) {
            return SReal.make(((RElement)element).getValue());
        }
        else if (element instanceof CElement) {
            return new SComplex(((CElement)element).getValue());
        }
        else if (element instanceof FreeElement) {
            FreeElement freeElement = (FreeElement)element;
            int len = freeElement.getLength();
            if (len > 1) {
                SExpr[] sexprList = new SExpr[len];
                for (int i = 0; i < len; i++) {
                    SExpr res = moduleElementToSExpr(freeElement.getComponent(i));
                    if (res == null) {
                        return null;
                    }
                    sexprList[i] = res;
                }
                return new SVector(sexprList);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
    
    private static ModuleElement sexprToModuleElement(SExpr sexpr) {
        if (sexpr.isInteger()) {
            return new ZElement(((SInteger)sexpr).getInt());
        }
        else if (sexpr.isRational()) {
            return new QElement(((SRational)sexpr).getRational());
        }
        else if (sexpr.isReal()) {
            return new RElement(((SReal)sexpr).getDouble());
        }
        else if (sexpr.isComplex()) {
            return new CElement(((SComplex)sexpr).getComplex());
        }
        else if (sexpr.isBoolean()) {
            return new ZElement(sexpr == SBoolean.TRUE?1:0);
        }
        else if (sexpr.isChar()) {
            return new ZStringElement(Character.toString(((SChar)sexpr).getChar()));
        }
        else if (sexpr.isString()) {
            return new ZStringElement(((SString)sexpr).getString());            
        }
        else if (sexpr.isSymbol()) {
            return new ZStringElement(((Symbol)sexpr).toString());            
        }
        else if (sexpr.isVector()) {
            SExpr[] v = ((SVector)sexpr).getArray();
            if (v.length == 0) {
                return null;
            }
            else if (v.length == 1) {
                return sexprToModuleElement(v[0]);
            }
            else {
                List<ModuleElement>elementList = new LinkedList<ModuleElement>();
                ModuleElement first = sexprToModuleElement(v[0]); 
                if (first != null) {
                    Module module = first.getModule();
                    elementList.add(first);
                    for (int i = 1; i < v.length; i++) {
                        ModuleElement element = sexprToModuleElement(v[i]);
                        if (element == null || !element.getModule().equals(module)) {
                            return null;
                        }
                        else {
                            elementList.add(element);
                        }
                    }
                    if (module.isRing()) {
                        Module resMod = ((Ring)module).getFreeModule(v.length);
                        return resMod.createElement(elementList);
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
        }
        else {
            return null;
        }
    }
    
    private static Symbol simple_sym  = Symbol.make("simple");
    private static Symbol limit_sym   = Symbol.make("limit");
    private static Symbol colimit_sym = Symbol.make("colimit");
    private static Symbol power_sym   = Symbol.make("power");
    private static Symbol list_sym    = Symbol.make("list");
    private static Symbol none_sym    = Symbol.make("none");

    protected static Repository rep = Repository.systemRepository();
}

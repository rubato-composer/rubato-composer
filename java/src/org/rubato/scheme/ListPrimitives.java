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

/**
 * Standard primitive procedures dealing with lists.
 * 
 * @author Gérard Milmeister
 */
abstract class ListPrimitives {

    public static void fillEnvironment(Env env) {
        env.addPrimitive(cons);
        env.addPrimitive(car);
        env.addPrimitive(cdr);
        env.addPrimitive(list);
        env.addPrimitive(length);
        env.addPrimitive(pair_p);
        env.addPrimitive(null_p);
        env.addPrimitive(list_p);
        env.addPrimitive(set_car);
        env.addPrimitive(set_cdr);
        env.addPrimitive(append);
        env.addPrimitive(reverse);
        env.addPrimitive(list_tail);
        env.addPrimitive(list_ref);
        env.addPrimitive(eq_p);
        env.addPrimitive(eqv_p);
        env.addPrimitive(equal_p);
        env.addPrimitive(memq);
        env.addPrimitive(memv);
        env.addPrimitive(member);
        env.addPrimitive(assq);
        env.addPrimitive(assv);
        env.addPrimitive(assoc);
        env.addPrimitive(caar);
        env.addPrimitive(cadr);
        env.addPrimitive(cdar);
        env.addPrimitive(cddr);
        env.addPrimitive(caaar);
        env.addPrimitive(caadr);
        env.addPrimitive(cadar);
        env.addPrimitive(caddr);
        env.addPrimitive(cdaar);
        env.addPrimitive(cdadr);
        env.addPrimitive(cddar);
        env.addPrimitive(cdddr);
        env.addPrimitive(caaaar);
        env.addPrimitive(caaadr);
        env.addPrimitive(caadar);
        env.addPrimitive(caaddr);
        env.addPrimitive(cadaar);
        env.addPrimitive(cadadr);
        env.addPrimitive(caddar);
        env.addPrimitive(cadddr);
        env.addPrimitive(cdaaar);
        env.addPrimitive(cdaadr);
        env.addPrimitive(cdadar);
        env.addPrimitive(cdaddr);
        env.addPrimitive(cddaar);
        env.addPrimitive(cddadr);
        env.addPrimitive(cdddar);
        env.addPrimitive(cddddr);
    }
    
    
    private static Primitive cons = new Primitive() {
        public String getName() { return "cons"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                return cons(car(args), car(cdr(args)));
            }
            else {
                eval.addError("cons: expect number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }        
    };
    
    private static Primitive car = new Primitive() {
        public String getName() { return "car"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                if (car(args).isCons()) {
                    return car(car(args));
                }
                else {
                    eval.addError("car: expected argument of type cons, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("car: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdr = new Primitive() {
        public String getName() { return "cdr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                if (car(args).isCons()) {
                    return cdr(car(args));
                }
                else {
                    eval.addError("cdr: expected argument of type cons, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("cdr: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive list = new Primitive() {
        public String getName() { return "list"; }
        public SExpr call(SExpr args, Evaluator eval) {
            return args;
        }
    };

    private static Primitive pair_p = new Primitive() {
        public String getName() { return "pair?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                return SBoolean.make(car(args).isCons());
            }
            else {
                eval.addError("pair?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive null_p = new Primitive() {
        public String getName() { return "null?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                return SBoolean.make(car(args).isNull());
            }
            else {
                eval.addError("null?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive list_p = new Primitive() {
        public String getName() { return "list?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                return SBoolean.make(car(args).getLength() >= 0);
            }
            else {
                eval.addError("list?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive length = new Primitive() {
        public String getName() { return "length"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                int l = car(args).getLength();
                if (l >= 0) {
                    return new SInteger(l);
                }
                else {
                    eval.addError("length: expected argument of type list, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("length: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive set_car = new Primitive() {
        public String getName() { return "set-car!"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                if (car(args).isCons()) {
                    car(args).setCar(args.nth(1));
                    return VOID;
                }
                else {
                    eval.addError("set-car!: expected argument of type cons, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("set-car!: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive set_cdr = new Primitive() {
        public String getName() { return "set-cdr!"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                if (car(args).isCons()) {
                    car(args).setCdr(args.nth(1));
                    return VOID;
                }
                else {
                    return null;
                }
            }
            else {
                eval.addError("set-cdr!: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive append = new Primitive() {
        public String getName() { return "append"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                return append(args.nth(0), args.nth(1));
            }
            else {
                eval.addError("append: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
        public SExpr append(SExpr l, SExpr a) {
            if (l.isNull()) {
                return a;
            }
            else {
                if (l.isCons()) {
                    SExpr c = append(cdr(l), a);
                    if (c != null) {
                        return cons(car(l), c);
                    }
                }
                return null;
            }
        }
    };

    private static Primitive reverse = new Primitive() {
        public String getName() { return "reverse"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                return reverse(car(args), NULL);
            }
            else {
                eval.addError("reverse: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive list_tail = new Primitive() {
        public String getName() { return "list-tail"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr l = args.nth(0);
                SExpr n = args.nth(1);
                if (n.isInteger()) {
                    int i = ((SInteger)n).getInt();
                    SExpr cdr_val = l;
                    while (i > 0) {
                        if (cdr_val.isCons()) {
                            cdr_val = cdr(cdr_val);
                        }
                        else {
                            eval.addError("list-tail: expected 1st argument of type list, but got %1", cdr_val);
                            return null;
                        }
                    }
                    return cdr_val;
                }
                else {
                    eval.addError("list-tail: expected 2nd argument of type integer, but got %1", n);
                    return null;
                }
            }
            else {
                eval.addError("list-tail: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive list_ref = new Primitive() {
        public String getName() { return "list-ref"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr l = args.nth(0);
                SExpr n = args.nth(1);
                if (n.isInteger()) {
                    int i = ((SInteger)n).getInt();
                    SExpr cdr_val = l;
                    while (i > 0) {
                        if (cdr_val.isCons()) {
                            cdr_val = cdr(cdr_val);
                        }
                        else {
                            eval.addError("list-ref: expected 1st argument of type list, but got %1", cdr_val);
                            return null;
                        }
                    }
                    if (cdr_val.isCons()) {
                        return car(cdr_val);
                    }
                    else {
                        eval.addError("list-ref: expected 1st argument of type list, but got %1", cdr_val);
                        return null;
                    }
                }
                else {
                    eval.addError("list-ref: expected 2nd argument of type integer, but got %1", n);
                    return null;
                }
            }
            else {
                eval.addError("list-ref: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive eq_p = new Primitive() {
        public String getName() { return "eq?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr a = args.nth(0);
                SExpr b = args.nth(1);
                return SBoolean.make(a.eq_p(b));
            }
            else {
                eval.addError("eq?: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive eqv_p = new Primitive() {
        public String getName() { return "eqv?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr a = args.nth(0);
                SExpr b = args.nth(1);
                return SBoolean.make(a.eqv_p(b));
            }
            else {
                eval.addError("eqv?: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive equal_p = new Primitive() {
        public String getName() { return "equal?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr a = args.nth(0);
                SExpr b = args.nth(1);
                return SBoolean.make(a.equal_p(b));
            }
            else {
                eval.addError("equal?: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive memq = new Primitive() {
        public String getName() { return "memq"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr obj = args.nth(0);
                SExpr lst = args.nth(1);
                while (lst.isCons()) {
                    if (lst.getCar().eq_p(obj)) {
                        return lst;
                    }
                    lst = lst.getCdr();
                }
                return SBoolean.FALSE;
            }
            else {
                eval.addError("memq: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive memv = new Primitive() {
        public String getName() { return "memv"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr obj = args.nth(0);
                SExpr lst = args.nth(1);
                while (lst.isCons()) {
                    if (lst.getCar().eqv_p(obj)) {
                        return lst;
                    }
                    lst = lst.getCdr();
                }
                return SBoolean.FALSE;
            }
            else {
                eval.addError("memv: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive member = new Primitive() {
        public String getName() { return "member"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr obj = args.nth(0);
                SExpr lst = args.nth(1);
                while (lst.isCons()) {
                    if (lst.getCar().equal_p(obj)) {
                        return lst;
                    }
                    lst = lst.getCdr();
                }
                return SBoolean.FALSE;
            }
            else {
                eval.addError("member: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive assq = new Primitive() {
        public String getName() { return "assq"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr obj = args.nth(0);
                SExpr lst = args.nth(1);
                while (lst.isCons()) {
                    SExpr a = lst.getCar();
                    if (a.isCons() && a.getCar().eq_p(obj)) {
                        return a;
                    }
                    lst = lst.getCdr();
                }
                return SBoolean.FALSE;
            }
            else {
                eval.addError("assq: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive assv = new Primitive() {
        public String getName() { return "assv"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr obj = args.nth(0);
                SExpr lst = args.nth(1);
                while (lst.isCons()) {
                    SExpr a = lst.getCar();
                    if (a.isCons() && a.getCar().eqv_p(obj)) {
                        return a;
                    }
                    lst = lst.getCdr();
                }
                return SBoolean.FALSE;
            }
            else {
                eval.addError("assv: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive assoc = new Primitive() {
        public String getName() { return "assoc"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr obj = args.nth(0);
                SExpr lst = args.nth(1);
                while (lst.isCons()) {
                    SExpr a = lst.getCar();
                    if (a.isCons() && a.getCar().equal_p(obj)) {
                        return a;
                    }
                    lst = lst.getCdr();
                }
                return SBoolean.FALSE;
            }
            else {
                eval.addError("assoc: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    public static SExpr reverse(SExpr l, SExpr a) {
        if (l.isNull()) {
            return a;
        }
        else {
            if (l.isCons()) {
                return reverse(cdr(l), cons(car(l), a));
            }
            return null;
        }
    }
    
    protected static SExpr safe_car(SExpr exp) {
        if (exp == null) {
            return null;
        }
        else if (exp.isCons()) {
            return car(exp);
        }
        else {
            return null;
        }
    }
    
    protected static SExpr safe_cdr(SExpr exp) {
        if (exp == null) {
            return null;
        }
        else if (exp.isCons()) {
            return cdr(exp);
        }
        else {
            return null;
        }
    }

    private static Primitive caar = new Primitive() {
        public String getName() { return "caar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_car(a));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cadr = new Primitive() {
        public String getName() { return "cadr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_cdr(a));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive cdar = new Primitive() {
        public String getName() { return "cdar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_car(a));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive cddr = new Primitive() {
        public String getName() { return "cddr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_cdr(a));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive caaar = new Primitive() {
        public String getName() { return "caaar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_car(safe_car(a)));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive caadr = new Primitive() {
        public String getName() { return "caadr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_car(safe_cdr(a)));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cadar = new Primitive() {
        public String getName() { return "cadar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_cdr(safe_car(a)));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive caddr = new Primitive() {
        public String getName() { return "caddr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_cdr(safe_cdr(a)));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdaar = new Primitive() {
        public String getName() { return "cdaar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_car(safe_car(a)));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdadr = new Primitive() {
        public String getName() { return "cdadr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_car(safe_cdr(a)));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cddar = new Primitive() {
        public String getName() { return "cddar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_cdr(safe_car(a)));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdddr = new Primitive() {
        public String getName() { return "cdddr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_cdr(safe_cdr(a)));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive caaaar = new Primitive() {
        public String getName() { return "caaaar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_car(safe_car(safe_car(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive caaadr = new Primitive() {
        public String getName() { return "caaadr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_car(safe_car(safe_cdr(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive caadar = new Primitive() {
        public String getName() { return "caadar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_car(safe_cdr(safe_car(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive caaddr = new Primitive() {
        public String getName() { return "caaddr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_car(safe_cdr(safe_cdr(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cadaar = new Primitive() {
        public String getName() { return "cadaar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_cdr(safe_car(safe_car(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cadadr = new Primitive() {
        public String getName() { return "cadadr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_cdr(safe_car(safe_cdr(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive caddar = new Primitive() {
        public String getName() { return "caddar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_cdr(safe_cdr(safe_car(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cadddr = new Primitive() {
        public String getName() { return "cadddr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_car(safe_cdr(safe_cdr(safe_cdr(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdaaar = new Primitive() {
        public String getName() { return "cdaaar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_car(safe_car(safe_car(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdaadr = new Primitive() {
        public String getName() { return "cdaadr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_car(safe_car(safe_cdr(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdadar = new Primitive() {
        public String getName() { return "cdadar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_car(safe_cdr(safe_car(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdaddr = new Primitive() {
        public String getName() { return "cdaddr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_car(safe_cdr(safe_cdr(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cddaar = new Primitive() {
        public String getName() { return "cddaar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_cdr(safe_car(safe_car(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cddadr = new Primitive() {
        public String getName() { return "cddadr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_cdr(safe_car(safe_cdr(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cdddar = new Primitive() {
        public String getName() { return "cdddar"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_cdr(safe_cdr(safe_car(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive cddddr = new Primitive() {
        public String getName() { return "cddddr"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                return safe_cdr(safe_cdr(safe_cdr(safe_cdr(a))));
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };


}

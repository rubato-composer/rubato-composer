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
 * Standard primitive procedures dealing with vectors.
 * 
 * @author Gérard Milmeister
 */
abstract class VectorPrimitives {

    public static void fillEnvironment(Env env) {
        env.addPrimitive(vector_p);
        env.addPrimitive(make_vector);
        env.addPrimitive(vector);
        env.addPrimitive(vector_length);
        env.addPrimitive(vector_ref);
        env.addPrimitive(vector_set);
        env.addPrimitive(vector_fill);
        env.addPrimitive(vector_to_list);
        env.addPrimitive(list_to_vector);
    }

    private static Primitive vector_p = new Primitive() {
        public String getName() { return "vector?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                return SBoolean.make(car(args).isVector());
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive make_vector = new Primitive() {
        public String getName() { return "make-vector"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr n = car(args);
                SExpr f = car(cdr(args));
                if (n.isInteger()) {
                    int len = (((SInteger)n).getInt());
                    len = (len < 0)?0:len;
                    SExpr[] array = new SExpr[len];
                    for (int i = 0; i < len; i++) {
                        array[i] = f;
                    }
                    return new SVector(array);
                }
                else {
                    eval.addError(getName()+": expected 1st argument to be of type integer, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError(getName()+": expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive vector = new Primitive() {
        public String getName() { return "vector"; }
        public SExpr call(SExpr args, Evaluator eval) {
            int len = args.getLength();
            if (len >= 0) {
                SExpr[] array = new SExpr[len];
                SExpr list = args;
                for (int i = 0; i < len; i++) {
                    array[i] = car(list);
                    list = cdr(list);
                }
                return new SVector(array);
            }
            else {
                eval.addError(getName()+": expected list of arguments, but got %1", args);
                return null;
            }
        }
    };

    private static Primitive vector_length = new Primitive() {
        public String getName() { return "vector-length"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                if (a.isVector()) {
                    return new SInteger(((SVector)a).getArray().length);
                }
                else {
                    eval.addError(getName()+": expected 1st argument to be of type vector, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive vector_ref = new Primitive() {
        public String getName() { return "vector-ref"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr a = car(args);
                SExpr n = car(cdr(args));
                if (a.isVector()) {
                    if (n.isInteger()) {
                        SExpr[] v = ((SVector)a).getArray();
                        int i = ((SInteger)n).getInt();
                        if (i >= 0 && i <= v.length) {
                            return v[i];
                        }
                        else {
                            eval.addError(getName()+": vector index %1 out of range", i);
                            return null;
                        }
                    }
                    else {
                        eval.addError(getName()+": expected 2nd argument to be of type integer, but got %1", car(cdr(args)));
                        return null;
                    }
                }
                else {
                    eval.addError(getName()+": expected 1st argument to be of type vector, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError(getName()+": expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive vector_set = new Primitive() {
        public String getName() { return "vector-set!"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 3) {
                SExpr a = car(args);
                SExpr n = car(cdr(args));
                SExpr e = car(cdr(cdr(args)));
                if (a.isVector()) {
                    if (n.isInteger()) {
                        SExpr[] v = ((SVector)a).getArray();
                        int i = ((SInteger)n).getInt();
                        if (i >= 0 && i <= v.length) {
                            v[i] = e;
                            return a;
                        }
                        else {
                            eval.addError(getName()+": vector index %1 out of range", i);
                            return null;
                        }
                    }
                    else {
                        eval.addError(getName()+": expected 2nd argument to be of type integer, but got %1", car(cdr(args)));
                        return null;
                    }
                }
                else {
                    eval.addError(getName()+": expected 1st argument to be of type vector, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError(getName()+": expected number of arguments is 3, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive vector_fill = new Primitive() {
        public String getName() { return "vector-fill!"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 2) {
                SExpr a = car(args);
                SExpr e = car(cdr(args));
                if (a.isVector()) {
                    SExpr[] v = ((SVector)a).getArray();
                    for (int i = 0; i < v.length; i++) {
                        v[i] = e;
                    }
                    return a;
                }
                else {
                    eval.addError(getName()+": expected 1st argument to be of type vector, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError(getName()+": expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive vector_to_list = new Primitive() {
        public String getName() { return "vector->list"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                if (a.isVector()) {
                    SExpr[] v = ((SVector)a).getArray();
                    int m = v.length-1;
                    SExpr res = NULL;
                    while (m >= 0) {
                        res = cons(v[m], res);
                        m--;
                    }
                    return res;
                }
                else {
                    eval.addError(getName()+": expected 1st argument to be of type vector, but got %1", a);
                    return null;
                }
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive list_to_vector = new Primitive() {
        public String getName() { return "list->vector"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                int len = a.getLength();
                if (len >= 0) {
                    SExpr[] v = new SExpr[len];
                    for (int i = 0; i < len; i++) {
                        v[i] = car(a);
                        a = cdr(a);
                    }
                    return new SVector(v);
                }
                else {
                    eval.addError(getName()+": expected 1st argument to be of type list, but got %1", a);
                    return null;
                }
            }
            else {
                eval.addError(getName()+": expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
}

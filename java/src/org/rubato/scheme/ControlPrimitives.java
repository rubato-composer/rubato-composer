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

import static org.rubato.scheme.SExpr.car;

/**
 * Standard primitive procedures for control structures and environments.
 * 
 * @author Gérard Milmeister
 */
abstract class ControlPrimitives {

    public static void fillEnvironment(Env env) {
        env.addPrimitive(procedure_p);
        env.addPrimitive(scheme_report_environment);
        env.addPrimitive(null_environment);
        env.addPrimitive(interaction_environment);
        env.addPrimitive(apply);
        env.addPrimitive(map);
    }

    private static Primitive procedure_p = new Primitive() {
        public String getName() { return "procedure?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr car = car(args);
                return SBoolean.make(car.isClosure() || car.isPrimitive());
            }
            else {
                eval.addError("procedure?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive scheme_report_environment = new Primitive() {
        public String getName() { return "scheme-report-environment"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr car = car(args);
                if (car.isInteger() && ((SInteger)car).getInt() == 5) {
                    return Env.makeStandardEnvironment();
                }
                else {
                    eval.addError("scheme-report-enviroment: expected argument is 5, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("scheme-report-enviroment: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive null_environment = new Primitive() {
        public String getName() { return "null-environment"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr car = car(args);
                if (car.isInteger() && ((SInteger)car).getInt() == 5) {
                    return new Env();
                }
                else {
                    eval.addError("null-enviroment: expected argument is 5, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("null-enviroment: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive interaction_environment = new Primitive() {
        public String getName() { return "interaction-environment"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 0) {
                return Env.makeGlobalEnvironment();
            }
            else {
                eval.addError("interaction-environment: expected number of arguments is 0, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    static Primitive map = new Primitive() {
        public String getName() { return "map"; }
        public SExpr call(SExpr args, Evaluator eval) {
            return SExpr.VOID;
        }
    };

    static Primitive apply = new Primitive() {
        public String getName() { return "apply"; }
        public SExpr call(SExpr args, Evaluator eval) {
            return SExpr.VOID;
        }
    };
}

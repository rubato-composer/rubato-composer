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

import static org.rubato.scheme.SExpr.VOID;
import static org.rubato.scheme.SExpr.car;
import static org.rubato.scheme.SExpr.cdr;

import java.io.*;
import java.util.List;


/**
 * Standard primitive procedure dealing with I/O.
 * 
 * @author Gérard Milmeister
 */
abstract class InputOutputPrimitives {

    public static void fillEnvironment(Env env) {
        env.addPrimitive(port_p);
        env.addPrimitive(open_input_file);
        env.addPrimitive(open_output_file);
        env.addPrimitive(close_input_port);
        env.addPrimitive(close_output_port);
        env.addPrimitive(input_port_p);
        env.addPrimitive(output_port_p);
        env.addPrimitive(current_input_port);
        env.addPrimitive(current_output_port);
        env.addPrimitive(display);
        env.addPrimitive(write);
        env.addPrimitive(newline);
        env.addPrimitive(write_char);
        env.addPrimitive(read);
    }
    
    private static Primitive port_p = new Primitive() {
        public String getName() { return "port?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr arg =car(args);
                return SBoolean.make(arg instanceof SInPort || arg instanceof SOutPort);
            }
            else {
                eval.addError("port?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }        
    };

    private static Primitive open_input_file = new Primitive() {
        public String getName() { return "open-input-file"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                if (a.isString()) {
                    String name = ((SString)a).getString();
                    try {
                        return new SInPort(new FileInputStream(name));
                    }
                    catch (FileNotFoundException e) {
                        eval.addError("open-input-file: file %%1 not found", name);
                        return null;
                    }
                }
                else {
                    eval.addError("open-input-file: expected argument type is string, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("open-input-file: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive open_output_file = new Primitive() {
        public String getName() { return "open-output-file"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr a = car(args);
                if (a.isString()) {
                    String name = ((SString)a).getString();
                        try {
                            return new SOutPort(new PrintStream(new FileOutputStream(name)));
                        }
                        catch (FileNotFoundException e) {
                            eval.addError("open-input-file: could not open file %%1 for writing", name);
                            return null;
                        }
                }
                else {
                    eval.addError("open-output-file: expected argument type is string, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("open-output-file: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive close_input_port = new Primitive() {
        public String getName() { return "close-input-port"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr port = car(args);
                if (port instanceof SInPort) {
                    SInPort in = (SInPort)port;
                    in.close();
                    return VOID;
                }
                else {
                    eval.addError("close-input-port: expected argument type is input-port, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("close-input-port: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive close_output_port = new Primitive() {
        public String getName() { return "close-output-port"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                SExpr port = car(args);
                if (port instanceof SOutPort) {
                    SOutPort out = (SOutPort)port;
                    out.close();
                    return VOID;
                }
                else {
                    eval.addError("close-input-port: expected argument type is input-port, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("close-input-port: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive input_port_p = new Primitive() {
        public String getName() { return "input-port?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                return SBoolean.make(car(args) instanceof SInPort);
            }
            else {
                eval.addError("input-port?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive output_port_p = new Primitive() {
        public String getName() { return "output-port?"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 1) {
                return SBoolean.make(car(args) instanceof SOutPort);
            }
            else {
                eval.addError("output-port?: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive current_output_port = new Primitive() {
        public String getName() { return "current-output-port"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 0) {
                return eval.getCurrentOutputPort();
            }
            else {
                eval.addError("current-output-port: expected number of arguments is 0, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive current_input_port = new Primitive() {
        public String getName() { return "current-input-port"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() == 0) {
                return eval.getCurrentInputPort();
            }
            else {
                eval.addError("current-input-port: expected number of arguments is 0, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive display = new Primitive() {
        public String getName() { return "display"; }
        public SExpr call(SExpr args, Evaluator eval) {
            int l = args.getLength();
            if (l == 1) {
                eval.getCurrentOutputPort().getPort().print(car(args).display());
                return VOID;
            }
            else if (l == 2) {
                SExpr port = car(cdr(args));
                if (port instanceof SOutPort) {
                    ((SOutPort)port).getPort().print(car(args).display());
                    return VOID;
                }
                else {
                    eval.addError("display: expected 2nd argument of type output-port, but got %1", car(cdr(args)));
                    return null;
                }
            }
            else {
                eval.addError("display: expected number of arguments is 1 or 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive write = new Primitive() {
        public String getName() { return "write"; }
        public SExpr call(SExpr args, Evaluator eval) {
            int l = args.getLength();
            if (l == 1) {
                eval.getCurrentOutputPort().getPort().print(car(args));
                return VOID;
            }
            else if (l == 2) {
                SExpr port = car(cdr(args));
                if (port instanceof SOutPort) {
                    ((SOutPort)port).getPort().print(car(args));
                    return VOID;
                }
                else {
                    eval.addError("write: expected 2nd argument of type output-port, but got %1", car(cdr(args)));
                    return null;
                }
            }
            else {
                eval.addError("write: expected number of arguments is 1 or 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive write_char = new Primitive() {
        public String getName() { return "write-char"; }
        public SExpr call(SExpr args, Evaluator eval) {
            int l = args.getLength();
            if (l == 1) {
                if (car(args).isChar()) {
                    eval.getCurrentOutputPort().getPort().print(((SChar)car(args)).getChar());
                    return VOID;
                }
                else {
                    eval.addError("write-char: expected 1st argument of type char, but got %1", car(args));
                    return null;
                }
            }
            else if (l == 2) {
                if (car(args).isChar()) {
                    SExpr port = car(cdr(args));
                    if (port instanceof SOutPort) {
                        ((SOutPort)port).getPort().print(((SChar)car(args)).getChar());
                        return VOID;
                    }
                    else {
                        eval.addError("write-char: expected 2nd argument of type output-port, but got %1", car(cdr(args)));
                        return null;
                    }
                }
                else {
                    eval.addError("write-char: expected 1st argument of type char, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("write-char: expected number of arguments is 1 or 2, but got %1", args.getLength());
                return null;
            }
        }
    };

    private static Primitive newline = new Primitive() {
        public String getName() { return "newline"; }
        public SExpr call(SExpr args, Evaluator eval) {
            int l = args.getLength();
            if (l == 0) {
                eval.getCurrentOutputPort().getPort().println();
                return VOID;
            }
            else if (l == 1) {
                SExpr port = car(args);
                if (port instanceof SOutPort) {
                    ((SOutPort)port).getPort().println();
                    return VOID;
                }
                else {
                    eval.addError("newline: expected argument of type output-port, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("newline: expected number of arguments is 0 or 1, but got %1", args.getLength());
                return null;
            }
        }
    };
    
    private static Primitive read = new Primitive() {
        public String getName() { return "read"; }
        public SExpr call(SExpr args, Evaluator eval) {
            int l = args.getLength();
            if (l == 0) {
                try {
                    String string = eval.getCurrentInputPort().getReader().readLine();
                    List<SExpr> res = parser.parse(string);
                    if (res == null) {
                        eval.addError("read: error while reading from %1", eval.getCurrentInputPort());
                        return null;
                    }
                    else if (res.size() == 0) {
                        return VOID;
                    }
                    else {
                        return res.get(0);
                    }
                }
                catch (IOException e) {
                    eval.addError("read: I/O error while reading from %1", eval.getCurrentInputPort());
                    return null;
                }
            }
            else if (l == 1) {
                SExpr port = car(args);
                if (port instanceof SInPort) {
                    SInPort in = (SInPort)port;
                    try {
                        String string = in.getReader().readLine();
                        List<SExpr> res = parser.parse(string);
                        if (res == null) {
                            eval.addError("read: error while reading from %1", in);
                            return null;
                        }
                        else if (res.size() == 0) {
                            return VOID;
                        }
                        else {
                            return res.get(0);
                        }
                    }
                    catch (IOException e) {
                        eval.addError("read: I/O error while reading from %1", in);
                        return null;
                    }
                }
                else {
                    eval.addError("read: expected argument of type input-port, but got %1", car(args));
                    return null;
                }
            }
            else {
                eval.addError("read: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
        }
    };

    
    protected static Parser parser = new Parser();    
}

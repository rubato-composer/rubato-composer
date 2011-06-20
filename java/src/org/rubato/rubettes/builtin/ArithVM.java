/*
 * Copyright (C) 2005 Gérard Milmeister
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

package org.rubato.rubettes.builtin;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.rubato.util.TextUtils;

/**
 * The virtual machine for execution bytecode created by
 * {@link ArithCompiler}.
 * 
 * @author Gérard Milmeister
 */
public class ArithVM {

    public ArithVM() { /* do nothing */ }
    
    
    public void eval(double ... parameters) {
        error = null;
        params = parameters;
        if (params.length-1 < maxParam) {
            setError("Not enough parameters");
            return;
        }
        ip = 0;
        sp = 0;

        while (ip < code.length) {
            int c = code[ip] & 255;
            int d = code[ip] >> 8;
            switch (c) {
            case IMMEDIATE: {
                stack[sp] = data[d];
                sp++;
                break;
            }
            case TIMES: {
                stack[sp-2] = stack[sp-2]*stack[sp-1];
                sp--;
                break;
            }
            case PLUS: {
                stack[sp-2] = stack[sp-2]+stack[sp-1];
                sp--;
                break;
            }
            case MINUS: {
                stack[sp-2] = stack[sp-2]-stack[sp-1];
                sp--;
                break;
            }
            case DIV: {
                stack[sp-2] = stack[sp-2]/stack[sp-1];
                sp--;
                break;
            }
            case EXP: {
                stack[sp-2] = pow(stack[sp-2],stack[sp-1]);
                sp--;
                break;
            }
            case UMINUS: {
                stack[sp-1] = -stack[sp-1];
                break;
            }
            case PARAM: {
                stack[sp] = params[d];
                sp++;
                break;
            }
            case FUNCALL: {
                FunInfo fi = functions.get(d);
                switch (fi.nrArgs) {
                case 0: {
                    stack[sp] = fi.eval();
                    sp++;
                    break;
                }
                case 1: {
                    stack[sp-1] = fi.eval(stack[sp-1]);
                    break;
                }
                case 2: {
                    stack[sp-2] = fi.eval(stack[sp-2], stack[sp-1]);
                    sp--;
                    break;
                }
                case 3: {
                    stack[sp-3] = fi.eval(stack[sp-3], stack[sp-2], stack[sp-1]);
                    sp -= 2;
                    break;
                }
                }
                break;
            }
            case JMP: {
                ip = d-1;
                break;
            }
            case BIF: {
                if (stack[sp-1] == 0) {
                    ip = d-1;
                }
                sp--;
                break;
            }
            case GE: {
                stack[sp-2] = (stack[sp-2] >= stack[sp-1])?1:0;
                sp--;
                break;
            }
            case GT: {
                stack[sp-2] = (stack[sp-2] > stack[sp-1])?1:0;
                sp--;
                break;
            }
            case LE: {
                stack[sp-2] = (stack[sp-2] <= stack[sp-1])?1:0;
                sp--;
                break;
            }
            case LT: {
                stack[sp-2] = (stack[sp-2] < stack[sp-1])?1:0;
                sp--;
                break;
            }
            case EQUALS: {
                stack[sp-2] = (stack[sp-2] == stack[sp-1])?1:0;
                sp--;
                break;
            }
            case NEQ: {
                stack[sp-2] = (stack[sp-2] == stack[sp-1])?1:0;
                sp--;
                break;
            }
            case NOT: {
                stack[sp-1] = 1-stack[sp-1];
                break;
            }
            case AND: {
                stack[sp-2] = stack[sp-2]*stack[sp-1];
                sp--;
                break;
            }
            case OR: {
                stack[sp-2] = (stack[sp-2] == 0)?stack[sp-1]:1;
                sp--;
                break;
            }
            case TRUE: {
                stack[sp] = 1;
                sp++;
                break;
            }
            case FALSE: {
                stack[sp] = 0;
                sp++;
                break;
            }
            case NOP: {
                // do nothing
                break;
            }
            default: {
                setError("Error in VM.");
                return;
            }
            }
            ip++;
        }
    }
    
    
    public double getRealResult() {
        return stack[0];
    }
    
    
    public boolean getBooleanResult() {
        return stack[0] == 1.0;
    }
    
    
    public void setError(String s) {
        error = s;
    }
    
    
    public void setError(String s, Object ... objects) {
        error = TextUtils.replaceStrings(s, objects);
    }
    
    
    public boolean hasError() {
        return error != null;
    }
    
    
    public String getError() {
        return error;
    }
    
    
    public void start() {
        tmpCode = new ArrayList<Integer>();
        tmpData = new ArrayList<Double>();
        maxParam = -1;
    }
    
    
    public void finish() {
        tmpCode.add(NOP);
        code = new int[tmpCode.size()];
        data = new double[tmpData.size()];
        for (int i = 0; i < tmpCode.size(); i++) {
            code[i] = tmpCode.get(i);
        }
        for (int i = 0; i < tmpData.size(); i++) {
            data[i] = tmpData.get(i);
        }
        tmpCode = null;
        tmpData = null;
    }

    
    public int getNrArgs() {
        return maxParam+1;
    }
    
    
    public void compileOp(int op) {
        tmpCode.add(op);
    }
    
    
    public void compileUnaryMinus() {
        tmpCode.add(UMINUS);
    }
    
    
    public void compileFalse() {
        tmpCode.add(FALSE);
    }
    
    
    public void compileTrue() {
        tmpCode.add(TRUE);
    }
    
    
    public void compileNot() {
        tmpCode.add(NOT);
    }
    
    
    public boolean compileId(String id, int nrArgs) {
        FunInfo fi = functionsMap.get(id);
        if (fi == null || fi.nrArgs != nrArgs) {
            return false;
        }
        tmpCode.add(FUNCALL+256*fi.code);
        return true;
    }
    
    
    public void compileParam(int param) {
        if (param > maxParam) { maxParam = param; }
        tmpCode.add(PARAM+256*param);
    }
    
    
    public void compileNumber(double value) {
        tmpData.add(value);
        int p = tmpData.size()-1;
        tmpCode.add(IMMEDIATE+256*p);
    }
    
    
    public int compileIf() {
        tmpCode.add(BIF);
        return tmpCode.size()-1;
    }
    
    
    public int compileThen(int ifpos) {
        tmpCode.add(JMP);
        tmpCode.set(ifpos, BIF+256*tmpCode.size());
        return tmpCode.size()-1;
    }
    
    
    public void compileElse(int jmppos) {
        tmpCode.set(jmppos, JMP+256*tmpCode.size());
    }
    
    
    public void dump() {
        for (int i = 0; i < code.length; i++) {
            System.out.print(i+": ");
            int c = code[i] & 255;
            int d = code[i] >> 8;
            if (c == IMMEDIATE) {
                System.out.println("immediate "+data[d]);
            }
            else if (c == TIMES) {
                System.out.println("*");
            }
            else if (c == PLUS) {
                System.out.println("+");
            }
            else if (c == MINUS) {
                System.out.println("-");
            }
            else if (c == UMINUS) {
                System.out.println("uminus");
            }
            else if (c == DIV) {
                System.out.println("/");
            }
            else if (c == PARAM) {
                System.out.println("param "+d);
            }
            else if (c == NOP) {
                System.out.println("nop");
            }
            else if (c == ID) {
                System.out.println("id");
            }
            else if (c == FUNCALL) {
                System.out.println("funcall "+d);
            }
            else if (c == EXP) {
                System.out.println("^");
            }
            else if (c == AND) {
                System.out.println("&");
            }
            else if (c == OR) {
                System.out.println("|");
            }
            else if (c == LE) {
                System.out.println("<=");
            }
            else if (c == LT) {
                System.out.println("<");
            }
            else if (c == GE) {
                System.out.println(">=");
            }
            else if (c == GT) {
                System.out.println(">");
            }
            else if (c == NOT) {
                System.out.println("!");
            }
            else if (c == EQUALS) {
                System.out.println("=");
            }
            else if (c == NEQ) {
                System.out.println("!=");
            }
            else if (c == TRUE) {
                System.out.println("T");
            }
            else if (c == FALSE) {
                System.out.println("F");
            }
            else if (c == BIF) {
                System.out.println("branch if false to "+d);
            }
            else if (c == JMP) {
                System.out.println("jump to "+d);
            }
        }
    }
    
    public static final int IMMEDIATE = 0;
    public static final int TIMES     = 1;
    public static final int DIV       = 2;
    public static final int PLUS      = 3;
    public static final int MINUS     = 4;
    public static final int PARAM     = 5;
    public static final int NOP       = 6;
    public static final int ID        = 7;
    public static final int FUNCALL   = 8;
    public static final int UMINUS    = 9;
    public static final int EXP       = 10;
    public static final int AND       = 11;
    public static final int OR        = 12;
    public static final int LE        = 13;
    public static final int LT        = 14;
    public static final int GE        = 15;
    public static final int GT        = 16;
    public static final int EQUALS    = 17;
    public static final int NEQ       = 18;
    public static final int FALSE     = 19;
    public static final int TRUE      = 20;
    public static final int NOT       = 21;
    public static final int BIF       = 22;
    public static final int JMP       = 23;
    
    private ArrayList<Integer> tmpCode;
    private ArrayList<Double>  tmpData;
    
    private int[]    code;
    private double[] data;
    private int      maxParam;
    private double[] stack = new double[256];
    private double[] params;
    private int      ip;
    private int      sp;
    private String   error;
 
    private static HashMap<String,FunInfo> functionsMap = new HashMap<String,FunInfo>();
    private static ArrayList<FunInfo>      functions = new ArrayList<FunInfo>();
    
    private static int codeCounter = 0;

    private static void addFunction(FunInfo fi) {
        fi.code = codeCounter++;
        functions.add(fi);
        functionsMap.put(fi.id, fi);
    }
    
    private static class FunInfo implements Comparable<FunInfo> {
        
        public FunInfo(String id, int nrArgs) {
            this.id = id;
            this.nrArgs = nrArgs;
        }
        
        public double eval() {
            return 0;
        }
        
        public double eval(double a) {
            return 0;
        }
        
        public double eval(double a, double b) {
            return 0;
        }
        
        public double eval(double a, double b, double c) {
            return 0;
        }
        
        public int compareTo(FunInfo obj) {
            return id.compareTo(obj.id);
        }
        
        public int hashCode() {
            return id.hashCode();
        }
        
        public String id;
        public int    nrArgs;
        public int    code;
    }
    
    static {
        FunInfo cos = new FunInfo("cos", 1) {
            public double eval(double a) {
                return cos(a);
            }
        };
        addFunction(cos);
        FunInfo sin = new FunInfo("sin", 1) {
            public double eval(double a) {
                return sin(a);
            }
        };
        addFunction(sin);
        FunInfo tan = new FunInfo("tan", 1) {
            public double eval(double a) {
                return tan(a);
            }
        };
        addFunction(tan);
        FunInfo log = new FunInfo("log", 1) {
            public double eval(double a) {
                return log(a);
            }
        };
        addFunction(log);
        FunInfo exp = new FunInfo("exp", 1) {
            public double eval(double a) {
                return exp(a);
            }
        };
        addFunction(exp);
        FunInfo abs = new FunInfo("abs", 1) {
            public double eval(double a) {
                return abs(a);
            }
        };
        addFunction(abs);
        FunInfo sqrt = new FunInfo("sqrt", 1) {
            public double eval(double a) {
                return sqrt(a);
            }
        };
        addFunction(sqrt);
        FunInfo max = new FunInfo("max", 2) {
            public double eval(double a, double b) {
                return max(a, b);
            }
        };
        addFunction(max);
        FunInfo min = new FunInfo("min", 2) {
            public double eval(double a, double b) {
                return min(a, b);
            }
        };
        addFunction(min);
        FunInfo pow = new FunInfo("pow", 2) {
            public double eval(double a, double b) {
                return pow(a, b);
            }
        };
        addFunction(pow);
    }
}

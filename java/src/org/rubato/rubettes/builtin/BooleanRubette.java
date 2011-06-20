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

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JConnectorSliders;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


/**
 * The Boolean rubette evaluates a logical expression on its inputs,
 * which are of form "Boolean".
 * 
 * @author Gérard Milmeister
 */
public final class BooleanRubette extends AbstractRubette {    
    
    public BooleanRubette() {
        expression = new Expression();
        setInCount(2);
        setOutCount(1);
        values = new boolean[getInCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = false;
        }
    }
    

    public void run(RunInfo runInfo) {
        if (!expression.hasCode()) {
            addError(Messages.getString("BooleanRubette.novalidexpression")); //$NON-NLS-1$
            return;
        }
        for (int i = 0; i < getInCount(); i++) {
            Denotator d = getInput(i);
            if (d == null) {
                values[i] = false;
            }
            else if (d.getForm().equals(booleanForm)) {
                int n = ((SimpleDenotator)d).getModInteger();
                values[i] = (n==0)?false:true;
            }
            else {
                addError(Messages.getString("BooleanRubette.inputwrongform"), i, "Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        if (!hasErrors()) {
            boolean res = expression.eval(values);
            setOutput(0, res?trueDeno:falseDeno);
        }
    }

    
    public Rubette duplicate() {
        BooleanRubette rubette = new BooleanRubette();
        rubette.setInCount(getInCount());
        rubette.setOutCount(getOutCount());
        rubette.expression = expression.newInstance();
        rubette.expressionString = expressionString;
        rubette.values = new boolean[getInCount()];
        return rubette;
    }
    

    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "Boolean"; //$NON-NLS-1$
    }

    
    public boolean hasProperties() {
        return true;
    }
    

    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            
            inSlider = new JConnectorSliders(true, false);
            inSlider.setInLimits(1, 8);
            inSlider.setInValue(getInCount());
            properties.add(inSlider, BorderLayout.NORTH);
            
            exprTextArea = new JTextArea(5, 20);
            exprTextArea.setText(expressionString);
            JScrollPane scrollPane = new JScrollPane(exprTextArea);
            properties.add(scrollPane, BorderLayout.CENTER);
            
            infoLabel = new JLabel(" "); //$NON-NLS-1$
            infoLabel.setForeground(Color.RED);
            infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            properties.add(infoLabel, BorderLayout.SOUTH);
        }
        return properties;
    }
    
    
    public boolean applyProperties() {
        infoLabel.setText(" "); //$NON-NLS-1$
        int newInCount = inSlider.getInValue();
        String expr = exprTextArea.getText().trim();
        if (expr.length() == 0) {
            infoLabel.setText(Messages.getString("BooleanRubette.noexpression")); //$NON-NLS-1$
            return false;
        }
        boolean ok = expression.parse(expr, newInCount);
        if (!ok) {
            infoLabel.setText(expression.getError());
            return false;
        }
        else {
            setInCount(newInCount);
            values = new boolean[getInCount()];
            expressionString = expr;
            return true;
        }
    }
    
    
    public void revertProperties() {
        infoLabel.setText(" "); //$NON-NLS-1$
        inSlider.setInValue(getInCount());
        exprTextArea.setText(expressionString);
    }
    

    public String getShortDescription() {
        return "Evaluates a logical expression";
    }

    
    public String getLongDescription() {
        return "The Boolean Rubette evaluates a logical expression "+
               "on its inputs of form \"Boolean\".";
    }
    
    
    public String getInTip(int i) {
        return "Denotator of form \"Boolean\" #"+i;
    }

    
    public String getOutTip(int i) {
        return Messages.getString("BooleanRubette.result"); //$NON-NLS-1$
    }
    

    public ImageIcon getIcon() {
        return icon;
    }
    
    
    private final static String INPUTS      = "Inputs"; //$NON-NLS-1$
    private final static String EXPRESSION  = "Expression"; //$NON-NLS-1$
    private final static String NUMBER_ATTR = "number"; //$NON-NLS-1$
    private final static String EXPR_ATTR   = "expr"; //$NON-NLS-1$
    
    
    public void toXML(XMLWriter writer) {
        writer.empty(INPUTS, NUMBER_ATTR, getInCount());
        writer.empty(EXPRESSION, EXPR_ATTR, expression);
    }

    
    public Rubette fromXML(XMLReader reader, Element element) {
        BooleanRubette rubette = new BooleanRubette();
        Element child = XMLReader.getChild(element, INPUTS);
        if (child != null) {
            int n = XMLReader.getIntAttribute(child, NUMBER_ATTR, 1);
            rubette.setInCount(n);
            child = XMLReader.getNextSibling(child, EXPRESSION);
            if (child != null) {
                rubette.expressionString = XMLReader.getStringAttribute(child, EXPR_ATTR);
                rubette.expression.parse(rubette.expressionString, getInCount());
            }
        }
        return rubette;
    }
    
    
    private JPanel       properties = null;
    private JTextArea    exprTextArea = null;
    private JLabel       infoLabel = null;
    private String       expressionString = ""; //$NON-NLS-1$
    private Expression   expression;
    private boolean[]    values = null;
    private JConnectorSliders inSlider = null;
    
    
    protected final class Expression {
        
        public boolean parse(String expr, int inputCount) {
            ins = inputCount;
            str = expr.trim();
            pos = 0;
            ch = nextChar();
            newCode = new ArrayList<Integer>();
            boolean ok = parseExpr();
            if (ok) {
                code = new int[newCode.size()];
                for (int i = 0; i < newCode.size(); i++) {
                    code[i] = newCode.get(i);
                }
            }
            return ok;
        }
        
        
        public boolean eval(boolean vals[]) {
            int stp = 0;
            for (int i = 0; i < code.length; i++) {
                switch (code[i]) {
                    case C_NOT: {
                        stack[stp-1] = !stack[stp-1];
                        break;
                    }
                    case C_AND: {
                        stack[stp-2] = stack[stp-2] & stack[stp-1];
                        stp--;
                        break;
                        
                    }
                    case C_OR: {
                        stack[stp-2] = stack[stp-2] | stack[stp-1];
                        stp--;
                        break;
                    }
                    case C_TRUE: {
                        stack[stp] = true;
                        stp++;
                        break;
                    }
                    case C_FALSE: {
                        stack[stp] = false;
                        stp++;
                        break;
                    }
                    default: {
                        stack[stp] = vals[code[i]];
                        stp++;
                    }
                }
            }
            return stack[0];
        }
        
        
        public String getError() {
            return error;
        }
        
        
        public boolean hasCode() {
            return code != null;
        }
        
        
        public void displayCode() {
            String s;
            for (int i = 0; i < code.length; i++) {
                s = i+": "+code[i]; //$NON-NLS-1$
                System.out.println(s);
            }
        }
        
        
        public Expression newInstance() {
            Expression e = new Expression();
            e.ins = ins;
            if (code != null) {
                e.code = code.clone();
            }
            return e;
        }
        
        
        private int       ins;
        private char      ch;
        private int       pos;
        private String    str;
        private boolean[] stack = new boolean[30];
        private int[]     code = null;
        private ArrayList<Integer> newCode = null;
        private String    error = ""; //$NON-NLS-1$

        private final static int C_OR = 50;
        private final static int C_AND = 51;
        private final static int C_NOT = 52;
        private final static int C_TRUE = 53;
        private final static int C_FALSE = 54;
        
        private char nextChar() {
            char c;
            if (pos >= str.length()) {
                return 0;
            }
            c = str.charAt(pos);
            while (Character.isWhitespace(c)) {
                pos++;
                c = str.charAt(pos);
            }
            pos++;
            return c;
        }
        
        
        private boolean parseExpr() {
            if (!parseTerm()) {
                return false;
            }
            while (ch == '|') {
                ch = nextChar();
                if (!parseTerm()) {
                    return false;
                }
                newCode.add(C_OR);
            }
            return true;
        }
        
        
        private boolean parseTerm() {
            if (!parseFactor()) {
                return false;
            }
            while (ch == '&') {
                ch = nextChar();
                if (!parseFactor()) {
                    return false;
                }
                newCode.add(C_AND);
            }
            return true;
        }

        
        private boolean parseFactor() {
            return parseLiteral();
        }

        
        private boolean parseLiteral() {
            boolean neg = false;
            while (ch == '~') {
                neg = !neg;
                ch = nextChar();
            }
            if (ch == 0) { return false; }
            if (ch == '#') {
                if ((ch = nextChar()) == 0) {
                    error = "Syntax error after \"#\"!"; //$NON-NLS-1$
                    return false;
                }
                if (Character.isDigit(ch)) {
                    int n = ch-'0';
                    if (n >= ins) {
                        error = "Variable #"+n+" out of range!"; //$NON-NLS-1$ //$NON-NLS-2$
                        return false;
                    }
                    newCode.add(n);
                    if (neg) {
                        newCode.add(C_NOT);
                    }
                    ch = nextChar();
                    return true;
                }
            }
            else if (ch == 'T') {
                int c = neg?C_FALSE:C_TRUE;
                newCode.add(c);
                return true;
            }
            else if (ch == 'F') {
                int c = neg?C_TRUE:C_FALSE;
                newCode.add(c);
                return true;
            }
            else if (ch =='(') {
                if ((ch = nextChar()) == 0) {
                    error = "Syntax error after \"(\"!"; //$NON-NLS-1$
                    return false;
                }
                parseExpr();
                if (ch != ')') {
                    error = "Missing \")\"!"; //$NON-NLS-1$
                    return false;
                }
                if (neg) {
                    newCode.add(C_NOT);
                }
                ch = nextChar();
                return true;
            }
            error = "Syntax error!"; //$NON-NLS-1$
            return false;
        }
    }
    

    private static final Form      booleanForm; 
    private static final Denotator trueDeno; 
    private static final Denotator falseDeno;
    private static final ImageIcon icon;
    
    static {
        icon = Icons.loadIcon(BooleanRubette.class, "logicicon.png"); //$NON-NLS-1$
        Repository rep = Repository.systemRepository();
        booleanForm = rep.getForm("Boolean"); //$NON-NLS-1$
        trueDeno = rep.getDenotator("True"); //$NON-NLS-1$
        falseDeno = rep.getDenotator("False");         //$NON-NLS-1$
    }
}

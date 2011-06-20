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

import static org.rubato.scheme.SExpr.VOID;
import static org.rubato.scheme.SExpr.car;
import static org.rubato.scheme.SExpr.cdr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JConnectorSliders;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.Denotator;
import org.rubato.scheme.*;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


public class SchemeRubette extends AbstractRubette {

    public SchemeRubette() {
        setInCount(1);
        setOutCount(1);
    }
    
    
    public void run(RunInfo runInfo) {
        Env globalEnv = rep.getSchemeEnvironment();
        Env env = new Env(globalEnv);
        addPrimitives(env);
        Evaluator evaluator = new Evaluator(env);
        if (sexprList != null) {
            evaluator.setRunInfo(runInfo);
            evaluator.eval(sexprList);
            if (evaluator.hasErrors()) {
                addError("Scheme error: "+evaluator.getErrors().get(0)+".");
            }
        }
        else {
            addError("No valid Scheme expression has been specified.");
        }
    }

    
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }
    

    public String getName() {
        return "Scheme"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        SchemeRubette rubette = new SchemeRubette();
        rubette.setInCount(getInCount());
        rubette.setOutCount(getOutCount());
        rubette.schemeCode = schemeCode;
        rubette.sexprList = rubette.parser.parse(rubette.schemeCode);
        return rubette;
    }
    
    
    public boolean hasProperties() {
        return true;
    }


    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            
            sliders = new JConnectorSliders(true, true);
            sliders.setInLimits(1, 8);
            sliders.setOutLimits(1, 8);
            sliders.setInValue(getInCount());
            sliders.setOutValue(getOutCount());
            properties.add(sliders, BorderLayout.NORTH);

            schemeCodeArea = new JTextArea(20, 0);
            schemeCodeArea.setFont(Font.decode("monospaced"));
            schemeCodeArea.setText(schemeCode);            
            properties.add(new JScrollPane(schemeCodeArea), BorderLayout.CENTER);

            infoLabel = new JLabel(" "); //$NON-NLS-1$
            infoLabel.setForeground(Color.RED);
            infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            properties.add(infoLabel, BorderLayout.SOUTH);
        }
        return properties;
    }


    public boolean applyProperties() {
        setInCount(sliders.getInValue());
        setOutCount(sliders.getOutValue());
        String code = schemeCodeArea.getText();
        List<SExpr> sexprs = parser.parse(code);
        if (parser.hasError()) {
            infoLabel.setText(parser.getError());
            parser.clearError();
            sexprList = null;
            return false;
        }
        else {
            schemeCode = code;
            sexprList = sexprs;
            infoLabel.setText(" ");
            return true;
        }
    }


    public void revertProperties() {
        sliders.setInValue(getInCount());
        sliders.setOutValue(getOutCount());
        schemeCodeArea.setText(schemeCode);
    }

    
    public String getShortDescription() {
        return "Executes Scheme code on the input denotators";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The Scheme rubette retrieves the input denotators"+
               " and executes the specified Scheme code.";
    }


    public String getInTip(int i) {
        return "Input denotator"; //$NON-NLS-1$
    }


    public String getOutTip(int i) {
        return "Output denotator #"+i; //$NON-NLS-1$
    }

    
    private final static String INPUTS      = "Inputs"; //$NON-NLS-1$
    private final static String OUTPUTS     = "Outputs"; //$NON-NLS-1$
    private final static String CODE        = "Code"; //$NON-NLS-1$
    private final static String NUMBER_ATTR = "number"; //$NON-NLS-1$
    
    
    public void toXML(XMLWriter writer) {
        writer.empty(INPUTS, NUMBER_ATTR, getOutCount());
        writer.empty(OUTPUTS, NUMBER_ATTR, getOutCount());
        writer.openBlock(CODE);
        writer.writeTextNode(schemeCode.trim()+"\n");
        writer.closeBlock();
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        int inCount;
        int outCount;
        String text = "";

        Element child = XMLReader.getChild(element, INPUTS);
        if (child != null) {
            inCount = XMLReader.getIntAttribute(child, NUMBER_ATTR, 1, 8, 0);
        }
        else {
            reader.setError("Expected element <%1>", INPUTS);
            return null;
        }
        
        child = XMLReader.getNextSibling(child, OUTPUTS);
        if (child != null) {
            outCount = XMLReader.getIntAttribute(child, NUMBER_ATTR, 1, 8, 0);
        }
        else {
            reader.setError("Expected element <%1>", OUTPUTS);
            return null;
        }
        child = XMLReader.getNextSibling(child, CODE);
        if (child != null) {
            text = XMLReader.getText(child).trim();
        }
        else {
            reader.setError("Expected element <%1>", CODE);
            return null;
        }
        
        SchemeRubette r = new SchemeRubette();
        r.setInCount(inCount);
        r.setOutCount(outCount);
        r.schemeCode = text;
        r.sexprList = r.parser.parse(r.schemeCode);
        return r;
    }

    
    private void addPrimitives(Env env) {
        env.addPrimitive(get_input);
        env.addPrimitive(set_output);
    }
    
    
    private Primitive get_input = new Primitive() {
        public String getName() { return "get-input"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() != 1) {
                eval.addError("get-input: expected number of arguments is 1, but got %1", args.getLength());
                return null;
            }
            else {
                SExpr arg = car(args);
                if (arg.isInteger()) {
                    int input = ((SInteger)arg).getInt();
                    if (input >= 0 && input < getInCount()) {
                        Denotator d = getInput(input);
                        if (d == null) {
                            eval.addError("get-input: input %1 is null", input);
                            return null;
                        }
                        else {
                            return new SDenotator(d);
                        }
                    }
                    else {
                        eval.addError("get-input: expected argument >= 0 and < %2, but got %1", input, getInCount());
                        return null;
                    }
                }
                else {
                    eval.addError("get-input: expected argument of type integer, but got %1", arg);
                    return null;
                }
            }
        }        
    };
    

    private Primitive set_output = new Primitive() {
        public String getName() { return "set-output"; }
        public SExpr call(SExpr args, Evaluator eval) {
            if (args.getLength() != 2) {
                eval.addError("set-output: expected number of arguments is 2, but got %1", args.getLength());
                return null;
            }
            else {
                SExpr arg1 = car(args);
                SExpr arg2 = car(cdr(args));
                if (arg1.isInteger()) {
                    int output = ((SInteger)arg1).getInt();
                    if (output >= 0 && output < getOutCount()) {
                        if (arg2.isDenotator()) {
                            setOutput(output, ((SDenotator)arg2).getDenotator());
                            return VOID;
                        }
                        else {
                            eval.addError("set-output: expected 2nd argument of type denotator, but got %1", arg2);
                            return null;
                        }
                    }
                    else {
                        eval.addError("set-output: expected argument >= 0 and < %2, but got %1", output, getOutCount());
                        return null;
                    }
                }
                else {
                    eval.addError("set-output: expected argument of type integer, but got %1", arg1);
                    return null;
                }
            }
        }        
    };

    
    private JPanel            properties = null;
    private JConnectorSliders sliders = null;
    private JTextArea         schemeCodeArea = null;
    private JLabel            infoLabel = null;
    
    private String      schemeCode = "";
    private Parser      parser = new Parser();
    private List<SExpr> sexprList = null;
    
    private static final ImageIcon icon;
    private static final Repository rep = Repository.systemRepository();

    static {
        icon = Icons.loadIcon(SchemeRubette.class, "schemeicon.png"); //$NON-NLS-1$
    }
}

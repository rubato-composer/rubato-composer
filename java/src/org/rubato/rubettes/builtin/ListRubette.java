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

package org.rubato.rubettes.builtin;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JConnectorSliders;
import org.rubato.composer.icons.Icons;
import org.rubato.logeo.DenoFactory;
import org.rubato.logeo.Lists;
import org.rubato.math.yoneda.*;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


public class ListRubette extends AbstractRubette {

    public ListRubette() {
        setInCount(2);
        setOutCount(1);
    }

    
    public void run(RunInfo runInfo) {
        Denotator res = null;
        switch (op) {
            case CONCAT: {
                res = doConcat(inputsToArray());
                break;
            }
            case APPEND: {
                res = doAppend();
                break;
            }
            case PREPEND: {
                res = doPrepend();
                break;
            }
            case SORT: {
                res = doSort();
                break;
            }
            case CONCATALL: {
                res = doConcatAll();
                break;
            }
        }
        setOutput(0, res);
    }

    
    private ListDenotator[] inputsToArray() {
        Form baseForm = null;
        LinkedList<ListDenotator> denoList = new LinkedList<ListDenotator>();
        for (int i = 0; i < getInCount(); i++) {
            ListDenotator d = toListDenotator(getInput(i), i);
            if (d != null) {
                if (baseForm == null) {
                    baseForm = d.getListForm().getForm();
                    denoList.add(d);
                }
                else {
                    if (!d.getListForm().getForm().equals(baseForm)) {
                        addError("Input list denotator %i has not the required base form.", i);
                    }
                    else {
                        denoList.add(d);
                    }
                }
            }
        }
        ListDenotator[] res = new ListDenotator[denoList.size()];
        int i = 0;
        for (ListDenotator d : denoList) {
            res[i++] = d;
        }
        return res;
    }
    
    
    private ListDenotator toListDenotator(Denotator d, int i) {
        if (d == null) {
            return null;
        }
        else if (d instanceof ListDenotator) {
            return (ListDenotator)d;
        }
        else {
            addError("Denotator #%1 is not of type list.", i);
            return null;
        }
    }
    
    
    private Denotator doConcat(ListDenotator[] denotators) {
        if (denotators.length == 0) {
            addError("There must be at least one valid input denotator.");
            return null;
        }
        ListDenotator res = null;
        try {
            res = Lists.concat(denotators);
        }
        catch (RubatoException e) {
            addError(e);
        }
        return res;
    }
    
    
    private Denotator doAppend() {
        ListDenotator res = null;
        ListDenotator ld = toListDenotator(getInput(0), 0);
        if (ld != null) {
            res = ld;
            try {
                for (int i = 1; i < getInCount(); i++) {
                    Denotator d = getInput(i);
                    res = Lists.appendElement(res, d);
                }
            }
            catch (RubatoException e) {
                addError(e);
                res = null;
            }
            
        }
        return res;
    }
    
    
    private Denotator doPrepend() {
        ListDenotator res = null;
        ListDenotator ld = toListDenotator(getInput(0), 0);
        if (ld != null) {
            res = ld;
            try {
                for (int i = 1; i < getInCount(); i++) {
                    Denotator d = getInput(i);
                    res = Lists.prependElement(res, d);
                }
            }
            catch (RubatoException e) {
                addError(e);
                res = null;
            }
            
        }
        return res;
    }
    
    
    private Denotator doSort() {
        ListDenotator res = null;
        ListDenotator ld = toListDenotator(getInput(0), 0);
        if (ld != null) {
            res = Lists.sort(ld);
        }
        return res;
    }
    
    
    private Denotator doConcatAll() {
        Denotator input = getInput(0);
        ListDenotator ld = toListDenotator(input, 0);
        Denotator res = null;
        if (ld != null) {
            Form f = ld.getListForm().getForm();
            if (f instanceof ListForm) {
                Form baseForm = f;
                List<Denotator> factors = ld.getFactors();
                List<Denotator> denoList = new LinkedList<Denotator>();
                for (Denotator factor : factors) {
                    ListDenotator d = (ListDenotator)factor;
                    denoList.addAll(d.getFactors());                    
                }
                res = DenoFactory.makeDenotator(baseForm, denoList);
            }
            else {
                addError("Base form is not of type list.");
            }
        }
        return res;
    }
    
    
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "List"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        ListRubette newRubette = new ListRubette();
        newRubette.op = op;
        newRubette.setInCount(getInCount());
        return newRubette;
    }
    
    
    public boolean hasInfo() {
        return true;
    }
    
    
    public String getInfo() {
        return opNames[op];
    }

    
    public boolean hasProperties() {
        return true;
    }


    private void configureSlider(int operation) {
        if (operation == CONCATALL || operation == SORT) {
            inSlider.setInLimits(1, 1);
        }
        else {
            inSlider.setInLimits(2, 8);
        }
    }

    
    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());

            inSlider = new JConnectorSliders(true, false);
            configureSlider(op);
            inSlider.setInValue(getInCount());
            properties.add(inSlider, BorderLayout.NORTH);
            
            JLabel opLabel = new JLabel("Operation"+": ");
            properties.add(opLabel, BorderLayout.WEST);
            
            opSelect = new JComboBox();
            opSelect.setEditable(false);
            for (int i = 0; i < opNames.length; i++) {
                opSelect.addItem(opNames[i]);
            }
            opSelect.setSelectedIndex(op);
            
            properties.add(opSelect, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {
        op = opSelect.getSelectedIndex();
        configureSlider(op);
        setInCount(inSlider.getInValue());
        return true;
    }


    public void revertProperties() {
        configureSlider(op);
        inSlider.setInValue(getInCount());
        opSelect.setSelectedIndex(op);
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getShortDescription() {
        return "Performs a list operation on its input denotators"; //$NON-NLS-1$
    }

    
    public String getLongDescription() {
        return "The List Rubette performs a list operation, e.g., "+ //$NON-NLS-1$
               "concatenation, on its input denotators."; //$NON-NLS-1$
    }


    public String getInTip(int i) {
        return "Input denotator #"+i; //$NON-NLS-1$
    }


    public String getOutTip(int i) {
        return "Output denotator"; //$NON-NLS-1$
    }

    
    private final static String INPUTS      = "Inputs"; //$NON-NLS-1$
    private final static String NUMBER_ATTR = "number"; //$NON-NLS-1$
    private final static String OPERATION   = "Operation"; //$NON-NLS-1$
    private final static String OP_ATTR     = "op"; //$NON-NLS-1$
    
    public void toXML(XMLWriter writer) {
        writer.empty(OPERATION, OP_ATTR, op);
        writer.empty(INPUTS, NUMBER_ATTR, getInCount());
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        // read operation type
        Element child = XMLReader.getChild(element, OPERATION);
        if (child == null) {
            return null;
        }
        int op0 = XMLReader.getIntAttribute(child, OP_ATTR, 0, opNames.length-1, 0);

        // read number of inputs
        child = XMLReader.getChild(element, INPUTS);
        if (child == null) {
            return null;
        }
        int n0 = XMLReader.getIntAttribute(child, NUMBER_ATTR, 1, 8, 2);
        
        ListRubette newRubette = new ListRubette();
        newRubette.op = op0;
        newRubette.setInCount(n0);
        return newRubette;
    }

    
    private JPanel            properties = null;
    private JComboBox         opSelect   = null;
    private JConnectorSliders inSlider   = null;
    
    private static final ImageIcon icon;
    
    private int op = CONCAT;
    
    private static final int CONCAT    = 0;
    private static final int APPEND    = 1;
    private static final int PREPEND   = 2;
    private static final int SORT      = 3;
    private static final int CONCATALL = 4;

    private static final String[] opNames = {
        "Concatenate",
        "Append elements",
        "Prepend elements",
        "Sort",
        "Concatenate All"
    };

    static {
       icon = Icons.loadIcon(ListRubette.class, "listicon.png"); //$NON-NLS-1$
    }
}

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

package org.rubato.rubettes.builtin.address;

import static org.rubato.composer.Utilities.getJDialog;
import static org.rubato.composer.Utilities.makeTitledBorder;
import static org.rubato.xml.XMLConstants.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.*;
import org.rubato.composer.icons.Icons;
import org.rubato.logeo.DenoFactory;
import org.rubato.math.arith.Complex;
import org.rubato.math.arith.Rational;
import org.rubato.math.module.*;
import org.rubato.math.module.morphism.MappingException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.yoneda.*;
import org.rubato.rubettes.builtin.address.JGraphSelect.QConfiguration;
import org.rubato.rubettes.builtin.address.JGraphSelect.RConfiguration;
import org.rubato.rubettes.builtin.address.JGraphSelect.ZConfiguration;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

/**
 * 
 * @author Gérard Milmeister
 */
public final class AddressEvalRubette
        extends AbstractRubette 
        implements ActionListener {

    public AddressEvalRubette() {
        setInCount(1);
        setOutCount(1);
    }
    

    public void run(RunInfo runInfo)  {
        Denotator input = getInput(0);
        Denotator res = null;
        if (input == null) {
            addError(INPUT_NULL_ERROR);
        }
        else {
            if (evalType == EVAL_TYPE_NULL) {
                res = input.atNull();
            }
            else if (evalType == EVAL_TYPE_ELEMENT) {
                res = runEvalTypeElement(input);
            }
            else if (evalType == EVAL_TYPE_LIST) {
                res = runEvalTypeList(input);
            }
            else if (evalType == EVAL_TYPE_CHANGE) {
                res = runEvalTypeChange(input);
            }
            else if (evalType == EVAL_TYPE_INPUT) {
                res = runEvalTypeInput(input);
            }
        }
        setOutput(0, res);
    }

    
    /**
     * Evaluates denotator <code>input</code> at the
     * configured module element.
     */
    private Denotator runEvalTypeElement(Denotator input) {
        Denotator res = null;
        if (moduleElement == null){
            // element not configured
            addError(ELEMENTNOTSET_ERROR);
        }
        else if (module == null) {
            // module not configured
            addError(MODULENOTSET_ERROR);
        }
        else if (!input.getAddress().equals(module)) {
            // address of the input denotator must be the same
            // as the configured module
            addError(ADDRESSMODULE_ERROR, input.getAddress(), module);
        }
        else {
            try {
                res = input.at(moduleElement);
            }
            catch (MappingException e) {
                addError(e);
            }
        }
        return res;
    }
    

    /**
     * Evaluates denotator <code>input</code> at the configured
     * list of elements. The result is a power or list denotator
     * whose form hat been configured before. The input denotator
     * must have the same form as the base form of the power
     * or list form.
     */
    private Denotator runEvalTypeList(Denotator input) {
        Denotator res = null;
        if (elements == null) {
            // not elements configured
            addError(LISTNOTSET_ERROR);
        }
        else if (module == null) {
            // module not configured
            addError(MODULENOTSET_ERROR);
        }
        else if (!input.getAddress().equals(module)) {
            // address of the input denotator must be the same
            // as the configured module
            addError(ADDRESSMODULE_ERROR, input.getAddress(), module);
        }
        else if (outputForm == null) {
            // output form must be configured
            addError(OUTPUTFORMNOTSET_ERROR);
        }
        else if (!input.hasForm(outputForm.getForm(0))) {
            // input form has not the form required by the
            // output power or list form
            addError(INPUT_WRONG_FORM, input.getForm(), outputForm.getForm(0));
        }
        else {
            try {
                LinkedList<Denotator> denoList = new LinkedList<Denotator>();
                for (ModuleElement e : elements) {
                    denoList.add(input.at(e));
                }
                if (outputForm instanceof PowerForm) {
                    res = new PowerDenotator(null, module.getNullModule(), (PowerForm)outputForm, denoList);
                }
                else if (outputForm instanceof ListForm) {
                    res = new ListDenotator(null, module.getNullModule(), (ListForm)outputForm, denoList);
                }
            }
            catch (MappingException e) {
                addError(e);
            }
            catch (RubatoException e) {
                addError(e);
            }
        }
        return res;
    }
    

    /**
     * Changes address of the input denotator using the configured
     * address changing morphism.
     */
    private Denotator runEvalTypeChange(Denotator input) {
        Denotator res = null;
        if (morphism == null) {
            addError(MORPHISMNOTSET_ERROR);
        }
        else if (!input.getAddress().equals(morphism.getCodomain())) {
            addError(ADDRESSMORPHISM_ERROR, input.getAddress(), morphism);
        }
        else {
            res = input.changeAddress(morphism);
        }
        return res;
    }

    
    /**
     * Evaluates the input denotator at the element(s) in
     * a second input denotator.
     */
    private Denotator runEvalTypeInput(Denotator input) {
        Denotator res = null;
        Denotator input2 = getInput(1);
        if (input2 == null) {
            addError(INPUT2NOTSET_ERROR);
        }
        else if (outputForm == null) {
            // if no output form is configured
            // 2nd input denotator must be of type simple
            if (input2 instanceof SimpleDenotator) {
                SimpleDenotator d = (SimpleDenotator)input2;
                ModuleElement el = d.getElement();
                try {
                    res = input.at(el);
                }
                catch (MappingException e) {
                    addError(INPUT2WRONGTYPE_ERROR);
                }
            }
            else {
                addError(INPUT2WRONGTYPE_ERROR);
            }
        }
        else if (outputForm != null) {
            // if an output form is configured
            // 2nd input denotator may be of type power or list
            // containing denotators of type simple
            if (input2 instanceof PowerDenotator ||
                input2 instanceof ListDenotator) {
                List<Denotator> list = null;
                list = ((FactorDenotator)input2).getFactors();
                if (list.size() == 0) {
                    res = DenoFactory.makeDenotator(outputForm, new Denotator[0]);
                }
                else {
                    if (list.get(0) instanceof SimpleDenotator) {
                        try {
                            LinkedList<Denotator> reslist = new LinkedList<Denotator>();
                            for (Denotator d : list) {
                                reslist.add(input.at(((SimpleDenotator)d).getElement()));
                            }
                            res = DenoFactory.makeDenotator(outputForm, reslist);
                        }
                        catch (MappingException e) {
                            addError(INPUT2WRONGTYPE_ERROR);
                        }
                    }
                    else {
                        addError(INPUT2WRONGTYPE_ERROR);
                    }
                }
            }
            else if (input2 instanceof SimpleDenotator) {
                res = input.changeAddress(((SimpleDenotator)input2).getModuleMorphism());
                if (res == null) { 
                    addError(INPUT2WRONGTYPE_ERROR);
                }
            }
            else {
                addError(INPUT2WRONGTYPE_ERROR);
            }
        }
        else {
            addError(INPUT2WRONGTYPE_ERROR);
        }
        return res;
    }
    
    
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "AddressEval"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        AddressEvalRubette rubette = new AddressEvalRubette();
        rubette.moduleElement = moduleElement;
        if (elements != null) {
            rubette.elements = new LinkedList<ModuleElement>(elements);
        }
        rubette.evalType = evalType;
        rubette.outputForm = outputForm;
        rubette.module = module;
        return rubette;
    }
    
    
    public boolean hasProperties() {
        return true;
    }
    
    
    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();
            properties.setLayout(new BorderLayout());
            
            Box box = Box.createVerticalBox();
            
            JPanel evalTypeSelectPanel = new JPanel();
            evalTypeSelectPanel.setLayout(new BorderLayout());
            evalTypeSelectPanel.setBorder(makeTitledBorder(Messages.getString("AddressEvalRubette.evaltype"))); //$NON-NLS-1$
            evalTypeSelect = new JComboBox(evalTypes);
            evalTypeSelect.setToolTipText(Messages.getString("AddressEvalRubette.evaltypetooltip")); //$NON-NLS-1$
            evalTypeSelect.setEditable(false);
            evalTypeSelect.setSelectedIndex(evalType);
            evalTypeSelect.addActionListener(this);
            evalTypeSelectPanel.add(evalTypeSelect, BorderLayout.CENTER);
            box.add(evalTypeSelectPanel);
            
            properties.add(box, BorderLayout.NORTH);
            
            addressPanel = new JPanel();
            addressPanel.setLayout(new BorderLayout());                        
            layoutAddressPanel(evalType);
            
            properties.add(addressPanel, BorderLayout.CENTER);
            
            statusline = new JStatusline();
            statusline.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 2));
            
            properties.add(statusline, BorderLayout.SOUTH);
            
        }
        return properties;
    }

    
    public boolean applyProperties() {
        statusline.clear();
        int t = evalTypeSelect.getSelectedIndex();
        if (t == EVAL_TYPE_ELEMENT) {
            Module mod = elementEntry.getModule();
            ModuleElement el = elementEntry.getModuleElement();
            if (mod == null) {
                statusline.setError(NOMODULE_ERROR);
                return false;
            }
            if (el == null) {
                statusline.setError(NOELEMENT_ERROR);
                return false;
            }
            evalType   = t;
            outputForm = null;
            module     = mod;
            moduleElement    = el;
        }
        else if (t == EVAL_TYPE_LIST) {
            Form oform = outputFormSelect.getForm();
            Module mod = listModuleEntry.getModule();
            List<ModuleElement> list = elementList.getElements();
            if (oform == null) {
                statusline.setError(NOOUTPUTFORM_ERROR);
                return false;
            }
            if (mod == null) {
                statusline.setError(NOMODULE_ERROR);
                return false;
            }
            if (list == null) {
                statusline.setError(NOELEMENTS_ERROR);
                return false;
            }
            evalType   = t;
            outputForm = oform;
            module     = mod;
            elements   = list;
        }
        else if (t == EVAL_TYPE_CHANGE) {
            ModuleMorphism m = morphismEntry.getMorphism();
            if (m == null) {
                statusline.setError(NOMORPHISM_ERROR);
                return false;
            }
            evalType   = t;
            outputForm = null;
            morphism   = m;
            elements   = null;
        }
        else if (t == EVAL_TYPE_INPUT) {
            evalType   = t;
            outputForm = null;
            morphism   = null;
            elements   = null;
            if (listButton.isSelected()) {
                Form oform = outputFormSelect.getForm();
                if (oform == null) {
                    statusline.setError(NOOUTPUTFORM_ERROR);
                    return false;
                }
                outputForm = oform;
            }
            setInCount(2);
        }
        else {            
            evalType   = EVAL_TYPE_NULL;
            outputForm = null;
            module     = null;
            moduleElement    = null;
        }
        return true;
    }

    
    public void revertProperties() {
        evalTypeSelect.setSelectedIndex(evalType);
        layoutAddressPanel(evalType);
        getJDialog(properties).pack();
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == evalTypeSelect) {
            layoutAddressPanel(evalTypeSelect.getSelectedIndex());
            getJDialog(properties).pack();
        }
        else if (src == listModuleEntry) {
            module = listModuleEntry.getModule();
            layoutAddressPanel(evalTypeSelect.getSelectedIndex());
            getJDialog(properties).pack();
        }
        else if (src == basisButton) {
            if (module instanceof FreeModule) {
                FreeModule m = (FreeModule)module;
                elementList.addElement(m.getZero());
                for (int i = 0; i < m.getDimension(); i++) {
                    elementList.addElement(m.getUnitElement(i));                    
                }
            }
        }
        else if (src == listButton || src == simpleButton) {
            if (simpleButton.isSelected()) {
                outputFormSelect.setEnabled(false);
            }
            else {
                outputFormSelect.setEnabled(true);
            }
        }
        else if (src == graphButton) {
            showGraphDialog();
        }
    }
    
    
    public ImageIcon getIcon() {
        return icon;
    }
    
    
    public boolean hasInfo() {
        return true;
    }
    
    
    public String getInfo() {
        return evalTypes[evalType];
    }
    
    
    public String getShortDescription() {
        return "Evaluates input denotator at a given address"; //$NON-NLS-1$
    }
    
    
    public String getLongDescription() {
        return "The AddressEval Rubette evaluates the input "+ //$NON-NLS-1$
               "denotator at one or more addresses specified "+ //$NON-NLS-1$
               "in the properties."; //$NON-NLS-1$
    }

    
    public String getInTip(int i) {
        if (i == 0) {
           if (outputForm == null) {
               return Messages.getString("AddressEvalRubette.inputdeno"); //$NON-NLS-1$
           }
           else {
               return TextUtils.replaceStrings(Messages.getString("AddressEvalRubette.inputdenotator"), outputForm.getForm(0).getNameString()); //$NON-NLS-1$
           }
        }
        else {
            return Messages.getString("AddressEvalRubette.evaldenotator"); //$NON-NLS-1$
        }
    }
    
    
    public String getOutTip(int i) {
        String name = Messages.getString("AddressEvalRubette.outputdeno"); //$NON-NLS-1$
        if (outputForm != null) {
            name = TextUtils.replaceStrings(Messages.getString("AddressEvalRubette.outputdenotator"), outputForm.getNameString()); //$NON-NLS-1$
        }
        return name;
    }
    
    
    private final static String EVALTYPE = "EvalType"; //$NON-NLS-1$
    
    public void toXML(XMLWriter writer) {
        writer.empty(EVALTYPE, VALUE_ATTR, evalType);
        if (evalType == EVAL_TYPE_ELEMENT) {
            moduleElement.toXML(writer);
        }
        else if (evalType == EVAL_TYPE_LIST) {
            if (outputForm != null) {
                writer.writeFormRef(outputForm);
                module.toXML(writer);
                for (ModuleElement el : elements) {
                    el.toXML(writer);
                }
            }
        }
        else if (evalType == EVAL_TYPE_CHANGE) {
            morphism.toXML(writer);
        }
        else if (evalType == EVAL_TYPE_INPUT) {
            if (outputForm != null) {
                writer.writeFormRef(outputForm);
            }
        }
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        int t = 0;
        AddressEvalRubette newRubette = null;
        
        Element child = XMLReader.getChild(element, EVALTYPE);

        if (child == null) {
            // there must be a type
            reader.setError(Messages.getString("AddressEvalRubette.missingelement"), EVALTYPE); //$NON-NLS-1$
            return null;
        }
        
        t = XMLReader.getIntAttribute(child, VALUE_ATTR, 0, evalTypes.length-1, 0);

        if (t == EVAL_TYPE_ELEMENT) {
            // type evaluate at element
            child = XMLReader.getNextSibling(child, MODULEELEMENT);
            if (child != null) {
                ModuleElement mel = reader.parseModuleElement(child);
                if (mel != null) {
                    newRubette = new AddressEvalRubette();
                    newRubette.evalType = t;
                    newRubette.moduleElement  = mel;
                    newRubette.module   = mel.getModule();
                }                
            }
            else {
                reader.setError(Messages.getString("AddressEvalRubette.missingelement"), MODULEELEMENT); //$NON-NLS-1$
            }
        }
        else if (t == EVAL_TYPE_LIST) {
            // type evaluate at list of elements
            child = XMLReader.getNextSibling(child, FORM);
            if (child == null) {
                // no output form has been given
                // there must be an output form
                reader.setError(Messages.getString("AddressEvalRubette.missingelement"), FORM); //$NON-NLS-1$
                return null;
            }
            // get output form
            Form oform = reader.parseAndResolveForm(child);
            if (oform == null) {
                return null;
            }
            child = XMLReader.getNextSibling(child, MODULE);
            if (child == null) {
                // no module has been given
                // there must be a module
                reader.setError(Messages.getString("AddressEvalRubette.missingelement"), MODULE); //$NON-NLS-1$
                return null;
            }
            Module module0 = reader.parseModule(child);
            if (module0 == null) {
                return null;
            }
            LinkedList<ModuleElement> list = new LinkedList<ModuleElement>();
            child = XMLReader.getNextSibling(child, MODULEELEMENT);
            while (child != null) {
                ModuleElement e = reader.parseModuleElement(child);
                if (e == null) {
                    return null;
                }
                if (!module0.hasElement(e)) {
                    reader.setError(Messages.getString("AddressEvalRubette.wrongmodule"), e.getModule(), module0); //$NON-NLS-1$
                    return null;
                }
                list.add(e);
                child = XMLReader.getNextSibling(child, MODULEELEMENT);
            }
            newRubette = new AddressEvalRubette();
            newRubette.evalType = t;
            newRubette.outputForm = oform;
            newRubette.elements = list;
            newRubette.module = module0;
        }
        else if (t == EVAL_TYPE_CHANGE) {
            // type change address
            child = XMLReader.getNextSibling(child, MODULEMORPHISM);
            if (child == null) {
                // no module morphism has been given
                // there must be a module morphism
                reader.setError(Messages.getString("AddressEvalRubette.missingelement"), MODULEMORPHISM); //$NON-NLS-1$
                return null;
            }
            ModuleMorphism morphism0 = reader.parseModuleMorphism(child);
            if (morphism0 == null) {
                return null;
            }
            newRubette = new AddressEvalRubette();
            newRubette.evalType = t;
            newRubette.morphism = morphism0;
        }
        else if (t == EVAL_TYPE_INPUT) {
            // get output form if any
            Form oform = null;
            child = XMLReader.getNextSibling(child, FORM);
            if (child != null) {
                oform = reader.parseAndResolveForm(child);
                if (oform == null) {
                    return null;
                }
            }
            newRubette = new AddressEvalRubette();
            newRubette.evalType = t;
            newRubette.outputForm = oform;
            newRubette.setInCount(2);
        }
        else {
            newRubette = new AddressEvalRubette();
            newRubette.evalType = 0;
        }
        
        return newRubette;
    }

    
    private void layoutAddressPanel(int type) {
        addressPanel.removeAll();
        if (type == EVAL_TYPE_ELEMENT) {
            Module m = (module == null)?ZRing.ring:module;
            elementEntry = new JModuleElementEntry(m);
            elementEntry.setBorder(makeTitledBorder(Messages.getString("AddressEvalRubette.moduleelement"))); //$NON-NLS-1$
            elementEntry.setToolTipText(Messages.getString("AddressEvalRubette.selectelement")); //$NON-NLS-1$
            if (moduleElement != null) {
                elementEntry.setModuleElement(moduleElement);
            }
            addressPanel.add(elementEntry, BorderLayout.CENTER);
        }
        else if (type == EVAL_TYPE_LIST) {
            Box box = Box.createVerticalBox();
            outputFormSelect = new JSelectForm(Repository.systemRepository(), Form.POWER, Form.LIST);
            outputFormSelect.setBorder(makeTitledBorder(Messages.getString("AddressEvalRubette.outputform"))); //$NON-NLS-1$
            outputFormSelect.setToolTipText(Messages.getString("AddressEvalRubette.setoutputform")); //$NON-NLS-1$
            if (outputForm != null) {
                outputFormSelect.setForm(outputForm);
            }
            box.add(outputFormSelect);
            
            Module m = (module == null)?ZRing.ring:module;
            
            listModuleEntry = new JModuleEntry();
            listModuleEntry.setBorder(makeTitledBorder(Messages.getString("AddressEvalRubette.elementmodule"))); //$NON-NLS-1$
            listModuleEntry.setModule(m);
            listModuleEntry.addActionListener(this);
            box.add(listModuleEntry);
            
            elementList = new JModuleElementList(m);
            elementList.setBorder(makeTitledBorder(Messages.getString("AddressEvalRubette.elementlist"))); //$NON-NLS-1$
            elementList.setToolTipText(Messages.getString("AddressEvalRubette.elementlisttooltip")); //$NON-NLS-1$
            if (elements != null) {
                elementList.addElements(elements);
            }
            box.add(elementList, BorderLayout.CENTER);
            addressPanel.add(box, BorderLayout.CENTER);
            
            box = Box.createHorizontalBox();
            box.add(Box.createHorizontalGlue());
            basisButton = new JButton(Messages.getString("AddressEvalRubette.basisvectors")); //$NON-NLS-1$
            basisButton.setToolTipText(Messages.getString("AddressEvalRubette.basistooltip")); //$NON-NLS-1$
            basisButton.addActionListener(this);
            basisButton.setEnabled(module instanceof FreeModule);                
            box.add(basisButton);
            box.add(Box.createHorizontalStrut(5));
            graphButton = new JButton(Messages.getString("AddressEvalRubette.graphical")); //$NON-NLS-1$
            graphButton.setToolTipText(Messages.getString("AddressEvalRubette.graphtooltip")); //$NON-NLS-1$
            graphButton.addActionListener(this);
            box.add(graphButton);
            box.add(Box.createHorizontalGlue());
            graphButton.setEnabled(isGraphical(module));
            
            addressPanel.add(box, BorderLayout.SOUTH);
        }
        else if (type == EVAL_TYPE_CHANGE) {
            morphismEntry = new JMorphismEntry(null, null);
            if (morphism != null) {
                morphismEntry.setMorphism(morphism);
            }
            morphismEntry.setBorder(makeTitledBorder(Messages.getString("AddressEvalRubette.changemorph"))); //$NON-NLS-1$
            morphismEntry.setToolTipText(Messages.getString("AddressEvalRubette.changemorphtooltip")); //$NON-NLS-1$
            addressPanel.add(morphismEntry, BorderLayout.CENTER);
        }
        else if (type == EVAL_TYPE_INPUT) {
            Box box = Box.createVerticalBox();
            JPanel typePanel = new JPanel();
            typePanel.setBorder(makeTitledBorder(Messages.getString("AddressEvalRubette.resulttype"))); //$NON-NLS-1$
            ButtonGroup group = new ButtonGroup();
            simpleButton = new JRadioButton(SIMPLE);
            group.add(simpleButton);
            listButton = new JRadioButton(LISTORPOWER);
            group.add(listButton);
            simpleButton.setSelected(outputForm == null);
            listButton.setSelected(outputForm != null);
            simpleButton.addActionListener(this);
            listButton.addActionListener(this);
            typePanel.add(simpleButton);
            typePanel.add(listButton);
            box.add(typePanel);            
            
            outputFormSelect = new JSelectForm(Repository.systemRepository(), Form.POWER, Form.LIST);
            outputFormSelect.setBorder(makeTitledBorder(Messages.getString("AddressEvalRubette.outputform"))); //$NON-NLS-1$
            outputFormSelect.setToolTipText(Messages.getString("AddressEvalRubette.setoutputform")); //$NON-NLS-1$
            if (outputForm != null) {
                outputFormSelect.setForm(outputForm);
            }
            box.add(outputFormSelect);
            
            addressPanel.add(box);
        }
    }

    
    private boolean isGraphical(Module module) {
        if (module == CRing.ring) {
            return true;
        }
        else if (module instanceof FreeModule) {
            if (((FreeModule)module).getDimension() != 2) {
                return false;
            }
            else {
                return (module instanceof RFreeModule ||
                        module instanceof QFreeModule ||
                        module instanceof ZFreeModule ||
                        module instanceof ZnFreeModule ||
                        module instanceof CFreeModule);
            }            
        }
        else {
            return false;
        }
    }
    
    private void showGraphDialog() {
        if (module instanceof RProperFreeModule) {
            RProperFreeModule m = (RProperFreeModule)module;
            if (m.getDimension() == 2) {
                JGraphSelect select = JGraphSelectDialog.showDialog(graphButton, RRing.ring, elementList.getElements());
                if (select != null) {
                    elementList.clear();
                    RConfiguration config = (RConfiguration)select.getConfiguration();
                    for (int i = 0; i < config.getSize(); i++) {
                        double[] p = new double[] { config.px.get(i), config.py.get(i) };
                        elementList.addElement(RProperFreeElement.make(p));
                    }
                }
            }
        }
        else if (module instanceof QProperFreeModule) {
            QProperFreeModule m = (QProperFreeModule)module;
            if (m.getDimension() == 2) {
                JGraphSelect select = JGraphSelectDialog.showDialog(graphButton, QRing.ring, elementList.getElements());
                if (select != null) {
                    elementList.clear();
                    QConfiguration config = (QConfiguration)select.getConfiguration();
                    for (int i = 0; i < config.getSize(); i++) {
                        Rational[] p = new Rational[] { config.qpx.get(i), config.qpy.get(i) }; 
                        elementList.addElement(QProperFreeElement.make(p));
                    }
                }
            }
        }
        else if (module instanceof ZProperFreeModule) {
            ZProperFreeModule m = (ZProperFreeModule)module;
            if (m.getDimension() == 2) {
                JGraphSelect select = JGraphSelectDialog.showDialog(graphButton, ZRing.ring, elementList.getElements());
                if (select != null) {
                    elementList.clear();
                    ZConfiguration config = (ZConfiguration)select.getConfiguration();
                    for (int i = 0; i < config.getSize(); i++) {
                        int[] p = new int[] { config.ipx.get(i), config.ipy.get(i) }; 
                        elementList.addElement(ZProperFreeElement.make(p));
                    }
                }
            }
        }
        else if (module instanceof ZnProperFreeModule) {
            ZnProperFreeModule m = (ZnProperFreeModule)module;
            if (m.getDimension() == 2) {
                JGraphSelect select = JGraphSelectDialog.showDialog(graphButton, m.getRing(), elementList.getElements());
                elementList.clear();
                if (select != null) {
                    ZConfiguration config = (ZConfiguration)select.getConfiguration();
                    for (int i = 0; i < config.getSize(); i++) {
                        int[] p = new int[] { config.ipx.get(i), config.ipy.get(i) }; 
                        elementList.addElement(ZnProperFreeElement.make(p, m.getModulus()));
                    }
                }
            }
        }
        else if (module instanceof CRing) {
            LinkedList<ModuleElement> elements0 = new LinkedList<ModuleElement>();
            for (ModuleElement m : elementList.getElements()) {
                Complex c = ((CElement)m).getValue();
                elements0.add(RProperFreeElement.make(new double[] { c.getReal(), c.getImag() }));
            }
            JGraphSelect select = JGraphSelectDialog.showDialog(graphButton, RRing.ring, elements0);
            if (select != null) {
                elementList.clear();
                RConfiguration config = (RConfiguration)select.getConfiguration();
                for (int i = 0; i < config.getSize(); i++) {
                    double[] p = new double[] { config.px.get(i), config.py.get(i) }; 
                    elementList.addElement(new CElement(p[0], p[1]));
                }
            }
        }
    }

    
    private JPanel      properties       = null;
    private JPanel      addressPanel     = null;
    private JComboBox   evalTypeSelect   = null;
    private JSelectForm outputFormSelect = null;
    private JStatusline statusline       = null;
    
    private JModuleElementEntry elementEntry    = null;
    private JModuleElementList  elementList     = null;
    private JModuleEntry        listModuleEntry = null;
    private JMorphismEntry      morphismEntry   = null;
    private JButton             basisButton     = null;
    private JButton             graphButton     = null;
    private JRadioButton        simpleButton    = null;
    private JRadioButton        listButton      = null;
    
    private Form          outputForm    = null;
    private Module        module        = null;
    private ModuleElement moduleElement = null;
    private int           evalType      = EVAL_TYPE_NULL;
    
    private List<ModuleElement> elements = null;
    private ModuleMorphism      morphism = null; 
    
    private final static int EVAL_TYPE_NULL    = 0;
    private final static int EVAL_TYPE_ELEMENT = 1;
    private final static int EVAL_TYPE_LIST    = 2;
    private final static int EVAL_TYPE_CHANGE  = 3;
    private final static int EVAL_TYPE_INPUT   = 4;
    
    private final static String[] evalTypes = {
        Messages.getString("AddressEvalRubette.evalnull"), //$NON-NLS-1$
        Messages.getString("AddressEvalRubette.evalelement"), //$NON-NLS-1$
        Messages.getString("AddressEvalRubette.evalllist"), //$NON-NLS-1$
        Messages.getString("AddressEvalRubette.changeaddress"), //$NON-NLS-1$
        Messages.getString("AddressEvalRubette.evalinput") //$NON-NLS-1$
    };
    
    // Message strings
    private final static String INPUT_NULL_ERROR    = Messages.getString("AddressEvalRubette.inputnullerror"); //$NON-NLS-1$ 
    private final static String INPUT_WRONG_FORM    = Messages.getString("AddressEvalRubette.inputwrongform"); //$NON-NLS-1$ 
    private final static String ELEMENTNOTSET_ERROR = Messages.getString("AddressEvalRubette.elementnotset"); //$NON-NLS-1$
    private final static String MODULENOTSET_ERROR  = Messages.getString("AddressEvalRubette.modulenotset"); //$NON-NLS-1$
    private final static String ADDRESSMODULE_ERROR = Messages.getString("AddressEvalRubette.addressmoduleerror"); //$NON-NLS-1$    
    private final static String LISTNOTSET_ERROR    = Messages.getString("AddressEvalRubette.listnotset"); //$NON-NLS-1$
    private final static String OUTPUTFORMNOTSET_ERROR = Messages.getString("AddressEvalRubette.outputformnotset"); //$NON-NLS-1$
    private final static String MORPHISMNOTSET_ERROR = Messages.getString("AddressEvalRubette.morphismnotset"); //$NON-NLS-1$
    private final static String ADDRESSMORPHISM_ERROR = Messages.getString("AddressEvalRubette.addressmorphismerror"); //$NON-NLS-1$
    private final static String NOMODULE_ERROR      = Messages.getString("AddressEvalRubette.modnotset"); //$NON-NLS-1$
    private final static String NOELEMENT_ERROR     = Messages.getString("AddressEvalRubette.modelnotset"); //$NON-NLS-1$
    private final static String NOOUTPUTFORM_ERROR  = Messages.getString("AddressEvalRubette.oformnotset"); //$NON-NLS-1$
    private final static String NOELEMENTS_ERROR    = Messages.getString("AddressEvalRubette.noellist"); //$NON-NLS-1$
    private final static String NOMORPHISM_ERROR    = Messages.getString("AddressEvalRubette.modmorphnotset"); //$NON-NLS-1$
    private final static String INPUT2WRONGTYPE_ERROR = Messages.getString("AddressEvalRubette.secinputwrongtype"); //$NON-NLS-1$
    private final static String INPUT2NOTSET_ERROR  = Messages.getString("AddressEvalRubette.secinputnull"); //$NON-NLS-1$
    private final static String SIMPLE              = Messages.getString("AddressEvalRubette.simple"); //$NON-NLS-1$
    private final static String LISTORPOWER         = Messages.getString("AddressEvalRubette.listorpower"); //$NON-NLS-1$

    private final static ImageIcon icon;

    static {
        icon = Icons.loadIcon(AddressEvalRubette.class, "addressicon.png"); //$NON-NLS-1$
    }
}
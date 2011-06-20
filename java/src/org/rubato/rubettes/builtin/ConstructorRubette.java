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

import static org.rubato.xml.XMLConstants.FORM;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JSelectForm;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.*;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


/**
 * The Constructor Rubette creates a denotator
 * of the form specified in the properties
 * with coordinates at the input connectors.
 * 
 * @author Gérard Milmeister
 */
public class ConstructorRubette extends AbstractRubette {       
    
    public ConstructorRubette() {
        setInCount(0);
        setOutCount(1);
    }
    
    
    public void run(RunInfo runInfo) {
        if (form == null) {
            addError(Messages.getString("ConstructorRubette.noform")); //$NON-NLS-1$
            return;            
        }
        switch (form.getType()) {
            case Form.LIMIT: {
                createLimit();
                break;
            }
            case Form.COLIMIT: {
                createColimit();
                break;
            }
            case Form.LIST: {
                createList();
                break;
            }
            case Form.POWER: {
                createPower();
                break;
            }
            default: {
                addError(Messages.getString("ConstructorRubette.formwrongtype")); //$NON-NLS-1$
            }
        }
    }

    
    private void createLimit() {
        LinkedList<Denotator> cds = new LinkedList<Denotator>();
        for (int i = 0; i < getInCount(); i++) {
            Denotator in = getInput(i);
            if (in == null) {
                addError("Input denotator #"+i+" must not be null.");
                return;
            }
            else {
                cds.add(in);
            }
        }
        Denotator out = null;
        try {
            out = new LimitDenotator(null, (LimitForm)form, cds);
        }
        catch (Exception e) {
            addError(e);
            return;
        }
        setOutput(0, out);
    }

    
    private void createColimit() {
        boolean hasInput = false;
        int index = 0;
        Denotator inputDenotator = null;
        for (int i = 0; i < getInCount(); i++) {
            Denotator d = getInput(i);
            if (d != null) {
                if (hasInput) {
                    addError(Messages.getString("ConstructorRubette.onlyonenonnullinput")); //$NON-NLS-1$
                    return;
                }
                inputDenotator = d;
                index = i;
                hasInput = true;
            }
        }
        if (!hasInput) {
            addError(Messages.getString("ConstructorRubette.atleastonenonnullinput")); //$NON-NLS-1$
            return;
        }
        Denotator out = null;
        try {
            out = new ColimitDenotator(null, (ColimitForm)form, index, inputDenotator);
        }
        catch (Exception e) {
            addError(e);
            return;
        }
        setOutput(0, out);
    }

    
    private void createList() {
        LinkedList<Denotator> cds = new LinkedList<Denotator>();
        for (int i = 0; i < getInCount(); i++) {
            Denotator d = getInput(i);
            if (d != null) {
                cds.add(d);
            }
        }
        if (cds.size() == 0) {
            addError(Messages.getString("ConstructorRubette.atleastonenonnullinput")); //$NON-NLS-1$
            return;
        }
        Denotator out = null;
        try {
            out = new ListDenotator(null, (ListForm)form, cds);
        }
        catch (Exception e) {
            addError(e);
            return;
        }
        setOutput(0, out);
    }


    private void createPower() {
        LinkedList<Denotator> cds = new LinkedList<Denotator>();
        for (int i = 0; i < getInCount(); i++) {
            Denotator d = getInput(i);
            if (d != null) {
                cds.add(d);
            }
        }
        if (cds.size() == 0) {
            addError(Messages.getString("ConstructorRubette.atleastonenonnullinput")); //$NON-NLS-1$
            return;
        }
        Denotator out = null;
        try {
            out = new PowerDenotator(null, (PowerForm)form, cds);
        }
        catch (Exception e) {
            addError(e);
            return;
        }
        setOutput(0, out);
    }


    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "Constructor"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        ConstructorRubette newRubette = new ConstructorRubette();
        newRubette.setForm(getForm());
        return newRubette;
    }

    
    public boolean hasInfo() {
        return true;
    }
    
    
    public String getInfo() {
        return formName;
    }
    
    
    public boolean hasProperties() {
        return true;
    }


    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            ArrayList<Integer> types = new ArrayList<Integer>();
            types.add(Form.LIMIT);
            types.add(Form.COLIMIT);
            types.add(Form.POWER);
            types.add(Form.LIST);
            formSelector = new JSelectForm(Repository.systemRepository(), types);
            if (form != null) {
                formSelector.setForm(form);
            }
            properties.add(formSelector, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {
        return setForm(formSelector.getForm());
    }


    public void revertProperties() {
        formSelector.setForm(form);
    }

    
    public String getShortDescription() {
        return "Creates a denotator of a given form";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The Constructor Rubette creates a denotator"+
               " of the form specified in the properties"+
               " with coordinates at the input connectors.";
    }


    public String getInTip(int i) {
        return (inTip == null)?"Input denotator #"+i:inTip[i]; //$NON-NLS-1$
    }


    public String getOutTip(int i) {
        return "Output denotator"; //$NON-NLS-1$
    }


    public boolean setForm(Form form) {
        if (form != null) {
            this.form = form;
            formName = form.getNameString()+": "+form.getTypeString(); //$NON-NLS-1$
            switch (form.getType()) {
                case Form.LIST:
                case Form.POWER: {
                    int formCount = 8;
                    Form factorForm = form.getForm(0);
                    setInCount(formCount);
                    inTip = new String[formCount];
                    for (int i = 0; i < formCount; i++) {
                        inTip[i] = factorForm.getNameString()+": "+factorForm.getTypeString();  //$NON-NLS-1$
                    }
                    break;
                }
                case Form.LIMIT:
                case Form.COLIMIT: {
                    int formCount = form.getFormCount();
                    setInCount(formCount);
                    inTip = new String[formCount];
                    for (int i = 0; i < formCount; i++) {
                        Form factorForm = form.getForm(i);
                        inTip[i] = factorForm.getNameString()+": "+factorForm.getTypeString(); //$NON-NLS-1$
                    }
                    break;
                }
                default: {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    
    public Form getForm() {
        return form;        
    }
    
    
    public void toXML(XMLWriter writer) {
        if (form != null) {
            writer.writeFormRef(form);
        }
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, FORM);
        Form f = reader.parseAndResolveForm(child);
        ConstructorRubette newRubette = new ConstructorRubette();
        newRubette.setForm(f);
        return newRubette;
    }

    
    private JPanel      properties = null;
    private JSelectForm formSelector = null;
    private Form        form = null;
    private String      formName = " "; //$NON-NLS-1$
    private String[]    inTip = null;
    private static final ImageIcon icon;

    static {
        icon = Icons.loadIcon(ConstructorRubette.class, "constricon.png"); //$NON-NLS-1$
    }
}

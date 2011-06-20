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

import static org.rubato.composer.Utilities.makeTitledBorder;
import static org.rubato.xml.XMLConstants.FORM;
import static org.rubato.xml.XMLConstants.VALUE_ATTR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JSelectForm;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.*;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


public class SplitRubette
        extends AbstractRubette
        implements ActionListener {       
    
    public SplitRubette() {
        setInCount(1);
        setOutCount(0);
    }

    
    public void run(RunInfo runInfo) {
        Denotator input = getInput(0);
        if (input == null) {
            addError(Messages.getString("SplitRubette.inputnull")); //$NON-NLS-1$
            return;
        }
        
        if (form == null) {
            addError(Messages.getString("SplitRubette.noform")); //$NON-NLS-1$
            return;
        }        
        
        if (hasErrors()) {
            return;
        }
        
        if (!input.getForm().equals(form)) {
            addError(Messages.getString("SplitRubette.wrongform")); //$NON-NLS-1$
            return;
        }
    
        LimitDenotator ld = (LimitDenotator)input;
        for (int i = 0; i < getOutCount(); i++) {
            setOutput(i, ld.getFactor(selectedForms[i]));
        }
    }

    
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "Split"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        SplitRubette newRubette = new SplitRubette();
        newRubette.set((LimitForm)getForm(), selectedForms);
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
            
            formSelector = new JSelectForm(Repository.systemRepository(), new int[] { Form.LIMIT });
            formSelector.addActionListener(this);
            properties.add(formSelector, BorderLayout.NORTH);

            formListModel = new FormListModel();
            formList = new JList(formListModel);
            JScrollPane scrollPane = new JScrollPane(formList);
            scrollPane.setBorder(makeTitledBorder("Output Forms"));
            
            properties.add(scrollPane, BorderLayout.CENTER);
            
            if (form != null) {
                formSelector.setForm(form);
                formListModel.setForm(form);
                if (selectedForms != null) {
                    formList.setSelectedIndices(selectedForms);
                }
            }
        }
        return properties;
    }


    public boolean applyProperties() {
        return set((LimitForm)formSelector.getForm(), formList.getSelectedIndices());
    }


    public void revertProperties() {
        formSelector.setForm(form);
        formListModel.setForm(form);
        if (selectedForms != null) {
            formList.setSelectedIndices(selectedForms);
        }
    }

    
    public String getShortDescription() {
        return "Splits the input denotator into its factors";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The Split Rubette decomposes"+
               " its input denotator of type limit into"+
               " its factors.";
    }


    public String getInTip(int i) {
        return "Input denotator"; //$NON-NLS-1$
    }


    public String getOutTip(int i) {
        if (outTip != null) {
            return outTip[i];
        }
        else {
            return "Output denotator"; //$NON-NLS-1$
        }
    }


    public boolean set(LimitForm limitForm, int[] selected) {
        if (limitForm != null) {
            form = limitForm;
            formName = form.getNameString()+": "+form.getTypeString(); //$NON-NLS-1$

            if (selected != null && selected.length <= 8) {
                selectedForms = selected;
                setOutCount(selectedForms.length);
                outTip = new String[selectedForms.length];
                for (int i = 0; i < selectedForms.length; i++) {
                    Form f = form.getForm(selectedForms[i]);
                    outTip[i] = f.getNameString()+": "+f.getTypeString();
                }
            }
            else {
                setOutCount(0);
                selectedForms = null;
                outTip = null;
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
        
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == formSelector) {
            formListModel.setForm((LimitForm)formSelector.getForm());
        }
    }
    
    
    private final static String SELECTED = "Selected"; //$NON-NLS-1$
    
    public void toXML(XMLWriter writer) {
        if (form != null) {
            writer.writeFormRef(form);
            if (selectedForms != null) {
                for (int i = 0; i < selectedForms.length; i++) {
                    writer.empty(SELECTED, VALUE_ATTR, selectedForms[i]);
                }
            }
        }
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, FORM);
        Form f = reader.parseAndResolveForm(child);
        SplitRubette newRubette = new SplitRubette();
        if (f instanceof LimitForm) {
            LimitForm limitForm = (LimitForm)f;
            int formCount = limitForm.getFormCount();
            ArrayList<Integer> selected = new ArrayList<Integer>();
            child = XMLReader.getNextSibling(child, SELECTED);
            while (child != null) {
                int i = XMLReader.getIntAttribute(child, VALUE_ATTR, 0, Integer.MAX_VALUE, 0);
                if (i < formCount && !selected.contains(i)) {
                    selected.add(i);
                }
                child = XMLReader.getNextSibling(child, SELECTED);
            }
            Collections.sort(selected);
            int[] sel = new int[selected.size()];
            for (int i = 0; i < sel.length; i++) {
                sel[i] = selected.get(i);
            }
            newRubette.set(limitForm, sel);
        }
        return newRubette;
    }

    
    private JPanel        properties = null;
    private JSelectForm   formSelector = null;
    private JList         formList = null;
    private FormListModel formListModel = null;
    private int[]         selectedForms = null;
    private LimitForm     form = null;
    private String        formName = " "; //$NON-NLS-1$
    private String[]      outTip;
    private static final  ImageIcon icon;

    protected class FormListModel extends DefaultListModel {

        @SuppressWarnings("nls")
        public void setForm(LimitForm limitForm) {
            removeAllElements();            
            forms = null;
            if (limitForm != null) {
                String label;
                forms = new Form[limitForm.getFormCount()];
                for (int i = 0; i < limitForm.getFormCount(); i++) {
                    forms[i] = limitForm.getForm(i);
                    label = "";
                    if (limitForm.hasLabels()) {
                        label = "["+limitForm.indexToLabel(i)+"] ";
                    }
                    addElement(label+forms[i].getNameString()+": "+forms[i].getTypeString());
                }
            }
        }
        
        public Form getForm(int i) {
            return forms[i];
        }
        
        private Form[]    forms = null;
    }
    
    static {
        icon = Icons.loadIcon(SplitRubette.class, "spliticon.png"); //$NON-NLS-1$
    }
}

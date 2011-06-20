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

package org.rubato.rubettes.select2d;

import static org.rubato.xml.XMLConstants.FORM;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.rubato.base.AbstractRubette;
import org.rubato.base.RubatoConstants;
import org.rubato.base.Rubette;
import org.rubato.composer.RunInfo;
import org.rubato.composer.icons.Icons;
import org.rubato.logeo.DenoFactory;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.FactorDenotator;
import org.rubato.math.yoneda.Form;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class Select2DRubette extends AbstractRubette {

    public Select2DRubette() {
        setInCount(1);
        setOutCount(2);
    }

    
    public void run(RunInfo runInfo) {
        Denotator input = getInput(0);
        if (input == null) {
            addError(INPUTNULL_ERROR);
        }
        else if (form == null) {
            addError(FORMNOTSET_ERROR);
        }
        else if (input.hasForm(form)){
            List<Denotator> denoList = ((FactorDenotator)input).getFactors();
            denotators = new ArrayList<Denotator>(denoList.size());
            for (Denotator d : denoList) {
                denotators.add(d);
            }
            
            if (select2DDialog != null) {
                Denotator output0, output1;
                select2DDialog.setDenotators(denotators);
                List<Denotator> selected = new LinkedList<Denotator>();
                List<Denotator> notSelected = new LinkedList<Denotator>();
                if (select2DDialog.hasSelections()) {
                    select2DDialog.getSelectedDenotators(selected, notSelected);
                    output0 = DenoFactory.makeDenotator(form, selected);
                    output1 = DenoFactory.makeDenotator(form, notSelected);
                }
                else {
                    // if there is no selection  at all,
                    // simply pass through input
                    output0 = input;
                    output1 = input.getForm().createDefaultDenotator(input.getAddress());
                }
                setOutput(0, output0);
                setOutput(1, output1);
            }
            else {
                setOutput(0, input);
                setOutput(1, input);
            }
        }
    }


    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "Select2D"; //$NON-NLS-1$
    }

    
    public ImageIcon getIcon() {
        return icon;
    }

    
    public boolean hasInfo() {
        return true;
    }
    
    
    public String getInfo() {
        if (form == null) {
            return FORMNOTSET_INFO;
        }
        else {
            return form.getNameString()+": "+form.getTypeString(); //$NON-NLS-1$
        }
    }
    
    
    public String getShortDescription() {
        return "Selects a subset of a denotator of "+ //$NON-NLS-1$
               "type power or list"; //$NON-NLS-1$
    }
    
    
    public String getLongDescription() {
        return "The Select2D Rubette selects a subset of "+ //$NON-NLS-1$
               "a denotator of type power or list according "+ //$NON-NLS-1$
               "to 2D graphical selection criteria specfied "+ //$NON-NLS-1$
               "in the properties."; //$NON-NLS-1$
    }
    
    
    public boolean hasProperties() {
        return true;
    }
    
    
    public JComponent getProperties() {
        if (select2DDialog ==  null) {
            select2DDialog = new Select2DDialog();
        }
        if (form != null) {
            // set properties
            select2DDialog.setForm(form);
            if (selections != null) {
                select2DDialog.setSelections(selections);
            }
            if (denotators != null) {
                select2DDialog.setDenotators(denotators);
            }
        }
        return select2DDialog;
    }

    
    public void revertProperties() {
        select2DDialog.setForm(form);
        select2DDialog.setSelections(selections);
    }
    
    
    public boolean applyProperties() {
        form = select2DDialog.getForm();
        selections = select2DDialog.getSelections();
        return true;
    }

    
    public String getInTip(int i) {
        if (form == null) {
            return Messages.getString("Select2DRubette.inputdenotip"); //$NON-NLS-1$
        }
        else {
            return Messages.getString("Select2DRubette.inputdenoformtip")+form.getNameString(); //$NON-NLS-1$
        }
    }


    public String getOutTip(int i) {
        if (form == null) {
            return Messages.getString("Select2DRubette.outputdenotip"); //$NON-NLS-1$
        }
        else {
            return Messages.getString("Select2DRubette.outputdenoformtip")+form.getNameString(); //$NON-NLS-1$
        }
    }

    
    public Rubette newInstance() {
        return new Select2DRubette();
    }
    
    
    public Rubette duplicate() {
        Select2DRubette rubette = new Select2DRubette();
        rubette.setForm(getForm());
        for (Select2DPanel s : getSelections()) {
            rubette.addSelection(s.duplicate());
        }
        rubette.getProperties();
        return rubette;
    }    

    
    public Form getForm() {
        return form;
    }
    
    
    public void setForm(Form form) {
        this.form = form;
    }
    
    
    public ArrayList<Select2DPanel> getSelections() {
        if (selections == null) {
            selections = new ArrayList<Select2DPanel>();
        }
        return selections;
    }
    
    
    public void addSelection(Select2DPanel selection) {
        if (selections == null) {
            selections = new ArrayList<Select2DPanel>();
        }
        selections.add(selection);
    }
    
    
    private final static String SELECTION_PANEL = "SelectionPanel"; //$NON-NLS-1$
    
    public void toXML(XMLWriter writer) {
        if (form != null) {
            writer.writeFormRef(form);
            for (Select2DPanel panel : selections) {
                writer.openBlock(SELECTION_PANEL);
                panel.toXML(writer);
                writer.closeBlock();
            }
        }
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, FORM);
        if (child == null) {
            // no form
            Select2DRubette rubette = new Select2DRubette();
            return rubette;
        }
        
        // parse form
        Form f = reader.parseAndResolveForm(child);
        if (f == null) {
            // invalid form
            return null;
        }
        
        // parse selection panels
        child = XMLReader.getNextSibling(child, SELECTION_PANEL);
        ArrayList<Select2DPanel> selectionPanels = new ArrayList<Select2DPanel>();
        while (child != null) {
            Select2DPanel panel = Select2DPanel.fromXML(reader, child, f);
            if (panel != null) {
                selectionPanels.add(panel);
            }
            child = XMLReader.getNextSibling(child, SELECTION_PANEL);
        }
        
        Select2DRubette rubette = new Select2DRubette();
        rubette.form = f;
        rubette.selections = selectionPanels;
        rubette.getProperties();
        
        return rubette;
    }
    

    // properties
    private Form                     form = null;
    private ArrayList<Select2DPanel> selections = new ArrayList<Select2DPanel>();
    
    // volatile data
    private Select2DDialog           select2DDialog = null;
    private ArrayList<Denotator>     denotators = null;
    
    private final static ImageIcon icon;

    private final static String INPUTNULL_ERROR  = Messages.getString("Select2DRubette.inputnullerror"); //$NON-NLS-1$;
    private final static String FORMNOTSET_ERROR = Messages.getString("Select2DRubette.formnotseterror"); //$NON-NLS-1$;
    private final static String FORMNOTSET_INFO  = Messages.getString("Select2DRubette.noformsetinfo"); //$NON-NLS-1$;
    
    static {
        icon = Icons.loadIcon(Select2DRubette.class, "select2dicon.png"); //$NON-NLS-1$
    }
}

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

import static org.rubato.xml.XMLConstants.FORM;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JSelectForm;
import org.rubato.composer.icons.Icons;
import org.rubato.logeo.DenoFactory;
import org.rubato.logeo.Select;
import org.rubato.math.yoneda.*;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class SelectFormRubette extends AbstractRubette {

    public SelectFormRubette() {
        setInCount(1);
        setOutCount(1);
    }
    
    
    public void run(RunInfo runInfo) {
        Denotator input = getInput(0);
        Denotator output = null;
        if (input == null) {
            addError("Input denotator is null.");
        }
        else if (outputForm != null) {
            try {
                List<Denotator> denoList = Select.select(baseForm, input);
                output = DenoFactory.makeDenotator(outputForm, denoList);                
            }
            catch (RubatoException e) {
                addError(e.getMessage()+".");
            }
        }
        setOutput(0, output);
    }

    
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }
    

    public String getName() {
        return "SelectForm"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        SelectFormRubette rubette = new SelectFormRubette();
        rubette.setOutputForm(outputForm);
        return rubette;
    }
    
    
    public boolean hasInfo() {
        return true;
    }
    
    
    public String getInfo() {
        if (outputForm == null) {
            return "Not set";
        }
        else {
            return outputForm.getNameString();
        }
    }
    

    public boolean hasProperties() {
        return true;
    }


    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            selectForm = new JSelectForm(Repository.systemRepository(), Form.LIST, Form.POWER);
            selectForm.setForm(outputForm);
            properties.add(selectForm, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {
        setOutputForm(selectForm.getForm());
        return true;
    }


    public void revertProperties() {
        selectForm.setForm(outputForm);
    }

    
    public String getShortDescription() {
        return "Selects all denotators of a given form";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The SelectForm Rubette selects all denotators "+
               "of a given form from the input denotators "+
               "and creates the result as a denotator of "+
               "type power or list.";
    }


    public String getInTip(int i) {
        return "Input denotator"; //$NON-NLS-1$
    }


    public String getOutTip(int i) {
        if (outputForm != null) {
            return TextUtils.replaceStrings("Output denotator of form %%1", outputForm.getNameString());
        }
        else {
            return "Output denotator";
        }
    }

    
    public void toXML(XMLWriter writer) {
        if (outputForm != null) {
            writer.writeFormRef(outputForm);
        }
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Form form = null;
        Element child = XMLReader.getChild(element, FORM);
        if (child != null) {
            form = reader.parseAndResolveForm(child);
        }
        SelectFormRubette rubette = new SelectFormRubette();
        rubette.setOutputForm(form);
        return rubette;
    }

    
    private void setOutputForm(Form form) {
        if (form == null) {
            outputForm = null;
            baseForm = null;
        }
        else if (form instanceof PowerForm) {
            outputForm = form;
            baseForm = ((PowerForm)form).getForm();
        }
        else if (form instanceof ListForm) {
            outputForm = form;
            baseForm = ((ListForm)form).getForm();
        }
        else {
            outputForm = null;
            baseForm = null;
        }
    }

    
    private JPanel properties = null;
    private JSelectForm selectForm = null;
    private Form outputForm = null; 
    private Form baseForm = null;
    private static final ImageIcon icon = Icons.emptyIcon;
}

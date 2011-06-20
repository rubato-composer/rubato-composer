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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JSelectForm;
import org.rubato.composer.icons.Icons;
import org.rubato.logeo.reform.Reformer;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.util.TextUtils;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


public class ReformRubette extends AbstractRubette {

    public ReformRubette() {
        setInCount(1);
        setOutCount(1);
    }
       
    
    public void run(RunInfo runInfo) {
        Denotator d = getInput(0);
        if (d == null) {
            addError("Input denotator is null.");
        }
        else if (outputForm == null) {
            addError("Output form is not set.");
        }
        else {
            Form inputForm = d.getForm();
            Reformer reformer = Reformer.make(inputForm, outputForm);
            if (reformer == null) {
                addError("Cannot reform %%1 to %%2.", inputForm.getNameString(), outputForm.getNameString());
            }
            else {
                try {
                    setOutput(0, reformer.reform(d));
                }
                catch (RubatoException e) {
                    addError(e);
                }
            }
        }
    }

    
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }
    

    public String getName() {
        return "Reform"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        ReformRubette rubette = new ReformRubette();
        rubette.outputForm = outputForm;
        return rubette;
    }

    
    public boolean hasProperties() {
        return true;
    }


    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            selectForm = new JSelectForm(Repository.systemRepository());
            selectForm.setForm(outputForm);
            properties.add(selectForm, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {
        outputForm = selectForm.getForm();
        return true;
    }


    public void revertProperties() {
        selectForm.setForm(outputForm);
    }

    
    public String getShortDescription() {
        return "Reforms a denotator";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The Reform Rubette cast its input denotator"+
               " to a denotator with a specified form.";
    }


    public String getInTip(int i) {
        return "Input denotator"; //$NON-NLS-1$
    }


    public String getOutTip(int i) {
        if (outputForm == null) {
            return "Output denotator"; //$NON-NLS-1$
        }
        else {
            return TextUtils.replaceStrings("Output dennotator of form %%1", outputForm.getNameString());
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
        ReformRubette rubette = new ReformRubette();
        rubette.outputForm = form;
        return rubette;
    }

    
    private JPanel      properties = null;
    private JSelectForm selectForm = null;
    private Form        outputForm = null;
    
    private static final ImageIcon icon;

    static {
        icon = Icons.loadIcon(ReformRubette.class, "reformicon.png"); //$NON-NLS-1$
    }
}

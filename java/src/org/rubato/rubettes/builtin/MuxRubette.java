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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.rubato.base.AbstractRubette;
import org.rubato.base.RubatoConstants;
import org.rubato.base.Rubette;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JConnectorSliders;
import org.rubato.composer.icons.Icons;
import org.rubato.math.module.ZRing;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


public class MuxRubette extends AbstractRubette {

    public MuxRubette() {
        setInCount(2);
        setOutCount(1);
    }

    
    public void run(RunInfo runInfo) {
        int n = 0;
        Denotator d = getInput(0);
        if (d != null && d instanceof SimpleDenotator) {
            SimpleDenotator sd = (SimpleDenotator)d;
            if (sd.getSimpleForm().getModule() == ZRing.ring) {
                n = sd.getInteger();
                if (n < 0) { n = 0; }
                if (n > getInCount()-2) { n = getInCount()-2; }
            }
            else {
                addError(Messages.getString("MuxRubette.nonintegererror")); //$NON-NLS-1$
                return;
            }
        }
        else {
            addError(Messages.getString("MuxRubette.nonintegererror")); //$NON-NLS-1$
            return;            
        }
        Denotator output = getInput(n+1);
        if (output == null) {
            addError(Messages.getString("MuxRubette.nullerror"), n+1); //$NON-NLS-1$
            return;
        }
        setOutput(0, output);
    }


    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "Mux"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        MuxRubette newRubette = new MuxRubette();
        newRubette.setInCount(getInCount());
        return newRubette;
    }

    
    public boolean hasProperties() {
        return true;
    }


    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            
            inSlider = new JConnectorSliders(true, false);
            inSlider.setInLimits(2, 8);
            inSlider.setInValue(getInCount());
            properties.add(inSlider, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {
        setInCount(inSlider.getInValue());
        return true;
    }


    public void revertProperties() {
        inSlider.setInValue(getInCount());
    }

    
    public String getShortDescription() {
        return "Selects an input denotator";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The Mux Rubette selects in input denotator"+
               " based on the integer at input #0.";
    }


    public String getInTip(int i) {
        if (i == 0) {
            return "Integer selector"; //$NON-NLS-1$
        }
        else {
            return "Input denotator #"+(i-1); //$NON-NLS-1$
        }
    }


    public String getOutTip(int i) {
        return "Output denotator"; //$NON-NLS-1$
    }

    
    private final static String INPUTS      = "Inputs"; //$NON-NLS-1$
    private final static String NUMBER_ATTR = "number"; //$NON-NLS-1$
    
    public void toXML(XMLWriter writer) {
        writer.empty(INPUTS, NUMBER_ATTR, getInCount());
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, INPUTS);
        if (child != null) {
            int n = XMLReader.getIntAttribute(child, NUMBER_ATTR, 2, 8, 2);
            MuxRubette r = new MuxRubette();
            r.setInCount(n);
            return r;
        }
        else {
            return null;
        }
    }

    
    private JPanel properties = null;
    private JConnectorSliders inSlider = null;
    private static final ImageIcon icon;

    static {
        icon = Icons.loadIcon(MuxRubette.class, "muxicon.png"); //$NON-NLS-1$
    }
}

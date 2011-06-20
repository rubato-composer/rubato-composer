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
import org.rubato.math.yoneda.Denotator;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


/**
 * The Latch Rubette stores its input denotator
 * and provides it on its outputs the number of
 * which can be configured.
 * 
 * @author Gérard Milmeister
 */
public class LatchRubette extends AbstractRubette {

    public LatchRubette() {
        setInCount(1);
        setOutCount(1);
    }
    

    public void run(RunInfo runInfo) {
        Denotator d = getInput(0);
        for (int i = 0; i < getOutCount(); i++) {
            setOutput(i, d);
        }
    }

    
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }
    

    public String getName() {
        return "Latch"; //$NON-NLS-1$
    }

    
    public Rubette duplicate() {
        LatchRubette newRubette = new LatchRubette();
        newRubette.setOutCount(getOutCount());
        return newRubette;
    }

    
    public boolean hasProperties() {
        return true;
    }


    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            
            outSlider = new JConnectorSliders(false, true);
            outSlider.setOutLimits(1, 8);
            outSlider.setOutValue(getOutCount());
            properties.add(outSlider, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {
        setOutCount(outSlider.getOutValue());
        return true;
    }


    public void revertProperties() {
        outSlider.setOutValue(getOutCount());
    }

    
    public String getShortDescription() {
        return "Stores an input denotator";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The Latch Rubette stores its input denotator"+
               " and provides it on its outputs the number of"+
               " which can be configured.";
    }


    public String getInTip(int i) {
        return "Input denotator"; //$NON-NLS-1$
    }


    public String getOutTip(int i) {
        return "Output denotator #"+i; //$NON-NLS-1$
    }

    
    private final static String OUTPUTS     = "Outputs"; //$NON-NLS-1$
    private final static String NUMBER_ATTR = "number"; //$NON-NLS-1$
    
    
    public void toXML(XMLWriter writer) {
        writer.empty(OUTPUTS, NUMBER_ATTR, getOutCount());
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, OUTPUTS);
        if (child != null) {
            int n = XMLReader.getIntAttribute(child, NUMBER_ATTR, 1, 8, 0);
            LatchRubette r = new LatchRubette();
            r.setOutCount(n);
            return r;
        }
        else {
            return null;
        }
    }

    
    private JPanel properties = null;
    private JConnectorSliders outSlider = null;
    private static final ImageIcon icon;

    static {
        icon = Icons.loadIcon(LatchRubette.class, "latchicon.png"); //$NON-NLS-1$
    }
}

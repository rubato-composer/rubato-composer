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

import static org.rubato.composer.Utilities.getJDialog;
import static org.rubato.composer.Utilities.makeTitledBorder;
import static org.rubato.xml.XMLConstants.NAME_ATTR;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

public class MacroInputRubette
        extends AbstractRubette
        implements ChangeListener {

    public MacroInputRubette() {
        setInCount(0);
        setOutCount(1);
        labels = new String[1];
    }
    
    
    public void run(RunInfo runInfo) {}

    
    public void setValue(int i, Denotator d) {
        values[i] = d;
        setOutput(i, values[i]);
    }
    

    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "MacroInput"; //$NON-NLS-1$
    }

    
    public Rubette newInstance() {
        return new MacroInputRubette();
    }
    
    
    public Rubette duplicate() {
        MacroInputRubette newRubette = new MacroInputRubette();
        newRubette.setOutCount(getOutCount());
        newRubette.values = new Denotator[getOutCount()];
        newRubette.labels = new String[getOutCount()];
        for (int i = 0; i < getOutCount(); i++) {
            newRubette.labels[i] = labels[i];
        }
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
            outSlider.addChangeListener(this);
            properties.add(outSlider, BorderLayout.NORTH);
            outPanel = new JPanel();
            fillOutPanel();
            properties.add(outPanel, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {
        int outCount = outSlider.getOutValue();
        setOutCount(outCount);
        values = new Denotator[outCount];
        labels = new String[outCount];
        for (int i = 0; i < outCount; i++) {
            labels[i] = outTextFields[i].getText().trim();
        }
        return true;
    }


    public void revertProperties() {
        outSlider.setOutValue(getOutCount());
        fillOutPanel();
        getJDialog(properties).pack();
    }

    
    public String getShortDescription() {
        return "Retrieves inputs in a MacroRubette";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The MacroInput Rubette retrieves inputs in a "+
               "MacroRubette";
    }


    public String getOutTip(int i) {
        if (i >= labels.length || labels[i] == null || labels[i].length() == 0) {
            return "Network input denotator #"+i; //$NON-NLS-1$
        }
        else {
            return labels[i];
        }
    }

    
    public void stateChanged(ChangeEvent e) {
        fillOutPanel();
        getJDialog(properties).pack();
    }
    
    
    private void fillOutPanel() {
        outPanel.removeAll();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        int outCount = outSlider.getOutValue();
        if (outCount > 0) {
            outPanel.setLayout(gbl);
            outPanel.setBorder(makeTitledBorder("Input labels")); //$NON-NLS-1$
            outTextFields = new JTextField[outCount];
            labels = new String[outCount];
            c.ipadx = 10;
            for (int i = 0; i < outCount; i++) {
                c.weightx = 0.0;
                c.fill = GridBagConstraints.NONE;
                c.gridwidth = GridBagConstraints.RELATIVE;
                JLabel label = new JLabel("#"+i); //$NON-NLS-1$
                gbl.setConstraints(label, c);
                outPanel.add(label);
                c.weightx = 1.0;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridwidth = GridBagConstraints.REMAINDER;
                outTextFields[i] = new JTextField();
                gbl.setConstraints(outTextFields[i], c);
                outPanel.add(outTextFields[i]);
            }
        }
    }
    
    
    private final static String LABELS     = "Labels"; //$NON-NLS-1$
    private final static String LABEL      = "Label"; //$NON-NLS-1$
    private final static String COUNT_ATTR = "count"; //$NON-NLS-1$

    
    public void toXML(XMLWriter writer) {
        writer.openBlock(LABELS, COUNT_ATTR, getOutCount());
        for (int i = 0; i < getOutCount(); i++) {
            String s = labels[i]==null?"":labels[i].trim(); //$NON-NLS-1$
            writer.empty(LABEL, NAME_ATTR, s);
        }
        writer.closeBlock();
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, LABELS);
        int outCount = XMLReader.getIntAttribute(child, COUNT_ATTR, 1, 8, 1);
        String[] lbls = new String[outCount];
        child = XMLReader.getChild(child, LABEL);
        int i = 0;
        while (child != null) {
            lbls[i] = child.getAttribute(NAME_ATTR);
            i++;
            child = XMLReader.getNextSibling(child, LABEL);
        }
        MacroInputRubette rubette = new MacroInputRubette();
        rubette.setOutCount(outCount);
        rubette.values = new Denotator[outCount];
        rubette.labels = lbls;
        return rubette;
    }

    
    private JPanel            properties = null;
    private JConnectorSliders outSlider  = null;
    private JPanel            outPanel   = null;
    private JTextField[]      outTextFields = null;
    private Denotator[]       values;
    private String[]          labels;
    private static final ImageIcon icon;

    static {
        icon = Icons.loadIcon(MacroInputRubette.class, "inputicon.png"); //$NON-NLS-1$
    }
}

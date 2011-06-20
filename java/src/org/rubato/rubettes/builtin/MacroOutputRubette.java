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

public class MacroOutputRubette
        extends AbstractRubette
        implements ChangeListener {

    public MacroOutputRubette() {
        setInCount(1);
        setOutCount(0);
        labels = new String[1];
    }
    
    
    public void run(RunInfo runInfo) {
        for (int i = 0; i < getInCount(); i++) {
            values[i] = getInput(i);
        }
    }


    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "MacroOutput";
    }

    
    public Rubette newInstance() {
        return new MacroOutputRubette();
    }
    
    
    public Rubette duplicate() {
        MacroOutputRubette newRubette = new MacroOutputRubette();
        newRubette.setInCount(getInCount());
        newRubette.values = new Denotator[getInCount()];
        newRubette.labels = new String[getInCount()];
        for (int i = 0; i < getInCount(); i++) {
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
            
            inSlider = new JConnectorSliders(true, false);
            inSlider.setInLimits(1, 8);
            inSlider.setInValue(getInCount());
            inSlider.addChangeListener(this);
            properties.add(inSlider, BorderLayout.NORTH);
            inPanel = new JPanel();
            fillInPanel();
            properties.add(inPanel, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {        
        int inCount = inSlider.getInValue();
        setInCount(inCount);
        values = new Denotator[inCount];
        labels = new String[inCount];
        for (int i = 0; i < inCount; i++) {
            labels[i] = inTextFields[i].getText().trim();
        }
        return true;
    }


    public void revertProperties() {
        inSlider.setInValue(getInCount());
        fillInPanel();
        getJDialog(properties).pack();
    }

    
    public String getShortDescription() {
        return "Sets outputs in a MacroRubette";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The MacroOutput Rubette sets outputs in a "+
               "MacroRubette.";
    }


    public String getInTip(int i) {
        
        if (i >= labels.length || labels[i] == null || labels[i].length() == 0) {
            return "Network output denotator #"+i;
        }
        else {
            return labels[i];
        }
    }

    
    public Denotator getValue(int i) {
        return values[i];
    }
    
    
    public void stateChanged(ChangeEvent e) {
        fillInPanel();
        getJDialog(properties).pack();
    }
    
    
    private void fillInPanel() {
        inPanel.removeAll();
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        int inCount = inSlider.getInValue();
        if (inCount > 0) {
            inPanel.setLayout(gbl);
            inPanel.setBorder(makeTitledBorder("Output labels"));
            inTextFields = new JTextField[inCount];
            labels = new String[inCount];
            c.ipadx = 10;
            for (int i = 0; i < inCount; i++) {
                c.weightx = 0.0;
                c.fill = GridBagConstraints.NONE;
                c.gridwidth = GridBagConstraints.RELATIVE;
                JLabel label = new JLabel("#"+i);
                gbl.setConstraints(label, c);
                inPanel.add(label);
                c.weightx = 1.0;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridwidth = GridBagConstraints.REMAINDER;
                inTextFields[i] = new JTextField();
                gbl.setConstraints(inTextFields[i], c);
                inPanel.add(inTextFields[i]);
            }
        }
    }
    
    
    private final static String LABELS     = "Labels";
    private final static String LABEL      = "Label";
    private final static String COUNT_ATTR = "count";

    
    public void toXML(XMLWriter writer) {
        writer.openBlock(LABELS, COUNT_ATTR, getInCount());
        for (int i = 0; i < getInCount(); i++) {
            String s = labels[i]==null?"":labels[i].trim();
            writer.empty(LABEL, NAME_ATTR, s);
        }
        writer.closeBlock();
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Element child = XMLReader.getChild(element, LABELS);
        int inCount = XMLReader.getIntAttribute(child, COUNT_ATTR, 1, 8, 1);
        String[] lbls = new String[inCount];
        child = XMLReader.getChild(child, LABEL);
        int i = 0;
        while (child != null) {
            lbls[i] = child.getAttribute(NAME_ATTR);
            i++;
            child = XMLReader.getNextSibling(child, LABEL);
        }
        MacroOutputRubette rubette = new MacroOutputRubette();
        rubette.setInCount(inCount);
        rubette.values = new Denotator[inCount];
        rubette.labels = lbls;
        return rubette;
    }

    
    private JPanel            properties = null;
    private JConnectorSliders inSlider = null;
    private JPanel            inPanel = null;
    private JTextField[]      inTextFields = null;
    private Denotator[]       values;
    private String[]          labels;
    private static final ImageIcon icon;

    static {
        icon = Icons.loadIcon(MacroOutputRubette.class, "outputicon.png");
    }
}

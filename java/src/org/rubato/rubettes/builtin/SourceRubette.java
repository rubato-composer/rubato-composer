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

import static org.rubato.xml.XMLConstants.*;

import java.awt.BorderLayout;

import javax.swing.*;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.components.JSelectDenotator;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.Denotator;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


public class SourceRubette extends AbstractRubette {    
    
    public SourceRubette() {
        setInCount(0);
        setOutCount(1);
    }

    
    public void run(RunInfo runInfo) {
        if (refreshable && denotator != null) {
            String s = denotator.getNameString();
            Denotator d = rep.getDenotator(s);
            if (d != null) {
                setDenotator(d);
            }
            else {
                addError(Messages.getString("SourceRubette.namenotavailable"), s); //$NON-NLS-1$
                return;
            }
        }
    }

    
    public Rubette duplicate() {
        SourceRubette rubette = new SourceRubette();
        rubette.setDenotator(denotator);
        rubette.setRefreshable(getRefreshable());
        return rubette;
    }
    
    
    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "Source"; //$NON-NLS-1$
    }

    
    public boolean hasProperties() {
        return true;
    }
    

    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            selector = new JSelectDenotator(Repository.systemRepository());
            selector.setDenotator(denotator);
            properties.add(selector, BorderLayout.CENTER);
            refreshBox = new JCheckBox(Messages.getString("SourceRubette.selfrefreshable")); //$NON-NLS-1$
            refreshBox.setToolTipText(Messages.getString("SourceRubette.selfrefreshtooltip")); //$NON-NLS-1$
            refreshBox.setSelected(refreshable);
            properties.add(refreshBox, BorderLayout.SOUTH);
        }
        return properties;
    }
    
    
    public boolean applyProperties() {
        setDenotator(selector.getDenotator());
        setRefreshable(refreshBox.isSelected());
        return true;
    }
    
    
    public void revertProperties() {
        selector.setDenotator(denotator);
        refreshBox.setSelected(getRefreshable());
    }

    
    public boolean hasInfo() {
        return true;
    }
    
    
    public String getInfo() {
        return name;
    }

    
    public ImageIcon getIcon() {
        return icon;
    }

    
    public String getShortDescription() {
        return Messages.getString("SourceRubette.containsdenotator"); //$NON-NLS-1$
    }

    
    public String getLongDescription() {
        return "The Source Rubette stores a denotator."; //$NON-NLS-1$
    }
    
    
    public String getOutTip(int i) {
        return Messages.getString("SourceRubette.storeddenotator"); //$NON-NLS-1$
    }

    
    private final static String REFRESHABLE = "Refreshable";
    
    
    public void toXML(XMLWriter writer) {
        writer.empty(REFRESHABLE, VALUE_ATTR, refreshable?TRUE_VALUE:FALSE_VALUE);
        if (denotator != null) {
            denotator.toXML(writer);
        }
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        Denotator d = null;
        boolean r = false; 
        Element child = XMLReader.getChild(element, REFRESHABLE);
        String val = child.getAttribute(VALUE_ATTR);
        if (val.equals(TRUE_VALUE)) {
            r = true;
        }
        child = XMLReader.getNextSibling(child, DENOTATOR);
        if (child != null) {
            d = reader.parseDenotator(child);
        }
        SourceRubette rubette = new SourceRubette();
        rubette.setRefreshable(r);
        rubette.setDenotator(d);
        return rubette;
    }
    
    
    private void setDenotator(Denotator d) {
        denotator = d;
        if (d == null) {
            name = " "; //$NON-NLS-1$
        }
        else {
            name = d.getNameString()+": "+d.getForm().getNameString(); //$NON-NLS-1$
        }
        setOutput(0, denotator);
    }
    
    
    private void setRefreshable(boolean b) {
        refreshable = b;
    }

    
    private boolean getRefreshable() {
        return refreshable;
    }

    
    private JPanel           properties = null;
    private Denotator        denotator = null;
    private JSelectDenotator selector;
    private JCheckBox        refreshBox;
    private String           name = " "; //$NON-NLS-1$
    private boolean          refreshable = false;
    private static final ImageIcon icon;
    private static final Repository rep = Repository.systemRepository();

    static {
        icon = Icons.loadIcon(SourceRubette.class, "sourceicon.png"); //$NON-NLS-1$
    }
}

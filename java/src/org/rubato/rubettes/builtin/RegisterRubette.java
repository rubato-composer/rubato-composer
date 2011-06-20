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

import static org.rubato.xml.XMLConstants.VALUE_ATTR;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.rubato.base.*;
import org.rubato.composer.RunInfo;
import org.rubato.composer.icons.Icons;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;


public class RegisterRubette extends AbstractRubette {

    public RegisterRubette() {
        setInCount(1);
        setOutCount(1);
    }

    
    public void run(RunInfo runInfo) {
        Denotator input = getInput(0);
        if (input == null) {
            addError("Input denotator is null.");
        }
        else if (denotatorName == null) {
            addError("Denotator name has not been set in properties.");
        }
        else {
            // create a new denotator from the input with the given name
            Denotator d = input.namedCopy(NameDenotator.make(getDenotatorName()));
            rep.register(d);
            setOutput(0, d);
        }
    }


    public String getGroup() {
        return RubatoConstants.CORE_GROUP;
    }

    
    public String getName() {
        return "Register";
    }
    
    
    public void setDenotatorName(String s) {
        if (s != null) {
            s = s.trim();
            if (s.length() == 0) { s = null; }
        }
        denotatorName = s;
    }
    

    public String getDenotatorName() {
        return denotatorName;
    }
    
    
    public Rubette duplicate() {
        RegisterRubette newRubette = new RegisterRubette();
        newRubette.setDenotatorName(getDenotatorName());
        return newRubette;
    }
    
    
    public boolean hasInfo() {
        return true;
    }

    
    public String getInfo() {
        return (denotatorName == null)?" ":denotatorName;
    }

    
    public boolean hasProperties() {
        return true;
    }


    public JComponent getProperties() {
        if (properties == null) {
            properties = new JPanel();            
            properties.setLayout(new BorderLayout());
            JPanel namePanel = new JPanel();
            namePanel.setLayout(new BorderLayout());
            nameField = new JTextField();
            nameField.setText(getDenotatorName());
            namePanel.add(nameField, BorderLayout.CENTER);
            namePanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Denotator name"));
            properties.add(namePanel, BorderLayout.CENTER);
        }
        return properties;
    }


    public boolean applyProperties() {
        setDenotatorName(nameField.getText());
        return true;
    }


    public void revertProperties() {
        nameField.setText((denotatorName == null)?"":denotatorName);
    }

    
    public String getShortDescription() {
        return "Registers its input denotator";
    }

    
    public ImageIcon getIcon() {
        return icon;
    }
    

    public String getLongDescription() {
        return "The Register Rubette gives a name to its input denotator"+
               " and registers it with the system repository"+
               " overriding any denotator with that name already present.";
    }


    public String getInTip(int i) {
        return "Input denotator";
    }


    public String getOutTip(int i) {
        return "Registered output denotator";
    }

    
    private final static String NAME = "Name";
    
    
    public void toXML(XMLWriter writer) {
        String name = getDenotatorName();
        name = (name == null)?"":name;
        writer.empty(NAME, VALUE_ATTR, name);
    }
    
    
    public Rubette fromXML(XMLReader reader, Element element) {
        RegisterRubette newRubette = new RegisterRubette();
        String name = null;
        Element child = XMLReader.getChild(element, NAME);
        if (child != null) {
            name = child.getAttribute(VALUE_ATTR);
        }
        newRubette.setDenotatorName(name);
        return newRubette;
    }

    
    private JPanel     properties = null;
    private JTextField nameField = null;
    private String     denotatorName = null; // either null or of length > 0
    private static final ImageIcon icon;
    private static Repository rep = Repository.systemRepository();

    static {
        icon = Icons.loadIcon(RegisterRubette.class, "registericon.png");
    }
}

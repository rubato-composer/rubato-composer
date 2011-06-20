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

package org.rubato.composer.components;

import static org.rubato.composer.Utilities.getJDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.rubato.math.module.Module;
import org.rubato.math.module.ModuleElement;


/**
 * @author Gérard Milmeister
 */
public class JModuleElementEntry extends JPanel implements ActionListener {

    public JModuleElementEntry(Module module) {
        this.module = module;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        layoutPanel();
    }
    
    
    public Module getModule() {
        return moduleEntry.getModule();
    }
    
    
    public ModuleElement getModuleElement() {
        return simpleEntry.getValue();
    }
    
    
    public void setModuleElement(ModuleElement element) {
        simpleEntry.setValue(element);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == moduleEntry) {            
            if (module != moduleEntry.getModule()) {
                module = moduleEntry.getModule();
                layoutPanel();
                getJDialog(this).pack();
            }
        }
    }

    
    private void layoutPanel() {
        removeAll();
        moduleEntry = new JModuleEntry();
        moduleEntry.addActionListener(this);
        moduleEntry.setModule(module);
        add(moduleEntry);
        
        add(Box.createVerticalStrut(5));
        
        simpleEntry = JSimpleEntry.make(module);
        add(simpleEntry);
    }

    
    private JModuleEntry moduleEntry;
    private JSimpleEntry simpleEntry;
    private Module       module;    
}

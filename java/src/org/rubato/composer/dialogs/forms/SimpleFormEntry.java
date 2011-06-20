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

package org.rubato.composer.dialogs.forms;

import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.rubato.composer.components.JModuleEntry;
import org.rubato.logeo.FormFactory;
import org.rubato.math.module.Module;
import org.rubato.math.yoneda.Form;

public class SimpleFormEntry
        extends AbstractFormEntry
        implements ActionListener {

    public SimpleFormEntry() {
        setLayout(new BorderLayout());
        moduleEntry = new JModuleEntry();
        moduleEntry.addActionListener(this);
        moduleEntry.setBorder(makeTitledBorder(Messages.getString("SimpleFormEntry.module"))); //$NON-NLS-1$
        add(moduleEntry, BorderLayout.CENTER);
    }
    
    
    public Form getForm(String name) {
        Module module = moduleEntry.getModule();
        if (module != null && name.length() > 0) {
            return FormFactory.makeModuleForm(name, module);
        }
        else {
            return null;
        }
    }

    
    public boolean canCreate() {
        return (moduleEntry.getModule() != null);
    }
    
    
    public void clear() {
        moduleEntry.clear();
    }

    
    public void actionPerformed(ActionEvent e) {
        fireActionEvent();
    }
    
    
    private JModuleEntry moduleEntry;
}

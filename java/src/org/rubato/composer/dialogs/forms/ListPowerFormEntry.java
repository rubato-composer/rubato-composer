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

import org.rubato.base.RubatoDictionary;
import org.rubato.composer.components.JSelectForm;
import org.rubato.logeo.FormFactory;
import org.rubato.math.yoneda.Form;

public class ListPowerFormEntry
        extends AbstractFormEntry
        implements ActionListener {

    public ListPowerFormEntry(RubatoDictionary dict, int type) {
        setLayout(new BorderLayout());
        this.type = type;
        selectForm = new JSelectForm(dict);
        selectForm.setBorder(makeTitledBorder(Messages.getString("ListPowerFormEntry.baseform"))); //$NON-NLS-1$
        selectForm.addActionListener(this);
        add(selectForm, BorderLayout.CENTER);
    }
    
    
    public Form getForm(String name) {
        Form baseForm = selectForm.getForm();
        if (baseForm != null && name.length() > 0) {
            if (type == Form.POWER) {
                return FormFactory.makePowerForm(name, baseForm);
            }
            else if (type == Form.LIST)  {
                return FormFactory.makeListForm(name, baseForm);
            }
        }
        return null;
    }

    
    public boolean canCreate() {
        return (selectForm.getForm() != null);
    }
    
    
    public void clear() {
        selectForm.clear();
    }

    
    public void actionPerformed(ActionEvent e) {
        fireActionEvent();
    }
    
    
    private JSelectForm selectForm;
    private int type;
}

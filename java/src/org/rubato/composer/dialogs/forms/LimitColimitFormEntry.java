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
import java.util.List;

import org.rubato.base.RubatoDictionary;
import org.rubato.logeo.FormFactory;
import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.LimitForm;

public class LimitColimitFormEntry
        extends AbstractFormEntry
        implements ActionListener {

    public LimitColimitFormEntry(RubatoDictionary dict, int type) {
        this.type = type;
        setLayout(new BorderLayout());
        formDiagram = new JFormDiagram(dict);
        formDiagram.setBorder(makeTitledBorder(Messages.getString("LimitColimitFormEntry.diagram"))); //$NON-NLS-1$
        add(formDiagram, BorderLayout.CENTER);
    }
    
    
    public Form getForm(String name) {
        List<Form> forms = formDiagram.getForms();
        if (forms != null && name.length() > 0) {
            if (type == Form.LIMIT) {
                LimitForm form = FormFactory.makeLimitForm(name, forms);
                List<String> labels = formDiagram.getLabels();
                if (labels != null) {
                    form.setLabels(labels);
                }
                return form;
            }
            else if (type == Form.COLIMIT) {
                ColimitForm form = FormFactory.makeColimitForm(name, forms);
                List<String> labels = formDiagram.getLabels();
                if (labels != null) {
                    form.setLabels(labels);
                }
                return form;
            }
        }
        return null;
    }

    
    public boolean canCreate() {
        return true;
    }
    
    
    public void clear() {
        formDiagram.clear();
    }

    
    public void actionPerformed(ActionEvent e) {
        fireActionEvent();
    }
    
    
    private JFormDiagram formDiagram;
    private int type;
}

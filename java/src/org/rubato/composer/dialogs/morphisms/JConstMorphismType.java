/*
 * Copyright (C) 2006 Gérard Milmeister
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

package org.rubato.composer.dialogs.morphisms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.rubato.composer.components.JSimpleEntry;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.morphism.ConstantMorphism;
import org.rubato.math.module.morphism.ModuleMorphism;

class JConstMorphismType extends JMorphismType implements ActionListener {

    public JConstMorphismType(JMorphismContainer container) {
        this.container = container;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        add(JMorphismDialog.createTitle("f(x) = c")); //$NON-NLS-1$
        add(Box.createVerticalStrut(10));
        simpleEntry = JSimpleEntry.make(container.getCodomain());
        simpleEntry.addActionListener(this);
        add(simpleEntry);
        add(Box.createVerticalStrut(5));
    }
    
    
    public void actionPerformed(ActionEvent e) {
        ModuleElement value = simpleEntry.getValue();
        if (value != null) {
            container.setMorphism(ModuleMorphism.getConstantMorphism(container.getDomain(), value));
        }
        else {
            container.setMorphism(null);
        }
    }
    
    
    public void editMorphism(ModuleMorphism morphism) {
        ModuleElement value = ((ConstantMorphism)morphism).getValue();
        simpleEntry.setValue(value);
    }
    
    
    private JMorphismContainer container;
    private JSimpleEntry       simpleEntry;
}
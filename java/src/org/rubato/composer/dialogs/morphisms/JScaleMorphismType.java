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

package org.rubato.composer.dialogs.morphisms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import org.rubato.composer.components.JMorphismEntry;
import org.rubato.composer.components.JSimpleEntry;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.RingElement;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.ScaledMorphism;

class JScaleMorphismType extends JMorphismType implements ActionListener {

    public JScaleMorphismType(JMorphismContainer container) {
        this.container = container;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(JMorphismDialog.createTitle("f(x) = s * g(x)")); //$NON-NLS-1$
        
        add(Box.createVerticalStrut(10));
        
        morphismEntry = new JMorphismEntry(container.getDomain(), container.getCodomain());
        morphismEntry.addActionListener(this);
        add(morphismEntry);
        
        add(Box.createVerticalStrut(5));
        
        simpleEntry = JSimpleEntry.make(container.getCodomain().getRing());
        add(simpleEntry);
        
        add(Box.createVerticalStrut(5));
        
        Box buttonBox = new Box(BoxLayout.X_AXIS);
        
        JButton applyButton = new JButton(Messages.getString("JMorphismDialog.apply")); //$NON-NLS-1$
        applyButton.addActionListener(this);
        
        buttonBox.add(applyButton);
        
        add(buttonBox);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        ModuleMorphism m = morphismEntry.getMorphism();
        ModuleElement value = simpleEntry.getValue();
        if (m != null && value != null) {
            try {
                container.setMorphism(m.scaled((RingElement)value));
            }
            catch (CompositionException ex) {
                container.setMorphism(null);
            }
        }
        else {
            container.setMorphism(null);
        }
    }
    
    
    public void editMorphism(ModuleMorphism morphism) {
        ScaledMorphism m = (ScaledMorphism)morphism;
        morphismEntry.setMorphism(m.getMorphism());
        simpleEntry.setValue(m.getScalar());
    }
    
    
    private JMorphismEntry morphismEntry;
    private JSimpleEntry   simpleEntry;

    private final JMorphismContainer container;
}
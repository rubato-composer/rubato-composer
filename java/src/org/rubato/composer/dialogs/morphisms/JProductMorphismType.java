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

import org.rubato.composer.components.JMorphismEntry;
import org.rubato.math.module.morphism.CompositionException;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.ProductMorphism;

class JProductMorphismType extends JMorphismType implements ActionListener {

    public JProductMorphismType(JMorphismContainer container) {
        this.container = container;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(JMorphismDialog.createTitle("f(x) = g(x) * h(x)")); //$NON-NLS-1$

        add(Box.createVerticalStrut(10));
        
        firstMorphism = new JMorphismEntry(container.getDomain(), container.getCodomain());
        firstMorphism.addActionListener(this);
        add(firstMorphism);
        
        add(Box.createVerticalStrut(5));
        
        secondMorphism = new JMorphismEntry(container.getDomain(), container.getCodomain());        
        secondMorphism.addActionListener(this);
        
        add(secondMorphism);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        ModuleMorphism first = firstMorphism.getMorphism();
        ModuleMorphism second = secondMorphism.getMorphism();
        if (first != null && second != null) {
            try {
                container.setMorphism(ProductMorphism.make(first, second));                    
            }
            catch (CompositionException ex) {
                container.setMorphism(null);
            }
        }
    }
    
    
    public void editMorphism(ModuleMorphism morphism) {
        firstMorphism.setMorphism(((ProductMorphism)morphism).getFirstMorphism());
        secondMorphism.setMorphism(((ProductMorphism)morphism).getSecondMorphism());
    }
    
    
    private JMorphismEntry firstMorphism;
    private JMorphismEntry secondMorphism;
    
    private final JMorphismContainer container;
}
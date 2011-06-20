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

import org.rubato.composer.components.JMorphismEntry;
import org.rubato.math.module.morphism.*;

class JSumDiffMorphismType extends JMorphismType implements ActionListener {

    public JSumDiffMorphismType(JMorphismContainer container, boolean sum) {
        this.container = container;
        this.sum = sum;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(JMorphismDialog.createTitle("f(x) = g(x) "+(sum?"+":"-")+" h(x)")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

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
                container.setMorphism(sum?first.sum(second):first.difference(second));                    
            }
            catch (CompositionException ex) {
                container.setMorphism(null);
            }
        }
    }
    
    
    public void editMorphism(ModuleMorphism morphism) {
        if (morphism instanceof SumMorphism) {
            sum = true;
            firstMorphism.setMorphism(((SumMorphism)morphism).getFirstMorphism());
            secondMorphism.setMorphism(((SumMorphism)morphism).getSecondMorphism());
        }
        else if (morphism instanceof DifferenceMorphism) {
            sum = false;
            firstMorphism.setMorphism(((DifferenceMorphism)morphism).getFirstMorphism());
            secondMorphism.setMorphism(((DifferenceMorphism)morphism).getSecondMorphism());
        }
    }
    
    
    private boolean        sum;
    private JMorphismEntry firstMorphism;
    private JMorphismEntry secondMorphism;
    
    private final JMorphismContainer container;
}
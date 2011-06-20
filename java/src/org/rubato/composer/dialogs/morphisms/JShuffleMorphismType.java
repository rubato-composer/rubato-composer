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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.rubato.math.module.FreeModule;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.ShuffleMorphism;

public class JShuffleMorphismType
        extends JMorphismType implements ActionListener {

    public JShuffleMorphismType(JMorphismContainer container) {
       this.container = container;
       dim = container.getDomain().getDimension();
       codim = container.getCodomain().getDimension();
       
       setLayout(new BorderLayout());
       
       jshuffle = new JShuffleArea(dim, codim);
       jshuffle.addActionListener(this);
       add(jshuffle, BorderLayout.CENTER);
       
       createMorphism();
    }

    
    public void actionPerformed(ActionEvent e) {
        createMorphism();
    }
    
    
    private void createMorphism() {
        int[] shuffle = jshuffle.getShuffle();
        FreeModule d = (FreeModule)container.getDomain();
        FreeModule c = (FreeModule)container.getCodomain();
        container.setMorphism(ShuffleMorphism.make(d, c, shuffle));
    }
    
    
    public void editMorphism(ModuleMorphism morphism) {
        // currently no editable
    }

    
    private int dim;
    private int codim;
    private JShuffleArea jshuffle;
    private JMorphismContainer container;
}

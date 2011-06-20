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
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;

import org.rubato.composer.components.JMorphismEntry;
import org.rubato.math.module.FreeModule;
import org.rubato.math.module.Module;
import org.rubato.math.module.Ring;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.SplitMorphism;

public class JSplitMorphismType
        extends JMorphismType implements ActionListener {

    public JSplitMorphismType(JMorphismContainer container) {
        this.container = container;
        module = (FreeModule)container.getDomain();
        ring = module.getRing();
        dim = module.getDimension();

        setLayout(new BorderLayout());
        
        splits = new int[] { dim };
        jsplit = new JSplitArea(dim);
        jsplit.addActionListener(this);
        add(jsplit, BorderLayout.CENTER);
        
        morphismBox = Box.createVerticalBox();
        add(morphismBox, BorderLayout.SOUTH);
        
        layoutMorphismPanel();
        createMorphism();
     }

     
     public void actionPerformed(ActionEvent e) {
         if (e.getSource() instanceof JMorphismEntry) {
             createMorphism();
         }
         else {
             splits = jsplit.getSplitLengths();
             layoutMorphismPanel();
         }
     }
     
     
     private void layoutMorphismPanel() {
         morphismBox.removeAll();
         morphismEntries = new JMorphismEntry[splits.length];
         for (int i = 0; i < splits.length; i++) {
             Module domain = ring.getFreeModule(splits[i]);
             morphismEntries[i] = new JMorphismEntry(domain, domain);
             morphismEntries[i].addActionListener(this);
             morphismBox.add(morphismEntries[i]);                 
             morphismBox.add(Box.createVerticalStrut(5));
         }
         container.pack();
     }
     
     
     private void createMorphism() {
         List<ModuleMorphism> morphisms = new LinkedList<ModuleMorphism>();
         for (JMorphismEntry entry : morphismEntries) {
             ModuleMorphism m = entry.getMorphism();
             if (m == null) {
                 container.setMorphism(null);
                 return;
             }
             morphisms.add(m);
         }
         ModuleMorphism morphism = SplitMorphism.make(module, morphisms);
         container.setMorphism(morphism);
     }
     
     
     public void editMorphism(ModuleMorphism morphism) {
         SplitMorphism m = (SplitMorphism)morphism;
         ModuleMorphism[] ms = m.getMorphisms();
         int[] sl = new int[ms.length];
         for (int i = 0; i < ms.length; i++) {
             sl[i] = ms[i].getDomain().getDimension();
         }
         jsplit.setSplitLengths(sl);
         for (int i = 0; i < morphismEntries.length; i++) {
             morphismEntries[i].setMorphism(ms[i]);
         }
     }

     
     private int        dim;
     private int[]      splits;
     private FreeModule module;
     private Ring       ring;
     
     private JSplitArea jsplit;
     private Box        morphismBox;
     
     private JMorphismEntry[]   morphismEntries;
     private JMorphismContainer container;
}

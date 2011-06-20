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

package org.rubato.rubettes.builtin.address;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;

import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.RRing;
import org.rubato.math.module.Ring;

/**
 * 
 * @author Gérard Milmeister
 */
class JGraphSelectDialog extends JDialog implements ActionListener {

    public static JGraphSelect showDialog(Component comp, Ring ring) {
        return showDialog(comp, ring, new LinkedList<ModuleElement>());
    }
    
    
    public static JGraphSelect showDialog(Component comp, Ring ring, List<ModuleElement> elements) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JGraphSelectDialog dialog = new JGraphSelectDialog(frame, ring, elements);
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);
        return dialog.getGraph();
    }

    
    public JGraphSelectDialog(Frame frame, Ring ring, List<ModuleElement> elements) {
        super(frame, Messages.getString("JGraphSelectDialog.selectgraph"), true); //$NON-NLS-1$
        this.ring = ring;
        createLayout(elements);
    }

    
    public JGraphSelect getGraph() {
        return graph;
    }
    
    
    private void createLayout(List<ModuleElement> elements) {
        setLayout(new BorderLayout());
        
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BorderLayout());
        graphPanel.setBorder(paddingBorder);
        
        graph = new JGraphSelect(ring, elements);
        graph.setBorder(lineBorder);
        graph.addActionListener(this);
        graphPanel.add(graph, BorderLayout.CENTER);
        add(graphPanel, BorderLayout.CENTER);
        
        Box bottomBox = new Box(BoxLayout.Y_AXIS);
        
        Box coordBox = new Box(BoxLayout.X_AXIS);
        coordBox.add(Box.createHorizontalStrut(10));
        coordBox.add(new JLabel(" x : ")); //$NON-NLS-1$
        xField = new JTextField("0"); //$NON-NLS-1$
        xField.setEditable(false);
        coordBox.add(xField);
        coordBox.add(Box.createHorizontalStrut(10));
        coordBox.add(new JLabel(" y : ")); //$NON-NLS-1$
        yField = new JTextField("0"); //$NON-NLS-1$
        yField.setEditable(false);
        coordBox.add(yField);
        coordBox.add(Box.createHorizontalStrut(10));
        bottomBox.add(coordBox);
        
        bottomBox.add(Box.createVerticalStrut(10));
        
        Box buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.add(Box.createGlue());
        applyButton = new JButton(Messages.getString("JGraphSelectDialog.apply")); //$NON-NLS-1$
        applyButton.addActionListener(this);
        buttonBox.add(applyButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        cancelButton = new JButton(Messages.getString("JGraphSelectDialog.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(this);
        buttonBox.add(cancelButton);
        buttonBox.add(Box.createGlue());        
        bottomBox.add(buttonBox);
        
        add(bottomBox, BorderLayout.SOUTH);
        
        pack();
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            setVisible(false);
            dispose();
        }
        else if (e.getSource() == cancelButton) {
            graph = null;
            setVisible(false);
            dispose();
        }
        else if (e.getSource() == graph) {
            xField.setText(graph.getCurrentXString());
            xField.setCaretPosition(0);
            yField.setText(graph.getCurrentYString());
            yField.setCaretPosition(0);
        }
    }

    
    private Ring ring;
    
    private JGraphSelect graph;
    private JButton      applyButton;
    private JButton      cancelButton;
    private JTextField   xField;
    private JTextField   yField;

    private Border paddingBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    private Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        showDialog(frame, RRing.ring);
    }
}

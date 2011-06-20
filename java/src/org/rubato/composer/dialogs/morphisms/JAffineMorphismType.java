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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.rubato.composer.Utilities;
import org.rubato.math.arith.Complex;
import org.rubato.math.arith.Rational;
import org.rubato.math.matrix.*;
import org.rubato.math.module.*;
import org.rubato.math.module.morphism.*;

class JAffineMorphismType
        extends JMorphismType
        implements ActionListener, CaretListener, FocusListener {

    public JAffineMorphismType(JMorphismContainer container) {
        this.container = container;
        rows = container.getCodomain().getDimension();
        cols = container.getDomain().getDimension();
        ring = container.getDomain().getRing();
        setLayout(new BorderLayout(0, 5));
        setBorder(Utilities.makeTitledBorder("f(x) = A*x+b"));
        
        // matrix and vector
        JPanel valuesPanel = new JPanel();
        valuesPanel.setLayout(new BorderLayout());
                
        Box matrixVectorBox = new Box(BoxLayout.X_AXIS);
                
        // matrix
        Box matrixBox = new Box(BoxLayout.Y_AXIS);
        JPanel matrixPanel = new JPanel();
        matrixPanel.setLayout(new GridLayout(rows+1, cols+1));
        matrixEntries = new JTextField[rows][cols];
        matrixPanel.add(new JLabel("A", SwingConstants.CENTER));
        for (int j = 0; j < cols; j++) {
            matrixPanel.add(new JLabel(Integer.toString(j+1), SwingConstants.CENTER));
        }
        for (int i = 0; i < rows; i++) {
            matrixPanel.add(new JLabel(Integer.toString(i+1), SwingConstants.CENTER));
            for (int j = 0; j < cols; j++) {
                String s = i==j?"1":"0"; //$NON-NLS-1$ //$NON-NLS-2$
                matrixEntries[i][j] = new JTextField(s, 5);
                matrixEntries[i][j].addCaretListener(this);
                matrixEntries[i][j].addFocusListener(this);
                matrixEntries[i][j].setToolTipText("A["+(i+1)+","+(j+1)+"]");
                matrixPanel.add(matrixEntries[i][j]);
            }
        }
        matrixBox.add(matrixPanel);
        matrixVectorBox.add(matrixBox);
        matrixVectorBox.add(Box.createHorizontalStrut(12));
        
        // vector
        Box vectorBox = new Box(BoxLayout.Y_AXIS);
        JPanel vectorPanel = new JPanel();
        vectorPanel.setLayout(new GridLayout(rows+1, 1));
        vectorPanel.add(new JLabel("b", SwingConstants.CENTER));
        vectorEntries = new JTextField[rows];
        for (int i = 0; i < rows; i++) {
            vectorEntries[i] = new JTextField("0", 5); //$NON-NLS-1$
            vectorEntries[i].addCaretListener(this);
            vectorEntries[i].addFocusListener(this);
            vectorEntries[i].setToolTipText("v["+(i+1)+"]");
            vectorPanel.add(vectorEntries[i]);
        }
        vectorBox.add(vectorPanel);
        matrixVectorBox.add(vectorBox);
        
        boolean horizontalScroll = cols > 15;
        boolean verticalScroll = rows > 20;
        if (horizontalScroll || verticalScroll) {
            JScrollPane scrollPane = new JScrollPane(matrixVectorBox);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setMaximumSize(new Dimension(500, 300));
            scrollPane.setPreferredSize(new Dimension(500, 300));
            valuesPanel.add(scrollPane);
        }
        else {
            valuesPanel.add(matrixVectorBox);
        }
        add(valuesPanel, BorderLayout.CENTER);
        
        // buttons
        if ((ring instanceof RRing || ring instanceof QRing ||
             ring instanceof ZRing || ring instanceof ZnRing ||
             ring instanceof CRing)
            && rows == 2 && cols == 2) {
            graphButton = new JButton(Messages.getString("JAffineMorphism.graphical")); //$NON-NLS-1$
            graphButton.addActionListener(this);
            add(graphButton, BorderLayout.SOUTH);
        }
    }
    
    
    public void focusGained(FocusEvent e) {
        if (e.getSource() instanceof JTextField) {
            ((JTextField)e.getSource()).selectAll();
        }
    }
    
    
    public void focusLost(FocusEvent e) {}

    
    public void caretUpdate(CaretEvent e) {
        apply();
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == graphButton) {            
            JAffineGraph graph = showGraphDialog();
            if (graph != null) {
                if (ring instanceof QRing) {
                    QMatrix m = graph.getQMatrix();
                    Rational[] v = graph.getQVector();
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            setMatrixEntry(i, j, m.get(i, j).toString());
                            
                        }
                        setVectorEntry(i, v[i].toString());
                    }
                    container.setMorphism(QFreeAffineMorphism.make(m, v));
                }
                else if (ring instanceof ZRing) {
                    ZMatrix m = graph.getZMatrix();
                    int[] v = graph.getZVector();
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            setMatrixEntry(i, j, Integer.toString(m.get(i, j)));
                        }
                        setVectorEntry(i, Integer.toString(v[i]));
                    }
                    container.setMorphism(ZFreeAffineMorphism.make(m, v));
                }
                else if (ring instanceof RRing) {
                    RMatrix m = graph.getRMatrix();
                    double[] v = graph.getRVector();
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            setMatrixEntry(i, j, Double.toString(m.get(i, j)));
                        }
                        setVectorEntry(i, Double.toString(v[i]));
                    }
                    container.setMorphism(RFreeAffineMorphism.make(m, v));
                }
                else if (ring instanceof CRing) {
                    CMatrix m = graph.getCMatrix();
                    Complex[] v = graph.getCVector();
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            setMatrixEntry(i, j, m.get(i, j).toString());
                        }
                        setVectorEntry(i, v[i].toString());
                    }
                    container.setMorphism(CFreeAffineMorphism.make(m, v));
                }
                else if (ring instanceof ZnRing) {
                    ZnMatrix m = graph.getZnMatrix();
                    int[] v = graph.getZnVector();
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            setMatrixEntry(i, j, Integer.toString(m.get(i, j)));
                        }
                        setVectorEntry(i, Integer.toString(v[i]));
                    }
                    container.setMorphism(ZnFreeAffineMorphism.make(m, v));
                }
            }
        }
    }
    
    
    private void apply() {
        boolean ok = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String s = matrixEntries[i][j].getText();
                if (ring.parseString(s) == null) {
                    ok = false;
                    matrixEntries[i][j].setBackground(Utilities.ERROR_BG_COLOR);
                }
                else {
                    matrixEntries[i][j].setBackground(Color.WHITE);
                }
            }
            String s = vectorEntries[i].getText();
            if (ring.parseString(s) == null) {
                ok = false;
                vectorEntries[i].setBackground(Utilities.ERROR_BG_COLOR);
            }
            else {
                vectorEntries[i].setBackground(Color.WHITE);
            }
        }
        if (ok) {
            container.setMorphism(createMorphism());
        }
        else {
            container.setMorphism(null);
        }
    }
    
    
    private ModuleMorphism createMorphism() {
        ModuleMorphism morphism = null;
        if (ring instanceof ZRing) {
            int[][] vA = new int[rows][cols];
            int[] b = new int[rows];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    ZElement v = (ZElement)getValue(i, j);
                    vA[i][j] = v.getValue();
                }
                ZElement v = (ZElement)getValue(i);
                b[i] = v.getValue();
            }
            ZMatrix A = new ZMatrix(vA);
            morphism = ZFreeAffineMorphism.make(A, b);
        }
        else if (ring instanceof QRing) {
            Rational[][] vA = new Rational[rows][cols];
            Rational[] b = new Rational[rows];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    QElement v = (QElement)getValue(i, j);
                    vA[i][j] = v.getValue();
                }
                QElement v = (QElement)getValue(i);
                b[i] = v.getValue();
            }
            QMatrix A = new QMatrix(vA);
            morphism = QFreeAffineMorphism.make(A, b);
        }
        else if (ring instanceof RRing) {
            double[][] vA = new double[rows][cols];
            double[] b = new double[rows];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    RElement v = (RElement)getValue(i, j);
                    vA[i][j] = v.getValue();
                }
                RElement v = (RElement)getValue(i);
                b[i] = v.getValue();
            }
            RMatrix A = new RMatrix(vA);
            morphism = RFreeAffineMorphism.make(A, b);
        }
        else if (ring instanceof CRing) {
            Complex[][] vA = new Complex[rows][cols];
            Complex[] b = new Complex[rows];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    CElement v = (CElement)getValue(i, j);
                    vA[i][j] = v.getValue();
                }
                CElement v = (CElement)getValue(i);
                b[i] = v.getValue();
            }
            CMatrix A = new CMatrix(vA);
            morphism = CFreeAffineMorphism.make(A, b);
        }
        else if (ring instanceof ZnRing) {
            int[][] vA = new int[rows][cols];
            int[] b = new int[rows];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    ZnElement v = (ZnElement)getValue(i, j);
                    vA[i][j] = v.getValue();
                }
                ZnElement v = (ZnElement)getValue(i);
                b[i] = v.getValue();
            }
            ZnMatrix A = new ZnMatrix(vA, ((ZnRing)ring).getModulus());
            morphism = ZnFreeAffineMorphism.make(A, b);
        }
        else {
            GenericAffineMorphism m = new GenericAffineMorphism(ring, cols, rows);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {                    
                    RingElement value = getValue(i, j);
                    m.setMatrix(i, j, value);
                }
                RingElement v = getValue(i);
                m.setVector(i, v);
            }
            morphism = m;
        }
        return morphism;
    }
    

    private String getStringValue(int i, int j) {
        return matrixEntries[i][j].getText().trim();                
    }

    
    private String getStringValue(int i) {
        return vectorEntries[i].getText().trim();                
    }

    
    private RingElement getValue(int i, int j) {
        return ring.parseString(getStringValue(i, j));                
    }

    
    private RingElement getValue(int i) {
        return ring.parseString(getStringValue(i));                
    }

    
    private void setMatrixEntry(int i, int j, String s) {
        matrixEntries[i][j].setText(s);
        matrixEntries[i][j].setCaretPosition(0);
    }
    
    
    private void setVectorEntry(int i, String s) {
        vectorEntries[i].setText(s);
        vectorEntries[i].setCaretPosition(0);
    }
    
    
    private JAffineGraph showGraphDialog() {
        if (ring instanceof RRing ||
            ring instanceof ZRing ||
            ring instanceof QRing) {
            boolean qring = ring instanceof QRing;
            double[][] A = new double[2][2];
            double[] b = new double[2];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    try {
                        if (qring) {
                            A[i][j] = Rational.parseRational(getStringValue(i, j)).doubleValue();
                        }
                        else {
                            A[i][j] = Double.parseDouble(getStringValue(i, j));
                        }
                    }
                    catch (NumberFormatException e) {
                        A[i][j] = 0.0;
                    }
                }
                try {
                    if (qring) {
                        b[i] = Rational.parseRational(getStringValue(i)).doubleValue();
                    }
                    else {
                        b[i] = Double.parseDouble(getStringValue(i));
                    }
                }
                catch (NumberFormatException e) {
                    b[i] = 0.0;
                }
            }
            return JAffineGraphDialog.showDialog(this, ring, A, b);
        }
        else {
            return JAffineGraphDialog.showDialog(this, ring);
        }
    }
    
    
    public void editMorphism(ModuleMorphism morphism) {
        if (morphism instanceof ZnAffineMorphism) {
            ZnAffineMorphism m = (ZnAffineMorphism)morphism;
            matrixEntries[0][0].setText(Integer.toString(m.getA()));
            vectorEntries[0].setText(Integer.toString(m.getB()));
        }
        else if (morphism instanceof ZnFreeAffineMorphism) {
            ZnFreeAffineMorphism m = (ZnFreeAffineMorphism)morphism;
            ZnMatrix matrix = m.getMatrix();
            int[] vector = m.getVector();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrixEntries[i][j].setText(Integer.toString(matrix.get(i, j)));
                }
                vectorEntries[i].setText(Integer.toString(vector[i]));
            }
        }
        else if (morphism instanceof ZAffineMorphism) {
            ZAffineMorphism m = (ZAffineMorphism)morphism;
            matrixEntries[0][0].setText(Integer.toString(m.getA()));
            vectorEntries[0].setText(Integer.toString(m.getB()));
        }
        else if (morphism instanceof ZFreeAffineMorphism) {
            ZFreeAffineMorphism m = (ZFreeAffineMorphism)morphism;
            ZMatrix matrix = m.getMatrix();
            int[] vector = m.getVector();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrixEntries[i][j].setText(Integer.toString(matrix.get(i, j)));
                }
                vectorEntries[i].setText(Integer.toString(vector[i]));
            }
        }
        else if (morphism instanceof QAffineMorphism) {
            QAffineMorphism m = (QAffineMorphism)morphism;
            matrixEntries[0][0].setText(m.getA().toString());
            vectorEntries[0].setText(m.getB().toString());
        }
        else if (morphism instanceof QFreeAffineMorphism) {
            QFreeAffineMorphism m = (QFreeAffineMorphism)morphism;
            QMatrix matrix = m.getMatrix();
            Rational[] vector = m.getVector();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrixEntries[i][j].setText(matrix.get(i, j).toString());
                }
                vectorEntries[i].setText(vector[i].toString());
            }
        }
        else if (morphism instanceof RAffineMorphism) {
            RAffineMorphism m = (RAffineMorphism)morphism;
            matrixEntries[0][0].setText(Double.toString(m.getA()));
            vectorEntries[0].setText(Double.toString(m.getB()));
        }
        else if (morphism instanceof RFreeAffineMorphism) {
            RFreeAffineMorphism m = (RFreeAffineMorphism)morphism;
            RMatrix matrix = m.getMatrix();
            double[] vector = m.getVector();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrixEntries[i][j].setText(Double.toString(matrix.get(i, j)));
                }
                vectorEntries[i].setText(Double.toString(vector[i]));
            }
        }
        else if (morphism instanceof CAffineMorphism) {
            CAffineMorphism m = (CAffineMorphism)morphism;
            matrixEntries[0][0].setText(m.getA().toString());
            vectorEntries[0].setText(m.getB().toString());
        }
        else if (morphism instanceof CFreeAffineMorphism) {
            CFreeAffineMorphism m = (CFreeAffineMorphism)morphism;
            CMatrix matrix = m.getMatrix();
            Complex[] vector = m.getVector();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrixEntries[i][j].setText(matrix.get(i, j).toString());
                }
                vectorEntries[i].setText(vector[i].toString());
            }
        }
    }

    
    private int  rows;
    private int  cols;
    private Ring ring;
    
    private JTextField[][]     matrixEntries;
    private JTextField[]       vectorEntries;
    private JMorphismContainer container;
    private JButton            graphButton;
}

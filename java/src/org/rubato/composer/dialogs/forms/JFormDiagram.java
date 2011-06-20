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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.base.RubatoDictionary;
import org.rubato.composer.dialogs.JSelectFormDialog;
import org.rubato.math.yoneda.Form;

public class JFormDiagram
        extends JPanel
        implements ActionListener, ListSelectionListener {

    public JFormDiagram(RubatoDictionary dict) {
        this.dict = dict;
        createLayout();
    }
    
    private void createLayout() {        
        setLayout(new BorderLayout());

        JPanel lists = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        lists.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        
        c.gridx = 0;
        c.gridy = 0;
        JLabel fromLabel = new JLabel(Messages.getString("JFormDiagram.from")+":"); //$NON-NLS-1$ //$NON-NLS-2$
        gridbag.setConstraints(fromLabel, c);
        lists.add(fromLabel);
        
        c.gridx = 2;
        c.gridy = 0;
        JLabel toLabel = new JLabel(Messages.getString("JFormDiagram.to")+":"); //$NON-NLS-1$ //$NON-NLS-2$
        gridbag.setConstraints(toLabel, c);
        lists.add(toLabel);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 1.0;
        fromModel = new FormListModel();
        fromList = new JList(fromModel);
        fromList.setCellRenderer(new FormCellRenderer());
        fromList.addListSelectionListener(this);
        fromList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fromList.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gridbag.setConstraints(fromList, c);
        lists.add(fromList);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
        diagramPanel = new DiagramPanel();
        gridbag.setConstraints(diagramPanel, c);
        lists.add(diagramPanel);
        
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1.0;
        toModel = new FormListModel();
        toList = new JList(toModel);
        toList.setCellRenderer(new FormCellRenderer());
        toList.addListSelectionListener(this);
        toList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toList.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        toList.repaint();
        gridbag.setConstraints(toList, c);
        lists.add(toList);
        
        add(lists, BorderLayout.CENTER);
        
        JPanel buttonBox = new JPanel();
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets.left = 5;
        c.insets.right = 5;
        c.insets.top = 2;
        c.insets.bottom = 2;
        buttonBox.setLayout(gridbag);
        buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 5));
        
        addButton = new JButton(Messages.getString("JFormDiagram.add")); //$NON-NLS-1$
        addButton.setToolTipText(Messages.getString("JFormDiagram.addtip")); //$NON-NLS-1$
        addButton.addActionListener(this);
        gridbag.setConstraints(addButton, c);
        buttonBox.add(addButton);
        
        addNewButton = new JButton(Messages.getString("JFormDiagram.addnew")); //$NON-NLS-1$
        addNewButton.setToolTipText(Messages.getString("JFormDiagram.addnewtip")); //$NON-NLS-1$
        addNewButton.addActionListener(this);
        gridbag.setConstraints(addNewButton, c);
        buttonBox.add(addNewButton);
        
        removeButton = new JButton(Messages.getString("JFormDiagram.remove")); //$NON-NLS-1$
        removeButton.setToolTipText(Messages.getString("JFormDiagram.removetip")); //$NON-NLS-1$
        removeButton.addActionListener(this);
        removeButton.setEnabled(false);
        gridbag.setConstraints(removeButton, c);
        buttonBox.add(removeButton);
        
        upButton = new JButton(Messages.getString("JFormDiagram.up")); //$NON-NLS-1$
        upButton.setToolTipText(Messages.getString("JFormDiagram.uptip")); //$NON-NLS-1$
        upButton.addActionListener(this);
        upButton.setEnabled(false);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(upButton, c);
        buttonBox.add(upButton);

        JLabel labelLabel = new JLabel(Messages.getString("JFormDiagram.label")+": "); //$NON-NLS-1$ //$NON-NLS-2$
        labelLabel.setHorizontalAlignment(JLabel.RIGHT);
        c.gridwidth = 1;
        gridbag.setConstraints(labelLabel, c);
        buttonBox.add(labelLabel);
        
        labelField = new JTextField();
        labelField.setToolTipText(Messages.getString("JFormDiagram.labeltip")); //$NON-NLS-1$
        c.gridwidth = 2;
        gridbag.setConstraints(labelField, c);
        buttonBox.add(labelField);
        

        downButton = new JButton(Messages.getString("JFormDiagram.down"));   //$NON-NLS-1$
        downButton.setToolTipText(Messages.getString("JFormDiagram.downtip")); //$NON-NLS-1$
        downButton.addActionListener(this);
        downButton.setEnabled(false);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(downButton, c);
        buttonBox.add(downButton);
        
        setMorphButton = new JButton(Messages.getString("JFormDiagram.setmorph"));        //$NON-NLS-1$
        setMorphButton.setToolTipText(Messages.getString("JFormDiagram.setmorphtip")); //$NON-NLS-1$
        setMorphButton.addActionListener(this);
        setMorphButton.setEnabled(false);
        c.gridwidth = 2;
        gridbag.setConstraints(setMorphButton, c);
        buttonBox.add(setMorphButton);
        
        removeMorphButton = new JButton(Messages.getString("JFormDiagram.remmorph")); //$NON-NLS-1$
        removeMorphButton.setToolTipText(Messages.getString("JFormDiagram.remmorphtip")); //$NON-NLS-1$
        removeMorphButton.addActionListener(this);
        removeMorphButton.setEnabled(false);
        c.gridwidth = 2;
        gridbag.setConstraints(removeMorphButton, c);
        buttonBox.add(removeMorphButton);
        
        add(buttonBox, BorderLayout.SOUTH);
    }

    
    public void clear() {
        fromModel.clear();
        toModel.clear();
        selectedIndex = -1;
        labelField.setText(""); //$NON-NLS-1$
        updateButtons();
    }
    
    
    public List<Form> getForms() {
        LinkedList<Form> result = new LinkedList<Form>();
        int size = fromModel.getSize();
        for (int i = 0; i < size; i++) {
            FormInfo info = (FormInfo)fromModel.get(i);
            result.add(info.form);
        }
        return result;
    }
    
    
    public List<String> getLabels() {
        LinkedList<String> result = new LinkedList<String>();
        int size = fromModel.getSize();
        boolean hasLabels = false;
        for (int i = 0; i < size; i++) {
            FormInfo info = (FormInfo)fromModel.get(i);
            if (info.label == null) {
                result.add(Integer.toString(i));
            }
            else {
                result.add(info.label);
                hasLabels = true;
            }
        }
        return hasLabels?result:null;
    }
    
    
    public void valueChanged(ListSelectionEvent e) {
        Object src = e.getSource();
        selectedIndex = -1;
        if (!e.getValueIsAdjusting()) {
            if (src == fromList) {
                selectedIndex = fromList.getSelectedIndex();
            }
            else {
                selectedIndex = toList.getSelectedIndex();
            }
            updateButtons();
        }
    }

    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addButton) {
            Form form = JSelectFormDialog.showDialog(this, dict);
            if (form != null) {
                addForm(form);
            }
        }
        else if (src == addNewButton) {
            Form form = null;
            if (dict instanceof TempDictionary) {
                form = JFormDialog.showDialog(this, (TempDictionary)dict, false);
            }
            else {
                form = JFormDialog.showDialog(this, false);
            }
            if (form != null) {
                addForm(form);
            }
        }
        else if (src == removeButton) {
            if (selectedIndex >= 0) {
                removeForm(selectedIndex);
            }
        }
        else if (src == upButton) {
            int i = selectedIndex;
            if (i > 0) {
                Object obj = fromModel.getElementAt(i);
                fromModel.removeElementAt(i);
                toModel.removeElementAt(i);
                fromModel.add(i-1, obj);
                toModel.add(i-1, obj);
                fromList.setSelectedIndex(i-1);
                toList.setSelectedIndex(i-1);
            }
        }
        else if (src == downButton) {
            int i = selectedIndex;
            if (i < fromModel.getSize()-1) {
                Object obj = fromModel.getElementAt(i);
                fromModel.removeElementAt(i);
                toModel.removeElementAt(i);
                fromModel.add(i+1, obj);
                toModel.add(i+1, obj);
                fromList.setSelectedIndex(i+1);
                toList.setSelectedIndex(i+1);
            }
        }
        else if (src == setMorphButton) {
            setMorphism(fromList.getSelectedIndex(), toList.getSelectedIndex());
        }
        else if (src == removeMorphButton) {
            removeMorphism(fromList.getSelectedIndex(), toList.getSelectedIndex());
        }
        updateButtons();
        diagramPanel.repaint(); 
    }

    
    private void addForm(Form form) {
        FormInfo info;
        String label = labelField.getText().trim();
        if (label.length() > 0) {
            info = new FormInfo(form, label);            
        }
        else {
            info = new FormInfo(form, null);            
        }
        fromModel.addForm(info);
        toModel.addForm(info);
        labelField.setText(""); //$NON-NLS-1$
        int size = fromModel.getSize();
        boolean[][] newMorphMatrix = new boolean[size][size];
        for (int i = 0; i <  size-1; i++) {
            for (int j = 0; j < size-1; j++) {
                newMorphMatrix[i][j] = morphMatrix[i][j];
            }
        }
        newMorphMatrix[size-1][size-1] = false;
        morphMatrix = newMorphMatrix;
    }

    
    private void removeForm(int f) {
        fromModel.remove(f);
        toModel.remove(f);
        int size = fromModel.getSize();
        boolean[][] newMorphMatrix = new boolean[size][size];
        for (int i = 0; i <  size-1; i++) {
            for (int j = 0; j < size-1; j++) {
                int i1 = (i >= f)?(i+1):i;
                int j1 = (j >= f)?(j+1):j;
                newMorphMatrix[i][j] = morphMatrix[i1][j1];
            }
        }
        morphMatrix = newMorphMatrix;
    }
    
    
    private void setMorphism(int from, int to) {
        morphMatrix[from][to] = true;
    }
    
    
    private void removeMorphism(int from, int to) {
        morphMatrix[from][to] = false;
    }

    
    private void updateButtons() {
        removeButton.setEnabled(selectedIndex >= 0);
        upButton.setEnabled(selectedIndex > 0);
        downButton.setEnabled(selectedIndex >= 0 && selectedIndex < fromModel.getSize()-1);
        int indexFrom = fromList.getSelectedIndex();
        int indexTo = toList.getSelectedIndex();
        setMorphButton.setEnabled(indexFrom >= 0 && indexTo >= 0);
        removeMorphButton.setEnabled(indexFrom >= 0 && indexTo >= 0 &&
                                     morphMatrix[indexFrom][indexTo]);
    }
    
    
    public Dimension getPreferredSize() {
        return new Dimension(500, 300);
    }
    
    
    class FormListModel extends DefaultListModel {
        public void addForm(FormInfo info) {
            addElement(info);
        }
    }
    
    
    class FormInfo implements Comparable<FormInfo> {
        
        public FormInfo(Form form, String label) {
            this.form = form;
            this.label = label;
            StringBuilder buf = new StringBuilder(30);
            buf.append("<html>"); //$NON-NLS-1$
            if (label != null) {
                buf.append("["); //$NON-NLS-1$
                buf.append(label);
                buf.append("]"); //$NON-NLS-1$
            }
            buf.append("<b>"); //$NON-NLS-1$
            buf.append(form.getNameString());
            buf.append("</b>"); //$NON-NLS-1$
            buf.append(": "); //$NON-NLS-1$
            buf.append("<i>"); //$NON-NLS-1$
            buf.append(form.getTypeString());
            buf.append("</i>"); //$NON-NLS-1$
            buf.append("</html>"); //$NON-NLS-1$
            name = buf.toString();
        }
        
        public int compareTo(FormInfo object) {
            return name.compareTo(object.name);
        }
        
        public String toString() {
            return name;
        }
        
        public Form   form;        
        public String name;
        public String label;
    }

    
    class FormCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
                JList list,
                Object value,            // value to display
                int index,               // cell index
                boolean isSelected,      // is the cell selected
                boolean cellHasFocus)    // the list and the cell have the focus
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setFont(PLAIN_FONT);
            setEnabled(list.isEnabled());
            setText(value.toString());
            setBorder((cellHasFocus)?UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$
            return this;
        }
    }

    
    class DiagramPanel extends JPanel {
        
        public DiagramPanel() {}
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawLines(g2d);
        }
        
        private void drawLines(Graphics2D g) {
            int size = fromModel.getSize();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (morphMatrix[i][j]) {
                        Rectangle fromBounds = fromList.getCellBounds(i, i);
                        Rectangle toBounds = toList.getCellBounds(j, j);
                        int fromY = fromBounds.y+fromBounds.height/2;
                        int toY = toBounds.y+toBounds.height/2;
                        g.drawLine(0, fromY, getWidth(), toY);
                    }
                }
            }
        }
        
        public Dimension getPreferredSize() {
            Dimension s = super.getPreferredSize();
            return new Dimension(DIAGRAM_WIDTH, s.width);
        }
    }

    protected JList         fromList;
    protected JList         toList;
    protected FormListModel fromModel;
    private   FormListModel toModel;
    
    private JButton addButton;
    private JButton addNewButton;
    private JButton removeButton;
    private JButton upButton;
    private JButton downButton;
    private JButton setMorphButton;
    private JButton removeMorphButton;

    private JTextField labelField;
    
    private DiagramPanel diagramPanel;
    
    private RubatoDictionary dict;
    private int selectedIndex = -1;
    
    protected boolean[][] morphMatrix = new boolean[0][0];
    
    private final static int  DIAGRAM_WIDTH = 100;
    protected final static Font PLAIN_FONT = Font.decode("Sans-PLAIN"); //$NON-NLS-1$
}

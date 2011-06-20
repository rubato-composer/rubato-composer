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

package org.rubato.composer.dialogs;

import static org.rubato.composer.Utilities.installEscapeKey;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.base.RubatoDictionary;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.ListForm;
import org.rubato.math.yoneda.PowerForm;

public final class JSelectFormDialog extends JDialog {

    public static Form showDialog(Component comp, RubatoDictionary dict) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JSelectFormDialog dialog = new JSelectFormDialog(frame, comp);
        dialog.setForms(dict.getForms());
        dialog.setVisible(true);
        return dialog.getValue();
    }
    
    
    public static Form showDialog(Component comp, RubatoDictionary dict,
                                  Form baseForm, ArrayList<Integer> types) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JSelectFormDialog dialog = new JSelectFormDialog(frame, comp, types);
        LinkedList<Form> newList = new LinkedList<Form>();
        for (Form f : dict.getForms()) {
            if (types.contains(f.getType())) {
                if (f instanceof PowerForm 
                    && ((PowerForm)f).getForm().equals(baseForm)) {
                    newList.add(f);
                }
                else if (f instanceof ListForm
                         && ((ListForm)f).getForm().equals(baseForm)) {
                    newList.add(f);
                }
            }
        }
        dialog.setForms(newList);
        dialog.setVisible(true);
        return dialog.getValue();
    }

    
    public static Form showDialog(Component comp, RubatoDictionary dict, ArrayList<Integer> types) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JSelectFormDialog dialog = new JSelectFormDialog(frame, comp, types);
        LinkedList<Form> newList = new LinkedList<Form>();
        for (Form f : dict.getForms()) {
            if (types.contains(f.getType())) {
                newList.add(f);
            }
        }
        dialog.setForms(newList);
        dialog.setVisible(true);
        return dialog.getValue();
    }

    
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }
    
    
    private JSelectFormDialog(Frame frame, Component comp) {
        super(frame, Messages.getString("JSelectFormDialog.selectform"), true); //$NON-NLS-1$
        createContents();
        setLocationRelativeTo(comp);
        installEscapeKey(this);
    }
    
    
    private JSelectFormDialog(Frame frame, Component comp, ArrayList<Integer> types) {
        super(frame, Messages.getString("JSelectFormDialog.selectform"), true); //$NON-NLS-1$
        createContents();
        String s = Messages.getString("JSelectFormDialog.type")+": "+Form.typeToString(types.get(0));  //$NON-NLS-1$ //$NON-NLS-2$
        
        for (int i = 1; i < types.size(); i++) {
            s += ", "+Form.typeToString(types.get(i)); //$NON-NLS-1$
        }
        infoLabel.setText(s);
        setLocationRelativeTo(comp);
        installEscapeKey(this);
    }
    
    
    private void createContents() {
        setLayout(new BorderLayout());
        infoLabel = new JLabel();
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(infoLabel, BorderLayout.NORTH);
        
        listModel = new FormListModel();
        formList = new JList(listModel);
        formList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ok();
                }
                super.mouseClicked(e);
            }
        });
        formList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    FormInfo info = (FormInfo)formList.getSelectedValue();
                    setValue(info.form);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(formList);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
        
        cancelButton = new JButton(Messages.getString("JSelectFormDialog.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        buttonPanel.add(cancelButton);
        
        okButton = new JButton(Messages.getString("JSelectFormDialog.ok")); //$NON-NLS-1$
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            } 
        });
        buttonPanel.add(okButton);
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(buttonPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(okButton);
        pack();
        setAlwaysOnTop(true);
        setValue(null);
    }
    
    
    private void setForms(Collection<Form> c) {
        FormInfo[] infoList = new FormInfo[c.size()];
        int i = 0;
        for (Form f : c) {
            infoList[i++] = new FormInfo(f);
        }
        Arrays.sort(infoList);
        listModel.setForms(infoList);
    }
    
    
    protected void setValue(Form f) {
        value = f;
    }
    
    
    private Form getValue() {
        return value;
    }
    
    
    protected void cancel() {
        setValue(null);
        setVisible(false);
    }
    
    
    protected void ok() {
        setVisible(false);        
    }
    
    
    private JLabel    infoLabel;
    protected JList   formList;
    private JButton   cancelButton;
    private JButton   okButton;
    private Form      value;
    private FormListModel listModel;
    
    private static final Dimension PREFERRED_SIZE = new Dimension(200, 300);
    
    class FormListModel extends DefaultListModel {
        
        public void setForms(FormInfo[] infos) {
            for (FormInfo info : infos) {
                addElement(info);
            }
        }
    }
    
    
    class FormInfo implements Comparable<FormInfo> {
        
        public FormInfo(Form f) {
            form = f;
            name = f.getNameString()+": "+f.getTypeString(); //$NON-NLS-1$
        }
        
        public int compareTo(FormInfo object) {
            return name.compareTo(object.name);
        }
        
        public String toString() {
            return name;
        }
        
        public Form   form;        
        public String name;        
    }
}

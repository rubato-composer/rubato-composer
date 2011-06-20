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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.base.RubatoDictionary;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;

public final class JSelectDenotatorDialog extends JDialog {

    public static Denotator showDialog(Component comp, RubatoDictionary dict) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JSelectDenotatorDialog dialog = new JSelectDenotatorDialog(frame, comp);
        dialog.setDenotators(dict.getDenotators());
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);
        return dialog.getValue();
    }
    
    
    public static Denotator showDialog(Component comp, RubatoDictionary dict, int type) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JSelectDenotatorDialog dialog = new JSelectDenotatorDialog(frame, comp, type);
        Collection<Denotator> allDenos = dict.getDenotators();
        ArrayList<Denotator> denoList = new ArrayList<Denotator>();
        for (Denotator d : allDenos) {
            if (d.getType() == type) {
                denoList.add(d);
            }
        }
        dialog.setDenotators(denoList);
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);
        return dialog.getValue();
    }

    
    public static Denotator showDialog(Component comp, RubatoDictionary dict, Form form) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JSelectDenotatorDialog dialog = new JSelectDenotatorDialog(frame, comp, form);
        Collection<Denotator> allDenos = dict.getDenotators();
        ArrayList<Denotator> denoList = new ArrayList<Denotator>();
        for (Denotator d : allDenos) {
            if (d.hasForm(form)) {
                denoList.add(d);
            }
        }
        dialog.setDenotators(denoList);
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);
        return dialog.getValue();
    }

    
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }
    
    
    private JSelectDenotatorDialog(Frame frame, Component comp) {
        super(frame, Messages.getString("JSelectDenotatorDialog.selectdenotator"), true); //$NON-NLS-1$
        createContents();
        setLocationRelativeTo(comp);
        installEscapeKey(this);
    }
    
    
    private JSelectDenotatorDialog(Frame frame, Component comp, int type) {
        super(frame, Messages.getString("JSelectDenotatorDialog.selectdenotator"), true); //$NON-NLS-1$
        createContents();
        infoLabel.setText(Messages.getString("JSelectDenotatorDialog.type")+": "+type); //$NON-NLS-1$ //$NON-NLS-2$
        setLocationRelativeTo(comp);
        installEscapeKey(this);
    }
    
    
    private JSelectDenotatorDialog(Frame frame, Component comp, Form form) {
        super(frame, Messages.getString("JSelectDenotatorDialog.selectdenotator"), true); //$NON-NLS-1$
        createContents();
        infoLabel.setText(Messages.getString("JSelectDenotatorDialog.form")+": "+form.getNameString()); //$NON-NLS-1$ //$NON-NLS-2$
        setLocationRelativeTo(comp);
        installEscapeKey(this);
    }
    
    
    private void createContents() {
        setLayout(new BorderLayout());

        infoLabel = new JLabel(" "); //$NON-NLS-1$
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(infoLabel, BorderLayout.NORTH);
        
        listModel = new DenotatorListModel();
        denotatorList = new JList(listModel);
        denotatorList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ok();
                }
                super.mouseClicked(e);
            }
        });
        denotatorList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    DenotatorInfo info = (DenotatorInfo)denotatorList.getSelectedValue();
                    setValue(info.denotator);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(denotatorList);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
        
        cancelButton = new JButton(Messages.getString("JSelectDenotatorDialog.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        buttonPanel.add(cancelButton);
        
        okButton = new JButton(Messages.getString("JSelectDenotatorDialog.ok")); //$NON-NLS-1$
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
    
    
    private void setDenotators(Collection<Denotator> c) {
        DenotatorInfo[] infoList = new DenotatorInfo[c.size()];
        int i = 0;
        for (Denotator d : c) {
            infoList[i++] = new DenotatorInfo(d);
        }
        Arrays.sort(infoList);
        listModel.setDenotators(infoList);
    }
    
    
    protected void setValue(Denotator d) {
        value = d;
    }
    
    
    private Denotator getValue() {
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
    protected JList   denotatorList;
    private JButton   cancelButton;
    private JButton   okButton;
    private Denotator value;
    private DenotatorListModel listModel;
    
    private static final Dimension PREFERRED_SIZE = new Dimension(250, 350);
    
    class DenotatorListModel extends DefaultListModel {
        
        public void setDenotators(DenotatorInfo[] infos) {
            for (DenotatorInfo info : infos) {
                addElement(info);
            }
        }
    }
    
    
    class DenotatorInfo implements Comparable<DenotatorInfo> {
        
        public DenotatorInfo(Denotator d) {
            denotator = d;
            name = d.getNameString()+":"+d.getForm().getNameString(); //$NON-NLS-1$
        }
        
        public int compareTo(DenotatorInfo object) {
            return name.compareTo(object.name);
        }
        
        public String toString() {
            return name;
        }
        
        public Denotator denotator;        
        public String    name;        
    }
}

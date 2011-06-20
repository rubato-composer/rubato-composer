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

import static org.rubato.composer.Utilities.installEnterKey;
import static org.rubato.composer.Utilities.installEscapeKey;
import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.rubato.base.Repository;
import org.rubato.composer.Utilities;
import org.rubato.composer.components.JStatusline;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.FormReference;


public class JFormDialog
        extends JDialog
        implements ActionListener, CaretListener {

    public static Form showDialog(Component comp, boolean toplevel) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JFormDialog dialog = new JFormDialog(frame, true, toplevel);
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);
        return dialog.getForm();
    }

    
    public static Form showDialog(Component comp, TempDictionary dict, boolean toplevel) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JFormDialog dialog = new JFormDialog(frame, true, dict, toplevel);
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);
        return dialog.getForm();
    }

    
    public JFormDialog(Frame frame, boolean modal, boolean toplevel) {
        this(frame, modal, new TempDictionary(Repository.systemRepository()), toplevel);
    }

    
    public JFormDialog(Frame frame, boolean modal, TempDictionary dict, boolean toplevel) {
        super(frame, Messages.getString("JFormDialog.createform"), modal); //$NON-NLS-1$
        this.dict = dict;
        this.toplevel = toplevel;
        setLayout(new BorderLayout(5, 5));

        JPanel selectTypePanel = new JPanel();
        selectTypePanel.setLayout(new BorderLayout());
        selectTypePanel.setBorder(makeTitledBorder(Messages.getString("JFormDialog.type"))); //$NON-NLS-1$
        selectType = new JComboBox();
        selectType.addItem(simpleType);
        selectType.addItem(limitType);
        selectType.addItem(colimitType);
        selectType.addItem(listType);
        selectType.addItem(powerType);        
        selectType.addActionListener(this);
        selectType.setToolTipText(Messages.getString("JFormDialog.typetip")); //$NON-NLS-1$
        selectTypePanel.add(selectType, BorderLayout.CENTER);
        add(selectTypePanel, BorderLayout.NORTH);

        formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        
        Box bottomBox = new Box(BoxLayout.Y_AXIS);
        bottomBox.setBorder(BorderFactory.createEmptyBorder());

        Box nameBox = new Box(BoxLayout.X_AXIS);
        nameField = new JTextField();
        nameFieldBg = nameField.getBackground();
        nameField.addCaretListener(this);
        nameBox.add(nameField);
        nameBox.setBorder(makeTitledBorder(Messages.getString("JFormDialog.name"))); //$NON-NLS-1$
        nameBox.setToolTipText(Messages.getString("JFormDialog.nametip")); //$NON-NLS-1$
        bottomBox.add(nameBox);
        bottomBox.add(Box.createVerticalStrut(5));
        
        Box infoBox = Box.createHorizontalBox();
        statusline = new JStatusline();
        infoBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        infoBox.add(statusline);
        bottomBox.add(infoBox);
        bottomBox.add(Box.createVerticalStrut(5));
        
        Box buttonBox = new Box(BoxLayout.X_AXIS);
        clearButton = new JButton(Messages.getString("JFormDialog.clear")); //$NON-NLS-1$
        clearButton.setToolTipText(Messages.getString("JFormDialog.cleartip")); //$NON-NLS-1$
        clearButton.addActionListener(this);
        buttonBox.add(clearButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        createButton = new JButton(Messages.getString("JFormDialog.create")); //$NON-NLS-1$
        createButton.setToolTipText(Messages.getString("JFormDialog.createtip")); //$NON-NLS-1$
        createButton.addActionListener(this);
        createButton.setEnabled(false);
        buttonBox.add(createButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        cancelButton = new JButton(Messages.getString("JFormDialog.cancel")); //$NON-NLS-1$
        cancelButton.setToolTipText(Messages.getString("JFormDialog.canceltip")); //$NON-NLS-1$
        cancelButton.addActionListener(this);
        buttonBox.add(cancelButton);
        bottomBox.add(buttonBox);
        add(bottomBox, BorderLayout.SOUTH);
        
        installEscapeKey(this);
        Action enterAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                createButton.doClick();
            }
        };         
        installEnterKey(this, enterAction);

        fillFormPanel();
        pack();
    }

    
    private void fillFormPanel() {
        formPanel.removeAll();
        JComponent comp = null;
        if (type == Form.SIMPLE) {
            SimpleFormEntry entry = new SimpleFormEntry();
            entry.addActionListener(this);
            formEntry = entry;
            comp = entry;
        }
        else if (type == Form.LIMIT || type == Form.COLIMIT) {
            LimitColimitFormEntry entry = new LimitColimitFormEntry(dict, type);
            entry.addActionListener(this);
            formEntry = entry;
            comp = entry;
        }
        else if (type == Form.LIST || type == Form.POWER) {
            ListPowerFormEntry entry = new ListPowerFormEntry(dict, type);            
            entry.addActionListener(this);
            formEntry = entry;
            comp = entry;
        }
        clearInfo();
        formPanel.add(comp, BorderLayout.CENTER);
        updateDialog();
        pack();
    }
    
    
    private void create() {
        String name = getName();
        resultForm = formEntry.getForm(name);
        dict.insertForm(name, resultForm);
        if (toplevel) {
            dict.registerAll(rep);
        }
        setVisible(false);
    }
    
    
    public Form getForm() {
        return resultForm;
    }
    
    
    public String getName() {
        return nameField.getText().trim();
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == selectType) {
            type = ((Type)selectType.getSelectedItem()).type;
            fillFormPanel();
        }
        else if (src == formEntry) {
            updateDialog();
        }
        else if (src == clearButton) {
            clear();
        }
        else if (src == createButton) {
            create();
        }
        else if (src == cancelButton) {
            resultForm = null;
            setVisible(false);
        }
    }
    
    
    public void caretUpdate(CaretEvent e) {
        updateDialog();
    }

    
    public void reset() {
        clear();
    }


    public void clear() {
        resultForm = null;
        formEntry.clear();
        nameField.setText(""); //$NON-NLS-1$
        pack();
    }

    
    private void updateDialog() {
        String name = getName();
        nameField.setBackground(nameFieldBg);
        createButton.setEnabled(false);
        if (name.length() > 0) {
            if (formRef == null) {
                // this is the first time that the name is set
                Form f = dict.getForm(name);
                if (f == null) {
                    // form name is ok
                    // create a reference
                    nameField.setBackground(nameFieldBg);
                    clearInfo();
                    createButton.setEnabled(formEntry.canCreate());
                    formRef = new FormReference(name, type);
                    dict.insertForm(name, formRef);
                }
                else {
                    // form with this name already exists
                    nameField.setBackground(ERROR_BG_COLOR);
                    setInfo(Messages.getString("JFormDialog.formexists")); //$NON-NLS-1$
                }
            }
            else {
                // name has already been set, i.e. reference already exists
                Form f = dict.getForm(name);
                if (f == null) {
                    // this name does not yet exists
                    nameField.setBackground(nameFieldBg);
                    clearInfo();
                    createButton.setEnabled(formEntry.canCreate());
                    dict.removeForm(formRef);
                    formRef.setName(getName());
                    dict.insertForm(name, formRef);
                }
                else if (f == formRef) {
                    // name already exists but form is this reference 
                    nameField.setBackground(nameFieldBg);
                    clearInfo();
                    createButton.setEnabled(formEntry.canCreate());
                }
                else {
                    // form with this name already exists
                    nameField.setBackground(ERROR_BG_COLOR);
                    setInfo(Messages.getString("JFormDialog.formexists")); //$NON-NLS-1$
                }
            }
        }
    }
    
    
    private void setInfo(String text) {
        statusline.setText(text);
    }
    
    
    private void clearInfo() {
        statusline.clear();
    }
    
    
    private static class Type {
        public Type(String s, int t) { typeString = s; type = t; }
        public String toString() { return typeString; }
        String typeString;
        int type;
    }
    

    private final static Type simpleType  = new Type("Simple", Form.SIMPLE);  //$NON-NLS-1$
    private final static Type limitType   = new Type("Limit", Form.LIMIT);  //$NON-NLS-1$
    private final static Type colimitType = new Type("Colimit", Form.COLIMIT);  //$NON-NLS-1$
    private final static Type listType    = new Type("List", Form.LIST);  //$NON-NLS-1$
    private final static Type powerType   = new Type("Power", Form.POWER);  //$NON-NLS-1$
    
    private JTextField  nameField;
    private JStatusline statusline;
    private JButton     clearButton;
    protected JButton   createButton;
    private JButton     cancelButton;
    private JPanel      formPanel;
    private JComboBox   selectType;
    private FormEntry   formEntry = null;
    
    private Color nameFieldBg;
    private final static Color ERROR_BG_COLOR = Utilities.ERROR_BG_COLOR;
    
    private int type = Form.SIMPLE;
    
    private FormReference formRef = null;
    private Form resultForm = null;
    
    private boolean toplevel;
    private TempDictionary dict = null;
    private final static Repository rep = Repository.systemRepository();
}

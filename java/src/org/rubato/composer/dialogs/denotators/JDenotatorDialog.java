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

package org.rubato.composer.dialogs.denotators;

import static org.rubato.composer.Utilities.installEnterKey;
import static org.rubato.composer.Utilities.installEscapeKey;
import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.rubato.base.Repository;
import org.rubato.composer.components.JSelectForm;
import org.rubato.composer.components.JStatusline;
import org.rubato.math.yoneda.*;

public final class JDenotatorDialog
        extends JDialog
        implements ActionListener, CaretListener {

    public static Denotator showDialog(Component comp, boolean nameRequired, Form form) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JDenotatorDialog dialog = new JDenotatorDialog(frame, true, nameRequired, form);
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);
        return dialog.getDenotator();
    }

    
    public static Denotator showDialog(Component comp, boolean nameRequired) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        JDenotatorDialog dialog = new JDenotatorDialog(frame, true, nameRequired, null);
        dialog.setLocationRelativeTo(comp);
        dialog.setVisible(true);
        return dialog.getDenotator();
    }

    
    public JDenotatorDialog(Frame frame, boolean modal, boolean nameRequired) {
        this(frame, modal, nameRequired, null);
    }
    
    
    public JDenotatorDialog(Frame frame, boolean modal, boolean nameRequired, Form form) {
        super(frame, Messages.getString("JDenotatorDialog.createdenotator"), modal); //$NON-NLS-1$
        setLayout(new BorderLayout(5, 5));
        this.form = form;
        this.nameRequired = nameRequired;
        
        if (form == null) {
            selectForm = new JSelectForm(rep);
            selectForm.setBorder(formBorder);
            selectForm.addActionListener(this);
            add(selectForm, BorderLayout.NORTH);
        }
        else {
            JTextField formLabel = new JTextField(form.toString());
            formLabel.setEditable(false);
            formLabel.setBackground(Color.WHITE);
            Box formBox = new Box(BoxLayout.Y_AXIS);
            formBox.add(formLabel);
            formBox.setBorder(formBorder);
            add(formBox, BorderLayout.NORTH);
        }

        denotatorPanel = new JPanel();
        denotatorPanel.setLayout(new BorderLayout());
        add(denotatorPanel, BorderLayout.CENTER);
        
        Box bottomBox = new Box(BoxLayout.Y_AXIS);
        bottomBox.setBorder(BorderFactory.createEmptyBorder());

        nameok = false;
        Box nameBox = new Box(BoxLayout.X_AXIS);
        nameField = new JTextField();
        nameField.addCaretListener(this);
        nameBox.add(nameField);
        nameBox.setBorder(nameBorder);
        bottomBox.add(nameBox);
        bottomBox.add(Box.createVerticalStrut(5));
        
        Box infoBox = Box.createHorizontalBox();
        statusline = new JStatusline();
        infoBox.add(statusline);
        bottomBox.add(infoBox);
        bottomBox.add(Box.createVerticalStrut(5));
        
        // buttons
        Box buttonBox = new Box(BoxLayout.X_AXIS);
        
        clearButton = new JButton(Messages.getString("JDenotatorDialog.clear")); //$NON-NLS-1$
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        buttonBox.add(clearButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        
        createButton = new JButton(Messages.getString("JDenotatorDialog.create")); //$NON-NLS-1$
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                create();
            }
        });
        buttonBox.add(createButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        
        cancelButton = new JButton(Messages.getString("JDenotatorDialog.cancel")); //$NON-NLS-1$
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
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

        fillDenotatorPanel();
        updateButtonState();
        pack();
    }

    
    public Denotator getDenotator() {
        return denotator;
    }
    
    
    public void caretUpdate(CaretEvent e) {
        if (e.getSource() == nameField) {
            nameok = nameField.getText().trim().length() > 0;
            updateButtonState();
        }
    }
    
    
    private void updateButtonState() {        
        createButton.setEnabled(denotatorEntry != null &&
                                denotatorEntry.canCreate() &&
                                (!nameRequired || nameok));
    }
    
    
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == selectForm) {
            form = selectForm.getForm();
            fillDenotatorPanel();
        }
        updateButtonState();
    }
    
    
    private void fillDenotatorPanel() {
        if (form != null) {
            denotator = null;
            JComponent comp = null;
            if (form instanceof SimpleForm) {
                JSimpleDenotatorEntry entry = new JSimpleDenotatorEntry((SimpleForm)form);
                comp = entry;
                denotatorEntry = entry;
            }
            else if (form instanceof LimitForm) {
                JLimitDenotatorEntry entry = new JLimitDenotatorEntry((LimitForm)form);
                comp = entry;
                denotatorEntry = entry;
            }
            else if (form instanceof ColimitForm) {
                JColimitDenotatorEntry entry = new JColimitDenotatorEntry((ColimitForm)form);
                comp = entry;
                denotatorEntry = entry;
            }
            else if (form instanceof PowerForm) {
                JPowerListDenotatorEntry entry = new JPowerListDenotatorEntry((PowerForm)form);
                comp = entry;
                denotatorEntry = entry;
            }
            else if (form instanceof ListForm) {
                JPowerListDenotatorEntry entry = new JPowerListDenotatorEntry((ListForm)form);
                comp = entry;
                denotatorEntry = entry;
            }
            denotatorEntry.addActionListener(this);
            clearInfo();
            denotatorPanel.removeAll();
            denotatorPanel.add(comp, BorderLayout.CENTER);
            pack();
        }
        else {
            clear();
        }
        updateButtonState();
    }
    
    
    protected void create() {
        denotator = null;
        if (denotatorEntry != null) {
            String name = nameField.getText().trim();
            if (name.length() > 0) {
                // denotator has been given a name
                denotator = denotatorEntry.getDenotator(name);
                if (denotator == null) {
                    setInfo(Messages.getString("JDenotatorDialog.nocreatedenotator")); //$NON-NLS-1$
                }
                else {
                    if (rep.getDenotator(NameDenotator.make(name)) != null) {
                        denotator = null;
                        setInfo(Messages.getString("JDenotatorDialog.nameexists"));                             //$NON-NLS-1$
                    }
                    else {
                        denotator = rep.register(denotator);
                    }
                }
            }
            else if (nameRequired) {
                // denotator must have a name but has not been given one
                denotator = null;
                setInfo(Messages.getString("JDenotatorDialog.musthavename"));                             //$NON-NLS-1$
            }
            else {
                // anonymous denotator
                denotator = denotatorEntry.getDenotator(null);
                if (denotator == null) {
                    setInfo(Messages.getString("JDenotatorDialog.nocreatedenotator")); //$NON-NLS-1$
                }
            }
        }
        if (denotator != null) {
            setVisible(false);
        }
    }
    
    
    public void reset() {
        clear();
    }
    
    
    public void clear() {
        if (selectForm != null) {
            selectForm.clear();
            form = null;
            denotatorPanel.removeAll();
            selectForm.clear();
            denotatorEntry = null;
        }
        else {
            denotatorEntry.clear();
        }
        clearInfo();
        nameField.setText(""); //$NON-NLS-1$
        updateButtonState();
        pack();
    }
    
    
    private void setInfo(String text) {
        statusline.setText(text);
    }
    
    
    private void clearInfo() {
        statusline.clear();
    }
    
    
    private JSelectForm selectForm = null;
    private JTextField  nameField;
    private JStatusline statusline;
    private JButton     clearButton;
    protected JButton   createButton;
    private JButton     cancelButton;
    private JPanel      denotatorPanel;
    private Form        form;
    private Denotator   denotator;
    private boolean     nameRequired;
    private boolean     nameok;

    private DenotatorEntry denotatorEntry = null;

    private final static Border formBorder = makeTitledBorder(Messages.getString("JDenotatorDialog.form")); //$NON-NLS-1$
    private final static Border nameBorder = makeTitledBorder(Messages.getString("JDenotatorDialog.name")); //$NON-NLS-1$

    private final static Repository rep = Repository.systemRepository();
}

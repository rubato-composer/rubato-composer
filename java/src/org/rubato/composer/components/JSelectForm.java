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

package org.rubato.composer.components;

import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import org.rubato.base.RubatoDictionary;
import org.rubato.composer.dialogs.JSelectFormDialog;
import org.rubato.composer.dialogs.forms.JFormDialog;
import org.rubato.composer.dialogs.forms.TempDictionary;
import org.rubato.math.yoneda.Form;


public class JSelectForm extends JPanel implements ActionListener {

    public JSelectForm(RubatoDictionary dict) {
        this.dict = dict;
        createLayout();
    }

    
    public JSelectForm(RubatoDictionary dict, int ... types) {
        this.types = new ArrayList<Integer>();
        for (int i = 0; i < types.length; i++) {
            this.types.add(types[i]);
        }
        this.dict = dict;
        createLayout();
    }
    

    public JSelectForm(RubatoDictionary dict, ArrayList<Integer> types) {
        this.types = types;
        this.dict = dict;
        createLayout();
    }
    

    public Form getForm() {
        return form;
    }
    
    
    public void setForm(Form f) {
        form = f;
        if (f != null) {
            formLabel.setText(f.getNameString()+": "+f.getTypeString()); //$NON-NLS-1$
        }
        else {
            formLabel.clear();
        }
    }
    
    
    public void setBaseForm(Form baseForm) {
        this.baseForm = baseForm;
    }
    
    
    public void clear() {
        setForm(null);
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        selectButton.setEnabled(enabled);
        formLabel.setEnabled(enabled);
    }
    
    
    public void disableBorder() {
        setBorder(null);
        repaint();
    }
    
    
    private void createLayout() {
        setLayout(new BorderLayout(5, 0));
        setBorder(makeTitledBorder(FORM));
        formLabel = new JLabelField();
        formLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectForm();
            }
        });
        add(formLabel, BorderLayout.CENTER);
        
        Box buttonBox = Box.createHorizontalBox();        
        buttonBox.add(Box.createHorizontalStrut(5));
        if (dict instanceof TempDictionary) {
            createButton = new JButton(CREATE_BUTTON);
            createButton.setToolTipText(CREATE_BUTTON_TIP);
            createButton.addActionListener(this);
            buttonBox.add(createButton);
            buttonBox.add(Box.createHorizontalStrut(5));
        }
        selectButton = new JButton(SELECT_BUTTON);
        selectButton.setToolTipText(SELECT_BUTTON_TIP);
        selectButton.addActionListener(this);
        buttonBox.add(selectButton);
        
        add(buttonBox, BorderLayout.EAST);
    }
    

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == selectButton) {
            selectForm();
        }
        else if (src == createButton && dict instanceof TempDictionary) {
            Frame frame = JOptionPane.getFrameForComponent(this);
            JFormDialog dialog = new JFormDialog(frame, true, (TempDictionary)dict, false);
            dialog.setVisible(true);
            Form res = dialog.getForm();
            if (res != null) {
                form = res;
                formLabel.setText(res.getNameString()+": "+res.getTypeString()); //$NON-NLS-1$
                fireActionEvent();
            }
        }
    }
    
    
    protected void selectForm() {
        Form res;
        if (baseForm != null) {
            res = JSelectFormDialog.showDialog(this, dict, baseForm, types);
        }
        else if (types != null) {
            res = JSelectFormDialog.showDialog(this, dict, types);
        }
        else {
            res = JSelectFormDialog.showDialog(this, dict);
        }
        if (res != null) {
            form = res;
            formLabel.setText(res.getNameString()+": "+res.getTypeString()); //$NON-NLS-1$
            fireActionEvent();
        }
    }
    
    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    
    protected void fireActionEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ActionListener.class) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 0, ""); //$NON-NLS-1$
                }
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }

    
    private EventListenerList listenerList = new EventListenerList();
    private ActionEvent       actionEvent  = null;
    
    private JLabelField formLabel;
    private JButton     selectButton;
    private JButton     createButton;
    
    private Form               form     = null;
    private Form               baseForm = null;
    private ArrayList<Integer> types    = null;
    
    private RubatoDictionary dict = null;
    
    private final static String FORM = Messages.getString("JSelectForm.form"); //$NON-NLS-1$
    private final static String CREATE_BUTTON = Messages.getString("JSelectForm.create"); //$NON-NLS-1$
    private final static String CREATE_BUTTON_TIP = Messages.getString("JSelectForm.createtip"); //$NON-NLS-1$
    private final static String SELECT_BUTTON = Messages.getString("JSelectForm.select"); //$NON-NLS-1$}
    private final static String SELECT_BUTTON_TIP = Messages.getString("JSelectForm.selecttip"); //$NON-NLS-1$
}
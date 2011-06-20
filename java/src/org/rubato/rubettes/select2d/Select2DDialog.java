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

package org.rubato.rubettes.select2d;

import static org.rubato.composer.Utilities.NULL_STRING;
import static org.rubato.composer.Utilities.getWindow;
import static org.rubato.composer.Utilities.makeTitledBorder;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.rubato.base.Repository;
import org.rubato.composer.components.JSelectForm;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;

class Select2DDialog
        extends JPanel
        implements ActionListener {

    public Select2DDialog() {
        select2DPanels = new ArrayList<Select2DPanel>(); 
        createLayout();
    }
    
    
    public void setDenotators(ArrayList<Denotator> denotators) {
        this.denotators = denotators;
        for (Select2DPanel panel : select2DPanels) {
            panel.setDenotators(denotators);
        }
    }
    
    
    public void getSelectedDenotators(List<Denotator> selected, List<Denotator> notSelected) {
        int s = select2DPanels.size();
        int j = 0;
        for (Denotator d : denotators) {
            boolean isSelected = true;
            for (int i = 0; i < s; i++) {
                if (!select2DPanels.get(i).contains(j)) {
                    isSelected = false;
                    break;
                }
            }
            if (isSelected) {
                selected.add(d);
            }
            else {
                notSelected.add(d);
            }
            j++;
        }
    }
    
    
    public void setForm(Form form) {
        if (this.form != form &&
            (form.getType() == Form.POWER || form.getType() == Form.LIST)) {
            this.form = form;
            selectForm.setForm(form);
            removeAllSelections();
            updateState();
        }
    }
    
    
    public Form getForm() {
        return form;
    }
    
    
    public boolean hasSelections() {
        for (Select2DPanel panel : select2DPanels) {
            if (panel.hasSelections()) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<Select2DPanel> getSelections() {
        return select2DPanels;
    }
    
    
    public void setSelections(ArrayList<Select2DPanel> selections) {
        removeAllSelections();
        this.select2DPanels = selections;
        for (Select2DPanel panel : selections) {
            selectionPane.addTab(NULL_STRING, panel);
        }
        Window window = getWindow(this);
        if (window != null) {
            window.pack();
        }
        updateState();
    }
    
    
    private void createLayout() {
        setLayout(new BorderLayout());
        
        Box topBox = Box.createHorizontalBox();
        topBox.add(Box.createHorizontalGlue());
        selectForm = new JSelectForm(rep, Form.LIST, Form.POWER);
        selectForm.setBorder(makeTitledBorder(SELECTFORM_TITLE));
        selectForm.addActionListener(this);
        topBox.add(selectForm);
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.setBorder(makeTitledBorder(BUTTONBOX_TITLE));
        addSelectionButton = new JButton(ADDSEL_BUTTON);
        addSelectionButton.addActionListener(this);
        buttonBox.add(addSelectionButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        removeSelectionButton = new JButton(REMOVESEL_BUTTON);
        removeSelectionButton.addActionListener(this);
        buttonBox.add(removeSelectionButton);
        
        topBox.add(buttonBox);
        topBox.add(Box.createHorizontalGlue());
        
        add(topBox, BorderLayout.NORTH);
        
        selectionPane = new JTabbedPane();
        add(selectionPane, BorderLayout.CENTER);
        
        updateState();
    }


    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == selectForm) {
            Form f = selectForm.getForm();
            if (f != form) {
                form = f;
                removeAllSelections();
                getWindow(this).pack();
            }
        }
        else if (src == addSelectionButton) {
            Select2DPanel panel = new Select2DPanel(form);
            panel.setDenotators(denotators);
            select2DPanels.add(panel);
            selectionPane.addTab(NULL_STRING, panel);
            selectionPane.setSelectedIndex(selectionPane.getTabCount()-1);
            if (selectionPane.getTabCount() == 1) {
                getWindow(this).pack();
            }
        }
        else if (src == removeSelectionButton) {
            int i = selectionPane.getSelectedIndex();
            select2DPanels.remove(i);
            selectionPane.removeTabAt(i);            
            if (selectionPane.getTabCount() == 0) {
                getWindow(this).pack();
            }
        }
        updateState();
    }
    
    
    public void updateState() {
        addSelectionButton.setEnabled(form != null);
        removeSelectionButton.setEnabled(form != null && selectionPane.getTabCount() > 0);
        for (int i = 0; i < selectionPane.getTabCount(); i++) {
            selectionPane.setTitleAt(i, SELTAB_TITLE+(i+1));
        }
    }
    
    
    private void removeAllSelections() {
        selectionPane.removeAll();
        select2DPanels = new ArrayList<Select2DPanel>();
    }
    
    
    private Form        form;
    private JSelectForm selectForm;
    private JButton     addSelectionButton;
    private JButton     removeSelectionButton;
    private JTabbedPane selectionPane;
    
    private ArrayList<Select2DPanel> select2DPanels;
    private ArrayList<Denotator> denotators;
    
    private static Repository rep = Repository.systemRepository();

    private static final String SELECTFORM_TITLE = Messages.getString("Select2DDialog.inputform"); //$NON-NLS-1$
    private static final String BUTTONBOX_TITLE  = Messages.getString("Select2DDialog.selections"); //$NON-NLS-1$
    private static final String ADDSEL_BUTTON    = Messages.getString("Select2DDialog.add"); //$NON-NLS-1$
    private static final String REMOVESEL_BUTTON = Messages.getString("Select2DDialog.remove"); //$NON-NLS-1$
    private static final String SELTAB_TITLE     = Messages.getString("Select2DDialog.seltabtitle"); //$NON-NLS-1$
}

/*
 * file     $RCSfile: ModuleListView.java,v $
 * @author  $Author: milmei $
 * @version $Revision: 1.9 $ $Date: 2008/12/06 21:12:13 $ 
 *
 * this file is part of the rubato project
 *
 * copyright (c) 2002 gérard milmeister
 * department of computer science / university of zurich
 */

package org.rubato.composer.denobrowser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.Ring;
import org.rubato.math.module.RingElement;
import org.rubato.math.yoneda.SimpleDenotator;


/**
 * @author Gérard Milmeister
 */
public class ModuleListView 
	extends ListView
	implements ListSelectionListener {

    public ModuleListView(SimpleDenotator p, int l) {
        parent = p;
        moduleElements = getModuleElements(parent);
        layoutPanel();
        setLevel(l);
    }

    public ModuleListView(int l) {
    	layoutPanel();
    	setLevel(l);
    }
        
    private void layoutPanel() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.ipadx = 4;
        c.ipady = 4;
        formLabel = new JLabel(" "); //$NON-NLS-1$
        formLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formLabel.setBorder(BorderFactory.createEtchedBorder());
        gridbag.setConstraints(formLabel, c);
        add(formLabel);

        typeLabel = new JLabel(" "); //$NON-NLS-1$
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setBorder(BorderFactory.createEtchedBorder());
        gridbag.setConstraints(typeLabel, c);
        add(typeLabel);
    
        moduleValues = getModuleValues(moduleElements);
        
        list = new JList(moduleValues);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        list.addMouseListener(this);
        JScrollPane scrollPane = new JScrollPane(list);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        gridbag.setConstraints(scrollPane, c);
        add(scrollPane);
    }
    
	public void valueChanged(ListSelectionEvent e) {
		if (list.getSelectedIndex() >= 0 && e.getValueIsAdjusting() == false) {
			setLabels(parent.getElement().getComponent(list.getSelectedIndex()).getModule().toString());
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
			if (list.getSelectedIndex() >= 0) {
				editValue(moduleElements[list.getSelectedIndex()]);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (list.getSelectedIndex() >= 0) {
				popupMenu(e.getX(), e.getY(), list.getSelectedIndex());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("edit")) { //$NON-NLS-1$
			editValue(moduleElements[list.getSelectedIndex()]);
		}
	}
	
	private void setLabels(String moduleName) {
		typeLabel.setText(moduleName);
	}
	
	private ModuleElement[] getModuleElements(SimpleDenotator p) {
		ModuleElement el = p.getElement();
		int len = el.getLength();
		ModuleElement[] elements = new ModuleElement[len];
		for (int i = 0; i < len; i++) {
			elements[i] = el.getComponent(i);			
		}
		return elements;
	}
	
	private String[] getModuleValues(ModuleElement[] elements) {
		int len = elements.length;
		String[] values = new String[len];
		for (int i = 0; i < len; i++) {
			ModuleElement el = elements[i];
			if (el instanceof RingElement) {
				values[i] = ((RingElement)el).stringRep();
			}
			else {
				values[i] = el.toString();
			}
		}
		return values;
	}
	
	private ModuleElement editValue(ModuleElement el) {
        ModuleElement resEl = null;
		if (el instanceof RingElement) {
			String res = JOptionPane.showInputDialog(this, Messages.getString("ModuleListView.valueforelement"), ((RingElement)el).stringRep()); //$NON-NLS-1$
			if (res != null) {
				resEl = ((Ring)el.getModule()).parseString(res);				
			}
		}
        return resEl;
	}
	
	private void popupMenu(int x, int y, int index) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem;
    	menuItem = new JMenuItem(Messages.getString("ModuleListView.edit")); //$NON-NLS-1$
		menuItem.addActionListener(this);
	    menuItem.setActionCommand("edit"); //$NON-NLS-1$
		popup.add(menuItem);		
		popup.show(list, x, y);
	}
	
	private JList list;
    private JLabel typeLabel;
    private JLabel formLabel;
    private String[] moduleValues;
    private ModuleElement[] moduleElements;
    private SimpleDenotator parent;
}

/*
 * file     $RCSfile: DenoListView.java,v $
 * @author  $Author: milmei $
 * @version $Revision: 1.10 $ $Date: 2007/01/04 20:58:29 $ 
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
import java.util.Collection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rubato.base.Repository;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleForm;


/**
 * @author Gérard Milmeister
 */
public class DenoListView 
	extends ListView
	implements ListSelectionListener {

    public DenoListView(Denotator p, Denotator[] denos, int l) {
        denotators = denos;
        parent = p;
        layoutPanel();
        setLevel(l);
    }

    public DenoListView(int l) {
    	denotators = new Denotator[0];
    	layoutPanel();
    	setLevel(l);
    }
        
    public void clear() {
    	denotators = new Denotator[0];
    	denoNames = new String[0];
    	parent = null;
    	list.setListData(denoNames);
    	formLabel.setText(" "); //$NON-NLS-1$
    	typeLabel.setText(" "); //$NON-NLS-1$
    }

    public void setDenotators(Denotator p, Denotator[] denos) {
    	clear();
    	denotators = denos;
        denoNames = getDenoNames(denotators);
        parent = p;
    	list.setListData(denoNames);
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
    
        denoNames = getDenoNames(denotators);
        
        list = new JList(denoNames);
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
			setLabels(denotators[list.getSelectedIndex()]);
			if (listener != null) {
				listener.valueChanged(getLevel(), denotators[list.getSelectedIndex()]);
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
			if (listener != null && list.getSelectedIndex() >= 0) {
				listener.doubleClicked(getLevel(), denotators[list.getSelectedIndex()]);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (list.getSelectedIndex() >= 0) {
				popupMenu(e.getX(), e.getY(), denotators[list.getSelectedIndex()]);
			}
			else if (parent != null) {
				popupMenuParent(e.getX(), e.getY());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("clear")) { //$NON-NLS-1$
			System.out.println("clear"); //$NON-NLS-1$
		}
		else if (cmd.equals("edit")) { //$NON-NLS-1$
			System.out.println("edit"); //$NON-NLS-1$
		}
		else if (cmd.equals("set")) { //$NON-NLS-1$
			System.out.println("set"); //$NON-NLS-1$
		}
		else if (cmd.equals("add_new")) { //$NON-NLS-1$
			listener.addNew(getLevel(), parent);
		}
		else if (cmd.equals("add_existing")) { //$NON-NLS-1$
			System.out.println("add_existing"); //$NON-NLS-1$
		}
	}

	private String[] getDenoNames(Denotator[] denos) {
		String[] names = new String[denos.length];
		for (int i = 0; i < names.length; i++) {
			String name = denos[i].getNameString();
			if (name.length() == 0) {
                name = Messages.getString("DenoListView.anonymous"); //$NON-NLS-1$
            }
			names[i] = name;
		}
		return names;
	}
	
	private void setLabels(Denotator d) {
		formLabel.setText(d.getForm().getNameString());
		String typeName = d.getForm().getTypeString();
		if (d.getForm().getType() == Form.SIMPLE) {
			typeName += ": "+((SimpleForm)d.getForm()).getModule(); //$NON-NLS-1$
		}
		typeLabel.setText(typeName);
	}
	
	private void popupMenu(int x, int y, Denotator d) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem;
		if (d.getForm().getType() == Form.SIMPLE) {
			menuItem = new JMenuItem(Messages.getString("DenoListView.edit")); //$NON-NLS-1$
			menuItem.addActionListener(this);
			menuItem.setActionCommand("edit"); //$NON-NLS-1$
			popup.add(menuItem);
		}
		else {
			menuItem = new JMenuItem(Messages.getString("DenoListView.clear")); //$NON-NLS-1$
			menuItem.addActionListener(this);
			menuItem.setActionCommand("clear"); //$NON-NLS-1$
			popup.add(menuItem);

			menuItem = new JMenuItem(Messages.getString("DenoListView.set")); //$NON-NLS-1$
			menuItem.addActionListener(this);
			menuItem.setActionCommand("set"); //$NON-NLS-1$
			popup.add(menuItem);
		}
		menuItem = new JMenuItem(Messages.getString("DenoListView.register")); //$NON-NLS-1$
		menuItem.addActionListener(this);
		menuItem.setActionCommand("register"); //$NON-NLS-1$
		popup.add(menuItem);

		popup.show(list, x, y);
	}
	
	private void popupMenuParent(int x, int y) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem;
		Form form = parent.getForm();
		switch (form.getType()) {
			case Form.POWER:
			case Form.LIST: {
				Form subForm = form.getForm(0);
				menuItem = new JMenuItem(Messages.getString("DenoListView.addnew")+subForm.getNameString()); //$NON-NLS-1$
				menuItem.addActionListener(this);
				menuItem.setActionCommand("add_new"); //$NON-NLS-1$
				popup.add(menuItem);
				menuItem = new JMenuItem(Messages.getString("DenoListView.addexisting")+subForm.getNameString()); //$NON-NLS-1$
				menuItem.addActionListener(this);
				menuItem.setActionCommand("add_existing"); //$NON-NLS-1$
				popup.add(menuItem);
			}
		}
		popup.show(list, x, y);
	}

	private JList list;
    private JLabel typeLabel;
    private JLabel formLabel;
    private String[] denoNames;
    private Denotator[] denotators;
    private Denotator parent;
    
    public static void main(String[] args) {
    	Repository rep = Repository.systemRepository();
    	Collection<Denotator> coll = rep.getDenotators();
    	Denotator[] denos = new Denotator[coll.size()];
    	coll.toArray(denos);
    	
    	DenoListView lv = new DenoListView(null, denos, 0);
        JFrame frame = new JFrame("Test ListView"); //$NON-NLS-1$
        frame.getContentPane().add(lv);
        frame.pack();
        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

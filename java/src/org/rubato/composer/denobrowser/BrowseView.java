/*
 * file     $RCSfile: BrowseView.java,v $
 * @author  $Author: milmei $
 * @version $Revision: 1.15 $ $Date: 2008/12/06 21:12:13 $ 
 *
 * this file is part of the rubato project
 *
 * copyright (c) 2002 gérard milmeister
 * department of computer science / university of zurich
 */

package org.rubato.composer.denobrowser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.rubato.base.Repository;
import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.*;

public class BrowseView 
	extends JPanel 
	implements 
		ListViewListener, 
		AdjustmentListener {
	
	public BrowseView(Denotator[] denos) {
		visibleLevels = 3;
		listViews = new ListView[30];
		listViews[0] = new DenoListView(null, denos, 0);
		listViews[0].setListViewListener(this);
		for (int i = 1; i < visibleLevels; i++) {
			listViews[i] = new ListView();
		}
		for (int i = visibleLevels; i < 30; i++) { 
			listViews[i] = null;
		}
		minLevel = 0;
		maxLevel = 0;
		layoutPanel();
		updateScrollbar();
	}

	private void layoutPanel() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 1.0;

		browsePanel = new JPanel();
		updateBrowsePanel();
		gridbag.setConstraints(browsePanel, c);
		add(browsePanel);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0.0;
		scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		scrollBar.setUnitIncrement(1);
		scrollBar.setBlockIncrement(1);
		scrollBar.setMinimum(0);
		scrollBar.setMaximum(0);
		scrollBar.setVisibleAmount(1);
		scrollBar.addAdjustmentListener(this);
		gridbag.setConstraints(scrollBar, c);		
		add(scrollBar);
	}
		
	private void updateBrowsePanel() {
		browsePanel.removeAll();
		browsePanel.setLayout(new GridLayout(1, visibleLevels));
		for (int i = minLevel; i < minLevel+visibleLevels; i++) {
			browsePanel.add(listViews[i]);
		}
		browsePanel.validate();
	}
	
	public void doubleClicked(int level, Denotator d) {
		System.out.println(d);
	}
	
	public void valueChanged(int level, Denotator d) {
		for (int i = level+1; i <= maxLevel; i++)
			listViews[i] = new ListView();
		if (d.getForm().getType() != Form.SIMPLE) {
			Denotator[] denos = makeDenoList(d);
			maxLevel = level+1;
			listViews[maxLevel] = new DenoListView(d, denos, maxLevel);
			listViews[maxLevel].setListViewListener(this);
			if (minLevel+visibleLevels-1 < maxLevel) {
				minLevel = maxLevel-minLevel-visibleLevels+1;
			}
			updateBrowsePanel();
			updateScrollbar();
		}
		else {
			maxLevel = level+1;
			listViews[maxLevel] = new ModuleListView((SimpleDenotator)d, maxLevel);
			listViews[maxLevel].setListViewListener(this);
			updateBrowsePanel();
			updateScrollbar();
		}
	}
	
	public void addNew(int level, Denotator d) {
	    try {
	        Form form = d.getForm().getForm(0);
	        Denotator newDeno = form.createDefaultDenotator();
	        ((FactorDenotator)d).appendFactor(newDeno);
	        Denotator[] denos = makeDenoList(d);
	        listViews[maxLevel] = new DenoListView(d, denos, maxLevel);
	        listViews[maxLevel].setListViewListener(this);
	        updateBrowsePanel();
	    }
	    catch (RubatoException e) {
	        e.printStackTrace();
	    }
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int value = e.getValue();
		if (minLevel != value) {
			minLevel = value;
			updateBrowsePanel();
		}
	}
	
	private void updateScrollbar() {
		scrollBar.setValue(minLevel);
		if (scrollBar.getMaximum() != maxLevel) {
			scrollBar.setMaximum(maxLevel);
			scrollBar.setVisibleAmount(1);
		}
	}

	private Denotator[] makeDenoList(Denotator d) {
		Form form = d.getForm();
		Denotator[] denos = null;
		switch (form.getType()) {
			case Form.LIMIT: {
				denos = new Denotator[((FactorDenotator)d).getFactorCount()];
				((FactorDenotator)d).getFactors().toArray(denos);
				break;
			}
			case Form.COLIMIT: {
				denos = new Denotator[((FactorDenotator)d).getFactorCount()];
				((FactorDenotator)d).getFactors().toArray(denos);
				break;
			}
			case Form.POWER: 
			case Form.LIST: {
				denos = new Denotator[((FactorDenotator)d).getFactorCount()];
				((FactorDenotator)d).getFactors().toArray(denos);
				break;
			}
		}
		return denos;
	}
	
	private ListView[] listViews;
	private int minLevel, maxLevel;
	private int visibleLevels;
	private JPanel browsePanel;
	private JScrollBar scrollBar;
	
    public static void main(String[] args) {
    	Repository rep = Repository.systemRepository();
    	Collection<Denotator> coll = rep.getDenotators();
    	Denotator[] denos = new Denotator[coll.size()];
    	coll.toArray(denos);
    	
    	Form scoreForm = rep.getForm("Score"); //$NON-NLS-1$
    	Form noteForm = rep.getForm("Note"); //$NON-NLS-1$
    	Denotator score1 = scoreForm.createDefaultDenotator();
    	score1.setNameString("Score1"); //$NON-NLS-1$
    	Denotator note1 = noteForm.createDefaultDenotator();
    	note1.setNameString("Note1"); //$NON-NLS-1$
    	denos = new Denotator[] { score1, note1 };
    	
    	BrowseView view = new BrowseView(denos);
        JFrame frame = new JFrame("Test BrowseView"); //$NON-NLS-1$
        frame.setContentPane(view);
        frame.pack();
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

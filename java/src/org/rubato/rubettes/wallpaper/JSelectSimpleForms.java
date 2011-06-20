/*
 * Copyright (C) 2006 Florian Thalmann
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

package org.rubato.rubettes.wallpaper;

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.rubato.composer.Utilities;
import org.rubato.math.yoneda.SimpleForm;
import org.rubato.math.module.Module;
import org.rubato.math.module.morphism.ModuleMorphism;

/**
 * A JDialog for selecting the SimpleForms to be transformed by a morphism. For all domain
 * and codomain dimensions, one of the rubette's input denotator's simple subforms can be selected.
 * The selected subforms are represented as a list of paths relative to the rubette's input form.
 * 
 * @author Florian Thalmann
 */
@SuppressWarnings("serial")
public class JSelectSimpleForms extends JDialog implements ActionListener {
	
	private WallpaperRubette rubette;
    private List<JComboBox> simpleFormBoxes;
	
	private JPanel domainPanel;
	private JPanel codomainPanel;
	private JButton okButton;
    
	private ModuleMorphism morphism;
	private int domainDimension;
	private int codomainDimension;
	
	/**
	 * Creates a JSelectSimpleForms for the specified WallpaperRubette and ModuleMorphism.
	 * 
	 * @param rubette - the rubette from which the simple subforms are to be taken.
	 * @param morphism - the morphism for which the transformed forms are to be selected.
	 */
	public JSelectSimpleForms(WallpaperRubette rubette, ModuleMorphism morphism) {
		super(rubette.getPropertiesFrame(), "Simple forms", true);
		this.rubette = rubette;
        this.createLayout();
		this.setMorphism(morphism);
    }
	
	/*
	 * creates the components shown in the dialog's content pane
	 */
	private void createLayout() {
		this.simpleFormBoxes = new ArrayList<JComboBox>();
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		this.domainPanel = new JPanel();
		this.domainPanel.setBorder(Utilities.makeTitledBorder("Domain"));
		this.codomainPanel = new JPanel();
		this.codomainPanel.setBorder(Utilities.makeTitledBorder("Codomain"));
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(this);
		contentPane.add(this.domainPanel, BorderLayout.NORTH);
		contentPane.add(this.codomainPanel, BorderLayout.CENTER);
		contentPane.add(this.okButton, BorderLayout.SOUTH);
		this.pack();
    }
	
	/**
	 * Replaces the current morphism by the specified one and updates the dialog's components.
	 * 
	 * @param morphism - the new morphism, the transformed simple forms of which are to be specified.
	 */
	public void setMorphism(ModuleMorphism morphism) {
		if (morphism != null && !morphism.equals(this.morphism)) {
			this.morphism = morphism;
			this.setDimension(morphism.getDomain().getDimension(), morphism.getCodomain().getDimension());
		}
	}
	
	/*
	 * sets the total number of dimensions, i.e. the number of forms to be selected
	 */
	private void setDimension(int domainDimension, int codomainDimension) {
		if (domainDimension != this.domainDimension || codomainDimension != this.codomainDimension) {
			this.domainDimension = domainDimension;
			this.codomainDimension = codomainDimension;
			this.updateLayout();
		}
	}
	
	/*
	 * returns the sum of domain and codomain dimensions
	 */
	private int getTotalDimension() {
		return this.domainDimension + codomainDimension;
	}
	
	/**
	 * Sets the selected subforms and updates the combo boxes.
	 * 
	 * @param paths a list of the subform's paths relative to the input form.
	 */
	public void setElements(List<List<Integer>> paths) {
		List<List<Integer>> simpleFormPaths = this.rubette.getSimpleFormPaths();
		int numberOfPaths = paths.size();
		if (numberOfPaths <= this.getTotalDimension()) {
			//iterate through paths and update formBoxes
			for (int i = 0; i < numberOfPaths; i++) {
				List<Integer> currentPath = paths.get(i);
				this.simpleFormBoxes.get(i).setSelectedIndex(simpleFormPaths.indexOf(currentPath));
			}
		}
	}
	
	/**
	 * Returns the currently selected subforms in the combo boxes.
	 */
	public List<List<Integer>> getElements() {
		List<List<Integer>> elementPaths = new ArrayList<List<Integer>>();
		List<List<Integer>> simpleFormPaths = this.rubette.getSimpleFormPaths();
		
		int numberOfFormBoxes = this.getNumberOfFormBoxes();
		for (int i = 0; i < numberOfFormBoxes; i++) {
			elementPaths.add(simpleFormPaths.get(this.simpleFormBoxes.get(i).getSelectedIndex()));
		}
		if (elementPaths.size() != numberOfFormBoxes || numberOfFormBoxes == 0) {
			return null;
		}
		return elementPaths;
	}
    
    /*
	 * creates the combo boxes with the subform's names and updates the domain and codomain
	 * panels
	 */
	private void updateLayout() {
		this.simpleFormBoxes = new ArrayList<JComboBox>();
		String[] simpleFormNames = this.getFormStrings(this.rubette.getSimpleForms());
		for (int i = 0; i < this.getTotalDimension(); i++) {
			this.simpleFormBoxes.add(new JComboBox(simpleFormNames));
		}
		this.updateDomainPanel();
		this.updateCodomainPanel();
		this.resetSimpleFormBoxes();
		this.pack();
	}
	
	/*
	 * returns an array with the names of the forms in the specified list
	 */
	private String[] getFormStrings(List<SimpleForm> forms) {
		int formCount = forms.size();
		String[] formStrings = new String[formCount];
		for (int i = 0; i < formCount; i++) {
			formStrings[i] = forms.get(i).toString();
		}
		return formStrings;
	}
	
	/*
	 * updates the panel with the combo boxes for selecting the domain subforms
	 */
	private void updateDomainPanel() {
		this.domainPanel.removeAll();
		this.domainPanel.setLayout(new GridLayout(this.domainDimension, 1));
		for (int i = 0; i < this.domainDimension; i++) {
			this.domainPanel.add(this.createLabeledFormBoxPanel(i));
		}
	}
	
	/*
	 * updates the panel with the combo boxes for selecting the codomain subforms
	 */
	private void updateCodomainPanel() {
		this.codomainPanel.removeAll();
		this.codomainPanel.setLayout(new GridLayout(this.codomainDimension, 1));
		for (int i = this.domainDimension; i < this.domainDimension + this.codomainDimension; i++) {
			this.codomainPanel.add(this.createLabeledFormBoxPanel(i));
		}
	}
	
	/*
	 * returns a new panel with a combo box for selecting a form. the box is labelled with the
	 * name of the domain or codomain component module specified with 'index'   
	 */
	private JPanel createLabeledFormBoxPanel(int index) {
		JPanel formBoxPanel = new JPanel();
		Module module;
		if (index < this.domainDimension) {
			module = this.morphism.getDomain().getComponentModule(index);
		} else {
			module = this.morphism.getCodomain().getComponentModule(index-this.domainDimension);
		}
		formBoxPanel.add(new JLabel(module.toVisualString()));
		formBoxPanel.add(this.simpleFormBoxes.get(index));
		return formBoxPanel;
	}
	
	/*
	 * resets the values selected in the subform combo boxes. both for domain and codomain, the
	 * i-th value is set to the rubette's i-th subform
	 */
	private void resetSimpleFormBoxes() {
		for (int i = 0; i < this.simpleFormBoxes.size(); i++) {
			int index = i;
			if (index >= this.domainDimension) {
				index -= this.domainDimension;
			}
			this.simpleFormBoxes.get(i).setSelectedIndex(index);
		}
	}
	
	/**
	 * Processes the events coming from the ok button.
	 */
	public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.okButton) {
            this.setVisible(false);
        }
    }
	
	//for testing...
	protected int getNumberOfFormBoxes() {
		return this.simpleFormBoxes.size();
	}

}

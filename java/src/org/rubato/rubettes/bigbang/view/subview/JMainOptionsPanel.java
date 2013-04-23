package org.rubato.rubettes.bigbang.view.subview;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.rubato.base.Repository;
import org.rubato.composer.Utilities;
import org.rubato.composer.components.JSelectForm;
import org.rubato.math.yoneda.ColimitForm;
import org.rubato.math.yoneda.Form;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.InputActiveAction;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.util.SpringUtilities;

public class JMainOptionsPanel extends JPanel implements ActionListener, View {
	
	private ViewController viewController;
	private BigBangController bigBangController;
	private JCheckBox inputActiveCheckBox;
	private JSelectForm selectFormPanel;
	private JViewParametersScrollPane viewParametersScrollPane;
	private JPanel colimitSelectionPanel;
	private List<JComboBox> colimitBoxes;
	
	public JMainOptionsPanel(ViewController controller, BigBangController bigBangController, ViewParameters viewParameters) {
		this.viewController = controller;
		this.bigBangController = bigBangController;
		this.setLayout(new BorderLayout(0,0));
		this.initNorthernPanel(controller);
		this.initCenterPanel(controller, viewParameters);
		this.initSouthernPanel();
	}
	
	private void initNorthernPanel(ViewController controller) {
		JPanel inputActivePanel = new JPanel();
		inputActivePanel.setBorder(Utilities.makeTitledBorder("Input active"));
		this.inputActiveCheckBox = new JCheckBox(new InputActiveAction(controller));
		inputActivePanel.add(this.inputActiveCheckBox);
		this.selectFormPanel = new JSelectForm(Repository.systemRepository());
		this.selectFormPanel.addActionListener(this);
		
		JPanel northernPanel = new JPanel();
		northernPanel.setLayout(new BorderLayout(0,0));
		northernPanel.add(inputActivePanel, BorderLayout.NORTH);
		northernPanel.add(this.selectFormPanel, BorderLayout.CENTER);
		this.add(northernPanel, BorderLayout.NORTH);
	}
	
	private void initCenterPanel(ViewController controller, ViewParameters viewParameters) {
		this.viewParametersScrollPane = new JViewParametersScrollPane(controller, viewParameters);
		controller.addView(this.viewParametersScrollPane);
		this.add(this.viewParametersScrollPane, BorderLayout.CENTER);
	}
	
	private void initSouthernPanel() {
		this.colimitBoxes = new ArrayList<JComboBox>();
		this.colimitSelectionPanel = new JPanel();
		this.colimitSelectionPanel.setLayout(new SpringLayout());
		this.colimitSelectionPanel.setBorder(Utilities.makeTitledBorder("Select colimit"));
		this.add(this.colimitSelectionPanel, BorderLayout.SOUTH);
	}
	
	private void initColimitBoxes(List<ColimitForm> colimits) {
		this.colimitBoxes.clear();
		this.colimitSelectionPanel.removeAll();
		for (ColimitForm currentColimit : colimits) {
			this.colimitSelectionPanel.add(new JLabel(currentColimit.getNameString()));
			JComboBox currentBox = new JComboBox(new Vector<Form>(currentColimit.getForms()));
			currentBox.addActionListener(this);
			this.colimitBoxes.add(currentBox);
			this.colimitSelectionPanel.add(currentBox);
		}
		SpringUtilities.makeCompactGrid(this.colimitSelectionPanel, colimits.size(), 2, 0, 0, 0, 0);
	}
	
	private void updateColimitBoxes(List<Integer> selectedCoordinates) {
		for (int i = 0; i < this.colimitBoxes.size(); i++) {
			int currentIndex = selectedCoordinates.get(i);
			this.colimitBoxes.get(i).setEnabled(currentIndex >= 0);
			this.colimitBoxes.get(i).setSelectedIndex(currentIndex);
		}
	}

	@SuppressWarnings("unchecked")
	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.INPUT_ACTIVE)) {
			this.inputActiveCheckBox.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.MAIN_OPTIONS_VISIBLE)) {
			this.setVisible((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.FORM)) {
			this.selectFormPanel.setForm(((DisplayObjectList)event.getNewValue()).getBaseForm());
			this.initColimitBoxes(((DisplayObjectList)event.getNewValue()).getTopDenotatorColimits());
			this.revalidate();
		} else if (propertyName.equals(ViewController.SELECTED_COLIMIT_COORDINATE)) {
			this.updateColimitBoxes(((List<Integer>)event.getNewValue()));
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.selectFormPanel)) {
			this.bigBangController.setForm(this.selectFormPanel.getForm());
		} else if (this.colimitBoxes.contains(event.getSource())) {
			JComboBox colimitBox = (JComboBox)event.getSource();
			int colimitIndex = this.colimitBoxes.indexOf(colimitBox);
			int coordinateIndex = colimitBox.getSelectedIndex();
			this.viewController.setSelectedColimitCoordinate(colimitIndex, coordinateIndex);
		}
	}
	
}

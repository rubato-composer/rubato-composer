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
	private JPanel drawingOptionsPanel;
	private JComboBox objectBox;
	private JComboBox satelliteLevelBox;
	private List<JLabel> colimitBoxLabels;
	private List<JComboBox> colimitBoxes;
	
	public JMainOptionsPanel(ViewController controller, BigBangController bigBangController, ViewParameters viewParameters) {
		this.viewController = controller;
		this.bigBangController = bigBangController;
		this.viewController.addView(this);
		this.bigBangController.addView(this);
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
		this.colimitBoxLabels = new ArrayList<JLabel>();
		this.colimitBoxes = new ArrayList<JComboBox>();
		this.drawingOptionsPanel = new JPanel();
		this.drawingOptionsPanel.setLayout(new SpringLayout());
		this.drawingOptionsPanel.setBorder(Utilities.makeTitledBorder("Drawing options"));
		this.add(this.drawingOptionsPanel, BorderLayout.SOUTH);
	}
	
	private void initDrawingOptionsPanelComponents(List<Form> objects, List<ColimitForm> colimits) {
		//TODO: only if more than one object to draw!?
		if (!objects.isEmpty()) {
			this.objectBox = new JComboBox(this.generateNameVector(objects));
			this.objectBox.addActionListener(this);
		} else {
			this.objectBox = null;
		}
		
		this.colimitBoxLabels.clear();
		this.colimitBoxes.clear();
		for (ColimitForm currentColimit : colimits) {
			this.colimitBoxLabels.add(new JLabel(currentColimit.getNameString()));
			JComboBox currentBox = new JComboBox(this.generateNameVector(currentColimit.getForms()));
			currentBox.addActionListener(this);
			this.colimitBoxes.add(currentBox);
		}
		this.updateDrawingOptionsPanel();
	}
	
	private void updateDrawingOptionsPanel() {
		this.drawingOptionsPanel.removeAll();
		int numberOfBoxes = 0;
		
		if (this.objectBox != null) {
			this.drawingOptionsPanel.add(new JLabel("Object"));
			this.drawingOptionsPanel.add(this.objectBox);
			numberOfBoxes++;
		}
		//TODO: ADD SEPARATOR!!
		if (this.satelliteLevelBox != null) {
			this.drawingOptionsPanel.add(new JLabel("Level"));
			this.drawingOptionsPanel.add(this.satelliteLevelBox);
			numberOfBoxes++;
		}
		//TODO: ADD SEPARATOR!!
		for (int i = 0; i < this.colimitBoxes.size(); i++) {
			this.drawingOptionsPanel.add(this.colimitBoxLabels.get(i));
			this.drawingOptionsPanel.add(this.colimitBoxes.get(i));
			numberOfBoxes++;
		}
		SpringUtilities.makeCompactGrid(this.drawingOptionsPanel, numberOfBoxes, 2, 0, 0, 0, 0);
		this.revalidate();
	}
	
	private Vector<String> generateNameVector(List<Form> forms) {
		Vector<String> names = new Vector<String>();
		for (Form currentObjectForm : forms) {
			names.add(currentObjectForm.getNameString());
		}
		return names;
	}
	
	private void updateSatelliteLevelBox(Integer maxSatelliteLevel) {
		if (maxSatelliteLevel != null) {
			Vector<Integer> availableLevels = new Vector<Integer>();
			for (int i = 0; i <= maxSatelliteLevel+1; i++) {
				availableLevels.add(i);
			}
			int previousSelection = 0;
			if (this.satelliteLevelBox != null && this.satelliteLevelBox.getSelectedIndex() <= maxSatelliteLevel) {
				previousSelection = this.satelliteLevelBox.getSelectedIndex();
			}
			this.satelliteLevelBox = new JComboBox(availableLevels);
			this.satelliteLevelBox.setSelectedIndex(previousSelection);
			this.satelliteLevelBox.addActionListener(this);
		} else {
			this.satelliteLevelBox = null; 
		}
		this.updateDrawingOptionsPanel();
	}
	
	private void updateColimitBoxes(List<Integer> selectedCoordinates) {
		if (selectedCoordinates.size() == this.colimitBoxes.size()) {
			for (int i = 0; i < this.colimitBoxes.size(); i++) {
				int currentIndex = selectedCoordinates.get(i);
				this.colimitBoxes.get(i).setEnabled(currentIndex >= 0);
				this.colimitBoxes.get(i).setSelectedIndex(currentIndex);
			}
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
			DisplayObjects displayObjects = (DisplayObjects)event.getNewValue();
			this.selectFormPanel.setForm(displayObjects.getBaseForm());
			this.initDrawingOptionsPanelComponents(displayObjects.getObjectTypes(), displayObjects.getActiveObjectType().getColimits());
			this.updateSatelliteLevelBox(displayObjects.getMaxSatelliteLevelOfActiveObject());
		} else if (propertyName.equals(ViewController.ACTIVE_OBJECT)) {
			this.objectBox.setSelectedIndex((Integer)event.getNewValue());
		} else if (propertyName.equals(ViewController.ACTIVE_COLIMIT_COORDINATE)) {
			this.updateColimitBoxes(((List<Integer>)event.getNewValue()));
		} else if (propertyName.equals(ViewController.MAX_SATELLITE_LEVEL)) {
			this.updateSatelliteLevelBox(((Integer)event.getNewValue()));
		} else if (propertyName.equals(ViewController.ACTIVE_SATELLITE_LEVEL)) {
			this.satelliteLevelBox.setSelectedIndex(((Integer)event.getNewValue()));
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.selectFormPanel)) {
			this.bigBangController.setForm(this.selectFormPanel.getForm());
		} else if (this.objectBox.equals(event.getSource())) {
			this.viewController.setActiveObject(this.objectBox.getSelectedIndex());
		} else if (this.colimitBoxes.contains(event.getSource())) {
			JComboBox colimitBox = (JComboBox)event.getSource();
			int colimitIndex = this.colimitBoxes.indexOf(colimitBox);
			int coordinateIndex = colimitBox.getSelectedIndex();
			this.viewController.setActiveColimitCoordinate(colimitIndex, coordinateIndex);
		} else if (event.getSource().equals(this.satelliteLevelBox)) {
			this.viewController.setActiveSatelliteLevel(this.satelliteLevelBox.getSelectedIndex());
		}
	}
	
}

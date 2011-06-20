package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.ViewParameter;

public class JViewParameterPanel extends JPanel implements ItemListener, ChangeListener {
	
	private int index;
	private ViewController controller;
	private JLabel nameLabel;
	private JCheckBox manualDenotatorLimitsBox;
	private JSpinner denotatorMin, denotatorMax;
	private JCheckBox relativeBox;
	private JSpinner parameterMin, parameterMax;
	private JCheckBox cyclicBox; 
	
	public JViewParameterPanel(int index, ViewController controller) {
		this.index = index;
		this.controller = controller;
		this.nameLabel = new JLabel();
		this.nameLabel.setPreferredSize(new Dimension(70, 25));
		this.add(this.nameLabel);
		this.manualDenotatorLimitsBox = new JCheckBox();
		this.manualDenotatorLimitsBox.addItemListener(this);
		this.add(this.manualDenotatorLimitsBox);
		this.add(new JLabel("denotator min"));
		this.denotatorMin = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1.0));
		this.denotatorMin.addChangeListener(this);
		this.add(this.denotatorMin);
		this.add(new JLabel("max"));
		this.denotatorMax = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1.0));
		this.denotatorMax.addChangeListener(this);
		this.add(this.denotatorMax);
		this.add(new JLabel("|"));
		this.relativeBox = new JCheckBox();
		this.relativeBox.addItemListener(this);
		this.add(this.relativeBox);
		this.add(new JLabel("parameter min"));
		this.parameterMin = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1.0));
		this.parameterMin.addChangeListener(this);
		this.add(this.parameterMin);
		this.add(new JLabel("max"));
		this.parameterMax = new JSpinner(new SpinnerNumberModel(0, -500, 500, 1.0));
		this.parameterMax.addChangeListener(this);
		this.add(this.parameterMax);
		this.add(new JLabel("cyclic"));
		this.cyclicBox = new JCheckBox();
		this.cyclicBox.addItemListener(this);
		this.add(this.cyclicBox);
	}
	
	public void updateValues(ViewParameter parameter) {
		this.nameLabel.setText(parameter.getName()+":");
		this.updateManualDenotatorLimits(parameter.manualDenotatorLimits());
		this.denotatorMin.setValue(parameter.getMinDenotatorValue());
		this.denotatorMax.setValue(parameter.getMaxDenotatorValue());
		this.updateRelativeParameterLimits(parameter.isRelative());
		this.parameterMin.setValue(parameter.getMinGoalValue());
		this.parameterMax.setValue(parameter.getMaxGoalValue());
		this.cyclicBox.setSelected(parameter.isCyclic());
	}
	
	private void updateManualDenotatorLimits(boolean manualDenotatorLimits) {
		this.manualDenotatorLimitsBox.setSelected(manualDenotatorLimits);
		this.denotatorMin.setEnabled(manualDenotatorLimits);
		this.denotatorMax.setEnabled(manualDenotatorLimits);
	}
	
	private void updateRelativeParameterLimits(boolean relative) {
		this.relativeBox.setSelected(relative);
		this.parameterMin.setEnabled(relative);
		this.parameterMax.setEnabled(relative);
		this.cyclicBox.setEnabled(relative);
	}

	public void stateChanged(ChangeEvent event) {
		JSpinner source = (JSpinner)event.getSource();
		if (source == this.denotatorMin || source == this.denotatorMax) {
			this.changeDenotatorMinAndMax();
		} else if (source == this.parameterMin || source == this.parameterMax) {
			this.changeParameterMinAndMax();
		}
	}
	
	public void itemStateChanged(ItemEvent event) {
		JCheckBox source = (JCheckBox)event.getSource();
		if (source == this.manualDenotatorLimitsBox) {
			this.changeDenotatorMinAndMax();
		} else if (source == this.relativeBox || source == this.cyclicBox) {
			this.changeParameterMinAndMax();
		}
	}
	
	private void changeDenotatorMinAndMax() {
		boolean manual = this.manualDenotatorLimitsBox.isSelected();
		double denotatorMin = ((Number)this.denotatorMin.getValue()).doubleValue();
		double denotatorMax = ((Number)this.denotatorMax.getValue()).doubleValue();
		this.controller.changeDenotatorMinAndMax(this.index, manual, denotatorMin, denotatorMax);
	}

	private void changeParameterMinAndMax() {
		boolean relative = this.relativeBox.isSelected();
		double parameterMin = ((Number)this.parameterMin.getValue()).doubleValue();
		double parameterMax = ((Number)this.parameterMax.getValue()).doubleValue();
		boolean cyclic = this.cyclicBox.isSelected();
		this.controller.changeParameterMinAndMax(this.index, relative, parameterMin, parameterMax, cyclic);
	}

}

package org.rubato.rubettes.bigbang.view.subview;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.rubato.base.Repository;
import org.rubato.composer.Utilities;
import org.rubato.composer.components.JSelectForm;
import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.InputActiveAction;

public class JMainOptionsPanel extends JPanel implements ActionListener, View {
	
	private BigBangController bigBangController;
	private JCheckBox inputActiveCheckBox;
	private JSelectForm selectFormPanel;
	
	public JMainOptionsPanel(ViewController controller, BigBangController bigBangController) {
		this.bigBangController = bigBangController;
		this.setLayout(new BorderLayout(0,0));
		JPanel inputActivePanel = new JPanel();
		inputActivePanel.setBorder(Utilities.makeTitledBorder("Input active"));
		this.inputActiveCheckBox = new JCheckBox(new InputActiveAction(controller));
		inputActivePanel.add(this.inputActiveCheckBox);
		this.selectFormPanel = new JSelectForm(Repository.systemRepository());
		this.selectFormPanel.addActionListener(this);
		this.add(inputActivePanel, BorderLayout.NORTH);
		this.add(this.selectFormPanel, BorderLayout.CENTER);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.INPUT_ACTIVE)) {
			this.inputActiveCheckBox.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.MAIN_OPTIONS_VISIBLE)) {
			this.setVisible((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.DISPLAY_NOTES)) {
			this.selectFormPanel.setForm(((DisplayObjectList)event.getNewValue()).getBaseForm());
			this.revalidate();
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this.selectFormPanel)) {
			this.bigBangController.setForm(this.selectFormPanel.getForm());
		}
	}
	
}

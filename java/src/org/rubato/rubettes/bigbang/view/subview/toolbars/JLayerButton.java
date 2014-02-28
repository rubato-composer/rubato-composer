package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.BigBangLayer;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class JLayerButton extends JButton implements ActionListener {
	
	private int index;
	private ViewController controller;
	private BigBangController bbController;
	private JTextField nameField;
	private JButton activeButton, audibleButton, visibleButton;
	
	public JLayerButton(int index, ViewController controller, BigBangController bbController) {
		this.index = index;
		this.controller = controller;
		this.bbController = bbController;
		this.init();
	}
	
	private void init() {
		this.addActionListener(this);
		this.setLayout(new FlowLayout());
		this.nameField = new JTextField(Integer.toString(this.index));
		this.nameField.addActionListener(this);
		this.nameField.setColumns(3);
		this.add(this.nameField);
		this.activeButton = this.createButton("S");
		this.audibleButton = this.createButton("A");
		this.visibleButton = this.createButton("V");
		this.setMaximumSize(new Dimension(150,40));
	}
	
	public void update(BigBangLayer layer) {
		this.nameField.setText(layer.getName());
		this.nameField.setCaretPosition(0);
		this.activeButton.setSelected(layer.isActive());
		this.audibleButton.setSelected(layer.isAudible());
		this.visibleButton.setSelected(layer.isVisible());
		this.activeButton.setEnabled(layer.isVisible());
		this.setEnabled(layer.isActive());
	}
	
	private JButton createButton(String name) {
		JButton button = new JButton(name);
		button.addActionListener(this);
		this.add(button);
		return button;
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(this)) {
			this.controller.setObjectsOnLayerSelected(this.index, !this.isSelected());
		} else if (event.getSource().equals(this.nameField)) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
			this.bbController.setLayerName(this.index, this.nameField.getText());
		} else if (event.getSource().equals(this.activeButton)) {
			this.bbController.setLayerActive(this.index, !this.activeButton.isSelected());
		} else if (event.getSource().equals(this.audibleButton)) {
			this.bbController.setLayerAudible(this.index, !this.audibleButton.isSelected());
		} else if (event.getSource().equals(this.visibleButton)) {
			this.bbController.setLayerVisible(this.index, !this.visibleButton.isSelected());
		}
	}

}

package org.rubato.rubettes.bigbang.view.subview;

import java.beans.PropertyChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rubato.rubettes.bigbang.view.View;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.general.InputActiveAction;

public class JMainOptionsPanel extends JPanel implements View {
	
	JCheckBox inputActiveCheckBox;
	
	public JMainOptionsPanel(ViewController controller) {
		this.add(new JLabel("Input active"));
		this.inputActiveCheckBox = new JCheckBox(new InputActiveAction(controller));
		this.add(this.inputActiveCheckBox);
	}

	public void modelPropertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(ViewController.INPUT_ACTIVE)) {
			this.inputActiveCheckBox.setSelected((Boolean)event.getNewValue());
		} else if (propertyName.equals(ViewController.MAIN_OPTIONS_VISIBLE)) {
			this.setVisible((Boolean)event.getNewValue());
		}
	}
	
}

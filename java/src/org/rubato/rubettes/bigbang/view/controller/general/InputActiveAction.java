package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class InputActiveAction extends AbstractAction {
	
	private ViewController controller;
	
	public InputActiveAction(ViewController controller) {
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.changeInputActive(((AbstractButton)event.getSource()).isSelected());
	}

}

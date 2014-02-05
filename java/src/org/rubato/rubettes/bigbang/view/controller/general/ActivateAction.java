package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class ActivateAction extends AbstractAction {

	ViewController controller;
	
	public ActivateAction(ViewController controller) {
		this.controller = controller;
	}
	
	public void actionPerformed(ActionEvent event) {
		this.controller.activateAllObjects();
	}

}

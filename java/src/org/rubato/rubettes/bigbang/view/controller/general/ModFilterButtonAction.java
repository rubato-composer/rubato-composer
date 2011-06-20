package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class ModFilterButtonAction extends AbstractAction {
	
	private ViewController controller;
	
	public ModFilterButtonAction(ViewController controller) {
		super("ModFilter");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.toggleModFilter();
	}
}
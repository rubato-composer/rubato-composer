package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class AddWindowAction extends AbstractAction {
	
	private ViewController controller;
	
	public AddWindowAction(ViewController controller) {
		super("Win");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.addNewWindow();
	}

}

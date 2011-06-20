package org.rubato.rubettes.bigbang.view.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ToggleMainOptionsAction extends AbstractAction {
	
	private ViewController controller;
	
	public ToggleMainOptionsAction(ViewController controller) {
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.toggleMainOptionsVisible();
	}

}

package org.rubato.rubettes.bigbang.view.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;


public class ToggleViewParametersAction extends AbstractAction {
	
	private ViewController controller;
	
	public ToggleViewParametersAction(ViewController controller) {
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.toggleViewParametersVisible();
	}

}

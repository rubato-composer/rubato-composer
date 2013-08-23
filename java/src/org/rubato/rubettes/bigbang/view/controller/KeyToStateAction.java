package org.rubato.rubettes.bigbang.view.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class KeyToStateAction extends AbstractAction {
	
	private ViewController controller;
	private int state;
	
	public KeyToStateAction(ViewController controller, int state) {
		this.controller = controller;
		this.state = state;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.selectCompositionState(this.state);
	}

}

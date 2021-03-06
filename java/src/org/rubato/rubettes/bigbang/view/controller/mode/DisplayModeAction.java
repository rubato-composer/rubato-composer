package org.rubato.rubettes.bigbang.view.controller.mode;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class DisplayModeAction extends AbstractAction {
	
	private ViewController controller;
	private DisplayModeAdapter adapter;
	
	public DisplayModeAction(ViewController controller, DisplayModeAdapter adapter) {
		this.controller = controller;
		this.adapter = adapter;
	}

	public void actionPerformed(ActionEvent e) {
		this.controller.clearDisplayTool();
		//TODO WHY AGAIN? this.controller.deselectOperations();
		this.controller.changeDisplayMode(this.adapter);
	}

}

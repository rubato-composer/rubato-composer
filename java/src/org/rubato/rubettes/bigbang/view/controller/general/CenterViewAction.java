package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class CenterViewAction extends AbstractAction {
	
	private ViewController controller;
	
	public CenterViewAction(ViewController controller) {
		super("Center view");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.centerView();
	}

}

package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class ShowWindowPreferencesAction extends AbstractAction {
	
	private ViewController controller;
	
	public ShowWindowPreferencesAction(ViewController controller) {
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent arg0) {
		this.controller.showWindowPreferences();
	}

}

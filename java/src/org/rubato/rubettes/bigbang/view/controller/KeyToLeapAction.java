package org.rubato.rubettes.bigbang.view.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;

public class KeyToLeapAction extends AbstractAction {
	private JBigBangPanel panel;
	private ViewController controller;
	
	public KeyToLeapAction(JBigBangPanel panel, ViewController controller) {
		this.panel = panel;
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		controller.setObjects();
	}
}

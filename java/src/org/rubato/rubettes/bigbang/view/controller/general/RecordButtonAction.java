package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class RecordButtonAction extends AbstractAction {
	
	private ViewController controller;
	
	public RecordButtonAction(ViewController controller) {
		super("Rec");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.toggleRecordMode();
	}

}
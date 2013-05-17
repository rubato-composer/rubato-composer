package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class DeleteObjectsAction extends AbstractAction {
	
	private ViewController controller;
	
	public DeleteObjectsAction(ViewController controller) {
		this.controller = controller;
	}
	
	public void actionPerformed(ActionEvent event) {
		this.controller.deleteSelectedObjects();
	}

}

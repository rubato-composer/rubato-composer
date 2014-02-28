package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class AddToNewLayerAction extends AbstractAction {
	
	private ViewController controller;
	
	public AddToNewLayerAction(ViewController controller) {
		super("New Layer");
		this.controller = controller;
	}
	
	public void actionPerformed(ActionEvent event) {
		this.controller.addSelectedObjectsToNewLayer();
	}
	
}

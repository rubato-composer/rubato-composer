package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class AddToLayerAction extends AbstractAction {
	
	private ViewController controller;
	private int layerIndex;
	
	public AddToLayerAction(ViewController controller, int layerIndex) {
		super("Layer "+layerIndex);
		this.controller = controller;
	}
	
	public void actionPerformed(ActionEvent event) {
		this.controller.addSelectedObjectsTo(this.layerIndex);
	}
	
}

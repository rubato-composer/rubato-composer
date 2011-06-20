package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class LayerButtonAction extends AbstractAction {
	
	private ViewController controller;
	private int layerIndex;
	
	public LayerButtonAction(ViewController controller, int layerIndex) {
		super(""+layerIndex);
		this.controller = controller;
		this.layerIndex = layerIndex;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.changeLayerState(this.layerIndex);
	}

}

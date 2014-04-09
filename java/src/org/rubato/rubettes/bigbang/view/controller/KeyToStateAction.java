package org.rubato.rubettes.bigbang.view.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.model.graph.CompositionState;

public class KeyToStateAction extends AbstractAction {
	
	private ViewController controller;
	private Integer stateIndex;
	
	public KeyToStateAction(ViewController controller, Integer stateIndex) {
		this.controller = controller;
		this.stateIndex = stateIndex;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.selectCompositionState(this.stateIndex);
	}

}

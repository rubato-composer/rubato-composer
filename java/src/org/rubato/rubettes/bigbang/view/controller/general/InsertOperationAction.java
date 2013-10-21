package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class InsertOperationAction extends AbstractAction {

	BigBangController controller;
	Integer compositionState;
	
	public InsertOperationAction(BigBangController controller, Integer compositionState) {
		this.controller = controller;
		this.compositionState = compositionState;
	}
	
	public void actionPerformed(ActionEvent event) {
		this.controller.insertOperation(this.compositionState);
	}

}

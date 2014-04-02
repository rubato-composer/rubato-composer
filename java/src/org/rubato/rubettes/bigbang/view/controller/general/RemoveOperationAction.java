package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.controller.BigBangController;
import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;

public class RemoveOperationAction extends AbstractAction {

	BigBangController controller;
	AbstractOperation operation;
	
	public RemoveOperationAction(BigBangController controller, AbstractOperation operation) {
		this.controller = controller;
		this.operation = operation;
	}
	
	public void actionPerformed(ActionEvent event) {
		this.controller.removeOperationFromGraph(this.operation);
	}

}

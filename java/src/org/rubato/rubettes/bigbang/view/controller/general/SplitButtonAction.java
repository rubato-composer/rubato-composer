package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class SplitButtonAction extends AbstractAction {
	
	private BigBangController controller;
	
	public SplitButtonAction(BigBangController controller) {
		super("Split");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.splitOperation();
	}

}
package org.rubato.rubettes.bigbang.controller;

import java.awt.event.ActionEvent;

import javax.swing.JButton;


public class AlterationAction extends AbstractBigBangAction {

	public AlterationAction(BigBangController controller) {
		super("Alt", controller);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (!((JButton) event.getSource()).isSelected()) {
			this.controller.addAlteration();
		}
	}

}

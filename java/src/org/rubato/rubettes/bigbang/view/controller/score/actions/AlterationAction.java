package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.rubato.rubettes.bigbang.view.controller.ViewController;


public class AlterationAction extends AbstractAction {

	private ViewController controller;
	
	public AlterationAction(ViewController controller) {
		super("Alt");
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (!((JButton) event.getSource()).isSelected()) {
			this.controller.addAlteration();
		}
	}

}

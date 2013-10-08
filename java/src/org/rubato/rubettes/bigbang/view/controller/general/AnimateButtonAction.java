package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class AnimateButtonAction extends AbstractAction {
	
	private BigBangController controller;
	
	public AnimateButtonAction(BigBangController controller) {
		super("Animate");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		//JButton source = (JButton)event.getSource();
		//source.setSelected(!source.isSelected());
		this.controller.toggleGraphAnimation();
	}

}
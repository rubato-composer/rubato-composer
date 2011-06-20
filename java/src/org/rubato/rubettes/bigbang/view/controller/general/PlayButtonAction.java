package org.rubato.rubettes.bigbang.view.controller.general;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class PlayButtonAction extends AbstractAction {
	
	private BigBangController controller;
	
	public PlayButtonAction(BigBangController controller) {
		super("Play");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.togglePlayMode();
	}

}
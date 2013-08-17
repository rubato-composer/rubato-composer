package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.controller.BigBangController;

public class StopWallpaperAction extends AbstractAction {
	
	private BigBangController controller;
	
	public StopWallpaperAction(BigBangController controller) {
		super("Wps");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.endWallpaper();
	}

}

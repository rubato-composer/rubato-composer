package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class StopWallpaperAction extends AbstractViewAction {
	
	public StopWallpaperAction(ViewController controller) {
		super("Wps", controller);
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.stopWallpaper();
	}

}

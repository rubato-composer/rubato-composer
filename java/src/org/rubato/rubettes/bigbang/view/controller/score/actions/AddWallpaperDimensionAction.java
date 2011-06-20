package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class AddWallpaperDimensionAction extends AbstractViewAction {
	
	public AddWallpaperDimensionAction(ViewController controller) {
		super("Wpa", controller);
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.addWallpaperDimension();
	}

}

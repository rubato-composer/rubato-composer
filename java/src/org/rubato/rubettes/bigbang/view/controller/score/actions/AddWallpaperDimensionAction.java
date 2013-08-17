package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class AddWallpaperDimensionAction extends AbstractAction {
	
	private ViewController controller;
	
	public AddWallpaperDimensionAction(ViewController controller) {
		super("Wpa");
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent event) {
		this.controller.addWallpaperDimension();
	}

}

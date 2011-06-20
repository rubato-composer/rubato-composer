package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.temp.BuildModulatorsMode;

public class BuildModulatorsAction extends AbstractAction {
	
	private ViewController controller;
	
	public BuildModulatorsAction(ViewController controller) {
		this.controller = controller;
	}
	
	public void actionPerformed(ActionEvent event) {
		this.controller.changeDisplayMode(new BuildModulatorsMode(this.controller));
	}
	
}

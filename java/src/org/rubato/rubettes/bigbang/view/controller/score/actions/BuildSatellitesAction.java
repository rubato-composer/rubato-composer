package org.rubato.rubettes.bigbang.view.controller.score.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.temp.BuildSatellitesMode;

public class BuildSatellitesAction extends AbstractAction {
	
	private ViewController controller;
	private int powersetIndex;
	
	public BuildSatellitesAction(ViewController controller, String formName, int powersetIndex) {
		super(formName);
		this.controller = controller;
		this.powersetIndex = powersetIndex;
	}
	
	public void actionPerformed(ActionEvent event) {
		this.controller.changeDisplayMode(new BuildSatellitesMode(this.controller, this.powersetIndex));
	}
	
}

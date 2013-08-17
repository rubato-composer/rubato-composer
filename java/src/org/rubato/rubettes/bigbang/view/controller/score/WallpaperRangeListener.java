package org.rubato.rubettes.bigbang.view.controller.score;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class WallpaperRangeListener implements ChangeListener {
	
	private ViewController controller;
	private boolean isToSpinner;
	
	public WallpaperRangeListener(ViewController controller, boolean isToSpinner) {
		this.controller = controller;
		this.isToSpinner = isToSpinner;
	}

	public void stateChanged(ChangeEvent e) {
		int spinnerValue = (Integer)((JSpinner)e.getSource()).getValue();
		this.controller.modifyWallpaperRange(this.isToSpinner, spinnerValue);
	}

}

package org.rubato.rubettes.bigbang.view.controller.mode.temp;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;

public class TemporaryDisplayMode extends DisplayModeAdapter {
	
	private ViewController controller; 
	private DisplayModeAdapter displayMode;

	public TemporaryDisplayMode(ViewController controller) {
		super(controller);
		this.controller = controller;
	}
	
	public void setPreviousDisplayMode(DisplayModeAdapter displayMode) {
		this.displayMode = displayMode;
	}
	
	public void goBackToPreviousMode() {
		if (this.displayMode != null) {
			this.controller.changeDisplayMode(this.displayMode);
		}
	}

}

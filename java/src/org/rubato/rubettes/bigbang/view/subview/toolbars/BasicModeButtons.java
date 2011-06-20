package org.rubato.rubettes.bigbang.view.subview.toolbars;

import java.util.ArrayList;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public abstract class BasicModeButtons extends ArrayList<JDisplayModeButton> {
	
	protected ViewController viewController;
	protected JDisplayModeButton drawingModeButton;
	
	public BasicModeButtons(ViewController viewController) {
		this.viewController = viewController;
		this.initModeButtons();
	}
	
	protected abstract void initModeButtons();
	
	public void enableSelectionAndDrawingModes(boolean enabled) {
		this.drawingModeButton.setEnabled(enabled);
	}

}

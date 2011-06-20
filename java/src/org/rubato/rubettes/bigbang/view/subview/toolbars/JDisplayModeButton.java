package org.rubato.rubettes.bigbang.view.subview.toolbars;

import javax.swing.JButton;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAction;
import org.rubato.rubettes.bigbang.view.controller.mode.DisplayModeAdapter;

public class JDisplayModeButton extends JButton {
	
	private DisplayModeAdapter adapter;
	
	public JDisplayModeButton(ViewController controller, String name, DisplayModeAdapter adapter) {
		super(new DisplayModeAction(controller, adapter));
		this.setText(name);
		this.adapter = adapter;
	}
	
	public DisplayModeAdapter getAdapter() {
		return this.adapter;
	}

}

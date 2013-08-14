package org.rubato.rubettes.bigbang.view.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class KeyToMidiAction extends AbstractAction {
	
	private ViewController controller;
	private int pitch;
	private boolean pressedNotReleased;
	
	public KeyToMidiAction(ViewController controller, int pitch, boolean pressedNotReleased) {
		this.controller = controller;
		this.pitch = pitch;
		this.pressedNotReleased = pressedNotReleased;
	}

	public void actionPerformed(ActionEvent event) {
		if (this.pressedNotReleased) {
			this.controller.pressMidiKey(this.pitch, 100);
		} else {
			this.controller.releaseMidiKey(this.pitch);
		}
	}

}

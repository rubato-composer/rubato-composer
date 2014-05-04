package org.rubato.rubettes.bigbang.view.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;

public class KeyToMidiAction extends AbstractAction {
	
	private JBigBangPanel panel;
	private ViewController controller;
	private int pitch;
	private boolean pressedNotReleased;
	
	public KeyToMidiAction(JBigBangPanel panel, ViewController controller, int pitch, boolean pressedNotReleased) {
		this.panel = panel;
		this.controller = controller;
		this.pitch = pitch;
		this.pressedNotReleased = pressedNotReleased;
	}

	public void actionPerformed(ActionEvent event) {
		int correctedPitch = this.pitch+(12*this.panel.getCurrentOctave());
		if (this.pressedNotReleased) {
			this.controller.pressMidiKey(-1, correctedPitch, -1);
		} else {
			this.controller.releaseMidiKey(-1, correctedPitch);
		}
	}

}

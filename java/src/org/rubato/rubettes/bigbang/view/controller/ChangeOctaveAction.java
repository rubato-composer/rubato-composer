package org.rubato.rubettes.bigbang.view.controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;

public class ChangeOctaveAction extends AbstractAction {
	
	private JBigBangPanel panel;
	private ViewController controller;
	private boolean up;
	
	public ChangeOctaveAction(JBigBangPanel panel, ViewController controller, boolean up) {
		this.panel = panel;
		this.controller = controller;
		this.up = up;
	}

	public void actionPerformed(ActionEvent event) {
		this.panel.changeOctave(this.up);
		this.controller.changeOctave(this.up);
	}

}

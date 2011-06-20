package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class NoteAdditionAdapter extends MouseInputAdapter {
	
	private ViewController controller;
	private int mouseButton;
	
	public NoteAdditionAdapter(ViewController controller, int mouseButton) {
		this.controller = controller;
		this.mouseButton = mouseButton;
	}
	
	public void mouseClicked(MouseEvent event) {
		this.drawNote(event);
	}
	
	public void mouseDragged(MouseEvent event) {
		this.drawNote(event);
	}
	
	private void drawNote(MouseEvent event) {
		if (event.getButton() == this.mouseButton) {
			this.controller.addNote(event.getPoint());
		}
	}

}

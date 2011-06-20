package org.rubato.rubettes.bigbang.view.controller.mode;

import java.awt.event.MouseEvent;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.NoteAdditionAdapter;

public class DrawingModeAdapter extends DisplayModeAdapter {

	public DrawingModeAdapter(ViewController controller) {
		super(controller);
		//left mouse button
		this.mouseInputListeners.add(new NoteAdditionAdapter(controller, MouseEvent.BUTTON1));
	}

}

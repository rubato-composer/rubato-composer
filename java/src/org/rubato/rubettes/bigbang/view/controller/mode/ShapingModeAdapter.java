package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.NoteShapingAdapter;

public class ShapingModeAdapter extends DisplayModeAdapter {

	public ShapingModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new NoteShapingAdapter(controller));
	}
}

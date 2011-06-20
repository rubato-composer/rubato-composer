package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.NoteSelectionAdapter;
import org.rubato.rubettes.bigbang.view.controller.score.NoteTranslationAdapter;

public class SelectionModeAdapter extends DisplayModeAdapter {

	public SelectionModeAdapter(ViewController controller) {
		super(controller);
		//left mouse button (on main area)
		this.mouseInputListeners.add(new NoteSelectionAdapter(controller));
		//left mouse button (on a selected note)
		//USE OWN MODE ADAPTER (deselection causes translation at the same time)
		this.mouseInputListeners.add(new NoteTranslationAdapter(controller));
	}

}
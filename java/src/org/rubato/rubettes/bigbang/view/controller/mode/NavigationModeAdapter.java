package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.display.DisplayPositionAdapter;
import org.rubato.rubettes.bigbang.view.controller.score.NoteTranslationAdapter;

public class NavigationModeAdapter extends DisplayModeAdapter {

	public NavigationModeAdapter(ViewController controller) {
		super(controller);
		//left mouse button (on main area)
		this.mouseInputListeners.add(new DisplayPositionAdapter(controller));
		//left mouse button (on a selected note)
		this.mouseInputListeners.add(new NoteTranslationAdapter(controller));
	}

}

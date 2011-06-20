package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.NoteTranslationAdapter;

public class TranslationModeAdapter extends DisplayModeAdapter {

	public TranslationModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new NoteTranslationAdapter(controller));
	}
	
}

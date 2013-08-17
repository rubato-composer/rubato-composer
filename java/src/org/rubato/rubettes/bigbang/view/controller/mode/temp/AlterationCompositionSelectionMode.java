package org.rubato.rubettes.bigbang.view.controller.mode.temp;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.ObjectSelectionAdapter;

public class AlterationCompositionSelectionMode extends TemporaryDisplayMode {

	public AlterationCompositionSelectionMode(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new ObjectSelectionAdapter(controller));
	}

}

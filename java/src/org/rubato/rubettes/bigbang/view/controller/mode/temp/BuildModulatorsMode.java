package org.rubato.rubettes.bigbang.view.controller.mode.temp;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.BuildModulatorsAdapter;

public class BuildModulatorsMode extends TemporaryDisplayMode {

	public BuildModulatorsMode(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new BuildModulatorsAdapter(controller, this));
	}

}

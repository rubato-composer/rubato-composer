package org.rubato.rubettes.bigbang.view.controller.mode.temp;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.BuildSatellitesAdapter;

public class BuildSatellitesMode extends TemporaryDisplayMode {

	public BuildSatellitesMode(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new BuildSatellitesAdapter(controller, this));
	}

}

package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.LeapObjectAffineAdapter;

public class AffineModeAdapter extends DisplayModeAdapter {
	public AffineModeAdapter(ViewController controller) {
		super(controller);
		this.leapListeners.add(new LeapObjectAffineAdapter(controller));
	}
}

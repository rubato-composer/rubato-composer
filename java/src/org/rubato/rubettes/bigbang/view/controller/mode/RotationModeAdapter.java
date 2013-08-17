package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.ObjectRotationAdapter;

public class RotationModeAdapter extends DisplayModeAdapter {

	public RotationModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new ObjectRotationAdapter(controller));
	}
	
	public RotationModeAdapter(ViewController controller, double[] center, double[] endPoint, double angle) {
		super(controller);
		this.mouseInputListeners.add(new ObjectRotationAdapter(controller, center, endPoint, angle));
	}

}

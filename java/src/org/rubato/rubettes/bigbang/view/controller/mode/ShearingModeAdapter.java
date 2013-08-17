package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.ObjectShearingAdapter;

public class ShearingModeAdapter extends DisplayModeAdapter {

	public ShearingModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new ObjectShearingAdapter(controller));
	}
	
	public ShearingModeAdapter(ViewController controller, double[] startingPoint, double[] endingPoint, double[] shearingFactors) {
		super(controller);
		this.mouseInputListeners.add(new ObjectShearingAdapter(controller, startingPoint, endingPoint, shearingFactors));
	}
	
}

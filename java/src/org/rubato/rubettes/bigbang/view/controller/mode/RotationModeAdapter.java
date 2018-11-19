package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
//import org.rubato.rubettes.bigbang.view.controller.score.LeapObjectAffineAdapter;
//import org.rubato.rubettes.bigbang.view.controller.score.LeapObjectRotationAdapter;
import org.rubato.rubettes.bigbang.view.controller.score.ObjectRotationAdapter;

public class RotationModeAdapter extends DisplayModeAdapter {

	public RotationModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new ObjectRotationAdapter(controller));
		//this.leapListeners.add(new LeapObjectRotationAdapter(controller));
	}
	
	public RotationModeAdapter(ViewController controller, double[] center, double[] startingPoint, double[] endPoint, double angle) {
		super(controller);
		this.mouseInputListeners.add(new ObjectRotationAdapter(controller, center, startingPoint, endPoint, angle));
		//this.leapListeners.add(new LeapObjectRotationAdapter(controller));
	}

}

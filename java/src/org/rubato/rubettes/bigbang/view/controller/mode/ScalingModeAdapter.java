package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.NoteScalingAdapter;

public class ScalingModeAdapter extends DisplayModeAdapter {

	public ScalingModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new NoteScalingAdapter(controller));
	}
	
	public ScalingModeAdapter(ViewController controller, double[] startingPoint, double[] endingPoint, double[] scaleFactors) {
		super(controller);
		this.mouseInputListeners.add(new NoteScalingAdapter(controller, startingPoint, endingPoint, scaleFactors));
	}

}
package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.NoteShearingAdapter;

public class ShearingModeAdapter extends DisplayModeAdapter {

	public ShearingModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new NoteShearingAdapter(controller));
	}
	
	public ShearingModeAdapter(ViewController controller, double[] center, double[] shearFactors) {
		super(controller);
		this.mouseInputListeners.add(new NoteShearingAdapter(controller, center, shearFactors));
	}
	
}

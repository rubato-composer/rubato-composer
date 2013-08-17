package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.ObjectTranslationAdapter;

public class TranslationModeAdapter extends DisplayModeAdapter {

	public TranslationModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new ObjectTranslationAdapter(controller));
	}
	
	public TranslationModeAdapter(ViewController controller, double[] startPoint, double[] endPoint) {
		super(controller);
		this.mouseInputListeners.add(new ObjectTranslationAdapter(controller, startPoint, endPoint));
	}
	
}

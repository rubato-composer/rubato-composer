package org.rubato.rubettes.bigbang.view.controller.mode;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.controller.score.NoteReflectionAdapter;

public class ReflectionModeAdapter extends DisplayModeAdapter {

	public ReflectionModeAdapter(ViewController controller) {
		super(controller);
		this.mouseInputListeners.add(new NoteReflectionAdapter(controller));
	}
	
	public ReflectionModeAdapter(ViewController controller, double[] center, double[] reflectionVector) {
		super(controller);
		this.mouseInputListeners.add(new NoteReflectionAdapter(controller, center, reflectionVector));
	}
	
}

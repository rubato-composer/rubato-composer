package org.rubato.rubettes.bigbang.view.input.leap;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

import com.leapmotion.leap.Controller;

public abstract class LeapTransformAdapter {
	ViewController viewController;
	public LeapTransformAdapter(ViewController viewController) {
		this.viewController = viewController;
	}
	public abstract void update(Controller controller);
	public abstract void capture();
	public abstract void end();
}

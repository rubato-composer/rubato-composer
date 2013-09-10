package org.rubato.rubettes.bigbang.view.input;

import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

public class LeapMotionIn extends Listener {
	
	Controller controller;
	ViewController viewController;
	
	public LeapMotionIn(ViewController viewController) {
		this.controller = new Controller();
		this.controller.addListener(this);
		this.viewController = viewController;
	}
	
	@Override
	public void onInit(Controller controller) {
		System.out.println("LeapMotion initialized. number of devices: " + this.controller.devices().count());
	}
	
	public void onFrame(Controller controller) {
		System.out.println("tipPosition " + controller.frame().fingers().frontmost().tipPosition());
		Vector tipPosition = controller.frame().fingers().frontmost().tipPosition();
		this.viewController.addObject(new Point2D.Double(tipPosition.getX(), tipPosition.getY()));
	}
	
}

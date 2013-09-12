package org.rubato.rubettes.bigbang.view.input;

import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.BigBangView;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

public class LeapMotionIn extends Listener {
	
	private final int AREA_WIDTH = 400;
	private final int AREA_HEIGHT = 300;
	private final int AREA_BOTTOM_EDGE = 50;
	private final int PLANE_Z_POSITION = 0;
	
	Controller controller;
	BigBangView view;
	ViewController viewController;
	
	private int leftEdge, rightEdge, topEdge;
	
	public LeapMotionIn(BigBangView view, ViewController viewController) {
		this.controller = new Controller();
		this.controller.addListener(this);
		this.view = view;
		this.viewController = viewController;
		this.leftEdge = -this.AREA_WIDTH/2;
		this.rightEdge = this.AREA_WIDTH/2;
		this.topEdge = this.AREA_BOTTOM_EDGE+this.AREA_HEIGHT;
	}
	
	@Override
	public void onInit(Controller controller) {
		System.out.println("Leap Motion initialized. Devices count: " + this.controller.devices().count());
	}
	
	public void onFrame(Controller controller) {
		Vector tipPosition = controller.frame().fingers().frontmost().tipPosition();
		if (this.view.inDrawingMode() && tipPosition.getZ() <= this.PLANE_Z_POSITION) {
			if (this.leftEdge <= tipPosition.getX() && tipPosition.getX() <= this.rightEdge
					&& this.AREA_BOTTOM_EDGE <= tipPosition.getY() && tipPosition.getY() <= this.topEdge) {
				double x = (tipPosition.getX()+(this.AREA_WIDTH/2))/this.AREA_WIDTH*JBigBangDisplay.DISPLAY_WIDTH;
				double y = (this.AREA_HEIGHT-(tipPosition.getY()-this.AREA_BOTTOM_EDGE))/this.AREA_HEIGHT*JBigBangPanel.CENTER_PANEL_HEIGHT;
				this.viewController.addObject(new Point2D.Double(x, y));
			}
		}
	}
	
}

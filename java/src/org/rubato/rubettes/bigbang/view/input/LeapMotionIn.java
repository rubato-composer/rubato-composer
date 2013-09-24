package org.rubato.rubettes.bigbang.view.input;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.BigBangView;
import org.rubato.rubettes.bigbang.view.subview.JBigBangDisplay;
import org.rubato.rubettes.bigbang.view.subview.JBigBangPanel;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.KeyTapGesture;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.ScreenTapGesture;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Gesture;

public class LeapMotionIn extends Listener {
	
	private final int AREA_WIDTH = 400;
	private final int AREA_HEIGHT = 300;
	private final int AREA_BOTTOM_EDGE = 50;
	private final int PLANE_Z_POSITION = 0;
	
	BigBangView view;
	ViewController viewController;
	Frame lastFrame;
	
	private int leftEdge, rightEdge, topEdge;
	
	public LeapMotionIn(BigBangView view, ViewController viewController) {
		System.out.println("Created Leap Motion in");
//		this.controller = new Controller();
//		this.controller.addListener(this);
//		this.controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
//		this.controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		this.view = view;
		this.viewController = viewController;
		this.leftEdge = -this.AREA_WIDTH/2;
		this.rightEdge = this.AREA_WIDTH/2;
		this.topEdge = this.AREA_BOTTOM_EDGE+this.AREA_HEIGHT;
		this.lastFrame = new Frame();
		System.out.println("Created Leap Motion out");
	}
	
	@Override
	public void onInit(Controller controller) {
		System.out.println("Leap Motion initialized. Devices count: " + controller.devices().count());
	}
	
	public void onFrame(Controller controller) {
		/*Frame frame = controller.frame();
		if (this.view.inDrawingMode()) {
			GestureList gestureList;
//			System.out.println("gestures");
			if (lastFrame.isValid()) {
				gestureList = controller.frame().gestures(lastFrame);
			}
			else {
				gestureList = controller.frame().gestures();
			}
			if (!gestureList.isEmpty()) {
				Iterator<Gesture> it = gestureList.iterator();
				Gesture gesture;
				while (it.hasNext()) {
					gesture = it.next();
					Vector tapPosition;
					switch (gesture.type()) {
					case TYPE_SCREEN_TAP:
						ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
						tapPosition = screenTap.position();
						break;
					case TYPE_KEY_TAP:
						KeyTapGesture keyTap = new KeyTapGesture(gesture);
						tapPosition = keyTap.position();
						break;
					default:
						continue;
					}
//					System.out.println(tapPosition.getX());
//					System.out.println(tapPosition.getY());
//					System.out.println(tapPosition.getZ());
					if (tapPosition.getZ() <= this.PLANE_Z_POSITION && this.leftEdge <= tapPosition.getX() && 
							tapPosition.getX() <= this.rightEdge && this.AREA_BOTTOM_EDGE <= tapPosition.getY() && 
							tapPosition.getY() <= this.topEdge) {
						double x = (tapPosition.getX()+(this.AREA_WIDTH/2))/this.AREA_WIDTH*JBigBangDisplay.DISPLAY_WIDTH;
						double y = (this.AREA_HEIGHT-(tapPosition.getY()-this.AREA_BOTTOM_EDGE))/this.AREA_HEIGHT*JBigBangPanel.CENTER_PANEL_HEIGHT;
						ArrayList<Point2D.Double> pointList = new ArrayList<Point2D.Double>(); 
						pointList.add(new Point2D.Double(x, y));
						this.viewController.addObjects(pointList, true);
					}
				}
			}
		}
		this.lastFrame = frame;*/
		ArrayList<Point2D.Double> pointList = new ArrayList<Point2D.Double>();
		for (int i = 0; i < controller.frame().fingers().count(); i++) {
			Vector currentTipPosition = controller.frame().fingers().get(i).tipPosition();
			if (this.view.inDrawingMode() && currentTipPosition.getZ() <= this.PLANE_Z_POSITION) {
				if (this.leftEdge <= currentTipPosition.getX() && currentTipPosition.getX() <= this.rightEdge
						&& this.AREA_BOTTOM_EDGE <= currentTipPosition.getY() && currentTipPosition.getY() <= this.topEdge) {
					double x = (currentTipPosition.getX()+(this.AREA_WIDTH/2))/this.AREA_WIDTH*JBigBangDisplay.DISPLAY_WIDTH;
					double y = (this.AREA_HEIGHT-(currentTipPosition.getY()-this.AREA_BOTTOM_EDGE))/this.AREA_HEIGHT*JBigBangPanel.CENTER_PANEL_HEIGHT;
					pointList.add(new Point2D.Double(x, y));
				}
			}
		}
		this.viewController.addObjects(pointList, true);
	}
	
}

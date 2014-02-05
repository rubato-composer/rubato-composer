package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.geom.Point2D;
import java.util.List;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.util.LeapUtil;
import org.rubato.rubettes.util.LeapUtil.Operation;
import org.rubato.rubettes.util.PointND;
import org.rubato.rubettes.util.LeapUtil.Axis;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Listener;

public class LeapObjectTranslationAdapter extends Listener {
	
	private Point2D.Double startPoint;
	private Point2D.Double endPoint;
	private Boolean isActive = false;
	private ViewController viewController;

	public LeapObjectTranslationAdapter(ViewController viewController) {
		this.viewController = viewController;
	}

	@Override
	public void onFrame(Controller controller) {
		List<Finger> fingers = LeapUtil.removeOffscreenFingers(LeapUtil.FingerListToJavaList(controller.frame().fingers()));
		fingers = LeapUtil.keepFingerIf(fingers, Axis.Z_AXIS, Operation.LESS_THAN, 0);
		fingers = LeapUtil.getFrontmostFingers(fingers, 1);
		if (fingers.isEmpty()) {
			capture();
			return;
		}
		Finger front = fingers.get(0);
		PointND p = LeapUtil.fingerToScreenPoint(front);
		if (isActive) {
			endPoint = new Point2D.Double(p.getCoord(0), p.getCoord(1));
			this.viewController.translateSelectedObjects(this.startPoint, this.endPoint, false, false);
		}
		else {
			startPoint = new Point2D.Double(p.getCoord(0), p.getCoord(1));
			this.viewController.translateSelectedObjects(this.startPoint, this.startPoint, false, true);
			isActive = true;
		}
	}

	
	public void capture() {
		if (isActive) {
			this.viewController.translateSelectedObjects(this.startPoint, this.endPoint, false, false);
		}
		isActive = false;
		
	}
}

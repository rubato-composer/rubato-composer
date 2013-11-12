package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.geom.Point2D;
import java.util.Iterator;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.util.PointND;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;

public class LeapObjectTranslationAdapter extends Listener {
	
	private LeapSpace leapSpace;
	private Point2D.Double startPoint;
	private Point2D.Double endPoint;
	private Boolean isActive = false;
	private ViewController viewController;

	public LeapObjectTranslationAdapter(ViewController viewController) {
		this.viewController = viewController;
		this.leapSpace = new LeapSpace();
	}

	@Override
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		FingerList fingers = frame.fingers();
		Iterator<Finger> it = fingers.iterator();
		while (it.hasNext()) {
			Finger f = it.next();
			if (!leapSpace.OnScreen(f.tipPosition())) {
				it.remove();
			}
		}
		if (fingers.isEmpty()) {
			capture();
			return;
		}
		Finger front = fingers.frontmost();
		if (front.tipPosition().getZ() > 0) {
			capture();
			return;
		}
		PointND p = leapSpace.ToScreenPoint(front.tipPosition());
		if (isActive) {
			endPoint = new Point2D.Double(p.getCoord(0), p.getCoord(1));
			this.viewController.translateSelectedObjects(this.startPoint, this.endPoint, false, true);
		}
		else {
			startPoint = new Point2D.Double(p.getCoord(0), p.getCoord(1));
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

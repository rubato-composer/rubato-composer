package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.geom.Point2D;
import java.util.Iterator;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.util.PointND;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;

public class LeapObjectSelectionAdapter extends Listener {
	private LeapSpace leapSpace;
	private SelectionTool selectionTool;
	private ViewController viewController;
	
	private Boolean stillSelecting = false;
	
	public LeapObjectSelectionAdapter(ViewController controller) {
		this.leapSpace = new LeapSpace();
		this.viewController = controller;
		selectionTool = new SelectionTool();
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
		if (fingers.count() < 2) {
			capture();
			return;
		}
		Finger first = new Finger();
		Finger second = new Finger();
		for (Finger finger : fingers) {
			if (!first.isValid() || first.tipPosition().getZ() > finger.tipPosition().getZ()) {
				second = first;
				first = finger;
			}
			else if (!second.isValid() || second.tipPosition().getZ() > finger.tipPosition().getZ()) {
				second = finger;
			}
		}
		if (second.tipPosition().getZ() > 0 || first.tipPosition().getZ() > 0) {
			capture();
			return;
		}
		PointND p1 = leapSpace.ToScreenPoint(first.tipPosition());
		selectionTool.setStartingPoint(new Point2D.Double(p1.getCoord(0),p1.getCoord(1)));
		PointND p2 = leapSpace.ToScreenPoint(second.tipPosition());
		selectionTool.setEndingPoint(new Point2D.Double(p2.getCoord(0),p2.getCoord(1)));
		stillSelecting = true;
		this.viewController.selectObjects(this.selectionTool, stillSelecting);
		
	}

	public void capture() {
		if (!stillSelecting) {
			return;
		}
		System.out.println("Capturing!");
		stillSelecting = false;
		this.viewController.selectObjects(this.selectionTool, stillSelecting);
	}
	
}

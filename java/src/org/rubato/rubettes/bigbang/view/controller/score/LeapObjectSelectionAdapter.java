package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.geom.Point2D;
import java.util.List;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.SelectionTool;
import org.rubato.rubettes.util.LeapUtil;
import org.rubato.rubettes.util.PointND;
import org.rubato.rubettes.util.LeapUtil.Axis;
import org.rubato.rubettes.util.LeapUtil.Operation;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Listener;

public class LeapObjectSelectionAdapter extends Listener {
	private SelectionTool selectionTool;
	private ViewController viewController;
	
	private Boolean stillSelecting = false;
	
	public LeapObjectSelectionAdapter(ViewController controller) {
		this.viewController = controller;
		selectionTool = new SelectionTool();
	}

	@Override
	public void onFrame(Controller controller) {
		List<Finger> fingers = LeapUtil.removeOffscreenFingers(LeapUtil.FingerListToJavaList(controller.frame().fingers()));
		fingers = LeapUtil.keepFingerIf(fingers, Axis.Z_AXIS, Operation.LESS_THAN, 0);
		fingers = LeapUtil.getFrontmostFingers(fingers, 2);
		if (fingers.size() < 2) {
			this.capture();
			return;
		}
		
		PointND p1 = LeapUtil.fingerToScreenPoint(fingers.get(0));
		selectionTool.setStartingPoint(new Point2D.Double(p1.getCoord(0),p1.getCoord(1)));
		PointND p2 = LeapUtil.fingerToScreenPoint(fingers.get(1));
		selectionTool.setEndingPoint(new Point2D.Double(p2.getCoord(0),p2.getCoord(1)));
		stillSelecting = true;
		this.viewController.selectObjects(this.selectionTool, stillSelecting);
		
	}

	public void capture() {
		if (!stillSelecting) {
			return;
		}
		stillSelecting = false;
		this.viewController.selectObjects(this.selectionTool, stillSelecting);
	}
	
}

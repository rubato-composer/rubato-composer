package org.rubato.rubettes.bigbang.view.input.leap;

import java.util.ArrayList;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.util.PointND;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Vector;

public class DrawingAdapter extends LeapTransformAdapter {
	private LeapSpace leapSpace;
	private ArrayList<PointND> lastUpdate;
	private Boolean lastFrameEmpty = true;
	
	public DrawingAdapter(ViewController controller, LeapSpace leapSpace) {
		super(controller);
		this.leapSpace = leapSpace;
	}

	@Override
	public void update(Controller controller) {
		update(controller, true);
		
	}
	
	public void update(Controller controller, Boolean usePreviewMode) {
		if (controller.frame().fingers().count() == 0) {
			if (lastFrameEmpty) {
				return;
			}
			lastFrameEmpty = true;
		}
		else {
			lastFrameEmpty = false;
		}
		ArrayList<PointND> pointList3D = new ArrayList<PointND>();
		for (int i = 0; i < controller.frame().fingers().count(); i++) {
			Vector currentTipPosition = controller.frame().fingers().get(i).tipPosition();
			if (leapSpace.OnScreen(currentTipPosition)) {
				pointList3D.add(leapSpace.ToScreenPoint(currentTipPosition));
			}
		}
		this.viewController.addObjects(pointList3D, usePreviewMode);
		lastUpdate = pointList3D;
	}

	@Override
	public void end() {
		
	}

	@Override
	public void capture() {
		if (lastUpdate.isEmpty()) {
			return;
		}
		this.viewController.addObjects(lastUpdate, false);
		this.lastUpdate = new ArrayList<PointND>();
	}
}

package org.rubato.rubettes.bigbang.view.controller.score;

import java.util.List;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.RotationTool;
import org.rubato.rubettes.util.GeometryTools;
import org.rubato.rubettes.util.LeapUtil;
import org.rubato.rubettes.util.LeapUtil.Axis;
import org.rubato.rubettes.util.LeapUtil.Operation;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.PointND;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Listener;

public class LeapObjectRotationAdapter extends Listener {
	
	private ViewController controller;
	private RotationTool rotationTool;
	
	private Boolean isActive = false;
	private Point2D center;
	private Point2D currentCenter;
	private Point2D startingPoint;
	private double startingAngle;
	private Point2D currentPoint;
	private double currentArc;

	public LeapObjectRotationAdapter(ViewController viewController) {
		this.controller = viewController;
		this.rotationTool = new RotationTool();
	}
	
	@Override
	public void onFrame(Controller controller) {
		List<Finger> fingers = LeapUtil.removeOffscreenFingers(LeapUtil.FingerListToJavaList(controller.frame().fingers()));
		fingers = LeapUtil.keepFingerIf(fingers, Axis.Z_AXIS, Operation.LESS_THAN, 0);
		fingers = LeapUtil.getFrontmostFingers(fingers, 2);
		
		if (fingers.size() < 1) {
			if (isActive) {
				this.updateView(false);
				isActive = false;
			}
			return;
		} 
		Point2D p1; 
		Point2D p2;
		if (fingers.size() < 2 && !isActive) {
			PointND t1 = LeapUtil.fingerToScreenPoint(fingers.get(0));
			p1 = ndToDouble(t1);
			setCenter(p1);
			this.controller.changeDisplayTool(this.rotationTool);
			return;
		}
		// Fakes two fingers by generating another point on the opposite side of the center. Avoids jumps in the rotation
		else if (fingers.size() < 2) {
			PointND t1 = LeapUtil.fingerToScreenPoint(fingers.get(0));
			p1 = ndToDouble(t1); 
			p2 = GeometryTools.subtractPoint(this.currentCenter, GeometryTools.subtractPoint(p1, this.currentCenter));
		}
		else {
			PointND t1 = LeapUtil.fingerToScreenPoint(fingers.get(0));
			PointND t2 = LeapUtil.fingerToScreenPoint(fingers.get(1));
			p1 = ndToDouble(t1); 
			p2 = ndToDouble(t2);
		}
		
		if (!isActive) {
//			setCenter(calcCenter(p1,p2));
			this.currentCenter = this.center;
			setStartingPoint(p1);
			setCurrentPoint(p1);
			setStartingAngle(GeometryTools.calculateAngle(this.center, this.startingPoint));
		}
		else {
			this.currentCenter = calcCenter(p1,p2);
			p1 = GeometryTools.addPoint(this.center, GeometryTools.subtractPoint(p1, this.currentCenter));
			p2 = GeometryTools.addPoint(this.center, GeometryTools.subtractPoint(p2, this.currentCenter));
			if (p1.distance(this.currentPoint) < p2.distance(this.currentPoint)) {
				setCurrentPoint(p1);
				setCurrentArc(GeometryTools.calculateArcAngle(this.center, this.startingAngle, this.currentPoint));
			}
			else {
				setCurrentPoint(p2);
				setCurrentArc(GeometryTools.calculateArcAngle(this.center, this.startingAngle, this.currentPoint));
			}
		}
		updateView(!this.isActive);
		this.isActive = true;
		
		
		
	}
	
	private void setCurrentArc(double a) {
		this.currentArc = a;
		this.rotationTool.setArcAngle(Math.toDegrees(a));
	}
	
	private void setStartingAngle(double a)  {
		this.startingAngle = a;
		this.rotationTool.setStartingAngle(Math.toDegrees(a));
	}
	
	private void setStartingPoint(Point2D p) {
		this.startingPoint = p;
		this.rotationTool.setStartingPoint(p);
	}
	
	private void setCurrentPoint(Point2D p) {
		this.currentPoint = p;
		this.rotationTool.setEndingPoint(p);
	}
	
	private void setCenter(Point2D p) {
		this.center = p;
		this.rotationTool.setCenter(this.center);
	}
	
	private Point2D calcCenter(Point2D p1, Point2D p2) {
		return new Point2D((p1.getX() + p2.getX())/2, (p1.getY() + p2.getY())/2);
	}
	
	private Point2D ndToDouble(PointND p) {
		return new Point2D(p.getCoord(0), p.getCoord(1));
	}

	private void updateView(Boolean startNewTransformation) {
		this.controller.rotateSelectedObjects(this.center, this.startingPoint, this.currentPoint, this.currentArc, false, startNewTransformation);
		this.controller.changeDisplayTool(this.rotationTool);
	}
}

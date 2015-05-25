package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.RotationTool;
import org.rubato.rubettes.util.GeometryTools;
import org.rubato.rubettes.util.Point2D;

public class ObjectRotationAdapter extends ObjectTransformationAdapter {
	
	private Point2D center;
	private double startingAngle;
	
	public ObjectRotationAdapter(ViewController controller) {
		super(controller);
	}
	
	public ObjectRotationAdapter(ViewController controller, double[] center, double[] startingPoint, double[] endingPoint, double angle) {
		super(controller, center, endingPoint);
		this.setCenter(center[0], center[1]);
		Point2D startingPoint2D = new Point2D(startingPoint[0], startingPoint[1]);
		this.updateStartAngle(startingPoint2D);
		this.updateArcAngle(angle);
		this.updateEndingPoint(GeometryTools.calculatePoint(this.center, startingPoint2D, angle));
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		Point clickedPoint = event.getPoint();
		this.updateCenter(clickedPoint.x, clickedPoint.y);
	}
	
	@Override
	protected void updateStartingPoint(double x, double y) {
		if (this.center != null) {
			super.updateStartingPoint(x, y);
			this.startingAngle = GeometryTools.calculateAngle(this.center, this.startingPoint);
			((RotationTool)this.displayTool).setStartingAngle(Math.toDegrees(this.startingAngle));
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (this.center != null) {
			Point2D currentEndingPoint = new Point2D(event.getPoint().x, event.getPoint().y);
			double arcAngle = this.calculateArcAngle(currentEndingPoint);
			this.updateArcAngle(arcAngle);
			super.mouseDragged(event);
		}
	}
	
	@Override
	protected void transformSelectedObjects(MouseEvent event, boolean startNewTransformation) {
		Point2D currentEndingPoint = new Point2D(event.getPoint().x, event.getPoint().y);
		double arcAngle = this.calculateArcAngle(currentEndingPoint);
		this.controller.rotateSelectedObjects(this.center, this.startingPoint, currentEndingPoint, arcAngle, event.isAltDown(), startNewTransformation);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event) {
		Point2D currentEndingPoint = new Point2D(event.getPoint().x, event.getPoint().y);
		double arcAngle = this.calculateArcAngle(currentEndingPoint);
		this.controller.modifyRotationAngle(arcAngle);
	}
	
	private double calculateStartingAngle(Point2D startingPoint) {
		return GeometryTools.calculateAngle(this.center, startingPoint);
	}
	
	private double calculateArcAngle(Point2D endingPoint) {
		return GeometryTools.calculateArcAngle(this.center, this.startingAngle, endingPoint);
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new RotationTool();
	}
	
	private void updateCenter(double x, double y) {
		this.setCenter(x, y);
		this.controller.changeDisplayTool(this.displayTool);
		if (this.inModificationMode) {
			this.controller.modifyCenterOfSelectedTransformation(this.center);
		}
	}
	
	private void setCenter(double x, double y) {
		this.center = new Point2D(x, y);
		if (this.displayTool != null) {
			((RotationTool)this.displayTool).setCenter(this.center);
		} else {
			this.initDisplayTool();
		}
	}
	
	private void updateStartAngle(Point2D startingPoint) {
		this.startingAngle = this.calculateStartingAngle(startingPoint);
		((RotationTool)this.displayTool).setStartingAngle(Math.toDegrees(this.startingAngle));
	}
	
	private void updateArcAngle(double arcAngle) {
		((RotationTool)this.displayTool).setArcAngle(Math.toDegrees(arcAngle));
		this.controller.changeDisplayTool(this.displayTool);
	}

}
package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.RotationTool;
import org.rubato.rubettes.util.GeometryTools;

public class NoteRotationAdapter extends NoteTransformationAdapter {
	
	private Point2D.Double center;
	private double startingAngle;
	
	public NoteRotationAdapter(ViewController controller) {
		super(controller);
	}
	
	public NoteRotationAdapter(ViewController controller, double[] center, double[] endingPoint, double angle) {
		super(controller, center, endingPoint);
		this.updateCenter(center[0], center[1]);
		Point2D.Double endingPoint2D = new Point2D.Double(endingPoint[0], endingPoint[1]);
		this.updateStartAngle(endingPoint2D, angle);
		this.updateArcAngle(angle);
	}
	
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
		Point2D.Double currentEndingPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		double arcAngle = this.calculateArcAngle(currentEndingPoint);
		this.updateArcAngle(arcAngle);
		super.mouseDragged(event);
	}
	
	@Override
	protected void transformSelectedNotes(MouseEvent event, boolean inPreviewMode) {
		Point2D.Double currentEndingPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		double arcAngle = this.calculateArcAngle(currentEndingPoint);
		this.controller.rotateSelectedNotes(this.center, currentEndingPoint, arcAngle, event.isAltDown(), inPreviewMode);
	}
	
	@Override
	protected void modifySelectedTransformation(MouseEvent event) {
		Point2D.Double currentEndingPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
		double arcAngle = this.calculateArcAngle(currentEndingPoint);
		this.controller.modifyRotationAngle(arcAngle);
	}
	
	private double calculateStartingAngle(Point2D.Double endPoint, double arcAngle) {
		double endAngle = GeometryTools.calculateAngle(this.center, endPoint);
		return endAngle-arcAngle;
	}
	
	private double calculateArcAngle(Point2D.Double endingPoint) {
		return GeometryTools.calculateArcAngle(this.center, this.startingAngle, endingPoint);
	}

	@Override
	protected void initDisplayTool() {
		this.displayTool = new RotationTool();
	}
	
	private void updateCenter(double x, double y) {
		this.center = new Point2D.Double(x, y);
		if (this.displayTool != null) {
			((RotationTool)this.displayTool).setCenter(this.center);
		} else {
			this.initDisplayTool();
		}
		this.controller.changeDisplayTool(this.displayTool);
		if (this.inModificationMode) {
			this.controller.modifySelectedTransformation(this.center);
		}
	}
	
	private void updateStartAngle(Point2D.Double endingPoint, double arcAngle) {
		this.startingAngle = this.calculateStartingAngle(endingPoint, arcAngle);
		((RotationTool)this.displayTool).setStartingAngle(Math.toDegrees(this.startingAngle));
	}
	
	private void updateArcAngle(double arcAngle) {
		((RotationTool)this.displayTool).setArcAngle(Math.toDegrees(arcAngle));
		this.controller.changeDisplayTool(this.displayTool);
	}

}
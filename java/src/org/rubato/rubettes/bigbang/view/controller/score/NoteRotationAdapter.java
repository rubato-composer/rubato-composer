package org.rubato.rubettes.bigbang.view.controller.score;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.model.tools.RotationTool;
import org.rubato.rubettes.util.GeometryTools;

public class NoteRotationAdapter extends MouseInputAdapter {
	
	private Point2D.Double center;
	private ViewController controller;
	private Point2D.Double startPoint;
	private double startAngle;
	private RotationTool rotationTool;
	private boolean dragging;
	
	public NoteRotationAdapter(ViewController controller) {
		this.init(controller);
	}
	
	public NoteRotationAdapter(ViewController controller, double[] center, double[] endPoint, double angle) {
		this.init(controller);
		this.updateCenter(center[0], center[1]);
		Point2D.Double endPoint2D = new Point2D.Double(endPoint[0], endPoint[1]);
		this.updateStartAngle(endPoint2D, angle);
		this.updateEndPointAndAngle(endPoint2D, angle);
	}
	
	private void init(ViewController controller) {
		this.controller = controller;
		this.dragging = false;
	}
	
	public void mouseClicked(MouseEvent event) {
		Point clickedPoint = event.getPoint();
		this.updateCenter(clickedPoint.x, clickedPoint.y);
	}
	
	private void updateCenter(double x, double y) {
		this.center = new Point2D.Double(x, y);
		this.rotationTool = new RotationTool(this.center);
		this.controller.changeDisplayTool(this.rotationTool);
	}
	
	private void updateStartAngle(Point2D.Double endPoint, double arcAngle) {
		this.startAngle = this.calculateStartAngle(endPoint, arcAngle);
		this.rotationTool.setStartAngle(Math.toDegrees(this.startAngle));
	}
	
	private void updateEndPointAndAngle(Point2D.Double endPoint, double arcAngle) {
		this.rotationTool.setEnd(endPoint, Math.toDegrees(arcAngle));
		this.controller.changeDisplayTool(this.rotationTool);
	}
	
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1 && this.center != null) {
			Point pressedPoint = event.getPoint();
			this.startPoint = new Point2D.Double(pressedPoint.x, pressedPoint.y);
			this.startAngle = GeometryTools.calculateAngle(this.center, this.startPoint);
			this.rotationTool.setStartAngle(Math.toDegrees(this.startAngle));
		}
	}

	public void mouseDragged(MouseEvent event) {
		this.changeRotationToolPosition(event);
		this.dragging = true;
	}

	public void mouseReleased(MouseEvent event) {
		if (this.dragging) {
			this.rotateSelectedNotes(event);
			this.dragging = false;
		}
	}
	
	private void changeRotationToolPosition(MouseEvent event) {
		if (this.startPoint != null) {
			Point2D.Double currentPoint = new Point2D.Double(event.getPoint().x, event.getPoint().y);
			double arcAngle = this.rotate(event, true);
			this.updateEndPointAndAngle(currentPoint, arcAngle);
		}
	}
	
	private void rotateSelectedNotes(MouseEvent event) {
		if (this.startPoint != null) {
			this.rotate(event, false);
			this.startPoint = null;
			this.rotationTool.resetRotation();
		}
	}
	
	private double rotate(MouseEvent event, boolean inPreviewMode) {
		Point currentPoint = event.getPoint();
		Point2D.Double currentEndPoint = new Point2D.Double(currentPoint.x, currentPoint.y);
		double arcAngle = GeometryTools.calculateArcAngle(this.center, this.startAngle, currentEndPoint);
		this.controller.rotateSelectedNotes(this.center, currentEndPoint, arcAngle, event.isAltDown(), inPreviewMode);
		return arcAngle;
	}
	
	private double calculateStartAngle(Point2D.Double endPoint, double arcAngle) {
		double endAngle = GeometryTools.calculateAngle(this.center, endPoint);
		return endAngle-arcAngle;
	}

}
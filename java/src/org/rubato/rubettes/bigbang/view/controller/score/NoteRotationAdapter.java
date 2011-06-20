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
	
	public NoteRotationAdapter(ViewController controller) {
		this.controller = controller;
	}
	
	public void mouseClicked(MouseEvent event) {
		Point clickedPoint = event.getPoint();
		this.center = new Point2D.Double(clickedPoint.x, clickedPoint.y);
		this.rotationTool = new RotationTool(this.center);
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
	}

	public void mouseReleased(MouseEvent event) {
		this.rotateSelectedNotes(event);
	}
	
	private void changeRotationToolPosition(MouseEvent event) {
		if (this.startPoint != null) {
			Point currentPoint = event.getPoint();
			Point2D.Double currentEndPoint = new Point2D.Double(currentPoint.x, currentPoint.y);
			double arcAngle = this.rotate(event, true);
			this.rotationTool.setEnd(currentEndPoint, Math.toDegrees(arcAngle));
			this.controller.changeDisplayTool(this.rotationTool);
			
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
		this.controller.rotateSelectedNotes(this.center, arcAngle, event.isAltDown(), inPreviewMode);
		return arcAngle;
	}
	
	

}
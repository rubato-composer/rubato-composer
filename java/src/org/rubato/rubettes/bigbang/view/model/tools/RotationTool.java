package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class RotationTool extends DisplayTool {
	
	private Point2D.Double center;
	private double startAngle;
	private Point2D.Double endPoint;
	private double arcAngle;
	
	public RotationTool(Point2D.Double center) {
		this.center = center;
	}
	
	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}
	
	public void setEnd(Point2D.Double endPoint, double arcAngle) {
		this.endPoint = endPoint;
		this.arcAngle = arcAngle;
	}
	
	public void resetRotation() {
		this.startAngle = 0;
		this.endPoint = null;
		this.arcAngle = 0;
	}
	
	@Override
	public void updatePosition(double xZoomFactor, double yZoomFactor) {
		//this.center =
	}

	@Override
	public void paint(AbstractPainter painter) {
		painter.setColor(this.DARK);
		painter.fillOval(this.center.x-2, this.center.y-2, 4, 4);
		if (this.startAngle != 0 && this.endPoint != null) {
			double radius = this.endPoint.distance(this.center);
			double x = this.center.x-radius;
			double y = this.center.y-radius;
			double doubleR = radius*2;
			painter.drawOval(x, y, doubleR, doubleR);
			painter.drawLine(this.center.x, this.center.y, this.endPoint.x, this.endPoint.y);
			painter.setColor(this.BRIGHT);
			if (this.arcAngle < 180) {
				painter.fillArc(x, y, doubleR, doubleR, this.startAngle, this.arcAngle);
			} else {
				painter.fillArc(x, y, doubleR, doubleR, this.startAngle, -1*(180-(this.arcAngle-180)));
			}
		}
	}

}

package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class RotationTool extends DisplayTool {
	
	private Point2D.Double center;
	private double startingAngle;
	private double arcAngle;
	
	public void setCenter(Point2D.Double center) {
		if (this.center != null && this.endingPoint != null) {
			double xDistance = center.x - this.center.x;
			double yDistance = center.y - this.center.y;
			this.endingPoint.setLocation(this.endingPoint.x+xDistance, this.endingPoint.y+yDistance);
		}
		this.center = center;
	}
	
	public void setStartingAngle(double startingAngle) {
		this.startingAngle = startingAngle;
	}
	
	public void setArcAngle(double arcAngle) {
		this.arcAngle = arcAngle;
	}

	@Override
	public void paint(AbstractPainter painter) {
		if (this.center != null) {
			painter.setColor(this.DARK);
			painter.fillOval(this.center.x-2, this.center.y-2, 4, 4);
			if (this.endingPoint != null) {
				double radius = this.endingPoint.distance(this.center);
				double x = this.center.x-radius;
				double y = this.center.y-radius;
				double doubleR = radius*2;
				painter.drawOval(x, y, doubleR, doubleR);
				painter.drawLine(this.center.x, this.center.y, this.endingPoint.x, this.endingPoint.y);
				painter.setColor(this.BRIGHT);
				if (this.arcAngle < 180) {
					painter.fillArc(x, y, doubleR, doubleR, this.startingAngle, this.arcAngle);
				} else {
					painter.fillArc(x, y, doubleR, doubleR, this.startingAngle, -1*(180-(this.arcAngle-180)));
				}
			}
		}
	}

}

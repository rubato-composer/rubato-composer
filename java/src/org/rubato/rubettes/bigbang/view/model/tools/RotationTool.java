package org.rubato.rubettes.bigbang.view.model.tools;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.util.Point2D;

public class RotationTool extends DisplayTool {
	
	private Point2D center;
	private double startingAngle;
	private Double arcAngle;
	
	public void setCenter(Point2D center) {
		if (this.center != null && this.endingPoint != null) {
			double xDistance = center.getX() - this.center.getX();
			double yDistance = center.getY() - this.center.getY();
			this.endingPoint.translate(xDistance, yDistance);
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
			painter.fillOval(this.center.getX()-2, this.center.getY()-2, 4, 4);
			if (this.arcAngle != null) {
				double radius = this.endingPoint.distance(this.center);
				double x = this.center.getX()-radius;
				double y = this.center.getY()-radius;
				double doubleR = radius*2;
				painter.drawOval(x, y, doubleR, doubleR);
				painter.drawLine(this.center.getX(), this.center.getY(), this.endingPoint.getX(), this.endingPoint.getY());
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

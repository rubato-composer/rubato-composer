package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class ReflectionTool extends DisplayTool {
	
	private Point2D.Double startPoint, endPoint;
	
	public ReflectionTool(Point2D.Double startPoint) {
		this.startPoint = startPoint;
	}
	
	public void setEndPoint(Point2D.Double endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public void paint(AbstractPainter painter) {
		if (this.endPoint != null) {
			painter.setColor(this.DARK);
			/*Rectangle bounds = g.getClipBounds();*/
			double xDifference = this.endPoint.x - this.startPoint.x;
			double yDifference = this.endPoint.y - this.startPoint.y;
			this.drawLine(painter, this.startPoint, xDifference, yDifference);
			this.drawLine(painter, this.startPoint, -1*xDifference, -1*yDifference);
		}
	}
	
	private void drawLine(AbstractPainter painter, Point2D.Double p, double deltaX, double deltaY) {
		painter.drawLine(p.x, p.y, p.x+deltaX, p.y+deltaY);
	}

}

package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Point;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class ReflectionTool extends DisplayTool {
	
	private Point startPoint, endPoint;
	
	public ReflectionTool(Point startPoint) {
		this.startPoint = startPoint;
	}
	
	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public void paint(AbstractPainter painter) {
		if (this.endPoint != null) {
			painter.setColor(this.DARK);
			/*Rectangle bounds = g.getClipBounds();*/
			int xDifference = this.endPoint.x - this.startPoint.x;
			int yDifference = this.endPoint.y - this.startPoint.y;
			this.drawLine(painter, this.startPoint, xDifference, yDifference);
			this.drawLine(painter, this.startPoint, -1*xDifference, -1*yDifference);
		}
	}
	
	private void drawLine(AbstractPainter painter, Point p, int deltaX, int deltaY) {
		painter.drawLine(p.x, p.y, p.x+deltaX, p.y+deltaY);
	}

}

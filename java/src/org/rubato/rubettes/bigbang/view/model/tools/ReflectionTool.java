package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class ReflectionTool extends DisplayTool {
	
	public ReflectionTool(Double startingPoint) {
		super(startingPoint);
	}

	@Override
	public void paint(AbstractPainter painter) {
		if (this.endingPoint != null) {
			painter.setColor(this.DARK);
			/*Rectangle bounds = g.getClipBounds();*/
			double xDifference = this.endingPoint.x - this.startingPoint.x;
			double yDifference = this.endingPoint.y - this.startingPoint.y;
			this.drawLine(painter, this.startingPoint, xDifference, yDifference);
			this.drawLine(painter, this.startingPoint, -1*xDifference, -1*yDifference);
		}
	}
	
	private void drawLine(AbstractPainter painter, Point2D.Double p, double deltaX, double deltaY) {
		painter.drawLine(p.x, p.y, p.x+deltaX, p.y+deltaY);
	}

}

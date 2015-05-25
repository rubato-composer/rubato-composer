package org.rubato.rubettes.bigbang.view.model.tools;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.util.Point2D;

public class ReflectionTool extends DisplayTool {

	@Override
	public void paint(AbstractPainter painter) {
		if (this.endingPoint != null) {
			painter.setColor(this.DARK);
			/*Rectangle bounds = g.getClipBounds();*/
			double xDifference = this.endingPoint.getX() - this.startingPoint.getX();
			double yDifference = this.endingPoint.getY() - this.startingPoint.getY();
			this.drawLine(painter, this.startingPoint, xDifference, yDifference);
			this.drawLine(painter, this.startingPoint, -1*xDifference, -1*yDifference);
		}
	}
	
	private void drawLine(AbstractPainter painter, Point2D p, double deltaX, double deltaY) {
		painter.drawLine(p.getX(), p.getY(), p.getX()+deltaX, p.getY()+deltaY);
	}

}

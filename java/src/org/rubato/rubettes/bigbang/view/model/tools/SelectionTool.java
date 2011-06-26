package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class SelectionTool extends DisplayTool {
	
	public SelectionTool(Point2D.Double startingPoint) {
		super(startingPoint);
	}
	
	public Rectangle2D.Double getArea() {
		double width = Math.abs(this.endingPoint.x-this.startingPoint.x);
		double height = Math.abs(this.endingPoint.y-this.startingPoint.y);
		return new Rectangle2D.Double(this.startingPoint.x, this.startingPoint.y, width, height);
	}

	@Override
	public void paint(AbstractPainter painter) {
		if (this.endingPoint != null) {
			double width = Math.abs(this.endingPoint.x-this.startingPoint.x);
			double height = Math.abs(this.endingPoint.y-this.startingPoint.y);
			painter.setColor(this.BRIGHT);
			painter.fillRect(this.startingPoint.x, this.startingPoint.y, width, height);
			painter.setColor(this.DARK);
			painter.drawRect(this.startingPoint.x, this.startingPoint.y, width, height);
		}
	}

}

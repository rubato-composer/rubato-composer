package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class TranslationTool extends DisplayTool {

	private final int SIZE = 20;
	private Point2D.Double startPoint, endPoint;
	
	public TranslationTool(Point2D.Double startPoint) {
		this.startPoint = startPoint;
	}
	
	public void setEndPoint(Point2D.Double endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public void paint(AbstractPainter painter) {
		painter.setColor(this.BRIGHT);
		painter.fillOval(this.startPoint.x-this.SIZE/2, this.startPoint.y-this.SIZE/2, this.SIZE, this.SIZE);
		if (this.endPoint != null) {
			painter.setColor(this.DARK);
			painter.drawOval(this.endPoint.x-this.SIZE/2, this.endPoint.y-this.SIZE/2, this.SIZE, this.SIZE);
		}
	}

}

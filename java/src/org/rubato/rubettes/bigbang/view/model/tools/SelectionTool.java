package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.util.GeometryTools;

public class SelectionTool extends DisplayTool {
	
	private Rectangle2D.Double area;
	
	public SelectionTool(Point2D.Double startingPoint) {
		super(startingPoint);
	}
	
	@Override
	public void setEndingPoint(Point2D.Double endingPoint) {
		super.setEndingPoint(endingPoint);
		this.updateArea();
	}
	
	public Rectangle2D.Double getArea() {
		return this.area;
	}

	@Override
	public void paint(AbstractPainter painter) {
		if (this.area != null) {
			painter.setColor(this.BRIGHT);
			painter.fillRect(this.area.x, this.area.y, this.area.width, this.area.height);
			painter.setColor(this.DARK);
			painter.drawRect(this.area.x, this.area.y, this.area.width, this.area.height);
		}
	}
	
	private void updateArea() {
		this.area = GeometryTools.getRectangle(this.startingPoint, new Point2D.Double(this.endingPoint.x, this.endingPoint.y));
	}

}

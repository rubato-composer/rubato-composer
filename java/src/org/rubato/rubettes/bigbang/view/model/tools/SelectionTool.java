package org.rubato.rubettes.bigbang.view.model.tools;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.util.GeometryTools;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.Rectangle2D;

public class SelectionTool extends DisplayTool {
	
	private Rectangle2D area;
	
	public SelectionTool() {
		super();
	}
	
	@Override
	public void setEndingPoint(Point2D endingPoint) {
		super.setEndingPoint(endingPoint);
		this.updateArea();
	}
	
	public Rectangle2D getArea() {
		return this.area;
	}

	@Override
	public void paint(AbstractPainter painter) {
		if (this.area != null) {
			painter.setColor(this.BRIGHT);
			painter.fillRect(this.area.getX(), this.area.getY(), this.area.getWidth(), this.area.getHeight());
			painter.setColor(this.DARK);
			painter.drawRect(this.area.getX(), this.area.getY(), this.area.getWidth(), this.area.getHeight());
		}
	}
	
	private void updateArea() {
		this.area = GeometryTools.getRectangle(this.startingPoint, new Point2D(this.endingPoint.getX(), this.endingPoint.getY()));
	}

}

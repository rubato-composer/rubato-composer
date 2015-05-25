package org.rubato.rubettes.bigbang.view.model.tools;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.Polygon2D;
import org.rubato.rubettes.util.Rectangle2D;

public class TransformationTool extends DisplayTool {
	
	private Point2D currentCenter, currentOpposite;
	private Rectangle2D reference;
	private double originalDiameter;
	private double angle;
	
	public TransformationTool(Point2D center, Point2D opposite) {
		double width = opposite.getX()-center.getX();
		double height = opposite.getY()-center.getY();
		this.reference = new Rectangle2D(center.getX(), center.getY(), width, height);
		this.setCurrentPoints(center, opposite);
		this.originalDiameter = Math.abs(opposite.distance(center));
		this.angle = Math.asin(Math.abs(this.reference.getHeight())/this.originalDiameter);
		if (Math.signum(width) == Math.signum(height)) {
			this.angle = -1*this.angle;
		}
	}
	
	public void setCurrentPoints(Point2D center, Point2D opposite) {
		this.currentCenter = center;
		this.currentOpposite = opposite;
	}

	@Override
	public void paint(AbstractPainter painter) {
		//draw original
		painter.setColor(this.BRIGHT);
		painter.fillRect(this.reference.getX(), this.reference.getY(), this.reference.getWidth(), this.reference.getHeight());
		//draw image
		painter.setColor(this.DARK);
		painter.drawPolygon(this.calculateImagePolygon());
	}
	
	private Polygon2D calculateImagePolygon() {
		Polygon2D imagePolygon = new Polygon2D();
		imagePolygon.addVertex(new Point2D(this.currentCenter.getX(), this.currentCenter.getY()));
		double xDist = this.currentOpposite.getX() - this.currentCenter.getX();
		double yDist = this.currentOpposite.getY() - this.currentCenter.getY();
		double x = this.currentCenter.getX() + (Math.cos(this.angle)*xDist - Math.sin(this.angle)*yDist)/this.originalDiameter*Math.abs(this.reference.getWidth());
		double y = this.currentCenter.getY() + (Math.sin(this.angle)*xDist + Math.cos(this.angle)*yDist)/this.originalDiameter*Math.abs(this.reference.getWidth());
		imagePolygon.addVertex(new Point2D(x, y));
		imagePolygon.addVertex(new Point2D(this.currentOpposite.getX(), this.currentOpposite.getY()));
		x = this.currentCenter.getX() + (this.currentOpposite.getX()-x);
		y = this.currentCenter.getY() + (this.currentOpposite.getY()-y);
		imagePolygon.addVertex(new Point2D(x, y));
		imagePolygon.addVertex(new Point2D(this.currentCenter.getX(), this.currentCenter.getY()));
		return imagePolygon;
	}

}

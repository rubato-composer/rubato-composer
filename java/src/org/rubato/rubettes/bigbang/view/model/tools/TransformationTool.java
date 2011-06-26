package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class TransformationTool extends DisplayTool {
	
	private Point2D.Double currentCenter, currentOpposite;
	private Rectangle2D.Double reference;
	private double originalDiameter;
	private double angle;
	
	public TransformationTool(Point2D.Double center, Point2D.Double opposite) {
		super(center);
		double width = opposite.x-center.x;
		double height = opposite.y-center.y;
		this.reference = new Rectangle2D.Double(center.x, center.y, width, height);
		this.setCurrentPoints(center, opposite);
		this.originalDiameter = Math.abs(opposite.distance(center));
		this.angle = Math.asin(Math.abs(this.reference.height)/this.originalDiameter);
		if (Math.signum(width) == Math.signum(height)) {
			this.angle = -1*this.angle;
		}
	}
	
	public void setCurrentPoints(Point2D.Double center, Point2D.Double opposite) {
		this.currentCenter = center;
		this.currentOpposite = opposite;
	}

	@Override
	public void paint(AbstractPainter painter) {
		//draw original
		painter.setColor(this.BRIGHT);
		painter.fillRect(this.reference.x, this.reference.y, this.reference.width, this.reference.height);
		//draw image
		painter.setColor(this.DARK);
		painter.drawPolygon(this.calculateImagePolygon());
	}
	
	private Path2D.Double calculateImagePolygon() {
		Path2D.Double imagePolygon = new Path2D.Double();
		imagePolygon.moveTo(this.currentCenter.x, this.currentCenter.y);
		double xDist = this.currentOpposite.x - this.currentCenter.x;
		double yDist = this.currentOpposite.y - this.currentCenter.y;
		double x = this.currentCenter.x + (Math.cos(this.angle)*xDist - Math.sin(this.angle)*yDist)/this.originalDiameter*Math.abs(this.reference.width);
		double y = this.currentCenter.y + (Math.sin(this.angle)*xDist + Math.cos(this.angle)*yDist)/this.originalDiameter*Math.abs(this.reference.width);
		imagePolygon.lineTo(x, y);
		imagePolygon.lineTo(this.currentOpposite.x, this.currentOpposite.y);
		x = this.currentCenter.x + (this.currentOpposite.x-x);
		y = this.currentCenter.y + (this.currentOpposite.y-y);
		imagePolygon.lineTo(x, y);
		imagePolygon.lineTo(this.currentCenter.x, this.currentCenter.y);
		return imagePolygon;
	}

}

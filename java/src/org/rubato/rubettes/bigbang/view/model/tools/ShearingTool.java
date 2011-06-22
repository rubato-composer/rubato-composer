package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Dimension;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class ShearingTool extends DisplayTool {
	
	private Point2D.Double center;
	private Rectangle2D.Double reference;
	private double[] shearingFactors;
	
	public ShearingTool(Point2D.Double center, Dimension reference) {
		this.center = center;
		int width = (int)reference.getWidth();
		int height = (int)reference.getHeight();
		this.reference = new Rectangle2D.Double(this.center.x-width/2, this.center.y-height/2, width, height);
	}
	
	public void setShearingFactors(double[] shearingFactors) {
		this.shearingFactors = shearingFactors;
	}

	@Override
	public void paint(AbstractPainter painter) {
		painter.setColor(this.BRIGHT);
		painter.fillRect(this.reference.x, this.reference.y, this.reference.width, this.reference.height);
		if (this.shearingFactors != null) {
			painter.setColor(this.DARK);
			painter.drawPolygon(this.calculateShearedPolygon(this.reference));
		}
	}
	
	private Path2D.Double calculateShearedPolygon(Rectangle2D.Double r) {
		Path2D.Double polygon = new Path2D.Double();
		double sx = shearingFactors[0]*r.width;
		double sy = shearingFactors[1]*r.height;
		polygon.moveTo(r.x, r.y+r.height);
		polygon.lineTo(r.x+r.width, r.y+r.height-sy);
		polygon.lineTo(r.x+r.width+sx, r.y-sy);
		polygon.lineTo(r.x+sx, r.y);
		polygon.closePath();
		return polygon;
	}

}

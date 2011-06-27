package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Dimension;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

public class ShearingTool extends DisplayTool {
	
	public final Dimension REFERENCE = new Dimension(100, 100);
	private Rectangle2D.Double reference;
	double[] shearingFactors;
	
	@Override
	public void setStartingPoint(Point2D.Double startingPoint) {
		super.setStartingPoint(startingPoint);
		this.reference = new Rectangle2D.Double(this.startingPoint.x-this.REFERENCE.width/2, this.startingPoint.y-this.REFERENCE.height/2, this.REFERENCE.width, this.REFERENCE.height);
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
			painter.drawPolygon(this.calculateShearedPolygon());
		}
	}
	
	private Path2D.Double calculateShearedPolygon() {
		Rectangle2D.Double r = this.reference;
		Path2D.Double polygon = new Path2D.Double();
		double sx = this.shearingFactors[0]*r.width;
		double sy = this.shearingFactors[1]*r.height;
		polygon.moveTo(r.x, r.y+r.height);
		polygon.lineTo(r.x+r.width, r.y+r.height-sy);
		polygon.lineTo(r.x+r.width+sx, r.y-sy);
		polygon.lineTo(r.x+sx, r.y);
		polygon.closePath();
		return polygon;
	}

}

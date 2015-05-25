package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Dimension;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.Polygon2D;
import org.rubato.rubettes.util.Rectangle2D;

public class ShearingTool extends DisplayTool {
	
	public final Dimension REFERENCE = new Dimension(100, 100);
	private Rectangle2D reference;
	double[] shearingFactors;
	
	@Override
	public void setStartingPoint(Point2D startingPoint) {
		super.setStartingPoint(startingPoint);
		this.reference = new Rectangle2D(this.startingPoint.getX()-this.REFERENCE.getWidth()/2, this.startingPoint.getY()-this.REFERENCE.getHeight()/2, this.REFERENCE.getWidth(), this.REFERENCE.getHeight());
	}
	
	public void setShearingFactors(double[] shearingFactors) {
		this.shearingFactors = shearingFactors;
	}

	@Override
	public void paint(AbstractPainter painter) {
		painter.setColor(this.BRIGHT);
		painter.fillRect(this.reference.getX(), this.reference.getY(), this.reference.getWidth(), this.reference.getHeight());
		if (this.shearingFactors != null) {
			painter.setColor(this.DARK);
			painter.drawPolygon(this.calculateShearedPolygon());
		}
	}
	
	private Polygon2D calculateShearedPolygon() {
		Rectangle2D r = this.reference;
		Polygon2D polygon = new Polygon2D();
		double sx = this.shearingFactors[0]*r.getWidth();
		double sy = this.shearingFactors[1]*r.getHeight();
		polygon.addVertex(new Point2D(r.getX(), r.getY()+r.getHeight()));
		polygon.addVertex(new Point2D(r.getX()+r.getWidth(), r.getY()+r.getHeight()-sy));
		polygon.addVertex(new Point2D(r.getX()+r.getWidth()+sx, r.getY()-sy));
		polygon.addVertex(new Point2D(r.getX()+sx, r.getY()));
		return polygon;
	}

}

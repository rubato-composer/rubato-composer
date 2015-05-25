package org.rubato.rubettes.bigbang.view.model.tools;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.Polygon2D;

public class AffineTool extends DisplayTool {
	
	private static double width = 100;
	private RMatrix transform;
	
	public AffineTool() {
		double[][] t = {{1,0,0},{0,1,0},{0,0,1}};
		transform = new RMatrix(t);
	}

	@Override
	public void paint(AbstractPainter painter) {
		//draw original
		painter.setColor(this.BRIGHT);
		painter.fillRect(this.startingPoint.getX() - width/2, this.startingPoint.getY() - width/2, 
				width, width);
		
		//draw image
		painter.setColor(this.DARK);
		painter.drawPolygon(this.calculateImagePolygon());
	}
	
	public void setTransform(RMatrix trans) {
		transform = trans;
	}
	
	private Point2D transformPoint(Point2D v) {
		double[] vec = {v.getX(), v.getY(), 1};
		vec = transform.product(vec);
		return new Point2D(vec[0], vec[1]);
	}
			
	private Polygon2D calculateImagePolygon() {
		double w2 = width/2;
		Polygon2D imagePolygon = new Polygon2D();
		Point2D p1 = transformPoint(new Point2D(startingPoint.getX()+w2, startingPoint.getY()+w2));
		Point2D p2 = transformPoint(new Point2D(startingPoint.getX()+w2, startingPoint.getY()-w2));
		Point2D p3 = transformPoint(new Point2D(startingPoint.getX()-w2, startingPoint.getY()-w2));
		Point2D p4 = transformPoint(new Point2D(startingPoint.getX()-w2, startingPoint.getY()+w2));
		imagePolygon.addVertex(new Point2D(p1.getX(), p1.getY()));
		imagePolygon.addVertex(new Point2D(p2.getX(), p2.getY()));
		imagePolygon.addVertex(new Point2D(p3.getX(), p3.getY()));
		imagePolygon.addVertex(new Point2D(p4.getX(), p4.getY()));
		imagePolygon.addVertex(new Point2D(p1.getX(), p1.getY()));

//		p1 = transformPoint(new Point2D(50+w2, 50+w2));
//		p2 = transformPoint(new Point2D(50+w2, 50-w2));
//		p3 = transformPoint(new Point2D(50-w2, 50-w2));
//		p4 = transformPoint(new Point2D(50-w2, 50+w2));
//		imagePolygon.moveTo(p1.getX(), p1.getY());
//		imagePolygon.lineTo(p2.getX(), p2.getY());
//		imagePolygon.lineTo(p3.getX(), p3.getY());
//		imagePolygon.lineTo(p4.getX(), p4.getY());
//		imagePolygon.lineTo(p1.getX(), p1.getY());
		return imagePolygon;
	}
	
}

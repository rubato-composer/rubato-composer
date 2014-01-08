package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;

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
		painter.fillRect(this.startingPoint.x - width/2, this.startingPoint.y - width/2, 
				width, width);
		
		//draw image
		painter.setColor(this.DARK);
		painter.drawPolygon(this.calculateImagePolygon());
	}
	
	public void setTransform(RMatrix trans) {
		transform = trans;
	}
	
	private Point2D.Double transformPoint(Point2D.Double v) {
		double[] vec = {v.x, v.y, 1};
		vec = transform.product(vec);
		return new Point2D.Double(vec[0], vec[1]);
	}
			
	private Path2D.Double calculateImagePolygon() {
		double w2 = width/2;
		Path2D.Double imagePolygon = new Path2D.Double();
		Point2D.Double p1 = transformPoint(new Point2D.Double(startingPoint.x+w2, startingPoint.y+w2));
		Point2D.Double p2 = transformPoint(new Point2D.Double(startingPoint.x+w2, startingPoint.y-w2));
		Point2D.Double p3 = transformPoint(new Point2D.Double(startingPoint.x-w2, startingPoint.y-w2));
		Point2D.Double p4 = transformPoint(new Point2D.Double(startingPoint.x-w2, startingPoint.y+w2));
		imagePolygon.moveTo(p1.x, p1.y);
		imagePolygon.lineTo(p2.x, p2.y);
		imagePolygon.lineTo(p3.x, p3.y);
		imagePolygon.lineTo(p4.x, p4.y);
		imagePolygon.lineTo(p1.x, p1.y);

//		p1 = transformPoint(new Point2D.Double(50+w2, 50+w2));
//		p2 = transformPoint(new Point2D.Double(50+w2, 50-w2));
//		p3 = transformPoint(new Point2D.Double(50-w2, 50-w2));
//		p4 = transformPoint(new Point2D.Double(50-w2, 50+w2));
//		imagePolygon.moveTo(p1.x, p1.y);
//		imagePolygon.lineTo(p2.x, p2.y);
//		imagePolygon.lineTo(p3.x, p3.y);
//		imagePolygon.lineTo(p4.x, p4.y);
//		imagePolygon.lineTo(p1.x, p1.y);
		return imagePolygon;
	}
	
}

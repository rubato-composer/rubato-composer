package org.rubato.rubettes.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GeometryTools {
	
	public static double calculateArcAngle(Point2D.Double center, double startAngle, Point2D.Double endPoint) {
		double endAngle = calculateAngle(center, endPoint);
		/*if (endAngle < startAngle) {
			endAngle += 2*Math.PI;
		}*/
		return endAngle - startAngle;
	}
	
	public static double calculateAngle(Point2D.Double center, Point2D.Double p) {
		Point2D.Double zeroAngle = new Point2D.Double(center.x+1, center.y);
		double angle = calculateAngle(center, zeroAngle, p);
		if (p.y > center.y) {
			angle = Math.PI + (Math.PI - angle);
		}
		return angle;
	}
	
	private static double calculateAngle(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
		double x2 = p2.x-p1.x;
		double y2 = p2.y-p1.y;
		double x3 = p3.x-p1.x;
		double y3 = p3.y-p1.y;
		double d2 = p2.distance(p1);
		double d3 = p3.distance(p1);
		return Math.acos(((x2*x3)+(y2*y3))/(Math.abs(d2)*Math.abs(d3)));
	}
	
	public static Rectangle2D.Double getRectangle(Point2D.Double p1, Point2D.Double p2) {
		double x = Math.min(p1.x, p2.x);
		double y = Math.min(p1.y, p2.y);
		double width = Math.abs(p2.x - p1.x);
		double height = Math.abs(p2.y - p1.y);
		return new Rectangle2D.Double(x, y, width, height);
	}

}

package org.rubato.rubettes.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public class GeometryTools {
	
	public static double calculateArcAngle(Point2D.Double center, double startAngle, Point2D.Double endPoint) {
		double endAngle = calculateAngle(center, endPoint);
		double angle = endAngle - startAngle;
		if (angle > Math.PI) {
			angle = -1*((2*Math.PI)-angle);
		}
		return angle;
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
	
	public static Point2D.Double calculatePoint(Point2D.Double center, Point2D.Double p, double angle) {
		angle += calculateAngle(center, p)+(Math.PI/2);
		double r = Math.sqrt(Math.pow(p.x-center.x, 2)+Math.pow(p.y-center.y, 2));
		return new Point2D.Double(center.x + (r*Math.sin(angle)), center.y + (r*Math.cos(angle)));
	}
	
	public static Rectangle2D.Double getRectangle(Point2D.Double p1, Point2D.Double p2) {
		double x = Math.min(p1.x, p2.x);
		double y = Math.min(p1.y, p2.y);
		double width = Math.abs(p2.x - p1.x);
		double height = Math.abs(p2.y - p1.y);
		return new Rectangle2D.Double(x, y, width, height);
	}
	
	public static List<Double> lagrangePredictValues(List<Double> values, int numberOfValues) {
		double[] x = new double[values.size()];
		double[] y = new double[values.size()];
		double sum = 0, min = Double.MAX_VALUE, max = -1*Double.MAX_VALUE;
		//calculate function
		for (int i = 0; i < values.size(); i++) {
			x[i] = i;
			y[i] = values.get(i);
			sum += y[i];
			min = Math.min(min, y[i]);
			max = Math.max(max, y[i]);
		}
		double average = sum/values.size();
		double range = max-min;
		PolynomialFunctionLagrangeForm function = new PolynomialFunctionLagrangeForm(x, y);
		//calculate additional values
		List<Double> predictedValues = new ArrayList<Double>();
		for (int i = 0; i < numberOfValues; i++) {
			double currentValue = function.value(values.size()+i);
			//normalize a little
			double signum = Math.signum(currentValue);
			double normedValue = Math.abs(currentValue-average);
			
			double currentCorrectedValue = signum*(average+(range*3*deform((normedValue/(normedValue+1)), function.degree()/2)));
			predictedValues.add(currentCorrectedValue);
		}
		return predictedValues;
	}
	
	private static double deform(double x, double t) {
		return x/(Math.pow(Math.E, 2*t) - x*(Math.pow(Math.E, 2*t) - 1));
	}

}

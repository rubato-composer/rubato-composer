package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public class GeometryTools {
	
	public static Point2D subtractPoint(Point2D p1, Point2D p2) {
		return new Point2D(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}
	
	public static Point2D addPoint(Point2D p1, Point2D p2) {
		return new Point2D(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}
	
	public static double calculateArcAngle(Point2D center, double startAngle, Point2D endPoint) {
		double endAngle = calculateAngle(center, endPoint);
		double angle = endAngle - startAngle;
		if (angle > Math.PI) {
			angle = -1*((2*Math.PI)-angle);
		}
		return angle;
	}
	
	public static double calculateAngle(Point2D center, Point2D p) {
		Point2D zeroAngle = new Point2D(center.getX()+1, center.getY());
		double angle = calculateAngle(center, zeroAngle, p);
		if (p.getY() > center.getY()) {
			angle = Math.PI + (Math.PI - angle);
		}
		return angle;
	}
	
	private static double calculateAngle(Point2D p1, Point2D p2, Point2D p3) {
		double x2 = p2.getX()-p1.getX();
		double y2 = p2.getY()-p1.getY();
		double x3 = p3.getX()-p1.getX();
		double y3 = p3.getY()-p1.getY();
		double d2 = p2.distance(p1);
		double d3 = p3.distance(p1);
		return Math.acos(((x2*x3)+(y2*y3))/(Math.abs(d2)*Math.abs(d3)));
	}
	
	public static Point2D calculatePoint(Point2D center, Point2D p, double angle) {
		angle += calculateAngle(center, p)+(Math.PI/2);
		double r = Math.sqrt(Math.pow(p.getX()-center.getX(), 2)+Math.pow(p.getY()-center.getY(), 2));
		return new Point2D(center.getX() + (r*Math.sin(angle)), center.getY() + (r*Math.cos(angle)));
	}
	
	public static Rectangle2D getRectangle(Point2D p1, Point2D p2) {
		double x = Math.min(p1.getX(), p2.getX());
		double y = Math.min(p1.getY(), p2.getY());
		double width = Math.abs(p2.getX() - p1.getX());
		double height = Math.abs(p2.getY() - p1.getY());
		return new Rectangle2D(x, y, width, height);
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

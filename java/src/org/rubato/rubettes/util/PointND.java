package org.rubato.rubettes.util;

import java.awt.geom.Point2D;

public class PointND {
	
	private double[] coordinates;
	
	public PointND(Point2D.Double point) {
		this.coordinates = new double[]{point.x, point.y};
	}
	
	public PointND(double ... c) {
		this.coordinates = c;
	}
	
	public int getDimension() {
		return coordinates.length;
	}
	
	public double getCoord(int i) {
		return this.coordinates[i];
	}
	
}
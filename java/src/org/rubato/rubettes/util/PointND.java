package org.rubato.rubettes.util;

import java.util.Arrays;

public class PointND {
	
	private double[] coordinates;
	
	public PointND(Point2D point) {
		this.coordinates = new double[]{point.getX(), point.getY()};
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
	
	public String toString() {
		return Arrays.toString(this.coordinates);
	}
	
}
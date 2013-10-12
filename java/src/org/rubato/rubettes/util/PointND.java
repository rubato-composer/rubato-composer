package org.rubato.rubettes.util;

public class PointND {
	
	private double[] coordinates;
	
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
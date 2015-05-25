package org.rubato.rubettes.util;

public class Point2D {
	
	private double x, y;
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double distance(Point2D other) {
		return Math.sqrt(Math.pow(Math.abs(this.x-other.x), 2)+(Math.pow(Math.abs(this.y-other.y), 2)));
	}
	
	public Point2D minus(Point2D other) {
		return new Point2D(this.x-other.x, this.y-other.y);
	}
	
	public Point2D plus(Point2D other) {
		return new Point2D(this.x+other.x, this.y+other.y);
	}
	
	public void translate(double xDistance, double yDistance) {
		this.x += xDistance;
		this.y += yDistance;
	}
	
	public String toString() {
		return "(" +this.x + ", " + this.y + ")";
	}
	
}
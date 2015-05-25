package org.rubato.rubettes.util;

public class Rectangle2D {
	
	private double x, y, width, height;
	
	public Rectangle2D(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public double getHeight() {
		return this.height;
	}
	
	public boolean contains(Point2D point) {
		return this.x < point.getX() && point.getX() < this.x + this.width
				&& this.y < point.getY() && point.getY() < this.y + this.height;
	}
	
}
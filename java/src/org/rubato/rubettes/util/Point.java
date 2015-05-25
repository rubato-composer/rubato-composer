package org.rubato.rubettes.util;

public class Point {
	
	private int x, y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public Point minus(Point other) {
		return new Point(this.x-other.x, this.y-other.y);
	}
	
	public Point plus(Point other) {
		return new Point(this.x+other.x, this.y+other.y);
	}
	
	public void translate(int xDistance, int yDistance) {
		this.x += xDistance;
		this.y += yDistance;
	}
	
	public String toString() {
		return "(" +this.x + ", " + this.y + ")";
	}
	
}
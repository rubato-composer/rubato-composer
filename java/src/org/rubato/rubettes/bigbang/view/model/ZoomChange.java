package org.rubato.rubettes.bigbang.view.model;

import java.awt.Point;

public class ZoomChange {
	
	private double changeFactor;
	private int x, y;
	
	public ZoomChange(double scrollUnits, int x, int y) {
		this.changeFactor = Math.pow(9.0/10, scrollUnits);
		this.x = x;
		this.y = y;
	}
	
	public ZoomChange(double changeFactor, Point position) {
		this.changeFactor = changeFactor;
		this.x = position.x;
		this.y = position.y;
	}
	
	public double getChangeFactor() {
		return this.changeFactor;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}

}

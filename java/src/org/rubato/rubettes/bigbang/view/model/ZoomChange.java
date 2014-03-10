package org.rubato.rubettes.bigbang.view.model;

public class ZoomChange {
	
	public static final int X_ONLY = 0;
	public static final int Y_ONLY = 1;
	public static final int BOTH = 2;
	
	private double changeFactor;
	private int activeCoordinates; 
	private int x, y;
	
	/**
	 * Creates a standard ZoomChange.
	 * @param scrollUnits
	 * @param x
	 * @param y
	 * @param activeCoordinates a modifier: either ZoomChange.X_ONLY, ZoomChange.Y_ONLY, or ZoomChange.BOTH
	 */
	public ZoomChange(double scrollUnits, int x, int y, int activeCoordinates) {
		this.changeFactor = Math.pow(9.0/10, scrollUnits);
		this.activeCoordinates = activeCoordinates;
		this.x = x;
		this.y = y;
	}
	
	public double getXChangeFactor() {
		if (this.activeCoordinates != ZoomChange.Y_ONLY) {
			return this.changeFactor;
		}
		return 1.0;
	}
	
	public double getYChangeFactor() {
		if (this.activeCoordinates != ZoomChange.X_ONLY) {
			return this.changeFactor;
		}
		return 1.0;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}

}

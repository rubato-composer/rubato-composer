package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Color;
import java.awt.geom.Point2D;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;

public abstract class DisplayTool {
	
	protected final Color DARK = Color.black;
	protected final Color BRIGHT = new Color(0, 0, 0, 0.2f);
	
	protected DisplayContents display;
	
	protected Point2D.Double startingPoint, endingPoint;
	
	public DisplayTool(Point2D.Double startingPoint) {
		this.startingPoint = startingPoint;
	}
	
	public void setEndingPoint(Point2D.Double endingPoint) {
		this.endingPoint = endingPoint;
	}
	
	public void setDisplay(DisplayContents display) {
		this.display = display;
	}
	
	//TODO: yes, dynamically modify position
	//public void updatePosition(double xZoomFactor, double yZoomFactor) {}
	
	public abstract void paint(AbstractPainter painter);

}

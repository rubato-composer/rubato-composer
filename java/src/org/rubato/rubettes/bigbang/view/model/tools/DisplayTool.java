package org.rubato.rubettes.bigbang.view.model.tools;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;
import org.rubato.rubettes.util.Point2D;

public abstract class DisplayTool {
	
	protected final float[] DARK = new float[]{0, 0, 0, 1}; //black
	protected final float[] BRIGHT = new float[]{0, 0, 0, 0.2f};
	
	protected DisplayContents display;
	
	protected Point2D startingPoint, endingPoint;
	
	public void setStartingPoint(Point2D startingPoint) {
		this.startingPoint = startingPoint;
	}
	
	public void setEndingPoint(Point2D endingPoint) {
		this.endingPoint = endingPoint;
	}
	
	public void setDisplay(DisplayContents display) {
		this.display = display;
	}
	
	//TODO: yes, dynamically modify position
	//public void updatePosition(double xZoomFactor, double yZoomFactor) {}
	
	public abstract void paint(AbstractPainter painter);

}

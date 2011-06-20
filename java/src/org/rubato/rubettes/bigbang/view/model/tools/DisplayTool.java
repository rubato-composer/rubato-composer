package org.rubato.rubettes.bigbang.view.model.tools;

import java.awt.Color;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;

public abstract class DisplayTool {
	
	protected final Color DARK = Color.black;
	protected final Color BRIGHT = new Color(0, 0, 0, 0.2f);
	
	protected DisplayContents display;
	
	public void setDisplay(DisplayContents display) {
		this.display = display;
	}
	
	public void updatePosition(double xZoomFactor, double yZoomFactor) {}
	
	public abstract void paint(AbstractPainter painter);

}

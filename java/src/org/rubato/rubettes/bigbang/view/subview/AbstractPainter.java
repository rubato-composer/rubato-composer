package org.rubato.rubettes.bigbang.view.subview;

import org.rubato.rubettes.util.Polygon2D;

public abstract class AbstractPainter {
	
	public abstract void setColor(float[] rgbaColor);

	public abstract void drawLine(double x1, double y1, double x2, double y2);
	
	public abstract void drawRect(double x, double y, double width, double height);
	
	public abstract void fillRect(double x, double y, double width, double height);
	
	public abstract void fillObject(double x, double y, double width, double height);
	
	public abstract void drawPolygon(Polygon2D p);
	
	public abstract void fillPolygon(Polygon2D p);
	
	public abstract void drawOval(double x, double y, double width, double height);
	
	public abstract void fillOval(double x, double y, double width, double height);
	
	public abstract void fillArc(double x, double y, double width, double height, double startAngle, double arcAngle);
	
	public abstract void drawString(String str, double x, double y);
	
	public abstract int getStringWidth(String s);
	
	public abstract int getStringHeight(String s);

}
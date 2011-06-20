package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Color;
import java.awt.geom.Path2D;

public abstract class AbstractPainter {
	
	public abstract void setColor(Color color);

	public abstract void drawLine(double x1, double y1, double x2, double y2);
	
	public abstract void drawRect(double x, double y, double width, double height);
	
	public abstract void fillRect(double x, double y, double width, double height);
	
	public abstract void fillNote(double x, double y, double width, double height);
	
	public abstract void drawPolygon(Path2D.Double p);
	
	public abstract void fillPolygon(Path2D.Double p);
	
	public abstract void drawOval(double x, double y, double width, double height);
	
	public abstract void fillOval(double x, double y, double width, double height);
	
	public abstract void fillArc(double x, double y, double width, double height, double startAngle, double arcAngle);
	
	public abstract void drawString(String str, double x, double y);
	
	public abstract int getStringWidth(String s);
	
	public abstract int getStringHeight(String s);

}
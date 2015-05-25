package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.Polygon2D;

public class AWTPainter extends AbstractPainter {
	
	private Graphics g;
	
	public AWTPainter(Graphics g) {
		this.g = g;
		this.g.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
	}
	
	@Override
	public void setColor(float[] rgba) {
		this.g.setColor(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));
	}
	
	@Override
	public void drawLine(double x1, double y1, double x2, double y2) {
		this.g.drawLine(this.round(x1), this.round(y1), this.round(x2), this.round(y2));
	}
	
	@Override
	public void drawRect(double x, double y, double width, double height) {
		this.g.drawRect(this.round(x), this.round(y), this.round(width), this.round(height));
	}

	@Override
	public void fillRect(double x, double y, double width, double height) {
		this.g.fillRect(this.round(x), this.round(y), this.round(width), this.round(height));
	}
	
	@Override
	public void drawPolygon(Polygon2D p) {
		this.g.drawPolygon(this.toAWTPolygon(p));
	}

	@Override
	public void fillPolygon(Polygon2D p) {
		this.g.fillPolygon(this.toAWTPolygon(p));
	}
	
	@Override
	public void drawOval(double x, double y, double width, double height) {
		this.g.drawOval(this.round(x), this.round(y), this.round(width), this.round(height));
	}
	
	@Override
	public void fillOval(double x, double y, double width, double height) {
		this.g.fillOval(this.round(x), this.round(y), this.round(width), this.round(height));
	}
	
	@Override
	public void fillArc(double x, double y, double width, double height, double startAngle, double arcAngle) {
		this.g.fillArc(this.round(x), this.round(y), this.round(width), this.round(height), this.round(startAngle), this.round(arcAngle));
	}
	
	@Override
	public void fillObject(double x, double y, double width, double height) {
		this.fillRect(this.round(x), this.round(y), this.round(width), this.round(height));
	}
	
	@Override
	public void drawString(String str, double x, double y) {
		this.g.drawString(str, this.round(x), this.round(y));
	}
	
	@Override
	public int getStringWidth(String s) {
		Graphics2D g2D = (Graphics2D)this.g;
		return (int)g2D.getFont().getStringBounds(s, g2D.getFontRenderContext()).getWidth();
	}
	
	@Override
	public int getStringHeight(String s) {
		Graphics2D g2D = (Graphics2D)this.g;
		return (int)g2D.getFont().getStringBounds(s, g2D.getFontRenderContext()).getHeight();
	}
	
	private int round(double d) {
		return (int)Math.round(d);
	}
	
	private Polygon toAWTPolygon(Polygon2D polygon) {
		Polygon p = new Polygon();
		for (Point2D currentPoint : polygon.getVertices()) {
			p.addPoint(this.round(currentPoint.getX()), this.round(currentPoint.getY()));
		}
		return p;
	}

}

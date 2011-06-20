package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

public class AWTPainter extends AbstractPainter {
	
	private Graphics g;
	
	public AWTPainter(Graphics g) {
		this.g = g;
		this.g.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
	}
	
	@Override
	public void setColor(Color color) {
		this.g.setColor(color);
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
	public void drawPolygon(Path2D.Double p) {
		this.g.drawPolygon(this.pathToPolygon(p));
	}

	@Override
	public void fillPolygon(Path2D.Double p) {
		this.g.fillPolygon(this.pathToPolygon(p));
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
	public void fillNote(double x, double y, double width, double height) {
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
	
	private Polygon pathToPolygon(Path2D.Double path) {
		Polygon p = new Polygon();
		PathIterator iterator = path.getPathIterator(null);
		double[] currentCoordinates = new double[2];
		while (!iterator.isDone()) {
			iterator.currentSegment(currentCoordinates);
			p.addPoint(this.round(currentCoordinates[0]), this.round(currentCoordinates[1]));
			iterator.next();
		}
		return p;
	}

}

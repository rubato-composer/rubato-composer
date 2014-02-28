package org.rubato.rubettes.bigbang.view.model;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;

/**
 * A visual object corresponding to a BigBangObject
 * @author florian thalmann
 */
public class DisplayObject implements Comparable<DisplayObject> {
	
	private static final float DARK = 0.6f;
	private static final float BRIGHT = 1;
	
	private BigBangObject bbObject;
	
	private DisplayContents display;
	private double xDiff, yDiff;
	private Rectangle2D.Double rectangle;
	private Point2D.Double center;
	private boolean isSelected;
	
	private float currentHue, currentOpacity;
	private int currentRed, currentBlue, currentGreen;
	
	public DisplayObject(BigBangObject bbObject) {
		this.bbObject = bbObject;
	}
	
	public BigBangObject getBigBangObject() {
		return this.bbObject;
	}
	
	public void setDisplay(DisplayContents display) {
		this.display = display;
	}
	
	public Double getNthValue(String valueName, int nameInstanceNumber) {
		return this.bbObject.getNthValue(valueName, nameInstanceNumber);
	}
	
	public void setSelected(boolean selected) {
		if (this.isActive()) {
			this.isSelected = selected;
		}
	}
	
	public boolean isSelected() {
		return this.isSelected;
	}
	
	public boolean isVisible() {
		return this.bbObject.isVisible();
	}
	
	public boolean isActive() {
		return this.bbObject.isActive();
	}
	
	public boolean isOnLayer(int layerIndex) {
		return this.bbObject.isOnLayer(layerIndex);
	}
	
	private double getX(double xZoomFactor, int xPosition) {
		double x = this.display.translateValue(this, ViewParameters.X);
		return (xZoomFactor*(x+this.xDiff))+xPosition;
	}
	
	private double getY(double yZoomFactor) {
		double y = this.display.translateValue(this, ViewParameters.Y);
		return yZoomFactor*(y+this.yDiff);
	}
	
	private double getWidth(double xZoomFactor) {
		double width = this.display.translateValue(this, ViewParameters.WIDTH);
		return xZoomFactor*width;
	}
	
	private double getHeight(double yZoomFactor) {
		double height = this.display.translateValue(this, ViewParameters.HEIGHT);
		return yZoomFactor*height;
	}
	
	public Color getColor() {
		this.currentOpacity = new Float(this.display.translateValue(this, ViewParameters.SATURATION));
		if (this.display.getViewParameters().inRGBMode()) {
			if (!this.isActive()) {
				return new Color(200, 200, 200, Math.round(this.currentOpacity));
			}
			return this.getRGBColor();
				
		}
		if (!this.isActive()) {
			//70-100% of maximal brightness
			float brightness = 0.3f*(1-this.currentOpacity)+0.7f;
			return Color.getHSBColor(this.currentHue, 0, brightness);
		}
		return this.getHueColor();
	}
	
	private Color getHueColor() {
		this.currentHue = new Float(this.display.translateValue(this, ViewParameters.HUE));
		return Color.getHSBColor(this.currentHue, this.currentOpacity, this.getBrightness());
	}
	
	private Color getRGBColor() {
		//brightness comes into play here, since there is no other way to make objects darker!
		this.currentRed = (int)Math.round(this.getBrightness()*this.display.translateValue(this, ViewParameters.RED));
		this.currentGreen = (int)Math.round(this.getBrightness()*this.display.translateValue(this, ViewParameters.GREEN));
		this.currentBlue = (int)Math.round(this.getBrightness()*this.display.translateValue(this, ViewParameters.BLUE));
		int alpha = Math.round(this.currentOpacity);
		return new Color(this.currentRed, this.currentGreen, this.currentBlue, alpha);
	}
	
	private float getBrightness() {
		if (this.isSelected) {
			return DisplayObject.DARK;
		}
		return DisplayObject.BRIGHT;
	}
	
	public void updateBounds(double xZoomFactor, double yZoomFactor, int xPosition, int yPosition) {
		double x = this.getX(xZoomFactor, xPosition);
		double width = this.getWidth(xZoomFactor);
		double height = this.getHeight(yZoomFactor);
		//only calculate other stuff, if x on display, and so on...
		double y = this.getY(yZoomFactor)-(height/2)+yPosition;
		this.rectangle = new Rectangle2D.Double(x, y, width, height);
		this.center = new Point2D.Double(x+width/2, y+height/2);
	}
	
	/**
	 * Paints a line that connects this object to the given point (x/y)  
	 */
	public void paintConnectors(AbstractPainter painter, double x, double y) {
		if (this.display != null && this.isVisible() && this.display.satellitesConnected()) {
			/*if (relation == DenotatorPath.SATELLITE) {
				painter.setColor(Color.black);
			} else {*/
				painter.setColor(this.getColor());
			//}
			painter.drawLine((float)x, (float)y, (float)this.center.x, (float)this.center.y);
		}
	}
	
	public void paintAnchorSelection(AbstractPainter painter) {
		if (this.isVisible()) {
			Path2D.Double triangle = new Path2D.Double();
			triangle.moveTo(this.rectangle.x, this.rectangle.y);
			triangle.lineTo(this.rectangle.x, this.rectangle.y+this.rectangle.height);
			triangle.lineTo(this.rectangle.x+this.rectangle.width, this.rectangle.y);
			triangle.closePath();
			painter.setColor(Color.getHSBColor(this.currentHue, this.currentOpacity, DisplayObject.DARK));
			painter.fillPolygon(triangle);
		}
	}
	
	public void paint(AbstractPainter painter) {
		if (this.isVisible() && this.display != null && this.rectangle != null) {
			painter.setColor(this.getColor());
			painter.fillNote(this.rectangle.x, this.rectangle.y, this.rectangle.width, this.rectangle.height);
		}
	}
	
	public Rectangle2D.Double getRectangle() {
		return this.rectangle;
	}
	
	public Point2D.Double getLocation() {
		return new Point2D.Double(this.rectangle.x, (int)this.center.getY());
	}
	
	public Point2D.Double getCenter() {
		return this.center;
	}
	
	public boolean intersects(Rectangle2D.Double area) {
		return this.rectangle.intersects(area) || area.contains(this.center);
	}

	public int compareTo(DisplayObject o) {
		return this.bbObject.compareTo(o.bbObject);
	}

}

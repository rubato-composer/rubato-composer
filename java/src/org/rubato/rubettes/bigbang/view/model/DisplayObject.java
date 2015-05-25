package org.rubato.rubettes.bigbang.view.model;

import java.util.Arrays;

import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;
import org.rubato.rubettes.util.Point2D;
import org.rubato.rubettes.util.Polygon2D;
import org.rubato.rubettes.util.Rectangle2D;

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
	private Rectangle2D rectangle;
	private Point2D center;
	private boolean isSelected;
	
	private float currentHue, currentOpacity;
	private float currentRed, currentBlue, currentGreen;
	
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
	
	//returns a float array representing an rgba color
	private float[] getColor() {
		this.currentOpacity = new Float(this.display.translateValue(this, ViewParameters.SATURATION));
		if (this.display.getViewParameters().inRGBMode()) {
			if (!this.isActive()) {
				return new float[]{.78125f, .78125f, .78125f, this.currentOpacity};
			}
			return this.getRGBColor();
				
		}
		if (!this.isActive()) {
			//70-100% of maximal brightness
			float brightness = 0.3f*(1-this.currentOpacity)+0.7f;
			return this.getRGBfromCurrentHue(this.currentHue, 0, brightness);
		}
		return this.getHueColor();
	}
	
	private float[] getHueColor() {
		this.currentHue = new Float(this.display.translateValue(this, ViewParameters.HUE));
		return this.getRGBfromCurrentHue(this.currentHue, this.currentOpacity, this.getBrightness());
	}
	
	private float[] getRGBColor() {
		//brightness comes into play here, since there is no other way to make objects darker!
		this.currentRed = (float) (this.getBrightness()*this.display.translateValue(this, ViewParameters.RED));
		this.currentGreen = (float) (this.getBrightness()*this.display.translateValue(this, ViewParameters.GREEN));
		this.currentBlue = (float) (this.getBrightness()*this.display.translateValue(this, ViewParameters.BLUE));
		return new float[]{this.currentRed, this.currentGreen, this.currentBlue, this.currentOpacity};
	}
	
	//TODO if doesnt work take code from awt.Color.HSBtoRGB(..
	private float[] getRGBfromCurrentHue(float h, float s, float b) {
		float c = (1-Math.abs(2*b-1))*s;
		float x = c*(1-Math.abs(((h*6)%2)-1));
	
		if (h < 1.0/6) {
			return new float[]{c,x,0};
		} else if (h < 2.0/6) {
			return new float[]{x,c,0};
		} else if (h < 3.0/6) {
			return new float[]{0,c,x};
		} else if (h < 4.0/6) {
			return new float[]{0,x,c};
		} else if (h < 5.0/6) {
			return new float[]{x,0,c};
		} else {
			return new float[]{c,0,x};
		}
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
		this.rectangle = new Rectangle2D(x, y, width, height);
		this.center = new Point2D(x+width/2, y+height/2);
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
			painter.drawLine((float)x, (float)y, (float)this.center.getX(), (float)this.center.getY());
		}
	}
	
	public void paintAnchorSelection(AbstractPainter painter) {
		if (this.isVisible()) {
			Polygon2D triangle = new Polygon2D();
			triangle.addVertex(new Point2D(this.rectangle.getX(), this.rectangle.getY()));
			triangle.addVertex(new Point2D(this.rectangle.getX(), this.rectangle.getY()+this.rectangle.getHeight()));
			triangle.addVertex(new Point2D(this.rectangle.getX()+this.rectangle.getWidth(), this.rectangle.getY()));
			//triangle.closePath();
			//TODO should react to rbg!!
			painter.setColor(this.getRGBfromCurrentHue(this.currentHue, this.currentOpacity, DisplayObject.DARK));
			painter.fillPolygon(triangle);
		}
	}
	
	public void paint(AbstractPainter painter) {
		if (this.isVisible() && this.display != null && this.rectangle != null) {
			painter.setColor(this.getColor());
			//System.out.println(Arrays.toString(this.getColor()));
			painter.fillObject(this.rectangle.getX(), this.rectangle.getY(), this.rectangle.getWidth(), this.rectangle.getHeight());
		}
	}
	
	public Rectangle2D getRectangle() {
		return this.rectangle;
	}
	
	public Point2D getLocation() {
		return new Point2D(this.rectangle.getX(), (int)this.center.getY());
	}
	
	public Point2D getCenter() {
		return this.center;
	}
	
	public boolean intersects(Rectangle2D area) {
		return area.getX() < this.rectangle.getX() + this.rectangle.getWidth()
				&& area.getX() + area.getWidth() > this.rectangle.getX()
				&& area.getY() < this.rectangle.getY() + this.rectangle.getHeight()
				&& area.getY() + area.getHeight() > this.rectangle.getY();
		//return this.rectangle.intersects(area) || area.contains(this.center);
	}

	public int compareTo(DisplayObject o) {
		return this.bbObject.compareTo(o.bbObject);
	}

}

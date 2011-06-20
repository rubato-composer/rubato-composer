package org.rubato.rubettes.bigbang.view.model;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;
import org.rubato.rubettes.util.NotePath;

public class DisplayNote implements Comparable<Object> {
	
	private static final float DARK = 0.6f;
	private static final float BRIGHT = 1;
	
	private NotePath originalPath;
	private DisplayContents display;
	private DisplayNote parent;
	private int relation;
	private List<DisplayNote> children;
	private double[] values;
	private int layer;
	private double xDiff, yDiff;
	private Rectangle2D.Double rectangle;
	private Point2D.Double center;
	private boolean selected;
	private boolean selectionVisible;
	private boolean visible;
	private boolean active;
	
	private float currentHue, currentOpacity;
	private Color currentColor;
	
	public DisplayNote(double[] values, DisplayNote parent, int relation, NotePath originalPath, int layer) {
		this.values = values;
		this.parent = parent;
		this.relation = relation;
		this.originalPath = originalPath;
		this.children = new ArrayList<DisplayNote>();
		this.selectionVisible = true;
		this.layer = layer;
	}
	
	public void setDisplay(DisplayContents display) {
		this.display = display;
	}
	
	public void setChildren(List<DisplayNote> children) {
		this.children = children;
	}
	
	public boolean hasChildren() {
		return this.children.size() > 0;
	}
	
	public DisplayNote getParent() {
		return this.parent;
	}
	
	public List<DisplayNote> getChildren() {
		return this.children;
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public int getLayer() {
		return this.layer;
	}
	
	public void setSelected(boolean selected) {
		if (this.active) {
			this.selected = selected;
		}
	}
	
	public boolean isSelected() {
		return this.selected;
	}
	
	private void updateSelected() {
		if (!this.active) {
			this.selected = false;
		}
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setSelectionVisible(boolean selectionVisible) {
		this.selectionVisible = selectionVisible;
	}
	
	public void move(double x, double y) {
		this.xDiff += x;
		this.yDiff += y;
	}
	
	public double getValue(int i) {
		return this.values[i];
	}
	
	private double getX(double xZoomFactor, int xPosition) {
		double x = this.display.translateValue(this.values, ViewParameters.X);
		return (xZoomFactor*(x+this.xDiff))+xPosition;
	}
	
	private double getY(double yZoomFactor) {
		double y = this.display.translateValue(this.values, ViewParameters.Y);
		return yZoomFactor*(y+this.yDiff);
	}
	
	private double getWidth(double xZoomFactor) {
		double width = this.display.translateValue(this.values, ViewParameters.WIDTH);
		return xZoomFactor*width;
	}
	
	private double getHeight(double yZoomFactor) {
		double height = this.display.translateValue(this.values, ViewParameters.HEIGHT);
		return yZoomFactor*height;
	}
	
	public Color getColor() {
		this.currentOpacity = new Float(this.display.translateValue(this.values, ViewParameters.SATURATION));
		if (this.active) {
			this.currentHue = new Float(this.display.translateValue(this.values, ViewParameters.HUE));
			return Color.getHSBColor(this.currentHue, this.currentOpacity, this.getBrightness());
		}
		//70-100% of maximal brightness  
		float brightness = 0.3f*(1-this.currentOpacity)+0.7f;
		return Color.getHSBColor(this.currentHue, 0, brightness);
	}
	
	private float getBrightness() {
		if (this.selected && this.selectionVisible) {
			return DisplayNote.DARK;
		}
		return DisplayNote.BRIGHT;
	}
	
	public void setVisibility(LayerState state) {
		this.visible = !state.equals(LayerState.invisible);
		this.active = state.equals(LayerState.active);
		this.updateSelected();
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
	
	public void paintConnectors(AbstractPainter painter) {
		if (this.visible) {
			if (this.display.satellitesConnected() && this.parent != null) {
				//System.out.println(this.parent.getCenter());
				if (this.parent.visible) {
					this.paintConnectors(painter, this.parent.getCenter().x, this.parent.getCenter().y, this.relation);
				}
			}
		}
	}
	
	public void paintConnectors(AbstractPainter painter, double parentX, double parentY, int relation) {
		if (this.visible) {
			if (relation == NotePath.SATELLITE) {
				painter.setColor(Color.black);
			} else {
				painter.setColor(this.getColor());
			}
			painter.drawLine((float)parentX, (float)parentY, (float)this.center.x, (float)this.center.y);
		}
	}
	
	public void paintAnchorSelection(AbstractPainter painter) {
		if (this.visible) {
			Path2D.Double triangle = new Path2D.Double();
			triangle.moveTo(this.rectangle.x, this.rectangle.y);
			triangle.lineTo(this.rectangle.x, this.rectangle.y+this.rectangle.height);
			triangle.lineTo(this.rectangle.x+this.rectangle.width, this.rectangle.y);
			triangle.closePath();
			painter.setColor(Color.getHSBColor(this.currentHue, this.currentOpacity, DisplayNote.DARK));
			painter.fillPolygon(triangle);
		}
	}
	
	public void paint(AbstractPainter painter) {
		if (this.visible) {
			painter.setColor(this.getColor());
			painter.fillNote(this.rectangle.x, this.rectangle.y, this.rectangle.width, this.rectangle.height);
		}
	}
	
	public Color getCurrentColor() {
		return this.currentColor;
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
	
	public NotePath getOriginalPath() {
		return this.originalPath;
	}
	
	public boolean intersects(Rectangle2D.Double area) {
		return this.rectangle.intersects(area) || area.contains(this.center);
	}

	public int compareTo(Object object) {
		if (!(object instanceof DisplayNote)) {
			throw new ClassCastException("DisplayNote expected.");
		}
		DisplayNote otherNote = (DisplayNote)object;
		int layerCompare = new Integer(this.layer).compareTo(otherNote.layer);
		if (layerCompare != 0) { 
			return layerCompare;
		}
		for (int i = 0; i < this.values.length; i++) {
			Double thisValue = this.values[i];
			Double otherValue = otherNote.values[i];
			int comparison = thisValue.compareTo(otherValue);
			if (comparison != 0) {
				return comparison;
			}
		}
		return 0;
	}

}

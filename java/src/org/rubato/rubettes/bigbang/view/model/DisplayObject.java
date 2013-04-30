package org.rubato.rubettes.bigbang.view.model;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.bigbang.view.subview.AbstractPainter;
import org.rubato.rubettes.bigbang.view.subview.DisplayContents;
import org.rubato.rubettes.util.DenotatorPath;

public class DisplayObject implements Comparable<Object> {
	
	private static final float DARK = 0.6f;
	private static final float BRIGHT = 1;
	
	private int topDenotatorType;
	private DenotatorPath topDenotatorPath;
	private DisplayContents display;
	private DisplayObject parent;
	private int relation;
	private List<DisplayObject> children;
	private List<Double> values;
	private List<Integer> structuralIndices;
	private int layer;
	private double xDiff, yDiff;
	private Rectangle2D.Double rectangle;
	private Point2D.Double center;
	private boolean selected;
	private boolean selectionVisible;
	private boolean visible;
	private boolean active;
	
	private float currentHue, currentOpacity;
	private int currentRed, currentBlue, currentGreen;
	private Color currentColor;
	
	public DisplayObject(DisplayObject parent, int relation, int topDenotatorType, List<Integer> structuralIndices, DenotatorPath topDenotatorPath) {
		this(parent, relation, topDenotatorType, structuralIndices, topDenotatorPath, 0);
	}
	
	public DisplayObject(DisplayObject parent, int relation, int topDenotatorType, List<Integer> structuralIndices, DenotatorPath topDenotatorPath, int layer) {
		this.values = new ArrayList<Double>();
		this.parent = parent;
		this.relation = relation;
		this.topDenotatorType = topDenotatorType;
		this.topDenotatorPath = topDenotatorPath;
		this.children = new ArrayList<DisplayObject>();
		this.selectionVisible = true;
		this.structuralIndices = structuralIndices;
		this.layer = layer;
		if (this.parent != null) {
			this.parent.addChild(this);
		}
	}
	
	public void setDisplay(DisplayContents display) {
		this.display = display;
	}
	
	public void setChildren(List<DisplayObject> children) {
		this.children = children;
	}
	
	public void addChild(DisplayObject newChild) {
		this.children.add(newChild);
	}
	
	public boolean hasChildren() {
		return this.children.size() > 0;
	}
	
	public DisplayObject getParent() {
		return this.parent;
	}
	
	public List<DisplayObject> getChildren() {
		return this.children;
	}
	
	public void setColimitIndex(int index) {
		this.structuralIndices.set(this.structuralIndices.size()-1, index);
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
	
	public void addValues(List<Double> values) {
		this.values.addAll(values);
	}
	
	public Double getValue(int index) {
		if (index >= values.size()) {
			index -= values.size();
			return this.structuralIndices.get(index).doubleValue();
		}
		return this.values.get(index);
	}
	
	public List<Double> getValues() {
		return this.values;
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
			if (this.display.getViewParameters().inRGBMode()) {
				return this.getRGBColor();
			}
			return this.getHueColor();
		}
		//70-100% of maximal brightness  
		float brightness = 0.3f*(1-this.currentOpacity)+0.7f;
		return Color.getHSBColor(this.currentHue, 0, brightness);
	}
	
	private Color getHueColor() {
		this.currentHue = new Float(this.display.translateValue(this.values, ViewParameters.HUE));
		return Color.getHSBColor(this.currentHue, this.currentOpacity, this.getBrightness());
	}
	
	private Color getRGBColor() {
		//brightness comes into play here, since there is no other way to make objects darker!
		this.currentRed = (int)Math.round(this.getBrightness()*this.display.translateValue(this.values, ViewParameters.RED));
		this.currentGreen = (int)Math.round(this.getBrightness()*this.display.translateValue(this.values, ViewParameters.GREEN));
		this.currentBlue = (int)Math.round(this.getBrightness()*this.display.translateValue(this.values, ViewParameters.BLUE));
		int alpha = Math.round(this.currentOpacity);
		return new Color(this.currentRed, this.currentGreen, this.currentBlue, alpha);
	}
	
	private float getBrightness() {
		if (this.selected && this.selectionVisible) {
			return DisplayObject.DARK;
		}
		return DisplayObject.BRIGHT;
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
			if (relation == DenotatorPath.SATELLITE) {
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
			painter.setColor(Color.getHSBColor(this.currentHue, this.currentOpacity, DisplayObject.DARK));
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
	
	public DenotatorPath getTopDenotatorPath() {
		return this.topDenotatorPath;
	}
	
	public boolean intersects(Rectangle2D.Double area) {
		return this.rectangle.intersects(area) || area.contains(this.center);
	}

	//why such an intricate compare method???
	public int compareTo(Object object) {
		if (!(object instanceof DisplayObject)) {
			throw new ClassCastException("DisplayObject expected.");
		}
		return this.topDenotatorPath.compareTo(((DisplayObject)object).getTopDenotatorPath());
		/*if (!(object instanceof DisplayObject)) {
			throw new ClassCastException("DisplayNote expected.");
		}
		DisplayObject otherNote = (DisplayObject)object;
		int layerCompare = new Integer(this.layer).compareTo(otherNote.layer);
		if (layerCompare != 0) { 
			return layerCompare;
		}
		for (int i = 0; i < this.values.size(); i++) {
			Double thisValue = this.values.get(i);
			Double otherValue = otherNote.values.get(i);
			int comparison = thisValue.compareTo(otherValue);
			if (comparison != 0) {
				return comparison;
			}
		}
		return 0;
		*/
	}

}

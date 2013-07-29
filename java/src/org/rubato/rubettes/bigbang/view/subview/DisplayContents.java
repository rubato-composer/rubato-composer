package org.rubato.rubettes.bigbang.view.subview;

import java.awt.Point;

import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.ViewParameter;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;

public class DisplayContents {
	
	//finally merge with display!!!!
	protected ViewParameters viewParameters;
	protected DisplayObjects displayObjects;
	private DisplayAxes axes;
	private DisplayTool tool;
	private DisplayPlaybackLine playbackLine;
	protected int currentWidth, currentHeight;
	protected double xZoomFactor, yZoomFactor;
	protected int xPosition, yPosition;
	private boolean satellitesConnected;
	
	public DisplayContents(BigBangPlayer player) {
		this.playbackLine = new DisplayPlaybackLine(this, player);
	}
	
	public void setNotes(DisplayObjects displayObjects) {
		for (DisplayObject currentNote : displayObjects.getObjects()) {
			currentNote.setDisplay(this);
		}
		this.displayObjects = displayObjects;
		this.updateNoteBounds();
		this.axes = new DisplayAxes(this, displayObjects.getCoordinateSystemValueNames());
	}
	
	public DisplayObjects getDisplayObjects() {
		return this.displayObjects;
	}
	
	public void setTool(DisplayTool tool) {
		this.tool = tool;
		if (this.tool != null) {
			this.tool.setDisplay(this);
		}
	}
	
	public void setViewParameters(ViewParameters viewParameters) {
		this.viewParameters = viewParameters;
	}
	
	public void setZoomFactors(double[] zoomFactors) {
		this.xZoomFactor = zoomFactors[0];
		this.yZoomFactor = zoomFactors[1];
		this.updateNoteBounds();
	}
	
	public void setPosition(Point position) {
		this.xPosition = position.x;
		this.yPosition = position.y;
		this.updateNoteBounds();
	}
	
	public void updateNoteBounds() {
		if (this.displayObjects != null) {
			this.displayObjects.updateBounds(this.xZoomFactor, this.yZoomFactor, this.xPosition, this.yPosition);
		}
	}
	
	public void setCurrentSize(int currentWidth, int currentHeight) {
		this.currentWidth = currentWidth;
		this.currentHeight = currentHeight;
	}
	
	public int getCurrentWidth() {
		return this.currentWidth;
	}
	
	public int getCurrentHeight() {
		return this.currentHeight;
	}
	
	public double getXZoomFactor() {
		return this.xZoomFactor;
	}
	
	public double getYZoomFactor() {
		return this.yZoomFactor;
	}
	
	public int getXPosition() {
		return this.xPosition;
	}
	
	public int getYPosition() {
		return this.yPosition;
	}
	
	public double getMinVisibleX() {
		double value = (0-this.xPosition)/this.xZoomFactor;
		//double value = this.xPosition;//this.xZoomFactor;
		return this.viewParameters.get(0).translateDisplayValue(value);
	}
	
	public double getMaxVisibleX() {
		double value = (this.currentWidth-this.xPosition)/this.xZoomFactor;
		return this.viewParameters.get(0).translateDisplayValue(value);
	}
	
	public double getMinVisibleY() {
		double value = (this.currentHeight-this.yPosition)/this.yZoomFactor;
		return this.viewParameters.get(1).translateDisplayValue(value);
	}
	
	public double getMaxVisibleY() {
		double value = (0-this.yPosition)/this.yZoomFactor;
		return this.viewParameters.get(1).translateDisplayValue(value);
	}
	
	public double translateXDenotatorValue(double value) {
		value = this.viewParameters.get(0).translateDenotatorValue(value);
		return value*this.xZoomFactor+this.xPosition;
	}
	
	public double translateYDenotatorValue(double value) {
		value = this.viewParameters.get(1).translateDenotatorValue(value);
		return value*this.yZoomFactor+this.yPosition;
	}
	
	public double getMaxY() {
		int selectedYParameter = this.viewParameters.getSelectedViewParameterAt(1);
		double[] maxValues = this.viewParameters.getMaxValues();
		if (selectedYParameter >= 0 && maxValues != null) {
			return maxValues[selectedYParameter];
		}
		return 0;
	}
	
	public ViewParameters getViewParameters() {
		return this.viewParameters;
	}
	
	public ViewParameter getViewParameter(int i) {
		return this.viewParameters.get(i);
	}
	
	public int getSelectedViewParameter(int i) {
		return this.viewParameters.getSelectedViewParameterAt(i);
	}
	
	public void setSatellitesConnected(boolean satellitesConnected) {
		this.satellitesConnected = satellitesConnected;
	}
	
	public boolean satellitesConnected() {
		return this.satellitesConnected;
	}
	
	public double translateValue(DisplayObject object, int viewParameterIndex) {
		int valueIndex = this.viewParameters.getSelectedViewParameterAt(viewParameterIndex);
		if (valueIndex > -1 && valueIndex < this.displayObjects.getCoordinateSystemValueNames().size()) {
			String valueName = this.displayObjects.getCoordinateSystemValueNames().get(valueIndex);
			int nameInstanceNumber = this.displayObjects.getInstanceNumberOfCoordinateValueName(valueIndex);
			Double denotatorValue = object.getNthValue(valueName, nameInstanceNumber);
			if (denotatorValue != null) {
				return this.viewParameters.get(viewParameterIndex).translateDenotatorValue(denotatorValue);
			}
		}
		return this.viewParameters.get(viewParameterIndex).getDefaultValue();
	}
	
	public void paint(AbstractPainter painter, int width, int height) {
		this.setCurrentSize(width, height);
		this.paintAxes(painter);
		this.paintNotes(painter);
		this.paintTool(painter);
		this.paintPlaybackLine(painter);
	}
	
	public void paintNotes(AbstractPainter painter) {
		this.displayObjects.paint(painter);
	}
	
	public void paintAxes(AbstractPainter painter) {
		this.axes.paint(painter);
	}
	
	public void paintTool(AbstractPainter painter) {
		if (this.tool != null) {
			this.tool.paint(painter);
		}
	}
	
	public void paintPlaybackLine(AbstractPainter painter) {
		this.playbackLine.paint(painter);
	}
	
	public int getTimeAxisIndex() {
		return this.displayObjects.getTimeAxisIndex(this.viewParameters);
	}

}

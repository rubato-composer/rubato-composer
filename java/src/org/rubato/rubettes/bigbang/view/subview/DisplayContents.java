package org.rubato.rubettes.bigbang.view.subview;

import org.rubato.rubettes.bigbang.view.model.DisplayObject;
import org.rubato.rubettes.bigbang.view.model.ViewParameter;
import org.rubato.rubettes.bigbang.view.model.ViewParameters;
import org.rubato.rubettes.bigbang.view.model.tools.DisplayTool;
import org.rubato.rubettes.bigbang.view.player.BigBangPlayer;
import org.rubato.rubettes.util.Point;

public class DisplayContents {
	
	//finally merge with display!!!!
	protected ViewParameters viewParameters;
	protected DisplayObjects displayObjects;
	private DisplayAxes axes;
	private DisplayTool tool;
	private DisplayPlaybackLine playbackLine;
	protected int currentWidth, currentHeight;
	protected double xZoomFactor, yZoomFactor;
	protected Point position;
	private boolean satellitesConnected;
	
	public DisplayContents() {
	}
	
	public DisplayContents(BigBangPlayer player) {
		this.playbackLine = new DisplayPlaybackLine(this, player);
	}
	
	public void setObjects(DisplayObjects displayObjects) {
		this.displayObjects = displayObjects;
		this.displayObjects.setDisplay(this);
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
	
	public void setZoomFactors(double[] factors) {
		this.xZoomFactor = factors[0];
		this.yZoomFactor = factors[1];
		this.updateNoteBounds();
	}
	
	public void setPosition(Point position) {
		this.position = position;
		this.updateNoteBounds();
	}
	
	public void centerView() {
		int xParameter = this.viewParameters.getSelectedViewParameterAt(0);
		int yParameter = this.viewParameters.getSelectedViewParameterAt(1);
		int widthParameter = this.viewParameters.getSelectedViewParameterAt(3);
		double minX = this.displayObjects.getMinValues().get(xParameter);
		double maxX = this.displayObjects.getMaxValues().get(xParameter);
		if (widthParameter >= 0) {
			maxX += this.displayObjects.getMaxValues().get(widthParameter);
		}
		double minY = this.displayObjects.getMinValues().get(yParameter);
		double maxY = this.displayObjects.getMaxValues().get(yParameter);
		//TODO maybe consider heightParameter too.....
		double xDiff = maxX-minX;
		double yDiff = maxY-minY;
		
		if (xDiff != 0 && yDiff != 0) {
			double xZoom = this.currentWidth/xDiff*0.9;
			double yZoom = this.currentHeight/yDiff*0.9;
			this.setZoomFactors(new double[]{xZoom, yZoom});
			
			int xPos = (int)Math.round((-1*this.viewParameters.get(0).translateDenotatorValue(minX)+1.2) * xZoom);
			int yPos = (int)Math.round(this.currentHeight+((-1*this.viewParameters.get(1).translateDenotatorValue(minY)-1.2) * yZoom));
			
			this.setPosition(new Point(xPos, yPos));
		}
	}
	
	public void updateNoteBounds() {
		if (this.displayObjects != null) {
			this.displayObjects.updateBounds(this.xZoomFactor, this.yZoomFactor, this.position.getX(), this.position.getY());
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
	
	public Point getPosition() {
		return this.position;
	}
	
	public double getMinVisibleX() {
		double value = (0-this.position.getX())/this.xZoomFactor;
		//double value = this.xPosition;//this.xZoomFactor;
		return this.viewParameters.get(0).translateDisplayValue(value);
	}
	
	public double getMaxVisibleX() {
		double value = (this.currentWidth-this.position.getX())/this.xZoomFactor;
		return this.viewParameters.get(0).translateDisplayValue(value);
	}
	
	public double getMinVisibleY() {
		double value = (this.currentHeight-this.position.getY())/this.yZoomFactor;
		return this.viewParameters.get(1).translateDisplayValue(value);
	}
	
	public double getMaxVisibleY() {
		double value = (0-this.position.getY())/this.yZoomFactor;
		return this.viewParameters.get(1).translateDisplayValue(value);
	}
	
	public double translateXDenotatorValue(double value) {
		value = this.viewParameters.get(0).translateDenotatorValue(value);
		return value*this.xZoomFactor+this.position.getX();
	}
	
	public double translateYDenotatorValue(double value) {
		value = this.viewParameters.get(1).translateDenotatorValue(value);
		return value*this.yZoomFactor+this.position.getY();
	}
	
	public double[] getXYDisplayValues(double[] denotatorValues) {
		return new double[] {this.translateXDenotatorValue(denotatorValues[0]),
				this.translateYDenotatorValue(denotatorValues[1])};
	}
	
	
	/*public double getMaxY() {
		int selectedYParameter = this.viewParameters.getSelectedViewParameterAt(1);
		double[] maxValues = this.viewParameters.getMaxValues();
		if (selectedYParameter >= 0 && maxValues != null) {
			return maxValues[selectedYParameter];
		}
		return 0;
	}*/
	
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
		if (this.playbackLine != null) {
			this.playbackLine.paint(painter);
		}
	}
	
	public int getTimeAxisIndex() {
		return this.displayObjects.getTimeAxisIndex(this.viewParameters);
	}

}

package org.rubato.rubettes.bigbang.view.model;

import org.rubato.rubettes.bigbang.model.Model;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.util.Dimension;
import org.rubato.rubettes.util.Point;
import org.rubato.rubettes.util.PointND;

public class FactsViewState extends Model {
	
	protected Point position;
	protected double xZoomFactor, yZoomFactor;
	private ViewParameters viewParameters;
	
	public FactsViewState(ViewController controller, boolean invertYAxis) {
		this.viewParameters = new ViewParameters(controller, invertYAxis);
		controller.addModel(this);
	}
	
	public Point getDisplayPosition() {
		return this.position;
	}
	
	public void changeDisplayPosition(Dimension difference) {
		this.position.translate(difference.getWidth(), difference.getHeight());
		this.firePropertyChange(ViewController.DISPLAY_POSITION, null, this.position);
	}
	
	public void setDisplayPosition(Point position) {
		this.position = position;
		this.firePropertyChange(ViewController.DISPLAY_POSITION, null, this.position);
	}
	
	public void setZoomFactors(Double xZoomFactor, Double yZoomFactor) {
		this.xZoomFactor = xZoomFactor;
		this.yZoomFactor = yZoomFactor;
		double[] newValue = new double[]{xZoomFactor, yZoomFactor};
		this.firePropertyChange(ViewController.ZOOM_FACTORS, null, newValue);
	}
	
	public void changeZoomFactors(ZoomChange zoomChange) {
		double oldXZoomFactor = this.xZoomFactor;
		double oldYZoomFactor = this.yZoomFactor;
		double xZoomFactor = this.xZoomFactor * zoomChange.getXChangeFactor();
		double yZoomFactor = this.yZoomFactor * zoomChange.getYChangeFactor();
		this.setZoomFactors(xZoomFactor, yZoomFactor);
		//zusammenfassen????
		int x = zoomChange.getX();
		int xPos = this.position.getX();
		int xPosition = (int)Math.round(xPos + (x-xPos)*(1-(this.xZoomFactor/oldXZoomFactor)));
		int y = zoomChange.getY();
		int yPos = this.position.getY();
		int yPosition = (int)Math.round(yPos + (y-yPos)*(1-(this.yZoomFactor/oldYZoomFactor)));
		this.setDisplayPosition(new Point(xPosition, yPosition));
	}
	
	public ViewParameters getViewParameters() {
		return this.viewParameters;
	}
	
	public double[] getXYZDenotatorValues(PointND location) {
		double xValue = this.translateXDisplayValue(location.getCoord(0));
		double yValue = this.translateYDisplayValue(location.getCoord(1));
		if (location.getDimension() > 2) {
			return new double[]{xValue, yValue, this.translateZDisplayValue(location.getCoord(2))};
		}
		//System.out.println(location + " " + xValue + " " + yValue + " " + this.position + " " + this.xZoomFactor);
		return new double[] {xValue, yValue};
	}
	
	public double getDenotatorValue(double displayValue, int parameterIndex) {
		if (parameterIndex == 0) {
			return this.translateXDisplayValue(displayValue);
		} else if (parameterIndex == 1) {
			return this.translateYDisplayValue(displayValue);
		}
		return this.translateZDisplayValue(displayValue);
	}
	
	private double translateXDisplayValue(double displayValue) {
		double value = (displayValue-this.position.getX())/this.xZoomFactor;
		return this.viewParameters.get(0).translateDisplayValue(value);		
	}
	
	private double translateYDisplayValue(double displayValue) {
		double value = (displayValue-this.position.getY())/this.yZoomFactor;
		return this.viewParameters.get(1).translateDisplayValue(value);		
	}
	
	private double translateZDisplayValue(double displayValue) {
		double avgZoom = (this.xZoomFactor + this.yZoomFactor) / 2; //avgZoom was 2 before...
		double value = displayValue/avgZoom;
		ViewParameter param = this.viewParameters.get(2); 
		param.setDenotatorLimitsIfNotManual(param.getMinGoalValue(), param.getMaxGoalValue()); 
		return this.viewParameters.get(2).translateDisplayValue(value);		
	}

}

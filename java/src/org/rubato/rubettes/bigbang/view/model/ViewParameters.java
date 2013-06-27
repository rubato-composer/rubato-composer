package org.rubato.rubettes.bigbang.view.model;

import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.bigbang.model.Model;
import org.rubato.rubettes.bigbang.view.controller.ViewController;

public class ViewParameters extends Model {
	
	public static final int X = 0;
	public static final int Y = 1;
	public static final int SATURATION = 2;
	public static final int WIDTH = 3;
	public static final int HUE = 4;
	public static final int RED = 4;
	public static final int GREEN = 5;
	public static final int BLUE = 6;
	public static int HEIGHT;
	//public static final int VIEW_PARAMETER_COUNT = 6;
	private static final String[] HUE_VIEW_PARAMETER_NAMES = new String[]{"X-Axis", "Y-Axis", "Opacity", "Width", "Color", "Height"};
	private static final String[] RGB_VIEW_PARAMETER_NAMES = new String[]{"X-Axis", "Y-Axis", "Alpha", "Width", "Red", "Green", "Blue", "Height"};
	
	private boolean rgbMode;
	private boolean invertYAxis;
	private ArrayList<ViewParameter> parameters;
	private int[] selectedViewParameters;
	private List<Double> minValues, maxValues;
	
	public ViewParameters(ViewController controller, boolean invertYAxis) {
		controller.addModel(this);
		this.setRGBMode(true);
		this.invertYAxis = invertYAxis;
		this.minValues = new ArrayList<Double>();
		this.maxValues = new ArrayList<Double>();
		this.initParameters(invertYAxis);
		//this.initSelections(5);
	}
	
	private void initParameters(boolean invertYAxis) {
		this.parameters = new ArrayList<ViewParameter>();
		this.parameters.add(new ViewParameter(HUE_VIEW_PARAMETER_NAMES[0], false, 0, false)); //x
		this.parameters.add(new ViewParameter(HUE_VIEW_PARAMETER_NAMES[1], invertYAxis, 0, false)); //y
		//ViewParameter opacity = new ViewParameter("opacity", false, 1, 0, 1, false);
		//opacity.setMinAndMaxGoalValues(false, 0, 127, false);
		if (this.rgbMode) {
			this.parameters.add(new ViewParameter(RGB_VIEW_PARAMETER_NAMES[2], false, 200, 64, 255, false)); //alpha
		} else {
			this.parameters.add(new ViewParameter(HUE_VIEW_PARAMETER_NAMES[2], false, 1, 0.35, 1, false)); //saturation
		}
		this.parameters.add(new ViewParameter(HUE_VIEW_PARAMETER_NAMES[3], false, 1, false)); //width
		if (this.rgbMode) {
			this.parameters.add(new ViewParameter(RGB_VIEW_PARAMETER_NAMES[4], false, 127, 0, 255, false)); //red
			this.parameters.add(new ViewParameter(RGB_VIEW_PARAMETER_NAMES[5], false, 127, 0, 255, false)); //green
			this.parameters.add(new ViewParameter(RGB_VIEW_PARAMETER_NAMES[6], false, 127, 0, 255, false)); //blue
		} else {
			this.parameters.add(new ViewParameter(HUE_VIEW_PARAMETER_NAMES[4], false, 0.0833, 0.0833, 1.0833, true)); //hue
		}
		this.parameters.add(new ViewParameter(HUE_VIEW_PARAMETER_NAMES[5], false, 1, 1, 10, false)); //height
		this.firePropertyChange(ViewController.VIEW_PARAMETERS, null, this);
	}
	
	public void setRGBMode(boolean rgbMode) {
		if (rgbMode != this.rgbMode) {
			this.rgbMode = rgbMode;
			if (this.rgbMode) {
				ViewParameters.HEIGHT = 7;
			} else {
				ViewParameters.HEIGHT = 5;
			}
			this.initParameters(this.invertYAxis);
		}
	}
	
	public boolean inRGBMode() {
		return this.rgbMode;
	}
	
	public int size() {
		return this.parameters.size();
	}
	
	public String[] getParameterNames() {
		if (this.rgbMode) {
			return ViewParameters.RGB_VIEW_PARAMETER_NAMES;
		}
		return ViewParameters.HUE_VIEW_PARAMETER_NAMES;
	}
	
	public ViewParameter get(int index) {
		return this.parameters.get(index);
	}
	
	public void setSelectedXYViewParameters(int[] newSelections) {
		this.selectedViewParameters[0] = newSelections[0];
		this.selectedViewParameters[1] = newSelections[1];
		this.updateMinAndMaxValues();
		this.firePropertyChange(ViewController.SELECTED_VIEW_PARAMETERS, null, this.selectedViewParameters);
	}
	
	public int[] getSelectedXYViewParameters() {
		return new int[]{this.selectedViewParameters[0], this.selectedViewParameters[1]};
	}
	
	//sets the first given number of view parameters diagonally
	public void initSelections(int numberOfSelectedOnes) {
		int[] newSelections = new int[this.size()];
		int i = 0;
		for (; i < Math.min(numberOfSelectedOnes, 6); i++) {
			newSelections[i] = i;
		}
		for (; i < newSelections.length; i++) {
			newSelections[i] = -1;
		}
		this.setSelectedViewParameters(newSelections);
	}
	
	public void setSelectedViewParameters(int[] newSelections) {
		this.selectedViewParameters = newSelections;
		this.updateMinAndMaxValues();
		this.firePropertyChange(ViewController.SELECTED_VIEW_PARAMETERS, null, this.selectedViewParameters);
	}
	
	public int getFirstIndexOfValue(int valueIndex) {
		for (int i = 0; i < this.selectedViewParameters.length; i++) {
			if (this.selectedViewParameters[i] == valueIndex) {
				return i;
			}
		}
		return -1;
	}
	
	public void setDenotatorMinAndMaxValues(List<Double> minValues, List<Double> maxValues) {
		this.minValues = minValues;
		this.maxValues = maxValues;
		this.updateMinAndMaxValues();
	}
	
	public void setManualDenotatorLimits(Integer index, Boolean manual, Double minValue, Double maxValue) {
		ViewParameter parameter = this.parameters.get(index);
		parameter.setManualDenotatorLimits(manual, minValue, maxValue);
		this.firePropertyChange(ViewController.VIEW_PARAMETERS, null, this);
	}
	
	public void setParameterMinAndMax(Integer index, Boolean relative, Double minValue, Double maxValue, Boolean cyclic) {
		this.parameters.get(index).setMinAndMaxGoalValues(relative, minValue, maxValue, cyclic);
		this.firePropertyChange(ViewController.VIEW_PARAMETERS, null, this);
	}
	
	public double[] getMaxValues() {
		return this.getMaxValues();
	}
	
	private void updateMinAndMaxValues() {
		for (int i = 0; i < this.parameters.size(); i++) {
			int d = this.selectedViewParameters[i];
			//System.out.println(this.minValues[1]+" "+this.maxValues[1]);
			if (d > -1 && this.minValues.size() > d && this.maxValues.size() > d) {
				this.parameters.get(i).setDenotatorLimitsIfNotManual(this.minValues.get(d), this.maxValues.get(d));
			}
		}
		this.firePropertyChange(ViewController.VIEW_PARAMETERS, null, this);
	}

}

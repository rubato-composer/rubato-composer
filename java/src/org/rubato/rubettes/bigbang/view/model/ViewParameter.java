package org.rubato.rubettes.bigbang.view.model;

public class ViewParameter {
	
	private String name;
	private boolean manualDenotatorLimits;
	private double minDenotatorValue, maxDenotatorValue;
	private double minGoalValue, maxGoalValue;
	private double defaultGoalValue;
	private boolean relative, invert, cyclic;
	
	public ViewParameter(String name, boolean invert, double defaultValue, double minGoalValue, double maxGoalValue, boolean cyclic) {
		this.name = name;
		this.relative = true;
		this.invert = invert;
		this.defaultGoalValue = defaultValue;
		this.setMinAndMaxGoalValues(minGoalValue, maxGoalValue, cyclic);
	}
	
	public ViewParameter(String name, boolean invert, double defaultValue, boolean cyclic) {
		this.name = name;
		this.relative = false; //means that there is no max value
		this.invert = invert;
		this.defaultGoalValue = defaultValue;
		this.cyclic = cyclic;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setManualDenotatorLimits(boolean manual, double minValue, double maxValue) {
		this.manualDenotatorLimits = manual;
		this.setDenotatorLimits(minValue, maxValue);
	}
	
	public void setDenotatorLimitsIfNotManual(double minValue, double maxValue) {
		if (!this.manualDenotatorLimits) {
			this.setDenotatorLimits(minValue, maxValue);
		}
	}
	
	private void setDenotatorLimits(double minValue, double maxValue) {
		if (maxValue > minValue) {
			this.minDenotatorValue = minValue;
			this.maxDenotatorValue = maxValue;
		}
	}
	
	public void setManualDenotatorLimits(boolean manualLimits) {
		this.manualDenotatorLimits = manualLimits;
	}
	
	public boolean manualDenotatorLimits() {
		return this.manualDenotatorLimits;
	}
	
	public double getMinDenotatorValue() {
		return this.minDenotatorValue;
	}
	
	public double getMaxDenotatorValue() {
		return this.maxDenotatorValue;
	}
	
	public boolean isRelative() {
		return this.relative;
	}
	
	public void setMinAndMaxGoalValues(boolean relative, double minValue, double maxValue, boolean cyclic) {
		this.relative = relative;
		if (relative) {
			this.setMinAndMaxGoalValues(minValue, maxValue, cyclic);
		} else {
			this.setMinAndMaxGoalValues(0, 0, false);
		}
	}
	
	private void setMinAndMaxGoalValues(double minValue, double maxValue, boolean cyclic) {
		if (maxValue > minValue) {
			this.minGoalValue = minValue;
			this.maxGoalValue = maxValue;
			this.cyclic = cyclic;
		}
	}
	
	public double getMinGoalValue() {
		return this.minGoalValue;
	}
	
	public double getMaxGoalValue() {
		return this.maxGoalValue;
	}
	
	public boolean isCyclic() {
		return this.cyclic;
	}
	
	//relativ (mit interval) nur wenn maxgoalbvalue definiert!
	public double translateDenotatorValue(double denotatorValue) {
		double translatedValue = denotatorValue;
		if (!this.relative) {
			return this.negateIfNecessary(translatedValue);
		}
		translatedValue = this.getLimitedValue(translatedValue, this.minDenotatorValue, this.maxDenotatorValue);
		translatedValue = this.mapTo01(translatedValue, this.minDenotatorValue, this.maxDenotatorValue);
		translatedValue = this.invertIfNecessary(translatedValue);
		return this.mapFrom01(translatedValue, this.minGoalValue, this.maxGoalValue);
	}
	
	public double translateDisplayValue(double displayValue) {
		double translatedValue = displayValue;
		if (!this.relative) {
			return this.negateIfNecessary(translatedValue);
		}
		translatedValue = this.mapTo01(translatedValue, this.minGoalValue, this.maxGoalValue);
		translatedValue = this.invertIfNecessary(translatedValue);
		translatedValue = this.mapFrom01(translatedValue, this.minDenotatorValue, this.maxDenotatorValue);
		return translatedValue;
	}
	
	public double getLimitedValue(double value, double minValue, double maxValue) {
		if (this.cyclic) {
			if (value == maxValue) {
				return maxValue;
			}
			value %= maxValue-minValue;
			if (value < 0) {
				value += maxValue; //because java uses symmetric modulo implementation
			}
			return value+minValue;
		}
		value = Math.max(value, minValue);
		return Math.min(value, maxValue);
	}
	
	private double mapTo01(double value, double minValue, double maxValue) {
		double q = maxValue-minValue;
		if (q > 0) {
			return (value-minValue)/(maxValue-minValue);
		}
		return 0.0; //TODO: PREVIOUSLY: this.defaultGoalValue; WHY????
	}
	
	private double mapFrom01(double value, double minValue, double maxValue) {
		return value*(maxValue-minValue)+minValue;
	}
	
	private double invertIfNecessary(double value) {
		if (this.invert) {
			return 1-value;
		}
		return value;
	}
	
	private double negateIfNecessary(double value) {
		if (this.invert) {
			return -1*value;
		}
		return value;
	}
	
	public double getDefaultValue() {
		return this.defaultGoalValue;
	}

}

package org.rubato.rubettes.util;

import java.util.List;

import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.PowerDenotator;

public class DenotatorAnalyzer {
	
	//TODO: take that functionality out of ObjectGenerator!!!
	private ObjectGenerator generator = new ObjectGenerator();
	
	public double[] getMinAndMaxValue(PowerDenotator powerset, int valueIndex) {
		List<Denotator> factors = powerset.getFactors();
		List<DenotatorPath> formValuePaths = new DenotatorValueFinder(factors.get(0).getForm(), false).getValuePathsInFoundOrder();
		if (formValuePaths.size() > valueIndex) {
			DenotatorPath valuePath = formValuePaths.get(valueIndex);
			double currentMin = Double.MAX_VALUE;
			double currentMax = Double.MIN_VALUE;
			for (Denotator currentFactor : factors) {
				double currentValue = this.generator.getDoubleValue(currentFactor, valuePath);
				currentMin = Math.min(currentMin, currentValue);
				currentMax = Math.max(currentMax, currentValue);
			}
			return new double[]{currentMin, currentMax};
		}
		return null;
	}

}

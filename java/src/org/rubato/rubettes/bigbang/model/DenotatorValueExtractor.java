package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.base.RubatoException;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ProductElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.yoneda.ColimitDenotator;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.FactorDenotator;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.FormValueFinder;
import org.rubato.rubettes.util.PerformanceCheck;

//TODO: deal with case that different occurences of same form have independent maxima!!!  
public class DenotatorValueExtractor {
	
	public static final String SATELLITE_LEVEL = "Satellite Level";
	public static final String SIBLING_NUMBER = "Sibling number";
	public static final String COLIMIT_INDEX = "Colimit index";
	
	private Map<String,Double> minValues, maxValues;
	private BigBangObjects objects;
	
	public DenotatorValueExtractor(BigBangObjects objects, Denotator composition) {
		this.objects = objects;
		this.minValues = new TreeMap<String,Double>();
		this.maxValues = new TreeMap<String,Double>();
		this.extractValues(composition);
	}
	
	private void extractValues(Denotator composition) {
		//PerformanceCheck.startTask("extract");
		Form baseForm = composition.getForm();
		this.minValues = new TreeMap<String,Double>();
		this.maxValues = new TreeMap<String,Double>();
		this.objects.clearValues();
		/*if (this.objects != null && baseForm.equals(this.objects.getBaseForm())) {
			this.objects.clearObjects();
		} else {
			this.objects = new BigBangObjects(baseForm);
		}*/
		try {
			if (this.objects.getObjectsAt(null) != null) {
				//System.out.println(this.objects.getObjectsAt(null).size() + " " + ((PowerDenotator)composition).getFactorCount());
				//composition.display();
			}
			this.extractObjects(composition, null, null, 0, 0, 0, new DenotatorPath(baseForm));
		} catch (RubatoException e) { e.printStackTrace(); }
		this.objects.setMinMaxValues(this.getMinValues(), this.getMaxValues());
	}
	
	//recursive method!!
	private void extractObjects(Denotator currentDenotator, BigBangObject parentObject, BigBangObject currentObject, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath currentPath) throws RubatoException {
		int denotatorType = currentDenotator.getType();
		if (denotatorType == Denotator.POWER || denotatorType == Denotator.LIST) {
			FactorDenotator currentPower = (FactorDenotator)currentDenotator;
			//System.out.println(currentDO + " " + satelliteLevel);
			for (int i = 0; i < currentPower.getFactorCount(); i++) {
				//call with currentDisplayObject and currentJSynObject null, since all children become independent objects
				if (currentObject != null) {
					this.extractObjects(currentPower.getFactor(i), currentObject, null, satelliteLevel+1, i, colimitIndex, currentPath.getChildPath(i));
				} else {
					this.extractObjects(currentPower.getFactor(i), currentObject, null, satelliteLevel, i, colimitIndex, currentPath.getChildPath(i));
				}
			}
		} else {
			if (currentObject == null) {
				currentObject = this.getBigBangObject(parentObject, satelliteLevel, siblingNumber, colimitIndex, currentPath);
			}
			if (currentObject != null) {
				if (denotatorType == Denotator.LIMIT) {
					LimitDenotator currentLimit = (LimitDenotator)currentDenotator;
					for (int i = 0; i < currentLimit.getFactorCount(); i++) {
						Denotator currentChild = currentLimit.getFactor(i);
						this.extractObjects(currentChild, parentObject, currentObject, satelliteLevel, siblingNumber, colimitIndex, currentPath.getChildPath(i));
					}
				} else if (denotatorType == Denotator.COLIMIT) {
					ColimitDenotator currentColimit = (ColimitDenotator)currentDenotator;
					Denotator onlyChild = currentColimit.getFactor();
					int childIndex = currentColimit.getIndex();
					currentObject.setColimitIndex(colimitIndex+childIndex);
					colimitIndex += currentColimit.getFactorCount();
					for (int i = 0; i < currentColimit.getForm().getFormCount(); i++) {
						if (i == childIndex) {
							DenotatorPath childPath = currentPath.getChildPath(childIndex);
							//TODO: uiui, not great, but should work for now
							DenotatorPath path = childPath;
							if (childPath.getTopPath().size() > 0) {
								path = path.subPath(childPath.size()-childPath.getTopPath().size());
							}
							currentObject.setObjectType(this.objects.getObjectType(currentObject.getTopDenotatorPath().getEndForm(), path));
							this.extractObjects(onlyChild, parentObject, currentObject, satelliteLevel, siblingNumber, childIndex, childPath);
						}
					}
				} else if (denotatorType == Denotator.SIMPLE) {
					this.addSimpleValues(parentObject, currentObject, (SimpleDenotator)currentDenotator);
				}
			}
		}
	}
	
	private BigBangObject getBigBangObject(BigBangObject parentObject, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath path) {
		BigBangObject object = this.objects.getObject(path);
		if (object != null) {
			//System.out.println("GBB "+path + " " + object);
			List<Integer> structuralValues = new ArrayList<Integer>();
			if (this.objects.baseFormAllowsForSatellites()) {
				structuralValues.add(satelliteLevel);
				structuralValues.add(siblingNumber);
			}
			if (this.objects.baseFormContainsColimits()) {
				structuralValues.add(colimitIndex);
			}
			object.setStructuralIndices(structuralValues);
			//object.setParent(parentObject);
			object.setObjectType(this.objects.getStandardObjectType(path.getEndForm()));
			if (this.objects.baseFormAllowsForSatellites()) {
				this.objects.updateMaxSatelliteLevels(object);
			}
		}
		//System.out.println(path + " " + object);
		return object;
	}
	
	private void addSimpleValues(BigBangObject parent, BigBangObject object, SimpleDenotator simpleDenotator) {
		List<String> valueNames = new ArrayList<String>();
		List<Double> values = new ArrayList<Double>();
		this.extractValues(simpleDenotator, parent, object, valueNames, values);
		object.addValues(values);
	}
	
	//TODO: maybe outsource, join with ObjectGenerator.createModule
	private void extractValues(SimpleDenotator denotator, BigBangObject parent, BigBangObject object, List<String> valueNames, List<Double> values) {
		String simpleName = denotator.getForm().getNameString();
		this.extractValues(parent, object, simpleName, denotator.getElement(), valueNames, values, "");
		this.updateMinAndMax(valueNames, values);
	}
	
	private void extractValues(BigBangObject parent, BigBangObject object, String simpleName, ModuleElement currentElement, List<String> valueNames, List<Double> values, String indexString) {
		if (currentElement instanceof ProductElement) {
			ProductElement productElement = (ProductElement)currentElement;
			for (int i = 0; i < productElement.getFactorCount(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				this.extractValues(parent, object, simpleName, productElement.getFactor(i), valueNames, values, indexString+(i+1));
			}
		} else if (currentElement.getModule().getDimension() > 1) {
			for (int i = 0; i < currentElement.getModule().getDimension(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				this.extractValues(parent, object, simpleName, currentElement.getComponent(i), valueNames, values, indexString+(i+1));
			}
		} else {
			String valueName = FormValueFinder.makeValueName(simpleName, currentElement.getModule(), indexString);
			double value = ((RElement)currentElement.cast(RRing.ring)).getValue();
			int nextIndex = object.getCurrentOccurrencesOfValueName(valueName);
			if (parent != null) {
				Double parentValue = parent.getNthValue(valueName, nextIndex);
				if (parentValue != null) {
					value += parentValue;
				}
			}
			valueNames.add(valueName);
			values.add(value);
		}
	}
	
	private void updateMinAndMax(List<String> valueNames, List<Double> values) {
		//TODO: consider multiple occurrences??? maybe not!! 
		for (int i = 0; i < valueNames.size(); i++) {
			String currentValueName = valueNames.get(i);
			Double currentValue = values.get(i);
			if (!this.minValues.keySet().contains(currentValueName)) {
				this.minValues.put(currentValueName, Double.MAX_VALUE);
				this.maxValues.put(currentValueName, -1*Double.MAX_VALUE);
			}
			this.minValues.put(currentValueName, Math.min(currentValue, this.minValues.get(currentValueName)));
			this.maxValues.put(currentValueName, Math.max(currentValue, this.maxValues.get(currentValueName)));
		}
	}
	
	private List<Double> getMinValues() {
		List<Double> minValues = new ArrayList<Double>();
		for (String currentValueName : this.objects.getCoordinateSystemValueNames()) {
			Double currentValue = this.minValues.get(currentValueName);
			if (currentValue != null) {
				minValues.add(currentValue);
			}
		}
		return minValues;
	}
	
	private List<Double> getMaxValues() {
		List<Double> maxValues = new ArrayList<Double>();
		for (String currentValueName : this.objects.getCoordinateSystemValueNames()) {
			Double currentValue = this.maxValues.get(currentValueName);
			if (currentValue != null) {
				maxValues.add(currentValue);
			}
		}
		return maxValues;
	}

}

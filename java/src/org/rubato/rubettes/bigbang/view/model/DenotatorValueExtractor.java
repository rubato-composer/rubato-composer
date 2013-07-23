package org.rubato.rubettes.bigbang.view.model;

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
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.player.JSynObject;
import org.rubato.rubettes.bigbang.view.player.JSynScore;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjects;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.FormValueFinder;

//TODO: deal with case that different occurences of same form have independent maxima!!!  
public class DenotatorValueExtractor {
	
	public static final String SATELLITE_LEVEL = "Satellite Level";
	public static final String SIBLING_NUMBER = "Sibling number";
	public static final String COLIMIT_INDEX = "Colimit index";
	
	private int maxLayer;
	private Map<String,Double> minValues, maxValues;
	private DisplayObjects displayObjects;
	private JSynScore jSynScore;
	//TreeSet in future for faster searching!!!
	private boolean selectObjects;
	private SelectedObjectsPathss selectedPaths;
	private LayerStates layerStates;
	
	public DenotatorValueExtractor(LayerStates layerStates) {
		//PerformanceCheck.startTask("extract");
		this.layerStates = layerStates;
	}
	
	//TODO: not really nice, but used this way just to get JSynScore of independent object..
	public DisplayObjects extractValues(Denotator score) {
		//PerformanceCheck.startTask("extract");
		this.layerStates = new LayerStates(new ViewController());
		return this.initAndExtract(new ViewController(), score, false);
	}
	
	public DisplayObjects extractValues(ViewController controller, ScoreChangedNotification notification, boolean selectObjects) {
		//PerformanceCheck.startTask("extract");
		this.selectedPaths = notification.getSelectedObjectsPaths();
		return this.initAndExtract(controller, notification.getScore(), selectObjects);
	}
	
	private DisplayObjects initAndExtract(ViewController controller, Denotator score, boolean selectObjects) {
		Form baseForm = score.getForm();
		this.minValues = new TreeMap<String,Double>();
		this.maxValues = new TreeMap<String,Double>();
		if (this.displayObjects != null && baseForm.equals(this.displayObjects.getBaseForm())) {
			this.displayObjects.clearObjects();
		} else {
			this.displayObjects = new DisplayObjects(controller, baseForm);
		}
		this.jSynScore = new JSynScore();
		this.selectObjects = selectObjects;
		try {
			this.extractObjects(score, null, null, null, null, 0, 0, 0, new DenotatorPath(baseForm));
		} catch (RubatoException e) { e.printStackTrace(); }
		this.layerStates.removeLayers(this.maxLayer);
		return this.displayObjects;
	}
	
	//recursive method!!
	//TODO: remove relation!! not very interesting anymore..
	private void extractObjects(Denotator currentDenotator, DisplayObject parentDO, DisplayObject currentDO, JSynObject parentJSO, JSynObject currentJSO, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath currentPath) throws RubatoException {
		int denotatorType = currentDenotator.getType();
		if (denotatorType == Denotator.POWER || denotatorType == Denotator.LIST) {
			FactorDenotator currentPower = (FactorDenotator)currentDenotator;
			//TODO: find out if modulators and add them to JSynObject!!
			for (int i = 0; i < currentPower.getFactorCount(); i++) {
				//call with currentDisplayObject and currentJSynObject null, since all children become independent objects
				this.extractObjects(currentPower.getFactor(i), currentDO, null, currentJSO, null, satelliteLevel+1, i, colimitIndex, currentPath.getChildPath(i));
			}
		} else {
			if (currentDO == null) {
				currentDO = this.addDisplayObject(parentDO, satelliteLevel, siblingNumber, colimitIndex, currentPath);
				currentJSO = this.jSynScore.addNewObject(parentJSO, currentDenotator.getForm());
			}
			if (denotatorType == Denotator.LIMIT) {
				LimitDenotator currentLimit = (LimitDenotator)currentDenotator;
				for (int i = 0; i < currentLimit.getFactorCount(); i++) {
					Denotator currentChild = currentLimit.getFactor(i);
					this.extractObjects(currentChild, parentDO, currentDO, parentJSO, currentJSO, satelliteLevel, siblingNumber, colimitIndex, currentPath.getChildPath(i));
				}
			} else if (denotatorType == Denotator.COLIMIT) {
				ColimitDenotator currentColimit = (ColimitDenotator)currentDenotator;
				Denotator onlyChild = currentColimit.getFactor();
				int childIndex = currentColimit.getIndex();
				currentDO.setColimitIndex(colimitIndex+childIndex);
				colimitIndex += currentColimit.getFactorCount();
				for (int i = 0; i < currentColimit.getForm().getFormCount(); i++) {
					if (i == childIndex) {
						DenotatorPath childPath = currentPath.getChildPath(childIndex);
						//TODO: uiui, not great, but should work for now
						DenotatorPath path = childPath;
						if (childPath.getTopPath().size() > 0) {
							path = path.subPath(childPath.size()-childPath.getTopPath().size());
						}
						currentDO.setObjectType(this.displayObjects.getObjectType(currentDO.getTopDenotatorPath().getEndForm(), path));
						this.extractObjects(onlyChild, parentDO, currentDO, parentJSO, currentJSO, satelliteLevel, siblingNumber, childIndex, childPath);
					}
				}
			} else if (denotatorType == Denotator.SIMPLE) {
				this.addSimpleValues(parentDO, currentDO, currentJSO, (SimpleDenotator)currentDenotator);
			}
		}
	}
	
	private DisplayObject addDisplayObject(DisplayObject parent, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath path) {
		DisplayObject displayObject = this.createDisplayObject(parent, satelliteLevel, siblingNumber, colimitIndex, path);
		displayObject.setObjectType(this.displayObjects.getStandardObjectType(path.getEndForm()));
		displayObject.setVisibility(this.layerStates.get(displayObject.getLayer()));
		this.displayObjects.addObject(displayObject);
		if (this.selectObjects && this.selectedPaths != null) {
			if (this.selectedPaths.containsObjectPath(path)) {
				this.displayObjects.selectNote(displayObject);
			}
			DenotatorPath selectedAnchorPath = this.selectedPaths.getAnchorPath();
			if (selectedAnchorPath != null && selectedAnchorPath.equals(path)) {
				this.displayObjects.setSelectedAnchorNote(displayObject);
			}
		}
		return displayObject;
	}
	
	private DisplayObject createDisplayObject(DisplayObject parent, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath path) {
		List<Integer> structuralValues = new ArrayList<Integer>();
		if (this.displayObjects.baseFormAllowsForSatellites()) {
			structuralValues.add(satelliteLevel);
			structuralValues.add(siblingNumber);
		}
		if (this.displayObjects.baseFormContainsColimits()) {
			structuralValues.add(colimitIndex);
		}
		return new DisplayObject(parent, structuralValues, path.clone());
	}
	
	private void addSimpleValues(DisplayObject parentDO, DisplayObject displayObject, JSynObject jSynObject, SimpleDenotator simpleDenotator) {
		List<String> valueNames = new ArrayList<String>();
		List<Double> values = new ArrayList<Double>();
		this.extractValues(simpleDenotator, parentDO, displayObject, valueNames, values);
		displayObject.addValues(values);
		jSynObject.addValues(simpleDenotator.getForm(), values); //needs to know forms!!
	}
	
	//TODO: maybe outsource, join with ObjectGenerator.createModule
	private void extractValues(SimpleDenotator denotator, DisplayObject parent, DisplayObject object, List<String> valueNames, List<Double> values) {
		String simpleName = denotator.getForm().getNameString();
		this.extractValues(parent, object, simpleName, denotator.getElement(), valueNames, values, "");
		this.updateMinAndMax(valueNames, values);
	}
	
	private void extractValues(DisplayObject parent, DisplayObject object, String simpleName, ModuleElement currentElement, List<String> valueNames, List<Double> values, String indexString) {
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
	
	public JSynScore getJSynScore() {
		return this.jSynScore;
	}
	
	public List<Double> getMinValues() {
		List<Double> minValues = new ArrayList<Double>();
		for (String currentValueName : this.displayObjects.getCoordinateSystemValueNames()) {
			Double currentValue = this.minValues.get(currentValueName);
			if (currentValue != null) {
				minValues.add(currentValue);
			}
		}
		return minValues;
	}
	
	public List<Double> getMaxValues() {
		List<Double> maxValues = new ArrayList<Double>();
		for (String currentValueName : this.displayObjects.getCoordinateSystemValueNames()) {
			Double currentValue = this.maxValues.get(currentValueName);
			if (currentValue != null) {
				maxValues.add(currentValue);
			}
		}
		return maxValues;
	}

}

package org.rubato.rubettes.bigbang.view.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.rubato.rubettes.bigbang.view.subview.DisplayObjectList;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.DenotatorValueFinder;

//TODO: deal with case that different occurences of same form have independent maxima!!!  
public class DenotatorValueExtractor {
	
	public static final String SATELLITE_LEVEL = "Satellite Level";
	public static final String SIBLING_NUMBER = "Sibling number";
	public static final String COLIMIT_INDEX = "Colimit index";
	
	private int maxLayer;
	private Map<String,Double> minValues, maxValues;
	private DisplayObjectList displayObjects;
	private JSynScore jSynScore;
	//TreeSet in future for faster searching!!!
	private boolean selectObjects;
	private Set<DenotatorPath> selectedPaths;
	private DenotatorPath selectedAnchor;
	private LayerStates layerStates;
	private DenotatorValueFinder finder;
	
	//TODO: not really nice, but used this way just to get JSynScore of independent object..
	public DenotatorValueExtractor(Denotator score) {
		//PerformanceCheck.startTask("extract");
		this.layerStates = new LayerStates(new ViewController());
		this.initAndExtract(new ViewController(), score, false);
	}
	
	public DenotatorValueExtractor(ViewController controller, ScoreChangedNotification notification, boolean selectObjects, LayerStates layerStates) {
		//PerformanceCheck.startTask("extract");
		this.selectedPaths = notification.getNotesToBeSelected();
		this.selectedAnchor = notification.getAnchorToBeSelected();
		this.layerStates = layerStates;
		this.initAndExtract(controller, notification.getScore(), selectObjects);
	}
	
	private void initAndExtract(ViewController controller, Denotator score, boolean selectObjects) {
		Form form = score.getForm();
		this.finder = new DenotatorValueFinder(form, true);
		this.minValues = new TreeMap<String,Double>();
		this.maxValues = new TreeMap<String,Double>();
		this.displayObjects = new DisplayObjectList(controller, form);
		this.jSynScore = new JSynScore();
		this.selectObjects = selectObjects;
		try {
			this.extractObjects(score, null, null, null, null, 0, 0, 0, new DenotatorPath(score.getForm()));
		} catch (RubatoException e) { e.printStackTrace(); }
		this.setTopDenotatorParameters();
		this.layerStates.removeLayers(this.maxLayer);
	}
	
	private void setTopDenotatorParameters() {
		this.displayObjects.setValueNamesAndPaths(this.finder.getValueNamesAndPaths());
		this.displayObjects.setObjects(this.finder.getObjectsInFoundOrder());
		this.displayObjects.setObjectsAndPaths(this.finder.getObjectsAndPaths());
		this.displayObjects.setTopDenotatorColimits(this.finder.getColimitsInFoundOrder());
		this.displayObjects.setTopDenotatorColimitsAndPaths(this.finder.getColimitsAndPaths());
		this.displayObjects.setAllowsForSatellites(this.finder.formAllowsForSatellites());
		
		List<String> valueNames = this.finder.getValueNamesInFoundOrder();
		if (this.finder.formAllowsForSatellites()) {
			valueNames.add(DenotatorValueExtractor.SATELLITE_LEVEL);
			valueNames.add(DenotatorValueExtractor.SIBLING_NUMBER);
		}
		if (this.finder.formContainsColimit()) {
			valueNames.add(DenotatorValueExtractor.COLIMIT_INDEX);
		}
		this.displayObjects.setValueNames(valueNames);
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
				currentDO = this.addDisplayObject(currentDenotator, parentDO, satelliteLevel, siblingNumber, colimitIndex, currentPath);
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
						this.extractObjects(onlyChild, parentDO, currentDO, parentJSO, currentJSO, satelliteLevel, siblingNumber, childIndex, currentPath.getChildPath(childIndex));
					}
				}
			} else if (denotatorType == Denotator.SIMPLE) {
				this.addSimpleValues(parentDO, currentDO, currentJSO, (SimpleDenotator)currentDenotator);
			}
		}
	}
	
	private DisplayObject addDisplayObject(Denotator denotator, DisplayObject parent, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath path) {
		DisplayObject displayObject = this.createDisplayObject(denotator, parent, satelliteLevel, siblingNumber, colimitIndex, path);
		displayObject.setVisibility(this.layerStates.get(displayObject.getLayer()));
		this.displayObjects.add(displayObject);
		if (this.selectObjects && this.selectedPaths != null) {
			if (this.selectedPaths.contains(path)) {
				this.displayObjects.selectNote(displayObject);
			}
		}
		if (this.selectedAnchor != null && this.selectedAnchor.equals(path)) {
			this.displayObjects.setSelectedAnchorNote(displayObject);
		}
		return displayObject;
	}
	
	private DisplayObject createDisplayObject(Denotator denotator, DisplayObject parent, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath path) {
		List<Integer> structuralValues = new ArrayList<Integer>();
		if (this.finder.formAllowsForSatellites()) {
			structuralValues.add(satelliteLevel);
			structuralValues.add(siblingNumber);
		}
		if (this.finder.formContainsColimit()) {
			structuralValues.add(colimitIndex);
		}
		return new DisplayObject(parent, denotator.getType(), structuralValues, path.clone());
	}
	
	private void addSimpleValues(DisplayObject parentDO, DisplayObject displayObject, JSynObject jSynObject, SimpleDenotator simpleDenotator) {
		Map<String,Double> objectValues = this.extractValues(simpleDenotator, parentDO);
		displayObject.addValues(objectValues);
		jSynObject.addValues(simpleDenotator.getForm(), objectValues); //needs to know forms!!
	}
	
	//TODO: maybe outsource, join with ObjectGenerator.createModule
	private Map<String,Double> extractValues(SimpleDenotator denotator, DisplayObject parent) {
		Map<String,Double> values = new TreeMap<String,Double>();
		String simpleName = denotator.getForm().getNameString();
		this.extractValues(parent, simpleName, denotator.getElement(), values, "");
		this.updateMinAndMax(values);
		return values;
	}
	
	private void extractValues(DisplayObject parent, String simpleName, ModuleElement currentElement, Map<String,Double> values, String indexString) {
		if (currentElement instanceof ProductElement) {
			ProductElement productElement = (ProductElement)currentElement;
			for (int i = 0; i < productElement.getFactorCount(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				this.extractValues(parent, simpleName, productElement.getFactor(i), values, indexString+(i+1));
			}
		} else if (currentElement.getModule().getDimension() > 1) {
			for (int i = 0; i < currentElement.getModule().getDimension(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				this.extractValues(parent, simpleName, currentElement.getComponent(i), values, indexString+(i+1));
			}
		} else {
			String valueName = DenotatorValueFinder.makeValueName(simpleName, currentElement.getModule(), indexString);
			double value = ((RElement)currentElement.cast(RRing.ring)).getValue();
			if (parent != null) {
				Double parentValue = parent.getValue(valueName);
				if (parentValue != null) {
					value += parentValue;
				}
			}
			values.put(valueName, value);
		}
	}
	
	private void updateMinAndMax(Map<String,Double> values) {
		for (String currentValueName : values.keySet()) {
			if (!this.minValues.keySet().contains(currentValueName)) {
				this.minValues.put(currentValueName, Double.MAX_VALUE);
				this.maxValues.put(currentValueName, -1*Double.MAX_VALUE);
			}
			this.minValues.put(currentValueName, Math.min(values.get(currentValueName), this.minValues.get(currentValueName)));
			this.maxValues.put(currentValueName, Math.max(values.get(currentValueName), this.maxValues.get(currentValueName)));
		}
	}
	
	public DisplayObjectList getDisplayObjects() {
		return this.displayObjects;
	}
	
	public JSynScore getJSynScore() {
		return this.jSynScore;
	}
	
	public List<Double> getMinValues() {
		List<Double> minValues = new ArrayList<Double>();
		for (String currentValueName : this.displayObjects.getValueNames()) {
			Double currentValue = this.minValues.get(currentValueName);
			if (currentValue != null) {
				minValues.add(currentValue);
			}
		}
		return minValues;
	}
	
	public List<Double> getMaxValues() {
		List<Double> maxValues = new ArrayList<Double>();
		for (String currentValueName : this.displayObjects.getValueNames()) {
			Double currentValue = this.maxValues.get(currentValueName);
			if (currentValue != null) {
				maxValues.add(currentValue);
			}
		}
		return maxValues;
	}

}

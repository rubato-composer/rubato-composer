package org.rubato.rubettes.bigbang.view.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
	
	private int maxLayer;
	private List<String> valueNames;
	private List<Double> minValues, maxValues;
	private DisplayObjectList displayObjects;
	private JSynScore jSynScore;
	//TreeSet in future for faster searching!!!
	private boolean selectObjects;
	private Set<DenotatorPath> selectedPaths;
	private DenotatorPath selectedAnchor;
	private LayerStates layerStates;
	private DenotatorValueFinder finder;
	
	//TODO: not really nice, but used this way just to get JSynScore..
	public DenotatorValueExtractor(Denotator score) {
		//PerformanceCheck.startTask("extract");
		this.initFinder(score.getForm());
		this.valueNames = new ArrayList<String>();
		this.minValues = new ArrayList<Double>();
		this.maxValues = new ArrayList<Double>();
		this.displayObjects = new DisplayObjectList(new ViewController(), score.getForm());
		this.jSynScore = new JSynScore();
		this.selectObjects = false;
		this.layerStates = new LayerStates(new ViewController());
		try {
			this.extractObjects(score, null, null, null, DenotatorPath.ANCHOR, 0, 0, 0, new DenotatorPath(score.getForm()));
		} catch (RubatoException e) { e.printStackTrace(); }
		this.setTopDenotatorParameters();
		this.layerStates.removeLayers(this.maxLayer);
	}
	
	public DenotatorValueExtractor(ViewController controller, ScoreChangedNotification notification, boolean selectObjects, LayerStates layerStates) {
		//PerformanceCheck.startTask("extract");
		this.initFinder(notification.getScore().getForm());
		this.valueNames = new ArrayList<String>();
		this.minValues = new ArrayList<Double>();
		this.maxValues = new ArrayList<Double>();
		this.displayObjects = new DisplayObjectList(controller, notification.getScore().getForm());
		this.jSynScore = new JSynScore();
		this.selectObjects = selectObjects;
		this.selectedPaths = notification.getNotesToBeSelected();
		this.selectedAnchor = notification.getAnchorToBeSelected();
		this.layerStates = layerStates;
		try {
			this.extractObjects(notification.getScore(), null, null, null, DenotatorPath.ANCHOR, 0, 0, 0, new DenotatorPath(notification.getScore().getForm()));
		} catch (RubatoException e) { e.printStackTrace(); }
		this.setTopDenotatorParameters();
		this.layerStates.removeLayers(this.maxLayer);
	}
	
	private void initFinder(Form form) {
		//TODO: FINDER CURRENTLY ONLY FINDS THE NAMES IN THE TOP DENOTATOR!!!!!! adjust methods!!
		if (form.getType() == Form.POWER || form.getType() == Form.LIST) {
			form = form.getForm(0);
		}
		this.finder = new DenotatorValueFinder(form, false);
	}
	
	private void setTopDenotatorParameters() {
		this.displayObjects.setTopDenotatorValues(this.finder.getValueNamesAndPaths());
		this.displayObjects.setTopDenotatorColimits(this.finder.getColimitsFoundInOrder());
		this.displayObjects.setTopDenotatorColimitsAndPaths(this.finder.getColimitFormsAndPaths());
		this.displayObjects.setContainsPowerset(this.finder.formContainsPowerset());
		
		this.valueNames = this.finder.getValueNamesInFoundOrder();
		if (this.finder.formContainsPowerset()) {
			this.valueNames.add("Satellite Level");
			this.valueNames.add("Sibling number");
		}
		if (this.finder.formContainsColimit()) {
			this.valueNames.add("Colimit index");
		}
		this.displayObjects.setValueNames(this.valueNames);
	}
	
	//recursive method!!
	//TODO: remove relation!! not very interesting anymore..
	private void extractObjects(Denotator currentDenotator, DisplayObject parent, DisplayObject currentDisplayObject, JSynObject currentJSynObject, int relation, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath currentPath) throws RubatoException {
		int denotatorType = currentDenotator.getType();
		if (denotatorType == Denotator.POWER || denotatorType == Denotator.LIST) {
			FactorDenotator currentPower = (FactorDenotator)currentDenotator;
			//TODO: find out if modulators and add them to JSynObject!!
			for (int i = 0; i < currentPower.getFactorCount(); i++) {
				//call with currentDisplayObject and currentJSynObject null, since all children become independent objects
				this.extractObjects(currentPower.getFactor(i), parent, null, null, DenotatorPath.SATELLITE, satelliteLevel+1, i, colimitIndex, currentPath.getChildPath(i));
			}
		} else {
			if (currentDisplayObject == null) {
				currentDisplayObject = this.addDisplayObject(currentDenotator, parent, relation, satelliteLevel, siblingNumber, colimitIndex, currentPath);
				currentJSynObject = this.jSynScore.addNewObject();
			}
			if (denotatorType == Denotator.SIMPLE) {
				this.addSimpleValues(currentDisplayObject, currentJSynObject, (SimpleDenotator)currentDenotator);
			} else if (denotatorType == Denotator.LIMIT) {
				LimitDenotator currentLimit = (LimitDenotator)currentDenotator;
				for (int i = 0; i < currentLimit.getFactorCount(); i++) {
					Denotator currentChild = currentLimit.getFactor(i);
					this.extractObjects(currentChild, parent, currentDisplayObject, currentJSynObject, relation, satelliteLevel, siblingNumber, colimitIndex, currentPath.getChildPath(i));
				}
			} else if (denotatorType == Denotator.COLIMIT) {
				ColimitDenotator currentColimit = (ColimitDenotator)currentDenotator;
				Denotator onlyChild = currentColimit.getFactor();
				int childIndex = currentColimit.getIndex();
				currentDisplayObject.setColimitIndex(colimitIndex+childIndex);
				colimitIndex += currentColimit.getFactorCount();
				for (int i = 0; i < currentColimit.getForm().getFormCount(); i++) {
					if (i == childIndex) {
						this.extractObjects(onlyChild, parent, currentDisplayObject, currentJSynObject, relation, satelliteLevel, siblingNumber, childIndex, currentPath.getChildPath(childIndex));
					} else {
						this.addNullValues(currentDisplayObject, currentPath.getChildPath(i));
					}
				}
			}
		}
	}
	
	private DisplayObject addDisplayObject(Denotator denotator, DisplayObject parent, int relation, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath path) {
		DisplayObject displayObject = this.createDisplayObject(denotator, parent, relation, satelliteLevel, siblingNumber, colimitIndex, path);
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
	
	private DisplayObject createDisplayObject(Denotator denotator, DisplayObject parent, int relation, int satelliteLevel, int siblingNumber, int colimitIndex, DenotatorPath path) {
		List<Integer> structuralValues = new ArrayList<Integer>();
		if (this.finder.formContainsPowerset()) {
			structuralValues.add(satelliteLevel);
			structuralValues.add(siblingNumber);
		}
		if (this.finder.formContainsColimit()) {
			structuralValues.add(colimitIndex);
		}
		return new DisplayObject(parent, relation, denotator.getType(), structuralValues, path.clone());
	}
	
	private void addSimpleValues(DisplayObject displayObject, JSynObject jSynObject, SimpleDenotator simpleDenotator) {
		List<Double> objectValues = this.extractValues(simpleDenotator);
		displayObject.addValues(objectValues);
		jSynObject.addValues(simpleDenotator.getForm(), objectValues); //needs to know forms!!
		//TODO: a map should keep track which DOs have certain simples so that they can be found quick
	}
	
	private void addNullValues(DisplayObject largerObject, DenotatorPath path) {
		DenotatorValueFinder finder = new DenotatorValueFinder(path.getForm(), false);
		for (int i = 0; i < finder.getValuePathsInFoundOrder().size(); i++) {
			largerObject.addValues(Arrays.asList(new Double[]{null}));
		}
	}
	
	
	
	//TODO: maybe outsource, join with ObjectGenerator.createModule
	private List<Double> extractValues(SimpleDenotator denotator) {
		List<Double> values = new ArrayList<Double>();
		List<String> moduleNames = new ArrayList<String>();
		this.extractValues(denotator.getElement(), values, moduleNames, "");
		this.updateMinAndMax(denotator.getForm().getNameString(), values, moduleNames);
		return values;
	}
	
	private void extractValues(ModuleElement currentElement, List<Double> values, List<String> moduleNames, String indexString) {
		if (currentElement instanceof ProductElement) {
			ProductElement productElement = (ProductElement)currentElement;
			for (int i = 0; i < productElement.getFactorCount(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				this.extractValues(productElement.getFactor(i), values, moduleNames, indexString+(i+1));
			}
		} else if (currentElement.getModule().getDimension() > 1) {
			for (int i = 0; i < currentElement.getModule().getDimension(); i++) {
				if (!indexString.isEmpty()) indexString += ".";
				this.extractValues(currentElement.getComponent(i), values, moduleNames, indexString+(i+1));
			}
		} else {
			values.add(((RElement)currentElement.cast(RRing.ring)).getValue());
			moduleNames.add(DenotatorValueFinder.makeModuleName(currentElement.getModule(), indexString));
			//TODO: add functionality for relative def!!! could be selected somewhere in the GUI 
		}
	}
	
	//TODO: these valueNames are currently overwritten!!! BUT: they are needed for identification of min/max!!!
	private void updateMinAndMax(String simpleName, List<Double> values, List<String> moduleNames) {
		for (int i = 0; i < values.size(); i++) {
			String currentName = simpleName + " " + moduleNames.get(i);
			int nameIndex = this.valueNames.indexOf(currentName);
			if (nameIndex < 0) {
				this.valueNames.add(currentName);
				nameIndex = this.valueNames.size()-1;
				this.minValues.add(Double.MAX_VALUE);
				this.maxValues.add(Double.MIN_VALUE);
			}
			this.minValues.set(nameIndex, Math.min(values.get(i), this.minValues.get(nameIndex)));
			this.maxValues.set(nameIndex, Math.max(values.get(i), this.maxValues.get(nameIndex)));
		}
	}
	
	public DisplayObjectList getDisplayObjects() {
		return this.displayObjects;
	}
	
	public JSynScore getJSynScore() {
		return this.jSynScore;
	}
	
	public List<Double> getMinValues() {
		return this.minValues;
	}
	
	public List<Double> getMaxValues() {
		return this.maxValues;
	}

}

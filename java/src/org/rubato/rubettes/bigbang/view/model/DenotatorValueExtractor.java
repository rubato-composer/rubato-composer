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
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.model.player.JSynModulator;
import org.rubato.rubettes.bigbang.model.player.JSynNote;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjectList;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.DenotatorValueFinder;

//TODO: deal with case that different occurences of same form have independent maxima!!!  
public class DenotatorValueExtractor {
	
	private int maxLayer;
	private List<String> valueNames;
	private List<Double> minValues, maxValues;
	private DisplayObjectList displayObjects;
	//TreeSet in future for faster searching!!!
	private boolean selectObjects;
	private Set<DenotatorPath> selectedPaths;
	private DenotatorPath selectedAnchor;
	private LayerStates layerStates;
	
	//TODO: REMOVE!!!
	private final int[][] ELEMENT_PATHS = new int[][] {
			{0,0},{1,0},{2,0},{3,0},{4,0},{5,0}};
	public static final int[][] DENOTATOR_PATHS = new int[][] {
			{0},{1},{2},{3},{4},{5}};
	
	
	public DenotatorValueExtractor() {}
	
	public DisplayObjectList extractDisplayObjects(ViewController controller, ScoreChangedNotification notification, boolean selectObjects, LayerStates layerStates) {
		//PerformanceCheck.startTask("extract");
		this.valueNames = new ArrayList<String>();
		this.minValues = new ArrayList<Double>();
		this.maxValues = new ArrayList<Double>();
		this.displayObjects = new DisplayObjectList(controller, notification.getScore().getForm());
		this.selectObjects = selectObjects;
		this.selectedPaths = notification.getNotesToBeSelected();
		this.selectedAnchor = notification.getAnchorToBeSelected();
		this.layerStates = layerStates;
		try {
			this.extractDisplayObjects(notification.getScore(), null, null, DenotatorPath.ANCHOR, 0, 0, new DenotatorPath(notification.getScore().getForm()));
		} catch (RubatoException e) { e.printStackTrace(); }
		this.layerStates.removeLayers(this.maxLayer);
		this.setTopDenotatorParameters(notification.getScore().getForm());
		return this.displayObjects;
	}
	
	private void setTopDenotatorParameters(Form form) {
		if (form.getType() == Form.POWER || form.getType() == Form.LIST) {
			form = form.getForm(0);
		}
		DenotatorValueFinder finder = new DenotatorValueFinder(form, false);
		this.valueNames = finder.getValueNamesInFoundOrder();
		this.displayObjects.setTopDenotatorValues(finder.getValueNamesAndPaths());
		this.displayObjects.setTopDenotatorColimits(finder.getColimitsFoundInOrder());
		this.displayObjects.setTopDenotatorColimitsAndPaths(finder.getColimitFormsAndPaths());
		//TODO: CURRENTLY ONLY FINDS THE NAMES IN THE TOP DENOTATOR!!!!!! adjust methods!!
		if (finder.formContainsPowerset()) {
			this.valueNames.add("Satellite Level");
			this.valueNames.add("Sibling number");
		}
		this.displayObjects.setValueNames(this.valueNames);
		this.displayObjects.setContainsPowerset(finder.formContainsPowerset());
	}
	
	//recursive method!!
	//TODO: remove relation!! not very interesting anymore..
	private DisplayObject extractDisplayObjects(Denotator currentDenotator, DisplayObject parent, DisplayObject largerObject, int relation, int satelliteLevel, int siblingNumber, DenotatorPath currentPath) throws RubatoException {
		int denotatorType = currentDenotator.getType();
		if (denotatorType == Denotator.SIMPLE) {
			SimpleDenotator currentSimple = (SimpleDenotator)currentDenotator;
			if (largerObject == null) {
				largerObject = this.addDisplayObject(currentSimple, parent, relation, satelliteLevel, siblingNumber, currentPath);
			}
			this.addSimpleValues(largerObject, currentSimple, currentPath);
		} else if (denotatorType == Denotator.LIMIT) {
			if (largerObject == null) {
				largerObject = this.addDisplayObject(currentDenotator, parent, relation, satelliteLevel, siblingNumber, currentPath);
			}
			LimitDenotator currentLimit = (LimitDenotator)currentDenotator;
			for (int i = 0; i < currentLimit.getFactorCount(); i++) {
				Denotator currentChild = currentLimit.getFactor(i);
				this.extractDisplayObjects(currentChild, parent, largerObject, relation, satelliteLevel, siblingNumber, currentPath.getChildPath(i));
			}
		} else if (denotatorType == Denotator.COLIMIT) {
			if (largerObject == null) {
				largerObject = this.addDisplayObject(currentDenotator, parent, relation, satelliteLevel, siblingNumber, currentPath);
			}
			ColimitDenotator currentColimit = (ColimitDenotator)currentDenotator;
			Denotator onlyChild = currentColimit.getFactor();
			//have to get it like this since Colimit.index is not implemented well TODO: AND NOW??
			int childIndex = currentColimit.getForm().getForms().indexOf(onlyChild.getForm());
			for (int i = 0; i < currentColimit.getForm().getFormCount(); i++) {
				if (i == childIndex) {
					this.extractDisplayObjects(onlyChild, parent, largerObject, relation, satelliteLevel, siblingNumber, currentPath.getChildPath(childIndex));
				} else {
					this.addNullValues(largerObject, currentPath.getChildPath(i));
				}
			}
		} else if (denotatorType == Denotator.POWER || denotatorType == Denotator.LIST) {
			FactorDenotator currentPower = (FactorDenotator)currentDenotator;
			for (int i = 0; i < currentPower.getFactorCount(); i++) {
				//call with largerObject null, since all children become independent objects
				DisplayObject currentChild = this.extractDisplayObjects(currentPower.getFactor(i), parent, null, DenotatorPath.SATELLITE, satelliteLevel+1, i, currentPath.getChildPath(i));
				if (largerObject != null) {
					largerObject.addChild(currentChild);
				}
			}
		}
		//largerObject may have changed, return for tracking child relationships in case of a Powerset
		return largerObject;
	}
	
	private DisplayObject addDisplayObject(Denotator denotator, DisplayObject parent, int relation, int satelliteLevel, int siblingNumber, DenotatorPath path) {
		DisplayObject displayObject = new DisplayObject(parent, relation, satelliteLevel, siblingNumber, denotator.getType(), path.clone());
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
	
	private void addSimpleValues(DisplayObject largerObject, SimpleDenotator simpleDenotator, DenotatorPath path) {
		List<Double> objectValues = this.extractValues(simpleDenotator);
		largerObject.addValues(path, objectValues);
		//TODO: a map should keep track which DOs have certain simples so that they can be found quick
	}
	
	private void addNullValues(DisplayObject largerObject, DenotatorPath path) {
		DenotatorValueFinder finder = new DenotatorValueFinder(path.getForm(), false);
		for (DenotatorPath currentPath : finder.getValuePathsInFoundOrder()) {
			largerObject.addValues(currentPath, Arrays.asList(new Double[]{null}));
		}
	}
	
	//TODO: REWRITE ALL JSYN-RELATED ONES!!!
	public JSynNote extractValues(Denotator node, int bpm) {
		try {
			Denotator note = node.get(new int[]{0});
			List<Double> noteValues = this.extractValues(note, new ArrayList<Double>());
			JSynNote jSynNote = new JSynNote(noteValues, bpm);
			this.extractModulators(jSynNote, note, noteValues);
			return jSynNote;
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void extractModulators(JSynNote jSynNote, Denotator note, List<Double> parentValues) throws RubatoException {
		PowerDenotator modulators = (PowerDenotator)note.get(new int[]{6});
		for (Denotator currentModulator: modulators.getFactors()) {
			List<Double> modulatorValues = this.extractValues(currentModulator, parentValues);
			JSynModulator jSynMod = jSynNote.addModulator(modulatorValues);
			this.extractModulators(jSynMod, currentModulator, modulatorValues);
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
	
	//TODO: these valueNames are currently overwritten!!! not needed anymore!!
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
	
	//TODO: get rid of this at some point
	private List<Double> extractValues(Denotator note, List<Double> parentValues) throws RubatoException {
		List<Double> values = new ArrayList<Double>();
		for (int i = 0; i < 5; i++) {
			ModuleElement e = note.getElement(this.ELEMENT_PATHS[i]).cast(RRing.ring);
			values.add(((RElement)e).getValue() + parentValues.get(i));
		}
		this.updateMinAndMax("", values, new ArrayList<String>());
		return values;
	}
	
	public List<Double> getMinValues() {
		return this.minValues;
	}
	
	public List<Double> getMaxValues() {
		return this.maxValues;
	}

}

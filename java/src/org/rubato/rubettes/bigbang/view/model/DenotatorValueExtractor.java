package org.rubato.rubettes.bigbang.view.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rubato.base.RubatoException;
import org.rubato.math.module.ModuleElement;
import org.rubato.math.module.ProductElement;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.yoneda.ColimitDenotator;
import org.rubato.math.yoneda.Denotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.yoneda.PowerDenotator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.rubettes.bigbang.controller.ScoreChangedNotification;
import org.rubato.rubettes.bigbang.model.player.JSynModulator;
import org.rubato.rubettes.bigbang.model.player.JSynNote;
import org.rubato.rubettes.bigbang.view.controller.ViewController;
import org.rubato.rubettes.bigbang.view.subview.DisplayObjectList;
import org.rubato.rubettes.util.DenotatorPath;

//TODO: deal with case that different occurences of same form have independent maxima!!!  
public class DenotatorValueExtractor {
	
	private int maxLayer;
	private List<String> simpleNames;
	private List<Double> minValues, maxValues;
	private DisplayObjectList displayObjects;
	//TreeSet in future for faster searching!!!
	private boolean selectObjects;
	private Set<DenotatorPath> selectedPaths;
	private DenotatorPath selectedAnchor;
	private LayerStates layerStates;
	
	public static final String[] VALUE_NAMES = new String[] {
			"Onset", "Pitch", "Loudness", "Duration", "Voice", "Modulator level",
			"Satellite level", "Sibling number"};
	public final int[][] ELEMENT_PATHS = new int[][] {
			{0,0},{1,0},{2,0},{3,0},{4,0},{5,0}};
	public static final int[][] DENOTATOR_PATHS = new int[][] {
			{0},{1},{2},{3},{4},{5}};
	
	
	public DenotatorValueExtractor() {}
	
	public DisplayObjectList extractDisplayObjects(ViewController controller, ScoreChangedNotification notification, boolean selectObjects, LayerStates layerStates) {
		//PerformanceCheck.startTask("extract");
		this.simpleNames = new ArrayList<String>();
		this.minValues = new ArrayList<Double>();
		this.maxValues = new ArrayList<Double>();
		this.displayObjects = new DisplayObjectList(controller);
		this.selectObjects = selectObjects;
		this.selectedPaths = notification.getNotesToBeSelected();
		this.selectedAnchor = notification.getAnchorToBeSelected();
		this.layerStates = layerStates;
		try {
			this.extractDisplayObjects(notification.getScore(), null, null, DenotatorPath.ANCHOR, 0, 0, new DenotatorPath());
		} catch (RubatoException e) { e.printStackTrace(); }
		this.layerStates.removeLayers(this.maxLayer);
		return this.displayObjects;
	}
	
	//recursive method!!
	//TODO: remove relation!! not very interesting anymore..
	private DisplayObject extractDisplayObjects(Denotator currentDenotator, DisplayObject parent, DisplayObject largerObject, int relation, int satelliteLevel, int siblingNumber, DenotatorPath currentPath) throws RubatoException {
		if (currentDenotator.getType() == Denotator.SIMPLE) {
			SimpleDenotator currentSimple = (SimpleDenotator)currentDenotator;
			if (largerObject == null) {
				largerObject = this.addDisplayObject(currentSimple, parent, satelliteLevel, siblingNumber, relation, currentPath);
			}
			this.addSimpleValues(largerObject, currentSimple, currentPath);
		} else if (currentDenotator.getType() == Denotator.LIMIT) {
			if (largerObject == null) {
				largerObject = this.addDisplayObject(currentDenotator, parent, satelliteLevel, siblingNumber, relation, currentPath);
			}
			LimitDenotator currentLimit = (LimitDenotator)currentDenotator;
			for (int i = 0; i < currentLimit.getFactorCount(); i++) {
				Denotator currentChild = currentLimit.getFactor(i);
				this.extractDisplayObjects(currentChild, parent, largerObject, relation, satelliteLevel, siblingNumber, currentPath.getChildPath(i));
			}
		} else if (currentDenotator.getType() == Denotator.COLIMIT) {
			if (largerObject == null) {
				largerObject = this.addDisplayObject(currentDenotator, parent, satelliteLevel, siblingNumber, relation, currentPath);
			}
			Denotator onlyChild = ((ColimitDenotator)currentDenotator).getFactor();
			this.extractDisplayObjects(onlyChild, parent, largerObject, relation, satelliteLevel, siblingNumber, currentPath.getChildPath(0));
		} else if (currentDenotator.getType() == Denotator.POWER) {
			PowerDenotator currentPower = (PowerDenotator)currentDenotator;
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
	
	//recursive method!!
	/*private List<DisplayObject> extractDisplayObjects(Denotator score, DisplayObject parent, int relation, int recLevel, int satLevel, NotePath currentPath) throws RubatoException {
		List<DisplayObject> siblings = new ArrayList<DisplayObject>();
		PowerDenotator currentPowerset = (PowerDenotator)soundScore.get(currentPath.toIntArray());
		for (int i = 0; i < currentPowerset.getFactorCount(); i++) {
			//create display note
			NotePath currentSubPath = currentPath.getPowersetChildPath(i);
			Denotator currentNote = soundScore.get(currentSubPath.toIntArray());
			DisplayObject currentDisplayNote = this.addNote(currentNote, parent, relation, modLevel, satLevel, i, currentSubPath);
			//recursive call for the note's satellites
			NotePath currentSatellitesPath = currentSubPath.getSatellitesPath();
			if (currentSatellitesPath != null) {
				currentDisplayNote.setChildren(this.extractDisplayObjects(soundScore, currentDisplayNote, NotePath.SATELLITE, modLevel, satLevel+1, currentSatellitesPath));
			}
			//recursive call for the note's modulators
			NotePath currentModulatorsPath = currentSubPath.getModulatorsPath();
			currentDisplayNote.setChildren(this.extractDisplayObjects(soundScore, currentDisplayNote, NotePath.MODULATOR, modLevel+1, satLevel, currentModulatorsPath));
			siblings.add(currentDisplayNote);
		}
		return siblings;
	}*/
	
	private DisplayObject addDisplayObject(Denotator denotator, DisplayObject parent, int relation, int satelliteLevel, int siblingNumber, DenotatorPath path) {
		DisplayObject displayObject = new DisplayObject(parent, relation, satelliteLevel, siblingNumber, denotator.getType(), new DenotatorPath(path));
		displayObject.setVisibility(this.layerStates.get(displayObject.getLayer()));
		this.displayObjects.add(displayObject);
		if (this.selectObjects) {
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
		double[] objectValues = this.extractSimpleValues(simpleDenotator);
		largerObject.addValues(simpleDenotator.getForm().getNameString(), path, objectValues);
		//TODO: a map should keep track which DOs have certain simples so that they can be found quick
	}
	
	/*private DisplayObject addNote(Denotator note, DisplayObject parent, int relation, int modLevel, int satLevel, int siblingNumber, NotePath nodePath) throws RubatoException {
		double[] noteValues = this.extractValues(note, parent, modLevel, satLevel, siblingNumber);
		ModuleElement e = note.getElement(this.ELEMENT_PATHS[5]);
		int layer = ((ZElement)e).getValue();
		this.maxLayer = Math.max(layer, this.maxLayer);
		DisplayObject displayNote = new DisplayObject(noteValues, parent, relation, new NotePath(nodePath), layer);
		displayNote.setVisibility(this.layerStates.get(layer));
		this.displayObjects.add(displayNote);
		if (this.selectObjects) {
			if (this.selectedPaths.contains(nodePath)) {
				this.displayObjects.selectNote(displayNote);
			}
		}
		if (this.selectedAnchor != null && this.selectedAnchor.equals(nodePath)) {
			this.displayObjects.setSelectedAnchorNote(displayNote);
		}
		return displayNote;
	}*/
	
	//TODO: REWRITE ALL JSYN-RELATED ONES!!!
	public JSynNote extractValues(Denotator node, int bpm) {
		try {
			Denotator note = node.get(new int[]{0});
			double[] noteValues = this.extractValues(note, new double[8]);
			JSynNote jSynNote = new JSynNote(noteValues, bpm);
			this.extractModulators(jSynNote, note, noteValues);
			return jSynNote;
		} catch (RubatoException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void extractModulators(JSynNote jSynNote, Denotator note, double[] parentValues) throws RubatoException {
		PowerDenotator modulators = (PowerDenotator)note.get(new int[]{6});
		for (Denotator currentModulator: modulators.getFactors()) {
			double[] modulatorValues = this.extractValues(currentModulator, parentValues);
			JSynModulator jSynMod = jSynNote.addModulator(modulatorValues);
			this.extractModulators(jSynMod, currentModulator, modulatorValues);
		}
	}
	
	private double[] extractSimpleValues(SimpleDenotator denotator) {
		ModuleElement element = denotator.getElement();
		double[] values;
		//TODO: make both product modules and n-dim modules work
		if (element instanceof ProductElement) {
			values = this.extractSimpleValues((ProductElement) element);
		} else {
			values = new double[] {this.extractSimpleValue(element)};
		}
		this.updateMinAndMax(denotator.getForm().getNameString(), values);
		return values;
	}
	
	private double[] extractSimpleValues(ProductElement element) {
		int dimension = element.getFactorCount();
		double[] values = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			values[i] = this.extractSimpleValue(element.getFactor(i));
		}
		return values;
	}
	
	private double extractSimpleValue(ModuleElement element) {
		return ((RElement)element.cast(RRing.ring)).getValue();
		//TODO: add functionality for relative def!!! could be selected somewhere in the GUI 
	}
	
	private void updateMinAndMax(String simpleName, double[] values) {
		for (int i = 0; i < values.length; i++) {
			String currentName = simpleName + i;
			int nameIndex = this.simpleNames.indexOf(currentName);
			if (nameIndex < 0) {
				this.simpleNames.add(currentName);
				nameIndex = this.simpleNames.size()-1;
				this.minValues.add(Double.MAX_VALUE);
				this.maxValues.add(Double.MIN_VALUE);
			}
			this.minValues.set(nameIndex, Math.min(values[i], this.minValues.get(nameIndex)));
			this.maxValues.set(nameIndex, Math.max(values[i], this.maxValues.get(nameIndex)));
		}
	}
	
	//TODO: get rid of this at some point
	private double[] extractValues(Denotator note, double[] parentValues) throws RubatoException {
		double[] values = new double[8];
		for (int i = 0; i < values.length-3; i++) {
			ModuleElement e = note.getElement(this.ELEMENT_PATHS[i]).cast(RRing.ring);
			values[i] = ((RElement)e).getValue() + parentValues[i];
		}
		this.updateMinAndMax("", values);
		return values;
	}
	
	public List<String> getSimpleNames() {
		return this.simpleNames;
	}
	
	public List<Double> getMinValues() {
		return this.minValues;
	}
	
	public List<Double> getMaxValues() {
		return this.maxValues;
	}

}

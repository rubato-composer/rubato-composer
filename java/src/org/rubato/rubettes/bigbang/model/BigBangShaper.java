package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.base.RubatoException;
import org.rubato.math.module.RElement;
import org.rubato.math.module.RRing;
import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangShaper extends BigBangScoreManipulator {
	
	private List<DenotatorPath> objectPaths;
	private TreeMap<Double,Double> shapingLocations;
	private boolean copyAndShape;
	private double shapingRange;
	
	public BigBangShaper(BigBangScore score, TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		super(score, properties.getElementPaths());
		this.objectPaths = new ArrayList<DenotatorPath>(properties.getNodePaths());
		this.shapingLocations = shapingLocations;
		this.shapingRange = 0.5;
		this.copyAndShape = properties.copyAndTransform();
	}
	
	public Map<DenotatorPath,Double> shapeObjects() {
		//PerformanceCheck.startTask(".pre");
		Map<List<Denotator>,Double> newObjectTracesAndOldYValues = new HashMap<List<Denotator>,Double>();
		
		this.objectPaths = this.score.reverseSort(this.objectPaths);
		
		Iterator<DenotatorPath> objectPathsIterator = this.objectPaths.iterator();
		if (objectPathsIterator.hasNext()) {
			DenotatorPath firstOfNextSiblings = objectPathsIterator.next();
			while (firstOfNextSiblings != null) {
				firstOfNextSiblings = this.shapeAndAddNextSiblings(newObjectTracesAndOldYValues, firstOfNextSiblings, objectPathsIterator);
			}
		}
		//PerformanceCheck.startTask(".find");
		Map<DenotatorPath,Double> newPathsAndOldYValues = new TreeMap<DenotatorPath,Double>();
		this.findPaths(newObjectTracesAndOldYValues, newPathsAndOldYValues);
		return newPathsAndOldYValues; 
	}
	
	private DenotatorPath shapeAndAddNextSiblings(Map<List<Denotator>,Double> newObjectTracesAndOldYValues, DenotatorPath firstSiblingPath, Iterator<DenotatorPath> nodePathsIterator) {
		//PerformanceCheck.startTask(".first_sib");
		List<Denotator> siblings = new ArrayList<Denotator>();
		List<DenotatorPath> siblingsPaths = new ArrayList<DenotatorPath>();
		
		siblingsPaths.add(firstSiblingPath);
		siblings.add(this.score.getAbsoluteObject(firstSiblingPath));
		DenotatorPath siblingsAnchorPath = firstSiblingPath.getAnchorPowersetPath();
		
		DenotatorPath currentSiblingPath = firstSiblingPath;
		while (nodePathsIterator.hasNext()) {
			currentSiblingPath = nodePathsIterator.next();
			//PerformanceCheck.startTask(".next_sibs");
			if (currentSiblingPath.isChildOf(siblingsAnchorPath)) {
				siblingsPaths.add(currentSiblingPath);
				siblings.add(this.score.getAbsoluteObject(currentSiblingPath));
			} else {
				this.removeShapeAndAdd(newObjectTracesAndOldYValues, siblings, siblingsAnchorPath, siblingsPaths);
				return currentSiblingPath;
			}
		}
		this.removeShapeAndAdd(newObjectTracesAndOldYValues, siblings, siblingsAnchorPath, siblingsPaths);
		return null;
	}
	
	private void removeShapeAndAdd(Map<List<Denotator>,Double> newObjectTracesAndOldYValues, List<Denotator> objects, DenotatorPath anchorPath, List<DenotatorPath> siblingsPaths) {
		//PerformanceCheck.startTask(".remove");
		if (!this.copyAndShape) {
			this.score.removeObjects(siblingsPaths);
		}
		this.shapeAndAddObjects(objects, anchorPath, newObjectTracesAndOldYValues);
	}
	
	private void shapeAndAddObjects(List<Denotator> objects, DenotatorPath anchorPath, Map<List<Denotator>,Double> newObjectTracesAndOldYValues) {
		Map<Denotator,Double> newObjectsAndOldYValues = new HashMap<Denotator,Double>();
		//boolean modulators = nodesAndNotes.get(0).getForm().equals(this.score.objectGenerator.SOUND_NOTE_FORM);
		for (int i = 0; i < objects.size(); i++) {
			//PerformanceCheck.startTask(".map");
			this.shapeObject(objects.get(i), newObjectsAndOldYValues);
		}
		//PerformanceCheck.startTask(".add");
		//TODO: ADD THEM AS THE SAME TYPE AS THEIR ORIGINAL!! MODULATOR OR SATELLITE
		List<Denotator> newObjects = new ArrayList<Denotator>(newObjectsAndOldYValues.keySet());
		List<DenotatorPath> newPaths = this.score.addObjectsToParent(newObjects, anchorPath, 0);
		List<List<Denotator>> newNoteTraces = this.score.extractObjects(newPaths);
		//PerformanceCheck.startTask(".extract");
		for (int i = 0; i < newNoteTraces.size(); i++) {
			newObjectTracesAndOldYValues.put(newNoteTraces.get(i), newObjectsAndOldYValues.get(newObjects.get(i)));
		}
	}
	
	private Denotator shapeObject(Denotator object, Map<Denotator,Double> newObjectsAndOldYValues) {
		Double newValue = this.getValueOfClosestLocation(object);
		int[] elementPath = new int[]{this.coordinatePaths[1][0], 0};
		double oldValue = this.score.objectGenerator.getDoubleValue(object, elementPath);
		if (newValue != null) {
			this.score.objectGenerator.replaceValue(object, elementPath, newValue);
		}
		object = object.copy();
		newObjectsAndOldYValues.put(object, oldValue);
		return object;
	}
	
	private void findPaths(Map<List<Denotator>,Double> newObjectsAndOldYValues, Map<DenotatorPath,Double> newPathsAndOldYValues) {
		List<List<Denotator>> objects = new ArrayList<List<Denotator>>(newObjectsAndOldYValues.keySet());
		List<DenotatorPath> paths = this.score.findPaths(objects);
		for (int i = 0; i < objects.size(); i++) {
			newPathsAndOldYValues.put(paths.get(i), newObjectsAndOldYValues.get(objects.get(i)));
		}
	}

	private Double getValueOfClosestLocation(Denotator note) {
		try {
			int[] elementPath = new int[]{this.coordinatePaths[0][0], 0};
			double xPosition = ((RElement)note.getElement(elementPath).cast(RRing.ring)).getValue();
			Map<Double,Double> subMap = this.shapingLocations.subMap(xPosition-this.shapingRange, xPosition+this.shapingRange);
			Double closestPosition = null;
			double minDistance = Double.MAX_VALUE;
			for (double currentPosition: subMap.keySet()) {
				double currentDistance = Math.abs(currentPosition - xPosition);
				if (currentDistance < minDistance) {
					minDistance = currentDistance;
					closestPosition = currentPosition;
				}
			}
			if (closestPosition != null) {
				return subMap.get(closestPosition);
			}
		} catch (RubatoException e) { 
			e.printStackTrace();
		}
		return null;
	}

}

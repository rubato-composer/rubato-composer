package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangShaper extends BigBangScoreManipulator {
	
	private SelectedObjectsPaths objectPaths;
	private TreeMap<Double,Double> shapingLocations;
	private boolean copyAndShape;
	private double shapingRange;
	
	public BigBangShaper(BigBangComposition score, TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		super(score, properties.getTransformationPaths());
		this.objectPaths = properties.getObjectsPaths();
		this.shapingLocations = shapingLocations;
		this.shapingRange = 0.5;
		this.copyAndShape = properties.copyAndTransform();
	}
	
	public List<Map<DenotatorPath,Double>> shapeCategorizedObjects() {
		List<Map<DenotatorPath,Double>> pathDifferences = new ArrayList<Map<DenotatorPath,Double>>();
		for (int i = 0; i < this.objectPaths.size(); i++) {
			pathDifferences.add(this.shapeObjects(new ArrayList<DenotatorPath>(this.objectPaths.get(i)), this.transformationPaths.get(i)));
		}
		return pathDifferences;
	}
	
	public Map<DenotatorPath,Double> shapeObjects(List<DenotatorPath> objectPaths, TransformationPaths shapingPaths) {
		//PerformanceCheck.startTask(".pre");
		Map<List<Denotator>,Double> newObjectTracesAndOldYValues = new HashMap<List<Denotator>,Double>();
		
		objectPaths = this.score.reverseSort(objectPaths);
		
		Iterator<DenotatorPath> objectPathsIterator = objectPaths.iterator();
		if (objectPathsIterator.hasNext()) {
			DenotatorPath firstOfNextSiblings = objectPathsIterator.next();
			while (firstOfNextSiblings != null) {
				firstOfNextSiblings = this.shapeAndAddNextSiblings(newObjectTracesAndOldYValues, firstOfNextSiblings, objectPathsIterator, shapingPaths);
			}
		}
		//PerformanceCheck.startTask(".find");
		Map<DenotatorPath,Double> newPathsAndOldYValues = new TreeMap<DenotatorPath,Double>();
		this.findPaths(newObjectTracesAndOldYValues, newPathsAndOldYValues);
		return newPathsAndOldYValues; 
	}
	
	private DenotatorPath shapeAndAddNextSiblings(Map<List<Denotator>,Double> newObjectTracesAndOldYValues, DenotatorPath firstSiblingPath, Iterator<DenotatorPath> nodePathsIterator, TransformationPaths shapingPaths) {
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
			if (currentSiblingPath.isSatelliteOf(siblingsAnchorPath)) {
				siblingsPaths.add(currentSiblingPath);
				siblings.add(this.score.getAbsoluteObject(currentSiblingPath));
			} else {
				this.removeShapeAndAdd(newObjectTracesAndOldYValues, siblings, siblingsAnchorPath, siblingsPaths, shapingPaths);
				return currentSiblingPath;
			}
		}
		this.removeShapeAndAdd(newObjectTracesAndOldYValues, siblings, siblingsAnchorPath, siblingsPaths, shapingPaths);
		return null;
	}
	
	private void removeShapeAndAdd(Map<List<Denotator>,Double> newObjectTracesAndOldYValues, List<Denotator> objects, DenotatorPath anchorPath, List<DenotatorPath> siblingsPaths, TransformationPaths shapingPaths) {
		//PerformanceCheck.startTask(".remove");
		if (!this.copyAndShape) {
			this.score.removeObjects(siblingsPaths);
		}
		this.shapeAndAddObjects(objects, anchorPath, newObjectTracesAndOldYValues, shapingPaths);
	}
	
	private void shapeAndAddObjects(List<Denotator> objects, DenotatorPath anchorPath, Map<List<Denotator>,Double> newObjectTracesAndOldYValues, TransformationPaths shapingPaths) {
		Map<Denotator,Double> newObjectsAndOldYValues = new HashMap<Denotator,Double>();
		//boolean modulators = nodesAndNotes.get(0).getForm().equals(this.score.objectGenerator.SOUND_NOTE_FORM);
		for (int i = 0; i < objects.size(); i++) {
			//PerformanceCheck.startTask(".map");
			this.shapeObject(objects.get(i), newObjectsAndOldYValues, shapingPaths);
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
	
	private Denotator shapeObject(Denotator object, Map<Denotator,Double> newObjectsAndOldYValues, TransformationPaths shapingPaths) {
		Double newValue = this.getValueOfClosestLocation(object, shapingPaths);
		DenotatorPath valuePath = shapingPaths.getDomainPath(1, object);
		double oldValue = this.score.objectGenerator.getDoubleValue(object, valuePath);
		if (newValue != null) {
			object = this.score.objectGenerator.replaceValue(object, valuePath, newValue);
		}
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

	private Double getValueOfClosestLocation(Denotator object, TransformationPaths shapingPaths) {
		DenotatorPath valuePath = shapingPaths.getDomainPath(0, object);
		double xPosition = this.score.objectGenerator.getDoubleValue(object, valuePath);
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
		return null;
	}

}

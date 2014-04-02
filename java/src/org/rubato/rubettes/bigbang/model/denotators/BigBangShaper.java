package org.rubato.rubettes.bigbang.model.denotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangShaper extends BigBangScoreManipulator {
	
	private final double SHAPING_RANGE = 0.5;
	
	private Set<DenotatorPath> objectPaths;
	private TreeMap<Double,Double> shapingLocations;
	private boolean copyAndShape;
	
	public BigBangShaper(BigBangDenotatorManager denotatorManager, Set<DenotatorPath> objectPaths, TreeMap<Double,Double> shapingLocations, List<TransformationPaths> paths, boolean copyAndShape) {
		super(denotatorManager, paths);
		this.objectPaths = objectPaths;
		this.shapingLocations = shapingLocations;
		this.copyAndShape = copyAndShape;
	}
	
	public OperationPathResults shapeCategorizedObjects() {
		//TODO WHAT NOW WITHOUT CATEGORIZED OBJECTS???
		//for (int i = 0; i < this.objectPaths.size(); i++) {
			this.shapeObjects(new ArrayList<DenotatorPath>(this.objectPaths), this.transformationPaths.get(0));
		//}
		return this.denotatorManager.getPathResults();
	}
	
	private void shapeObjects(List<DenotatorPath> objectPaths, TransformationPaths shapingPaths) {
		//PerformanceCheck.startTask(".pre");
		objectPaths = this.denotatorManager.sortAndReverse(objectPaths);
		
		Iterator<DenotatorPath> objectPathsIterator = objectPaths.iterator();
		if (objectPathsIterator.hasNext()) {
			DenotatorPath firstOfNextSiblings = objectPathsIterator.next();
			while (firstOfNextSiblings != null) {
				firstOfNextSiblings = this.shapeAndAddNextSiblings(firstOfNextSiblings, objectPathsIterator, shapingPaths);
			}
		}
	}
	
	private DenotatorPath shapeAndAddNextSiblings(DenotatorPath firstSiblingPath, Iterator<DenotatorPath> nodePathsIterator, TransformationPaths shapingPaths) {
		//PerformanceCheck.startTask(".first_sib");
		List<Denotator> siblings = new ArrayList<Denotator>();
		List<DenotatorPath> siblingsPaths = new ArrayList<DenotatorPath>();
		
		siblingsPaths.add(firstSiblingPath);
		siblings.add(this.denotatorManager.getAbsoluteObject(firstSiblingPath));
		DenotatorPath siblingsAnchorPath = firstSiblingPath.getAnchorPowersetPath();
		
		DenotatorPath currentSiblingPath = firstSiblingPath;
		while (nodePathsIterator.hasNext()) {
			currentSiblingPath = nodePathsIterator.next();
			//PerformanceCheck.startTask(".next_sibs");
			if (currentSiblingPath.isDirectSatelliteOf(siblingsAnchorPath)) {
				siblingsPaths.add(currentSiblingPath);
				siblings.add(this.denotatorManager.getAbsoluteObject(currentSiblingPath));
			} else {
				this.shapeAndReplaceOrAdd(siblings, siblingsAnchorPath, siblingsPaths, shapingPaths);
				return currentSiblingPath;
			}
		}
		this.shapeAndReplaceOrAdd(siblings, siblingsAnchorPath, siblingsPaths, shapingPaths);
		return null;
	}
	
	/*
	 * Shapes the given objects and adds them to the given anchorPath (they should thus originally be siblings).
	 * Returns a list with all the 
	 */	
	private void shapeAndReplaceOrAdd(List<Denotator> objects, DenotatorPath anchorPath, List<DenotatorPath> siblingsPaths, TransformationPaths shapingPaths) {
		Map<Denotator,Double> newObjectsAndOldYValues = new HashMap<Denotator,Double>();
		for (int i = 0; i < objects.size(); i++) {
			//PerformanceCheck.startTask(".map");
			this.shapeObject(objects.get(i), newObjectsAndOldYValues, shapingPaths);
		}
		//PerformanceCheck.startTask(".add");
		List<Denotator> newObjects = new ArrayList<Denotator>(newObjectsAndOldYValues.keySet());
		if (!this.copyAndShape) {
			this.denotatorManager.replaceSiblingObjects(newObjects, siblingsPaths);
		} else {
			//TODO: ADD THEM AS THE SAME TYPE AS THEIR ORIGINAL!! MODULATOR OR SATELLITE! FIGURE OUT POWERSET INDEX!!
			DenotatorPath powersetPath = anchorPath.getPowersetPath(0);
			this.denotatorManager.addObjectsToParent(newObjects, powersetPath);
		}
	}
	
	private Denotator shapeObject(Denotator object, Map<Denotator,Double> newObjectsAndOldYValues, TransformationPaths shapingPaths) {
		Double newValue = this.getValueOfClosestLocation(object, shapingPaths);
		DenotatorPath valuePath = shapingPaths.getDomainPath(1, object);
		double oldValue = this.denotatorManager.getObjectGenerator().getDoubleValue(object, valuePath);
		if (newValue != null) {
			object = this.denotatorManager.getObjectGenerator().replaceValue(object, valuePath, newValue);
		}
		newObjectsAndOldYValues.put(object, oldValue);
		return object;
	}

	private Double getValueOfClosestLocation(Denotator object, TransformationPaths shapingPaths) {
		DenotatorPath valuePath = shapingPaths.getDomainPath(0, object);
		double xPosition = this.denotatorManager.getObjectGenerator().getDoubleValue(object, valuePath);
		Map<Double,Double> subMap = this.shapingLocations.subMap(xPosition-this.SHAPING_RANGE, xPosition+this.SHAPING_RANGE);
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

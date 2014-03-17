package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.rubettes.util.PerformanceCheck;

public class BigBangWallpaper {
	
	private BigBangDenotatorManager denotatorManager;
	private Denotator compositionBeforeWallpaper;
	private Set<DenotatorPath> motif;
	private List<BigBangWallpaperDimension> dimensions;
	
	public BigBangWallpaper(BigBangDenotatorManager denotatorManager, Set<DenotatorPath> motif) {
		this.denotatorManager = denotatorManager;
		this.compositionBeforeWallpaper = denotatorManager.getComposition();
		this.motif = motif;
		this.dimensions = new ArrayList<BigBangWallpaperDimension>();
	}
	
	public Denotator getCompositionBeforeWallpaper() {
		return this.compositionBeforeWallpaper;
	}
	
	public void addDimension(int rangeFrom, int rangeTo) {
		this.dimensions.add(new BigBangWallpaperDimension(rangeFrom, rangeTo));
	}
	
	public void addTransformationToLastDimension(BigBangTransformation transformation) {
		transformation.setCopyAndMap(true);
		this.dimensions.get(dimensions.size()-1).addTransformation(transformation);
	}
	
	public OperationPathResults update() {
		Set<DenotatorPath> currentMotif = this.motif;
		OperationPathResults lastDimensionResults = null;
		for (int i = 0; i < this.dimensions.size(); i++) {
			if (i == this.dimensions.size()-1) {
				lastDimensionResults = new OperationPathResults();
			}
			BigBangWallpaperDimension currentDimension = this.dimensions.get(i);
			currentMotif = this.mapDimension(currentMotif, currentDimension, lastDimensionResults);
		}
		return lastDimensionResults;
	}
	
	private Set<DenotatorPath> mapDimension(Set<DenotatorPath> currentPaths, BigBangWallpaperDimension dimension, OperationPathResults lastDimensionResults) {
		Set<DenotatorPath> resultPaths = new TreeSet<DenotatorPath>();
		int rangeFrom = dimension.getRangeFrom();
		int rangeTo = dimension.getRangeTo();
		int i = 0;
		if (rangeFrom > 0) {
			while (i < rangeFrom) {
				currentPaths = this.mapIteration(currentPaths, dimension, false, false, lastDimensionResults);
				i++;
			}
			resultPaths.addAll(currentPaths);
		} else {
			while (i > rangeFrom) {
				currentPaths = this.mapIteration(currentPaths, dimension, false, true, lastDimensionResults);
				i--;
			}
			resultPaths.addAll(currentPaths);
		}
		
		while (i < rangeTo) {
			currentPaths = this.mapIteration(currentPaths, dimension, true, false, lastDimensionResults);
			resultPaths.addAll(currentPaths);
			i++;
		}
		
		return resultPaths;
	}
	
	private Set<DenotatorPath> mapIteration(Set<DenotatorPath> currentPaths, BigBangWallpaperDimension dimension, boolean copyAndMap, boolean inverse, OperationPathResults lastTransformationResults) {
		PerformanceCheck.startTask("iteration");
		List<BigBangTransformation> transformations = new ArrayList<BigBangTransformation>(dimension.getTransformations());
		if (inverse) {
			Collections.reverse(transformations);
		}
		OperationPathResults iterationPathResults = new OperationPathResults();
		for (int i = 0; i < transformations.size(); i++) {
			//System.out.println(currentPaths);
			BigBangTransformation currentTransformation = transformations.get(i); 
			if (inverse) {
				currentTransformation = currentTransformation.inverse();
			}
			currentTransformation.setCopyAndMap(copyAndMap && i == 0);
			PerformanceCheck.startTask("map");
			OperationPathResults currentPathResults = new BigBangMapper(this.denotatorManager, currentTransformation).mapCategorizedObjects(currentPaths);
			PerformanceCheck.startTask("UPPATHS");
			iterationPathResults.updatePaths(currentPathResults);
			//if last transformation of last dimension, record changed paths
			PerformanceCheck.startTask("UPPATHS2");
			if (lastTransformationResults != null && i == transformations.size()-1) {
				lastTransformationResults.updatePaths(currentPathResults);
			}
			//System.out.println(iterationPathResults);
			//if an anchor is copied, new paths include its satellites. need to be removed in order to yield motif
			PerformanceCheck.startTask("satellites");
			currentPaths = this.getSetWithoutSatellites((TreeSet<DenotatorPath>)iterationPathResults.getNewPaths());
		}
		PerformanceCheck.startTask("return");
		//return new paths of created iteration (motif for next iteration)
		return currentPaths;
	}
	
	private Set<DenotatorPath> getSetWithoutSatellites(TreeSet<DenotatorPath> paths) {
		Set<DenotatorPath> anchorPaths = new TreeSet<DenotatorPath>();
		Iterator<DenotatorPath> pathsIterator = paths.descendingIterator();
		while (pathsIterator.hasNext()) {
			DenotatorPath currentPath = pathsIterator.next();
			DenotatorPath currentAnchor = currentPath.getAnchorPath();
			if (currentAnchor == null || !paths.contains(currentAnchor)) {
				anchorPaths.add(currentPath);
			}
		}
		return anchorPaths;
	}
	
	public String toString() {
		return this.dimensions.toString();
	}

}

package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.util.DenotatorPath;

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
		//System.out.println("..."+((PowerDenotator)score.getComposition()).getFactorCount());
		Set<DenotatorPath> currentMotif = this.motif;
		OperationPathResults lastDimensionResults = null;
		for (int i = 0; i < this.dimensions.size(); i++) {
			if (i == this.dimensions.size()-1) {
				lastDimensionResults = new OperationPathResults();
			}
			BigBangWallpaperDimension currentDimension = this.dimensions.get(i);
			currentMotif = this.mapDimension(currentMotif, currentDimension, lastDimensionResults);
		}
		//System.out.println("..."+((PowerDenotator)score.getComposition()).getFactorCount());
		return lastDimensionResults;
	}
	
	private Set<DenotatorPath> mapDimension(Set<DenotatorPath> currentPaths, BigBangWallpaperDimension dimension, OperationPathResults lastTransformationResults) {
		Set<DenotatorPath> resultPaths = new TreeSet<DenotatorPath>();
		int rangeFrom = dimension.getRangeFrom();
		int rangeTo = dimension.getRangeTo();
		int i = 0;
		if (rangeFrom > 0) {
			while (i < rangeFrom) {
				currentPaths = this.mapIteration(currentPaths, dimension, false, false, lastTransformationResults);
				i++;
			}
			resultPaths.addAll(currentPaths);
		} else {
			while (i > rangeFrom) {
				currentPaths = this.mapIteration(currentPaths, dimension, false, true, lastTransformationResults);
				i--;
			}
			resultPaths.addAll(currentPaths);
		}
		
		while (i < rangeTo) {
			currentPaths = this.mapIteration(currentPaths, dimension, true, false, lastTransformationResults);
			resultPaths.addAll(currentPaths);
			i++;
		}
		
		return resultPaths;
	}
	
	private Set<DenotatorPath> mapIteration(Set<DenotatorPath> currentPaths, BigBangWallpaperDimension dimension, boolean copyAndMap, boolean inverse, OperationPathResults lastTransformationResults) {
		List<BigBangTransformation> transformations = new ArrayList<BigBangTransformation>(dimension.getTransformations());
		if (inverse) {
			Collections.reverse(transformations);
		}
		OperationPathResults iterationPathResults = new OperationPathResults();
		for (int i = 0; i < transformations.size(); i++) {
			BigBangTransformation currentTransformation = transformations.get(i); 
			if (inverse) {
				currentTransformation = currentTransformation.inverse();
			}
			currentTransformation.setCopyAndMap(copyAndMap && i == 0);
			System.out.println("m " + currentPaths);
			OperationPathResults currentPathResults = new BigBangMapper(this.denotatorManager, currentTransformation).mapCategorizedObjects(currentPaths);
			System.out.print("IT ");
			iterationPathResults.updatePaths(currentPathResults);
			//if last transformation of last dimension, record changed paths
			if (lastTransformationResults != null && i == transformations.size()-1) {
				System.out.print("DI ");
				lastTransformationResults.updatePaths(currentPathResults);
			}
			
			currentPaths = iterationPathResults.getNewPaths();
		}
		//score.resetLastNewPaths();
		//return new paths of created iteration (motif for next iteration)
		System.out.println("p "+currentPaths + " " + lastTransformationResults);
		return currentPaths;
	}
	
	public String toString() {
		return this.dimensions.toString();
	}

}

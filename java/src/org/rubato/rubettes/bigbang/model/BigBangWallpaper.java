package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangWallpaper {
	
	private SelectedObjectsPaths motif;
	private List<BigBangWallpaperDimension> dimensions;
	
	public BigBangWallpaper(SelectedObjectsPaths motif) {
		this.motif = motif;
		this.dimensions = new ArrayList<BigBangWallpaperDimension>();
	}
	
	public SelectedObjectsPaths getMotif() {
		return this.motif;
	}
	
	public void addDimension(int rangeFrom, int rangeTo) {
		this.dimensions.add(new BigBangWallpaperDimension(rangeFrom, rangeTo));
	}
	
	public List<BigBangWallpaperDimension> getDimensions() {
		return this.dimensions;
	}
	
	public void addTransformationToLastDimension(BigBangTransformation transformation) {
		transformation.setCopyAndMap(true);
		this.dimensions.get(dimensions.size()-1).addTransformation(transformation);
	}
	
	public void removeLastTransformation() {
		this.dimensions.get(dimensions.size()-1).removeLastTransformation();
	}
	
	public DenotatorPath getLastAnchorPath() {
		if (this.dimensions.size() > 0) {
			BigBangTransformation t = this.dimensions.get(dimensions.size()-1).getLastTransformation();
			if (t != null) {
				return t.getAnchorNodePath();
			}
		}
		return null;
	}
	
	public void setRange(int dimension, boolean rangeTo, int value) {
		BigBangWallpaperDimension currentDimension = this.dimensions.get(dimension);
		if (rangeTo) {
			currentDimension.setRangeTo(value);
		} else {
			currentDimension.setRangeFrom(value);
		}
	}
	
	public void applyTo(BigBangComposition score) {
		List<DenotatorPath> currentMotif = this.motif.get(0);
		for (BigBangWallpaperDimension currentDimension: this.dimensions) {
			currentMotif = this.mapDimension(score, currentMotif, currentDimension);
		}
		
	}
	
	private List<DenotatorPath> mapDimension(BigBangComposition score, List<DenotatorPath> currentPaths, BigBangWallpaperDimension dimension) {
		List<DenotatorPath> resultPaths = new ArrayList<DenotatorPath>();
		int rangeFrom = dimension.getRangeFrom();
		int rangeTo = dimension.getRangeTo();
		int i = 0;
		if (rangeFrom > 0) {
			while (i < rangeFrom) {
				currentPaths = this.map(score, currentPaths, dimension, false, false);
				i++;
			}
			resultPaths.addAll(currentPaths);
		} else {
			while (i > rangeFrom) {
				currentPaths = this.map(score, currentPaths, dimension, false, true);
				i--;
			}
			resultPaths.addAll(currentPaths);
		}
		
		while (i < rangeTo) {
			currentPaths = this.map(score, currentPaths, dimension, true, false);
			resultPaths.addAll(currentPaths);
			i++;
		}
		
		return resultPaths;
	}
	
	private List<DenotatorPath> map(BigBangComposition score, List<DenotatorPath> currentPaths, BigBangWallpaperDimension dimension, boolean copyAndMap, boolean inverse) {
		List<BigBangTransformation> transformations = dimension.getTransformations();
		if (inverse) {
			for (int i = transformations.size()-1; i >= 0; i--) {
				BigBangTransformation currentTransformation = transformations.get(i).inverse(); 
				currentTransformation.setCopyAndMap(copyAndMap && i == transformations.size()-1);
				//TODO: BAD!!! make ready for categorized objects!!!!
				currentPaths = new BigBangMapper(score, currentTransformation).mapCategorizedObjects(new SelectedObjectsPaths(new TreeSet<DenotatorPath>(currentPaths), null)).get(0);
			}
		} else {
			for (int i = 0; i < transformations.size(); i++) {
				BigBangTransformation currentTransformation = transformations.get(i); 
				currentTransformation.setCopyAndMap(copyAndMap && i == 0);
				//TODO: BAD!!! make ready for categorized objects!!!!
				currentPaths = new BigBangMapper(score, currentTransformation).mapCategorizedObjects(new SelectedObjectsPaths(new TreeSet<DenotatorPath>(currentPaths), null)).get(0);
			}
		}
		return currentPaths;
	}
	
	public String toString() {
		return this.dimensions.toString();
	}

}

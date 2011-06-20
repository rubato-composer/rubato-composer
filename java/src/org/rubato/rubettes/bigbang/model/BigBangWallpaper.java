package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.util.NotePath;

public class BigBangWallpaper {
	
	private List<NotePath> motif;
	private List<BigBangWallpaperDimension> dimensions;
	
	public BigBangWallpaper(List<NotePath> motif) {
		this.motif = motif;
		this.dimensions = new ArrayList<BigBangWallpaperDimension>();
	}
	
	public List<NotePath> getMotif() {
		return this.motif;
	}
	
	public void addDimension(int rangeFrom, int rangeTo) {
		this.dimensions.add(new BigBangWallpaperDimension(rangeFrom, rangeTo));
	}
	
	public void removeLastDimension() {
		if (this.dimensions.size() > 0) {
			this.dimensions.remove(this.dimensions.size()-1);
		}
	}
	
	public void addTransformationToLastDimension(BigBangTransformation transformation) {
		transformation.setCopyAndMap(true);
		this.dimensions.get(dimensions.size()-1).addTransformation(transformation);
	}
	
	public BigBangTransformation getLastTransformation() {
		return this.dimensions.get(dimensions.size()-1).getLastTransformation();
	}
	
	public void removeLastTransformation() {
		this.dimensions.get(dimensions.size()-1).removeLastTransformation();
	}
	
	public NotePath getLastAnchorPath() {
		if (this.dimensions.size() > 0) {
			BigBangTransformation t = this.dimensions.get(dimensions.size()-1).getLastTransformation();
			if (t != null) {
				return t.getAnchorNodePath();
			}
		}
		return null;
	}
	
	public void updateRanges(List<Integer> ranges) {
		for (int i = 0; i < this.dimensions.size(); i++) {
			BigBangWallpaperDimension currentDimension = this.dimensions.get(i);
			currentDimension.setRanges(ranges.get(2*i), ranges.get(2*i+1));
		}
	}
	
	public void applyTo(BigBangScore score) {
		List<NotePath> currentMotif = this.motif;
		for (BigBangWallpaperDimension currentDimension: this.dimensions) {
			currentMotif = this.mapDimension(score, currentMotif, currentDimension);
		}
		
	}
	
	private List<NotePath> mapDimension(BigBangScore score, List<NotePath> currentPaths, BigBangWallpaperDimension dimension) {
		List<NotePath> resultPaths = new ArrayList<NotePath>();
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
	
	private List<NotePath> map(BigBangScore score, List<NotePath> currentPaths, BigBangWallpaperDimension dimension, boolean copyAndMap, boolean inverse) {
		List<BigBangTransformation> transformations = dimension.getTransformations();
		if (inverse) {
			for (int i = transformations.size()-1; i >= 0; i--) {
				BigBangTransformation currentTransformation = transformations.get(i).inverse(); 
				currentTransformation.setCopyAndMap(copyAndMap && i == transformations.size()-1);
				currentPaths = new BigBangMapper(score, currentTransformation).mapNodes(currentPaths);
			}
		} else {
			for (int i = 0; i < transformations.size(); i++) {
				BigBangTransformation currentTransformation = transformations.get(i); 
				currentTransformation.setCopyAndMap(copyAndMap && i == 0);
				currentPaths = new BigBangMapper(score, currentTransformation).mapNodes(currentPaths);
			}
		}
		return currentPaths;
	}

}

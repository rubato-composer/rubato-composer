package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;

public class BigBangWallpaperDimension {
	
	private int rangeFrom, rangeTo;
	private List<BigBangTransformation> transformations;
	
	public BigBangWallpaperDimension(int rangeFrom, int rangeTo) {
		this.setRanges(rangeFrom, rangeTo);
		this.transformations = new ArrayList<BigBangTransformation>();
	}
	
	public void setRanges(int rangeFrom, int rangeTo) {
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
	}
	
	public int getRangeFrom() {
		return this.rangeFrom;
	}
	
	public int getRangeTo() {
		return this.rangeTo;
	}
	
	public void addTransformation(BigBangTransformation transformation) {
		this.transformations.add(transformation);
	}
	
	public BigBangTransformation getLastTransformation() {
		if (this.transformations.size() > 0) {
			return this.transformations.get(this.transformations.size()-1);
		} else {
			return null;
		}
	}
	
	public void removeLastTransformation() {
		this.transformations.remove(this.getLastTransformation());
	}
	
	public List<BigBangTransformation> getTransformations() {
		return this.transformations;
	}

}

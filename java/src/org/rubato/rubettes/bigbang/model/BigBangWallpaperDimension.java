package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;

public class BigBangWallpaperDimension {
	
	private int rangeFrom, rangeTo;
	private List<BigBangTransformation> transformations;
	
	public BigBangWallpaperDimension(int rangeFrom, int rangeTo) {
		this.setRangeFrom(rangeFrom);
		this.setRangeTo(rangeTo);
		this.transformations = new ArrayList<BigBangTransformation>();
	}
	
	public void setRangeFrom(int rangeFrom) {
		this.rangeFrom = rangeFrom;
	}
	
	public void setRangeTo(int rangeTo) {
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
		}
		return null;
	}
	
	public void removeLastTransformation() {
		this.transformations.remove(this.getLastTransformation());
	}
	
	public List<BigBangTransformation> getTransformations() {
		return this.transformations;
	}
	
	public String toString() {
		return this.transformations.toString();
	}

}

package org.rubato.rubettes.bigbang.model.operations;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractOperation {
	
	protected BigBangModel model;
	protected double modificationRatio;
	protected Double minModRatio, maxModRatio;
	protected boolean isAnimatable;
	protected boolean isSplittable;
	//duration in seconds
	protected double duration;
	
	public AbstractOperation(BigBangModel model) {
		this.model = model;
		this.modificationRatio = 1;
		this.isAnimatable = false;
		this.isSplittable = false;
		this.duration = 1;
	}
	
	protected abstract void updateOperation();
	
	public void modify(double ratio) {
		if (this.minModRatio != null && ratio <= this.minModRatio) {
			ratio = this.minModRatio;
		}
		if (this.maxModRatio != null && ratio >= this.maxModRatio) {
			ratio = this.maxModRatio;
		}
		this.modificationRatio = ratio;
		this.updateOperation();
	}
	
	protected Set<DenotatorPath> getObjectPaths(Set<BigBangObject> objects) {
		Set<DenotatorPath> objectPaths = new TreeSet<DenotatorPath>();
		if (objects.size() == 0) {
			//return all objects if none here!! operation will be applied to all!!
			objects = this.model.getObjects().getObjectsAt(this);
		}
		for (BigBangObject currentObject : objects) {
			DenotatorPath currentPath = currentObject.getTopDenotatorPathAt(this);
			if (currentPath != null) {
				objectPaths.add(currentPath);
			}
		}
		return objectPaths;
	}
	
	public String getPresentationName() {
		return  this.getSpecificPresentationName() + (this.isAnimatable ? " (" + Double.toString(this.duration) + ")" : "");
	}
	
	protected abstract String getSpecificPresentationName();
	
	public abstract OperationPathResults execute();
	
	public String toString() {
		return this.getPresentationName();
	}
	
	public boolean isAnimatable() {
		return this.isAnimatable;
	}
	
	public boolean isSplittable() {
		return this.isSplittable;
	}
	
	/**
	 * @param ratio a number between 0 and 1
	 * @return a list with two operations that represent this operation split at the given ratio.
	 * null if not splittable
	 */
	public List<AbstractOperation> getSplitOperations(double ratio) {
		return null;
	}
	
	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	public double getDuration() {
		return this.duration;
	}
	
}
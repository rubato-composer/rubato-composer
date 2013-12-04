package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractPathBasedOperationEdit extends AbstractOperationEdit {
	
	private List<DenotatorPath> objectPaths;
	protected List<DenotatorPath> modifiedObjectPaths;
	
	public AbstractPathBasedOperationEdit(BigBangScoreManager scoreManager) {
		super(scoreManager);
		this.isAnimatable = true;
		this.isSplittable = false;
		this.minModRatio = 0.0;
		this.maxModRatio = 1.0;
	}
	
	public AbstractPathBasedOperationEdit(BigBangScoreManager scoreManager, List<DenotatorPath> objectPaths) {
		this(scoreManager);
		this.setObjectPaths(objectPaths);
	}
	
	protected void setObjectPaths(List<DenotatorPath> objectPaths) {
		this.objectPaths = objectPaths;
		this.updateOperation();
	}
	
	//adjusts the number of objects to be handled according to this.modificationRatio
	protected void updateOperation() {
		this.modifiedObjectPaths = new ArrayList<DenotatorPath>();
		int modifiedNumberOfObjects = (int)Math.round(this.modificationRatio*this.objectPaths.size());
		for (int i = 0; i < modifiedNumberOfObjects; i++) {
			this.modifiedObjectPaths.add(this.objectPaths.get(i));
		}
	}
	
	protected void addMissingObjectPaths(List<DenotatorPath> paths) {
		while (paths.size() < this.objectPaths.size()) {
			paths.add(this.objectPaths.get(paths.size()));
		}
	}
	
	public List<AbstractOperationEdit> getSplitOperations(double ratio) {
		try {
			List<AbstractOperationEdit> splitOperations = new ArrayList<AbstractOperationEdit>();
			AbstractPathBasedOperationEdit firstOperation = this.clone();
			AbstractPathBasedOperationEdit secondOperation = this.clone();
			int firstNumberOfObjects = (int)Math.round(ratio*this.objectPaths.size());
			firstOperation.setObjectPaths(this.objectPaths.subList(0, firstNumberOfObjects));
			secondOperation.setObjectPaths(this.objectPaths.subList(firstNumberOfObjects, this.objectPaths.size()));
			splitOperations.add(firstOperation);
			splitOperations.add(secondOperation);
			return splitOperations;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public AbstractPathBasedOperationEdit clone() {
		AbstractPathBasedOperationEdit clone;
		try {
			clone = this.getClass().getDeclaredConstructor(BigBangScoreManager.class).newInstance(this.scoreManager);
			clone.setObjectPaths(this.objectPaths);
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

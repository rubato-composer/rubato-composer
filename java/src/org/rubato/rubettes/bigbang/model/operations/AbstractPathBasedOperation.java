package org.rubato.rubettes.bigbang.model.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;

public abstract class AbstractPathBasedOperation extends AbstractOperation {
	
	private Set<BigBangObject> objects;
	protected Set<BigBangObject> modifiedObjects;
	
	public AbstractPathBasedOperation(BigBangModel model) {
		super(model);
		this.isAnimatable = true;
		this.isSplittable = false;
		this.minModRatio = 0.0;
		this.maxModRatio = 1.0;
	}
	
	public AbstractPathBasedOperation(BigBangModel model, Set<BigBangObject> objects) {
		this(model);
		this.setObjects(objects);
	}
	
	protected void setObjects(Set<BigBangObject> objects) {
		this.objects = objects;
		this.updateOperation();
	}
	
	//adjusts the number of objects to be handled according to this.modificationRatio
	protected void updateOperation() {
		this.modifiedObjects = new TreeSet<BigBangObject>();
		int modifiedNumberOfObjects = (int)Math.round(this.modificationRatio*this.objects.size());
		Iterator<BigBangObject> objectIterator = this.objects.iterator();
		while (this.modifiedObjects.size() < modifiedNumberOfObjects) {
			this.modifiedObjects.add(objectIterator.next());
		}
	}
	
	public List<AbstractOperation> getSplitOperations(double ratio) {
		try {
			List<AbstractOperation> splitOperations = new ArrayList<AbstractOperation>();
			AbstractPathBasedOperation firstOperation = this.clone();
			AbstractPathBasedOperation secondOperation = this.clone();
			int amountInFirstSet = (int)Math.round(ratio*this.objects.size());
			List<Set<BigBangObject>> splitObjects = this.getSplitObjects(amountInFirstSet);
			firstOperation.setObjects(splitObjects.get(0));
			secondOperation.setObjects(splitObjects.get(1));
			splitOperations.add(firstOperation);
			splitOperations.add(secondOperation);
			return splitOperations;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Set<BigBangObject>> getSplitObjects(int amountInFirstSet) {
		List<Set<BigBangObject>> splitSets = new ArrayList<Set<BigBangObject>>();
		splitSets.add(new TreeSet<BigBangObject>());
		splitSets.add(new TreeSet<BigBangObject>());
		for (BigBangObject currentObject : this.objects) {
			if (splitSets.get(0).size() < amountInFirstSet) {
				splitSets.get(0).add(currentObject);
			} else {
				splitSets.get(1).add(currentObject);
			}
		}
		return splitSets;
	}
	
	public AbstractPathBasedOperation clone() {
		AbstractPathBasedOperation clone;
		try {
			clone = this.getClass().getDeclaredConstructor(BigBangModel.class).newInstance(this.model);
			clone.setObjects(this.objects);
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

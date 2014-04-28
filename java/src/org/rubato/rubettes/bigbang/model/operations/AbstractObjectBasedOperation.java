package org.rubato.rubettes.bigbang.model.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.xml.XMLReader;
import org.w3c.dom.Element;

public abstract class AbstractObjectBasedOperation extends AbstractOperation {
	
	private Set<BigBangObject> objects;
	
	protected AbstractObjectBasedOperation(BigBangModel model, AbstractObjectBasedOperation other) {
		this(model);
		if (model == other.model) {
			this.setObjects(new TreeSet<BigBangObject>(other.objects));
		}
	}
	
	public AbstractObjectBasedOperation(BigBangModel model, Set<BigBangObject> objects) {
		this(model);
		this.setObjects(objects);
	}
	
	private AbstractObjectBasedOperation(BigBangModel model) {
		super(model);
		this.init();
	}
	
	public AbstractObjectBasedOperation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		this.init();
	}
	
	private void init() {
		this.isAnimatable = true;
		this.isSplittable = false;
		this.minModRatio = 0.0;
		this.maxModRatio = 1.0;
		this.setObjects(new TreeSet<BigBangObject>());
	}
	
	protected void setObjects(Set<BigBangObject> objects) {
		this.objects = objects;
		this.updateOperation();
	}
	
	protected void addObjects(Set<BigBangObject> objects) {
		this.objects.addAll(objects);
		this.updateOperation();
	}
	
	protected void updateOperation() {
		//do nothing. modificationRatio is considered in getObjectPaths 
	}
	
	protected Set<DenotatorPath> getObjectPaths() {
		List<DenotatorPath> objectPaths = new ArrayList<DenotatorPath>(super.getObjectPaths(this.objects));
		int modifiedNumberOfObjects = (int)Math.round(this.modificationRatio*objectPaths.size());
		return new TreeSet<DenotatorPath>(objectPaths.subList(0, modifiedNumberOfObjects));
	}
	
	public List<AbstractOperation> getSplitOperations(double ratio) {
		try {
			List<AbstractOperation> splitOperations = new ArrayList<AbstractOperation>();
			AbstractObjectBasedOperation firstOperation = (AbstractObjectBasedOperation)this.clone();
			AbstractObjectBasedOperation secondOperation = (AbstractObjectBasedOperation)this.clone();
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

}

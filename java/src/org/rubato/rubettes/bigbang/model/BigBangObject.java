package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.operations.AbstractOperation;
import org.rubato.rubettes.util.DenotatorObjectConfiguration;
import org.rubato.rubettes.util.DenotatorPath;

/**
 * Every denotator that is either the top level denotator or an element of a powerset is represented by a
 * BigBangObject. This class keeps track of the path of its location as well as its values at every stage of the 
 * composition, i.e. before every operation. It is used to bring the comfort of permanent identity known from object oriented programming to the
 * otherwise functional world of Rubato Composer.  
 * 
 * @author florian thalmann
 */
public class BigBangObject implements Comparable<BigBangObject> {
	
	private AbstractOperation creatingOperation;
	private DenotatorObjectConfiguration objectType;
	
	//these attributes are recorded for each operation before which this object exists
	//topDenotatorPaths are relative to parent's topDenotatorPath
	private Map<AbstractOperation,DenotatorPath> topDenotatorPaths;
	//private Map<AbstractOperationEdit,DenotatorObjectConfiguration> objectType;
	private Map<AbstractOperation,BigBangObject> parents;
	private Map<AbstractOperation,Set<BigBangObject>> children;
	
	//these attributes just reflect the final state (final state also reflected under null keys in maps!)
	private List<Double> values;
	private List<Double> preliminaryValues; //next values while being added, then just replace this.values
	private List<Integer> structuralIndices; //sibling number, satellite level, colimit index, etc
	private List<BigBangLayer> layers;
	
	
	/**
	 * Constructs a standard BigBangObject.
	 * @param creatingOperation the operation during which this object is created
	 * @param initialOperation the first operation this object exist at
	 * @param parent
	 * @param topDenotatorPath
	 */
	public BigBangObject(AbstractOperation creatingOperation, AbstractOperation initialOperation, BigBangObject parent, DenotatorPath topDenotatorPath, BigBangLayer layer) {
		this.creatingOperation = creatingOperation;
		this.values = new ArrayList<Double>();
		this.preliminaryValues = new ArrayList<Double>();
		this.topDenotatorPaths = new HashMap<AbstractOperation,DenotatorPath>();
		this.parents = new HashMap<AbstractOperation,BigBangObject>();
		this.children = new HashMap<AbstractOperation,Set<BigBangObject>>();
		this.children.put(initialOperation, new TreeSet<BigBangObject>());
		this.updatePathAndParent(initialOperation, topDenotatorPath, parent);
		this.setLayer(layer);
	}
	
	public void setObjectType(DenotatorObjectConfiguration objectType) {
		this.objectType = objectType;
	}
	
	public BigBangObject getParent() {
		return this.parents.get(null);
	}
	
	public BigBangObject getParentAt(AbstractOperation operation) {
		return this.parents.get(operation);
	}
	
	public void addChild(AbstractOperation operation, BigBangObject newChild) {
		//System.out.println(operation + " " + newChild + " " + this.children.get(operation) + " " + this.getTopDenotatorPath());
		if (!this.children.containsKey(operation)) {
			this.children.put(operation, new TreeSet<BigBangObject>());
		}
		this.children.get(operation).add(newChild);
	}
	
	public boolean hasChildren() {
		return this.children.get(null).size() > 0;
	}
	
	public boolean hasChildrenAt(AbstractOperation operation) {
		return this.children.get(operation).size() > 0;
	}
	
	public void removeChild(AbstractOperation operation, BigBangObject newChild) {
		this.children.get(operation).remove(newChild);
	}
	
	public void removeAllChildren(AbstractOperation operation) {
		if (this.children.containsKey(operation)) {
			this.children.get(operation).clear();
		}
	}
	
	/**
	 * removes this object from all children sets it appears in
	 */
	public void removeFromHierarchy() {
		for (AbstractOperation currentOperation : this.parents.keySet()) {
			this.parents.get(currentOperation).removeChild(currentOperation, this);
		}
	}
	
	public Set<BigBangObject> getChildrenAt(AbstractOperation operation) {
		return this.children.get(operation);
	}
	
	public Set<BigBangObject> getChildren() {
		Set<BigBangObject> children = this.children.get(null);
		if (children != null) {
			return children;
		}
		return new TreeSet<BigBangObject>();
	}
	
	public void setColimitIndex(int index) {
		this.structuralIndices.set(this.structuralIndices.size()-1, index);
	}
	
	public void updateValues() {
		this.values = this.preliminaryValues;
		this.preliminaryValues = new ArrayList<Double>();
	}
	
	public void addValues(List<Double> values) {
		this.preliminaryValues.addAll(values);
	}
	
	public void setStructuralIndices(List<Integer> indices) {
		this.structuralIndices = indices;
	}
	
	public void updatePathAndParent(AbstractOperation operation, DenotatorPath entirePath, BigBangObject parent) {
		//remove from parent if there was one, then add new parent if not null
		BigBangObject previousParent = this.getParentAt(operation);
		if (previousParent != null && previousParent != parent) {
			previousParent.removeChild(operation, this);
		}
		
		//System.out.println("UP " + operation + " " + path);
		DenotatorPath subPath = entirePath;
		if (parent != null) {
			subPath = entirePath.subPath(parent.getTopDenotatorPathAt(operation).size());
		}
		this.topDenotatorPaths.put(operation, subPath);
		
		//add to new parent
		this.parents.put(operation, parent);
		if (parent != null) {
			parent.addChild(operation, this);
		}
	}
	
	/*
	 * Associates the previous final path with the given operation.
	 */
	/*public void concretizeFinalPath(AbstractOperationEdit operation) {
		this.topDenotatorPaths.put(operation, this.topDenotatorPaths.get(null));
		this.parents.put(operation, this.parents.get(null));
		this.children.put(operation, this.children.get(null));
		//could remove all the null ones. but will typically be overwritten right after...
	}*/
	
	public int getCurrentOccurrencesOfValueName(String valueName) {
		return this.objectType.getOccurrencesOfValueName(valueName);
	}
	
	public List<Double> getValues(String valueName) {
		List<Double> values = new ArrayList<Double>();
		List<Integer> indices = this.objectType.getIndicesOfValueName(valueName);
		if (indices != null) {
			for (int currentIndex : indices) {
				if (currentIndex < this.values.size()) {
					values.add(this.values.get(currentIndex));
				}
			}
		}
		return values;
	}
	
	/**
	 * @return the nth value with the given name, n >= 0 like a normal index
	 */
	public Double getNthValue(String valueName, int n) {
		if (valueName.equals(DenotatorValueExtractor.SATELLITE_LEVEL) || valueName.equals(DenotatorValueExtractor.COLIMIT_INDEX)) {
			return this.structuralIndices.get(0).doubleValue();
		} else if (valueName.equals(DenotatorValueExtractor.SIBLING_NUMBER)) {
			return this.structuralIndices.get(1).doubleValue();
		}
		int valueIndex = this.objectType.getIndexOfNthInstanceOfValueName(valueName, n);
		if (valueIndex != -1 && valueIndex < this.values.size()) {
			Double value = this.values.get(valueIndex);
			if (value != null) {
				return value;
			}
		} else if (this.parents.get(null) != null) {
			return this.parents.get(null).getNthValue(valueName, n);
		}
		return null;
	}
	
	public DenotatorPath getTopDenotatorPath() {
		DenotatorPath topDenotatorPath = this.getTopDenotatorPathAt(null);
		if (topDenotatorPath != null) {
			return topDenotatorPath;
		}
		//often only option for new objects
		return this.topDenotatorPaths.get(this.topDenotatorPaths.keySet().iterator().next());
	}
	
	public DenotatorPath getTopDenotatorPathAt(AbstractOperation operation) {
		if (this.topDenotatorPaths.get(operation) != null) {
			if (this.parents.get(operation) != null) {
				return this.parents.get(operation).getTopDenotatorPathAt(operation).append(this.topDenotatorPaths.get(operation));
			}
			return this.topDenotatorPaths.get(operation);
		}
		return null;
	}
	
	public AbstractOperation getCreatingOperation() {
		return this.creatingOperation;
	}
	
	public void removeOperation(AbstractOperation operation) {
		this.topDenotatorPaths.remove(operation);
		this.parents.remove(operation);
		this.children.remove(operation);
	}
	
	public void setLayer(BigBangLayer layer) {
		this.layers = new ArrayList<BigBangLayer>();
		this.addLayer(layer);
	}
	
	public void addLayer(BigBangLayer layer) {
		this.layers.add(layer);
	}
	
	public boolean isOnLayer(int layerIndex) {
		for (BigBangLayer currentLayer : this.layers) {
			if (currentLayer.getIndex() == layerIndex) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isActive() {
		for (BigBangLayer currentLayer : this.layers) {
			if (currentLayer.isActive()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isVisible() {
		for (BigBangLayer currentLayer : this.layers) {
			if (currentLayer.isVisible()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAudible() {
		for (BigBangLayer currentLayer : this.layers) {
			if (currentLayer.isAudible()) {
				return true;
			}
		}
		return false;
	}
	
	//TODO improve....
	public int compareTo(BigBangObject other) {
		//return this.getTopDenotatorPath().compareTo(other.getTopDenotatorPath());
		DenotatorPath thisPath = this.getTopDenotatorPath();
		DenotatorPath otherPath = other.getTopDenotatorPath();
		if (thisPath != null && otherPath != null && !thisPath.equals(otherPath)) {
			return thisPath.compareTo(otherPath);
		}
		List<AbstractOperation> paths = new ArrayList<AbstractOperation>(this.topDenotatorPaths.keySet());
		for (AbstractOperation currentOperation : paths) {
			if (other.topDenotatorPaths.containsKey(currentOperation)) {
				thisPath = this.getTopDenotatorPathAt(currentOperation);
				otherPath = other.getTopDenotatorPathAt(currentOperation);
				if (thisPath != null && otherPath != null && !thisPath.equals(otherPath)) {
					return thisPath.compareTo(otherPath);
				}
			}
		}
		return 0;
		/*int lastState = this.getLastExistingState();
		return this.getTopDenotatorPathAt(lastState).compareTo(object.getTopDenotatorPathAt(lastState));
		/*if (!(object instanceof DisplayObject)) {
			throw new ClassCastException("DisplayNote expected.");
		}
		DisplayObject otherNote = (DisplayObject)object;
		int layerCompare = new Integer(this.layer).compareTo(otherNote.layer);
		if (layerCompare != 0) { 
			return layerCompare;
		}
		for (int i = 0; i < this.values.size(); i++) {
			Double thisValue = this.values.get(i);
			Double otherValue = otherNote.values.get(i);
			int comparison = thisValue.compareTo(otherValue);
			if (comparison != 0) {
				return comparison;
			}
		}
		return 0;
		*/
	}
	
	public String toString() {
		return super.toString() + "(" + this.topDenotatorPaths.toString() + " " +  this.values + ")";
		//return super.toString() +"(" + this.topDenotatorPaths.toString() + " " + this.values + ")";
	}

}

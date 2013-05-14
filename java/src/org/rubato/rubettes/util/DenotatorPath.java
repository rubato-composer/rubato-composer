package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.base.RubatoException;
import org.rubato.math.module.Module;
import org.rubato.math.module.ProductRing;
import org.rubato.math.yoneda.Form;
import org.rubato.math.yoneda.SimpleForm;

/**
 * Just interesting for comparing paths denoting coordinates of the same denotator,
 * i.e. same size and only last index deferring.
 * @author flo
 *
 * TODO: calculate form, module etc when path is made and altered!!!!
 */
public class DenotatorPath implements Comparable<Object> {
	
	private List<Integer> indices;
	private Form baseForm;
	//form at which the path ends
	private Form form;
	//null if not a simple form
	private Module module;
	private int elementPathIndex;
	
	//necessary for cyclical forms without powersets!
	private static final int POWERSET_SEARCH_DEPTH = 10;
	
	//TODO: REMOVE AT SOME POINT!!! constants designating the three possible note functions (carriers are either anchor or satellite...)
	public static final int ANCHOR = 0;
	public static final int SATELLITE = 1;
	public static final int MODULATOR = 2;
	
	public DenotatorPath(Form baseForm) {
		this.baseForm = baseForm;
		this.indices = new ArrayList<Integer>();
		this.updateFormAndModule();
	}
	
	public DenotatorPath(Form baseForm, int[] path) {
		this(baseForm);
		for (int currentIndex: path) {
			this.indices.add(currentIndex);
		}
		this.updateFormAndModule();
	}
	
	public DenotatorPath(Form baseForm, List<Integer> path) {
		this(baseForm);
		this.indices = path;
		this.updateFormAndModule();
	}
	
	private void updateFormAndModule() {
		Form currentForm = this.baseForm;
		Module currentModule = null;
		this.elementPathIndex = -1;
		for (int i = 0; i < this.indices.size(); i++) {
			int currentIndex = this.indices.get(i);
			if (currentForm.getType() == Form.POWER || currentForm.getType() == Form.LIST) {
				currentForm = currentForm.getForm(0);
			} else if (currentForm.getType() != Form.SIMPLE) {
				if (currentForm.getFormCount() <= currentIndex) {
					//hahaha
					try { throw new RubatoException("Illegal DenotatorPath: " + this.baseForm.getNameString() + ": " + this); }
					catch (RubatoException e) { e.printStackTrace(); }
				}
				currentForm = currentForm.getForm(currentIndex);
			} else {
				currentModule = ((SimpleForm)currentForm).getModule();
				this.elementPathIndex = i;
				for (int j = i; j < this.indices.size(); j++) {
					if (currentModule instanceof ProductRing) {
						currentModule = ((ProductRing)currentModule).getFactor(j);
					} else {
						currentModule = currentModule.getComponentModule(j);
					}
				}
			}
		}
		this.form = currentForm;
		this.module = currentModule;
	}
	
	public int size() {
		return this.indices.size();
	}
	
	public boolean isElementPath() {
		return this.module != null;
	}
	
	public DenotatorPath getDenotatorSubpath() {
		if (this.elementPathIndex >= 0) {
			return this.subPath(0, this.elementPathIndex);
		}
		return this.clone();
	}
	
	public DenotatorPath getElementSubpath() {
		if (this.elementPathIndex >= 0) {
			return this.subPath(this.elementPathIndex);
		}
		return null;
	}
	
	public boolean inConflictingColimitPositions(DenotatorPath path) {
		Set<DenotatorPath> thisColimits = new TreeSet<DenotatorPath>(this.getParentColimitPaths());
		Set<DenotatorPath> pathColimits = new TreeSet<DenotatorPath>(path.getParentColimitPaths());
		for (DenotatorPath currentColimitPath : thisColimits) {
			if (pathColimits.contains(currentColimitPath)) {
				if (this.subPath(0, currentColimitPath.size()+1).getLastIndex()
						!= path.subPath(0, currentColimitPath.size()+1).getLastIndex()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public DenotatorPath clone() {
		return new DenotatorPath(this.baseForm, new ArrayList<Integer>(this.indices));
	}
	
	public DenotatorPath replaceLast(int index) {
		DenotatorPath path = this.getParentPath();
		path.add(index);
		return path;
	}
	
	public DenotatorPath subPath(int fromIndex) {
		return this.subPath(fromIndex, this.indices.size());
	}
	
	public DenotatorPath subPath(int fromIndex, int toIndex) {
		try {
			return new DenotatorPath(this.baseForm, new ArrayList<Integer>(this.indices.subList(fromIndex, toIndex)));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	/**
	 * New implementation, where all shorter paths and there the paths having smaller last
	 * indices are smaller.
	 */
	public int compareTo(Object object) {
		if (!(object instanceof DenotatorPath)) {
			throw new ClassCastException("DenotatorPath expected, got " + object.getClass());
		}
		DenotatorPath otherPath = (DenotatorPath)object;
		if (this.indices.size() == otherPath.indices.size()) {
			for (int i = 0; i < this.indices.size(); i++) {
				Integer thisIndex = this.indices.get(i);
				Integer otherIndex = otherPath.indices.get(i);
				if (!thisIndex.equals(otherIndex)) {
					return thisIndex.compareTo(otherIndex);
				}
			}
			return 0;
		}
		return this.indices.size() - otherPath.indices.size();
	}
	
	public boolean equals(Object object) {
		if (object == null || !(object instanceof DenotatorPath)) {
			return false;
		}
		return this.indices.equals(((DenotatorPath)object).indices);
	}
	
	/**
	 * Returns an array conversion of a List path
	 */
	public int[] toIntArray() {
		int[] arrayPath = new int[this.indices.size()];
		for (int i = 0; i < arrayPath.length; i++) {
			arrayPath[i] = this.indices.get(i).intValue();
		}
		return arrayPath;
	}
	
	private void add(int index) {
		this.indices.add(index);
		this.updateFormAndModule();
	}
	
	public int getLastIndex() {
		return this.indices.get(this.indices.size()-1);
	}
	
	/**
	 * @param childIndex
	 * @return the path of the childIndex-th child of this path, regardless wether it exists or not
	 */
	public DenotatorPath getChildPath(int childIndex) {
		if (this.form.getFormCount() > childIndex || this.form.getType() == Form.POWER || this.form.getType() == Form.LIST
				|| (this.form.getType() == Form.SIMPLE && this.getSubModuleOrRing(childIndex) != null)) {
			DenotatorPath childPath = this.clone();
			childPath.add(childIndex);
			return childPath;
		}
		return null;
	}
	
	/**
	 * @return the path of the denotator this object is contained in
	 */
	public DenotatorPath getParentPath() {
		return this.subPath(0, this.indices.size()-1);
	}
	
	public DenotatorPath getSatellitePath(int index, int powersetIndex) {
		DenotatorPath powersetPath = this.getPowersetPath(powersetIndex);
		return powersetPath.getChildPath(index);
	}
	
	/**
	 * @return the top path of the connected object this object is a satellite of. null if there is none or if it is the topmost object
	 */
	public DenotatorPath getAnchorPath() {
		DenotatorPath anchorPowersetPath = this.getAnchorPowersetPath();
		if (anchorPowersetPath != null) {
			return anchorPowersetPath.getTopPath();
		}
		return null;
	}
	
	/**
	 * @return a list of the paths of all anchor notes through the hierarchy starting with the direct anchor
	 */
	public List<DenotatorPath> getAnchorPaths() {
		List<DenotatorPath> anchorPaths = new ArrayList<DenotatorPath>();
		DenotatorPath currentAnchor = this.getAnchorPath();
		while (currentAnchor != null) {
			anchorPaths.add(currentAnchor);
			currentAnchor = currentAnchor.getAnchorPath();
		}
		return anchorPaths;
	}
	
	/**
	 * @return the path of the powerset this connected object is in
	 */
	public DenotatorPath getAnchorPowersetPath() {
		return this.getTopPath().getParentPath();
	}
	
	/**
	 * @return the index of this connected object in the powerset it is in
	 */
	public int getObjectIndex() {
		return this.getTopPath().getLastIndex();
	}
	
	public List<DenotatorPath> getParentColimitPaths() {
		DenotatorPath currentPath = this.clone();
		List<DenotatorPath> parentColimitPaths = new ArrayList<DenotatorPath>();
		while (currentPath != null) {
			if (currentPath.getForm().getType() == Form.COLIMIT) {
				parentColimitPaths.add(currentPath);
			}
			currentPath = currentPath.getParentPath();
		}
		Collections.reverse(parentColimitPaths);
		return parentColimitPaths;
	}
	
	/*
	 * @return the top path of this connected object, meaning the path at which it appears in a powerset above
	 */
	public DenotatorPath getTopPath() {
		DenotatorPath currentPath = this;
		Form currentParentForm = currentPath.getParentForm();
		while(currentParentForm != null && currentParentForm.getType() != Form.POWER && currentParentForm.getType() != Form.LIST) {
			currentPath = currentPath.getParentPath();
			currentParentForm = currentPath.getParentForm();
		}
		return currentPath;
	}
	
	private Form getParentForm() {
		DenotatorPath parentPath = this.getParentPath(); 
		if (parentPath != null) {
			return parentPath.getForm();
		}
		return null;
	}
	
	public boolean isSatelliteOf(DenotatorPath path) {
		DenotatorPath anchorPath = this.getTopPath().getAnchorPath();
		return (path == null && anchorPath == null) || anchorPath.equals(path.getTopPath());
	}
	
	public boolean isDescendantOf(DenotatorPath path) {
		return this.size() > path.size() && this.subPath(0, path.size()).equals(path);
	}
	
	/*public DenotatorPath getMinimalCommonParentPath(DenotatorPath path) {
		if (path != null) {
			DenotatorPath minimalCommonParentPath = new DenotatorPath(this.baseForm);
			for (int i = 0; i < Math.min(this.size(), path.size()); i++) {
				Integer currentIndex = this.indices.get(i);
				if (currentIndex.equals(path.indices.get(i))) {
					minimalCommonParentPath.add(currentIndex);
				} else break;
			}
			return minimalCommonParentPath;
		}
		return null;
	}*/
	
	public static List<DenotatorPath> getAnchorPaths(List<DenotatorPath> denotatorPaths) {
		List<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		for (DenotatorPath currentObjectPath: denotatorPaths) {
			parentPaths.add(currentObjectPath.getAnchorPath());
		}
		return parentPaths;
	}
	
	public static List<DenotatorPath> getGrandAnchorPowersetPaths(List<DenotatorPath> denotatorPaths) {
		List<DenotatorPath> grandParentPaths = new ArrayList<DenotatorPath>();
		for (DenotatorPath currentNotePath: denotatorPaths) {
			grandParentPaths.add(currentNotePath.getAnchorPath().getAnchorPowersetPath());
		}
		return grandParentPaths;
	}
	
	/**
	 * TODO: this will have to get all the powerset indices for the given paths!!!!!!!! OHOooH
	 */
	public static int[] getPowersetIndices(List<DenotatorPath> paths) {
		int[] powersetIndices = new int[paths.size()];
		for (int i = 0; i < paths.size(); i++) {
			DenotatorPath currentPath = paths.get(i);
			powersetIndices[i] = 0; //currentPath.getPowersetIndex();
		}
		return powersetIndices;
	}
	
	/**
	 * @return the form that the path ends with, even if it goes on into module elements
	 */
	public Form getForm() {
		return this.form;
	}
	
	public Module getModule() {
		return this.module;
	}
		
	private Module getSubModuleOrRing(int index) {
		if (this.form.getType() == Form.SIMPLE && this.module == null) {
			return this.getSubModule(((SimpleForm)this.form).getModule(), index); 
		} else if (this.module != null) {
			return this.getSubModule(this.module, index);
		}
		return null;
	}
	
	private Module getSubModule(Module module, int index) {
		if (module instanceof ProductRing) {
			return ((ProductRing)module).getFactor(index);
		}
		return module.getComponentModule(index);
	}
	
	public DenotatorPath getFirstPowersetPath() {
		return this.getPowersetPath(0);
	}
	
	/**
	 * @return the path of the powersetIndex-th powerset found, null if there are none within the given
	 * search depth or less than reached by the powersetIndex. search does not continue through powersets. if the
	 * given form itself is a powerset and powersetIndex is 0 it is returned instantly.
	 */
	public DenotatorPath getPowersetPath(int powersetIndex) {
		PriorityQueue<DenotatorPath> pathQueue = new PriorityQueue<DenotatorPath>();
		int foundCount = 0;
		pathQueue.add(this.getTopPath());
		while (!pathQueue.isEmpty()) {
			DenotatorPath currentPath = pathQueue.poll();
			Form currentForm = currentPath.getForm();
			//TODO: LIST TOO, change all names!!!
			if (currentForm.getType() == Form.POWER || currentForm.getType() == Form.LIST) {
				if (foundCount >= powersetIndex) {
					return currentPath;
				}
				foundCount++;
			} else {
				for (int i = 0; i < currentForm.getFormCount(); i++) {
					pathQueue.add(currentPath.getChildPath(i));
				}
			}
		}
		//gets here when there are less powersets than reached by powersetIndex
		return null;
	}
	
	public String toString() {
		return this.indices.toString();
	}

}

package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.rubato.math.yoneda.Form;

/**
 * Just interesting for comparing paths denoting coordinates of the same denotator,
 * i.e. same size and only last index deferring.
 * @author flo
 *
 */
public class DenotatorPath extends ArrayList<Integer> implements Comparable<Object> {
	
	private Form baseForm;
	
	//necessary for cyclical forms without powersets!
	private static final int POWERSET_SEARCH_DEPTH = 10;
	
	//TODO: REMOVE AT SOME POINT!!! constants designating the three possible note functions (carriers are either anchor or satellite...)
	public static final int ANCHOR = 0;
	public static final int SATELLITE = 1;
	public static final int MODULATOR = 2;
	
	public DenotatorPath(Form baseForm) {
		this.baseForm = baseForm;
	}
	
	public DenotatorPath(Form baseForm, int[] path) {
		this(baseForm);
		for (int currentIndex: path) {
			this.add(currentIndex);
		}
	}
	
	private DenotatorPath(Form baseForm, List<Integer> path) {
		super(path);
		this.baseForm = baseForm;
	}
	
	public DenotatorPath clone() {
		return new DenotatorPath(this.baseForm, this);
	}
	
	public DenotatorPath subPath(int fromIndex) {
		return this.subPath(fromIndex, this.size()-1);
	}
	
	public DenotatorPath subPath(int fromIndex, int toIndex) {
		try {
			return new DenotatorPath(this.baseForm, this.subList(fromIndex, toIndex));
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
		if (this.size() == otherPath.size()) {
			for (int i = 0; i < this.size(); i++) {
				Integer thisIndex = this.get(i);
				Integer otherIndex = otherPath.get(i);
				if (!thisIndex.equals(otherIndex)) {
					return thisIndex.compareTo(otherIndex);
				}
			}
			return 0;
		}
		return this.size() - otherPath.size();
	}
	
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		return super.equals(object);
	}
	
	/**
	 * Returns an array conversion of a List path
	 */
	public int[] toIntArray() {
		int[] arrayPath = new int[this.size()];
		for (int i = 0; i < arrayPath.length; i++) {
			arrayPath[i] = this.get(i).intValue();
		}
		return arrayPath;
	}
	
	private int getLastIndex() {
		return this.get(this.size()-1);
	}
	
	/**
	 * @param childIndex
	 * @return the path of the childIndex-th child of this path, regardless wether it exists or not
	 */
	public DenotatorPath getChildPath(int childIndex) {
		if (this.getForm().getFormCount() > childIndex || this.getForm().getType() == Form.POWER) {
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
		return this.subPath(0, this.size()-1);
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
	
	/*
	 * @return the top path of this connected object, meaning the path at which it appears in a powerset above
	 */
	private DenotatorPath getTopPath() {
		DenotatorPath currentPath = this;
		Form currentParentForm = currentPath.getParentForm();
		while(currentParentForm != null && currentParentForm.getType() != Form.POWER) {
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
	
	public boolean isChildOf(DenotatorPath path) {
		DenotatorPath anchorPath = this.getTopPath().getAnchorPath();
		return (path == null && anchorPath == null) || anchorPath.equals(path.getTopPath());
	}
	
	public static List<DenotatorPath> getAnchorPowersetPaths(List<DenotatorPath> denotatorPaths) {
		List<DenotatorPath> parentPaths = new ArrayList<DenotatorPath>();
		for (DenotatorPath currentObjectPath: denotatorPaths) {
			parentPaths.add(currentObjectPath.getAnchorPowersetPath());
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
	 * @return the form that the path ends with TODO: ignore module elements!!!!
	 */
	public Form getForm() {
		Form currentForm = this.baseForm;
		for (int i : this) {
			currentForm = currentForm.getForm(i);
		}
		return currentForm;
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
			if (currentForm.getType() == Form.POWER) {
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

}

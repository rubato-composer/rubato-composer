package org.rubato.rubettes.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Just interesting for comparing paths denoting coordinates of the same denotator,
 * i.e. same size and only last index deferring.
 * @author flo
 *
 */
public class NotePath extends ArrayList<Integer> implements Comparable<Object> {
	
	//constants designating the three possible note functions (carriers are either anchor or
	//satellite...)
	public static final int ANCHOR = 0;
	public static final int SATELLITE = 1;
	public static final int MODULATOR = 2;
	
	public NotePath() {
	}
	
	public NotePath(NotePath path) {
		super(path);
	}
	
	private NotePath(NotePath path, int additionalIndex) {
		super(path);
		this.add(additionalIndex);
	}
	
	public NotePath(int[] path) {
		for (int currentIndex: path) {
			this.add(currentIndex);
		}
	}
	
	private NotePath(List<Integer> path) {
		super(path);
	}
	
	private void removeLast() {
		this.remove(this.size()-1);
	}
	
	private NotePath subPath(int fromIndex, int toIndex) {
		try {
			return new NotePath(this.subList(fromIndex, toIndex));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	/**
	 * New implementation, where all shorter paths and there the paths having smaller last
	 * indices are smaller.
	 */
	public int compareTo(Object object) {
		if (!(object instanceof NotePath)) {
			throw new ClassCastException("NotePath expected, got " + object.getClass());
		}
		NotePath otherPath = (NotePath)object;
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
	
	private boolean isTopLevelAnchor() {
		return this.size() <= 2;
	}
	
	/**
	 * @return true if it is a path for a denotator on the modulator level
	 */
	public boolean isModulatorPath() {
		for (int i = 1; i < this.size()-1; i+=2) {
			if (this.get(i) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPowersetPath() {
		if (!this.isModulatorPath()) {
			return this.size() == 0 || (this.size() % 2 == 0 && this.get(this.size()-1) == 1);
		}
		return this.size() % 2 == 1;
	}
	
	public NotePath getPowersetChildPath(int index) {
		if (this.isPowersetPath()) {
			NotePath childPath = new NotePath(this);
			childPath.add(index);
			if (!this.isModulatorPath()) {
				childPath.add(0);
			}
			return childPath;
		}
		return null;
	}
	
	public NotePath getChildPath(int index, boolean modulator) {
		NotePath childPath = new NotePath(this);
		if (modulator) {
			childPath.add(6);
			childPath.add(index);
		} else if (this.isModulatorPath()) {
			return null;
		} else {
			childPath.removeLast();
			childPath.add(1);
			childPath.add(index);
			childPath.add(0);
		}
		return childPath;
	}
	
	/**
	 * @return the node path for satellites, note path for modulators
	 */
	public NotePath getElementPath() {
		if (!this.isModulatorPath()) {
			return new NotePath(this.subPath(0, this.size()-1));
		}
		return new NotePath(this);
	}
	
	/**
	 * @return the path of the powerset this note is contained in
	 */
	public NotePath getPowersetPath() {
		NotePath powersetPath = this.getElementPath();
		powersetPath.removeLast();
		return powersetPath;
	}
	
	/**
	 * @return the node index for satellites, note index for modulators
	 */
	public int getNoteIndex() {
		NotePath elementPath = this.getElementPath();
		return elementPath.get(elementPath.size()-1);
	}
	
	/**
	 * @return a list of the paths of all parent notes through the hierarchy starting with the direct parent
	 */
	public List<NotePath> getParentPaths() {
		List<NotePath> parentPaths = new ArrayList<NotePath>();
		NotePath currentPath = this.getParentPath();
		while (currentPath != null) {
			parentPaths.add(currentPath);
			currentPath = currentPath.getParentPath();
		}
		return parentPaths;
	}
	
	public NotePath getParentPath() {
		if (this.isModulatorPath()) {
			return this.getCarrierPath();
		}
		return this.getAnchorPath();
	}
	
	public NotePath getChildrenPath() {
		if (this.isModulatorPath()) {
			return this.getModulatorsPath();
		}
		return this.getSatellitesPath();
	}
	
	public NotePath getSatellitesPath() {
		if (!this.isModulatorPath()) {
			return new NotePath(this.subPath(0, this.size()-1), 1);
		}
		return null;
	}
	
	private NotePath getAnchorPath() {
		if (!this.isTopLevelAnchor()) {
			NotePath anchorPath = this.subPath(0, this.size()-3);
			anchorPath.add(0);
			return anchorPath;
		}
		return null;
	}
	
	public NotePath getModulatorsPath() {
		return new NotePath(this, 6);
	}
	
	private NotePath getCarrierPath() {
		if (this.isModulatorPath()) {
			return this.subPath(0, this.size()-2);
		}
		return null;
	}
	
	public boolean isChildOf(NotePath path) {
		if (this.isModulatorPath()) {
			return this.isModulatorOf(path);
		}
		return this.isSatelliteOf(path);
	}
	
	private boolean isSatelliteOf(NotePath path) {
		NotePath anchorPath = this.getAnchorPath();
		return (path == null && anchorPath == null) || (anchorPath != null && anchorPath.equals(path));
	}
	
	private boolean isModulatorOf(NotePath path) {
		NotePath carrierPath = this.getCarrierPath();
		return carrierPath != null && carrierPath.equals(path);
	}
	
	public static List<NotePath> getParentPaths(List<NotePath> notePaths) {
		List<NotePath> parentPaths = new ArrayList<NotePath>();
		for (NotePath currentNotePath: notePaths) {
			parentPaths.add(currentNotePath.getParentPath());
		}
		return parentPaths;
	}
	
	public static List<NotePath> getGrandParentPaths(List<NotePath> notePaths) {
		List<NotePath> grandParentPaths = new ArrayList<NotePath>();
		for (NotePath currentNotePath: notePaths) {
			grandParentPaths.add(currentNotePath.getParentPath().getParentPath());
		}
		return grandParentPaths;
	}
	
	public static int[] getFunctions(List<NotePath> notePaths) {
		int[] functions = new int[notePaths.size()];
		for (int i = 0; i < notePaths.size(); i++) {
			NotePath currentNotePath = notePaths.get(i);
			if (currentNotePath.isModulatorPath()) {
				functions[i] = NotePath.MODULATOR;
			} else {
				functions[i] = NotePath.SATELLITE;
			}
		}
		return functions;
	}

}

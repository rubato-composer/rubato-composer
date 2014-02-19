package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractOperationEdit extends AbstractUndoableEdit {
	
	protected BigBangDenotatorManager denotatorManager;
	protected double modificationRatio;
	protected Double minModRatio, maxModRatio;
	protected boolean isAnimatable;
	protected boolean isSplittable;
	//duration in seconds
	protected double duration;
	
	public AbstractOperationEdit(BigBangDenotatorManager denotatorManager) {
		this.denotatorManager = denotatorManager;
		this.modificationRatio = 1;
		this.isAnimatable = false;
		this.isSplittable = false;
		this.duration = 1;
	}
	
	public BigBangDenotatorManager getDenotatorManager() {
		return this.denotatorManager;
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
	
	protected Set<DenotatorPath> getObjectPaths(Set<BigBangObject> objectList) {
		Set<DenotatorPath> objectPaths = new TreeSet<DenotatorPath>();
		for (BigBangObject currentObject : objectList) {
			DenotatorPath currentPath = currentObject.getTopDenotatorPathAt(this);
			if (currentPath != null) {
				objectPaths.add(currentPath);
			}
		}
		return objectPaths;
	}
	
	@Override
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
	public List<AbstractOperationEdit> getSplitOperations(double ratio) {
		return null;
	}
	
	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	public double getDuration() {
		return this.duration;
	}
	
	/*//TODO eventually move away from here..
	protected List<Map<DenotatorPath,DenotatorPath>> getPathDifferences(List<DenotatorPath> oldPaths, List<DenotatorPath> newPaths) {
		List<Map<DenotatorPath,DenotatorPath>> pathDifferences = new ArrayList<Map<DenotatorPath,DenotatorPath>>();
		if (oldPaths != null) {
			if (oldPaths.size() != newPaths.size()) {
				return pathDifferences;
			}
			pathDifferences.add(new TreeMap<DenotatorPath,DenotatorPath>());
			for (int j = 0; j < newPaths.size(); j++) {
				if (j < oldPaths.size() && j < newPaths.size()) {
					if (!oldPaths.get(j).equals(newPaths.get(j))) {
						pathDifferences.get(0).put(oldPaths.get(j), newPaths.get(j));
					}
				} else if (j < oldPaths.size()) {
					pathDifferences.get(0).put(oldPaths.get(j), null);
				} else {
					pathDifferences.get(0).put(newPaths.get(j), newPaths.get(j));
				}
			}
		}
		return pathDifferences;
	}*/
	
	/*protected Map<DenotatorPath,DenotatorPath> getPathDifferences(SelectedObjectsPaths oldPaths, SelectedObjectsPaths newPaths) {
		Map<DenotatorPath,DenotatorPath> pathDifferences = new TreeMap<DenotatorPath,DenotatorPath>();
		if (oldPaths != null) {
			if (oldPaths.size() != newPaths.size()) {
				return pathDifferences;
			}
			for (int i = 0; i < newPaths.size(); i++) {
				while (pathDifferences.size() <= i) {
					pathDifferences.add(new TreeMap<DenotatorPath,DenotatorPath>());
				}
				List<DenotatorPath> currentObjectOldPaths = oldPaths.get(i);
				List<DenotatorPath> currentObjectNewPaths = newPaths.get(i);
				for (int j = 0; j < currentObjectNewPaths.size(); j++) {
					if (j < currentObjectOldPaths.size() && j < currentObjectNewPaths.size()) {
						if (!currentObjectOldPaths.get(j).equals(currentObjectNewPaths.get(j))) {
							pathDifferences.get(i).put(currentObjectOldPaths.get(j), currentObjectNewPaths.get(j));
						}
					} else if (j < currentObjectOldPaths.size()) {
						pathDifferences.get(i).put(currentObjectOldPaths.get(j), null);
					} else {
						pathDifferences.get(i).put(currentObjectNewPaths.get(j), currentObjectNewPaths.get(j));
					}
				}
			}
		}
		return pathDifferences;
	}*/
	
	/*TODO eventually move away from here..
	protected List<Map<DenotatorPath,DenotatorPath>> getPathDifferences(SelectedObjectsPaths oldPaths, SelectedObjectsPaths newPaths) {
		List<Map<DenotatorPath,DenotatorPath>> pathDifferences = new ArrayList<Map<DenotatorPath,DenotatorPath>>();
		if (oldPaths != null) {
			if (oldPaths.size() != newPaths.size()) {
				return pathDifferences;
			}
			for (int i = 0; i < newPaths.size(); i++) {
				while (pathDifferences.size() <= i) {
					pathDifferences.add(new TreeMap<DenotatorPath,DenotatorPath>());
				}
				List<DenotatorPath> currentObjectOldPaths = oldPaths.get(i);
				List<DenotatorPath> currentObjectNewPaths = newPaths.get(i);
				for (int j = 0; j < currentObjectNewPaths.size(); j++) {
					if (j < currentObjectOldPaths.size() && j < currentObjectNewPaths.size()) {
						if (!currentObjectOldPaths.get(j).equals(currentObjectNewPaths.get(j))) {
							pathDifferences.get(i).put(currentObjectOldPaths.get(j), currentObjectNewPaths.get(j));
						}
					} else if (j < currentObjectOldPaths.size()) {
						pathDifferences.get(i).put(currentObjectOldPaths.get(j), null);
					} else {
						pathDifferences.get(i).put(currentObjectNewPaths.get(j), currentObjectNewPaths.get(j));
					}
				}
			}
		}
		return pathDifferences;
	}*/

}

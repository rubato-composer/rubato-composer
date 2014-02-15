package org.rubato.rubettes.bigbang.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.TreeBidiMap;
import org.rubato.rubettes.util.DenotatorPath;

public class OperationPathResults {
	
	private Set<DenotatorPath> newPaths;
	private Set<DenotatorPath> removedPaths;
	private BidiMap<DenotatorPath, DenotatorPath> changedPaths;
	private Set<DenotatorPath> pathsToBeSelected;
	
	public OperationPathResults() {
		this.newPaths = new TreeSet<DenotatorPath>();
		this.removedPaths = new TreeSet<DenotatorPath>();
		this.changedPaths = new TreeBidiMap<DenotatorPath,DenotatorPath>();
		this.pathsToBeSelected = new TreeSet<DenotatorPath>();
	}
	
	public OperationPathResults(Set<DenotatorPath> pathsToBeSelected) {
		this();
		this.pathsToBeSelected = pathsToBeSelected;
	}
	
	public OperationPathResults(Set<DenotatorPath> newPaths, Set<DenotatorPath> pathsToBeSelected) {
		this(pathsToBeSelected);
		if (newPaths != null) {
			this.newPaths = newPaths;
		}
	}
	
	public OperationPathResults(Map<DenotatorPath, DenotatorPath> changedPaths) {
		this();
		this.changedPaths = new TreeBidiMap<DenotatorPath,DenotatorPath>(changedPaths);
	}
	
	public OperationPathResults(Set<DenotatorPath> newPaths, Set<DenotatorPath> removedPaths, Map<DenotatorPath, DenotatorPath> changedPaths) {
		this(changedPaths);
		if (newPaths != null) {
			this.newPaths = newPaths;
		}
		if (removedPaths != null) {
			this.removedPaths = removedPaths;
		}
	}
	
	public void addPaths(OperationPathResults paths) {
		this.newPaths.addAll(paths.getNewPaths());
		this.changedPaths.putAll(paths.getChangedPaths());
		this.pathsToBeSelected.addAll(paths.getPathsToBeSelected());
	}
	
	public void updatePaths(OperationPathResults pathResults) {
		this.updatePaths(pathResults.getChangedPaths(), pathResults.getNewPaths(), null);
	}
	
	public void updatePaths(List<DenotatorPath> previousPaths, List<DenotatorPath> currentPaths, Collection<DenotatorPath> newPaths) {
		Map<DenotatorPath,DenotatorPath> changedPaths = new TreeMap<DenotatorPath,DenotatorPath>();
		Set<DenotatorPath> removedPaths = new TreeSet<DenotatorPath>();
		for (int i = previousPaths.size()-1; i >= 0; i--) {
			DenotatorPath keyPath = previousPaths.get(i);
			DenotatorPath valuePath = currentPaths.get(i);
			if (valuePath != null) {
				changedPaths.put(keyPath, valuePath);
			} else {
				removedPaths.add(keyPath);
			}
		}
		this.updatePaths(changedPaths, newPaths, removedPaths);
	}
	
	private void updatePaths(Map<DenotatorPath,DenotatorPath> changedPaths, Collection<DenotatorPath> newPaths, Set<DenotatorPath> removedPaths) {
		//need to build new sets and maps separately so that no values are changed back and forth, e.g. in cases
		//of two paths being switched
		Set<DenotatorPath> newNewPaths = new TreeSet<DenotatorPath>();
		BidiMap<DenotatorPath,DenotatorPath> newChangedPaths = new TreeBidiMap<DenotatorPath,DenotatorPath>(this.changedPaths);
		
		for (DenotatorPath keyPath : changedPaths.keySet()) {
			DenotatorPath valuePath = changedPaths.get(keyPath);
			
			//move changed newPaths to newNewPaths
			if (this.newPaths.contains(keyPath)) {
				this.newPaths.remove(keyPath);
				newNewPaths.add(valuePath);
			//adjust changedPaths
			} else if (this.changedPaths.containsValue(keyPath)) {
				DenotatorPath oldKey = this.changedPaths.getKey(keyPath);
				if (!oldKey.equals(valuePath)) {
					//adjust previous changed path
					newChangedPaths.put(oldKey, valuePath);
				} else {
					//remove if results in x=x
					newChangedPaths.remove(oldKey);
				}
			} else if (!keyPath.equals(valuePath)){
				newChangedPaths.put(keyPath, valuePath);
			}
		}
		//add unchanged newPaths to newNewPaths
		newNewPaths.addAll(this.newPaths);
		this.newPaths = newNewPaths;
		this.changedPaths = newChangedPaths;
		
		//only add new and removed paths after changes made!!
		if (newPaths != null) {
			this.newPaths.addAll(newPaths);
			//this.lastNewPaths.addAll(newPaths);
		}
		if (removedPaths != null) {
			this.removedPaths.addAll(removedPaths);
		}
	}
	
	public void setNewPaths(Set<DenotatorPath> newPaths) {
		this.newPaths = newPaths;
	}
	
	public Set<DenotatorPath> getNewPaths() {
		return this.newPaths;
	}
	
	public Set<DenotatorPath> getRemovedPaths() {
		return this.removedPaths;
	}
	
	public Map<DenotatorPath, DenotatorPath> getChangedPaths() {
		return this.changedPaths;
	}
	
	public Set<DenotatorPath> getPathsToBeSelected() {
		return this.pathsToBeSelected;
	}
	
	public String toString() {
		return this.newPaths + " " + this.changedPaths + " " + this.removedPaths;
	}

}

package org.rubato.rubettes.bigbang.view.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.rubato.rubettes.util.DenotatorPath;

public class SelectedObjectsPaths {
	
	private List<Set<DenotatorPath>> objectPaths;
	private DenotatorPath anchorPath;
	
	public SelectedObjectsPaths(List<Set<DenotatorPath>> objectPaths, DenotatorPath anchorPath) {
		this.objectPaths = objectPaths;
		this.anchorPath = anchorPath;
	}
	
	@SuppressWarnings("unchecked")
	public SelectedObjectsPaths(Set<DenotatorPath> objectPaths, DenotatorPath anchorPath) {
		this.objectPaths = Arrays.asList(objectPaths);
		this.anchorPath = anchorPath;
	}
	
	public List<Set<DenotatorPath>> getObjectPaths() {
		return this.objectPaths;
	}
	
	public DenotatorPath getAnchorPath() {
		return this.anchorPath;
	}
	
	public void setAnchorPath(DenotatorPath anchorPath) {
		this.anchorPath = anchorPath;
	}
	
	public int size() {
		int size = 0;
		for (Set<DenotatorPath> currentPaths : this.objectPaths) {
			size += currentPaths.size();
		}
		return size;
	}

}

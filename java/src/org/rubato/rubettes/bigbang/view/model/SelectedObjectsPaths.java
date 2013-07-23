package org.rubato.rubettes.bigbang.view.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.rubato.rubettes.util.DenotatorPath;

public class SelectedObjectsPaths {
	
	private List<List<DenotatorPath>> objectPaths;
	private DenotatorPath anchorPath;
	
	public SelectedObjectsPaths(List<List<DenotatorPath>> objectPaths, DenotatorPath anchorPath) {
		this.objectPaths = objectPaths;
		this.anchorPath = anchorPath;
	}
	
	@SuppressWarnings("unchecked")
	public SelectedObjectsPaths(Collection<DenotatorPath> objectPaths, DenotatorPath anchorPath) {
		if (objectPaths != null) {
			List<DenotatorPath> list = new ArrayList<DenotatorPath>(objectPaths);
			this.objectPaths = Arrays.asList(list);
		}
		this.anchorPath = anchorPath;
	}
	
	public boolean containsObjectPath(DenotatorPath path) {
		if (this.objectPaths != null) {
			for (List<DenotatorPath> currentSet : this.objectPaths) {
				if (currentSet.contains(path)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<DenotatorPath> get(int objectIndex) {
		return this.objectPaths.get(objectIndex);
	}
	
	public DenotatorPath getAnchorPath() {
		return this.anchorPath;
	}
	
	public void setAnchorPath(DenotatorPath anchorPath) {
		this.anchorPath = anchorPath;
	}
	
	public int size() {
		return this.objectPaths.size();
	}
	
	public int totalObjectPaths() {
		int size = 0;
		for (List<DenotatorPath> currentPaths : this.objectPaths) {
			size += currentPaths.size();
		}
		return size;
	}

}

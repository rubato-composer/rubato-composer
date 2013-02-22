package org.rubato.rubettes.bigbang.view.model;

import java.util.Set;

import org.rubato.rubettes.util.DenotatorPath;

public class SelectedPaths {
	
	private Set<DenotatorPath> nodePaths;
	private DenotatorPath anchorPath;
	
	public SelectedPaths(Set<DenotatorPath> nodePaths, DenotatorPath anchorPath) {
		this.nodePaths = nodePaths;
		this.anchorPath = anchorPath;
	}
	
	public Set<DenotatorPath> getNodePaths() {
		return this.nodePaths;
	}
	
	public DenotatorPath getAnchorPath() {
		return this.anchorPath;
	}

}

package org.rubato.rubettes.bigbang.view.model;

import java.util.Set;

import org.rubato.rubettes.util.NotePath;

public class SelectedPaths {
	
	private Set<NotePath> nodePaths;
	private NotePath anchorPath;
	
	public SelectedPaths(Set<NotePath> nodePaths, NotePath anchorPath) {
		this.nodePaths = nodePaths;
		this.anchorPath = anchorPath;
	}
	
	public Set<NotePath> getNodePaths() {
		return this.nodePaths;
	}
	
	public NotePath getAnchorPath() {
		return this.anchorPath;
	}

}

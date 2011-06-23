package org.rubato.rubettes.bigbang.model;

import java.util.Set;

import org.rubato.rubettes.util.NotePath;

public class TransformationProperties {
	
	private Set<NotePath> nodePaths;
	private NotePath anchorNodePath;
	private int[][] elementPaths;
	private boolean copyAndTransform;
	private double[] center;
	private double[] distance;
	private boolean inPreviewMode;
	private boolean inWallpaperMode;
	
	public TransformationProperties(Set<NotePath> nodePaths, int[][] elementPaths, boolean copyAndTransform, boolean inPreviewMode, boolean inWallpaperMode) {
		this.nodePaths = nodePaths;
		this.elementPaths = elementPaths;
		this.copyAndTransform = copyAndTransform;
		this.inPreviewMode = inPreviewMode;
		this.inWallpaperMode = inWallpaperMode;
	}
	
	public void setNodePaths(Set<NotePath> nodePaths) {
		this.nodePaths = nodePaths;
	}
	
	public Set<NotePath> getNodePaths() {
		return this.nodePaths;
	}
	
	public int[][] getElementPaths() {
		return this.elementPaths;
	}
	
	public boolean copyAndTransform() {
		return this.copyAndTransform;
	}
	
	public void setCenter(double[] center) {
		this.center = center;
	}
	
	public double[] getCenter() {
		return this.center;
	}
	
	/*
	 * set the distance traveled by the mouse when the transformation was defined
	 */
	public void setDistance(double[] distance) {
		this.distance = distance;
	}
	
	public double[] getDistance() {
		return this.distance;
	}
	
	public void setAnchorNodePath(NotePath anchorNodePath) {
		this.anchorNodePath = anchorNodePath;
	}
	
	public NotePath getAnchorNodePath() {
		return this.anchorNodePath;
	}
	
	public boolean inPreviewMode() {
		return this.inPreviewMode;
	}
	
	public boolean inWallpaperMode() {
		return this.inWallpaperMode;
	}

}

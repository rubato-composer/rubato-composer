package org.rubato.rubettes.bigbang.model;

import java.util.Map;
import java.util.Set;

import org.rubato.rubettes.util.DenotatorPath;

public class TransformationProperties {
	
	private Set<DenotatorPath> objectPaths;
	private DenotatorPath anchorNodePath;
	private TransformationPaths transformationPaths;
	private boolean copyAndTransform;
	private double[] center;
	private double[] endPoint;
	private boolean inPreviewMode;
	private boolean inWallpaperMode;
	
	public TransformationProperties(Set<DenotatorPath> nodePaths, TransformationPaths transformationPaths, boolean copyAndTransform, boolean inPreviewMode, boolean inWallpaperMode) {
		this.objectPaths = nodePaths;
		this.transformationPaths = transformationPaths;
		this.copyAndTransform = copyAndTransform;
		this.inPreviewMode = inPreviewMode;
		this.inWallpaperMode = inWallpaperMode;
	}
	
	public void setNodePaths(Set<DenotatorPath> nodePaths) {
		this.objectPaths = nodePaths;
	}
	
	public void updateNodePaths(Map<DenotatorPath,DenotatorPath> pathDifferences) {
		for (DenotatorPath currentPath : pathDifferences.keySet()) {
			if (this.objectPaths.contains(currentPath)) {
				DenotatorPath newPath = pathDifferences.get(currentPath);
				this.objectPaths.add(newPath);
				this.objectPaths.remove(currentPath);
			}
		}
	}
	
	public Set<DenotatorPath> getObjectPaths() {
		return this.objectPaths;
	}
	
	public TransformationPaths getTransformationPaths() {
		return this.transformationPaths;
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
	 * set the end point reached by the mouse movement with which the transformation was defined
	 */
	public void setEndPoint(double[] endPoint) {
		this.endPoint = endPoint;
	}
	
	public double[] getEndPoint() {
		return this.endPoint;
	}
	
	public void setAnchorNodePath(DenotatorPath anchorNodePath) {
		this.anchorNodePath = anchorNodePath;
	}
	
	public DenotatorPath getAnchorNodePath() {
		return this.anchorNodePath;
	}
	
	public void setInPreviewMode(boolean inPreviewMode) {
		this.inPreviewMode = inPreviewMode;
	}
	
	public boolean inPreviewMode() {
		return this.inPreviewMode;
	}
	
	public boolean inWallpaperMode() {
		return this.inWallpaperMode;
	}

}

package org.rubato.rubettes.bigbang.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class TransformationProperties {
	
	private SelectedObjectsPaths objectPaths;
	private TransformationPaths transformationPaths;
	private boolean copyAndTransform;
	private double[] center;
	private double[] endPoint;
	private boolean inPreviewMode;
	private boolean inWallpaperMode;
	
	public TransformationProperties(SelectedObjectsPaths objectPaths, TransformationPaths transformationPaths, boolean copyAndTransform, boolean inPreviewMode, boolean inWallpaperMode) {
		this.objectPaths = objectPaths;
		this.transformationPaths = transformationPaths;
		this.copyAndTransform = copyAndTransform;
		this.inPreviewMode = inPreviewMode;
		this.inWallpaperMode = inWallpaperMode;
	}
	
	public void updateObjectPaths(List<Map<DenotatorPath,DenotatorPath>> pathDifferences) {
		for (int i = 0; i < pathDifferences.size(); i++) {
			Map<DenotatorPath,DenotatorPath> currentObjectDifferences = pathDifferences.get(i);
			Set<DenotatorPath> currentObjectPaths = this.objectPaths.getObjectPaths().get(i);
			for (DenotatorPath currentPath : currentObjectDifferences.keySet()) {
				if (currentObjectPaths.contains(currentPath)) {
					DenotatorPath newPath = currentObjectDifferences.get(currentPath);
					currentObjectPaths.add(newPath);
					currentObjectPaths.remove(currentPath);
				}
			}
		}
	}
	
	public int getNumberOfObjectTypes() {
		return this.objectPaths.size();
	}
	
	public SelectedObjectsPaths getObjectsPaths() {
		return this.objectPaths;
	}
	
	public Set<DenotatorPath> getObjectPathsAt(int objectTypeIndex) {
		return this.objectPaths.getObjectPaths().get(objectTypeIndex);
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
	
	public void setAnchorNodePath(DenotatorPath anchorPath) {
		this.objectPaths.setAnchorPath(anchorPath);
	}
	
	public DenotatorPath getAnchorNodePath() {
		return this.objectPaths.getAnchorPath();
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

package org.rubato.rubettes.bigbang.model;

import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class TransformationProperties {
	
	private SelectedObjectsPaths objectPaths;
	private List<TransformationPaths> transformationPaths;
	private boolean copyAndTransform;
	private double[] center;
	private double[] endPoint;
	private boolean inPreviewMode;
	
	public TransformationProperties(SelectedObjectsPaths objectPaths, List<TransformationPaths> transformationPaths, boolean copyAndTransform, boolean inPreviewMode) {
		this.objectPaths = objectPaths;
		this.transformationPaths = transformationPaths;
		this.copyAndTransform = copyAndTransform;
		this.inPreviewMode = inPreviewMode;
	}
	
	public void updateObjectPaths(List<Map<DenotatorPath,DenotatorPath>> pathDifferences) {
		for (int i = 0; i < pathDifferences.size(); i++) {
			Map<DenotatorPath,DenotatorPath> currentObjectDifferences = pathDifferences.get(i);
			List<DenotatorPath> currentObjectPaths = this.objectPaths.get(i);
			for (DenotatorPath currentPath : currentObjectDifferences.keySet()) {
				if (currentObjectPaths.contains(currentPath)) {
					DenotatorPath newPath = currentObjectDifferences.get(currentPath);
					currentObjectPaths.add(newPath);
					currentObjectPaths.remove(currentPath);
				}
			}
		}
	}
	
	public SelectedObjectsPaths getObjectsPaths() {
		return this.objectPaths;
	}
	
	public List<TransformationPaths> getTransformationPaths() {
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
	
	public TransformationProperties clone() {
		TransformationProperties clone = new TransformationProperties(this.objectPaths, this.transformationPaths, this.copyAndTransform, this.inPreviewMode);
		clone.center = this.center;
		clone.endPoint = this.endPoint;
		return clone;
	}

}

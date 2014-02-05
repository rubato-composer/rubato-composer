package org.rubato.rubettes.bigbang.model;

import java.util.List;
import java.util.Set;

public class TransformationProperties {
	
	private Set<BigBangObject> objects;
	private BigBangObject anchor;
	private List<TransformationPaths> transformationPaths;
	private boolean copyAndTransform;
	private double[] center;
	private double[] endPoint;
	private boolean startNewTransformation;
	
	public TransformationProperties(Set<BigBangObject> objects, BigBangObject anchor, List<TransformationPaths> transformationPaths, boolean copyAndTransform, boolean startNewTransformation) {
		this.objects = objects;
		this.anchor = anchor;
		this.transformationPaths = transformationPaths;
		this.copyAndTransform = copyAndTransform;
		this.startNewTransformation = startNewTransformation;
	}
	
	public Set<BigBangObject> getObjects() {
		return this.objects;
	}
	
	public BigBangObject getAnchor() {
		return this.anchor;
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
	
	public void setAnchor(BigBangObject anchor) {
		this.anchor = anchor;
	}
	
	public void setStartNewTransformation(boolean startNewTransformation) {
		this.startNewTransformation = startNewTransformation;
	}
	
	public boolean startNewTransformation() {
		return this.startNewTransformation;
	}
	
	public TransformationProperties clone() {
		TransformationProperties clone = new TransformationProperties(this.objects, this.anchor, this.transformationPaths, this.copyAndTransform, this.startNewTransformation);
		clone.center = this.center;
		clone.endPoint = this.endPoint;
		return clone;
	}

}

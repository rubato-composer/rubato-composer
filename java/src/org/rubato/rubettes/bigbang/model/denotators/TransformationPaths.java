package org.rubato.rubettes.bigbang.model.denotators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rubato.base.RubatoException;
import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.util.DenotatorPath;

public class TransformationPaths {
	
	//indices of coordinates within the main coordinate system values
	private int[] xyCoordinates;
	//first list for dimensions, second for colimits
	private List<List<DenotatorPath>> domainPaths, codomainPaths;
	
	public TransformationPaths() {
		this.domainPaths = new ArrayList<List<DenotatorPath>>();
		this.codomainPaths = new ArrayList<List<DenotatorPath>>();
	}
	
	public void setXYCoordinates(int[] selectedCoordinates) {
		this.xyCoordinates = selectedCoordinates;
	}
	
	public int[] getXYCoordinates() {
		return this.xyCoordinates;
	}
	
	public void addSinglePathToNewDomainDimension(DenotatorPath path) {
		this.domainPaths.add(Arrays.asList(path));
	}
	
	public void addSinglePathToNewCodomainDimension(DenotatorPath path) {
		this.codomainPaths.add(Arrays.asList(path));
	}
	
	public void setDomainPaths(int dimensionIndex, List<DenotatorPath> paths) {
		//init all dimensions up to the one here in case they aren't yet
		for (int i = this.domainPaths.size(); i <= dimensionIndex; i++) {
			this.domainPaths.add(new ArrayList<DenotatorPath>());
		}
		this.domainPaths.set(dimensionIndex, paths);
	}
	
	public void setCodomainPaths(int dimensionIndex, List<DenotatorPath> paths) {
		//init all dimensions up to the one here in case they aren't yet
		for (int i = this.codomainPaths.size(); i <= dimensionIndex; i++) {
			this.codomainPaths.add(new ArrayList<DenotatorPath>());
		}
		this.codomainPaths.set(dimensionIndex, paths);
	}
	
	/**
	 * @return the first path of the given dimension of the domain that is appropriate for the given denotator,
	 * null if there is none or the dimension is not present
	 */
	public DenotatorPath getDomainPath(int dimensionIndex, Denotator denotator) {
		return this.getAppropriatePath(this.domainPaths, dimensionIndex, denotator);
	}
	
	/**
	 * @return the first path of the given dimension of the codomain that is appropriate for the given denotator,
	 * null if there is none or the dimension is not present
	 */
	public DenotatorPath getCodomainPath(int dimensionIndex, Denotator denotator) {
		return this.getAppropriatePath(this.codomainPaths, dimensionIndex, denotator);
	}
	
	
	private DenotatorPath getAppropriatePath(List<List<DenotatorPath>> paths, int dimensionIndex, Denotator denotator) {
		if (paths.get(dimensionIndex) != null) { //dimension may not be present
			for (DenotatorPath currentPath : paths.get(dimensionIndex)) {
				try {
					if (currentPath.isElementPath() && denotator.getElement(currentPath.toIntArray()) != null) {
						return currentPath;
					} else if (denotator.get(currentPath.toIntArray()) != null) {
						return currentPath;
					}
				//do nothing if path leads to error, just try next one
				} catch (RubatoException e) { }
			}
		}
		return null;
	}
	
	/*public List<DenotatorPath> getDomainPaths(int dimensionIndex) {
		return this.domainPaths.get(dimensionIndex);
	}
	
	public List<DenotatorPath> getCodomainPaths(int dimensionIndex) {
		return this.codomainPaths.get(dimensionIndex);
	}*/
	
	public int getDomainDim() {
		return this.domainPaths.size();
	}
	
	public int getCodomainDim() {
		return this.codomainPaths.size();
	}
	
	public String toString() {
		return this.domainPaths.toString() + " " + this.codomainPaths.toString();
	}
	
	public boolean equals(Object object) {
		if (object instanceof TransformationPaths) {
			TransformationPaths other = (TransformationPaths)object;
			return Arrays.equals(this.xyCoordinates, other.xyCoordinates);
		}
		return false;
	}

}

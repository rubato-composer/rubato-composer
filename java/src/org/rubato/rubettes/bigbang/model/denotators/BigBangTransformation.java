package org.rubato.rubettes.bigbang.model.denotators;

import java.util.List;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangTransformation {
	
	private ModuleMorphism morphism; 
	private List<TransformationPaths> transformationPaths;
	private boolean copyAndMap; 
	private DenotatorPath anchorNodePath;
	
	public BigBangTransformation(ModuleMorphism morphism, List<TransformationPaths> transformationPaths, boolean copyAndMap, DenotatorPath anchorNodePath) {
		this.morphism = morphism;
		this.transformationPaths = transformationPaths;
		this.copyAndMap = copyAndMap;
		this.anchorNodePath = anchorNodePath;
	}
	
	public ModuleMorphism getModuleMorphism() {
		return this.morphism;
	}
	
	public List<TransformationPaths> getTransformationPaths() {
		return this.transformationPaths;
	}
	
	public void setCopyAndMap(boolean copyAndMap) {
		this.copyAndMap = copyAndMap;
	}
	
	public boolean isCopyAndMap() {
		return this.copyAndMap;
	}
	
	public DenotatorPath getAnchorNodePath() {
		return this.anchorNodePath;
	}
	
	public BigBangTransformation inverse() {
		RFreeAffineMorphism morphism = (RFreeAffineMorphism)this.morphism;
		RMatrix inverseMatrix = morphism.getMatrix().inverse();
		double[] shift = morphism.getVector();
		double[] inverseShift = inverseMatrix.product(shift);
		inverseShift = new double[]{-1*inverseShift[0], -1*inverseShift[1]}; 
		ModuleMorphism inverseMorphism = RFreeAffineMorphism.make(inverseMatrix, inverseShift);
		return new BigBangTransformation(inverseMorphism, this.transformationPaths, this.copyAndMap, this.anchorNodePath);
	}
	
	public String toString() {
		return this.morphism.toString();
	}

}

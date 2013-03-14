package org.rubato.rubettes.bigbang.model;

import java.util.List;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangTransformation {
	
	private ModuleMorphism morphism; 
	private List<DenotatorPath> valuePaths;
	private boolean copyAndMap; 
	private DenotatorPath anchorNodePath;
	
	public BigBangTransformation(ModuleMorphism morphism, List<DenotatorPath> valuePaths, boolean copyAndMap, DenotatorPath anchorNodePath) {
		this.morphism = morphism;
		this.valuePaths = valuePaths;
		this.copyAndMap = copyAndMap;
		this.anchorNodePath = anchorNodePath;
	}
	
	public ModuleMorphism getModuleMorphism() {
		return this.morphism;
	}
	
	public List<DenotatorPath> getValuePaths() {
		return this.valuePaths;
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
		return new BigBangTransformation(inverseMorphism, this.valuePaths, this.copyAndMap, this.anchorNodePath);
	}

}

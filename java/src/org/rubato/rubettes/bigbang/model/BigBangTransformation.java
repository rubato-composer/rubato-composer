package org.rubato.rubettes.bigbang.model;

import org.rubato.math.matrix.RMatrix;
import org.rubato.math.module.morphism.ModuleMorphism;
import org.rubato.math.module.morphism.RFreeAffineMorphism;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangTransformation {
	
	private ModuleMorphism morphism; 
	private int[][] coordinatePaths;
	private boolean copyAndMap; 
	private DenotatorPath anchorNodePath;
	
	public BigBangTransformation(ModuleMorphism morphism, int[][] coordinatePaths, boolean copyAndMap, DenotatorPath anchorNodePath) {
		this.morphism = morphism;
		this.coordinatePaths = coordinatePaths;
		this.copyAndMap = copyAndMap;
		this.anchorNodePath = anchorNodePath;
	}
	
	public ModuleMorphism getModuleMorphism() {
		return this.morphism;
	}
	
	public int[][] getCoordinatePaths() {
		return this.coordinatePaths;
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
		return new BigBangTransformation(inverseMorphism, this.coordinatePaths, this.copyAndMap, this.anchorNodePath);
	}

}

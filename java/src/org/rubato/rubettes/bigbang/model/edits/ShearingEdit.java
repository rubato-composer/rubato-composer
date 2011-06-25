package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ShearingEdit extends AbstractLocalTransformationEdit {
	
	private double[] shearingFactors;
	
	public ShearingEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] shearingFactors) {
		super(scoreLayers, properties);
		this.shearingFactors = shearingFactors;
		this.execute();
	}
	
	public String getPresentationName() {
		return "Shearing";
	}

	@Override
	protected RMatrix getMatrix() {
		double sx = this.shearingFactors[0];
		double sy = this.shearingFactors[1];
		return new RMatrix(new double[][]{{1,sx},{sy,1}});
	}
	
	public double[] getShearingFactors() {
		return this.shearingFactors;
	}
	
}

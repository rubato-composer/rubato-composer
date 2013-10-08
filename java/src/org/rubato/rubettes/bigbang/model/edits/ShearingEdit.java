package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ShearingEdit extends AbstractLocalTransformationEdit {
	
	private double[] shearingFactors;
	
	public ShearingEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] shearingFactors) {
		super(scoreLayers, properties);
		this.shearingFactors = shearingFactors;
		this.updateOperation();
	}
	
	public void modify(double[] newValues) {
		this.shearingFactors = newValues;
		this.updateOperation();
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Shearing " + super.round(this.getShearingFactors()[0]) + ", " + super.round(this.getShearingFactors()[1]);
	}

	@Override
	protected RMatrix getMatrix() {
		double[] modifiedFactors = this.getShearingFactors();
		double sx = modifiedFactors[0];
		double sy = modifiedFactors[1];
		return new RMatrix(new double[][]{{1,sx},{sy,1}});
	}
	
	public double[] getShearingFactors() {
		return new double[]{this.modificationRatio*this.shearingFactors[0],this.modificationRatio*this.shearingFactors[1]};
	}
	
}

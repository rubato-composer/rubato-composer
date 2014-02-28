package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ShearingEdit extends AbstractLocalTransformationEdit {
	
	private double[] shearingFactors;
	
	//used for cloning
	protected ShearingEdit(BigBangModel model) {
		super(model);
	}
	
	public ShearingEdit(BigBangModel model, TransformationProperties properties, double[] shearingFactors) {
		super(model, properties);
		this.modify(shearingFactors);
	}
	
	public void modify(double[] newShearingFactors) {
		this.shearingFactors = newShearingFactors;
		this.updateOperation();
	}
	
	//creates a copy of this with the same center and scaleFactors adjusted by the given ratio
	protected ShearingEdit createModifiedCopy(double ratio) {
		ShearingEdit modifiedCopy = (ShearingEdit)this.clone();
		double[] partialShearing = new double[]{this.shearingFactors[0]*ratio, this.shearingFactors[1]*ratio};
		modifiedCopy.modify(partialShearing);
		return modifiedCopy;
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

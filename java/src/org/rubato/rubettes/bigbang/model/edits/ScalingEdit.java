package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ScalingEdit extends AbstractLocalTransformationEdit {
	
	private double[] scaleFactors;
	
	protected ScalingEdit(BigBangModel model) {
		super(model);
	}
	
	public ScalingEdit(BigBangModel model, TransformationProperties properties, double[] scaleFactors) {
		super(model, properties);
		this.modify(scaleFactors);
	}
	
	@Override
	public void modify(double[] newScaleFactors) {
		this.scaleFactors = newScaleFactors;
		this.updateOperation();
	}
	
	//creates a copy of this with the same center and scaleFactors adjusted by the given ratio
	protected ScalingEdit createModifiedCopy(double ratio) {
		ScalingEdit modifiedCopy = (ScalingEdit)this.clone();
		double[] partialScaling = new double[]{this.getModifiedScaleFactor(this.scaleFactors[0], ratio),
				this.getModifiedScaleFactor(this.scaleFactors[1], ratio)};
		modifiedCopy.modify(partialScaling);
		return modifiedCopy;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Scaling " + super.round(this.getScaleFactors()[0]) + ", " + super.round(this.getScaleFactors()[1]);
	}

	@Override
	protected RMatrix getMatrix() {
		double[] modifiedFactors = this.getScaleFactors();
		double sx = modifiedFactors[0];
		double sy = modifiedFactors[1];
		return new RMatrix(new double[][]{{sx,0},{0,sy}});
	}
	
	public double[] getScaleFactors() {
		return new double[]{this.getModifiedScaleFactor(this.scaleFactors[0], this.modificationRatio),
				this.getModifiedScaleFactor(this.scaleFactors[1], this.modificationRatio)};
	}
	
	private double getModifiedScaleFactor(double scaleFactor, double ratio) {
		return 1+(ratio*(scaleFactor-1));
	}

}
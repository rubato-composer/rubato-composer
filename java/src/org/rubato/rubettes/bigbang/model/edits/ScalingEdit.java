package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ScalingEdit extends AbstractLocalTransformationEdit {
	
	private double[] scaleFactors;
	
	public ScalingEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] scaleFactors) {
		super(scoreLayers, properties);
		this.scaleFactors = scaleFactors;
		this.updateOperation();
	}
	
	@Override
	public void modify(double[] newValues) {
		this.scaleFactors = newValues;
		this.updateOperation();
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
		return new double[]{this.getModifiedScaleFactor(this.scaleFactors[0]),this.getModifiedScaleFactor(this.scaleFactors[1])};
	}
	
	private double getModifiedScaleFactor(double scaleFactor) {
		return 1+(this.modificationRatio*(scaleFactor-1));
	}

}
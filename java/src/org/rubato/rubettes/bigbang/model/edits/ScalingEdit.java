package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ScalingEdit extends AbstractLocalTransformationEdit {
	
	private double[] scaleFactors;
	
	public ScalingEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] scaleFactors) {
		super(scoreLayers, properties);
		this.scaleFactors = scaleFactors;
		this.initTransformation();
	}
	
	public void modify(double[] newValues) {
		this.scaleFactors = newValues;
		this.initTransformation();
	}
	
	public String getPresentationName() {
		return "Scaling " + super.getPresentationName();
	}

	@Override
	protected RMatrix getMatrix() {
		double sx = this.scaleFactors[0];
		double sy = this.scaleFactors[1];
		return new RMatrix(new double[][]{{sx,0},{0,sy}});
	}
	
	public double[] getScaleFactors() {
		return this.scaleFactors;
	}

}
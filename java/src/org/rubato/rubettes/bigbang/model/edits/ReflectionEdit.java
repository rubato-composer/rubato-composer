package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ReflectionEdit extends AbstractLocalTransformationEdit {
	
	private double[] reflectionVector;
	
	public ReflectionEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] reflectionVector) {
		super(scoreLayers, properties);
		this.reflectionVector = reflectionVector;
		this.updateOperation();
	}
	
	public void modify(double[] newValues) {
		this.reflectionVector = newValues;
		this.updateOperation();
	}
	
	public String getPresentationName() {
		return "Reflection " + super.getPresentationName();
	}

	@Override
	protected RMatrix getMatrix() {
		double[] modifiedVector = this.getReflectionVector();
		double x = modifiedVector[0];
		double y = modifiedVector[1];
		double x2 = Math.pow(x, 2);
		double y2 = Math.pow(y, 2);
		double q = x2 + y2;
		double m11 = (x2 - y2) / q;
		double m12 = (2*x*y)/q;
		double m22 = (y2 - x2)/q;
		return new RMatrix(new double[][]{{m11, m12}, {m12, m22}});
	}
	
	public double[] getReflectionVector() {
		return new double[]{this.modificationRatio*this.reflectionVector[0],this.modificationRatio*this.reflectionVector[1]};
	}

}

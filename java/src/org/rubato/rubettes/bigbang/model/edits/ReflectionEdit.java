package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ReflectionEdit extends AbstractLocalTransformationEdit {
	
	private double[] reflectionVector;
	
	public ReflectionEdit(BigBangModel model, TransformationProperties properties, double[] reflectionVector) {
		super(model, properties);
		this.modify(reflectionVector);
		this.isSplittable = false;
	}
	
	public void modify(double[] newValues) {
		this.reflectionVector = newValues;
		this.updateOperation();
	}
	
	//does not work (yet) for this! 
	protected ReflectionEdit createModifiedCopy(double ratio) {
		return this;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Reflection " + super.round(this.reflectionVector[0]) + ", " + super.round(this.reflectionVector[1]);
	}

	@Override
	protected RMatrix getMatrix() {
		double x = this.reflectionVector[0];
		double y = this.reflectionVector[1];
		double x2 = Math.pow(x, 2);
		double y2 = Math.pow(y, 2);
		double q = x2 + y2;
		double m11 = (this.modificationRatio*(x2 - y2)/q)+(1-this.modificationRatio);
		double m12 = this.modificationRatio*(2*x*y)/q;
		double m22 = (this.modificationRatio*(y2 - x2)/q)+(1-this.modificationRatio);
		return new RMatrix(new double[][]{{m11, m12}, {m12, m22}});
	}
	
	public double[] getReflectionVector() {
		return this.reflectionVector;
	}

}

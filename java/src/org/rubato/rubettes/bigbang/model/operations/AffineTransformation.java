package org.rubato.rubettes.bigbang.model.operations;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;

public class AffineTransformation extends AbstractLocalTransformation {
	
	private double[] shift;
	private RMatrix transform;
	
	//used for cloning
	protected AffineTransformation(BigBangModel model) {
		super(model);
	}
	
	public AffineTransformation(BigBangModel model, TransformationProperties properties, double[] shift, RMatrix transform2x2) {
		super(model, properties);
		//System.out.println(properties.getCenter()[0] + " " + properties.getCenter()[1]);
		this.setParameters(shift, transform2x2);
	}
	
	private void setParameters(double[] shift, RMatrix transform2x2) {
		this.shift = shift;
		this.transform = transform2x2;
		this.updateOperation();
	}
	
	//creates a copy of this with the same center and scaleFactors adjusted by the given ratio
	protected AffineTransformation createModifiedCopy(double ratio) {
		AffineTransformation modifiedCopy = (AffineTransformation)this.clone();
		double[] partialShift = new double[]{this.shift[0]*ratio, this.shift[1]*ratio};
		double[][] scaleMat = {{ratio, 0},{0,ratio}};
		RMatrix partialTransform = this.transform.product(new RMatrix(scaleMat));
		modifiedCopy.setParameters(partialShift, partialTransform);
		return modifiedCopy;
	}
	
	@Override
	protected RMatrix getMatrix() {
		return transform;
	}
	
	protected double[] getShift() {
		return this.shift;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Affine Transformation";
	}

}

package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class AffineTransformationEdit extends AbstractLocalTransformationEdit {
	
	private double[] shift;
	private RMatrix transform;
	
	//used for cloning
	protected AffineTransformationEdit(BigBangScoreManager scoreManager) {
		super(scoreManager);
	}
	
	public AffineTransformationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] shift, RMatrix transform2x2) {
		super(scoreLayers, properties);
		//System.out.println(properties.getCenter()[0] + " " + properties.getCenter()[1]);
		this.setParameters(shift, transform2x2);
	}
	
	private void setParameters(double[] shift, RMatrix transform2x2) {
		this.shift = shift;
		this.transform = transform2x2;
		this.updateOperation();
	}
	
	//creates a copy of this with the same center and scaleFactors adjusted by the given ratio
	protected AffineTransformationEdit createModifiedCopy(double ratio) {
		AffineTransformationEdit modifiedCopy = (AffineTransformationEdit)this.clone();
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

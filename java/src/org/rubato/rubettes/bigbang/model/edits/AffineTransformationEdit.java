package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class AffineTransformationEdit extends AbstractLocalTransformationEdit {
	
	private double[] shift;
	private double angle;
	private double[] scaleFactors;
	
	public AffineTransformationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] shift, double angle, double[] scaleFactors) {
		super(scoreLayers, properties);
		//System.out.println(properties.getCenter()[0] + " " + properties.getCenter()[1]);
		this.shift = shift;
		this.angle = angle;
		this.scaleFactors = scaleFactors;
		this.initTransformation();
	}
	
	@Override
	protected RMatrix getMatrix() {
		double sin = Math.sin(this.modificationRatio*this.angle);
		double cos = Math.cos(this.modificationRatio*this.angle);
		RMatrix rotationMatrix = new RMatrix(new double[][]{{cos,-1*sin},{sin,cos}});
		double sx = this.modificationRatio*this.scaleFactors[0];
		double sy = this.modificationRatio*this.scaleFactors[1];
		RMatrix dilationMatrix = new RMatrix(new double[][]{{sx,0},{0,sy}});
		return rotationMatrix.product(dilationMatrix);
	}
	
	protected double[] getShift() {
		return this.shift;
	}
	
	public String getPresentationName() {
		return "Affine Transformation";
	}

}

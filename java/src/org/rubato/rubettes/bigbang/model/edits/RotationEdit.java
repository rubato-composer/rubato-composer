package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class RotationEdit extends AbstractLocalTransformationEdit {
	
	private double angle;
	
	public RotationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double angle) {
		super(scoreLayers, properties);
		this.angle = angle;
		this.initTransformation();
	}
	
	@Override
	public void modify(double[] newValues) {
		this.properties.setCenter(newValues);
		this.initTransformation();
	}
	
	public void modifyAngle(double angle) {
		this.angle = angle;
		this.initTransformation();
	}
	
	@Override
	protected RMatrix getMatrix() {
		double sin = Math.sin(this.angle);
		double cos = Math.cos(this.angle);
		return new RMatrix(new double[][]{{cos,-1*sin},{sin,cos}});
	}
	
	public String getPresentationName() {
		return "Rotation " + super.getPresentationName();
	}
	
	public double getAngle() {
		return this.angle;
	}

}

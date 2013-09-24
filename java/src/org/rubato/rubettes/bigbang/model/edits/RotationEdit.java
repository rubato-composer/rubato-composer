package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class RotationEdit extends AbstractLocalTransformationEdit {
	
	private double[] startingPoint;
	private double angle;
	
	public RotationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties, double[] startingPoint, double angle) {
		super(scoreLayers, properties);
		this.startingPoint = startingPoint;
		this.angle = angle;
		this.updateOperation();
	}
	
	@Override
	public void modify(double[] newValues) {
		this.properties.setCenter(newValues);
		this.updateOperation();
	}
	
	public void modifyAngle(double angle) {
		this.angle = angle;
		this.updateOperation();
	}
	
	@Override
	protected RMatrix getMatrix() {
		double sin = Math.sin(this.getAngle());
		double cos = Math.cos(this.getAngle());
		return new RMatrix(new double[][]{{cos,-1*sin},{sin,cos}});
	}
	
	public String getPresentationName() {
		return "Rotation " + super.getPresentationName();
	}
	
	public double getAngle() {
		System.out.println(this.modificationRatio + " " + this.modificationRatio*this.angle);
		return this.modificationRatio*this.angle;
	}
	
	public double[] getStartingPoint() {
		return this.startingPoint;
	}

}

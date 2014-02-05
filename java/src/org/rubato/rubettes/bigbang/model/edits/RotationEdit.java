package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class RotationEdit extends AbstractLocalTransformationEdit {
	
	private double[] startingPoint;
	private double angle;
	
	//used for cloning
	protected RotationEdit(BigBangDenotatorManager denotatorManager) {
		super(denotatorManager);
	}
	
	public RotationEdit(BigBangDenotatorManager denotatorManager, TransformationProperties properties, double[] startingPoint, double angle) {
		super(denotatorManager, properties);
		this.setParameters(startingPoint, angle);
	}
	
	public void setParameters(double[] startingPoint, double angle) {
		this.startingPoint = startingPoint;
		this.angle = angle;
		this.updateOperation();
	}
	
	public void modifyAngle(double angle) {
		this.angle = angle;
		this.updateOperation();
	}
	
	//creates a copy of this with the same center and scaleFactors adjusted by the given ratio
	protected RotationEdit createModifiedCopy(double ratio) {
		RotationEdit modifiedCopy = (RotationEdit)this.clone();
		double partialAngle = this.angle*ratio;
		modifiedCopy.setParameters(this.startingPoint, partialAngle);
		return modifiedCopy;
	}
	
	@Override
	protected RMatrix getMatrix() {
		double sin = Math.sin(this.getAngle());
		double cos = Math.cos(this.getAngle());
		return new RMatrix(new double[][]{{cos,-1*sin},{sin,cos}});
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Rotation " + super.round(this.getAngle());
	}
	
	public double getAngle() {
		return this.modificationRatio*this.angle;
	}
	
	public double[] getStartingPoint() {
		return this.startingPoint;
	}

}

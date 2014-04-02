package org.rubato.rubettes.bigbang.model.operations;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;

public abstract class AbstractLocalTransformation extends AbstractTransformation {
	
	private double[] shift1, shift2;
	
	//used for cloning
	protected AbstractLocalTransformation(BigBangModel model) {
		super(model);
	}
	
	public AbstractLocalTransformation(BigBangModel model, TransformationProperties properties) {
		super(model, properties);
	}
	
	public void updateOperation() {
		this.shift1 = new double[]{-1*this.center[0],-1*this.center[1]};
		this.shift2 = new double[]{this.center[0],this.center[1]};
		this.initTransformation(this.getMatrix(), this.getShift());
	}
	
	protected void initTransformation(RMatrix matrix, double[] shift) {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		List<RMatrix> matrices = new ArrayList<RMatrix>();
		matrices.add(identity);
		matrices.add(matrix);
		matrices.add(identity);
		List<double[]> shifts = new ArrayList<double[]>();
		shifts.add(this.shift1);
		shifts.add(shift);
		shifts.add(this.shift2);
		this.initTransformation(matrices, shifts);
	}
	
	protected abstract RMatrix getMatrix();
	
	public List<AbstractOperation> getSplitOperations(double ratio) {
		this.modify(1);
		List<AbstractOperation> shearings = new ArrayList<AbstractOperation>();
		shearings.add(this.createModifiedCopy(ratio));
		shearings.add(this.createModifiedCopy(1-ratio));
		return shearings;
	}
	
	protected abstract AbstractLocalTransformation createModifiedCopy(double ratio);
	
	public double[] getCenter() {
		return this.center;
	}
	
	public double[] getEndingPoint() {
		double x = this.center[0]+(this.endingPoint[0]-this.center[0])*this.modificationRatio;
		double y = this.center[1]+(this.endingPoint[1]-this.center[0])*this.modificationRatio;
		return new double[]{x,y};
	}
	
	protected double[] getShift() {
		return new double[2];
	}
	
}
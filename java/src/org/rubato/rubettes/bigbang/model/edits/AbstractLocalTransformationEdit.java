package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public abstract class AbstractLocalTransformationEdit extends AbstractTransformationEdit {
	
	private double[] center;
	private double[] endPoint;
	private double[] shift1, shift2;
	
	public AbstractLocalTransformationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties) {
		super(scoreLayers, properties);
	}
	
	public void updateOperation() {
		this.center = this.properties.getCenter();
		this.endPoint = this.properties.getEndPoint();
		this.shift1 = new double[]{-1*this.center[0],-1*this.center[1]};
		this.shift2 = new double[]{this.center[0],this.center[1]};
		this.initTransformation(this.getMatrix(), this.getShift());
	}
	
	/*public void undo() {
		super.undo();
		this.transform(this.getMatrix().inverse(), this.getInverse(this.getShift()));
	}*/
	
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
	
	public double[] getCenter() {
		return this.center;
	}
	
	public double[] getEndingPoint() {
		double x = this.center[0]+(this.endPoint[0]-this.center[0])*this.modificationRatio;
		double y = this.center[1]+(this.endPoint[1]-this.center[0])*this.modificationRatio;
		return new double[]{x,y};
	}
	
	protected double[] getShift() {
		return new double[2];
	}
	
	/*private double[] getInverse(double[] shift) {
		return new double[]{-1*shift[0], -1*shift[1]};
	}*/
	
}
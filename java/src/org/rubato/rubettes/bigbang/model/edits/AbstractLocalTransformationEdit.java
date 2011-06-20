package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public abstract class AbstractLocalTransformationEdit extends AbstractTransformationEdit {
	
	private double[] shift1, shift2;
	
	public AbstractLocalTransformationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties) {
		super(scoreLayers, properties);
		double[] center = properties.getCenter();
		this.shift1 = new double[]{-1*center[0],-1*center[1]};
		this.shift2 = new double[]{center[0],center[1]};
	}
	
	public void execute() {
		this.transform(this.getMatrix(), this.getShift());
	}
	
	public void undo() {
		super.undo();
		this.transform(this.getMatrix().inverse(), this.getInverse(this.getShift()));
	}
	
	protected void transform(RMatrix matrix, double[] shift) {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		List<RMatrix> matrices = new ArrayList<RMatrix>();
		matrices.add(identity);
		matrices.add(matrix);
		matrices.add(identity);
		List<double[]> shifts = new ArrayList<double[]>();
		shifts.add(this.shift1);
		shifts.add(shift);
		shifts.add(this.shift2);
		this.map(matrices, shifts);
	}
	
	protected abstract RMatrix getMatrix();
	
	protected double[] getShift() {
		return new double[2];
	}
	
	public abstract String getPresentationName();
	
	private double[] getInverse(double[] shift) {
		return new double[]{-1*shift[0], -1*shift[1]};
	}
	
}
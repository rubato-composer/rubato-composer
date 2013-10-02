package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class TranslationEdit extends AbstractTransformationEdit {
	
	private double[] startingPoint;
	private double[] shift;
	
	public TranslationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties) {
		super(scoreLayers, properties);
		this.updateOperation();
	}
	
	@Override
	protected void updateOperation() {
		this.startingPoint = this.properties.getCenter();
		double[] endingPoint = this.properties.getEndPoint();
		this.shift = new double[]{this.modificationRatio*(endingPoint[0]-this.startingPoint[0]),
				this.modificationRatio*(endingPoint[1]-this.startingPoint[1])};
		this.updateMatrix();
	}
	
	private void updateMatrix() {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		this.initTransformation(identity, this.shift);
	}
	
	public String getPresentationName() {
		return "Translation " + super.getPresentationName();
	}
	
	public double[] getStartingPoint() {
		return this.startingPoint;
	}
	
	public double[] getEndingPoint() {
		return new double[]{this.startingPoint[0]+this.shift[0],this.startingPoint[1]+this.shift[1]};
	}

}

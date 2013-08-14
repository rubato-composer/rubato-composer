package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class TranslationEdit extends AbstractTransformationEdit {
	
	private double[] startingPoint;
	private double[] endingPoint;
	private double[] shift;
	
	public TranslationEdit(BigBangScoreManager scoreLayers, TransformationProperties properties) {
		super(scoreLayers, properties);
		this.initTransformation();
	}
	
	@Override
	protected void initTransformation() {
		this.startingPoint = this.properties.getCenter();
		this.endingPoint = this.properties.getEndPoint();
		this.shift = new double[]{this.modificationRatio*(this.endingPoint[0]-this.startingPoint[0]),
				this.modificationRatio*(this.endingPoint[1]-this.startingPoint[1])};
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
		return this.endingPoint;
	}

}

package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class TranslationEdit extends AbstractTransformationEdit {
	
	private double[] modifiedShift;
	
	//used for cloning
	protected TranslationEdit(BigBangScoreManager scoreManager) {
		super(scoreManager);
	}
	
	public TranslationEdit(BigBangScoreManager scoreManager, TransformationProperties properties) {
		super(scoreManager, properties);
		this.updateOperation();
	}
	
	@Override
	protected void updateOperation() {
		this.modifiedShift = new double[]{this.modificationRatio*(this.endingPoint[0]-this.center[0]),
				this.modificationRatio*(this.endingPoint[1]-this.center[1])};
		this.updateMatrix();
	}
	
	private void updateMatrix() {
		RMatrix identity = new RMatrix(new double[][]{{1,0},{0,1}});
		this.initTransformation(identity, this.modifiedShift);
	}
	
	public List<AbstractOperationEdit> getSplitOperations(double ratio) {
		this.modify(1);
		List<AbstractOperationEdit> translations = new ArrayList<AbstractOperationEdit>();
		double[] partialShift = new double[]{this.modifiedShift[0]*ratio, this.modifiedShift[1]*ratio};
		double[] pointInBetween = new double[]{this.getStartingPoint()[0]+partialShift[0], this.getStartingPoint()[1]+partialShift[1]};
		TranslationEdit firstTranslation = (TranslationEdit)this.clone();
		firstTranslation.modify(pointInBetween);
		TranslationEdit secondTranslation = (TranslationEdit)this.clone();
		secondTranslation.modifyCenter(pointInBetween);
		secondTranslation.modify(this.endingPoint);
		translations.add(firstTranslation);
		translations.add(secondTranslation);
		return translations;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Translation " + super.round(this.modifiedShift[0]) + ", " + super.round(this.modifiedShift[1]);
	}
	
	public double[] getStartingPoint() {
		return this.center;
	}
	
	public double[] getEndingPoint() {
		return new double[]{this.getStartingPoint()[0]+this.modifiedShift[0],this.getStartingPoint()[1]+this.modifiedShift[1]};
	}

}

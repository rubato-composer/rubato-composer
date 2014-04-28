package org.rubato.rubettes.bigbang.model.operations;

import java.util.ArrayList;
import java.util.List;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.xml.XMLReader;
import org.w3c.dom.Element;

public class TranslationTransformation extends AbstractTransformation {
	
	private double[] modifiedShift;
	
	//used for cloning
	protected TranslationTransformation(BigBangModel model, TranslationTransformation other) {
		super(model, other);
		this.updateOperation();
	}
	
	public TranslationTransformation(BigBangModel model, TransformationProperties properties) {
		super(model, properties);
		this.updateOperation();
	}
	
	public TranslationTransformation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
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
	
	public List<AbstractOperation> getSplitOperations(double ratio) {
		this.modify(1);
		List<AbstractOperation> translations = new ArrayList<AbstractOperation>();
		double[] partialShift = new double[]{this.modifiedShift[0]*ratio, this.modifiedShift[1]*ratio};
		double[] pointInBetween = new double[]{this.getStartingPoint()[0]+partialShift[0], this.getStartingPoint()[1]+partialShift[1]};
		TranslationTransformation firstTranslation = (TranslationTransformation)this.clone();
		firstTranslation.modify(pointInBetween);
		TranslationTransformation secondTranslation = (TranslationTransformation)this.clone();
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

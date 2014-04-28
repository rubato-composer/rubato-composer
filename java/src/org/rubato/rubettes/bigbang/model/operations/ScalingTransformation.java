package org.rubato.rubettes.bigbang.model.operations;

import java.util.Arrays;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class ScalingTransformation extends AbstractLocalTransformation {
	
	private double[] scaleFactors;
	
	protected ScalingTransformation(BigBangModel model, ScalingTransformation other) {
		super(model, other);
		this.modify(other.scaleFactors);
	}
	
	public ScalingTransformation(BigBangModel model, TransformationProperties properties, double[] scaleFactors) {
		super(model, properties);
		this.modify(scaleFactors);
	}
	
	public ScalingTransformation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		this.fromXML(element);
	}
	
	@Override
	public void modify(double[] newScaleFactors) {
		this.scaleFactors = newScaleFactors;
		this.updateOperation();
	}
	
	//creates a copy of this with the same center and scaleFactors adjusted by the given ratio
	protected ScalingTransformation createModifiedCopy(double ratio) {
		ScalingTransformation modifiedCopy = (ScalingTransformation)this.clone();
		double[] partialScaling = new double[]{this.getModifiedScaleFactor(this.scaleFactors[0], ratio),
				this.getModifiedScaleFactor(this.scaleFactors[1], ratio)};
		modifiedCopy.modify(partialScaling);
		return modifiedCopy;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Scaling " + super.round(this.getScaleFactors()[0]) + ", " + super.round(this.getScaleFactors()[1]);
	}

	@Override
	protected RMatrix getMatrix() {
		double[] modifiedFactors = this.getScaleFactors();
		double sx = modifiedFactors[0];
		double sy = modifiedFactors[1];
		return new RMatrix(new double[][]{{sx,0},{0,sy}});
	}
	
	public double[] getScaleFactors() {
		return new double[]{this.getModifiedScaleFactor(this.scaleFactors[0], this.modificationRatio),
				this.getModifiedScaleFactor(this.scaleFactors[1], this.modificationRatio)};
	}
	
	private double getModifiedScaleFactor(double scaleFactor, double ratio) {
		return 1+(ratio*(scaleFactor-1));
	}
	
	private static final String SCALING_TAG = "Scaling";
	private static final String SCALE_FACTOR_ATTR = "scaleFactors";
	
	public void toXML(XMLWriter writer) {
		super.toXML(writer);
		writer.empty(SCALING_TAG, SCALE_FACTOR_ATTR, Arrays.toString(this.scaleFactors));
	}
	
	private void fromXML(Element element) {
		Element scalingElement = XMLReader.getChild(element, SCALING_TAG);
		this.modify(XMLReader.getDoubleArrayAttribute(scalingElement, SCALE_FACTOR_ATTR));
	}

}
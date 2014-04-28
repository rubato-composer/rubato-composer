package org.rubato.rubettes.bigbang.model.operations;

import java.util.Arrays;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class ShearingTransformation extends AbstractLocalTransformation {
	
	private double[] shearingFactors;
	
	//used for cloning
	protected ShearingTransformation(BigBangModel model, ShearingTransformation other) {
		super(model, other);
		this.modify(other.shearingFactors);
	}
	
	public ShearingTransformation(BigBangModel model, TransformationProperties properties, double[] shearingFactors) {
		super(model, properties);
		this.modify(shearingFactors);
	}
	
	public ShearingTransformation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		this.fromXML(element);
	}
	
	public void modify(double[] newShearingFactors) {
		this.shearingFactors = newShearingFactors;
		this.updateOperation();
	}
	
	//creates a copy of this with the same center and scaleFactors adjusted by the given ratio
	protected ShearingTransformation createModifiedCopy(double ratio) {
		ShearingTransformation modifiedCopy = (ShearingTransformation)this.clone();
		double[] partialShearing = new double[]{this.shearingFactors[0]*ratio, this.shearingFactors[1]*ratio};
		modifiedCopy.modify(partialShearing);
		return modifiedCopy;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Shearing " + super.round(this.getShearingFactors()[0]) + ", " + super.round(this.getShearingFactors()[1]);
	}

	@Override
	protected RMatrix getMatrix() {
		double[] modifiedFactors = this.getShearingFactors();
		double sx = modifiedFactors[0];
		double sy = modifiedFactors[1];
		return new RMatrix(new double[][]{{1,sx},{sy,1}});
	}
	
	public double[] getShearingFactors() {
		return new double[]{this.modificationRatio*this.shearingFactors[0],this.modificationRatio*this.shearingFactors[1]};
	}
	
	private static final String SHEARING_TAG = "Shearing";
	private static final String SHEARING_FACTOR_ATTR = "shearingFactors";
	
	public void toXML(XMLWriter writer) {
		super.toXML(writer);
		writer.empty(SHEARING_TAG, SHEARING_FACTOR_ATTR, Arrays.toString(this.shearingFactors));
	}
	
	private void fromXML(Element element) {
		Element shearingElement = XMLReader.getChild(element, SHEARING_TAG);
		this.modify(XMLReader.getDoubleArrayAttribute(shearingElement, SHEARING_FACTOR_ATTR));
	}
	
}

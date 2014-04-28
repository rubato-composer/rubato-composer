package org.rubato.rubettes.bigbang.model.operations;

import java.util.Arrays;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class ReflectionTransformation extends AbstractLocalTransformation {
	
	private double[] reflectionVector;
	
	public ReflectionTransformation(BigBangModel model, ReflectionTransformation other) {
		super(model, other);
		this.init(other.reflectionVector);
	}
	
	public ReflectionTransformation(BigBangModel model, TransformationProperties properties, double[] reflectionVector) {
		super(model, properties);
		this.init(reflectionVector);
	}
	
	public ReflectionTransformation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		this.fromXML(element);
	}
	
	private void init(double[] reflectionVector) {
		this.isSplittable = false;
		this.modify(reflectionVector);
	}
	
	public void modify(double[] newValues) {
		this.reflectionVector = newValues;
		this.updateOperation();
	}
	
	//does not work (yet) for this! 
	protected ReflectionTransformation createModifiedCopy(double ratio) {
		return this;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Reflection " + super.round(this.reflectionVector[0]) + ", " + super.round(this.reflectionVector[1]);
	}

	@Override
	protected RMatrix getMatrix() {
		double x = this.reflectionVector[0];
		double y = this.reflectionVector[1];
		double x2 = Math.pow(x, 2);
		double y2 = Math.pow(y, 2);
		double q = x2 + y2;
		double m11 = (this.modificationRatio*(x2 - y2)/q)+(1-this.modificationRatio);
		double m12 = this.modificationRatio*(2*x*y)/q;
		double m22 = (this.modificationRatio*(y2 - x2)/q)+(1-this.modificationRatio);
		return new RMatrix(new double[][]{{m11, m12}, {m12, m22}});
	}
	
	public double[] getReflectionVector() {
		return this.reflectionVector;
	}
	
	private static final String REFLECTION_TAG = "Reflection";
	private static final String REFLECTION_VECTOR_ATTR = "reflectionVector";
	
	public void toXML(XMLWriter writer) {
		super.toXML(writer);
		writer.empty(REFLECTION_TAG, REFLECTION_VECTOR_ATTR, Arrays.toString(this.reflectionVector));
	}
	
	private void fromXML(Element element) {
		Element reflectionElement = XMLReader.getChild(element, REFLECTION_TAG);
		this.modify(XMLReader.getDoubleArrayAttribute(reflectionElement, REFLECTION_VECTOR_ATTR));
	}

}

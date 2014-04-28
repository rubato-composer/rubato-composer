package org.rubato.rubettes.bigbang.model.operations;

import java.util.Arrays;

import org.rubato.math.matrix.RMatrix;
import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class RotationTransformation extends AbstractLocalTransformation {
	
	private double[] startingPoint;
	private double angle;
	
	//used for cloning
	protected RotationTransformation(BigBangModel model, RotationTransformation other) {
		super(model, other);
		this.setParameters(other.startingPoint, other.angle);
	}
	
	public RotationTransformation(BigBangModel model, TransformationProperties properties, double[] startingPoint, double angle) {
		super(model, properties);
		this.setParameters(startingPoint, angle);
	}
	
	public RotationTransformation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		this.fromXML(element);
	}
	
	public void setParameters(double[] startingPoint, double angle) {
		this.startingPoint = startingPoint;
		this.angle = angle;
		this.updateOperation();
	}
	
	public void modifyAngle(double angle) {
		this.angle = angle;
		this.updateOperation();
	}
	
	//creates a copy of this with the same center and scaleFactors adjusted by the given ratio
	protected RotationTransformation createModifiedCopy(double ratio) {
		RotationTransformation modifiedCopy = (RotationTransformation)this.clone();
		double partialAngle = this.angle*ratio;
		modifiedCopy.setParameters(this.startingPoint, partialAngle);
		return modifiedCopy;
	}
	
	@Override
	protected RMatrix getMatrix() {
		double sin = Math.sin(this.getAngle());
		double cos = Math.cos(this.getAngle());
		return new RMatrix(new double[][]{{cos,-1*sin},{sin,cos}});
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Rotation " + super.round(this.getAngle());
	}
	
	public double getAngle() {
		return this.modificationRatio*this.angle;
	}
	
	public double[] getStartingPoint() {
		return this.startingPoint;
	}
	
	private static final String ROTATION_TAG = "Rotation";
	private static final String STARTING_POINT_ATTR = "startingPoint";
	private static final String ANGLE_ATTR = "angle";
	
	public void toXML(XMLWriter writer) {
		super.toXML(writer);
		writer.empty(ROTATION_TAG, STARTING_POINT_ATTR, Arrays.toString(this.startingPoint),
				ANGLE_ATTR, this.angle);
	}
	
	private void fromXML(Element element) {
		Element rotationElement = XMLReader.getChild(element, ROTATION_TAG);
		this.setParameters(XMLReader.getDoubleArrayAttribute(rotationElement, STARTING_POINT_ATTR),
				XMLReader.getRealAttribute(rotationElement, ANGLE_ATTR, 0));
	}

}

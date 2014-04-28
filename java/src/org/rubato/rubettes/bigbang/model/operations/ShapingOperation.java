package org.rubato.rubettes.bigbang.model.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.bigbang.model.denotators.TransformationPaths;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class ShapingOperation extends AbstractOperation {
	
	Set<BigBangObject> objects;
	private TreeMap<Double,Double> shapingLocations;
	List<TransformationPaths> shapingPaths;
	boolean copyAndShape;
	
	//for cloning
	protected ShapingOperation(BigBangModel model, ShapingOperation other) {
		super(model);
		this.init(new TreeMap<Double,Double>(other.shapingLocations), new ArrayList<TransformationPaths>(other.shapingPaths), other.copyAndShape);
		if (model == other.model) {
			this.objects = other.objects;
		}
	}
	
	public ShapingOperation(BigBangModel model, TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		super(model);
		this.init(shapingLocations, properties.getTransformationPaths(), properties.copyAndTransform());
		this.objects = properties.getObjects();
	}
	
	public ShapingOperation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		this.fromXML(reader, element);
		this.objects = new TreeSet<BigBangObject>();
	}
	
	private void init(TreeMap<Double,Double> shapingLocations, List<TransformationPaths> shapingPaths, boolean copyAndShape) {
		this.objects = new TreeSet<BigBangObject>();
		this.shapingLocations = shapingLocations;
		this.shapingPaths = shapingPaths;
		this.copyAndShape = copyAndShape;
	}
	
	public void addShapingLocations(TreeMap<Double,Double> shapingLocations) {
		this.shapingLocations.putAll(shapingLocations);
	}
	
	public List<TransformationPaths> getShapingPaths() {
		return this.shapingPaths;
	}

	@Override
	public OperationPathResults execute() {
		return this.model.getDenotatorManager().shapeObjects(this.getObjectPaths(this.objects), this.shapingLocations, this.shapingPaths, this.copyAndShape);
	}

	@Override
	protected String getSpecificPresentationName() {
		return "Shaping";
	}

	@Override
	protected void updateOperation() {
		//do nothing for now
	}
	
	private static final String SHAPING_TAG = "Shaping";
	private static final String COPY_AND_SHAPE_ATTR = "copyAndShape";
	private static final String SHAPING_LOCATION_TAG = "ShapingLocation";
	private static final String X_POS_ATTR = "xPosition";
	private static final String Y_POS_ATTR = "yPosition";
	
	public void toXML(XMLWriter writer) {
		super.toXML(writer);
		writer.openBlock(SHAPING_TAG, COPY_AND_SHAPE_ATTR, this.copyAndShape);
		for (Double currentX : this.shapingLocations.keySet()) {
			writer.empty(SHAPING_LOCATION_TAG, X_POS_ATTR, currentX, Y_POS_ATTR, this.shapingLocations.get(currentX));
		}
		for (TransformationPaths currentTransformationPath : this.shapingPaths) {
			currentTransformationPath.toXML(writer);
		}
		writer.closeBlock();
	}
	
	private void fromXML(XMLReader reader, Element element) {
		Element shapingElement = XMLReader.getChild(element, SHAPING_TAG);
		this.copyAndShape = XMLReader.getBooleanAttribute(shapingElement, COPY_AND_SHAPE_ATTR);
		//load shaping locations
		Element locationElement = XMLReader.getChild(shapingElement, SHAPING_LOCATION_TAG);
		this.shapingLocations = new TreeMap<Double,Double>();
		while (locationElement != null) {
			double xPosition = XMLReader.getRealAttribute(locationElement, X_POS_ATTR, 0);
			double yPosition = XMLReader.getRealAttribute(locationElement, Y_POS_ATTR, 0);
			this.shapingLocations.put(xPosition, yPosition);
			locationElement = XMLReader.getNextSibling(locationElement, SHAPING_LOCATION_TAG);
		}
		//load shaping paths
		Element pathsElement = XMLReader.getChild(shapingElement, TransformationPaths.TRANSFORMATION_PATHS_TAG);
		this.shapingPaths = new ArrayList<TransformationPaths>();
		while (pathsElement != null) {
			this.shapingPaths.add(new TransformationPaths(reader, pathsElement));
			pathsElement = XMLReader.getNextSibling(pathsElement, TransformationPaths.TRANSFORMATION_PATHS_TAG);
		}
	}
	
}

package org.rubato.rubettes.bigbang.model.operations;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.bigbang.model.denotators.TransformationPaths;
import org.rubato.rubettes.bigbang.model.denotators.TransformationProperties;

public class ShapingEdit extends AbstractOperation {
	
	Set<BigBangObject> objects;
	private TreeMap<Double,Double> shapingLocations;
	List<TransformationPaths> shapingPaths;
	boolean copyAndShape;
	
	public ShapingEdit(BigBangModel model, TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		super(model);
		this.objects = properties.getObjects();
		this.shapingLocations = shapingLocations;
		this.shapingPaths = properties.getTransformationPaths();
		this.copyAndShape = properties.copyAndTransform();
		this.execute();
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
	
	public String getPresentationName() {
		return "Shaping";
	}

	@Override
	protected String getSpecificPresentationName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateOperation() {
		// TODO Auto-generated method stub
		
	}
	
}

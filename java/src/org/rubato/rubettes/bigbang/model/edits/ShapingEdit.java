package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.bigbang.model.TransformationPaths;
import org.rubato.rubettes.bigbang.model.TransformationProperties;

public class ShapingEdit extends AbstractOperationEdit {
	
	Set<BigBangObject> objects;
	private TreeMap<Double,Double> shapingLocations;
	List<TransformationPaths> shapingPaths;
	boolean copyAndShape;
	
	public ShapingEdit(BigBangDenotatorManager denotatorManager, TransformationProperties properties, TreeMap<Double,Double> shapingLocations) {
		super(denotatorManager);
		this.objects = properties.getObjects();
		this.shapingLocations = shapingLocations;
		this.shapingPaths = properties.getTransformationPaths();
		this.copyAndShape = properties.copyAndTransform();
		this.execute();
	}

	@Override
	public OperationPathResults execute() {
		return this.denotatorManager.shapeObjects(this.getObjectPaths(this.objects), this.shapingLocations, this.shapingPaths, this.copyAndShape);
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

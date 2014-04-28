package org.rubato.rubettes.bigbang.model.operations;

import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.xml.XMLReader;
import org.w3c.dom.Element;

public class BuildSatellitesOperation extends AbstractObjectBasedOperation {
	
	private BigBangObject anchorObject;
	private int powersetIndex;
	
	//used for cloning
	protected BuildSatellitesOperation(BigBangModel model, BuildSatellitesOperation other) {
		super(model, other);
		if (model == other.model) {
			this.anchorObject = other.anchorObject;
		}
		this.powersetIndex = other.powersetIndex;
	}
	
	public BuildSatellitesOperation(BigBangModel model, Set<BigBangObject> objects, BigBangObject anchorObject, int powersetIndex) {
		super(model, objects);
		this.anchorObject = anchorObject;
		this.powersetIndex = powersetIndex;
	}
	
	public BuildSatellitesOperation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		//TODO IMPLEMENT
	}
	
	@Override
	public OperationPathResults execute() {
		Set<DenotatorPath> objectPaths = this.getObjectPaths();
		DenotatorPath anchorPath = this.anchorObject.getTopDenotatorPathAt(this);
		return this.model.getDenotatorManager().buildSatelliteObjects(objectPaths, anchorPath, this.powersetIndex);
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Build Satellites";
	}

}

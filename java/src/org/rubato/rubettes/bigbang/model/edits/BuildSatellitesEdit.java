package org.rubato.rubettes.bigbang.model.edits;

import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public class BuildSatellitesEdit extends AbstractPathBasedOperationEdit {
	
	private BigBangObject anchorObject;
	private int powersetIndex;
	
	//used for cloning
	protected BuildSatellitesEdit(BigBangModel model) {
		super(model);
	}
	
	public BuildSatellitesEdit(BigBangModel model, Set<BigBangObject> objects, BigBangObject anchorObject, int powersetIndex) {
		super(model, objects);
		this.anchorObject = anchorObject;
		this.powersetIndex = powersetIndex;
	}
	
	@Override
	public OperationPathResults execute() {
		Set<DenotatorPath> objectPaths = this.getObjectPaths(this.modifiedObjects);
		DenotatorPath anchorPath = this.anchorObject.getTopDenotatorPathAt(this);
		return this.model.getDenotatorManager().buildSatelliteObjects(objectPaths, anchorPath, this.powersetIndex);
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Build Satellites";
	}
	
	public BuildSatellitesEdit clone() {
		BuildSatellitesEdit clone;
		clone = (BuildSatellitesEdit)super.clone();
		clone.anchorObject = this.anchorObject;
		clone.powersetIndex = this.powersetIndex;
		return clone;
	}

}

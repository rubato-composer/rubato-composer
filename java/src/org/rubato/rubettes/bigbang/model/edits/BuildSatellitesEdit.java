package org.rubato.rubettes.bigbang.model.edits;

import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;

public class BuildSatellitesEdit extends AbstractPathBasedOperationEdit {
	
	private BigBangObject anchorObject;
	private int powersetIndex;
	
	//used for cloning
	protected BuildSatellitesEdit(BigBangDenotatorManager denotatorManager) {
		super(denotatorManager);
	}
	
	public BuildSatellitesEdit(BigBangDenotatorManager denotatorManager, Set<BigBangObject> objects, BigBangObject anchorObject, int powersetIndex) {
		super(denotatorManager, objects);
		this.anchorObject = anchorObject;
		this.powersetIndex = powersetIndex;
	}
	
	@Override
	public OperationPathResults execute() {
		Set<DenotatorPath> objectPaths = this.getObjectPaths(this.modifiedObjects);
		DenotatorPath anchorPath = this.anchorObject.getTopDenotatorPathAt(this);
		return this.denotatorManager.buildSatelliteObjects(objectPaths, anchorPath, this.powersetIndex);
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

package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class BuildSatellitesEdit extends AbstractPathBasedOperationEdit {
	
	private DenotatorPath anchorPath;
	private int powersetIndex;
	private List<DenotatorPath> previousResultPaths;
	
	//used for cloning
	protected BuildSatellitesEdit(BigBangScoreManager scoreManager) {
		super(scoreManager);
	}
	
	public BuildSatellitesEdit(BigBangScoreManager scoreManager, List<DenotatorPath> objectPaths, DenotatorPath anchorPath, int powersetIndex) {
		super(scoreManager, objectPaths);
		this.anchorPath = anchorPath;
		this.powersetIndex = powersetIndex;
	}
	
	@Override
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath, DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		List<DenotatorPath> newResultPaths = this.scoreManager.moveObjectsToParent(this.modifiedObjectPaths, this.anchorPath, this.powersetIndex, fireCompositionChange);
		this.addMissingObjectPaths(newResultPaths);
		pathDifferences = this.getPathDifferences(this.previousResultPaths, newResultPaths);
		this.previousResultPaths = newResultPaths;
		//System.out.println(pathDifferences);
		return pathDifferences;
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "Build Satellites";
	}

	@Override
	public void setInPreviewMode(boolean inPreviewMode) {
		// TODO Auto-generated method stub
	}
	
	public BuildSatellitesEdit clone() {
		BuildSatellitesEdit clone;
		clone = (BuildSatellitesEdit)super.clone();
		clone.anchorPath = this.anchorPath;
		clone.powersetIndex = this.powersetIndex;
		return clone;
	}

}

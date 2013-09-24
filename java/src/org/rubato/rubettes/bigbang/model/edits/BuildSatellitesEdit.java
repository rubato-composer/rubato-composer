package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class BuildSatellitesEdit extends AbstractPathBasedOperationEdit {
	
	private DenotatorPath anchorPath;
	private int powersetIndex;
	
	public BuildSatellitesEdit(BigBangScoreManager scoreManager, List<DenotatorPath> objectPaths, DenotatorPath anchorPath, int powersetIndex) {
		super(scoreManager, objectPaths);
		this.anchorPath = anchorPath;
		this.powersetIndex = powersetIndex;
	}
	
	@Override
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath, DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		this.scoreManager.moveObjectsToParent(this.modifiedObjectPaths, this.anchorPath, this.powersetIndex, fireCompositionChange);
		return pathDifferences;
	}
	
	/*public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.oldSatellitePaths = this.scoreManager.undoMoveToParent(this.newSatellitePaths, this.oldSatellitePaths);
	}*/
	
	public String getPresentationName() {
		return "Build Satellites";
	}

	@Override
	public void setInPreviewMode(boolean inPreviewMode) {
		// TODO Auto-generated method stub
	}

}

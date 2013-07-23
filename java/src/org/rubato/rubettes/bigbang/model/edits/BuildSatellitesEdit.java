package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class BuildSatellitesEdit extends AbstractOperationEdit {
	
	private List<DenotatorPath> oldSatellitePaths;
	private DenotatorPath anchorPath;
	private List<DenotatorPath> newSatellitePaths;
	private int powersetIndex;
	
	public BuildSatellitesEdit(BigBangScoreManager scoreManager, TreeSet<DenotatorPath> oldSatellitePaths, DenotatorPath anchorPath, int powersetIndex) {
		super(scoreManager);
		this.oldSatellitePaths = new ArrayList<DenotatorPath>(oldSatellitePaths);
		this.anchorPath = anchorPath;
		this.powersetIndex = powersetIndex;
	}
	
	@Override
	public List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath, DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		this.newSatellitePaths = this.scoreManager.moveObjectsToParent(this.oldSatellitePaths, this.anchorPath, this.powersetIndex, fireCompositionChange);
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

package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class SatelliteBuildingEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreLayers;
	private List<DenotatorPath> oldSatellitePaths;
	private DenotatorPath anchorPath;
	private List<DenotatorPath> newSatellitePaths;
	
	public SatelliteBuildingEdit(BigBangScoreManager scoreLayers, TreeSet<DenotatorPath> oldSatellitePaths, DenotatorPath anchorPath) {
		this.scoreLayers = scoreLayers;
		this.oldSatellitePaths = new ArrayList<DenotatorPath>(oldSatellitePaths);
		this.anchorPath = anchorPath;
		this.execute();
	}
	
	public void execute() {
		this.newSatellitePaths = this.scoreLayers.moveNotesToParent(this.oldSatellitePaths, this.anchorPath, false);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.oldSatellitePaths = this.scoreLayers.undoMoveToParent(this.newSatellitePaths, this.oldSatellitePaths);
	}
	
	public String getPresentationName() {
		return "Build Satellites";
	}

}

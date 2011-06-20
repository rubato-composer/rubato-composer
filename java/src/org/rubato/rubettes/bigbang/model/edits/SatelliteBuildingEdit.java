package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.NotePath;

public class SatelliteBuildingEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreLayers;
	private List<NotePath> oldSatellitePaths;
	private NotePath anchorPath;
	private List<NotePath> newSatellitePaths;
	
	public SatelliteBuildingEdit(BigBangScoreManager scoreLayers, TreeSet<NotePath> oldSatellitePaths, NotePath anchorPath) {
		this.scoreLayers = scoreLayers;
		this.oldSatellitePaths = new ArrayList<NotePath>(oldSatellitePaths);
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

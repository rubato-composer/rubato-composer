package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class ModulatorBuildingEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreLayers;
	private List<DenotatorPath> oldModulatorPaths;
	private DenotatorPath carrierPath;
	private List<DenotatorPath> newModulatorPaths;
	
	public ModulatorBuildingEdit(BigBangScoreManager scoreLayers, TreeSet<DenotatorPath> oldModulators, DenotatorPath carrierPath) {
		this.scoreLayers = scoreLayers;
		this.oldModulatorPaths = new ArrayList<DenotatorPath>(oldModulators);
		this.carrierPath = carrierPath;
		this.execute();
	}
	
	public void execute() {
		this.newModulatorPaths = this.scoreLayers.moveObjectsToParent(this.oldModulatorPaths, this.carrierPath, 1);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.oldModulatorPaths = this.scoreLayers.undoMoveToParent(this.newModulatorPaths, this.oldModulatorPaths);
	}
	
	public String getPresentationName() {
		return "Build Modulators";
	}

}

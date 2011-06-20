package org.rubato.rubettes.bigbang.model.edits;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.NotePath;

public class ModulatorBuildingEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreLayers;
	private List<NotePath> oldModulatorPaths;
	private NotePath carrierPath;
	private List<NotePath> newModulatorPaths;
	
	public ModulatorBuildingEdit(BigBangScoreManager scoreLayers, TreeSet<NotePath> oldModulators, NotePath carrierPath) {
		this.scoreLayers = scoreLayers;
		this.oldModulatorPaths = new ArrayList<NotePath>(oldModulators);
		this.carrierPath = carrierPath;
		this.execute();
	}
	
	public void execute() {
		this.newModulatorPaths = this.scoreLayers.moveNotesToParent(this.oldModulatorPaths, this.carrierPath, true);
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

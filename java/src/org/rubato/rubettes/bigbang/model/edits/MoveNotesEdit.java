package org.rubato.rubettes.bigbang.model.edits;

import java.util.Map;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.NotePath;

public class MoveNotesEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager score;
	private Set<NotePath> notePaths;
	private Map<NotePath,Integer> newPathsAndOldLayers;
	private int layerIndex;
	
	public MoveNotesEdit(BigBangScoreManager scoreLayers, Set<NotePath> notePaths, int layerIndex) {
		this.score = scoreLayers;
		this.notePaths = notePaths;
		this.layerIndex = layerIndex;
		this.execute();
	}
	
	public void execute() {
		this.newPathsAndOldLayers = this.score.moveNotesToLayer(this.notePaths, this.layerIndex);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.notePaths = this.score.moveNotesToLayers(this.newPathsAndOldLayers);
	}
	
	public String getPresentationName() {
		return "Move Notes";
	}

}

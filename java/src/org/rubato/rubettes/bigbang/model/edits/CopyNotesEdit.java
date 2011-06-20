package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.NotePath;

public class CopyNotesEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager score;
	private Set<NotePath> notePaths;
	private List<NotePath> copyPaths;
	private int layerIndex;
	
	public CopyNotesEdit(BigBangScoreManager scoreLayers, Set<NotePath> notePaths, int layerIndex) {
		this.score = scoreLayers;
		this.notePaths = notePaths;
		this.layerIndex = layerIndex;
		this.execute();
	}
	
	public void execute() {
		this.copyPaths = this.score.copyNotesToLayer(this.notePaths, this.layerIndex);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.score.removeNotes(new TreeSet<NotePath>(this.copyPaths));
	}
	
	public String getPresentationName() {
		return "Copy Notes";
	}

}

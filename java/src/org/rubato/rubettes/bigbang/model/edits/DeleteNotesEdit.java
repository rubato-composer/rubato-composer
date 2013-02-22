package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class DeleteNotesEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager score;
	private List<DenotatorPath> notePaths;
	private List<LimitDenotator> notes;
	
	public DeleteNotesEdit(BigBangScoreManager scoreLayers, List<DenotatorPath> notePaths) {
		this.score = scoreLayers;
		this.notePaths = notePaths;
		this.execute();
	}
	
	public void execute() {
		this.notes = this.score.removeNotes(new TreeSet<DenotatorPath>(this.notePaths));
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		List<DenotatorPath> oldAnchorPaths = DenotatorPath.getParentPaths(this.notePaths);
		this.notePaths = this.score.addNotes(this.notes, oldAnchorPaths);
	}
	
	public String getPresentationName() {
		return "Delete Notes";
	}

}

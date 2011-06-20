package org.rubato.rubettes.bigbang.model.edits;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.NotePath;

public class AddNoteEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreManager;
	private double[] noteValues;
	private NotePath notePath;
	
	public AddNoteEdit(BigBangScoreManager scoreManager, double[] noteValues) {
		this.scoreManager = scoreManager;
		this.noteValues = noteValues;
		this.execute();
	}
	
	public void execute() {
		this.notePath = this.scoreManager.addNote(this.noteValues);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.scoreManager.removeNote(this.notePath);
	}
	
	public String getPresentationName() {
		return "Add Note";
	}

}

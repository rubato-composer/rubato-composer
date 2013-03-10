package org.rubato.rubettes.bigbang.model.edits;

import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class AddObjectEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreManager;
	private Map<DenotatorPath,Double> pathsWithValues;
	private DenotatorPath objectPath;
	
	public AddObjectEdit(BigBangScoreManager scoreManager, Map<DenotatorPath,Double> pathsWithValues) {
		this.scoreManager = scoreManager;
		this.pathsWithValues = pathsWithValues;
		this.execute();
	}
	
	public void execute() {
		this.objectPath = this.scoreManager.addObject(this.pathsWithValues);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.scoreManager.removeObject(this.objectPath);
	}
	
	public String getPresentationName() {
		return "Add Object";
	}

}

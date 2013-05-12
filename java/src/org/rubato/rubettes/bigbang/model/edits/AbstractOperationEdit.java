package org.rubato.rubettes.bigbang.model.edits;

import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractOperationEdit extends AbstractUndoableEdit {
	
	protected BigBangScoreManager scoreManager;
	
	public AbstractOperationEdit(BigBangScoreManager scoreManager) {
		this.scoreManager = scoreManager;
	}
	
	public BigBangScoreManager getScoreManager() {
		return this.scoreManager;
	}
	
	public abstract Map<DenotatorPath,DenotatorPath> execute(Map<DenotatorPath,DenotatorPath> pathDifferences, boolean sendCompositionChange);
	
	public abstract void setInPreviewMode(boolean inPreviewMode);
	
	public String toString() {
		return this.getPresentationName();
	}

}

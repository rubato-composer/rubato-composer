package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public abstract class AbstractOperationEdit extends AbstractUndoableEdit {
	
	protected BigBangScoreManager scoreManager;
	protected double modificationRatio;
	protected Double minModRatio, maxModRatio;
	
	public AbstractOperationEdit(BigBangScoreManager scoreManager) {
		this.scoreManager = scoreManager;
		this.modificationRatio = 1;
	}
	
	public BigBangScoreManager getScoreManager() {
		return this.scoreManager;
	}
	
	protected abstract void updateOperation();
	
	public void modify(double ratio) {
		if (this.minModRatio != null && ratio <= this.minModRatio) {
			ratio = this.minModRatio;
		}
		if (this.maxModRatio != null && ratio >= this.maxModRatio) {
			ratio = this.maxModRatio;
		}
		this.modificationRatio = ratio;
		this.updateOperation();
	}
	
	public abstract List<Map<DenotatorPath,DenotatorPath>> execute(List<Map<DenotatorPath,DenotatorPath>> pathDifferences, boolean sendCompositionChange);
	
	public abstract void setInPreviewMode(boolean inPreviewMode);
	
	public String toString() {
		return this.getPresentationName();
	}

}

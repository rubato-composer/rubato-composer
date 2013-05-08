package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangTransformationGraph extends ArrayList<AbstractTransformationEdit> {
	
	public BigBangTransformationGraph() {
	}
	
	@Override
	public boolean add(AbstractTransformationEdit edit) {
		return this.add(edit, false);
	}
	
	public void previewTransformationAtEnd(AbstractTransformationEdit edit) {
		this.add(edit, true);
		this.removeLastWithoutUpdate();
	}
	
	private boolean add(AbstractTransformationEdit edit, boolean inPreviewMode) {
		boolean added = super.add(edit);
		if (added) {
			this.updateScore(inPreviewMode);
		}
		return added;
	}
	
	public void updateScore(boolean inPreviewMode) {
		if (this.size()>0) {
			Map<DenotatorPath,DenotatorPath> pathDifferences = new TreeMap<DenotatorPath,DenotatorPath>();
			this.get(this.size()-1).setInPreviewMode(inPreviewMode);
			this.get(0).getScoreManager().resetFactualScore();
			for (int i = 0; i < this.size(); i++) {
				AbstractTransformationEdit edit = this.get(i);
				//only preview with last one!!!!!!
				pathDifferences = edit.map(pathDifferences, i==this.size()-1);
			}
		}
	}
	
	public AbstractTransformationEdit removeLast() {
		AbstractTransformationEdit removed = this.removeLastWithoutUpdate();
		this.updateScore(false);
		return removed;
	}
	
	private AbstractTransformationEdit removeLastWithoutUpdate() {
		return super.remove(this.size()-1);
	}

}

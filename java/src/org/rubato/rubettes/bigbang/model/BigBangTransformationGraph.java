package org.rubato.rubettes.bigbang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rubato.rubettes.bigbang.model.edits.AbstractOperationEdit;
import org.rubato.rubettes.bigbang.model.edits.AbstractTransformationEdit;
import org.rubato.rubettes.util.DenotatorPath;

public class BigBangTransformationGraph extends ArrayList<AbstractOperationEdit> {
	
	public BigBangTransformationGraph() {
	}
	
	@Override
	public boolean add(AbstractOperationEdit edit) {
		return this.add(edit, false);
	}
	
	public void previewTransformationAtEnd(AbstractTransformationEdit edit) {
		this.add(edit, true);
		this.removeLastWithoutUpdate();
	}
	
	private boolean add(AbstractOperationEdit edit, boolean inPreviewMode) {
		boolean added = super.add(edit);
		if (added) {
			this.updateScore(inPreviewMode);
		}
		return added;
	}
	
	public void updateScore(boolean inPreviewMode) {
		if (this.size()>0) {
			List<Map<DenotatorPath,DenotatorPath>> pathDifferences = new ArrayList<Map<DenotatorPath,DenotatorPath>>();
			this.get(this.size()-1).setInPreviewMode(inPreviewMode);
			this.get(0).getScoreManager().resetScore();
			for (int i = 0; i < this.size(); i++) {
				AbstractOperationEdit edit = this.get(i);
				//only send composition change with last one!!!!!!
				pathDifferences = edit.execute(pathDifferences, i==this.size()-1);
			}
		}
	}
	
	public AbstractOperationEdit removeLast() {
		AbstractOperationEdit removed = this.removeLastWithoutUpdate();
		this.updateScore(false);
		return removed;
	}
	
	private AbstractOperationEdit removeLastWithoutUpdate() {
		return super.remove(this.size()-1);
	}

}

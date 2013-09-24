package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class FlattenEdit extends AbstractPathBasedOperationEdit {
	
	public FlattenEdit(BigBangScoreManager scoreManager, List<DenotatorPath> objectPaths) {
		super(scoreManager, objectPaths);
	}
	
	@Override
	public List<Map<DenotatorPath, DenotatorPath>> execute(List<Map<DenotatorPath, DenotatorPath>> pathDifferences,	boolean sendCompositionChange) {
		this.scoreManager.flattenNotes(new TreeSet<DenotatorPath>(this.modifiedObjectPaths));
		return pathDifferences;
	}
	
	/*public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.oldNodePaths = this.score.unflattenNotes(this.newAndOldPaths);
	}*/

	@Override
	public void setInPreviewMode(boolean inPreviewMode) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getPresentationName() {
		return "Flatten";
	}

}

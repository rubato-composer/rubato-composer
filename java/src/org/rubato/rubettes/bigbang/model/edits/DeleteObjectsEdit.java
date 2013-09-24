package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class DeleteObjectsEdit extends AbstractPathBasedOperationEdit {
	
	public DeleteObjectsEdit(BigBangScoreManager scoreManager, List<DenotatorPath> objectPaths) {
		super(scoreManager, objectPaths);
	}
	
	@Override
	public List<Map<DenotatorPath, DenotatorPath>> execute(List<Map<DenotatorPath, DenotatorPath>> pathDifferences, boolean fireCompositionChange) {
		this.scoreManager.removeObjects(new TreeSet<DenotatorPath>(this.modifiedObjectPaths), fireCompositionChange);
		return pathDifferences;
	}
	
	/*public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		List<DenotatorPath> oldAnchorPaths = DenotatorPath.getAnchorPaths(this.objectPaths);
		this.objectPaths = this.scoreManager.addObjects(this.objects, oldAnchorPaths, false);
	}*/
	
	public String getPresentationName() {
		return "Delete Objects";
	}

	@Override
	public void setInPreviewMode(boolean inPreviewMode) {
		// TODO Auto-generated method stub
		
	}

}

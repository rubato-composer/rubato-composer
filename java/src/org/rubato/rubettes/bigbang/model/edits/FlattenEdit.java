package org.rubato.rubettes.bigbang.model.edits;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class FlattenEdit extends AbstractOperationEdit {
	
	private Set<DenotatorPath> oldNodePaths;
	private TreeMap<DenotatorPath,DenotatorPath> newAndOldPaths;
	
	public FlattenEdit(BigBangScoreManager scoreManager, TreeSet<DenotatorPath> oldNodePaths) {
		super(scoreManager);
		this.oldNodePaths = oldNodePaths;
	}
	
	@Override
	public Map<DenotatorPath, DenotatorPath> execute(Map<DenotatorPath, DenotatorPath> pathDifferences,	boolean sendCompositionChange) {
		this.newAndOldPaths = this.scoreManager.flattenNotes(this.oldNodePaths);
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
	
	public String getPresentationName() {
		return "Flatten";
	}

	@Override
	public void setInPreviewMode(boolean inPreviewMode) {
		// TODO Auto-generated method stub
		
	}

}

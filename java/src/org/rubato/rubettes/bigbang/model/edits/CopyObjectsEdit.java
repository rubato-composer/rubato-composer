package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class CopyObjectsEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager score;
	private Set<DenotatorPath> objectPaths;
	private List<DenotatorPath> copyPaths;
	
	public CopyObjectsEdit(BigBangScoreManager scoreLayers, Set<DenotatorPath> objectPaths) {
		this.score = scoreLayers;
		this.objectPaths = objectPaths;
		this.execute();
	}
	
	public void execute() {
		this.copyPaths = this.score.copyObjects(this.objectPaths);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.score.removeObjects(new TreeSet<DenotatorPath>(this.copyPaths));
	}
	
	public String getPresentationName() {
		return "Copy Objects";
	}

}

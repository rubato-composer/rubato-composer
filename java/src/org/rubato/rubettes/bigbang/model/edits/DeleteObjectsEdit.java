package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.math.yoneda.Denotator;
import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

public class DeleteObjectsEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager score;
	private List<DenotatorPath> objectPaths;
	private List<Denotator> objects;
	
	public DeleteObjectsEdit(BigBangScoreManager scoreLayers, List<DenotatorPath> objectPaths) {
		this.score = scoreLayers;
		this.objectPaths = objectPaths;
		this.execute();
	}
	
	public void execute() {
		this.objects = this.score.removeObjects(new TreeSet<DenotatorPath>(this.objectPaths));
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		List<DenotatorPath> oldAnchorPaths = DenotatorPath.getAnchorPaths(this.objectPaths);
		this.objectPaths = this.score.addObjects(this.objects, oldAnchorPaths, false);
	}
	
	public String getPresentationName() {
		return "Delete Objects";
	}

}

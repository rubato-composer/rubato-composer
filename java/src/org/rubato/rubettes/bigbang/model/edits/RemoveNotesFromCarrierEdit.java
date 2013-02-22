package org.rubato.rubettes.bigbang.model.edits;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.util.DenotatorPath;

/*
 * same as flatten edit. unite!!
 */
public class RemoveNotesFromCarrierEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager score;
	private Set<DenotatorPath> oldNodePaths;
	private TreeMap<DenotatorPath,DenotatorPath> newAndOldPaths;
	
	public RemoveNotesFromCarrierEdit(BigBangScoreManager score, TreeSet<DenotatorPath> oldNodePaths) {
		this.score = score;
		this.oldNodePaths = oldNodePaths;
		this.execute();
	}
	
	public void execute() {
		this.newAndOldPaths = this.score.flattenNotes(this.oldNodePaths);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.oldNodePaths = this.score.unflattenNotes(this.newAndOldPaths);
	}
	
	public String getPresentationName() {
		return "Disconnect Modulators";
	}

}

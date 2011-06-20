package org.rubato.rubettes.bigbang.model.edits;

import javax.swing.undo.AbstractUndoableEdit;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;

public class AddWallpaperDimensionEdit extends AbstractUndoableEdit {
	
	private BigBangScoreManager scoreManager;
	private int rangeFrom, rangeTo;
	
	public AddWallpaperDimensionEdit(BigBangScoreManager manager, int rangeFrom, int rangeTo) {
		this.scoreManager = manager;
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
		this.execute();
	}
	
	public void execute() {
		this.scoreManager.addWallpaperDimensionS(this.rangeFrom, this.rangeTo);
	}
	
	public void redo() {
		super.redo();
		this.execute();
	}
	
	public void undo() {
		super.undo();
		this.scoreManager.removeLastWallpaperDimension();
	}
	
	public String getPresentationName() {
		return "Add Wallpaper Dimension";
	}

}

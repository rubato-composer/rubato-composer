package org.rubato.rubettes.bigbang.model.edits;

import java.util.List;
import java.util.Map;

import org.rubato.rubettes.bigbang.model.BigBangScoreManager;
import org.rubato.rubettes.bigbang.view.model.SelectedObjectsPaths;
import org.rubato.rubettes.util.DenotatorPath;

public class AddWallpaperDimensionEdit extends AbstractOperationEdit {
	
	private BigBangScoreManager scoreManager;
	private SelectedObjectsPaths objectPaths;
	private int rangeFrom, rangeTo;
	
	public AddWallpaperDimensionEdit(BigBangScoreManager manager, SelectedObjectsPaths objectPaths, int rangeFrom, int rangeTo) {
		super(manager);
		this.scoreManager = manager;
		this.objectPaths = objectPaths;
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
	}
	
	public String getPresentationName() {
		return "Add Wallpaper Dimension (" + this.rangeFrom + "," + this.rangeTo + ")";
	}

	@Override
	public List<Map<DenotatorPath, DenotatorPath>> execute(List<Map<DenotatorPath, DenotatorPath>> pathDifferences, boolean sendCompositionChange) {
		//TODO could it be possible to have different paths for each dimension??? or even transformation??
		this.scoreManager.addWallpaperDimensionS(this.objectPaths, this.rangeFrom, this.rangeTo, sendCompositionChange);
		return pathDifferences;
	}

	@Override
	public void setInPreviewMode(boolean inPreviewMode) {
		//do nothing
	}
	
	public void setRange(boolean rangeTo, int value) {
		if (rangeTo) {
			this.rangeTo = value;
		} else {
			this.rangeFrom = value;
		}
	}
	
	public int getRangeFrom() {
		return this.rangeFrom;
	}
	
	public int getRangeTo() {
		return this.rangeTo;
	}

}

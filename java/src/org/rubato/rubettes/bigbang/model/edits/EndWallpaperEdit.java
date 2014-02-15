package org.rubato.rubettes.bigbang.model.edits;

import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class EndWallpaperEdit extends AbstractOperationEdit {
	
	public EndWallpaperEdit(BigBangDenotatorManager denotatorManager) {
		super(denotatorManager);
		this.isAnimatable = false;
		this.isSplittable = false;
		this.duration = 0;
	}
	
	//not changed by modification!!
	protected void updateOperation() { }

	@Override
	public OperationPathResults execute() {
		this.denotatorManager.endWallpaper();
		return new OperationPathResults();
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "End Wallpaper";
	}

}

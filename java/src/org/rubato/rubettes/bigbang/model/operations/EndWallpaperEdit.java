package org.rubato.rubettes.bigbang.model.operations;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class EndWallpaperEdit extends AbstractOperation {
	
	public EndWallpaperEdit(BigBangModel model) {
		super(model);
		this.isAnimatable = false;
		this.isSplittable = false;
		this.duration = 0;
	}
	
	//not changed by modification!!
	protected void updateOperation() { }

	@Override
	public OperationPathResults execute() {
		this.model.getDenotatorManager().endWallpaper();
		return new OperationPathResults();
	}
	
	@Override
	protected String getSpecificPresentationName() {
		return "End Wallpaper";
	}

}

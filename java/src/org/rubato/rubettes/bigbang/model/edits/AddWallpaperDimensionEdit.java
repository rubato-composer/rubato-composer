package org.rubato.rubettes.bigbang.model.edits;

import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangDenotatorManager;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class AddWallpaperDimensionEdit extends AbstractOperationEdit {
	
	private Set<BigBangObject> objects;
	private int rangeFrom, rangeTo;
	
	public AddWallpaperDimensionEdit(BigBangDenotatorManager denotatorManager, Set<BigBangObject> objects, int rangeFrom, int rangeTo) {
		super(denotatorManager);
		this.objects = objects;
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
		this.duration = 0;
	}
	
	//not changed by modification!!
	protected void updateOperation() { }
	
	@Override
	protected String getSpecificPresentationName() {
		return "Add Wallpaper Dimension (" + this.rangeFrom + "," + this.rangeTo + ")";
	}

	@Override
	public OperationPathResults execute() {
		//TODO could it be possible to have different paths for each dimension??? or even transformation??
		this.denotatorManager.addWallpaperDimension(this.getObjectPaths(this.objects), this.rangeFrom, this.rangeTo);
		return new OperationPathResults();
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

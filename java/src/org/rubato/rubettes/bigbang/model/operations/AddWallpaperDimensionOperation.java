package org.rubato.rubettes.bigbang.model.operations;

import java.util.Set;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;

public class AddWallpaperDimensionOperation extends AbstractOperation {
	
	private Set<BigBangObject> objects;
	private int rangeFrom, rangeTo;
	
	public AddWallpaperDimensionOperation(BigBangModel model, Set<BigBangObject> objects, int rangeFrom, int rangeTo) {
		super(model);
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
		this.model.getDenotatorManager().addWallpaperDimension(this.getObjectPaths(this.objects), this.rangeFrom, this.rangeTo);
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

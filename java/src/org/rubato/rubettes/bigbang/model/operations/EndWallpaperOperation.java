package org.rubato.rubettes.bigbang.model.operations;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.xml.XMLReader;
import org.w3c.dom.Element;

public class EndWallpaperOperation extends AbstractOperation {
	
	//used for cloning
	protected EndWallpaperOperation(BigBangModel model, @SuppressWarnings("unused") EndWallpaperOperation other) {
		this(model);
	}
	
	public EndWallpaperOperation(BigBangModel model) {
		super(model);
		this.isAnimatable = false;
		this.isSplittable = false;
		this.duration = 0;
	}
	
	public EndWallpaperOperation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
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

package org.rubato.rubettes.bigbang.model.operations;

import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public class AddWallpaperDimensionOperation extends AbstractOperation {
	
	private Set<BigBangObject> objects;
	private int rangeFrom, rangeTo;
	
	//used for cloning
	protected AddWallpaperDimensionOperation(BigBangModel model, AddWallpaperDimensionOperation other) {
		this(model, other.rangeFrom, other.rangeTo);
		if (model == other.model) {
			this.objects = new TreeSet<BigBangObject>(other.objects);
		} else {
			this.objects = new TreeSet<BigBangObject>();
		}
	}
	
	public AddWallpaperDimensionOperation(BigBangModel model, Set<BigBangObject> objects, int rangeFrom, int rangeTo) {
		this(model, rangeFrom, rangeTo);
		this.objects = objects;
	}
	
	private AddWallpaperDimensionOperation(BigBangModel model, int rangeFrom, int rangeTo) {
		super(model);
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
		this.duration = 0;
	}
	
	public AddWallpaperDimensionOperation(BigBangModel model, XMLReader reader, Element element) {
		super(model, reader, element);
		this.objects = new TreeSet<BigBangObject>();
		this.fromXML(element);
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
	
	private static final String WALLPAPER_DIMENSION_TAG = "AddWallpaperDimension";
	private static final String RANGE_FROM_ATTR = "rangeFrom";
	private static final String RANGE_TO_ATTR = "rangeTo";
	
	public void toXML(XMLWriter writer) {
		super.toXML(writer);
		writer.empty(WALLPAPER_DIMENSION_TAG, RANGE_FROM_ATTR, this.rangeFrom, RANGE_TO_ATTR, this.rangeTo);
	}
	
	private void fromXML(Element element) {
		Element wallpaperElement = XMLReader.getChild(element, WALLPAPER_DIMENSION_TAG);
		this.rangeFrom = XMLReader.getIntAttribute(wallpaperElement, RANGE_FROM_ATTR, 0);
		this.rangeTo = XMLReader.getIntAttribute(wallpaperElement, RANGE_TO_ATTR, 5);
	}

}

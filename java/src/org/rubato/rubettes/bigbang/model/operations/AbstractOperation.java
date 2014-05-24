package org.rubato.rubettes.bigbang.model.operations;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.rubato.rubettes.bigbang.model.BigBangModel;
import org.rubato.rubettes.bigbang.model.BigBangObject;
import org.rubato.rubettes.bigbang.model.OperationPathResults;
import org.rubato.rubettes.util.DenotatorPath;
import org.rubato.xml.XMLReader;
import org.rubato.xml.XMLWriter;
import org.w3c.dom.Element;

public abstract class AbstractOperation {
	
	protected BigBangModel model;
	protected double modificationRatio;
	protected Double minModRatio, maxModRatio;
	protected boolean isAnimatable;
	protected boolean isSplittable;
	//duration in seconds
	protected double duration;
	
	public AbstractOperation(BigBangModel model) {
		this.model = model;
		this.modificationRatio = 1;
		this.isAnimatable = false;
		this.isSplittable = false;
		this.duration = 1;
	}
	
	public AbstractOperation(BigBangModel model, XMLReader reader, Element element) {
		this.model = model;
		this.fromXML(element);
	}
	
	protected abstract void updateOperation();
	
	public void modify(double ratio) {
		if (this.minModRatio != null && ratio <= this.minModRatio) {
			ratio = this.minModRatio;
		}
		if (this.maxModRatio != null && ratio >= this.maxModRatio) {
			ratio = this.maxModRatio;
		}
		this.modificationRatio = ratio;
		this.updateOperation();
	}
	
	protected Set<DenotatorPath> getObjectPaths(Set<BigBangObject> objects) {
		Set<DenotatorPath> objectPaths = new TreeSet<DenotatorPath>();
		if (objects.size() == 0) {
			//return all objects if none here!! operation will be applied to all!!
			Set<BigBangObject> allObjects = this.model.getObjects().getObjectsAt(this);
			if (allObjects != null) {
				objects = allObjects;
			}
		}
		for (BigBangObject currentObject : objects) {
			DenotatorPath currentPath = currentObject.getTopDenotatorPathAt(this);
			if (currentPath != null) {
				objectPaths.add(currentPath);
			}
		}
		return objectPaths;
	}
	
	public String getPresentationName() {
		return  this.getSpecificPresentationName() + (this.isAnimatable ? " (" + Double.toString(this.duration) + ")" : "");
	}
	
	protected abstract String getSpecificPresentationName();
	
	public abstract OperationPathResults execute();
	
	public String toString() {
		return this.getPresentationName();
	}
	
	public boolean isAnimatable() {
		return this.isAnimatable;
	}
	
	public boolean isSplittable() {
		return this.isSplittable;
	}
	
	/**
	 * @param ratio a number between 0 and 1
	 * @return a list with two operations that represent this operation split at the given ratio.
	 * null if not splittable
	 */
	public List<AbstractOperation> getSplitOperations(double ratio) {
		return null;
	}
	
	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	public double getDuration() {
		return this.duration;
	}
	
	@Override
	public AbstractOperation clone() {
		return this.clone(this.model);
	}
	
	public AbstractOperation clone(BigBangModel model) {
		try {
			return this.getClass().getDeclaredConstructor(BigBangModel.class, this.getClass())
					.newInstance(model, this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final String PROPERTIES_TAG = "Properties";
	private static final String MOD_RATIO_ATTR = "modificationRatio";
	private static final String DURATION_ATTR = "duration";
	
	public void toXML(XMLWriter writer) {
		writer.empty(PROPERTIES_TAG, MOD_RATIO_ATTR, this.modificationRatio, DURATION_ATTR, this.duration);
	}
	
	private void fromXML(Element element) {
		Element propertiesElement = XMLReader.getChild(element, PROPERTIES_TAG);
		this.modificationRatio = XMLReader.getRealAttribute(propertiesElement, MOD_RATIO_ATTR, 1);
		this.duration = XMLReader.getRealAttribute(propertiesElement, DURATION_ATTR, 1);
	}
	
}